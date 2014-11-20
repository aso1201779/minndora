package com.example.test24;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class D_entry extends Activity implements View.OnClickListener{

	SQLiteDatabase db = null;
	MySQLiteOpenHelper helper = null;
	String username;
	int viewID ;
	private Bitmap bm;
	private Uri bitmapUri;
	static final int REQUEST_CODE_CAMERA = 1; /* カメラを判定するコード */
	static final int REQUEST_CODE_GALLERY = 2; /* ギャラリーを判定するコード */





	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		Button Dentry =(Button)findViewById(R.id.Send);
		Dentry.setOnClickListener(this);
		Button DReset =(Button)findViewById(R.id.Reset);
		DReset.setOnClickListener(this);
		ImageView imageView1 =(ImageView)findViewById(R.id.imageView1);
		imageView1.setOnClickListener(this);
		ImageView imageView2 =(ImageView)findViewById(R.id.imageView2);
		imageView2.setOnClickListener(this);
		ImageView imageView3 =(ImageView)findViewById(R.id.imageView3);
		imageView3.setOnClickListener(this);

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

			case R.id.imageView1:
				viewID = 1;
				camera();
				break;
			case R.id.imageView2:
				viewID = 2;
				camera();
				break;
			case R.id.imageView3:
				viewID = 3;
				camera();
				break;

			case R.id.Send:
				EditText title = (EditText)findViewById(R.id.Title);
				EditText comment = (EditText)findViewById(R.id.Comment);

				String inputTitle = title.getText().toString();
				String inputComment = comment.getText().toString();

					if(inputTitle != null && !inputTitle.isEmpty()){

						helper.insertSpot(db, inputTitle, inputComment);


						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
						alertDialogBuilder.setMessage("登録完了しました。\n登録を続けますか？")
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
						alertDialogBuilder.setNegativeButton("続ける",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO 自動生成されたメソッド・スタブ
										Intent intent = new Intent(D_entry.this,D_entry.class);
										intent.putExtra("username", username);
										startActivity(intent);
									}
								});
						AlertDialog alert = alertDialogBuilder.create();
						//設定画面へ移動するかの問い合わせダイアログを表示
						alert.show();
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
					ImageView imageView2 =(ImageView)findViewById(R.id.imageView2);
					imageView2.setImageResource(R.drawable.noimage);
					ImageView imageView3 =(ImageView)findViewById(R.id.imageView3);
					imageView3.setImageResource(R.drawable.noimage);
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
						wakeupGallery(); // ギャラリー起動
						break;
					default:
						// キャンセルを選んだ場合
						break;
							}
				}
		}).show();
	}

	protected void wakeupCamera(){
		File mediaStorageDir = new File(
			Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES
			), "PictureSaveDir"
		);
		if (! mediaStorageDir.exists() & ! mediaStorageDir.mkdir()){
			return;
		}
		String timeStamp = new SimpleDateFormat("yyyMMddHHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".JPG");
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		bitmapUri = Uri.fromFile(mediaFile);
		i.putExtra(MediaStore.EXTRA_OUTPUT, bitmapUri); // 画像をmediaUriに書き込み
		startActivityForResult(i, REQUEST_CODE_CAMERA);
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
			case 2:
				ImageView imageView2 =(ImageView)findViewById(R.id.imageView2);
				imageView2.setImageBitmap(bm); // imgView（イメージビュー）を準備しておく
				bm = null;
				break;
			case 3:
				ImageView imageView3 =(ImageView)findViewById(R.id.imageView3);
				imageView3.setImageBitmap(bm); // imgView（イメージビュー）を準備しておく
				bm = null;
				break;
			}

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
						startActivity(intent);
					}
		});
		AlertDialog alert = alertDialogBuilder.create();
		//設定画面へ移動するかの問い合わせダイアログを表示
		alert.show();
	}

}
