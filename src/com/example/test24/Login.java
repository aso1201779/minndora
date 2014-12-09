package com.example.test24;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Login extends Activity implements View.OnClickListener{

	SQLiteDatabase db = null;
	MySQLiteOpenHelper helper = null;
	private JSONArray rootObjectArray;
	String inputloginID = null;
	String inputloginpass = null;
	String flg;

	ImageView login;
	ImageView notMember;
	ImageView member_entry;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		login =(ImageView)findViewById(R.id.login);
		login.setOnClickListener(this);
		login.setImageResource(R.drawable.login);
		notMember =(ImageView)findViewById(R.id.notMember);
		notMember.setOnClickListener(this);
		notMember.setImageResource(R.drawable.nomember);
		member_entry =(ImageView)findViewById(R.id.member_entry);
		member_entry.setOnClickListener(this);
		member_entry.setImageResource(R.drawable.newentry);


		if(db == null){
			helper = new MySQLiteOpenHelper(getApplicationContext());
		}
		try{
			db = helper.getWritableDatabase();
		}catch(SQLiteException e){
			Log.e("ERROR" , e.toString());
		}
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
		case R.id.login:
			login.setImageResource(R.drawable.login_sya);

			EditText ID = (EditText)findViewById(R.id.userID);
			EditText pass = (EditText)findViewById(R.id.password);
			inputloginID = ID.getText().toString();
			inputloginpass = pass.getText().toString();

				if(inputloginID.length() > 0 && inputloginpass.length() > 0){

					if(inputloginID.length() < 8  && inputloginpass.length() < 9){

						//JSONの呼び出し
						exec_post();
						//String strSelect = helper.selectMember(db, inputloginID, inputloginpass);
					}else{
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
						alertDialogBuilder.setMessage("文字数が多いです")

						 .setPositiveButton("OK",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int id) {
									// TODO 自動生成されたメソッド・スタブ
									login.setImageResource(R.drawable.login);
								}
					});
						AlertDialog alert = alertDialogBuilder.create();
						alert.show();
					}
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
					alertDialogBuilder.setMessage("正しく入力されていません、\n確認してください。")

					 .setPositiveButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {
								// TODO 自動生成されたメソッド・スタブ
								login.setImageResource(R.drawable.login);
							}
				});

					AlertDialog alert = alertDialogBuilder.create();
					alert.show();
				}
			ID.setText("");
			pass.setText("");

			break;
		case R.id.member_entry:
			member_entry.setImageResource(R.drawable.kaiintouroku_sya);
			intent = new Intent(Login.this, Member_entry.class);
			startActivity(intent);
			break;
		case R.id.notMember:
			notMember.setImageResource(R.drawable.hikaiin_sya);
			intent = new Intent(Login.this, Home.class);
			intent.putExtra("flg","0");
			startActivity(intent);
			break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 private void exec_post() {

		    Log.d("posttest", "postします");

		    HashMap<String,Object> ret = null;

		    // URL
		    URI url = null;
		    try {
		      url = new URI( "http://54.68.202.192/getname.php" );
		      Log.d("posttest", "URLはOK");
		    } catch (URISyntaxException e) {
		      e.printStackTrace();
		      //String code =toString(ret.getStatusLine().getStatusCode());
		      //ret = e.toString();
		    }

		    // POSTパラメータ付きでPOSTリクエストを構築
		    HttpPost request = new HttpPost( url );

		    /*
		    List<NameValuePair> post_params\e = new ArrayList<NameValuePair>();
		    post_params.add(new BasicNameValuePair("post_1", "ユーザID"));
		    post_params.add(new BasicNameValuePair("post_2", "パスワード"));
		    */


		    HashMap<String, Object> hashMap = new HashMap<String, Object>();
		    hashMap.put("menberID", inputloginID);
		    hashMap.put("password",inputloginpass );
		    Log.d("menberID",inputloginID);
		    Log.d("password",inputloginpass);


		    //オブジェクトクラスHashMap　キーワードと値をペアでセット

		    try {
			    request.setHeader("Content-Type", "application/json; charset=utf-8");
			    //
			    Type mapType = new TypeToken<HashMap<String, Object>>() {}.getType();
			    //HashMapをJSONに変換
			    request.setEntity(new StringEntity(new Gson().toJson(hashMap, mapType)));
			    //同上

			    /*
			    // 送信パラメータのエンコードを指定
		        request.setEntity(new UrlEncodedFormEntity(post_params, "UTF-8"));
		        */

		    } catch (UnsupportedEncodingException e1) {
		        e1.printStackTrace();
		    }

		    // POSTリクエストを実行
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    try {
		      Log.d("posttest", "POST開始");

		      // POSTを実行して、戻ってきたJSONをHashMapの形にして受け取る
		      ret = httpClient.execute(request, new MyResponseHandler());
		      //


		    } catch (IOException e) {
		      Log.d("posttest", "通信に失敗：" + e.toString());
		    } finally {
		      // shutdownすると通信できなくなる
		      httpClient.getConnectionManager().shutdown();
		    }

		    // 受信結果をUIに表示
	}
	 public class MyResponseHandler implements ResponseHandler<HashMap<String,Object>> {

			@Override
			public HashMap<String,Object> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				// TODO 自動生成されたメソッド・スタブ
				//		          Log.d(
				//		            "posttest",
				//		            "レスポンスコード：" + response.getStatusLine().getStatusCode()

				HashMap<String,Object> retMap = new HashMap<String,Object>();

	            // 正常に受信できた場合は200
		          switch (response.getStatusLine().getStatusCode()) {
			          case HttpStatus.SC_OK:
			            Log.d("posttest", "レスポンス取得に成功");

			            try {
			            		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			            		response.getEntity().writeTo(outputStream);
			            		String data;
			            		data = outputStream.toString(); // JSONデータ
			            		rootObjectArray = new JSONArray(data);


			            		JSONObject jsonobject = rootObjectArray.getJSONObject(0);


			            		String username = jsonobject.getString("username");
			            		String menberID = jsonobject.getString("menberID");
			            		Log.d("username",username);
			            		Log.d("menberID",menberID);

			            		retMap.put("username", username);
			            		retMap.put("menberID", menberID);

			            		String GETusername = (String)retMap.get("username");
			            		String GETmenberID = (String)retMap.get("menberID");
			            		Log.d("response","1");
			      		      	Log.d("response","2");
			      		      	Intent intent = null;
			      		      	intent = new Intent(Login.this, Home.class);
			      		      	intent.putExtra("userID", GETmenberID);
			      		      	intent.putExtra("username",GETusername );
			      		      	flg = "1";
			      		      	intent.putExtra("flg","1");
			      		      	startActivity(intent);


			            } catch (Exception e) {
			            	Log.d("Json取得エラー", "Error");
			            	retMap.put("status_code", "220");
			            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
							alertDialogBuilder.setMessage("IDまたは\npassが違います。")

							 .setPositiveButton("OK",  new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO 自動生成されたメソッド・スタブ
										login.setImageResource(R.drawable.login);
									}
						});
							AlertDialog alert = alertDialogBuilder.create();
							alert.show();
			            }

			            break;

			          case HttpStatus.SC_NOT_FOUND:
			            Log.d("posttest", "データが存在しない");
			            retMap.put("status_code", "404");
			            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
						alertDialogBuilder.setMessage("IDまたは\npassが違います。")

						 .setPositiveButton("OK", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int id) {
									// TODO 自動生成されたメソッド・スタブ
									login.setImageResource(R.drawable.login);
								}
					});
						AlertDialog alert = alertDialogBuilder.create();
						alert.show();

			            break;

			          default:
			            Log.d("posttest", "通信エラー");
			            retMap.put("status_code", "500");
			            break;
		          }
		          return retMap;

			}
	 }

}
