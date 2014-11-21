package com.example.test24;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class UploadAsyncTask
  extends AsyncTask<String, Integer, String> {

	private UploadAsyncTaskCallback callback;
	ProgressDialog dialog;
	Context context;
	String result;

  public UploadAsyncTask(Context context,UploadAsyncTaskCallback callback){
    this.context = context;
    this.callback = callback;
  }

@Override
  protected String doInBackground(String... params) {
    try {
      String fileName = params[0];

      HttpClient httpClient = new DefaultHttpClient();
      HttpPost request = new HttpPost("http://54.68.202.192/postphoto.php");
      MultipartEntity multipartEntity =
        new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

      File file = new File(fileName);
      FileBody fileBody = new FileBody(file, file.getAbsolutePath());
      multipartEntity.addPart("f1", fileBody);
      multipartEntity.addPart("filename", new StringBody(params[1]));
      request.setEntity(multipartEntity);
      //Response
      result = httpClient.execute(request, new ResponseHandler<String>(){
          public String handleResponse(HttpResponse response) throws IOException{
              switch(response.getStatusLine().getStatusCode()){
              case HttpStatus.SC_OK:
                  System.out.println(HttpStatus.SC_OK);
                  return EntityUtils.toString(response.getEntity(), "UTF-8");
              case HttpStatus.SC_NOT_FOUND:
                  System.out.println(HttpStatus.SC_NOT_FOUND);
                  return "404";
              default:
                  System.out.println("unknown");
                  return "unknown";
              }
          }
      });
      System.out.println(result);
      httpClient.getConnectionManager().shutdown();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  @Override
  protected void onPostExecute(String result) {
    if(dialog != null){
      dialog.dismiss();
    }
    if(result == null)
    {
    	callback.onFailedUpload();
    }else{
    	callback.onSuccessUpload(result);
    }
  }

  @Override
  protected void onPreExecute() {
    dialog = new ProgressDialog(context);
    dialog.setTitle("Please wait");
    dialog.setMessage("Uploading...");
    dialog.show();
  }
}