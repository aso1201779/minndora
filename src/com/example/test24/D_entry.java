package com.example.test24;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class D_entry extends Activity implements View.OnClickListener, UploadAsyncTaskCallback{

	SQLiteDatabase db = null;
	MySQLiteOpenHelper helper = null;
	String username;
	String userID;
	String mapID;
	String spotID;
	int viewID ;
	private Bitmap bm;
	private Uri bitmapUri;
	static final int REQUEST_CODE_CAMERA = 1; /* カメラを判定するコード */
	static final int REQUEST_CODE_GALLERY = 2; /* ギャラリーを判定するコード */

	String inputTitle;
	String inputComment;
	String folderPath;
	String filename;
	boolean flg = false;





	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		Button Dentry =(Button)findViewById(R.id.Send);
		Dentry.setOnClickListener(this);
		Button DReset =(Button)findViewById(R.id.Reset);
		DReset.setOnClickListener(this);
		Button gazouBtn =(Button)findViewById(R.id.gazouBtn);
		gazouBtn.setOnClickListener(this);
		ImageView imageView1 =(ImageView)findViewById(R.id.imageView1);
		imageView1.setOnClickListener(this);

		if(db == null){
			 helper = new MySQLiteOpenHelper(getApplicationContext());
			}try{
				db = helper.getWritableDatabase();
			}catch(SQLiteException e){
				return;
			}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.d_entry);
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		userID = intent.getStringExtra("userID");
		mapID = intent.getStringExtra("mapID");
		spotID = intent.getStringExtra("spotID");
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



	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ

		switch(v.getId()) {

			case R.id.gazouBtn:

				viewID = 1;
				camera();
				break;

			case R.id.Send:
				EditText title = (EditText)findViewById(R.id.Title);
				EditText comment = (EditText)findViewById(R.id.Comment);

				inputTitle = title.getText().toString();
				inputComment = comment.getText().toString();

					if(inputTitle != null && !inputTitle.isEmpty()){


						if(flg == true){
							Log.d("flg","画像をアップロードするよ");
					        upload(folderPath,filename);
					        photo_post();
					        //必ずphotoURLをPOSTしなければならないのか分らん。getmap.phpでテーブル内に値がなかったら
					        //エラーになるのかね？

						}

						//JSONの呼び出し
						exec_post();

						//photoURLもPOSTできたら嬉しい。けど、getmap.phpを書き換えんといかん

						//helper.insertSpot(db, inputTitle, inputComment);







					}
				title.setText("");
				comment.setText("");
			break;

				case R.id.Reset:
					EditText Rtitle = (EditText)findViewById(R.id.Title);
					Rtitle.setText("");
					EditText Rcomment = (EditText)findViewById(R.id.Comment);
					Rcomment.setText("");

					ImageView imageView1 =(ImageView)findViewById(R.id.imageView1);
					imageView1.setImageResource(R.drawable.noimage);
				break;
		}
	}

	protected void camera(){
		// アップロードボタンが押された時
		String[] str_items = {"ギャラリーの選択", "キャンセル"};
		new AlertDialog.Builder(this)
		.setTitle("写真をアップロード")
		.setItems(str_items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自動生成されたメソッド・スタブ
				switch(which){
					case 0:
						flg = true;
						wakeupGallery(); // ギャラリー起動
						break;
					default:
						// キャンセルを選んだ場合
						flg = false;
						break;
							}
				}
		}).show();
	}


	protected void wakeupGallery(){
		Intent i = new Intent();
		i.setType("image/*"); // 画像のみが表示されるようにフィルターをかける
		i.setAction(Intent.ACTION_GET_CONTENT); // 出0他を取得するアプリをすべて開く
		startActivityForResult(i, REQUEST_CODE_GALLERY);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == RESULT_OK){
			if (bm != null)
				bm.recycle(); // 直前のBitmapが読み込まれていたら開放する

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4; // 元の1/4サイズでbitmap取得

			switch(requestCode){
				case 2: // ギャラリーの場合
					try {
						InputStream is = getContentResolver().openInputStream(data.getData());
						folderPath = data.getData().toString();
						Log.d("フォルダのぱす",folderPath);
						int from = folderPath.lastIndexOf("/");
						int to = folderPath.length();
						filename = folderPath.substring(from + 1, to);

						bm = BitmapFactory.decodeStream(is);
						is.close();
						// 選択した画像を表示
					} catch (Exception e) {

					}
				break;
			}
			switch(viewID){
			case 1:
				ImageView imageView1 =(ImageView)findViewById(R.id.imageView1);
				imageView1.setImageBitmap(bm); // imgView（イメージビュー）を準備しておく
				bm = null;
				break;
			}

		}
	}



	 private void exec_post() {

		    Log.d("posttest", "postします");

		    HashMap<String,Object> ret = null;

		    // URL
		    URI url = null;
		    try {
		      url = new URI( "http://54.68.202.192/spotinsert.php" );
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
		    hashMap.put("menberID", userID);
		    hashMap.put("mapID", mapID);
		    hashMap.put("spotID", spotID);
		    hashMap.put("Title", inputTitle);
		    hashMap.put("comment", inputComment);
		    Log.d("menberID",userID);
		    Log.d("mapID",mapID);
		    Log.d("spotID",spotID);
		    Log.d("title",inputTitle);
		    Log.d("comment",inputComment);



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
		            		String GETresponce = EntityUtils.toString(response.getEntity(),"UTF-8");
//		            		GETresponce = String.valueOf(GETresponce.charAt(1));

		            		Log.d("GETresponce",GETresponce);
		            		if (GETresponce.equals("0")){
		            			Log.d("GETresponce","0だったよ");
		            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(D_entry.this);
								alertDialogBuilder.setMessage("登録完了しました。\n登録を続けますか？")
								.setCancelable(false)

								//GPS設定画面起動用ボタンとイベントの定義
								.setPositiveButton("終了",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog, int id) {
												// TODO 自動生成されたメソッド・スタブ
												endpop();
												Log.d("終了","押されたよー");
											}
								});
								//キャンセルボタン処理
								alertDialogBuilder.setNegativeButton("続ける",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int id) {
												// TODO 自動生成されたメソッド・スタブ
												Intent intent = new Intent(D_entry.this,D_entry.class);
												intent.putExtra("username", username);
												intent.putExtra("userID", userID);
												intent.putExtra("mapID", mapID);
												Integer NextSpotID = Integer.parseInt(spotID) + 1;
												spotID = NextSpotID.toString();
												intent.putExtra("spotID", spotID);
												startActivity(intent);
											}
										});
								AlertDialog alert = alertDialogBuilder.create();
								//設定画面へ移動するかの問い合わせダイアログを表示
								alert.show();

		            		}else if (GETresponce.equals("1")){
		            			Log.d("GETresponce","１だったよ");
		            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(D_entry.this);
								alertDialogBuilder.setMessage("１だったよ")


								.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO 自動生成されたメソッド・スタブ
										Intent intent = new Intent(D_entry.this,Login.class);
										startActivity(intent);

									}
								});
								AlertDialog alert = alertDialogBuilder.create();
								//設定画面へ移動するかの問い合わせダイアログを表示
								alert.show();
		            		}else{
		            			Log.d("GETresponce","どれでもなかったよ");
		            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(D_entry.this);
								alertDialogBuilder.setMessage("どっちでもなかったよ")


								.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO 自動生成されたメソッド・スタブ
										Intent intent = new Intent(D_entry.this,Login.class);
										startActivity(intent);

									}
								});
								AlertDialog alert = alertDialogBuilder.create();
								//設定画面へ移動するかの問い合わせダイアログを表示
								alert.show();
		            		}

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
	 public void upload(String... str){
			//Task生成
		    UploadAsyncTask up = new UploadAsyncTask(this,this);
		    up.execute(str[0],str[1]);
		}



	@Override
	public void onSuccessUpload(String result) {
		// TODO 自動生成されたメソッド・スタブ

	}



	@Override
	public void onFailedUpload() {
		// TODO 自動生成されたメソッド・スタブ

	}


	 private void photo_post() {

		    Log.d("posttest", "postします");

		    HashMap<String,Object> ret = null;

		    // URL
		    URI url = null;
		    try {
		      url = new URI( "http://54.68.202.192/photoinsert.php" );
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
		    hashMap.put("menberID", userID);
		    hashMap.put("mapID", mapID);
		    hashMap.put("spotID", spotID);
		    hashMap.put("photoURL",filename);
		    Log.d("menberID",userID);
		    Log.d("mapID",mapID);
		    Log.d("spotID",spotID);
		    Log.d("photoURL",filename);



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
		      ret = httpClient.execute(request, new MyResponseHandler2());
		      //

		    } catch (IOException e) {
		      Log.d("posttest", "通信に失敗：" + e.toString());
		    } finally {
		      // shutdownすると通信できなくなる
		      httpClient.getConnectionManager().shutdown();
		    }

		    // 受信結果をUIに表示
	}
	 public class MyResponseHandler2 implements ResponseHandler<HashMap<String,Object>> {

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
		            		String GETresponce = EntityUtils.toString(response.getEntity(),"UTF-8");
//		            		GETresponce = String.valueOf(GETresponce.charAt(1));

		            		Log.d("GETresponce",GETresponce);
		            		if (GETresponce.equals("0")){

		            		}else if (GETresponce.equals("1")){

		            		}else{

		            		}


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

	 protected void endpop(){
			Intent intent = getIntent();
			final String un = intent.getStringExtra("username");

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setMessage("終了しました。")
			.setCancelable(false)

			//GPS設定画面起動用ボタンとイベントの定義
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							// TODO 自動生成されたメソッド・スタブ
							Intent intent = new Intent(D_entry.this,Home.class);
							intent.putExtra("username", username);
							intent.putExtra("userID", userID);
							startActivity(intent);
						}
			});
			AlertDialog alert = alertDialogBuilder.create();
			//設定画面へ移動するかの問い合わせダイアログを表示
			alert.show();
		}


}