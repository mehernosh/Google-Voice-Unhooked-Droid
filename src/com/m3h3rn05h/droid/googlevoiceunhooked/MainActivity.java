package com.m3h3rn05h.droid.googlevoiceunhooked;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.m3h3rn05h.droid.grandcentral.Voice;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((TextView)findViewById( R.id.status_box)).setMovementMethod(new ScrollingMovementMethod());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void doLogin(View view){
		String username = ((EditText) findViewById(R.id.username_input)).getText().toString().trim() + "@gmail.com"; 
		String password = ((EditText) findViewById(R.id.passwd_input)).getText().toString();
		Voice voice = new Voice(this);
		voice.authenticate(username,password,(TextView)findViewById( R.id.status_box));
	}
}
