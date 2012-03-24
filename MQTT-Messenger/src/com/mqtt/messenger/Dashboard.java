package com.mqtt.messenger;

import android.app.Activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;

public class Dashboard extends Activity {
	
	private String android_id;
	private MqttClient client;
	private TextView messageView;
	private String server;
	private int port;
	private ScrollView scroller;
	private AlertDialog alert;
	private ProgressDialog pd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dashpage);
        
        server = getIntent().getStringExtra("server");
        port = Integer.parseInt(getIntent().getStringExtra("port"));
        
		messageView = (TextView) findViewById(R.id.message);
		android_id = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
		scroller = (ScrollView) findViewById(R.id.scrollView1);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("\nMQTT Messenger V1.0\n");
		alert = builder.create();
		
		 pd = ProgressDialog.show(this, "Connecting", "Please wait..", true,false);
		 new Thread() { 
			 int flag;
			 public void run() { 
				 if(connect())
					 flag=1;
				 else 
					 flag=0;
				 handlerConnect.sendMessage(Message.obtain(handlerConnect,flag)); } }.start();	//Connect in a new thread!
		 
    }
    
    final Handler handlerConnect = new Handler() {
		public void handleMessage(Message msg) {
			pd.dismiss();
			if(msg.what==0)
				{
				Intent i = new Intent (Dashboard.this, StartPage.class);
				i.putExtra("msg", "Connect Failed!");
				startActivity(i);
				finish();
				}
			
		}
	};
    final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			messageView.append("\n\n" + msg.getData().getString("topic")+"\n"+msg.getData().getString("message")+"\n\n");
			scroller.post(new Runnable() {
				   public void run() {
				        scroller.scrollTo(messageView.getMeasuredWidth(), messageView.getMeasuredHeight());
				    }
				});
		}
		
	};

	private boolean connect() {
		try {
			messageView.setText("");
			client = (MqttClient) MqttClient.createMqttClient("tcp://"+server+":"+port, null);
			client.registerSimpleHandler(new MessageHandler());
			client.connect("HM" + android_id, true, (short) 240);
			return true;
		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	private class MessageHandler implements MqttSimpleCallback 
	{
		public void publishArrived(String _topic, byte[] payload, int qos, boolean retained) throws Exception 
		{
			String _message = new String(payload);
			Bundle b = new Bundle();
			b.putString("topic", _topic);
			b.putString("message", _message);
			Message msg = handler.obtainMessage();
			msg.setData(b);
			handler.sendMessage(msg);
			Log.d("MQTT", _message);
		}

		public void connectionLost() throws Exception 
		{
			client = null;
			Log.v("HelloMQTT", "connection dropped");
			Thread t = new Thread(new Runnable() {

				public void run() 
				{
					do {// pause for 5 seconds and try again;
						Log.v("HelloMQTT",
								"sleeping for 10 seconds before trying to reconnect");
						try {
							Thread.sleep(10 * 1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} while (!connect());
					System.err.println("reconnected");
				}
			});
		}
	}
	
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.dashitem1:	try {
        						client.publish("NITTrichy", "This is a Demo Message from Android Application".getBytes() ,1, false);
        						} catch ( MqttException e){
        							Toast.makeText(this, "Publish Failed!", Toast.LENGTH_SHORT).show();
        						}
        						Toast.makeText(this, "Publish Success!", Toast.LENGTH_SHORT).show();
        						break;
        						
        case R.id.dashitem2:	try {
					        	String topics[] = { "#" };
								int qos[] = { 1 };
								client.subscribe(topics, qos);
								} catch ( MqttException e){
									Toast.makeText(this, "Subscribe Failed!", Toast.LENGTH_SHORT).show();
								}
								Toast.makeText(this, "Subscribe Success!", Toast.LENGTH_SHORT).show();
								messageView.setText("");
        						break;
        
        case R.id.dashitem3:	try {
					        	String topics[] = { "#" };
								client.unsubscribe(topics);
								} catch ( MqttException e){
									Toast.makeText(this, "Unsubscribe Failed!", Toast.LENGTH_SHORT).show();
								}
								Toast.makeText(this, "Unsubscribe Success!", Toast.LENGTH_SHORT).show();
								messageView.setText("");
        						break;
        
        case R.id.dashitem4:	try {
									client.disconnect();
									client.terminate();
								} catch (MqttPersistenceException e) {
									e.printStackTrace();
								}
        						Intent i = new Intent (Dashboard.this, StartPage.class);
        						startActivity(i);
        						finish();
        						break;
        						
        case R.id.dashitem5:	try {
								client.disconnect();
								client.terminate();
							} catch (MqttPersistenceException e) {
								e.printStackTrace();
							}
	        					finish();
								break;
								
        case R.id.dashitem6:	alert.show();
        						break;
            default:     		break;
        }
        return true;
    }
}	