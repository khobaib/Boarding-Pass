package com.seatunity.boardingpass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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

import com.artifex.mupdfdemo.ChoosePDFActivity;
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
import com.seatunity.boardingpass.fragment.FragmentAbout;
import com.seatunity.boardingpass.fragment.FragmentGetBoardingPasseFromBackend;
import com.seatunity.boardingpass.fragment.HomeListFragment;
import com.seatunity.boardingpass.fragment.PastBoardingPassListFragment;
import com.seatunity.boardingpass.fragment.TabFragment;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.interfaces.CollapseClassSelectionList;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.boardingpass.utilty.PkpassReader;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassParser;
import com.seatunity.model.NavDrawerItem;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import com.touhiDroid.filepicker.FilePickerActivity;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements CallBackApiCall {

	private final String TAG = this.getClass().getSimpleName();

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
	private BoardingPassApplication appInstance;
	@SuppressWarnings("unused")
	private FragmentManager fragmentManager;
	int lastselectedposition = -1;
	private BoardingPass boardingPass, needtoReusedBoardingPass;
	private HomeListFragment fragHome;
	int prevSelectedPosition = -2;
	private ImageView img_from_camera, img_from_sdcard;
	private TextView tv_add_boardingpasswith_camera, tv_add_fromsdcard;
	private RelativeLayout vw_bottom;
	// private MainActivity lisenar;
	private int BARCODE_SCAN_FROM_CAMERA = 0;
	private int SCAN_BOARDING_PASS_FROM_SD_CARD = 10;
	// private int SCANBOARDINGPASSFROMSDCAdIMAGE_FIRST_ROATAION = 101;
	// private int SCANBOARDINGPASSFROMSDCAdIMAGE_SECOND_ROATAION = 102;
	// private int SCANBOARDINGPASSFROMSDCAdIMAGE_THIIRD_ROATAION = 10;

	int SCAN_BARCODE_FROM_PDF = 12;
	// private ProgressDialog progDialog;

	private static CollapseClassSelectionList ccsListenerSeatMetView;

	/**
	 * This method sets a custom-listview to the overflow-menu.
	 */
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

		// First of all, check for hook-intent

		getOverflowMenu();

		ImageView icon = (ImageView) findViewById(android.R.id.home);
		FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon.getLayoutParams();
		iconLp.leftMargin = iconLp.rightMargin = 20;
		icon.setLayoutParams(iconLp);
		fragmentManager = getFragmentManager();
		appInstance = (BoardingPassApplication) getApplication();

		mTitle = mDrawerTitle = getTitle();

		initDrawerAndOtherFields(false);
		boolean isHookSuccessful = checkAndActAsHookedCall();
		BoardingPassApplication.setHookCallMode(isHookSuccessful);
	}

	/**
	 * Initializes navigation-drawer items, adapter & selected item as well as
	 * fragment.
	 */
	private void initDrawerAndOtherFields(boolean isHookSuccessful) {
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
		adapter = new NavDrawerListAdapter(MainActivity.this, getApplicationContext(), appInstance);
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
		if (!isHookSuccessful)
			displayView(0);
		// }
	}

	/**
	 * This metho checks for hook call from the file-browsing & saves any valid
	 * boarding-pass from the selected image, pdf or pkpass file.
	 * 
	 * @return
	 */
	private boolean checkAndActAsHookedCall() {

		Log.i(TAG, "\n.\nHooking app data ... \n.\n.");
		try {
			Intent intent = getIntent();
			if (intent == null) {
				Log.e(TAG, "Hooked intent is null");
				return false;
			}
			Uri u = intent.getData();
			if (u == null) {
				Log.e(TAG, "Hooked intent data as URI is null");
				return false;
			}
			String scheme = u.getScheme();

			if (scheme.equals(ContentResolver.SCHEME_FILE)) {
				String filePath = intent.getData().toString();
				filePath = filePath.replace("file://", "");
				Log.d(TAG, "Got file path: " + filePath);
				if ((!filePath.equals(null)) && (!filePath.equals(""))) {
					if (Constants.isImage(filePath)) {
						Log.d(TAG, "onCreate : External intent received for an image file: " + filePath);
						Bitmap bitmap = BitmapFactory.decodeFile(filePath);
						scanBPassFromBmp(bitmap);
						return true;
					} else if (Constants.isPdf(filePath)) {
						Log.d(TAG, "onCreate : External intent received for PDF file: " + filePath);
						getBoardingPassFromPDF(filePath);
						return true;
					} else if (Constants.isPkPass(filePath)) {
						Log.d(TAG, "onCreate : External intent received for pkpass file: " + filePath);
						try {
							String boardingpass = PkpassReader.getPassbookBarcodeString(filePath);
							JSONObject job = new JSONObject(boardingpass);
							String m = job.getString("message");
							String f = job.getString("format");
							Log.e(TAG, "Barcode Message: " + m + "\nBarcode Format: " + f);
							saveScannedBoardingPassToDB(m, f);
							return true;
						} catch (JSONException e) {
							Log.e(TAG, getResources().getString(R.string.txt_invalid_borading_pass));
							Toast.makeText(MainActivity.this,
									getResources().getString(R.string.txt_invalid_borading_pass), Toast.LENGTH_SHORT)
									.show();
							return false;
						}
					} else
						Log.e(TAG, "\nUnknown\nFile\nType\nFound");
					return false;
				} else
					Log.e(TAG, "Hooked file path is null/empty");
				return false;
			} else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
				// TODO
				try {
					ContentResolver cr = getContentResolver();
					String mimeType = cr.getType(u);
					AssetFileDescriptor afd = cr.openAssetFileDescriptor(u, "r");
					long length = afd.getLength();
					byte[] filedata = new byte[(int) length];
					InputStream is = cr.openInputStream(u);
					if (is == null) {
						Log.e(TAG, "Input stream of the content resolver is null");
						return false;
					}
					try {
						// is.read(filedata, 0, (int) length);
						String filePath = Constants.APP_DIRECTORY + "/temp.thd";
						File f = new File(filePath);
						if (f.mkdirs() && f.exists() && f.delete() && f.createNewFile())
							Log.i(TAG, "New temp.thd file created as data-station");
						FileOutputStream fout = new FileOutputStream(f);
						int count = 0;
						while ((count = is.read(filedata)) != -1) {
							fout.write(filedata, 0, count);
						}
						fout.close();
						is.close();
						Log.i(TAG, "file data is read of length: " + length);
						// Util.loadOTDRFileFromByteArray(filedata);
						if (mimeType.endsWith("pdf")) {
							Log.d(TAG, "PDF File found & downloaded at location: " + filePath);
							getBoardingPassFromPDF(filePath);
							return true;
						} else if (mimeType.endsWith("pkpass")) {
							Log.d(TAG, "Pkpass File found & downloaded at location: " + filePath);
							try {
								String boardingpass = PkpassReader.getPassbookBarcodeString(filePath);
								JSONObject job = new JSONObject(boardingpass);
								String m = job.getString("message");
								String format = job.getString("format");
								Log.e(TAG, "Barcode Message: " + m + "\nBarcode Format: " + format);
								saveScannedBoardingPassToDB(m, format);
								return true;
							} catch (JSONException e) {
								Log.e(TAG, getResources().getString(R.string.txt_invalid_borading_pass));
								Toast.makeText(MainActivity.this,
										getResources().getString(R.string.txt_invalid_borading_pass),
										Toast.LENGTH_SHORT).show();
								return false;
							}
						} else if (mimeType.startsWith("image")) {
							// TODO Handle image hooks
							Log.d(TAG, "onCreate : External intent received for an image file: " + filePath);
							Bitmap bitmap = BitmapFactory.decodeFile(filePath);
							scanBPassFromBmp(bitmap);
							return true;
						} else {
							Log.d(TAG, "Unknown File found with mimetype=" + mimeType + " & downloaded at location: "
									+ filePath);
							return false;
						}
						// if(f.delete())
						// Log.i(TAG,"Temporary file deleted successfully.");
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				Log.e(TAG, "Scheme didn't match! :: " + scheme);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "There wasn't any hook call, just a normal app-open");
			return false;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(MainActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// lisenar = this;
		if (Constants.SELECTEDBOARDINGPASSPOSITION == 1) {
			displayView(1);
			Constants.SELECTEDBOARDINGPASSPOSITION = 0;
		}
		// displayView(Constants.SELECTEDPOSITION);
	}

	/**
	 * Class to provide any instance for any drawer-item click. It only
	 * overrides the {@code onItemClick() } method of
	 * {@link ListView.OnItemClickListener }
	 * 
	 * @author Sumon
	 * 
	 */
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

	public static void setCollapseListForSeatMetListener(CollapseClassSelectionList ccsListener) {
		ccsListenerSeatMetView = ccsListener;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		if (item.getItemId() == android.R.id.home) {
			return mDrawerToggle.onOptionsItemSelected(item);
		}
		if (ccsListenerSeatMetView != null)
			ccsListenerSeatMetView.collapseList(true);
		switch (item.getItemId()) {
		case R.id.add:
			openDialogToAddBoardingPass();
			return true;
		case R.id.delete:
			// openSearch();
			// item.setVisible(false);
			return true;
			// case R.id.share:
			// openSearch();
			// item.setVisible(false);
			// shareApp();
			// return true;
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

	// /**
	// * The method calls the intent to share the application with
	// * {@link Intent.ACTION_SEND} flag & share-text as : <br>
	// * {@code
	// "Hey I am using SeatUnity. This is a nice app to store boarding pass and to communicate with seatmates."
	// }
	// */
	// private void shareApp() {
	// Intent sendIntent = new Intent();
	// sendIntent.setAction(Intent.ACTION_SEND);
	// sendIntent
	// .putExtra(Intent.EXTRA_TEXT,
	// "Hey I am using SeatUnity. This is a nice app to store boarding pass and to communicate with seatmates.");
	// sendIntent.setType("text/plain");
	// startActivity(Intent.createChooser(sendIntent,
	// getResources().getString(R.string.share_via)));
	// }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(false);

		//
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Opens the selected fragment as position. The fragments according to
	 * position are as below: <br>
	 * 0 -> {@link HomeListFragment} (0 for the
	 * {@link FragmentGetBoardingPasseFromBackend} )<br>
	 * 1 -> {@link AccountListFragment}<br>
	 * 2 -> {@link PastBoardingPassListFragment}<br>
	 * 3 -> {@link HomeListFragment} (3 for the {@link FragmentAbout } ) <br>
	 * 
	 * @param position
	 *            as in the drawer-list to choose the fragment as described
	 *            above.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void displayView(int position) {

		// Log.d(TAG, "displayView : position=" + position);
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
			prevSelectedPosition = position;
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
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Calls the {@code finisssh() } method.
	 */
	public void close() {
		finisssh();
	}

	/**
	 * Opens the dialog to add new boarding-pass, when clicked on the "+" button
	 * at the action-bar.
	 */
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
				onClickCamera();
				dialog.cancel();
			}
		});
		img_from_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onClickCamera();
				dialog.cancel();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}

	/**
	 * Calls the {@link FilePickerActivity} to pick any pdf, pass-book or image
	 * file to scan for a boarding-pass.
	 */
	private void onclicksdcard() {
		Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
		startActivityForResult(intent, SCAN_BOARDING_PASS_FROM_SD_CARD);
	}

	/**
	 * Calls the MuPDF library's custom-intent to scan a barcode with camera.
	 */
	private void onClickCamera() {
		Intent intent = new Intent("com.touhiDroid.android.SCAN");
		intent.putExtra(Intents.Scan.FORMATS, Intents.Scan.AZTEC_MODE + "," + Intents.Scan.PDF417_MODE + ","
				+ Intents.Scan.QR_CODE_MODE + "," + Intents.Scan.DATA_MATRIX_MODE);
		startActivityForResult(intent, BARCODE_SCAN_FROM_CAMERA);
	}

	// /**
	// * When user selects any file from any file-browser, the app can be chosen
	// * from the chooser & this activity is opened with this method called,
	// which
	// * actually decides the file-type (image/pdf/pkpass) & prompts the user to
	// * scan it for any boarding-pass.
	// *
	// * @param filename
	// * The chosen file's absolute name.
	// */
	// private void showAlert(final String filename) {
	// AlertDialog.Builder alertDialogBuilder = new
	// AlertDialog.Builder(MainActivity.this);
	// alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
	// alertDialogBuilder
	// .setMessage(getResources().getString(R.string.txt_startscan_forboardingpass))
	// .setCancelable(false)
	// .setPositiveButton(getResources().getString(R.string.txt_ok), new
	// DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// dialog.cancel();
	// if (Constants.isImage(filename)) {
	// Bitmap bitmap = BitmapFactory.decodeFile(filename);
	// scanAndSaveBPassFromBmp(bitmap);
	// } else if (Constants.isPdf(filename)) {
	// getBoardingPassFromPDF(filename);
	// } else if (Constants.isPkPass(filename)) {
	// try {
	// String boardingpass = PkpassReader.getPassbookBarcodeString(filename);
	// JSONObject job = new JSONObject(boardingpass);
	// saveScannedBoardingPassToDB(job.getString("message"),
	// job.getString("format"));
	// } catch (JSONException e) {
	// Toast.makeText(MainActivity.this,
	// getResources().getString(R.string.txt_invalid_borading_pass),
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	//
	// }
	// })
	// .setNegativeButton(getResources().getString(R.string.txt_cancel),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// MainActivity.this.finish();
	// }
	// });
	// AlertDialog alertDialog = alertDialogBuilder.create();
	// alertDialog.show();
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			if (requestCode == BARCODE_SCAN_FROM_CAMERA) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.d("result", contents + " " + format);
				saveScannedBoardingPassToDB(contents, format);
			} else if (requestCode == SCAN_BOARDING_PASS_FROM_SD_CARD) {
				int format = intent.getIntExtra(FilePickerActivity.FILE_FORMAT_KEY, 0);
				String filepath = intent.getStringExtra(FilePickerActivity.FILE_PATH);
				if (format == FilePickerActivity.PDF_FILE) {
					getBoardingPassFromPDF(filepath);
				} else if (format == FilePickerActivity.PASSBOOK_FILE) {
					try {
						String boardingpass = PkpassReader.getPassbookBarcodeString(filepath);
						JSONObject job = new JSONObject(boardingpass);
						saveScannedBoardingPassToDB(job.getString("message"), job.getString("format"));
					} catch (JSONException e) {
						e.printStackTrace();
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

					boolean scanstatus = scanAndSaveBPassFromBmp(bitmap);
					if (scanstatus) {
						// Toast.makeText(MainActivity.this, "scanstatus",
						// Toast.LENGTH_SHORT).show();
					} else if (scanAndSaveBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix_NINTY, true))) {
						// Toast.makeText(MainActivity.this, "matrix_NINTY",
						// Toast.LENGTH_SHORT).show();

					} else if (scanAndSaveBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix_ONE80, true))) {
						// Log.e("matrix_NINTY", "matrix_ONE80");
						// Toast.makeText(MainActivity.this, "matrix_ONE80",
						// Toast.LENGTH_SHORT).show();

					} else if (scanAndSaveBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix_TWO70, true))) {
						// Log.e("matrix_TWO70", "matrix_TWO70");
						// Toast.makeText(MainActivity.this, "matrix_TWO70",
						// Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_failed_to_scan),
								Toast.LENGTH_SHORT).show();
					}
				}
			} else if (requestCode == SCAN_BARCODE_FROM_PDF) {
				String path = intent.getStringExtra("bitmap_file_path");
				Log.d(TAG, "Result got from MuPDF: file_path = " + path);
				// getResultFromActivity(requestCode, resultCode, path);
				// showProgDialog("Scanning the PDF file for boarding pass...");
				new BarcodeScanTask().execute(path);
			}
		}
	}

	// private void showProgDialog(final String message) {
	// // runOnUiThread(new Runnable() {
	// // @Override
	// // public void run() {
	// progDialog = new ProgressDialog(MainActivity.this);
	// progDialog.setContentView(R.layout.progress_content);
	// ((TextView) progDialog.findViewById(R.id.tv_message)).setText(message);
	// if (!progDialog.isShowing())
	// progDialog.show();
	// // }
	// // });
	// }

	private boolean scanAndSaveBPassFromBmp(Bitmap bmap) {
		Result scanResult = scanBPassFromBmp(bmap);
		if (scanResult != null)
			saveScannedBoardingPassToDB(scanResult.getText().toString(), scanResult.getBarcodeFormat().toString());
		return scanResult != null;
	}

	/**
	 * Scans the passed bitmap for any barcode consisting of any boarding-pass
	 * object.
	 * 
	 * @param bmap
	 * @return
	 */
	private Result scanBPassFromBmp(Bitmap bmap) {
		Log.d(TAG, "Scanning barcode ...");
		Result result = null;
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
				result = reader.decode(bitmap, decodeHints);
				Log.i("text", result.getText());
				Log.i("format", result.getBarcodeFormat().toString());

				saveScannedBoardingPassToDB(result.getText().toString(), result.getBarcodeFormat().toString());
				// scansuccess = true;
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (ChecksumException e) {
				e.printStackTrace();
			} catch (FormatException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Calls {@link MuPDFActivity } with the passed file's path, which will
	 * return the temporary image-file's path formed from the passed PDF file.
	 * 
	 * @param filepath
	 */
	private void getBoardingPassFromPDF(String filepath) {
		Intent intent = new Intent(MainActivity.this, MuPDFActivity.class);
		// "com.touhiDroid.PDF.GET_BITMAP"
		intent.setAction(ChoosePDFActivity.GET_BITMAP);
		intent.setData(Uri.parse(filepath));
		startActivityForResult(intent, SCAN_BARCODE_FROM_PDF);
	}

	/**
	 * Saves the boarding-pass object to the database.
	 * 
	 * @param contents
	 * @param format
	 */
	private void saveScannedBoardingPassToDB(String contents, String format) {
		//
		if ((contents.length() > 60) && (contents.charAt(0) == 'M')) {
			BoardingPassParser boardingpassparser = new BoardingPassParser(contents, format);
			boardingPass = boardingpassparser.getBoardingpass();

			if (appInstance == null)
				appInstance = (BoardingPassApplication) getApplication();
			if (!appInstance.isRememberMe()) {
				Log.e(TAG, "User not logged in : Contents=" + contents);
				saveBoardingpassInLocalDB();
			} else {
				if (Constants.isOnline(MainActivity.this)) {
					Log.i(TAG, "Saving boardingpass in local-db & server");
					saveBoardingpassInLocalDB();
					// TODO Check - will it update the boarding pass in
					// the remote-server in any later phase, rather than
					// instantly?
					if (Looper.myLooper() == Looper.getMainLooper())
						saveBoardingPasstoServer(boardingPass);
				} else {
					Log.e("No Internet", contents);
					saveBoardingpassInLocalDB();
				}
			}
		} else {
			Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_invalid_borading_pass),
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Calls the API to save the passed boarding-pass object in server.
	 * 
	 * @param bpass
	 */
	private void saveBoardingPasstoServer(BoardingPass bpass) {
		Log.d(TAG, "saveBoardingPasstoServer : inside");
		needtoReusedBoardingPass = bpass;
		String bpassdata = "";
		bpassdata = getJsonOfBoardingPass(bpass);
		AsyncaTaskApiCall change_pass = new AsyncaTaskApiCall(MainActivity.this, bpassdata, MainActivity.this, "newbp",
				Constants.REQUEST_TYPE_POST);
		change_pass.execute();
	}

	/**
	 * Forms a JSON-string to send boarding-pass data to save at the server.
	 * 
	 * @param bpass
	 *            The boarding-pass to save in the server.
	 * @return
	 */
	private String getJsonOfBoardingPass(BoardingPass bpass) {
		JSONObject bPassObj = new JSONObject();
		try {
			bPassObj.put("token", appInstance.getUserCred().getToken());
			bPassObj.put("version", "1");
			bPassObj.put("stringform", bpass.getStringform());
			bPassObj.put("firstname", bpass.getFirstname());
			bPassObj.put("lastname", bpass.getLastname());
			bPassObj.put("PNR", bpass.getPNR());
			bPassObj.put("travel_from", bpass.getTravel_from());
			bPassObj.put("travel_to", bpass.getTravel_to());
			bPassObj.put("carrier", bpass.getCarrier());
			bPassObj.put("flight_no", bpass.getFlight_no());
			bPassObj.put("julian_date", bpass.getJulian_date().trim());
			bPassObj.put("compartment_code", bpass.getCompartment_code());
			bPassObj.put("seat", bpass.getSeat());
			bPassObj.put("departure", bpass.getDeparture());
			bPassObj.put("arrival", bpass.getArrival());
			bPassObj.put("year", "2014");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bPassObj.toString();
	}

	/**
	 * Saves the boarding-pass data as in the global variable
	 * {@code boardingPass} in the local DB.
	 */
	private void saveBoardingpassInLocalDB() {
		Log.d(TAG, "saveBoardingpassInLocalDB : inside");
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(MainActivity.this);
		dbInstance.open();
		dbInstance.insertOrUpdateBoardingPass(boardingPass);
		// ArrayList<BoardingPass>list=(ArrayList<BoardingPass>)
		// dbInstance.retrieveBoardingPassList();
		dbInstance.close();
		boolean isMainThread = (Looper.myLooper() == Looper.getMainLooper());
		Log.d(TAG, "Calling displayView from ui thread, where currnt thread is-main=" + (isMainThread));
		if (isMainThread) {
			if (Constants.isBPassDateInFuture(boardingPass.getJulian_date().trim())) {
				Log.i(TAG, "Calling displayView(0) : Future B-pass list");
				displayView(0);
			} else {
				Log.i(TAG, "Calling displayView(2) : Past B-pass list");
				displayView(2);
			}
		} else
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (Constants.isBPassDateInFuture(boardingPass.getJulian_date().trim())) {
						Log.i(TAG, "UI Thread run to call displayView(0) : Future B-pass list");
						displayView(0);
					} else {
						Log.i(TAG, "UI Thread run to call displayView(2) : Past B-pass list");
						displayView(2);
					}
				}
			});
	}

	/**
	 * Method to save the read barcode image (from PDF) as a boarding-pass in
	 * the local DB.<br>
	 * This method rotates the read-image in 3 other orthogonal angels & tries
	 * to read the barcode in all the 4 possible perspectives.
	 * 
	 * @param path
	 *            The bitmap-image's file path. The file is deleted after
	 *            processing.
	 */
	@SuppressWarnings("unused")
	private void getResultFromActivity(String path) {
		ProgressDialog pd = new ProgressDialog(MainActivity.this);
		pd.show();
		File f = new File(path);
		Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
		if (bitmap != null) {
			Matrix matrix_NINTY = new Matrix();
			matrix_NINTY.postRotate(90);
			Matrix matrix_ONE80 = new Matrix();
			matrix_ONE80.postRotate(180);
			Matrix matrix_TWO70 = new Matrix();
			matrix_TWO70.postRotate(270);

			if (scanAndSaveBPassFromBmp(bitmap)) {
			} else if (scanAndSaveBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
					matrix_NINTY, true))) {

			} else if (scanAndSaveBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
					matrix_ONE80, true))) {

			} else if (scanAndSaveBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
					matrix_TWO70, true))) {

			} else {
				Toast.makeText(MainActivity.this, getResources().getString(R.string.txt_failed_to_scan),
						Toast.LENGTH_SHORT).show();
			}
		}
		if (f.exists())
			f.delete();
		if (pd.isShowing())
			pd.cancel();
	}

	/**
	 * Calls the {@code onBackPressed() } method of the super class.
	 */
	private void finisssh() {
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
				saveBoardingpassInLocalDB();
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
				saveBoardingpassInLocalDB();
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
				bpassdata = getJsonOfBoardingPass(needtoReusedBoardingPass);
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

	@Override
	public void responseFailure(ServerResponse response) {
		if (response.getStatus() != 200) {
			BoardingPassApplication.alert(MainActivity.this,
					"Internet connectivity is lost! Please retry the operation.");
		}
	}

	/**
	 * Asynchronous-task to save the read barcode image (from PDF) as a
	 * boarding-pass in the local DB.<br>
	 * This method rotates the read-image in 3 other orthogonal angels & tries
	 * to read the barcode in all the 4 possible perspectives.
	 * 
	 * @param path
	 *            The bitmap-image's file path. The file is deleted after
	 *            processing.
	 */
	private class BarcodeScanTask extends AsyncTask<String, Void, Result> {

		@Override
		protected Result doInBackground(String... params) {
			// ProgressDialog pd = new ProgressDialog(MainActivity.this);
			// pd.show();
			String path = params[0];
			File f = new File(path);
			Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
			Result result = null;
			Log.i(TAG, "Bitmap created from the PDF provided barcode at location: " + path);
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

				result = scanBPassFromBmp(bitmap);
				if (result != null) {
					Log.i(TAG, "Real image is scanned!");
				} else if ((result = scanBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix_NINTY, true))) != null) {
					Log.i(TAG, "90 deg. rotated image is scanned!");
				} else if ((result = scanBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix_ONE80, true))) != null) {
					Log.i(TAG, "180 deg. rotated image is scanned!");
				} else if ((result = scanBPassFromBmp(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix_TWO70, true))) != null) {
					Log.i(TAG, "270 deg. rotated image is scanned!");
				} else {
					return null;
				}
			} else {
				Log.e(TAG, "Retrieved bitmap is null!");
			}
			if (f.exists() && f.delete())
				Log.i(TAG, "The PDF-parsed temporary file is deleted safely.");
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			try {
				// if (progDialog.isShowing())
				// progDialog.cancel();
				if (result == null) {
					// MainActivity.this.runOnUiThread(new Runnable() {
					// @Override
					// public void run() {
					Log.e(TAG, "Barcode format is not in boarding-pass format!");
					Toast.makeText(MainActivity.this, "Barcode format is not in boarding-pass format!",
							Toast.LENGTH_SHORT).show();
					// }
					// });
				} else {
					Log.i(TAG, "Boarding pass added successfully.");
					Toast.makeText(MainActivity.this, "Boarding pass added successfully.", Toast.LENGTH_SHORT).show();
					saveScannedBoardingPassToDB(result.getText().toString(), result.getBarcodeFormat().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}