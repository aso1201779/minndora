package com.example.test24;

import java.io.InputStream;
import java.net.URL;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class W_photo extends Activity implements OnTouchListener{

	String username;
	String userID;
	String photoURL;
	String mapID;
	String spotID;
	String GETmenberID;
	String GETusername;
	String seibetu;
	String title;
	String mapURL;
	int flg;
	ImageView test;

	// 移動とズームに利用する
    // res/layout/main.xml にて android:scaleType="matrix" 指定
    private Matrix matrix      = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start       = new PointF();
    private float oldDist      = 0f;
    private PointF mid         = new PointF();
    private float curRatio     = 1f;

    // 以下の状態を取り得る
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private float minScale = 1F;
    private float maxScale = 8F;


    private Rect mRect;
    private Matrix mMatrix = new Matrix();
    private float[] mValues = new float[9];



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.w_photo);


		Intent intent = getIntent();
		flg = Integer.parseInt(intent.getStringExtra("flg"));
		if(flg == 1){
			username = intent.getStringExtra("username");
			userID = intent.getStringExtra("userID");
		}
		photoURL= intent.getStringExtra("photoURL");


		//StrictModeを設定 penaltyDeathを取り除く
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

        test = (ImageView)findViewById(R.id.w_photo_big);
        test.setOnTouchListener(this);

         //画像のURL
         String urlString="http://54.68.202.192/img/" + photoURL;

         try {
             //URLクラス
            URL url = new URL(urlString);
             //入力ストリームを開く
            InputStream istream = url.openStream();

             //画像をDrawableで取得
            Drawable d = Drawable.createFromStream(istream, "webimg");

             //入力ストリームを閉じる
            istream.close();


            Bitmap bm = ((BitmapDrawable) d).getBitmap();

            test.setImageBitmap(bm);

         } catch (Exception e) {
             System.out.println("nuu: "+e);
         }




	}


	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();


	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO 自動生成されたメソッド・スタブ
        ImageView view = (ImageView)v;

        // イベントのダンプ
        dumpEvent(event);

        /***********
         * ドラッグ
         ***********/
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:

            savedMatrix.set(matrix);
            start.set(event.getX(), event.getY());
            Log.d("MyApp", "mode=DRAG");
            mode = DRAG;
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            Log.d("MyApp", "mode=NONE");
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
            	imageMove(view,event);
//                matrix.set(savedMatrix);
//                matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
            }
            break;
        }

        /***********
         * ズーム
         ***********/
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(event);
            Log.d("MyApp", "oldDist=" + oldDist);
            // Android のポジション誤検知を無視
            if (oldDist > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
                Log.d("MyApp", "mode=ZOOM");
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == ZOOM) {
                float newDist = spacing(event);
                float scale = newDist / oldDist;
                Log.d("MyApp", "scale=" + scale);
                float tmpRatio = curRatio * scale;
                if (minScale < tmpRatio && tmpRatio < maxScale) {
                    curRatio = tmpRatio;
                    matrix.postScale(scale, scale, mid.x, mid.y);
                }
            }
            break;
        }

        // 変換の実行
        view.setImageMatrix(matrix);

        return true; // イベントがハンドリングされたことを示す
    }
    /**
     * 2点間の距離を計算
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    /**
     * 2点間の中間点を計算
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    @SuppressWarnings("deprecation")
	private void dumpEvent(MotionEvent event) {
        String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
                             "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        // event.getAction() の 下位8bitはアクションコード、次の8bitはポインターID
        // ビット演算 の & と ビットシフト>> で分離する。
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_" ).append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
         || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid " ).append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")" );
        }
        sb.append("[" );
        //　event.getPointerCount() 何カ所ポイントされているか、
        // event.getX(),event.getY() で座標が取得できる
        // getPointerId() で、どのポインターIDについての情報かを判定出来る
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#" ).append(i);
            sb.append("(pid " ).append(event.getPointerId(i));
            sb.append(")=" ).append((int) event.getX(i));
            sb.append("," ).append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";" );
        }
        sb.append("]" );
        Log.d("MyApp", sb.toString());
    }

    private void imageMove(ImageView iv, MotionEvent e) {
    	 if (e.getHistorySize() > 0) {
    	     int x = (int) e.getHistoricalX(0) - (int) e.getX();
    	     int y = (int) e.getHistoricalY(0) - (int) e.getY();
    	     iv.scrollBy(x, y); // ①

    	     int new_x = iv.getScrollX();
    	     int new_y = iv.getScrollY();

    	     mRect = iv.getDrawable().getBounds();
    	     mMatrix = iv.getImageMatrix();
    	     mMatrix.getValues(mValues);

    	     // ②
    	     // 画面上の画像の横サイズ
    	     int iw = (int) ((int) mRect.width() * mValues[Matrix.MSCALE_X]);
    	     // 画面上の画像の縦サイズ
    	     int ih = (int) ((int) mRect.height() * mValues[Matrix.MSCALE_Y]);
    	     // 画像の横サイズの半分
    	     int iw_harf = iw / 2;
    	     // 画像の縦サイズの半分
    	     int ih_harf = ih / 2;
    	     // 縦方向の黒くなっているところの高さ
    	     int black_out_w = (iv.getWidth() / 4) - iw_harf;
    	     // 縦方向の黒くなっているところの高さ
    	     int black_out_h = (iv.getHeight() / 4) - ih_harf;

    	     Log.d("imageMove", "new_x=" + Integer.toString(new_x) + ", new_y="
    	             + Integer.toString(new_y));
    	     Log.d("imageMove", "_x=" + Integer.toString(iw) + ", _y="
    	             + Integer.toString(ih));
    	     Log.d("imageMove", "black_out_w=" + Integer.toString(black_out_w)
    	             + ", black_out_h=" + Integer.toString(black_out_h));

    	     // 拡大画像がはみ出ないようにする処理
    	     if (Math.abs(new_x) > black_out_w || Math.abs(new_y) > black_out_h) {
    	         // new_x, new_yが0の場合を考慮する必要あり
    	         if (new_x == 0 && new_y == 0) {
    	             // 何もしない ③
    	         } else if (new_x == 0) {
    	             iv.scrollTo(0, new_y
    	                     / Math.abs(new_y)
    	                     * (int) Math.min(Math.abs(new_y), Math
    	                             .abs(black_out_h))); // ④
    	         } else if (new_y == 0) {
    	             iv.scrollTo(new_x
    	                     / Math.abs(new_x)
    	                     * (int) Math.min(Math.abs(new_x), Math
    	                             .abs(black_out_w)), 0); // ⑤
    	         } else {
    	             iv.scrollTo(new_x
    	                     / Math.abs(new_x)
    	                     * (int) Math.min(Math.abs(new_x), Math
    	                             .abs(black_out_w)), new_y
    	                     / Math.abs(new_y)
    	                     * (int) Math.min(Math.abs(new_y), Math
    	                             .abs(black_out_h))); // ⑥
    	         }
    	     }
    	 }
    	}




}
