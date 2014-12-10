package com.seatunity.boardingpass.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.fragment.FragmentSeatMet;
import com.seatunity.boardingpass.interfaces.CollapseClassSelectionList;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.SeatMate;

/**
 * Adapter for the seat-mate list.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("InflateParams")
public class AdapterForSeatmet extends BaseAdapter {
	String month;
	String day;
	private Activity activity;
	private ArrayList<SeatMate> list;
	FragmentSeatMet lisenar;
	BoardingPassApplication appInstance;
	String token;
	String hint;
	EditText input;
	AlertDialog d;

	private CollapseClassSelectionList collapseClsSelector;

	/**
	 * The only constructor
	 * 
	 * @param token
	 * @param lisenar
	 * @param context
	 * @param item
	 */
	public AdapterForSeatmet(String token, FragmentSeatMet lisenar,
			Activity activity, ArrayList<SeatMate> item,
			CollapseClassSelectionList collapseClsSelector) {
		this.lisenar = lisenar;
		this.activity = activity;
		this.token = token;
		this.list = item;
		this.collapseClsSelector = collapseClsSelector;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Class holding all the views of a single object in the list.
	 * 
	 * @author Sumon
	 * 
	 */
	private class ViewHolder {
		ImageView img_profile, img_send_msz;
		TextView txt_name, tv_prof, tv_seat_pref;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater;
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			mInflater = (LayoutInflater) activity
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.seatmate_inflate, null);

			holder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			holder.tv_prof = (TextView) convertView.findViewById(R.id.tv_prof);
			holder.tv_seat_pref = (TextView) convertView
					.findViewById(R.id.tv_seat_pref);
			holder.img_profile = (ImageView) convertView
					.findViewById(R.id.img_profile);
			holder.img_send_msz = (ImageView) convertView
					.findViewById(R.id.img_send_msz);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).getName() != null) {
			holder.txt_name.setText(list.get(position).getName());
		}
		if ((list.get(position).getTravel_class() != null)
				&& (list.get(position).getSeat() != null)) {
			holder.tv_seat_pref.setText(list.get(position).getTravel_class()
					+ ", "
					+ Constants.removeingprecingZero(list.get(position)
							.getSeat()));
		}
		Log.e("Error", position + " " + list.get(position).getProfession());
		if (list.get(position).getProfession() != null) {
			holder.tv_prof.setText(list.get(position).getProfession());
		}
		if ((list.get(position).getImage_url() == null)
				|| (list.get(position).getImage_url().equals(""))) {

		} else {
			ImageLoader.getInstance().displayImage(
					list.get(position).getImage_url(), holder.img_profile);
		}
		holder.img_send_msz.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				collapseClsSelector.collapseList(true);
				showAlertDilog(position);
			}
		});

		return convertView;
	}

	/**
	 * Shows an alert-dialog for the seat-mate listed at the position (as
	 * passed) in the view's list with an {@link EditText} & a {@link Button} to
	 * message the mate.
	 * 
	 * @param position
	 */
	@SuppressWarnings("static-access")
	public void showAlertDilog(final int position) {
		hint = activity.getResources().getString(R.string.txt_message);
		input = new EditText(activity);
		d = new AlertDialog.Builder(activity)
				.setView(input)
				// Set to null. We override the onclick
				.setPositiveButton(
						activity.getResources().getString(R.string.txt_ok),
						null)
				.setNegativeButton(
						activity.getResources().getString(R.string.txt_cancel),
						null).create();
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view,
				null);
		TextView title = (TextView) customTitleView.findViewById(R.id.tv_title);
		title.setText(activity.getResources()
				.getString(R.string.txt_send_email));
		d.setCustomTitle(customTitleView);
		d.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String value = input.getText().toString();

						if ((value == null) || (value.equals(""))) {
							Toast.makeText(
									activity,
									activity.getResources().getString(
											R.string.txt_please_enter)
											+ " " + hint, Toast.LENGTH_SHORT)
									.show();
						} else {
							d.cancel();
							try {
								JSONObject loginObj = new JSONObject();
								loginObj.put("token", token);
								loginObj.put("message", value);
								lisenar.callfrom = 1;
								lisenar.savedMessage = value;
								lisenar.Savedurl = "messagemate/"
										+ list.get(position).getId();
								AsyncaTaskApiCall sendmessage = new AsyncaTaskApiCall(
										lisenar, loginObj.toString(), activity,
										"messagemate/"
												+ list.get(position).getId(),
										Constants.REQUEST_TYPE_POST);
								sendmessage.execute();

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

}
