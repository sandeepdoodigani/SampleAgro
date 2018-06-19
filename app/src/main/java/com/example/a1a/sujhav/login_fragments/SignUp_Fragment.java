package com.example.a1a.sujhav.login_fragments;

/**
 * Created by Sandeep Doodigani on 30-11-2017.
 */

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public class SignUp_Fragment extends Fragment implements OnClickListener {
	private static View view;
	private static EditText fullName, emailId, mobileNumber,aadhaarNo,landSurvey,pincode,village,mandal,state,district,
			password, confirmPassword;
	private static TextView login;
	private static Button signUpButton;
	private static CheckBox terms_conditions;
	ProgressDialog pDialog;
	String data;

	public SignUp_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.signup_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize all views
	private void initViews() {
		fullName = (EditText) view.findViewById(R.id.fullName);
		emailId = (EditText) view.findViewById(R.id.userEmailId);
		mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
		aadhaarNo=(EditText)view.findViewById(R.id.aadhaar);
		landSurvey=(EditText)view.findViewById(R.id.landSurveyNo);
		pincode=(EditText)view.findViewById(R.id.pinCode);
		village=(EditText)view.findViewById(R.id.village);
		mandal=(EditText)view.findViewById(R.id.mandal);
		state = (EditText) view.findViewById(R.id.state);
		district=(EditText)view.findViewById(R.id.district);
		password = (EditText) view.findViewById(R.id.password);
		confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);

		// Setting text selector over textviews
		@SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			login.setTextColor(csl);
			terms_conditions.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signUpBtn:

			// Call checkValidation method
			try {

				checkValidation();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case R.id.already_user:

			// Replace login fragment
			new MainActivity().replaceLoginFragment();
			break;
		}

	}

	// Check Validation Method
	private void checkValidation() throws UnsupportedEncodingException, JSONException {

		// Get all edittext texts
		String getFullName = fullName.getText().toString();
		String getEmailId = emailId.getText().toString();
		String getMobileNumber = mobileNumber.getText().toString();
		String getAdhaar = aadhaarNo.getText().toString();
		String getLandSurveyNo=landSurvey.getText().toString();
		String getPincode=pincode.getText().toString();
		String getVillage = village.getText().toString();
		String getMandal = mandal.getText().toString();
		String getState = state.getText().toString();
		String getDistrict = district.getText().toString();
		String getPassword = password.getText().toString();
		String getConfirmPassword = confirmPassword.getText().toString();

		// Pattern match for email id
		Pattern p = Pattern.compile(Utils.regEx);
		Matcher m = p.matcher(getEmailId);

		// Check if all strings are null or not
		if (getFullName.equals("") || getFullName.length() == 0
				|| getEmailId.equals("") || getEmailId.length() == 0
				|| getMobileNumber.equals("") || getMobileNumber.length() == 0
				|| getAdhaar.equals("") || getAdhaar.length() == 0
				|| getVillage.equals("") || getAdhaar.length() == 0
				|| getMandal.equals("") || getMandal.length() == 0
				|| getState.equals("") || getState.length() == 0
				|| getDistrict.equals("") || getDistrict.length() == 0
				|| getPincode.equals("") || getPincode.length() == 0
				||getPassword.equals("") || getPassword.length() == 0
				|| getConfirmPassword.equals("")
				|| getConfirmPassword.length() == 0)

			new CustomToast().Show_Toast(getActivity(), view,
					"All fields are required.");

		// Check if email id valid or not
		else if (!m.find())
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");

		// Check if both password should be equal
		else if (!getConfirmPassword.equals(getPassword))
			new CustomToast().Show_Toast(getActivity(), view,
					"Both password doesn't match.");

		// Make sure user should check Terms and Conditions checkbox
		else if (!terms_conditions.isChecked())
			new CustomToast().Show_Toast(getActivity(), view,
					"Please select Terms and Conditions.");

		// Else do signup or do your stuff
		else{
			data = URLEncoder.encode("Fullname", "UTF-8")
					+ "=" + URLEncoder.encode(getFullName, "UTF-8");
			data += "&" + URLEncoder.encode("AdharNo", "UTF-8") + "="
					+ URLEncoder.encode(getAdhaar, "UTF-8");
			data += "&" + URLEncoder.encode("Village", "UTF-8") + "="
					+ URLEncoder.encode(getVillage, "UTF-8");
			data += "&" + URLEncoder.encode("Mandal", "UTF-8") + "="
					+ URLEncoder.encode(getMandal, "UTF-8");
			data += "&" + URLEncoder.encode("Distict", "UTF-8") + "="
					+ URLEncoder.encode(getDistrict, "UTF-8");
			data += "&" + URLEncoder.encode("State", "UTF-8") + "="
					+ URLEncoder.encode(getState, "UTF-8");
			data += "&" + URLEncoder.encode("LandsurveyNo", "UTF-8") + "="
					+ URLEncoder.encode(getLandSurveyNo, "UTF-8");
			data += "&" + URLEncoder.encode("Mobile", "UTF-8") + "="
					+ URLEncoder.encode(getMobileNumber, "UTF-8");
			data += "&" + URLEncoder.encode("Email", "UTF-8") + "="
					+ URLEncoder.encode(getEmailId, "UTF-8");
			data += "&" + URLEncoder.encode("Password", "UTF-8") + "="
					+ URLEncoder.encode(getPassword, "UTF-8");

			if(data!=null){
				sendData sendData=new sendData();
				sendData.execute();
			}
		}


	}
	class sendData extends AsyncTask<Void, Void, String> {


		protected void onPreExecute() {
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Logging In...");
			pDialog.setCancelable(true);
			pDialog.setProgress(0);
			pDialog.setMax(100);
			pDialog.show();
		}

		protected String doInBackground(Void... urls) {
			try {
				URL url = new URL("http://www.sujhav.in/API/farmer_signup");

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
			Log.i("Abhishek ",s);
			JSONObject jsonObject= null;
			try {
				jsonObject = new JSONObject(s);
			String status=jsonObject.getString("err");
			String msg=jsonObject.getString("msg");
			if(status.contentEquals("1")){

				new CustomToast().Show_Toast(getActivity(), view,
						msg);
				android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(R.id.frameContainer, new Login_Fragment(),
								Utils.Login_Fragment).commit();

			}
			else {
				new CustomToast().Show_Toast(getActivity(), view,
						msg);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
