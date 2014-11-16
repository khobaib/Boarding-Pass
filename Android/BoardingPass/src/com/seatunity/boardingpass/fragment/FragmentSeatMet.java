package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForSeatmet;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.interfaces.CollapseClassSelectionList;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.SeatMate;
import com.seatunity.model.SeatMetList;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;

/**
 * Shows the list of the seat-mates.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentSeatMet extends Fragment implements CallBackApiCall {

	private final String TAG = this.getClass().getSimpleName();

	HomeListFragment parentAsHome;
	PastBoardingPassListFragment parentAsPast;
	ImageView img_add_boardingpass;
	TextView tv_add_boardingpass;
	BoardingPassApplication appInstance;
	SeatMetList seatmet_listobj;
	BoardingPass bpass;
	public String savedMessage;
	public String Savedurl;
	ArrayList<String> itemList;
	TextView tv_from, tv_to, tv_month_inside_icon, tv_date_inside_icon, tv_seat_no, tv_flight_no, tv_start_time,
			tv_arrival_time, tv_jfk, tv_cdg, tv_message;
	ListView lv_seat_met_list;
	int selectedposition = 0;
	Context context;
	public int callfrom = 0;
	ListView lvClassSelectionABar;
	ArrayAdapter<String> aAdpt;

	private boolean isClassDropDownVisible = false;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		context = getActivity();
	}

	/**
	 * @param seatmet_listobj
	 *            The seat-mate list to show
	 * @param bpass
	 *            The {@link BoardingPass } object for which the seat-mate list
	 *            is to be shown
	 */
	public FragmentSeatMet(SeatMetList seatmet_listobj, BoardingPass bpass) {
		Log.e("insideList4", bpass.getTravel_from_name());
		this.seatmet_listobj = seatmet_listobj;
		this.bpass = bpass;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_seat_mate, container, false);
		tv_message = (TextView) v.findViewById(R.id.tv_message);
		tv_from = (TextView) v.findViewById(R.id.tv_from);
		tv_to = (TextView) v.findViewById(R.id.tv_to);
		tv_month_inside_icon = (TextView) v.findViewById(R.id.tv_month_inside_icon);
		tv_date_inside_icon = (TextView) v.findViewById(R.id.tv_date_inside_icon);
		tv_seat_no = (TextView) v.findViewById(R.id.tv_seat_no);
		tv_flight_no = (TextView) v.findViewById(R.id.tv_flight_no);
		tv_start_time = (TextView) v.findViewById(R.id.tv_start_time);
		tv_arrival_time = (TextView) v.findViewById(R.id.tv_arrival_time);
		tv_cdg = (TextView) v.findViewById(R.id.tv_cdg);
		tv_jfk = (TextView) v.findViewById(R.id.tv_jfk);
		lv_seat_met_list = (ListView) v.findViewById(R.id.lv_seat_met_list);
		lvClassSelectionABar = (ListView) v.findViewById(R.id.lv_class_list);
		MainActivity.setCollapseListForSeatMetListener(ccsListener);

		setListViewAtPosition(0);
		selectedposition = 0;
		setDetailsBoaredingpass();

		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lvClassSelectionABar.setVisibility(View.GONE);
				isClassDropDownVisible = false;
			}
		});
		// v.findViewById(R.id.re_details_holder).setOnClickListener(new
		// OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// lvClassSelectionABar.setVisibility(View.GONE);
		// isClassDropDownVisible = false;
		// }
		// });

		return v;
	}

	/**
	 * Initiates the seat-mate list-view for the selected class-type. But shows
	 * a text-view instead of the listview with an error-message if no seat-mate
	 * of the specified type is found for this boarding-pass.
	 * 
	 * @param i
	 *            The position of the action-menu list-item
	 */
	public void setListViewAtPosition(int i) {
		AdapterForSeatmet adapter;

		if (i == 0) {
			ArrayList<SeatMate> seatmatelist_all = new ArrayList<SeatMate>(seatmet_listobj.getAllSeatmateList());
			if (seatmatelist_all.size() > 0) {
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter = new AdapterForSeatmet(appInstance.getUserCred().getToken(), FragmentSeatMet.this,
						getActivity(), seatmet_listobj.getAllSeatmateList(), ccsListener);
				lv_seat_met_list.setAdapter(adapter);
			} else {
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound) + " "
						+ itemList.get(i) + " " + getActivity().getResources().getString(R.string.txt_is_found));
			}

		} else if (i == 1) {
			ArrayList<SeatMate> seatmatelist_firstclass = new ArrayList<SeatMate>();
			for (int k = 0; k < seatmet_listobj.getAllSeatmateList().size(); k++) {
				if (seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("First Class")) {
					seatmatelist_firstclass.add(seatmet_listobj.getAllSeatmateList().get(k));
				}
			}
			if (seatmatelist_firstclass.size() > 0) {
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter = new AdapterForSeatmet(appInstance.getUserCred().getToken(), FragmentSeatMet.this,
						getActivity(), seatmatelist_firstclass, ccsListener);
				lv_seat_met_list.setAdapter(adapter);
			} else {
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound) + " "
						+ itemList.get(i) + " " + getActivity().getResources().getString(R.string.txt_is_found));
			}

		} else if (i == 2) {

			ArrayList<SeatMate> seatmatelist_business_class = new ArrayList<SeatMate>();
			for (int k = 0; k < seatmet_listobj.getAllSeatmateList().size(); k++) {
				if (seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("Business Class")) {
					seatmatelist_business_class.add(seatmet_listobj.getAllSeatmateList().get(k));
				}
			}
			if (seatmatelist_business_class.size() > 0) {
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter = new AdapterForSeatmet(appInstance.getUserCred().getToken(), FragmentSeatMet.this,
						getActivity(), seatmatelist_business_class, ccsListener);
				lv_seat_met_list.setAdapter(adapter);
			} else {
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound) + " "
						+ itemList.get(i) + " " + getActivity().getResources().getString(R.string.txt_is_found));
			}

		} else if (i == 3) {
			ArrayList<SeatMate> seatmatelist_economy_calss = new ArrayList<SeatMate>();
			for (int k = 0; k < seatmet_listobj.getAllSeatmateList().size(); k++) {
				if ((seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("Economy Class"))
						|| (seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("Premium Economy"))) {
					seatmatelist_economy_calss.add(seatmet_listobj.getAllSeatmateList().get(k));
				}
			}
			if (seatmatelist_economy_calss.size() > 0) {
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter = new AdapterForSeatmet(appInstance.getUserCred().getToken(), FragmentSeatMet.this,
						getActivity(), seatmatelist_economy_calss, ccsListener);
				lv_seat_met_list.setAdapter(adapter);
			} else {
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound) + " "
						+ itemList.get(i) + " " + getActivity().getResources().getString(R.string.txt_is_found));
			}
		}

		lv_seat_met_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				lvClassSelectionABar.setVisibility(View.GONE);
				isClassDropDownVisible = false;
				if (parentAsHome != null)
					parentAsHome.startFragmentSingleSeatmet(seatmet_listobj.getAllSeatmateList().get(position), bpass);
				else if (parentAsPast != null)
					parentAsPast.startFragmentSingleSeatmet(seatmet_listobj.getAllSeatmateList().get(position), bpass);
			}
		});
	}

	private ActionBar actionBar;

	public void setActionBarNavigation(boolean show) {
		actionBar = getActivity().getActionBar();
		// OnNavigationListener imll = new OnNavigationListener() {
		// @Override
		// public boolean onNavigationItemSelected(int itemPosition, long
		// itemId) {
		// setListView(itemPosition);
		// selectedposition = itemPosition;
		// return false;
		// }
		// };
		String[] class_list = getActivity().getResources().getStringArray(R.array.seat_class);
		itemList = new ArrayList<String>(Arrays.asList(class_list));
		// AdapterBaseMaps adapter = new AdapterBaseMaps(getActivity(),
		// itemList);
		aAdpt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1,
				itemList);
		if (show) {
			// actionBar.setDisplayShowTitleEnabled(false);
			// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			// actionBar.setListNavigationCallbacks(adapter, imll);
			//

			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionBar.setCustomView(R.layout.action_bar_title);

			View v = actionBar.getCustomView();
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					lvClassSelectionABar.setVisibility(View.GONE);
					isClassDropDownVisible = false;
				}
			});
			TextView titleTxtView = (TextView) v.findViewById(R.id.txt_title);
			final TextView txt_seatmate = (TextView) v.findViewById(R.id.txt_seatmate);
			titleTxtView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isClassDropDownVisible) {
						lvClassSelectionABar.setVisibility(View.VISIBLE);
						isClassDropDownVisible = true;
					} else {
						lvClassSelectionABar.setVisibility(View.GONE);
						isClassDropDownVisible = false;
					}
					lvClassSelectionABar.setAdapter(aAdpt);
				}
			});
			v.findViewById(R.id.img_icon).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goBack();
				}
			});
			v.findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goBack();
				}
			});
			lvClassSelectionABar.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3) {
					setListViewAtPosition(itemPosition);
					isClassDropDownVisible=false;
					selectedposition = itemPosition;
					lvClassSelectionABar.setVisibility(View.GONE);
					txt_seatmate.setText(itemList.get(itemPosition));

				}
			});
			// RatingBar RatingBar=v.findViewById(R.id.ratingBar1)
			// titleTxtView.setText("abcd");
		} else {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setListNavigationCallbacks(null, null);
		}

	}

	protected boolean goBack() {
		if (parentAsHome != null)
			parentAsHome.onBackPressed();
		else if (parentAsPast != null)
			parentAsPast.onBackPressed();
		else
			return false;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		lvClassSelectionABar.setVisibility(View.GONE);
		isClassDropDownVisible = false;
		if (item.getItemId() == android.R.id.home) {
			if (goBack())
				return true;
			else
				return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();
		setActionBarNavigation(false);
		((MainActivity) getActivity()).refreash_menu.setVisible(false);
		actionBar.setDisplayShowCustomEnabled(false);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_HOME_AS_UP);

		// actionBar.setDisplayShowHomeEnabled(true);
		//
		// actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO);
		// actionBar.setDisplayHomeAsUpEnabled(true);

		// getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onResume() {
		super.onResume();
		setActionBarNavigation(true);
		((MainActivity) getActivity()).refreash_menu.setVisible(true);
		((MainActivity) getActivity()).refreash_menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if ((Constants.isOnline(getActivity())) && (!appInstance.getUserCred().getEmail().equals(""))) {

					callSeatmet();
				} else {
					sowAlertMessage();
				}
				return false;

			}
		});
	}

	/**
	 * Messages the seat-mate.
	 */
	public void callSeatmet() {
		callfrom = 2;
		String extendedurl = "seatmatelist/" + bpass.getCarrier() + "/" + bpass.getFlight_no() + "/"
				+ bpass.getJulian_date();
		extendedurl = extendedurl.replace(" ", "");
		AsyncaTaskApiCall get_list = new AsyncaTaskApiCall(FragmentSeatMet.this, getJsonObjet(), getActivity(),
				extendedurl, Constants.REQUEST_TYPE_POST);
		get_list.execute();
	}

	/**
	 * If internet is not activated, then this alert shows the message that: <br>
	 * "The seatmates list is only vailable in the online mode"
	 */
	public void sowAlertMessage() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setMessage(getResources().getString(R.string.txt_seatmet_message_only_online))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * @return A JSON-formatted string with only the app-token to call API.
	 */
	public String getJsonObjet() {

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Sets the details of the boarding-pass from the global {@link #bpass}
	 * object.
	 */
	public void setDetailsBoaredingpass() {
		String date = Constants.getDayandYear(Integer.parseInt(bpass.getJulian_date()));
		String[] dateParts = date.split(":");
		String month = dateParts[1];
		String dateofmonth = dateParts[0];
		tv_from.setText(bpass.getTravel_from_name());
		tv_to.setText(bpass.getTravel_to_name());
		tv_month_inside_icon.setText(month);
		tv_date_inside_icon.setText(dateofmonth);
		tv_seat_no.setText(getActivity().getResources().getString(R.string.txt_seat_nno) + " "
				+ Constants.removeingprecingZero(bpass.getSeat()));
		tv_flight_no.setText(getActivity().getResources().getString(R.string.txt_flight_no) + " " + bpass.getCarrier()
				+ bpass.getFlight_no());
		tv_start_time.setText(bpass.getDeparture());
		tv_arrival_time.setText(bpass.getArrival());
		Log.e("test", "a " + bpass.getTravel_from_name());
		tv_cdg.setText(bpass.getTravel_from());
		tv_jfk.setText(bpass.getTravel_to());
	}

	@Override
	public void responseOk(JSONObject job) {
		try {
			if (job.getString("success").equals("true")) {
				// boarding_passes
				if (callfrom == 1) {
					Toast.makeText(context, context.getResources().getString(R.string.txt_emailsent_success),
							Toast.LENGTH_SHORT).show();

				} else if (callfrom == 2) {
					SeatMetList seatmet_listlist = SeatMetList.getSeatmetListObj(job);
					this.seatmet_listobj = seatmet_listlist;
					if (seatmet_listobj.getAllSeatmateList().size() > 0) {
						setListViewAtPosition(selectedposition);
					} else {
						Toast.makeText(context, context.getResources().getString(R.string.txt_getseatmate_failure),
								Toast.LENGTH_SHORT).show();
					}
				}

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
				AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(FragmentSeatMet.this, loginData, context,
						"login", Constants.REQUEST_TYPE_POST, true);
				log_in_lisenar.execute();

			} else {
				Toast.makeText(context, job.getString("message"), Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
		try {
			UserCred userCred;
			String status = job.getString("success");
			if (status.equals("true")) {
				userCred = UserCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				appInstance.setUserCred(userCred);
				appInstance.setRememberMe(true);
				if (callfrom == 1) {
					JSONObject loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					loginObj.put("message", savedMessage);
					callfrom = 1;
					AsyncaTaskApiCall sendmessage = new AsyncaTaskApiCall(FragmentSeatMet.this, loginObj.toString(),
							context, Savedurl, Constants.REQUEST_TYPE_POST);
					sendmessage.execute();
				} else if (callfrom == 2) {
					callfrom = 2;
					String extendedurl = "seatmatelist/" + bpass.getCarrier() + "/" + bpass.getFlight_no() + "/"
							+ bpass.getJulian_date();
					extendedurl = extendedurl.replace(" ", "");
					AsyncaTaskApiCall get_list = new AsyncaTaskApiCall(FragmentSeatMet.this, getJsonObjet(), context,
							extendedurl, Constants.REQUEST_TYPE_POST);
					get_list.execute();
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
			// String code =joberror.getString("code");
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

	private CollapseClassSelectionList ccsListener = new CollapseClassSelectionList() {
		@Override
		public void collapseList(boolean isToCollapse) {
			if (isToCollapse && lvClassSelectionABar != null) {
				lvClassSelectionABar.setVisibility(View.GONE);
				isClassDropDownVisible = false;
			}
		}
	};

}
