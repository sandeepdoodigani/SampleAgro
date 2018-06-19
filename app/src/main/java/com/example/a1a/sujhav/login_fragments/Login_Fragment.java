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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1a.sujhav.Avatar;
import com.example.a1a.sujhav.R;
import com.example.a1a.sujhav.adapter.CustomToast;
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

public class Login_Fragment extends Fragment implements OnClickListener {
	private static View view;

	private static EditText emailid, password;
	private static Button loginButton;
	private static TextView forgotPassword, signUp;
	private static CheckBox show_hide_password;
	private static LinearLayout loginLayout;
	private static Animation shakeAnimation;
	private static FragmentManager fragmentManager;
	String farmer;
	String data;
	public Login_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.login_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();

		emailid = (EditText) view.findViewById(R.id.login_emailid);
		password = (EditText) view.findViewById(R.id.login_password);
		loginButton = (Button) view.findViewById(R.id.loginBtn);
		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
		signUp = (TextView) view.findViewById(R.id.createAccount);
		show_hide_password = (CheckBox) view
				.findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

		// Load ShakeAnimation
		shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);

		// Setting text selector over textviews
		@SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			forgotPassword.setTextColor(csl);
			show_hide_password.setTextColor(csl);
			signUp.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		loginButton.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);
		signUp.setOnClickListener(this);

		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
												 boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {

							show_hide_password.setText(R.string.hide_pwd);// change
							// checkbox
							// text

							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change
							// checkbox
							// text

							password.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod
									.getInstance());// hide password

						}

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loginBtn:
				try {
					checkValidation();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				break;

			case R.id.forgot_password:

				// Replace forgot password fragment with animation
				fragmentManager
						.beginTransaction()
						.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
						.replace(R.id.frameContainer,
								new ForgotPassword_Fragment(),
								Utils.ForgotPassword_Fragment).commit();
				break;
			case R.id.createAccount:

				// Replace signup frgament with animation
				fragmentManager
						.beginTransaction()
						.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
						.replace(R.id.frameContainer, new SignUp_Fragment(),
								Utils.SignUp_Fragment).commit();
				break;
		}

	}

	// Check Validation before login
	private void checkValidation() throws UnsupportedEncodingException {
		// Get email id and password
		String getEmailId = emailid.getText().toString();
		String getPassword = password.getText().toString();

		// Check patter for email id
		Pattern p = Pattern.compile(Utils.regEx);

		Matcher m = p.matcher(getEmailId);

		// Check for both field is empty or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0) {
			loginLayout.startAnimation(shakeAnimation);
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter both credentials.");

		}
		// Check if email id is valid or not
		else if (!m.find())
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
			// Else do login and do your stuff
		else
		{
			data = URLEncoder.encode("Email", "UTF-8")
					+ "=" + URLEncoder.encode(getEmailId, "UTF-8");
			data += "&" + URLEncoder.encode("Password", "UTF-8") + "="
					+ URLEncoder.encode(getPassword, "UTF-8");
			if(!TextUtils.isEmpty(data)){

				login l =new login();
				l.execute();
			}

		}

	}
	class login extends AsyncTask<Void, Void, String> {
		ProgressDialog pDialog;
		String status;
		String msg;
		String accountId;
		protected void onPreExecute() {
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Logging In....");
			pDialog.setCancelable(true);
			pDialog.setProgress(0);
			pDialog.setMax(100);
			pDialog.show();
		}

		protected String doInBackground(Void... urls) {
			try {
				URL url = new URL("http://www.sujhav.in/API/farmer_signin");

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

		@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			pDialog.dismiss();
			Log.e("Login Data",s);
			Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
			Log.i("AbhiS",s);
			JSONObject jsonObject= null;
			try {
				jsonObject = new JSONObject(s);
				status=jsonObject.getString("err");
				msg=jsonObject.getString("msg");
				accountId=jsonObject.getString("AccountId");
				farmer=jsonObject.getString("FarmerName");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(status.contentEquals("1")){
				new CustomToast().Show_Toast(getActivity(), view,
						msg);
				Intent in =new Intent(getActivity(),Avatar.class);
				in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				SharedPreferences sharedpreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.putString("FarmerName",farmer);
				editor.putString("AccountId", accountId);
				editor.apply();
				editor.commit();
				startActivity(in);
				getActivity().finish();
			}
			else{
				new CustomToast().Show_Toast(getActivity(), view,
						msg);
				loginLayout.startAnimation(shakeAnimation);
			}

		}
	}

}
