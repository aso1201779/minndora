package com.example.test24;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Home extends Activity implements View.OnClickListener{

	int gest = 0;
	String un;
	String username;
	String userID;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);


		TextView tv = (TextView)findViewById(R.id.myname);
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		userID = intent.getStringExtra("userID");
		Log.d("getuserID",userID);

		if(username != null){
			tv.setText("ようこそ" + username + "さん");
		}else{
			tv.setText("ようこそゲストさん");
			gest = 1;
		}
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		ImageButton drive =(ImageButton)findViewById(R.id.drive);
		drive.setOnClickListener(this);
		ImageButton watch =(ImageButton)findViewById(R.id.watch);
		watch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
			case R.id.drive:
				if(gest != 1){
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
				intent.putExtra("username", username);
				intent.putExtra("userID", userID);
				startActivity(intent);

		}

	}
}