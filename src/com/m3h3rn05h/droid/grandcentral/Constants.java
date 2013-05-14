package com.m3h3rn05h.droid.grandcentral;

public class Constants {
	final static String account_type_google = "GOOGLE";
	final static String account_type_hosted = "HOSTED";
	final static String account_type_hosted_or_google = "HOSTED_OR_GOOGLE";
	final static String service = "grandcentral";
	final static String source = "com.m3h3rn05h";
	final static String enc = "UTF-8";
	final static String loginURLString = "https://www.google.com/accounts/ClientLogin";
	final static String generalURLString = "https://www.google.com/voice/b/0";
	final static String callConnectURLString = "https://www.google.com/voice/call/connect/";
	final static String cancelCallConnectURLString ="https://www.google.com/voice/b/0/call/cancel/";
	final static String phonesInfoURLString = "https://www.google.com/voice/b/0/settings/tab/phones";
	final static String groupsInfoURLString = "https://www.google.com/voice/b/0/settings/tab/groups";
	final static String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.A.B.C Safari/525.13";
	final static int MAX_REDIRECTS = 4;
	static enum apiActions{login, startSession, initiateCallBack, cancelCallBack };
}
