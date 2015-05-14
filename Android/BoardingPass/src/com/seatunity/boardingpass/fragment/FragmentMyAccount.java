package com.seatunity.boardingpass.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.boardingpass.EditAccountActivity;
import com.seatunity.boardingpass.EditUserNameActivity;
import com.seatunity.boardingpass.PasswordChangeActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForSettings;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.Base64;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.ImageScale;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;

/**
 * This fragment shows the profile-data of the user.
 * 
 * @author Sumon
 * 
 */
@SuppressLint({ "NewApi", "InflateParams" })
@SuppressWarnings("unused")
public class FragmentMyAccount extends Fragment implements CallBackApiCall {
	private ImageView img_edit, img_prof_pic, img_status;
	private ArrayList<String> setting_criteria;
	private BoardingPassApplication appInstance;
	private AdapterForSettings adapter;
	private ListView lv_setting;
	private Spinner spn_country;
	public AccountListFragment parent;
	private Activity activity;
	private TextView tv_uname, tv_email, tv_stataus, tv_statau;
	private UserCred userCred;
	private int ACTION_REQUEST_CAMERA = 31;
	private int ACTION_REQUEST_GALLERY = 21;
	private int ACTION_REQUEST_GALLERY_KITKAT = 22;
	private String drectory;
	private String photofromcamera;
	private Context context;
	private String contentbodyremeber = "";
	private ViewGroup v;
	private Bitmap photo;
	private int callfrom = 0;
	private JSONObject loginObj;
	private Bundle savedState = null;

