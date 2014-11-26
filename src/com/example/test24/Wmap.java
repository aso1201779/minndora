package com.example.test24;

import java.io.InputStream;
import java.net.URL;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Wmap extends Activity implements View.OnClickListener{

	String username;
	String userID;
	String mapdate;
	String mapURL;
	String flg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wmap);
		Intent intent = getIntent();
		flg = intent.getStringExtra("flg");
		if(flg == "1"){
		username = intent.getStringExtra("username");
		userID = intent.getStringExtra("userID");
		}
		mapURL = intent.getStringExtra("mapURL");
	    Log.d("mapURL",mapURL);

	    //StrictModeを設定 penaltyDeathを取り除く
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

         //TextViewを取得
         ImageView test = (ImageView)findViewById(R.id.Wimage);
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

             //TextViewの背景に表示
            test.setBackgroundDrawable(d);

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
						intent.putExtra("flg", flg);
						if(flg == "1"){
						intent.putExtra("username", username);
						intent.putExtra("userID", userID);
						}
						startActivity(intent);
					}
		});
		AlertDialog alert = alertDialogBuilder.create();
		//設定画面へ移動するかの問い合わせダイアログを表示
		alert.show();
	}

}