package com.m3h3rn05h.droid.grandcentral;

import android.app.Activity;
import android.widget.TextView;

public class Voice {
	//public String authToken;
	private Activity caller;
	private String username,password,authToken;
	private void setAuthToken(String val){
		this.authToken = val;
	}
	
	public Voice(Activity caller){
		this.caller = caller;
	}
	
	public void authenticate(String user, String pass,TextView textView){
		authenticate(user, pass, textView,false);
	}
	
	public void authenticate(String user, String pass,TextView textView, Boolean rememberMe){
		Communicator communicator = Communicator.getInstance();
		communicator.setTextViewRef(textView);
		communicator.execute("0",user,pass);
		if(rememberMe){
			this.username = user;
			this.password = pass;
		}
	}
	
	public void getPhones(TextView textView){
		Communicator communicator = Communicator.getInstance();
		communicator.setTextViewRef(textView);
		communicator.execute("2");
	}
	
	public Communicator placeCall(String toPhone, String fromPhone,int phoneType, TextView textView){
		Communicator communicator = Communicator.getInstance();
		communicator.setTextViewRef(textView);
		communicator.execute("3",toPhone,fromPhone,phoneType+"");
		return communicator;
	}
	
	public void cancelCall(Communicator callTask, String toPhone, String fromPhone,int phoneType, TextView textView){
		if(callTask!= null) callTask.cancel(true);
		Communicator communicator = Communicator.getInstance();
		communicator.setTextViewRef(textView);
		communicator.execute("4",toPhone,fromPhone,phoneType+"");
	}
}