	private final String TAG = this.getClass().getSimpleName();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		v = (ViewGroup) inflater.inflate(R.layout.fragment_my_account, container, false);
		Log.i(TAG, "onCreateView");
		tv_uname = (TextView) v.findViewById(R.id.tv_uname);
		tv_email = (TextView) v.findViewById(R.id.tv_email);
		tv_stataus = (TextView) v.findViewById(R.id.tv_stataus);
		img_prof_pic = (ImageView) v.findViewById(R.id.img_prof_pic);
		lv_setting = (ListView) v.findViewById(R.id.lv_setting);
		img_status = (ImageView) v.findViewById(R.id.img_status);
		tv_stataus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowStatus();
			}
		});
		img_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowStatus();
			}
		});
		img_edit = (ImageView) v.findViewById(R.id.img_edit);
		spn_country = (Spinner) v.findViewById(R.id.spn_country);

		img_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(context, EditUserNameActivity.class);
				Intent intent = new Intent(context, EditAccountActivity.class);
				startActivity(intent);
			}
		});
		img_prof_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Constants.isOnline(activity)) {
					String[] photochooser = context.getResources().getStringArray(R.array.upload_photo_from);
					showDialogTochosePhoto(photochooser, context.getResources().getString(R.string.txt_select_photo));
				} else {
					Toast.makeText(getActivity(),
							activity.getResources().getString(R.string.txt_please_check_internet), Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		

		

		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			context = getActivity();
			activity = getActivity();
		}
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		userCred = appInstance.getUserCred();
		setting_criteria = new ArrayList<String>();
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_live_in));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_age));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_gender));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_prof));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_seatting_pref));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_som_about_you));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_change_pass));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_sign_out));
		
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		userCred = appInstance.getUserCred();
		setListView();

		if ( (appInstance.getUserCred().getImage_url() == null) || (appInstance.getUserCred().getImage_url().equals(""))) {

		} else {
			ImageLoader.getInstance().displayImage(appInstance.getUserCred().getImage_url(), img_prof_pic);
		}
		Constants.IMG_PROF_PIC = img_prof_pic;
		setView();
	}

	/**
	 * Initiates the text-views with the user-data.
	 */
	public void setView() {
		tv_uname.setText(appInstance.getUserCred().getFirstname());
		tv_email.setText(appInstance.getUserCred().getEmail());
		tv_stataus.setText(appInstance.getUserCred().getStatus());
	}

	/**
	 * Forms the JSON-content & calls the API to update the profile image of the
	 * user.
	 * 
	 * @param bitmap
	 */
	public void uploadProfileImage(Bitmap bitmap) {
		try {
			loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("password", appInstance.getUserCred().getPassword());
			loginObj.put("language", appInstance.getUserCred().getLanguage());
			loginObj.put("firstname", appInstance.getUserCred().getFirstname());
			loginObj.put("lastname", appInstance.getUserCred().getLastname());
			loginObj.put("gender", appInstance.getUserCred().getGender());
			loginObj.put("live_in", appInstance.getUserCred().getLive_in());
			loginObj.put("age", appInstance.getUserCred().getAge());
			loginObj.put("profession", appInstance.getUserCred().getProfession());
			loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
			loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
			loginObj.put("status", appInstance.getUserCred().getStatus());
			loginObj.put("image_name", appInstance.getUserCred().getLastname() + System.currentTimeMillis() + ".png");
			loginObj.put("image_type", "image/png");
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bao);
			byte[] ba = bao.toByteArray();
			String base64Str = Base64.encodeBytes(ba);
			loginObj.put("image_content", base64Str);
			callfrom = 2;
			Constants.CHANGE_PHOTO_FLAG = true;
			AsyncaTaskApiCall update_prof_lisenar = new AsyncaTaskApiCall(FragmentMyAccount.this, loginObj.toString(),
					getActivity(), "reg_update", Constants.REQUEST_TYPE_POST);
			update_prof_lisenar.execute();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	

	/**
	 * This method sets the list-view containing the user-data & sign-out
	 * option. The password, gender & 'about-me' can also be updated from this
	 * list-actions.
	 */
	public void setListView() {
		Log.i("MyAccount: setListView",userCred.toString());
		adapter = new AdapterForSettings(context, setting_criteria, userCred);
		lv_setting.setAdapter(adapter);
		lv_setting.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (position == 0) {
					String[] country_list = context.getResources().getStringArray(R.array.countries_array);
					showDialogWithChooser(country_list, context.getResources().getString(R.string.txt_select_country),
							position);
				}
				if (position == 1) {
					String[] age_list = context.getResources().getStringArray(R.array.age_range_list);
					showDialogWithChooser(age_list, context.getResources().getString(R.string.txt_select_age), position);
				} else if (position == 2) {
					String[] gender_list = context.getResources().getStringArray(R.array.gender);
					showDialogWithChooser(gender_list, context.getResources().getString(R.string.txt_gender), position);
				} else if (position == 3) {
					Constants.POFESSION_FLAG = true;
					setSomethingAbout(context.getResources().getString(R.string.txt_edit_profesion), context
							.getResources().getString(R.string.acc_prof), position);
				} else if (position == 4) {
					String[] seating_pref_list = context.getResources().getStringArray(R.array.seating_pref);
					String title = context.getResources().getString(R.string.txt_seatting_pref_cap);
					showDialogWithChooser(seating_pref_list, title, position);
				} else if (position == 5) {
					Constants.SOME_ABOUT_FLAG = true;
					setSomethingAbout(context.getResources().getString(R.string.txt_sm_about), context.getResources()
							.getString(R.string.txt_say_sm_about), position);
				} else if (position == 6) {
					Intent intent = new Intent(context, PasswordChangeActivity.class);
					startActivity(intent);
				} else if (position == 7) {
					signOut();
				}
			}
		});
	}

	/**
	 * Shows the user's status details in a dialog.
	 */
	@SuppressWarnings("static-access")
	@SuppressLint("InflateParams")
	public void ShowStatus() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView title = (TextView) customTitleView.findViewById(R.id.tv_title);
		title.setText(context.getResources().getString(R.string.txt_status));
		builder.setCustomTitle(customTitleView);
		builder.setMessage(appInstance.getUserCred().getStatus())
				.setCancelable(false)
				.setPositiveButton(context.getResources().getString(R.string.txt_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();

							}
						}).setTitle(context.getResources().getString(R.string.acc_sign_out))
				.setIcon(R.drawable.ic_sing_out);
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Calls the sign-out API with necessary data after getting confirmation
	 * from the user by a dialog & checking the internet connection.
	 */
	public void signOut() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		ImageView im_icon = (ImageView) customTitleView.findViewById(R.id.im_icon);
		TextView tvtitle = (TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(context.getResources().getString(R.string.acc_sign_out));
		im_icon.setVisibility(View.VISIBLE);
		im_icon.setImageResource(R.drawable.ic_sing_out);
		builder.setCustomTitle(customTitleView);
		builder.setMessage(context.getResources().getString(R.string.txt_sign_out_msz))
				.setCancelable(false)
				.setPositiveButton(context.getResources().getString(R.string.txt_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								try {
									loginObj = new JSONObject();
									loginObj.put("token", appInstance.getUserCred().getToken());
									callfrom = 1;
									if (Constants.isOnline(activity)) {
										AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(
												FragmentMyAccount.this, loginObj.toString(), getActivity(), "logout",
												Constants.REQUEST_TYPE_POST);
										// TODO Check table delete when logged
										// out;
										SeatUnityDatabase sDb = new SeatUnityDatabase(context);
										sDb.droptableBoardingPassDbManager();
										log_in_lisenar.execute();
									} else {
										Toast.makeText(getActivity(),
												activity.getResources().getString(R.string.txt_please_check_internet),
												Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						})
				.setNegativeButton(context.getResources().getString(R.string.txt_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).setTitle(context.getResources().getString(R.string.acc_sign_out))
				.setIcon(R.drawable.ic_sing_out);
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Shows the gender-update dialog.
	 * 
	 * @param items
	 * @param title
	 * @param position
	 */
	public void showDialogWithChooser(final String[] items, String title, final int position) {
		userCred = appInstance.getUserCred();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tvtitle = (TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(title);
		builder.setCustomTitle(customTitleView);
		builder.setPositiveButton(context.getResources().getString(R.string.txt_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						Constants.setAllFlagFalse();
					}
				});
		String val = getValueByPosition(position);
		int posInArray = getPositionInArray(items, val);
		builder.setSingleChoiceItems(items, posInArray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				try {
					loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					loginObj.put("password", appInstance.getUserCred().getPassword());
					loginObj.put("language", appInstance.getUserCred().getLanguage());
					loginObj.put("firstname", appInstance.getUserCred().getFirstname());
					loginObj.put("lastname", appInstance.getUserCred().getLastname());
					loginObj.put("gender", appInstance.getUserCred().getGender());
					loginObj.put("live_in", appInstance.getUserCred().getLive_in());
					loginObj.put("age", appInstance.getUserCred().getAge());
					loginObj.put("profession", appInstance.getUserCred().getProfession());
					loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
					loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
					loginObj.put("status", appInstance.getUserCred().getStatus());
					loginObj.put("image_name", "");
					loginObj.put("image_type", "");
					loginObj.put("image_content", "");
					if(userCred==null)
						userCred = appInstance.getUserCred();
					if (position == 0) {
						String country = items[which].toString();
						loginObj.put("live_in", country);
						userCred.setLive_in(country);
						Constants.LIVE_IN_FLAG = true;
					} else if (position == 1) {
						String age = items[which].toString();
						loginObj.put("age", age);
						userCred.setAge(age);
						Constants.AGE_FLAG = true;

					} else if (position == 2) {
						String gender = items[which].toString();
						loginObj.put("gender", gender);
						userCred.setGender(gender);
						Constants.GENDER_FLAG = true;

					} else if (position == 4) {
						String seat_pref = items[which].toString();
						loginObj.put("seating_pref", seat_pref);
						userCred.setSeating_pref(seat_pref);
						Constants.SEATING_PREF_FLAG = true;
					}
					appInstance.setUserCred(userCred);
					callfrom = 2;
					if (Constants.isOnline(activity)) {
						AsyncaTaskApiCall update_prof_lisenar = new AsyncaTaskApiCall(FragmentMyAccount.this, loginObj
								.toString(), getActivity(), "reg_update", Constants.REQUEST_TYPE_POST);
						update_prof_lisenar.execute();
					} else {
						Toast.makeText(getActivity(),
								activity.getResources().getString(R.string.txt_please_check_internet), Toast.LENGTH_SHORT)
								.show();
						return;
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		builder.show();

	}

	private String getValueByPosition(int position) {
		String val = "";
		if (position == 0)
			val = userCred.getLive_in();
		else if (position == 1)
			val = userCred.getAge();
		else if (position == 2)
			val = userCred.getGender();
		else if (position == 4)
			val = userCred.getSeating_pref();
		return val;
	}

	private int getPositionInArray(String[] items, String string) {
		int len = items.length;
		for (int i = 0; i < len; i++) {
			if (items[i].equalsIgnoreCase(string))
				return i;
		}
		return -1;
	}

	/**
	 * Shows a dialog with the details of the user's 'about-info'.
	 * 
	 * @param title
	 * @param hint
	 * @param position
	 */
	@SuppressLint("InflateParams")
	public void setSomethingAbout(String title, final String hint, final int position) {
		final EditText input = new EditText(context);
		input.setHint(hint);
		final AlertDialog d = new AlertDialog.Builder(context).setView(input)
				// Set to null. We override the onclick
				.setPositiveButton(context.getResources().getString(R.string.txt_ok), null)
				.setNegativeButton(context.getResources().getString(R.string.txt_cancel), null).create();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tvtitle = (TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(title);
		if (position == 3) {
			input.setText(appInstance.getUserCred().getProfession());

		} else if (position == 5) {

			input.setText(appInstance.getUserCred().getSomethinAbout());

		}
		d.setCustomTitle(customTitleView);

		d.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button ok = d.getButton(AlertDialog.BUTTON_POSITIVE);
				Button cancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						d.cancel();
						Constants.setAllFlagFalse();
					}
				});
				ok.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String value = input.getText().toString();

						if ((value == null) || (value.equals(""))) {
							Toast.makeText(context,
									context.getResources().getString(R.string.txt_please_enter) + " " + hint,
									Toast.LENGTH_SHORT).show();
						} else {
							d.cancel();
							try {
								JSONObject loginObj = new JSONObject();
								loginObj.put("token", appInstance.getUserCred().getToken());
								loginObj.put("password", appInstance.getUserCred().getPassword());
								loginObj.put("language", appInstance.getUserCred().getLanguage());
								loginObj.put("firstname", appInstance.getUserCred().getFirstname());
								loginObj.put("lastname", appInstance.getUserCred().getLastname());
								loginObj.put("gender", appInstance.getUserCred().getGender());
								loginObj.put("live_in", appInstance.getUserCred().getLive_in());
								loginObj.put("age", appInstance.getUserCred().getAge());
								loginObj.put("profession", appInstance.getUserCred().getProfession());
								loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
								loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
								loginObj.put("status", appInstance.getUserCred().getStatus());
								loginObj.put("image_name", "");
								loginObj.put("image_type", "");
								loginObj.put("image_content", "");
								if (position == 3) {
									loginObj.put("profession", value);
									userCred.setProfession(value);
								} else if (position == 5) {
									loginObj.put("some_about_you", value);
									userCred.setSomethinAbout(value);

								}
								callfrom = 2;
								if (Constants.isOnline(activity)) {
									AsyncaTaskApiCall update_prof_lisenar = new AsyncaTaskApiCall(
											FragmentMyAccount.this, loginObj.toString(), getActivity(), "reg_update",
											Constants.REQUEST_TYPE_POST);
									update_prof_lisenar.execute();
								} else {
									Toast.makeText(getActivity(),
											activity.getResources().getString(R.string.txt_please_check_internet),
											Toast.LENGTH_SHORT).show();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}
				});
			}
		});
		d.show();
	}

	/**
	 * Shows a password-change dialog as the user-name update view.
	 */
	public void showCustomDialogForPasswordChange() {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.edit_user_name);
		dialog.show();
	}

	/**
	 * Shows a dialog with two options: pick image from SD card or take a photo
	 * with camera to update the profile picture.
	 * 
	 * @param items
	 * @param title
	 */
	public void showDialogTochosePhoto(final CharSequence[] items, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tvtitle = (TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(title);
		builder.setCustomTitle(customTitleView);

		builder.setPositiveButton(context.getResources().getString(R.string.txt_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				if (which == 0) {
					// Intent cameraIntent = new
					// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					// //Intent cameraIntent = new
					// Intent("android.media.action.IMAGE_CAPTURE");
					// //Intent cameraIntent = new
					// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					// startActivityForResult(cameraIntent,
					// ACTION_REQUEST_CAMERA);

					createfolder();
					photofromcamera = System.currentTimeMillis() + ".jpg";
					Constants.drectory = drectory;
					Constants.photofromcamera = photofromcamera;
					final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(drectory, photofromcamera);
					Log.e("pos", drectory + photofromcamera + " " + f.exists());
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, ACTION_REQUEST_CAMERA);
					Constants.SELECTEDBOARDINGPASSPOSITION = 1;
				} else if (which == 1) {

					if (Build.VERSION.SDK_INT < 19) {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");

						Intent chooser = Intent.createChooser(intent, "Choose a Picture");
						startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
					} else {

						Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						intent.setType("image/jpeg");
						startActivityForResult(intent, ACTION_REQUEST_GALLERY_KITKAT);
					}
					Constants.SELECTEDBOARDINGPASSPOSITION = 1;
				}
			}
		});
		builder.show();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ((requestCode == ACTION_REQUEST_GALLERY) || (requestCode == ACTION_REQUEST_GALLERY_KITKAT)) {
			Uri selectedImageUri;
			ImageScale scaleimage = new ImageScale();
			String tempPath = "";
			if (requestCode == ACTION_REQUEST_GALLERY_KITKAT) {
				selectedImageUri = data.getData();
				tempPath = Constants.getPath(activity, selectedImageUri);
			} else {
				selectedImageUri = data.getData();
				tempPath = getPath(selectedImageUri, activity);
				Log.e("external", "msg " + selectedImageUri.toString());
			}

			File file = new File(tempPath);
			photo = scaleimage.decodeImagetoUpload(file.getAbsolutePath());
			file.delete();
			uploadProfileImage(photo);
		} else if (requestCode == ACTION_REQUEST_CAMERA) {
			try {
				// //Toast.makeText(activity, "working", 2000).show();
				// Log.e("tag", "1");
				// Uri selectedImageUri ;
				// Log.e("tag", "2");
				// ImageScale scaleimage=new ImageScale();
				// Log.e("tag", "3");
				//
				// String tempPath ="";
				// selectedImageUri = data.getData();
				// Log.e("tag", "4");
				//
				// Log.e("Path", "ab "+data);
				// Log.e("tag", "5");
				//
				// tempPath =Constants.getPath(activity, selectedImageUri);
				// Log.e("tag", "6");
				//
				// Log.e("Path", "ab "+tempPath);
				// //File file=new File(tempPath);
				// photo = scaleimage.decodeFile(tempPath);
				// Log.e("height", photo.getHeight()+" ab "+photo.getWidth());
				// //file.delete();
				// uploadProfileImage(photo);
				if (drectory == null) {
					drectory = Constants.drectory;
				}
				if (photofromcamera == null) {
					photofromcamera = Constants.photofromcamera;
				}
				String filepath = drectory + "/" + photofromcamera;
				Log.e("inside", "on  " + drectory + "  " + photofromcamera);
				File file = new File(filepath);

				if (file.exists()) {
					// Toast.makeText(activity, "working", 2000).show();
					ImageScale scaleimage = new ImageScale();
					photo = scaleimage.decodeFile(file.getAbsolutePath());
					file.delete();

					uploadProfileImage(photo);
				}
				// else{
				// Toast.makeText(activity, "Not working", 2000).show();
				// }

			} catch (Exception e) {
				Log.e("Could not save", e.toString());
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * Creates a folder named "Lipberryfinal" in the SD card :o
	 */
	public void createfolder() {
		String newFolder = "/Lipberryfinal";
		String thumb = "/Lipberrythumb";
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		drectory = extStorageDirectory + newFolder;
		File myNewFolder = new File(drectory);
		myNewFolder.mkdir();
	}

	/**
	 * Deletes the file passed.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	@Override
	public void responseOk(JSONObject job) {
		Log.e("afImageUrl", "" + Constants.IMG_PROF_PIC);
		try {
			if (job.get("success").equals("true")) {
				Constants.setAllFlagFalse();
				if (callfrom == 1) {
					UserCred ucrCred = new UserCred("", "", "", "", "", "", "", "", "", "", "", "", "", "");
					activity.finish();
					appInstance.setUserCred(ucrCred);
					appInstance.setRememberMe(false);
					SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
					dbInstance.open();
					dbInstance.droptableBoardingPassDbManager();
					dbInstance.createtableBoardingPassDbManager();
					dbInstance.close();
					Toast.makeText(context, getResources().getString(R.string.txt_logout_success), Toast.LENGTH_SHORT)
							.show();
				} else if (callfrom == 2) {
					if (job.has("image_url")) {
						String imageurl = job.getString("image_url");
						if (!imageurl.equals("")) {
							userCred.setImage_url(imageurl);
							appInstance.setUserCred(userCred);
							ImageLoader.getInstance().displayImage(appInstance.getUserCred().getImage_url(),
									img_prof_pic);
						}

					}

					Toast.makeText(context, context.getResources().getString(R.string.txt_update_success),
							Toast.LENGTH_SHORT).show();

					appInstance.setUserCred(userCred);
					setListView();
					Constants.photo = null;
				}
				// this.img_prof_pic.setImageBitmap(photo);
				ImageLoader.getInstance()
						.displayImage(appInstance.getUserCred().getImage_url(), Constants.IMG_PROF_PIC);

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void responseFailure(JSONObject job) {
		try {
			JSONObject joberror = new JSONObject(job.getString("error"));
			String code = joberror.getString("code");
			if (code.equals("x05")) {
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", appInstance.getUserCred().getEmail());
				loginObj.put("password", appInstance.getUserCred().getPassword());
				String loginData = loginObj.toString();
				AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(FragmentMyAccount.this, loginData, context,
						"login", Constants.REQUEST_TYPE_POST, true);
				log_in_lisenar.execute();

			} else {
				if (callfrom == 1) {

					Toast.makeText(context, getResources().getString(R.string.txt_logout_failed), Toast.LENGTH_SHORT)
							.show();
				} else if (callfrom == 2) {
					Toast.makeText(context, getResources().getString(R.string.txt_update_failed), Toast.LENGTH_SHORT)
							.show();

				}
			}

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
		try {
			UserCred ucredFromServer;

			String status = job.getString("success");

			if (status.equals("true")) {
				ucredFromServer = UserCred.parseUserCred(job);
				ucredFromServer.setEmail(appInstance.getUserCred().getEmail());
				ucredFromServer.setPassword(appInstance.getUserCred().getPassword());
				appInstance.setUserCred(ucredFromServer);
				appInstance.setRememberMe(true);
				Log.e("tagged email", "abc " + appInstance.getUserCred().getEmail());
				JSONObject loginObjnew = new JSONObject();
				loginObjnew.put("token", appInstance.getUserCred().getToken());
				loginObjnew.put("password", appInstance.getUserCred().getPassword());
				loginObjnew.put("language", appInstance.getUserCred().getLanguage());
				loginObjnew.put("firstname", appInstance.getUserCred().getFirstname());
				loginObjnew.put("lastname", appInstance.getUserCred().getLastname());
				loginObjnew.put("gender", appInstance.getUserCred().getGender());
				loginObjnew.put("live_in", appInstance.getUserCred().getLive_in());
				loginObjnew.put("age", appInstance.getUserCred().getAge());
				loginObjnew.put("profession", appInstance.getUserCred().getProfession());
				loginObjnew.put("seating_pref", appInstance.getUserCred().getSeating_pref());
				loginObjnew.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
				loginObjnew.put("status", appInstance.getUserCred().getStatus());
				loginObjnew.put("image_name", "");
				loginObjnew.put("image_type", "");
				loginObjnew.put("image_content", "");
				UserCred ucredcopy = ucredFromServer;
				if (Constants.LIVE_IN_FLAG) {
					ucredcopy.setLive_in(userCred.getLive_in());
					loginObjnew.put("live_in", userCred.getLive_in());
				} else if (Constants.AGE_FLAG) {
					ucredcopy.setAge(userCred.getAge());
					loginObjnew.put("age", userCred.getAge());

				} else if (Constants.GENDER_FLAG) {
					ucredcopy.setGender(userCred.getGender());
					loginObjnew.put("gender", userCred.getGender());

				} else if (Constants.POFESSION_FLAG) {
					ucredcopy.setProfession(userCred.getProfession());
					loginObjnew.put("profession", userCred.getProfession());

				} else if (Constants.SEATING_PREF_FLAG) {
					ucredcopy.setSeating_pref(userCred.getSeating_pref());
					loginObjnew.put("seating_pref", userCred.getSeating_pref());

				} else if (Constants.SOME_ABOUT_FLAG) {
					ucredcopy.setSomethinAbout(userCred.getSomethinAbout());
					loginObjnew.put("some_about_you", userCred.getSomethinAbout());

				} else if (Constants.CHANGE_PHOTO_FLAG) {
					// ucredcopy.setLive_in(userCred.getLive_in());
					loginObjnew.put("image_name", loginObj.get("image_name"));
					loginObjnew.put("image_type", loginObj.get("image_type"));
					loginObjnew.put("image_content", loginObj.get("image_content"));

				}
				userCred = ucredcopy;
				if (callfrom == 1) {
					JSONObject loginObj2 = new JSONObject();
					loginObj2.put("token", appInstance.getUserCred().getToken());
					callfrom = 1;
					AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(FragmentMyAccount.this,
							loginObj2.toString(), getActivity(), "logout", Constants.REQUEST_TYPE_POST);
					log_in_lisenar.execute();
				} else if (callfrom == 2) {
					callfrom = 2;
					AsyncaTaskApiCall update_prof_lisenar = new AsyncaTaskApiCall(FragmentMyAccount.this,
							loginObjnew.toString(), getActivity(), "reg_update", Constants.REQUEST_TYPE_POST);
					update_prof_lisenar.execute();
				}

			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void LoginFailed(JSONObject job) {
		try {
			JSONObject joberror = new JSONObject(job.getString("error"));
			String code = joberror.getString("code");
			Constants.setAllFlagFalse();
			String message = joberror.getString("message");
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void responseFailure(ServerResponse response) {
		if (response.getStatus() != 200) {
			BoardingPassApplication.alert(getActivity(), "Internet connectivity is lost! Please retry the operation.");
		}
	}

}
