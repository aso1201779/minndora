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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.test24.R.id;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Member_entry extends Activity implements View.OnClickListener{

	SQLiteDatabase db = null;
	MySQLiteOpenHelper helper = null;
	String ddd = null;
	String eee = null;
	private Button btnWeb = null;
	private TextView tv = null;
	private JSONArray rootObjectArray;

	EditText aaa;
	EditText bbb;
	EditText ccc;
	String inputID;
	String inputpass;
	String inputname;
	String inputyear;
	String inputseibetu;

	ImageView entry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_entry);

		// ラジオグループ
		final RadioGroup rgSelect = (RadioGroup)findViewById(R.id.radioGroup1);
		rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			// ラジオボタンが変更されると何番が選択されたか表示する
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.man:
						eee = "0";
						break;

					case R.id.woman:
						eee = "1";
						break;
				}
			}
		});


		Time time = new Time("Asia/Tokyo");
		time.setToNow();
		int date = time.year;

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// アイテムを追加します

		int i;
		for(i=0;i < 100;i++){
			adapter.add(Integer.toString(date));
			date = date - 1 ;
		}
		Spinner spinner = (Spinner) findViewById(id.seinen);
		// アダプターを設定します
		spinner.setAdapter(adapter);
		// スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				Spinner spinner = (Spinner) parent;
				// 選択されたアイテムを取得します
				String item = (String) spinner.getSelectedItem();
				ddd = item;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		entry =(ImageView)findViewById(R.id.btnentry);
		entry.setOnClickListener(this);
		entry.setImageResource(R.drawable.entry);

	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
		case R.id.btnentry:
			entry.setImageResource(R.drawable.touroku_sya);
			aaa = (EditText)findViewById(R.id.userID);
			bbb = (EditText)findViewById(R.id.password);
			ccc = (EditText)findViewById(R.id.user_name);

			inputID = aaa.getText().toString();
			inputpass = bbb.getText().toString();
			inputname = ccc.getText().toString();
			inputyear = ddd;
			inputseibetu = eee;



				if(inputID != null && !inputID.isEmpty()
				   && inputpass != null && !inputpass.isEmpty()
				   && inputname != null && !inputname.isEmpty()){


					if(inputID.length() < 8 && inputpass.length() < 9){

						exec_post();
						//helper.insertMember(db, inputID, inputpass, inputname, inputyear, inputseibetu);


					}else{

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
						alertDialogBuilder.setMessage("規定以上の文字です。\nもう一度操作してください。")

						 .setPositiveButton("OK",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int id) {
									// TODO 自動生成されたメソッド・スタブ
									entry.setImageResource(R.drawable.entry);
								}
					});
						AlertDialog alert = alertDialogBuilder.create();
						alert.show();
					}

				}else{

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
					alertDialogBuilder.setMessage("正しく入力されていません、\n確認してください。")

					 .setPositiveButton("OK",new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {
								// TODO 自動生成されたメソッド・スタブ
								entry.setImageResource(R.drawable.entry);
							}
				});
					AlertDialog alert = alertDialogBuilder.create();
					alert.show();

				}
			aaa.setText("");
			bbb.setText("");
			ccc.setText("");
			break;
		}
	}
	 private void exec_post() {

		    Log.d("posttest", "postします");

		    HashMap<String,Object> ret = null;

		    // URL
		    URI url = null;
		    try {
		      url = new URI( "http://54.68.202.192/menberinsert.php" );
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
		    hashMap.put("menberID", inputID);
		    hashMap.put("password",inputpass );
		    hashMap.put("username", inputname);
		    hashMap.put("birthyear", inputyear);
		    hashMap.put("seibetu", inputseibetu);
		    Log.d("menberID",inputID);
		    Log.d("username",inputpass);
		    Log.d("username",inputname);
		    Log.d("birthyear",inputyear);
		    Log.d("seibetu",inputseibetu);



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
		            		String GETresponce = outputStream.toString(); // JSONデータ

		            		Log.d("GETresponce",GETresponce);
		            		if (GETresponce.equals("0")){
		            			Log.d("GETresponce","0だったよ");
		            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Member_entry.this);
								alertDialogBuilder.setMessage("登録しました。")


								.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO 自動生成されたメソッド・スタブ
										Intent intent = new Intent(Member_entry.this,Login.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);

									}
								});
								AlertDialog alert = alertDialogBuilder.create();
								//設定画面へ移動するかの問い合わせダイアログを表示
								alert.show();

		            		}else if (GETresponce.equals("1")){
		            			Log.d("GETresponce","１だったよ");
		            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Member_entry.this);
								alertDialogBuilder.setMessage("登録失敗しました。\nもう一度操作をして下さい。")


								.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO 自動生成されたメソッド・スタブ
										Intent intent = new Intent(Member_entry.this,Login.class);
										startActivity(intent);

									}
								});
								AlertDialog alert = alertDialogBuilder.create();
								//設定画面へ移動するかの問い合わせダイアログを表示
								alert.show();
		            		}else{
		            			Log.d("GETresponce","どれでもなかったよ");
		            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Member_entry.this);
								alertDialogBuilder.setMessage("エラーです。\nもう一度操作をして下さい。")


								.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO 自動生成されたメソッド・スタブ
										Intent intent = new Intent(Member_entry.this,Login.class);
										startActivity(intent);

									}
								});
								AlertDialog alert = alertDialogBuilder.create();
								//設定画面へ移動するかの問い合わせダイアログを表示
								alert.show();
		            		}


		            	Intent intent = new Intent(Member_entry.this,Login.class);
						startActivity(intent);
		      		      retMap.put("status_code", "200");

		            } catch (Exception e) {
		            	Log.d("Json取得エラー", "Error");
		            	retMap.put("status_code", "220");
		            }

		            break;

		          case HttpStatus.SC_NOT_FOUND:
		            Log.d("posttest", "データが存在しない");
		            retMap.put("status_code", "404");
		            break;

		          default:
		            Log.d("posttest", "通信エラー");
		            retMap.put("status_code", "500");
		            break;
	          }
	          return retMap;
			}
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
