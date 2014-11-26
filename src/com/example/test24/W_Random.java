package com.example.test24;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.test24.W_Select.MyResponseHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class W_Random extends Activity implements View.OnClickListener {

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
	String flg;
	private JSONArray rootObjectArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.w_random);
		Intent intent = getIntent();
		flg = intent.getStringExtra("flg");
		if(flg == "1"){
			username = intent.getStringExtra("username");
			userID = intent.getStringExtra("userID");
		}
		mapID = intent.getStringExtra("mapID");
		spotID = intent.getStringExtra("spotID");
		GETmenberID = intent.getStringExtra("GETmenberID");
		GETusername = intent.getStringExtra("GETusername");
		seibetu = intent.getStringExtra("seibetu");
		title = intent.getStringExtra("title");
		photoURL= intent.getStringExtra("photoURL");
		mapURL = intent.getStringExtra("mapURL");

		TextView tv = (TextView)findViewById(R.id.textRandom);
		tv.setText(GETusername + "さんのマップ");

		TextView w_title = (TextView)findViewById(R.id.w_title);
		w_title.setText(title);

		//StrictModeを設定 penaltyDeathを取り除く
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

         //TextViewを取得
         ImageView test = (ImageView)findViewById(R.id.w_photo);
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

             //TextViewの背景に表示
            test.setBackgroundDrawable(d);

         } catch (Exception e) {
             System.out.println("nuu: "+e);
         }

	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		ImageView back = (ImageView)findViewById(R.id.backbtn);
		back.setOnClickListener(this);
		ImageView watchmap = (ImageView)findViewById(R.id.watchMap);
		watchmap.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Intent intent = null;

		switch(v.getId()){
		case R.id.watchMap:

			intent = new Intent(W_Random.this,Wmap.class);
			intent.putExtra("flg", flg);
			if(flg == "1"){
		      intent.putExtra("username", username);
		      intent.putExtra("userID", userID);
			}
		      intent.putExtra("mapURL",mapURL);
		      startActivity(intent);


			break;

		case R.id.backbtn:
			intent = new Intent(W_Random.this,W_Select.class);
			startActivity(intent);
			break;
		}
	}

}