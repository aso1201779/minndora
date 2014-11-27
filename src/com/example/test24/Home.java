package com.example.test24;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Home extends Activity implements View.OnClickListener{

	int gest = 0;
	String un;
	String username;
	String userID;
	int flg;


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
		ImageView drive =(ImageView)findViewById(R.id.drive);
		drive.setOnClickListener(this);
		ImageView watch =(ImageView)findViewById(R.id.watch);
		watch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
			case R.id.drive:
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
							dialog.cancel();
						}
					});
					AlertDialog alert = alertDialogBuilder.create();
					alert.show();
				}
				break;
			case R.id.watch:
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
}