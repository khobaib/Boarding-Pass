package com.seatunity.boardingpass;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.bugsense.trace.BugSenseHandler;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.HybridBinarizer;
import com.seatunity.boardingpass.adapter.NavDrawerListAdapter;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.fragment.AccountListFragment;
import com.seatunity.boardingpass.fragment.HomeListFragment;
import com.seatunity.boardingpass.fragment.PastBoardingPassListFragment;
import com.seatunity.boardingpass.fragment.TabFragment;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.boardingpass.utilty.PkpassReader;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassParser;
import com.seatunity.model.NavDrawerItem;
import com.seatunity.model.UserCred;
import com.touhiDroid.filepicker.FilePickerActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements CallBackApiCall {
	private DrawerLayout mDrawerLayout;
	public ListView mDrawerList;
	public TabFragment activeFragment;
	public MenuItem delete_menu;
	public MenuItem refreash_menu;
	public ActionBarDrawerToggle mDrawerToggle;
	public CharSequence mDrawerTitle;
	public CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	BoardingPassApplication appInstance;
	@SuppressWarnings("unused")
	private FragmentManager fragmentManager;
	int lastselectedposition = -1;
	BoardingPass boardingPass, needtoReusedBoardingPass;
	HomeListFragment fragHome;
	int prevselectedposition = -2;
	ImageView img_from_camera, img_from_sdcard;
	TextView tv_add_boardingpasswith_camera, tv_add_fromsdcard;
	RelativeLayout vw_bottom;
	MainActivity lisenar;
	int BARCODESCANFROMDIRECT = 0;
	int SCANBOARDINGPASSFROMSDCARD = 10;
	int SCANBOARDINGPASSFROMSDCAdIMAGE_FIRST_ROATAION = 101;
	int SCANBOARDINGPASSFROMSDCAdIMAGE_SECOND_ROATAION = 102;
	int SCANBOARDINGPASSFROMSDCAdIMAGE_THIIRD_ROATAION = 10;

	int SCANBARCODEFROMPDF = 12;

	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {

				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(MainActivity.this, "2b60c090");
		setContentView(R.layout.activity_main);
		try {
			Intent intent = getIntent();
			String filepath = intent.getData().toString();
			filepath = filepath.replace("file://", "");
			if ((filepath != null) && (!filepath.equals(""))) {
				showalert(filepath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		getOverflowMenu();
		ImageView icon = (ImageView) findViewById(android.R.id.home);
		FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon.getLayoutParams();
		iconLp.leftMargin = iconLp.rightMargin = 20;
		icon.setLayoutParams(iconLp);
		fragmentManager = getFragmentManager();
		appInstance = (BoardingPassApplication) getApplication();
		mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		navDrawerItems = new ArrayList<NavDrawerItem>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navMenuIcons.recycle();
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		adapter = new NavDrawerListAdapter(getApplicationContext(), appInstance);
		mDrawerList.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_navigation_drawer_closed,
				R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// getActionBar().setIcon(R.drawable.ic_navigation_drawer_closed);
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// if (savedInstanceState == null) {
		displayView(0);
		// }

	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(MainActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		lisenar = this;
		if (Constants.SELECTEDBOARDINGPASSPOSITION == 1) {
			displayView(1);
			Constants.SELECTEDBOARDINGPASSPOSITION = 0;
		}
		// displayView(Constants.SELECTEDPOSITION);
	}

	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Constants.SELECTEDPOSITION = position;
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		delete_menu = menu.findItem(R.id.delete);
		delete_menu.setVisible(false);
		refreash_menu = menu.findItem(R.id.refresh);
		refreash_menu.setVisible(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.add:

			openDialogToAddBoardingPass();
			return true;
		case R.id.delete:
			// openSearch();
			// item.setVisible(false);
			return true;
		case R.id.share:
			// openSearch();
			// item.setVisible(false);
			shareApp();
			return true;
		case R.id.about:
			if (lastselectedposition == 0) {
				fragHome.startFragmentAbout();
			} else {
				displayView(3);

			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void shareApp() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent
				.putExtra(Intent.EXTRA_TEXT,
						"Hey I am using SeatUnity. This is a nice app to store boarding pass and to communicate with seatmates.");
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share_via)));
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(false);

		//
		return super.onPrepareOptionsMenu(menu);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void displayView(int position) {

		lastselectedposition = position;
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(MainActivity.this);
		dbInstance.open();
		// ArrayList<BoardingPass> list = (ArrayList<BoardingPass>)
		// dbInstance.retrieveBoardingPassList();
		dbInstance.close();
		// String email = appInstance.getUserCred().getEmail();

		Fragment fragment = null;
		switch (position) {
		case 0:
			fragHome = new HomeListFragment(0);
			fragment = fragHome;
			break;
		case 1:
			fragment = new AccountListFragment();
			break;
		case 2:
			fragment = new PastBoardingPassListFragment();
			break;
		case 3:
			fragHome = new HomeListFragment(3);
			fragment = fragHome;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack("" + position)
					.commit();
			prevselectedposition = position;
			if (position == 3) {
				mDrawerList.setItemChecked(0, true);
				mDrawerList.setSelection(0);
				setTitle(getResources().getString(R.string.about));
			} else {
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				setTitle(navMenuTitles[position]);
			}

			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Log.e("main", "onPostCreate");

		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Log.e("main", "onConfigurationChanged");
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void close() {
		finisssh();

	}

	public void openDialogToAddBoardingPass() {
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_boarding_passs);
		img_from_camera = (ImageView) dialog.findViewById(R.id.img_from_camera);
		img_from_sdcard = (ImageView) dialog.findViewById(R.id.img_from_sdcard);
		tv_add_boardingpasswith_camera = (TextView) dialog.findViewById(R.id.tv_add_boardingpasswith_camera);
		tv_add_fromsdcard = (TextView) dialog.findViewById(R.id.tv_add_fromsdcard);
		vw_bottom = (RelativeLayout) dialog.findViewById(R.id.re_bottom_holder);
		vw_bottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		tv_add_fromsdcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onclicksdcard();
				dialog.cancel();
			}
		});
		img_from_sdcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onclicksdcard();
				dialog.cancel();
			}
		});

		tv_add_boardingpasswith_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onclickacamera();
				dialog.cancel();
			}
		});
		img_from_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onclickacamera();
				dialog.cancel();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}

	public void onclicksdcard() {
		Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
		startActivityForResult(intent, SCANBOARDINGPASSFROMSDCARD);

	}

	public void onclickacamera() {
		Intent intent = new Intent("com.touhiDroid.android.SCAN");
		intent.putExtra(Intents.Scan.FORMATS, Intents.Scan.AZTEC_MODE + "," + Intents.Scan.PDF417_MODE + ","
				+ Intents.Scan.QR_CODE_MODE + "," + Intents.Scan.DATA_MATRIX_MODE);
		startActivityForResult(intent, BARCODESCANFROMDIRECT);
	}

	public void showalert(final String filename) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
		alertDialogBuilder
				.setMessage(getResources().getString(R.string.txt_startscan_forboardingpass))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						if (Constants.isImage(filename)) {
							Bitmap bitmap = BitmapFactory.decodeFile(filename);
							scanBarcodeFromImage(bitmap);
							// Toast.makeText(MainActivity.this,
							// "ab "+bitmap.getHeight(), 2000).show();
						} else if (Constants.isPdf(filename)) {
							GetBoardingPassFromPDF(filename);
						} else if (Constants.isPkPass(filename)) {
							try {

								String boardingpass = PkpassReader.getPassbookBarcodeString(filename);
								JSONObject job = new JSONObject(boardingpass);
								saveScannedBoardingPasstodatabes(job.getString("message"), job.getString("format"));
							} catch (JSONException e) {
								Toast.makeText(MainActivity.this,
										getResources().getString(R.string.txt_invalid_borading_pass),
										Toast.LENGTH_SHORT).show();
							}
						}

					}
				})
				.setNegativeButton(getResources().getString(R.string.txt_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								MainActivity.this.finish();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == BARCODESCANFROMDIRECT) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d("result", contents + " " + format);
				saveScannedBoardingPasstodatabes(contents, format);
			} else if (resultCode == RESULT_CANCELED) {
			}
		}

		else if (requestCode == SCANBOARDINGPASSFROMSDCARD) {
			if (resultCode == RESULT_OK) {
				int format = intent.getIntExtra(FilePickerActivity.FILE_FORMAT_KEY, 0);
				String filepath = intent.getStringExtra(FilePickerActivity.FILE_PATH);
				if (format == FilePickerActivity.PDF_FILE) {
					GetBoardingPassFromPDF(filepath);
				} else if (format == FilePickerActivity.PASSBOOK_FILE) {
					try {
						String boardingpass = PkpassReader.getPassbookBarcodeString(filepath);
						JSONObject job = new JSONObject(boardingpass);
						saveScannedBoardingPasstodatabes(job.getString("message"), job.getString("format"));
					} catch (JSONException e) {
					}
				} else if (format == FilePickerActivity.IMAGE_FILE) {
					Bitmap bitmap = BitmapFactory.decodeFile(filepath);

					Matrix matrix_NINTY = new Matrix();
					matrix_NINTY.postRotate(90);

					Matrix matrix_ONE80 = new Matrix();
					matrix_ONE80.postRotate(180);

					Matrix matrix_TWO70 = new Matrix();
					matrix_TWO70.postRotate(270);

					// rotated = Bitmap.createBitmap(original, 0, 0,
					// original.getWidth(), original.getHeight(),
					// matrix, true);

					boolean scanstatus = scanBarcodeFromImage(bitmap);
					if (scanstatus) {
						// Toast.makeText(MainActivity.this, "scanstatus",
						// Toast.LENGTH_SHORT).show();
					} else if (scanBarcodeFromImage(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix_NINTY, true))) {
						// Toast.makeText(MainActivity.this, "matrix_NINTY",
						// Toast.LENGTH_SHORT).show();

					} else if (scanBarcodeFromImage(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix_ONE80, true))) {
						// Log.e("matrix_NINTY", "matrix_ONE80");
						// Toast.makeText(MainActivity.this, "matrix_ONE80",
						// Toast.LENGTH_SHORT).show();

					} else if (scanBarcodeFromImage(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix_TWO70, true))) {
						// Log.e("matrix_TWO70", "matrix_TWO70");
						// Toast.makeText(MainActivity.this, "matrix_TWO70",
						// Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_failed_to_scan),
								Toast.LENGTH_SHORT).show();
					}

				}

			} else if (resultCode == RESULT_CANCELED) {
			}
		} else if (requestCode == SCANBARCODEFROMPDF) {
			if (resultCode == RESULT_OK) {
				String path = intent.getStringExtra("bitmap_file_path");
				getResultFromActivity(requestCode, resultCode, path);
			} else if (resultCode == RESULT_CANCELED) {
			}

		}
	}

	public boolean scanBarcodeFromImage(Bitmap bmap) {
		boolean scansuccess = false;
		Bitmap bMap = bmap;

		try {
			int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
			bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
			LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Reader reader = new MultiFormatReader();// use this otherwise
			try {
				Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
				decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
				decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
				Result result = reader.decode(bitmap, decodeHints);
				Log.e("text", result.getText());
				Log.e("format", result.getBarcodeFormat().toString());

				saveScannedBoardingPasstodatabes(result.getText(), result.getBarcodeFormat().toString());
				scansuccess = true;

			} catch (NotFoundException e) {
				scansuccess = false;

				e.printStackTrace();
			} catch (ChecksumException e) {
				scansuccess = false;

				e.printStackTrace();
			} catch (FormatException e) {
				e.printStackTrace();
				scansuccess = false;

			} catch (NullPointerException e) {
				scansuccess = false;
				e.printStackTrace();

			}
		} catch (Exception e) {
			scansuccess = false;
			e.printStackTrace();
		}
		return scansuccess;
	}

	public void GetBoardingPassFromPDF(String filepath) {
		Intent intent = new Intent(MainActivity.this, MuPDFActivity.class);
		intent.setAction("com.touhiDroid.PDF.GET_BITMAP");
		intent.setData(Uri.parse(filepath));
		startActivityForResult(intent, SCANBARCODEFROMPDF);
	}

	public void saveScannedBoardingPasstodatabes(String contents, String format) {
		//

		if ((contents.length() > 60) && (contents.charAt(0) == 'M')) {
			BoardingPassParser boardingpassparser = new BoardingPassParser(contents, format);
			boardingPass = boardingpassparser.getBoardingpass();

			if (!appInstance.isRememberMe()) {
				Log.e("notlogin", contents);
				setBoardingpassInLocalDB();
			} else {
				if (Constants.isOnline(MainActivity.this)) {
					SaveboardingPasstoServer(boardingPass);

				} else {
					Log.e("nonetlogin", contents);
					setBoardingpassInLocalDB();
				}
			}
		} else {
			Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_invalid_borading_pass),
					Toast.LENGTH_SHORT).show();
		}

	}

	public void SaveboardingPasstoServer(BoardingPass bpass) {
		needtoReusedBoardingPass = bpass;
		String bpassdata = "";
		bpassdata = getJsonObjet(bpass);
		AsyncaTaskApiCall change_pass = new AsyncaTaskApiCall(MainActivity.this, bpassdata, MainActivity.this,

		"newbp", Constants.REQUEST_TYPE_POST);
		change_pass.execute();
	}

	public String getJsonObjet(BoardingPass bpass) {

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("version", "1");
			loginObj.put("stringform", bpass.getStringform());
			loginObj.put("firstname", bpass.getFirstname());
			loginObj.put("lastname", bpass.getLastname());
			loginObj.put("PNR", bpass.getPNR());
			loginObj.put("travel_from", bpass.getTravel_from());
			loginObj.put("travel_to", bpass.getTravel_to());
			loginObj.put("carrier", bpass.getCarrier());
			loginObj.put("flight_no", bpass.getFlight_no());
			loginObj.put("julian_date", bpass.getJulian_date());
			loginObj.put("compartment_code", bpass.getCompartment_code());
			loginObj.put("seat", bpass.getSeat());
			loginObj.put("departure", bpass.getDeparture());
			loginObj.put("arrival", bpass.getArrival());
			loginObj.put("year", "2014");
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void setBoardingpassInLocalDB() {
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(MainActivity.this);
		dbInstance.open();
		dbInstance.insertOrUpdateBoardingPass(boardingPass);
		// ArrayList<BoardingPass>list=(ArrayList<BoardingPass>)
		// dbInstance.retrieveBoardingPassList();
		dbInstance.close();
		Calendar c = Calendar.getInstance();
		int dayofyear = c.get(Calendar.DAY_OF_YEAR);
		int ju_date = Integer.parseInt(boardingPass.getJulian_date());
		if ((ju_date < dayofyear)) {
			displayView(2);

		} else {
			displayView(0);
		}
	}

	public void getResultFromActivity(int requestCode, int resultCode, String path) {
		File f = new File(path);
		Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
		if (bitmap != null) {
			Matrix matrix_NINTY = new Matrix();
			matrix_NINTY.postRotate(90);

			Matrix matrix_ONE80 = new Matrix();
			matrix_ONE80.postRotate(180);

			Matrix matrix_TWO70 = new Matrix();
			matrix_TWO70.postRotate(270);

			// rotated = Bitmap.createBitmap(original, 0, 0,
			// original.getWidth(), original.getHeight(),
			// matrix, true);

			boolean scanstatus = scanBarcodeFromImage(bitmap);
			if (scanstatus) {
				// Toast.makeText(MainActivity.this, "scanstatus",
				// Toast.LENGTH_SHORT).show();
			} else if (scanBarcodeFromImage(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
					matrix_NINTY, true))) {
				// Toast.makeText(MainActivity.this, "matrix_NINTY",
				// Toast.LENGTH_SHORT).show();

			} else if (scanBarcodeFromImage(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
					matrix_ONE80, true))) {
				// Log.e("matrix_NINTY", "matrix_ONE80");
				// Toast.makeText(MainActivity.this, "matrix_ONE80",
				// Toast.LENGTH_SHORT).show();

			} else if (scanBarcodeFromImage(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
					matrix_TWO70, true))) {
				// Log.e("matrix_TWO70", "matrix_TWO70");
				// Toast.makeText(MainActivity.this, "matrix_TWO70",
				// Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_failed_to_scan),
						Toast.LENGTH_SHORT).show();
			}

		}
		// else{
		// Toast.makeText(MainActivity.this,"Not working",
		// Toast.LENGTH_SHORT).show();
		// }
		if (f.exists())
			f.delete();
	}

	public void finisssh() {
		super.onBackPressed();
	}

	@Override
	public void onBackPressed() {
		activeFragment.onBackPressed();
	}

	@Override
	public void responseOk(JSONObject job) {
		// addBoardingPassonBackendSuccess(job);
		String success;
		try {
			success = job.getString("success");
			if (success.equals("true")) {
				String id = job.getString("id");
				boardingPass.setId(id);
				// Context context;

				if (MainActivity.this == null) {

				} else {
					Toast.makeText(MainActivity.this,
							getResources().getString(R.string.txt_boarding_pass_added_successfully), Toast.LENGTH_SHORT)
							.show();
				}
				setBoardingpassInLocalDB();

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
				AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(MainActivity.this, loginData,
						MainActivity.this, "login", Constants.REQUEST_TYPE_POST, true);
				log_in_lisenar.execute();
			} else if (code.equals("x01")) {
				Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_failedto_add_boardingpasses),
						Toast.LENGTH_SHORT).show();
				setBoardingpassInLocalDB();
			} else {
				Toast.makeText(MainActivity.this, job.getString("message"), Toast.LENGTH_SHORT).show();
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
				String bpassdata = "";
				bpassdata = getJsonObjet(needtoReusedBoardingPass);
				AsyncaTaskApiCall change_pass = new AsyncaTaskApiCall(MainActivity.this, bpassdata, MainActivity.this,

				"newbp", Constants.REQUEST_TYPE_POST);
				change_pass.execute();

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void LoginFailed(JSONObject job) {
		try {
			JSONObject joberror = new JSONObject(job.getString("error"));
			// String code = joberror.getString("code");
			Constants.setAllFlagFalse();
			String message = joberror.getString("message");
			Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}