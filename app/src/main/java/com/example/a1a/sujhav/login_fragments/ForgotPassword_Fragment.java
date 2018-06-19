package com.example.a1a.sujhav.login_fragments;

/**
 * Created by Sandeep Doodigani on 30-11-2017.
 */
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1a.sujhav.Avatar;
import com.example.a1a.sujhav.R;
import com.example.a1a.sujhav.adapter.CustomToast;
import com.example.a1a.sujhav.MainActivity;
import com.example.a1a.sujhav.adapter.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword_Fragment extends Fragment implements
        OnClickListener {
	private static View view;

	private static EditText emailId;
	private static TextView submit, back;
	String data;
	public ForgotPassword_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.forgotpassword_layout, container,
				false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize the views
	private void initViews() {
		emailId = (EditText) view.findViewById(R.id.registered_emailid);
		submit = (TextView) view.findViewById(R.id.forgot_button);
		back = (TextView) view.findViewById(R.id.backToLoginBtn);

		// Setting text selector over textviews
		@SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			back.setTextColor(csl);
			submit.setTextColor(csl);

		} catch (Exception e) {
		}

	}

	// Set Listeners over buttons
	private void setListeners() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backToLoginBtn:

			// Replace Login Fragment on Back Presses
			new MainActivity().replaceLoginFragment();
			break;

		case R.id.forgot_button:

			// Call Submit button task
			try {
				submitButtonTask();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;

		}

	}

	private void submitButtonTask() throws UnsupportedEncodingException {
		String getEmailId = emailId.getText().toString();

		// Pattern for email id validation
		Pattern p = Pattern.compile(Utils.regEx);

		// Match the pattern
		Matcher m = p.matcher(getEmailId);

		// First check if email id is not null else show error toast
		if (getEmailId.equals("") || getEmailId.length() == 0)

			new CustomToast().Show_Toast(getActivity(), view,
					"Please enter your Email Id.");

		// Check if email id is valid or not
		else if (!m.find())
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");

		// Else submit email id and fetch passwod or do your stuff
		else
		{
			data = URLEncoder.encode("Email", "UTF-8")
					+ "=" + URLEncoder.encode(getEmailId, "UTF-8");

			if(!TextUtils.isEmpty(data)){

				login l =new login();
				l.execute();
			}

		}

	}
	class login extends AsyncTask<Void, Void, String> {
		ProgressDialog pDialog;

		protected void onPreExecute() {
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Sending Email");
			pDialog.setCancelable(true);
			pDialog.setProgress(0);
			pDialog.setMax(100);
			pDialog.show();
		}

		protected String doInBackground(Void... urls) {
			try {
				URL url = new URL("http://www.sujhav.in/API/farmer_forgotpassword");

				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					StringBuilder stringBuilder = new StringBuilder();
					String line;
					System.out.println(" bufferedreader response :" + bufferedReader);

					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line);


					}
					bufferedReader.close();
					Log.e("res", stringBuilder.toString());
					return stringBuilder.toString();
				} finally {

				}
			} catch (Exception e) {
				Log.e("ERROR", e.getMessage(), e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			pDialog.dismiss();
			Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
			Log.i("AbhiS",s);
			JSONObject jsonObject= null;
			try {
				jsonObject = new JSONObject(s);
				String status=jsonObject.getString("err");
				String msg=jsonObject.getString("msg");


					new CustomToast().Show_Toast(getActivity(), view,
							msg);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}