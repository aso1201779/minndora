package com.example.test24;

public interface UploadAsyncTaskCallback {
	  /**
	   * 画像のダウンロードが成功した時に呼ばれるメソッド
	   */
	  void onSuccessUpload(String result);

	  /**
	   * 画像のダウンロードが失敗した時に呼ばれるメソッド
	   */
	  void onFailedUpload();

}