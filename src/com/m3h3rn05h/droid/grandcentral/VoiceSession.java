package com.m3h3rn05h.droid.grandcentral;

import java.util.ArrayList;

public class VoiceSession {
	private String authToken;
	private String rnrSe;
	private static VoiceSession sessionSingleton = new VoiceSession();

	private VoiceSession() {
	}

	private ArrayList<Phone> phones = new ArrayList<Phone>(1);

	void addPhone(Phone phone) {
		this.phones.add(phone);
	}

	public ArrayList<Phone> getPhones() {
		return phones;
	}

	void setPhones(ArrayList<Phone> phones) {
		this.phones = phones;
	}

	public String getAuthToken() {
		return authToken;
	}

	void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getRnrSe() {
		return rnrSe;
	}

	void setRnrSe(String rnrSe) {
		this.rnrSe = rnrSe;
	}

	public static VoiceSession getInstance() {
		return sessionSingleton;
	}

}
