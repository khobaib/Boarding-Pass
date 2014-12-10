package com.seatunity.boardingpass.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.ServerResponse;

/**
 * Adapter for the {@link BoardingPass} list
 * 
 * @author Sumon
 * 
 */
@SuppressLint("InflateParams")
public class AdapterForBoardingPass extends BaseAdapter {
	private final String TAG = this.getClass().getSimpleName();

	String month;
	String day;
	private Context context;
	private ArrayList<BoardingPass> list;

	/**
	 * The only constructor
	 * 
	 * @param context
	 * @param item
	 */
	public AdapterForBoardingPass(Context context,
			ArrayList<BoardingPass> item, boolean isPast) {
		this.context = context;
		this.list = item;
		final int padVal = isPast ? -1 : 1;
		Collections.sort(list, new Comparator<BoardingPass>() {
			public int compare(BoardingPass o1, BoardingPass o2) {
				String s1 = o1.getJulian_date().trim();
				String s2 = o2.getJulian_date().trim();
				if (s1 == s2)
					return 0;
				return padVal
						* (Integer.parseInt(s1) < Integer.parseInt(s2) ? -1 : 1);
			}
		});

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
	@SuppressWarnings("unused")
	private class ViewHolder {

		TextView tv_flight_to_from;
		TextView tv_cdg;
		TextView tv_jfk;
		TextView tv_flight_date;
		TextView tv_start_time;
		TextView tv_arrival_time;
		TextView tv_seat_no;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater;

		ViewHolder holder = new ViewHolder();
		Log.d(TAG, "BPass's local DB delete-state = "
				+ list.get(position).getDeletestate());
		String date = Constants.getDayandYear(Integer.parseInt(list
				.get(position).getJulian_date().trim()));
		String[] dateParts = date.split(":");
		month = dateParts[1];
		day = dateParts[0];
		if (convertView == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.boarding_pass_inflate,
					null);

			holder.tv_flight_to_from = (TextView) convertView
					.findViewById(R.id.tv_flight_to_from);
			holder.tv_cdg = (TextView) convertView.findViewById(R.id.tv_cdg);
			holder.tv_jfk = (TextView) convertView.findViewById(R.id.tv_jfk);
			holder.tv_flight_date = (TextView) convertView
					.findViewById(R.id.tv_flight_date);
			holder.tv_start_time = (TextView) convertView
					.findViewById(R.id.tv_start_time);
			// holder.tv_flight_date=(TextView)
			// convertView.findViewById(R.id.tv_flight_date);
			holder.tv_arrival_time = (TextView) convertView
					.findViewById(R.id.tv_arrival_time);
			holder.tv_seat_no = (TextView) convertView
					.findViewById(R.id.tv_seat_no);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).getTravel_from_name() == null) {
			list.get(position).setTravel_from_name("");
		}
		if (list.get(position).getTravel_to_name() == null) {
			list.get(position).setTravel_to_name("");
		}
		String travelFrom = list.get(position).getTravel_from_name();
		String travelTo = list.get(position).getTravel_to_name();
		if (travelFrom.equals(null) || travelFrom.length() <= 0
				|| travelTo.equals(null) || travelTo.length() <= 0) {
			try {
				Log.e("Touhid",
						"From-to airport-name not found ... looking for those");
				holder.tv_flight_to_from.setText(" - ");
				new GetSingleBPassDetails(position, holder.tv_flight_to_from)
						.execute();
			} catch (Exception e) {
				holder.tv_flight_to_from.setText(travelFrom + " - " + travelTo);
				e.printStackTrace();
			}
		} else
			holder.tv_flight_to_from.setText(travelFrom + " - " + travelTo);

		holder.tv_flight_date.setText(month + day + ", "
				+ list.get(position).getCarrier()
				+ list.get(position).getFlight_no());
		// holder.tv_start_time.setText(list.get(position).getDeparture());
		holder.tv_cdg.setText(list.get(position).getTravel_from());
		holder.tv_jfk.setText(list.get(position).getTravel_to());

		// holder.tv_arrival_time.setText(""+list.get(position).getArrival());
		holder.tv_seat_no.setText(context.getResources().getString(
				R.string.txt_seat_nno)
				+ " "
				+ Constants.removeingprecingZero(list.get(position).getSeat()));

		return convertView;
	}

	private class GetSingleBPassDetails extends
			AsyncTask<Void, Void, JSONObject> {

		private int bpPosition;
		private String bpId;
		private TextView tvTravelFromTo;

		public GetSingleBPassDetails(int bpPos, TextView tvTravelFromTo) {
			this.bpPosition = bpPos;
			this.bpId = list.get(bpPos).getId();
			this.tvTravelFromTo = tvTravelFromTo;
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject jObj = new JSONObject();
			try {
				jObj.put("token", BoardingPassApplication.getStaticUserCred()
						.getToken());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.i("Touhid",
					"Calling Single-BPass API: content: " + jObj.toString());
			JsonParser jsonParser = new JsonParser();
			ServerResponse response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_POST, Constants.baseurl
							+ "bpdetail/" + bpId, null, jObj.toString(), null);
			Log.i("Touhid", "Got response: " + response.toString());
			if (response.getStatus() == 200)
				return response.getjObj();
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			if (responseObj != null) {
				try {
					Log.i("Touhid",
							"Setting text as: " + responseObj.toString());
					String tFrom = responseObj.getString("travel_from_name");
					String tTo = responseObj.getString("travel_to_name");
					list.get(bpPosition).setTravel_from_name(tFrom);
					list.get(bpPosition).setTravel_to_name(tTo);
					tvTravelFromTo.setText(tFrom + " - " + tTo);
					notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
