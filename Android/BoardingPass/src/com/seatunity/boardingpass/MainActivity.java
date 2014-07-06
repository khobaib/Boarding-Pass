package com.seatunity.boardingpass;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONException;

import com.seatunity.boardingpass.adapter.NavDrawerListAdapter;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.fragment.AccountListFragment;
import com.seatunity.boardingpass.fragment.FragmentAddBoardingPass;
import com.seatunity.boardingpass.fragment.FragmentAddBoardingPassDuringLogin;
import com.seatunity.boardingpass.fragment.FragmentMyAccount;
import com.seatunity.boardingpass.fragment.FragmentBoardingPasses;
import com.seatunity.boardingpass.fragment.FragmentReminder;
import com.seatunity.boardingpass.fragment.HomeFragment;
import com.seatunity.boardingpass.fragment.HomeListFragment;
import com.seatunity.boardingpass.fragment.PastBoardingPassListFragment;
import com.seatunity.boardingpass.fragment.TabFragment;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.PkpassReader;
import com.seatunity.model.BoardingPass;
import com.touhiDroid.filepicker.FilePickerActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	public TabFragment activeFragment;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	BoardingPassApplication appInstance;
	public  Stack<Fragment> fragmentStack;
	public  Stack<Fragment> fragmentStack1;
	public  Stack<Fragment> fragmentStack2;
	public  Stack<String> indicator;
	private FragmentManager fragmentManager;
	int lastselectedposition=-1;
	int prevselectedposition=-2;
	public FragmentAddBoardingPass holder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragmentStack = new Stack<Fragment>();
		fragmentStack1 = new Stack<Fragment>();
		fragmentStack2 = new Stack<Fragment>();
		indicator=new Stack<>();
		fragmentManager = getFragmentManager();
		appInstance =(BoardingPassApplication)getApplication();
		mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		navDrawerItems = new ArrayList<NavDrawerItem>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navMenuIcons.recycle();
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		adapter = new NavDrawerListAdapter(getApplicationContext());
		mDrawerList.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_navigation_drawer_closed, 
				R.string.app_name, 
				R.string.app_name 
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				getActionBar().setIcon(R.drawable.ic_navigation_drawer_closed);
				invalidateOptionsMenu();
			}
			public void onDrawerOpened(View drawerView) {
				getActionBar().setIcon(R.drawable.ic_navigation_drawer_open);
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			displayView(0);
		}
		displayView(0);
	}

	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}	    switch (item.getItemId()) {
		case R.id.add:
			openSearch();
			return true;
		case R.id.delete:
			//openSearch();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public void openSearch(){
		Fragment fragment = new HomeListFragment(1);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack("0").commit();
		mDrawerList.setItemChecked(0, true);
		//mDrawerList.setSelection(0);
	}

	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		if (mDrawerToggle.onOptionsItemSelected(item)) {
	//			return true;
	//		}
	//		switch (item.getItemId()) {
	//		case R.id.action_settings:
	//			return true;
	//		default:
	//			return super.onOptionsItemSelected(item);
	//		}
	//	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void displayView(int position) {
		lastselectedposition=position;
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(MainActivity.this);
		dbInstance.open();
		ArrayList<BoardingPass> list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();

		dbInstance.close();
		String email=appInstance.getUserCred().getEmail();

		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeListFragment(0);
			break;
		case 1:
			fragment = new AccountListFragment();
			break;
		case 2:
			fragment = new PastBoardingPassListFragment();
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
			prevselectedposition=position;
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Toast.makeText(MainActivity.this, "Calling from"+requestCode, 2000).show();
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				Log.d("TAG", "contents: " + contents);
			} else if (resultCode == RESULT_CANCELED) {
			}
		}
		else if (requestCode == 12) {
			if (resultCode == RESULT_OK) {
				String path=data.getStringExtra("bitmap_file_path");
				Log.e("test", "1 "+holder+" "+path );
				holder.getResultFromActivity(requestCode, resultCode, path);
			} 
			else if (resultCode == RESULT_CANCELED) {
			}

		}
	}
	public void close() {
		finisssh();

	}
	
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//
//		Toast.makeText(getActivity(), "Calling"+requestCode, 2000).show();
//		tv_add_boardingpasswith_camera.setText("gefcefckhgdfc");
//		if (requestCode == 0) {
//			if (resultCode == getActivity().RESULT_OK) {
//				String contents = intent.getStringExtra("SCAN_RESULT");
//				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//				saveScannedBoardingPasstodatabes(contents,format);
//			} else if (resultCode == getActivity().RESULT_CANCELED) {
//			}
//		}
//
//		else if (requestCode == 10) {
//			if (resultCode == getActivity().RESULT_OK) {
//				int format=intent.getIntExtra(FilePickerActivity.FILE_FORMAT_KEY, 0);
//				String filepath=intent.getStringExtra(FilePickerActivity.FILE_PATH);
//				if(format==FilePickerActivity.PDF_FILE){
//					GetBoardingPassFromPDF(filepath);
//				}
//				else if(format==FilePickerActivity.PASSBOOK_FILE){
//					try {
//						String boardingpass=PkpassReader.getPassbookBarcodeString(filepath);
//						Log.e("json Object", boardingpass);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				else if(format==FilePickerActivity.IMAGE_FILE){
//					Bitmap bitmap = BitmapFactory.decodeFile(filepath);
//					scanBarcodeFromImage(bitmap);
//				}
//
//			} else if (resultCode == getActivity().RESULT_CANCELED) {
//				Log.i("App","Scan unsuccessful");
//
//			}
//		}
//		else if (requestCode == 12) {
//			if (resultCode == getActivity().RESULT_OK) {
//				
//				File f = new File(intent.getStringExtra("bitmap_file_path"));
//				   Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
//				  
//				
//				if (bmp != null){
//					Log.e("tag", "4");
//					scanBarcodeFromImage(bmp);
//				}
//				 if (f.exists())
//					    f.delete();
//			} else if (resultCode == getActivity().RESULT_CANCELED) {
//			}
//		}
//	}
//

	public void finisssh(){
		super.onBackPressed();
	}
	@Override
	public void onBackPressed() {
		activeFragment.onBackPressed();
	}
}