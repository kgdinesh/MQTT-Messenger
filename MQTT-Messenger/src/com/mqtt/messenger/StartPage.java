package com.mqtt.messenger;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StartPage extends Activity {
	public Spinner e1;
	public EditText e2;
	AlertDialog alert;

	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.startpage);

		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.server_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("\nMQTT Messenger V1.0\n");
		alert = builder.create();

		Bundle extras = getIntent().getExtras();
		if(extras!=null)
			Toast.makeText(this, extras.getString("msg"), Toast.LENGTH_SHORT).show(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.startmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.startitem1:
			finish();
			break;
		case R.id.startitem2: alert.show();
			break;
		default:
			break;
		}
		return true;
	}

	public void processLogin(View v) {

		e1 = (Spinner) findViewById(R.id.spinner1);
		String server = e1.getSelectedItem().toString();
		e2 = (EditText) findViewById(R.id.editText2);
		String port = e2.getText().toString();
		
		Intent i = new Intent(StartPage.this, Dashboard.class);
		i.putExtra("server", server);
		i.putExtra("port", port);
		startActivity(i);
		finish();
	}
}