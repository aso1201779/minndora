package com.example.test24;

import java.io.InputStream;
import java.net.URL;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
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
import android.widget.Button;
import android.widget.ImageView;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Wmap extends Activity implements View.OnClickListener,OnTouchListener{

	String username;
	String userID;
	String mapdate;
	String mapURL;
	int flg;

	ImageView test;
	Bitmap bm;

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
    private float maxScale = 5F;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wmap);
		Intent intent = getIntent();
		flg = Integer.parseInt(intent.getStringExtra("flg"));
		if(flg == 1){
		username = intent.getStringExtra("username");
		userID = intent.getStringExtra("userID");
		}
		mapURL = intent.getStringExtra("mapURL");
	    Log.d("mapURL",mapURL);

	    //StrictModeを設定 penaltyDeathを取り除く
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

         //TextViewを取得
         test = (ImageView)findViewById(R.id.Wimage);
         test.setOnTouchListener(this);
         //画像のURL
         String urlString="http://54.68.202.192/img/" + mapURL;

         try {
             //URLクラス
            URL url = new URL(urlString);
             //入力ストリームを開く
            InputStream istream = url.openStream();

             //画像をDrawableで取得
            Drawable d = Drawable.createFromStream(istream, "webimg");

             //入力ストリームを閉じる
            istream.close();

            bm = ((BitmapDrawable) d).getBitmap();

            //TextViewの背景に表示
            test.setImageBitmap(bm);

         } catch (Exception e) {
             System.out.println("nuu: "+e);
         }
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		Button MapEnd = (Button)findViewById(R.id.button1);
		MapEnd.setOnClickListener(this);
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()){
			case R.id.button1:
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder.setMessage("ウォッチを終了しますか？")
				.setCancelable(false)

				//GPS設定画面起動用ボタンとイベントの定義
				.setPositiveButton("終了",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {
								// TODO 自動生成されたメソッド・スタブ
								endpop();
							}
				});
				//キャンセルボタン処理
				alertDialogBuilder.setNegativeButton("キャンセル",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// TODO 自動生成されたメソッド・スタブ
								dialog.cancel();
							}
						});
				AlertDialog alert = alertDialogBuilder.create();
				//設定画面へ移動するかの問い合わせダイアログを表示
				alert.show();
				break;
		}
	}
	protected void endpop(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("終了しました。")
		.setCancelable(false)

		//GPS設定画面起動用ボタンとイベントの定義
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// TODO 自動生成されたメソッド・スタブ
						Intent intent = new Intent(Wmap.this,Home.class);
						if(flg == 1){
						intent.putExtra("username", username);
						intent.putExtra("userID", userID);
						intent.putExtra("flg","1");
						}else{
							intent.putExtra("flg","0");
						}
						startActivity(intent);
					}
		});
		AlertDialog alert = alertDialogBuilder.create();
		//設定画面へ移動するかの問い合わせダイアログを表示
		alert.show();
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
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
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
                if(minScale >= tmpRatio){
                	test.setImageBitmap(bm);
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

}