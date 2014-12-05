package com.example.test24;

import java.io.InputStream;
import java.net.URL;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ZoomControls;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class W_photo extends Activity implements View.OnClickListener{

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
    float resizeW =1.0f;
    float resizeY =1.0f;
    ImageView test;
    Drawable d;

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

         //TextViewを取得
         ImageView test = (ImageView)findViewById(R.id.w_photo_big);
         //画像のURL
         String urlString="http://54.68.202.192/img/" + photoURL;

         try {
             //URLクラス
            URL url = new URL(urlString);
             //入力ストリームを開く
            InputStream istream = url.openStream();

             //画像をDrawableで取得
            d = Drawable.createFromStream(istream, "webimg");

             //入力ストリームを閉じる
            istream.close();

             //TextViewの背景に表示
            test.setImageDrawable(d);

         } catch (Exception e) {
             System.out.println("nuu: "+e);
         }


      // ズームコントロールを取得
         ZoomControls zoomCtr = (ZoomControls)findViewById(R.id.zoomControls1);
         // ズームイン
         zoomCtr.setOnZoomInClickListener(new OnClickListener() {
             public void onClick(View v) {
            	// 拡大比率 10倍まで
                 if(resizeW<=10){
                     resizeW += 1.0f;
                     resizeY += 1.0f;
                 }

                 //拡大処理
                 onBitmapResize();
             }
         });
         // ズームアウト
         zoomCtr.setOnZoomOutClickListener(new OnClickListener() {
             public void onClick(View v) {
            	// 縮小比率 倍率が１より大きいときに縮小
                 if(resizeW>1){
                     resizeW -= 1.0f;
                     resizeY -= 1.0f;
                 }

                 //拡大処理
                 onBitmapResize();

             }
         });

	}
	private void onBitmapResize(){
        Resources r = getResources();
        Matrix matrix = new Matrix();

        // 比率をMatrixに設定
        matrix.postScale( resizeW, resizeY );

        // 画像
        Bitmap bs = ((BitmapDrawable) d).getBitmap();

        // リサイズ
        Bitmap br = Bitmap.createBitmap(bs, 0, 0, bs.getWidth(),bs.getHeight(), matrix,true);

        //画像をセット
        test.setImageBitmap(br);
    }


	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
