package com.example.networktest2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

	public static final int SHOW_RESPONSE=0;
	private Button sendRequest;
	private TextView responseText;
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case SHOW_RESPONSE:
				String response=(String)msg.obj;
				responseText.setText(response);
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sendRequest=(Button)findViewById(R.id.send_request);
		responseText=(TextView)findViewById(R.id.response_text);
		sendRequest.setOnClickListener(this);
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
		switch(v.getId()){
		case R.id.send_request:
			//sendRequestWithHttpURLConnection();
			sendRequestWithHttpClient();
		}
		
	}

	private void sendRequestWithHttpClient() {
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					HttpClient httpClient=new DefaultHttpClient();
					HttpGet httpGet=new HttpGet("https://api.heweather.com/x3/weather?cityid=CN101230510&key=dc908906531e4c38886eb3245eab890d");
					HttpResponse httpResponse=httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode()==200){
						//请求和响应都成功了
						HttpEntity entity=httpResponse.getEntity();
						String response=EntityUtils.toString(entity,"utf-8");
						Log.v("TAG", "client_RESPONSE="+response);
						Message message=new Message();
						message.what=SHOW_RESPONSE;
						message.obj=response.toString();
						handler.sendMessage(message);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}).start();
		
	}

	private void sendRequestWithHttpURLConnection() {
		new Thread(new Runnable(){

			@Override
			public void run() {
				HttpURLConnection connection=null;
				try{
					//android4.4下,地址末位是.html的用HttpURLConnection格式无法获取到response，只能用HttpClient替换
					
					//URL url=new URL("http://www.baidu.com");
					URL url=new URL("https://api.heweather.com/x3/weather?cityid=CN101230510&key=dc908906531e4c38886eb3245eab890d");
					
					connection =(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);					
					InputStream in=connection.getInputStream();					
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					Log.v("TAG", "connection_RESPONSE="+response);
					Message message=new Message();
					message.what=SHOW_RESPONSE;
					message.obj=response.toString();
					handler.sendMessage(message);
				}catch(Exception e){
					Log.v("TAG", "something wrong");
					e.printStackTrace();
				}finally{
					
					if(connection!=null){
						connection.disconnect();
					}
				}
				
			}
			
		}).start();
		
	}
}
