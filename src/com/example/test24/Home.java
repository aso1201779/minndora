package com.example.test24;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Home extends Activity implements View.OnClickListener{

	int gest = 0;
	String un;
	String username;
	String userID;
	int flg;

	ImageView drive;
	ImageView watch;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);


		TextView tv = (TextView)findViewById(R.id.myname);
		Intent intent = getIntent();
		flg = Integer.parseInt(intent.getStringExtra("flg"));
		if(flg == 1){
			username = intent.getStringExtra("username");
			userID = intent.getStringExtra("userID");
			tv.setText("ようこそ" + username + "さん");
			Log.d("ここだよ","会員");
		}else{
			tv.setText("ようこそゲストさん");
			gest = 1;
			Log.d("ここだよ","ゲスト");
		}
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		drive =(ImageView)findViewById(R.id.drive);
		drive.setOnClickListener(this);
		drive.setImageResource(R.drawable.drive2);
		watch =(ImageView)findViewById(R.id.watch);
		watch.setOnClickListener(this);
		watch.setImageResource(R.drawable.watch3);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
			case R.id.drive:
				drive.setImageResource(R.drawable.drive_sya);
				if(flg == 1){
					intent = new Intent(Home.this, Dmap.class);
					intent.putExtra("username", username);
					intent.putExtra("userID", userID);
					startActivity(intent);
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
					alertDialogBuilder.setMessage("非会員はご利用になれません。")

					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							// TODO 自動生成されたメソッド・スタブ
							drive.setImageResource(R.drawable.drive2);
						}
			});
					AlertDialog alert = alertDialogBuilder.create();
					alert.show();
				}
				break;
			case R.id.watch:
				watch.setImageResource(R.drawable.watch_sya);
				intent = new Intent(Home.this,W_Select.class);
				if(flg == 1){
					intent.putExtra("username", username);
					intent.putExtra("userID", userID);
					intent.putExtra("flg","1");
				}else{
					intent.putExtra("flg","0");
				}
				startActivity(intent);

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		if(keyCode==KeyEvent.KEYCODE_BACK){
            // アラートダイアログ
            dialog();
            return true;
        }
        return false;
	}

	private void dialog() {
		// TODO 自動生成されたメソッド・スタブ
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("ログイン画面に移動しますか？")
		.setCancelable(false)

		//GPS設定画面起動用ボタンとイベントの定義
		.setPositiveButton("移動する",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// TODO 自動生成されたメソッド・スタブ
						Home.this.finish();
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
	}

}