package com.seatunity.boardingpass.fragment;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.seatunity.boardingpass.AcountActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.TermAndConditionActivity;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.boardingpass.utilty.DialogViewer;
import com.seatunity.boardingpass.utilty.MyCustomSpannable;
import com.seatunity.model.ServerResponse;

/**
 * Fragment containing the UI & necessary API call to support a new user
 * registration process.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentSignUp extends Fragment implements CallBackApiCall {
	private final String TAG = this.getClass().getSimpleName();
	// private TextView tv_signup_message;
	private TextView tv_sign_message;
	private EditText et_email, et_password, et_confirm_password, et_first_name, et_last_name, et_livein, et_profession,
			et_seatting_pref, et_age;
	private Button bt_register;
	private String email = "", gender, password = "", confirmpassword = "", firstname = "", lastname = "", livein = "",
			profession = "", seating = "";
	private RadioGroup rdgrp_gender;
	private int SEATING_PREF = 0;
	private int AGE = 1;
	// private int LIVE_IN = 2;
	private MyCustomSpannable customSpannable;
	private RadioButton radio_male, radio_female, radio_not_say;
	// private ArrayList<String> agelist = new ArrayList<String>();
	private String[] NoCore_Array = new String[5];

	{
		NoCore_Array[0] = "1";
		NoCore_Array[1] = "2";
		NoCore_Array[2] = "3";
		NoCore_Array[3] = "4";
		NoCore_Array[4] = "5";
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// agelist.clear();
		// agelist.add("< 15");
		// for (int i = 15; i < 100;) {
		// String agecalculation = i + " - " + (i + 4);
		// agelist.add(agecalculation);
		// i = i + 5;
		// }
		// agelist.add("100 <");
		// for (int i = 0; i < agelist.size(); i++) {
		// Log.i("" + i, agelist.get(i));
		// }
	}

	/**
	 * Shows a confirmation dialog saying that the registration email is
	 * successfully sent
	 */
	public void showCustomDialog() {
		Log.d(TAG, "Sign up OK dialog");
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dilog);
		Button btnOK = (Button) dialog.findViewById(R.id.ok);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Sign up OK dialog : on-click");
				dialog.dismiss();
				if (dialog.isShowing())
					dialog.cancel();
			}
		});

		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
		dialog.show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.signup, container, false);
		Log.i(TAG, "onCreateView");
		rdgrp_gender = (RadioGroup) v.findViewById(R.id.rdgrp_gender);
		initview(v);
		return v;
	}

	/**
	 * Initializes all the views.
	 * 
	 * @param v
	 *            The {@link ViewGroup} to which the view is to be formed.
	 */
	public void initview(ViewGroup v) {
		bt_register = (Button) v.findViewById(R.id.bt_register);
		// tv_signup_message = (TextView)
		// v.findViewById(R.id.tv_signup_message);
		tv_sign_message = (TextView) v.findViewById(R.id.tv_sign_message);
		et_email = (EditText) v.findViewById(R.id.et_email);
		et_password = (EditText) v.findViewById(R.id.et_password);
		et_confirm_password = (EditText) v.findViewById(R.id.et_confirm_password);
		et_first_name = (EditText) v.findViewById(R.id.et_first_name);
		et_last_name = (EditText) v.findViewById(R.id.et_last_name);
		et_livein = (EditText) v.findViewById(R.id.et_livein);
		et_age = (EditText) v.findViewById(R.id.et_age);
		et_profession = (EditText) v.findViewById(R.id.et_profession);
		et_seatting_pref = (EditText) v.findViewById(R.id.et_seatting_pref);
		radio_male = (RadioButton) v.findViewById(R.id.radio_male);
		radio_female = (RadioButton) v.findViewById(R.id.radio_female);
		radio_not_say = (RadioButton) v.findViewById(R.id.radio_not_say);
		radio_female.setTextColor(Color.parseColor("#FFFFFF"));
		radio_male.setTextColor(Color.parseColor("#000000"));
		radio_not_say.setTextColor(Color.parseColor("#000000"));

		final String[] age_list = getActivity().getResources().getStringArray(R.array.age_range_list);

		et_seatting_pref.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					String[] seating_pref_list = getActivity().getResources().getStringArray(R.array.seating_pref);
					String title = getActivity().getResources().getString(R.string.txt_seatting_pref_cap);
					showDialogForSeatingPref(seating_pref_list, title, SEATING_PREF);
				}
			}
		});
		et_seatting_pref.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] seating_pref_list = getActivity().getResources().getStringArray(R.array.seating_pref);
				String title = getActivity().getResources().getString(R.string.txt_seatting_pref_cap);
				showDialogForSeatingPref(seating_pref_list, title, SEATING_PREF);
			}
		});
		et_age.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogForSeatingPref(age_list, getActivity().getResources().getString(R.string.txt_age), AGE);
			}
		});
		et_age.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showDialogForSeatingPref(age_list, getActivity().getResources().getString(R.string.txt_age), AGE);
				}
			}
		});
		String text1 = "By registering, you acknowledge that you have read "
				+ "and agreed to the SeatUnity Terms of Conditions";
		int i = text1.indexOf("Terms of Conditions");
		Log.e("size", i + "  " + text1.length());
		String text = "<font color=#000000>By registering, you acknowledge "
				+ "that you have read and agreed to the SeatUnit</font>"
				+ " <font color=#0099cc>Terms of Conditions</font>";
		// tv_sign_message.setText(Html.fromHtml(text));
		Log.e("size", i + "  " + text1.length());
		SpannableStringBuilder stringBuilder = new SpannableStringBuilder(Html.fromHtml(text));

		customSpannable = new MyCustomSpannable("http://www.google.co.in/") {

			@Override
			public void onClick(View widget) {
				Intent intent = new Intent(getActivity(), TermAndConditionActivity.class);
				startActivity(intent);

			}
		};
		stringBuilder.setSpan(customSpannable, i, text1.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		tv_sign_message.setText(stringBuilder, BufferType.SPANNABLE);
		tv_sign_message.setMovementMethod(LinkMovementMethod.getInstance());

		bt_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// email,password,confirmpassword,firstname,lastname,livein,age,profession,seating
				email = et_email.getText().toString();
				password = et_password.getText().toString();
				confirmpassword = et_confirm_password.getText().toString();
				firstname = et_first_name.getText().toString();
				lastname = et_last_name.getText().toString();
				livein = et_livein.getText().toString();
				// age = et_age.getText().toString();
				profession = et_profession.getText().toString();
				// seating=et_seatting_pref.getText().toString();
				et_email.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						et_email.setBackgroundResource(android.R.drawable.editbox_background_normal);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});

				et_password.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						et_password.setBackgroundResource(android.R.drawable.editbox_background_normal);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});

				if (Constants.isOnline(getActivity())) {
					if (!confirmpassword.equals(password)) {
						Toast.makeText(getActivity(),
								getActivity().getResources().getString(R.string.txt_password_mismatched),
								Toast.LENGTH_SHORT).show();
						et_password.setBackgroundResource(R.drawable.rounded_text_nofield);
						et_confirm_password.setBackgroundResource(R.drawable.rounded_text_nofield);

					} else if (!Constants.isValidEmail(email)) {
						Toast.makeText(getActivity(),
								getActivity().getResources().getString(R.string.txt_enter_valid_email),
								Toast.LENGTH_SHORT).show();
						et_email.setBackgroundResource(R.drawable.rounded_text_nofield);

					} else {
						callsignUp();
					}
				} else {
					Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_check_internet),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		// setcity();
		rdgrp_gender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_female:
					radio_female.setTextColor(Color.parseColor("#FFFFFF"));
					radio_male.setTextColor(Color.parseColor("#000000"));
					radio_not_say.setTextColor(Color.parseColor("#000000"));
					break;
				case R.id.radio_male:
					radio_male.setTextColor(Color.parseColor("#FFFFFF"));
					radio_female.setTextColor(Color.parseColor("#000000"));
					radio_not_say.setTextColor(Color.parseColor("#000000"));
					break;
				case R.id.radio_not_say:
					radio_not_say.setTextColor(Color.parseColor("#FFFFFF"));
					radio_male.setTextColor(Color.parseColor("#000000"));
					radio_female.setTextColor(Color.parseColor("#000000"));
					break;
				}

			}
		});

	}

	/**
	 * Called on sign-up button action to validate the input values & procede on
	 * with the sign-up.
	 */
	public void callsignUp() {
		if ((!email.equals("")) && (!password.equals("")) && (!password.equals(""))) {
			String signupdata = "";
			signupdata = getJsonObjet();
			AsyncaTaskApiCall sign_uplisenar = new AsyncaTaskApiCall(FragmentSignUp.this, signupdata, getActivity(),
					"reg", Constants.REQUEST_TYPE_POST);
			sign_uplisenar.execute();
		} else {
			if (email.equals("")) {
				et_email.setBackgroundResource(R.drawable.rounded_text_nofield);
			}
			if (password.equals("")) {
				et_password.setBackgroundResource(R.drawable.rounded_text_nofield);
			}
			if (confirmpassword.equals("")) {
				et_confirm_password.setBackgroundResource(R.drawable.rounded_text_nofield);
			}

			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_fillup_required_field),
					Toast.LENGTH_SHORT).show();
		}
		et_confirm_password.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				et_confirm_password.setBackgroundResource(android.R.drawable.editbox_background_normal);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	/**
	 * @return A JSON-formatted string to call the sign-up API
	 */
	@SuppressWarnings("unused")
	public String getJsonObjet() {
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("email", email);
			loginObj.put("password", password);
			String required = loginObj.toString();
			loginObj.put("firstname", firstname);
			loginObj.put("lastname", lastname);
			loginObj.put("seating_pref", "male");
			loginObj.put("language", Locale.getDefault().getLanguage());
			loginObj.put("live_in", livein);
			int selected = rdgrp_gender.getCheckedRadioButtonId();
			RadioButton rb_gender;
			if (selected == R.id.radio_female) {
				gender = getActivity().getResources().getString(R.string.txt_gender_female);
			} else if (selected == R.id.radio_male) {
				gender = getActivity().getResources().getString(R.string.txt_gender_male);
			} else if (selected == R.id.radio_not_say) {
				gender = getActivity().getResources().getString(R.string.txt_gender_rather_not_say);
			}
			loginObj.put("gender", gender);
			String age = et_age.getText().toString();
			if ((!age.equals("")) && (!age.equals(getActivity().getResources().getString(R.string.txt_age)))) {
				loginObj.put("age", age);
			} else {
				loginObj.put("age", "");
			}
			loginObj.put("profession", profession);
			loginObj.put("seating_pref", seating);
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Shows a custom dialog providing the user a list of items to choose.
	 * 
	 * @param items
	 * @param title
	 * @param type
	 */
	@SuppressLint("InflateParams")
	public void showDialogForSeatingPref(final CharSequence[] items, String title, final int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tv_title = (TextView) customTitleView.findViewById(R.id.tv_title);
		tv_title.setText(title);
		builder.setCustomTitle(customTitleView);
		builder.setPositiveButton(getActivity().getResources().getString(R.string.txt_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// G
		String prevText = "";
		if (type == AGE)
			prevText = et_age.getText().toString();
		else
			prevText = et_seatting_pref.getText().toString();
		int checkedItem = -1;
		if (!prevText.equals(null) && prevText.length() > 0)
			for (int i = 0; i < items.length; i++) {
				Log.d("List of :" + ((type == AGE) ? "AGE" : "Seating"), "prev text: " + prevText + ", item: "
						+ items[i]);
				if (prevText.equalsIgnoreCase((String) items[i])) {
					checkedItem = i;
					break;
				}
			}
		builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (type == SEATING_PREF) {
					seating = items[which].toString();
					et_seatting_pref.setText(seating);
				} else if (type == AGE) {
					et_age.setText(items[which].toString());
				}
				dialog.cancel();
			}
		});
		builder.show();

	}

	@Override
	public void responseOk(JSONObject job) {
		((AcountActivity) getActivity()).indicator.setViewPager(((AcountActivity) getActivity()).pager, 0);
		showCustomDialog();
	}

	// {"error":{"message":"email in use","code":"x03"},"success":"false"}

	@Override
	public void responseFailure(JSONObject job) {
		Log.e("SignUp", "responseFailure:: \n" + job.toString());

		try {
			if (job.getJSONObject("error").getString("code").equals("x03"))
				DialogViewer.alertSimple(getActivity(), "The provided email address is already in use!");
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				DialogViewer.alertSimple(getActivity(), "Error: " + job.getJSONObject("error").getString("message"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
	}

	@Override
	public void LoginFailed(JSONObject job) {
		// Toast.makeText(getActivity(),
		// getActivity().getResources().getString(R.string.txt_signup_failed1),
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void responseFailure(ServerResponse response) {
		if (response.getStatus() != 200) {
			BoardingPassApplication.alert(getActivity(), "Internet connectivity is lost! Please retry the operation.");
		}
	}
}
