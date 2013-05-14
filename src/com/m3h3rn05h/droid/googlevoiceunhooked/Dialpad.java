package com.m3h3rn05h.droid.googlevoiceunhooked;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.m3h3rn05h.droid.grandcentral.Communicator;
import com.m3h3rn05h.droid.grandcentral.Phone;
import com.m3h3rn05h.droid.grandcentral.Voice;
import com.m3h3rn05h.droid.grandcentral.VoiceSession;

public class Dialpad extends Activity {
	protected static final int REQUEST_PICK_CONTACT = 10;
	private Toast backtoast;
	private Voice voice = new Voice(this);
	private Communicator lastCallTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dialpad);
		initGetPhones();
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public void onBackPressed() {
		if (backtoast != null && backtoast.getView().getWindowToken() != null) {
			finish();
		} else {
			backtoast = Toast.makeText(this, "Press back to exit",
					Toast.LENGTH_SHORT);
			backtoast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialpad, menu);
		return true;
	}

	public void initiateCallBack(View view) {
		// voice.getPhones((TextView)findViewById(R.id.status_box));
		Phone phone = (Phone) ((Spinner) findViewById(R.id.callback_phone_input))
				.getSelectedItem();
		String dialedNumber = ((EditText) findViewById(R.id.dialer_input))
				.getText().toString();
		lastCallTask = voice.placeCall(dialedNumber, phone.getPhoneNumber(),
				phone.getType(), (TextView) findViewById(R.id.status_box));

	}

	public void cancelCallBack(View view) {
		Phone phone = (Phone) ((Spinner) findViewById(R.id.callback_phone_input))
				.getSelectedItem();
		String dialedNumber = ((EditText) findViewById(R.id.dialer_input))
				.getText().toString();
		voice.cancelCall(lastCallTask, dialedNumber, phone.getPhoneNumber(),
				phone.getType(), (TextView) findViewById(R.id.status_box));
	}

	private void initGetPhones() {
		ArrayAdapter<Phone> dataAdapter = new ArrayAdapter<Phone>(this,
				android.R.layout.simple_spinner_dropdown_item, VoiceSession
						.getInstance().getPhones());
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.callback_phone_input))
				.setAdapter(dataAdapter);
	}


	public void pickContact(View view) {
		Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(pickImageIntent, REQUEST_PICK_CONTACT);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {  
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case REQUEST_PICK_CONTACT:  
                // handle contact results  
            	Uri contactUri = intent.getData();
            	
            	Cursor c = getContentResolver().query(contactUri, new String[]{ 
                        ContactsContract.CommonDataKinds.Phone.NUMBER,  
                        ContactsContract.CommonDataKinds.Phone.TYPE },
                    null, null, null); 
            	if(c.moveToFirst())
            		((EditText)findViewById(R.id.dialer_input)).setText( c.getString(0));
            }  
        } else {  
            // gracefully handle failure  
            Log.w("Dialpad.onActivityResult", "Warning: activity result not ok: "+resultCode);  
        }  
    }
}
