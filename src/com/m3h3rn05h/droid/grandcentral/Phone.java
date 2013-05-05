package com.m3h3rn05h.droid.grandcentral;

public class Phone {
	int id;
	String phoneNumber;
	int type;
	@Override
	public String toString() {
		return phoneNumber + " [" + getTypeString() + "]";
	}
	public Phone(int id, String phoneNumber, int type) {
		super();
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.type = type;
	}
	String getTypeString(){
		switch (type){
			case 1: return "Home";
			case 2: return "Mobile";
			case 3: return "Work";
			case 7: return "Gizmo";
			case 9: return "Browser";
			default:return "";
		}
			
	}
	public int getId() {
		return id;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public int getType() {
		return type;
	}
}
