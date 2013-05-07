package com.m3h3rn05h.droid.grandcentral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

import com.m3h3rn05h.droid.googlevoiceunhooked.Dialpad;

public class Communicator extends AsyncTask<String, String, Integer> {

	private WeakReference<TextView> textViewRef;
	private String authToken;
	private String rnrSe;
	private int redirectCounter;

	// private static Communicator communicatorSingleton = new Communicator();

	private Communicator() {
		redirectCounter = 0;
		authToken = VoiceSession.getInstance().getAuthToken();
		rnrSe = VoiceSession.getInstance().getRnrSe();
	}

	public WeakReference<TextView> getTextViewRef() {
		return textViewRef;
	}

	public void setTextViewRef(WeakReference<TextView> textViewRef) {
		this.textViewRef = textViewRef;
	}

	public void setTextViewRef(TextView textView) {
		this.textViewRef = new WeakReference<TextView>(textView);
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getRnrSe() {
		return rnrSe;
	}

	public static Communicator getInstance() {

		return new Communicator();
	}

	@Override
	protected Integer doInBackground(String... params) {
		if (params.length > 0) {
			int action = Integer.parseInt(params[0]);
			switch (action) {
			case 0:
				login(params[1], params[2]);
			case 1:
				fetchRnrSee();
			case 2:
				getPhones();
				break;
			case 3:
				call(params[1], params[2], params[3]);
				break;
			
			case 4:
				cancelCall(params[1], params[2], params[3]);
				break;
			}	
			return action;
		}
		return null;
	}

	private void getPhones() {
		try {
			String lJson = removeUninterestingParts(
					get(Constants.groupsInfoURLString), "<json><![CDATA[",
					"]]></json>", false);
			JSONObject json = new JSONObject(lJson);
			JSONObject phones = json.getJSONObject("phones");
			JSONArray phoneNames = phones.names();

			VoiceSession s = VoiceSession.getInstance();
			s.setPhones(new ArrayList<Phone>(phones.length()));
			
			for (int i = 0; i < phones.length(); i++) {
				JSONObject phone = phones
						.getJSONObject(phoneNames.getString(i));
				if (phone.getBoolean("active")) {
					s.addPhone(new Phone(phone.getInt("id"), phone
							.getString("phoneNumber"), phone.getInt("type")));
				}

			}
			json = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void fetchRnrSee() {
		try {
			String rawHtml = get(Constants.generalURLString);
			if (rawHtml != null) {
				if (rawHtml.contains("'_rnr_se': '")) {
					String p1 = rawHtml.split("'_rnr_se': '", 2)[1];
					this.rnrSe = p1.split("',", 2)[0];
					VoiceSession.getInstance().setRnrSe(rnrSe);
					publishProgress("Successfully Received rnr_se.");
					p1 = null;
				} else {
					publishProgress("Answer did not contain rnr_se! " + rawHtml);
					throw new IOException("Answer did not contain rnr_se! "
							+ rawHtml);
				}
			} else {
				publishProgress("setRNRSEE(): Answer was null!");
				throw new IOException("setRNRSEE(): Answer was null!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String login(String user, String pass) {
		String data;
		authToken = null;
		try {
			data = URLEncoder.encode("accountType", Constants.enc) + "="
					+ URLEncoder.encode(Constants.account_type, Constants.enc);
			data += "&" + URLEncoder.encode("Email", Constants.enc) + "="
					+ URLEncoder.encode(user, Constants.enc);
			data += "&" + URLEncoder.encode("Passwd", Constants.enc) + "="
					+ URLEncoder.encode(pass, Constants.enc);
			data += "&" + URLEncoder.encode("service", Constants.enc) + "="
					+ URLEncoder.encode(Constants.service, Constants.enc);
			data += "&" + URLEncoder.encode("source", Constants.enc) + "="
					+ URLEncoder.encode(Constants.source, Constants.enc);
			publishProgress("Logging in:");
			publishProgress(Constants.loginURLString);
			URL url = new URL(Constants.loginURLString);
			HttpsURLConnection conn;
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestProperty("User-agent", Constants.USER_AGENT);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();

			conn.connect();
			int responseCode = conn.getResponseCode();
			// System.out.println(loginURLString + " - " +
			// conn.getResponseMessage());
			InputStream is;
			// wrpublishProgressHTTP:"+responseCode);
			if (responseCode == 200) {
				is = conn.getInputStream();
			} else {
				is = conn.getErrorStream();
			}
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader rd = new BufferedReader(isr);
			String line;

			// String AuthToken = null;
			while ((line = rd.readLine()) != null) {

				if (line.contains("Auth=")) {
					authToken = line.split("=", 2)[1].trim();
					publishProgress("Logged in to Google - Auth token received");
				}
			}
			wr.close();
			rd.close();

			// if (PRINT_TO_CONSOLE){
			// System.out.println(completelineDebug);
			// }
			// String loginPageHtml = get(Constants.generalURLString);

			VoiceSession session = VoiceSession.getInstance();
			session.setAuthToken(authToken);
			// session.setRnrSe(rnrSe);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(authToken);
		return authToken;
	}

	@Override
	protected void onProgressUpdate(String... args) {
		writeStatusLine(args[0]);
	}

	@Override
	protected void onPostExecute(Integer action) {
		super.onPostExecute(action);
		if (!isCancelled()) {
			switch (action) {
			case 0:
			case 1:
				writeStatusLine("Authentication Successful; we're in!");
				TextView textView = textViewRef.get();
				if (textView != null) {
					Intent intent = new Intent(textView.getContext(),
							Dialpad.class);// textView.getContext()
					textView.getContext().startActivity(intent);
				}
				break;
			case 3:
				writeStatusLine("Calling you, please wait!");
				break;
			case 4:
				writeStatusLine("Abort requested!");
				break;
			}
			
		}
	}

	private void writeStatusLine(String line) {
		if (textViewRef != null) {
			TextView textView = textViewRef.get();
			if (textView != null) {
				textView.setText('\n' + line);
			}
		}
	}

	String get(String urlString) throws IOException {
		URL url = new URL(urlString);
		// + "?auth=" + URLEncoder.encode(authToken, enc));

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestProperty("Authorization", "GoogleLogin auth="
				+ authToken);
		conn.setRequestProperty("User-agent", Constants.USER_AGENT);
		conn.setInstanceFollowRedirects(false); // will follow redirects of same
												// protocol http to http, but
												// does not follow from http to
												// https for example if set to
												// true

		// Get the response
		conn.connect();
		int responseCode = conn.getResponseCode();
		System.out.println(urlString + " - " + conn.getResponseMessage());
		InputStream is;
		if (responseCode == 200) {
			is = conn.getInputStream();
		} else if (responseCode == HttpsURLConnection.HTTP_MOVED_PERM
				|| responseCode == HttpsURLConnection.HTTP_MOVED_TEMP
				|| responseCode == HttpsURLConnection.HTTP_SEE_OTHER
				|| responseCode == 307) {
			redirectCounter++;
			if (redirectCounter > Constants.MAX_REDIRECTS) {
				redirectCounter = 0;
				throw new IOException(urlString + " : "
						+ conn.getResponseMessage() + "(" + responseCode
						+ ") : Too manny redirects. exiting.");
			}
			String location = conn.getHeaderField("Location");
			if (location != null && !location.equals("")) {
				System.out.println(urlString + " - " + responseCode
						+ " - new URL: " + location);
				return get(location);
			} else {
				throw new IOException(urlString + " : "
						+ conn.getResponseMessage() + "(" + responseCode
						+ ") : Received moved answer but no Location. exiting.");
			}
		} else {
			is = conn.getErrorStream();
		}
		redirectCounter = 0;

		if (is == null) {
			throw new IOException(urlString + " : " + conn.getResponseMessage()
					+ "(" + responseCode
					+ ") : InputStream was null : exiting.");
		}

		String result = "";
		try {
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line + "\n\r");
			}
			rd.close();
			result = sb.toString();
		} catch (Exception e) {
			throw new IOException(urlString + " - " + conn.getResponseMessage()
					+ "(" + responseCode + ") - " + e.getLocalizedMessage());
		}
		return result;
	}

	public String call(String destinationNumber, String originNumber,
			String phoneType) {
		String out = "";
		StringBuffer calldata = new StringBuffer();

		// POST /voice/call/connect/
		// outgoingNumber=[number to call]
		// &forwardingNumber=[forwarding number]
		// &subscriberNumber=undefined
		// &phoneType=[phone type from google]
		// &remember=0
		// &_rnr_se=[pull from page]
		try {
			// String rnrSEE = VoiceSession.getInstance().getRnrSe();
			calldata.append("outgoingNumber=");
			calldata.append(URLEncoder.encode(destinationNumber, Constants.enc));
			calldata.append("&forwardingNumber=");
			calldata.append(URLEncoder.encode(originNumber, Constants.enc));
			calldata.append("&subscriberNumber=undefined");
			calldata.append("&phoneType=");
			calldata.append(URLEncoder.encode(phoneType, Constants.enc));
			calldata.append("&remember=0");
			calldata.append("&_rnr_se=");
			calldata.append(URLEncoder.encode(rnrSe, Constants.enc));
			publishProgress("Connecting");
			publishProgress(Constants.callConnectURLString);
			URL callURL = new URL(Constants.callConnectURLString);

			HttpsURLConnection callconn = (HttpsURLConnection)callURL.openConnection();
			callconn.setRequestProperty("Authorization", "GoogleLogin auth="
					+ authToken);
			callconn.setRequestProperty("User-agent", Constants.USER_AGENT);

			callconn.setDoOutput(true);
			OutputStreamWriter callwr = new OutputStreamWriter(
					callconn.getOutputStream());

			callwr.write(calldata.toString());
			callwr.flush();

			BufferedReader callrd = new BufferedReader(new InputStreamReader(
					callconn.getInputStream()));

			String line;
			while ((line = callrd.readLine()) != null) {
				out += line + "\n\r";

			}

			callwr.close();
			callrd.close();

			if (out.equals("")) {
				throw new IOException("No Response Data Received.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;

	}

	public static final String removeUninterestingParts(String text,
			String startBorder, String endBorder, boolean includeBorders) {
		String ret = "";
		try {
			if (text != null && startBorder != null && endBorder != null
					&& (text.indexOf(startBorder) != -1)
					&& (text.indexOf(endBorder) != -1)) {

				if (includeBorders) {
					text = text.substring(text.indexOf(startBorder));
					if (text != null) {
						ret = text.substring(0, text.indexOf(endBorder)
								+ endBorder.length());
					} else {
						ret = null;
					}
				} else {
					text = text.substring(text.indexOf(startBorder)
							+ startBorder.length());
					if (text != null) {
						ret = text.substring(0, text.indexOf(endBorder));
					} else {
						ret = null;
					}
				}

			} else {
				ret = null;
			}
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			System.out.println("Begin:" + startBorder);
			System.out.println("End:" + endBorder);
			System.out.println("Text:" + text);
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	public String cancelCall(String destinationNumber, String originNumber,
			String phoneType) {
		String out = "";
		String calldata = "";
		try{
		calldata += URLEncoder.encode("outgoingNumber", Constants.enc) + "="
				+ URLEncoder.encode("undefined", Constants.enc);
		calldata += "&" + URLEncoder.encode("forwardingNumber", Constants.enc) + "="
				+ URLEncoder.encode("undefined", Constants.enc);

		calldata += "&" + URLEncoder.encode("cancelType", Constants.enc) + "="
				+ URLEncoder.encode("C2C", Constants.enc);
		calldata += "&" + URLEncoder.encode("_rnr_se", Constants.enc) + "="
				+ URLEncoder.encode(rnrSe, Constants.enc);
		// POST /voice/call/connect/ outgoingNumber=[number to
		// call]&forwardingNumber=[forwarding
		// number]&subscriberNumber=undefined&remember=0&_rnr_se=[pull from
		// page]
		URL callURL = new URL(Constants.cancelCallConnectURLString);

		HttpsURLConnection callconn =(HttpsURLConnection) callURL.openConnection();
		callconn.setRequestProperty( "Authorization",
                "GoogleLogin auth="+authToken );
		callconn
				.setRequestProperty(
						"User-agent",
						Constants.USER_AGENT);

		callconn.setDoOutput(true);
		OutputStreamWriter callwr = new OutputStreamWriter(callconn
				.getOutputStream());
		callwr.write(calldata);
		callwr.flush();

		BufferedReader callrd = new BufferedReader(new InputStreamReader(
				callconn.getInputStream()));

		String line;
		while ((line = callrd.readLine()) != null) {
			out += line + "\n\r";

		}

		callwr.close();
		callrd.close();

		if (out.equals("")) {
			throw new IOException("No Response Data Received.");
		}
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return out;

	}

}
