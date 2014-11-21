package com.example.test24;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
	private JSONArray rootObjectArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.w_random);
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		userID = intent.getStringExtra("userID");

		mapID = intent.getStringExtra("mapID");
		spotID = intent.getStringExtra("spotID");
		GETmenberID = intent.getStringExtra("GETmenberID");
		GETusername = intent.getStringExtra("GETusername");
		seibetu = intent.getStringExtra("seibetu");
		title = intent.getStringExtra("title");
		photoURL= intent.getStringExtra("photoURL");
		mapURL = intent.getStringExtra("mapURL");

	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		Button back = (Button)findViewById(R.id.backbtn);
		back.setOnClickListener(this);
		Button watchmap = (Button)findViewById(R.id.watchMap);
		watchmap.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Intent intent = null;

		switch(v.getId()){
		case R.id.watchMap:

			//JSONの呼び出し
			exec_post();


			break;

		case R.id.backbtn:
			intent = new Intent(W_Random.this,W_Select.class);
			startActivity(intent);
			break;
		}
	}

	private void exec_post() {

	    Log.d("posttest", "postします");

	    HashMap<String,Object> ret = null;

	    // URL
	    URI url = null;
	    try {
	      url = new URI( "http://54.68.202.192/getmap2.php" );
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
	    hashMap.put("menberID",GETmenberID );
	    hashMap.put("mapID",mapID );
	    Log.d("menberID",GETmenberID);
	    Log.d("mapID",mapID);


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

		            		//ループ処理をしたい
		            		JSONObject jsonobject = rootObjectArray.getJSONObject(0);


		            		String mapdate = jsonobject.getString("mapdate");
		            		String mapURL = jsonobject.getString("mapURL");

		            		Log.d("mapdate",mapdate);
		            		Log.d("mapID",mapURL);

		            		retMap.put("mapdate", mapdate);
		            		retMap.put("mapURL", mapURL);

		            		String GETmapdate = (String)retMap.get("mapdate");
		            		String GETmapURL = (String)retMap.get("mapURL");
		            		Log.d("response","1");


		      		      	Intent intent = null;
		      		      intent = new Intent(W_Random.this,Wmap.class);

		      		      intent.putExtra("username", username);
		      		      intent.putExtra("userID", userID);

		      		      intent.putExtra("mapdate", GETmapdate);
		      		      intent.putExtra("mapURL",GETmapURL);
		      		      startActivity(intent);



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



}