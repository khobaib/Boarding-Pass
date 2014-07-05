package com.seatunity.boardingpass;

import java.util.ArrayList;
import java.util.Stack;

import com.seatunity.boardingpass.adapter.NavDrawerListAdapter;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.fragment.AccountListFragment;
import com.seatunity.boardingpass.fragment.FragmentAddBoardingPassDuringLogin;
import com.seatunity.boardingpass.fragment.FragmentMyAccount;
import com.seatunity.boardingpass.fragment.FragmentBoardingPasses;
import com.seatunity.boardingpass.fragment.FragmentReminder;
import com.seatunity.boardingpass.fragment.HomeFragment;
import com.seatunity.boardingpass.fragment.HomeListFragment;
import com.seatunity.boardingpass.fragment.PastBoardingPassListFragment;
import com.seatunity.boardingpass.fragment.TabFragment;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.BoardingPass;

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
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		navMenuIcons.recycle();
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		adapter = new NavDrawerListAdapter(getApplicationContext());
		mDrawerList.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
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
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void displayView(int position) {
		// update the main content by replacing fragments
		lastselectedposition=position;
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(MainActivity.this);
		dbInstance.open();
		ArrayList<BoardingPass> list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
		
		dbInstance.close();
		String email=appInstance.getUserCred().getEmail();
		
		Fragment fragment = null;
		switch (position) {
		case 0:
			
			 fragment = new HomeListFragment();
//			fragment=new TabFragment() {
//				
//				@Override
//				public void onBackPressed() {
//					// TODO Auto-generated method stub
//					
//				}
//			};
//			if((email.equals(""))&&(list.size()<1)){
//				fragment = new HomeFragment();
//				
//			}
//			else if((!email.equals(""))&&(list.size()<1)){
//				fragment = new FragmentAddBoardingPassDuringLogin();
//				
//			}
//			else{
//				
//				fragment = new FragmentBoardingPasses();
//				
//
//			}
			
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
			
			
//				
//				FragmentManager fragmentManager = getFragmentManager();
//				FragmentTransaction ft = fragmentManager.beginTransaction();
////				
//				if(position==0){
//					
//					if(fragmentStack.size()<1){
//						fragmentStack.push(fragment);
//						fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
//						
//
//					}
//					else{
//							fragment=fragmentStack.peek();
//							fragmentStack.push(fragment);
//							fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
//						 
//					}
//					Log.e("size", ""+fragmentStack.size());
//
//				}
//				else if(position==1){
//					if(fragmentStack1.size()<1){
//						fragmentStack1.push(fragment);
//						fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
//						
//
//					}
//					else{
//						fragment=fragmentStack1.peek();
//						fragmentStack1.push(fragment);
//						fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
//						
//					}
//				}
//				else if(position==2){
////					if(fragmentStack.size()>1){
//////						fragmentStack2.lastElement().onPause();
//////						ft.hide(fragmentStack2.lastElement());
////
////					}
////					fragmentStack2.push(fragment);
//					if(fragmentStack2.size()<1){
//						fragmentStack2.push(fragment);
//						fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
//						
//
//					}
//					else{
//						fragment=fragmentStack2.peek();
//						//fragmentStack2.push(fragment);
//						fragmentManager.beginTransaction().add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
//						
//						 
//					}
//				}
//				
//				mDrawerList.setItemChecked(position, true);
//				mDrawerList.setSelection(position);
//				setTitle(navMenuTitles[position]);
//				mDrawerLayout.closeDrawer(mDrawerList);
//			}
			
	
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
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				Log.d("TAG", "contents: " + contents);
			} else if (resultCode == RESULT_CANCELED) {
			}
		}
	}
	public void close() {
		finisssh();
		
	}

	public void finisssh(){
		super.onBackPressed();
	}
	@Override
	public void onBackPressed() {
		activeFragment.onBackPressed();
	}
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		super.onBackPressed();
//		getFragmentManager().popBackStack();
//		fm.beginTransaction().add(R.id.frame_container, newFragment).addToBackStack("fragBack").commit();
//		.add(R.id.frame_container, fragment).addToBackStack(""+position).commit();
//	}
//	@Override
//	public void onBackPressed() {
//		Log.e("pos", fragmentStack.size()+" "+fragmentStack1.size()+" "+fragmentStack2.size()+"  "+lastselectedposition);
//		if(lastselectedposition==0){
//			if (fragmentStack.size()>1) {
//				FragmentTransaction ft = fragmentManager.beginTransaction();
//				fragmentStack.lastElement().onPause();
//				ft.remove(fragmentStack.pop());
//				fragmentStack.lastElement().onResume();
//				ft.show(fragmentStack.lastElement());
//				ft.commit();
//
//			} else {
//				super.onBackPressed();
//			}
//			
//		}
//		else if(lastselectedposition==1){
//			if (fragmentStack1.size()>1) {
//				FragmentTransaction ft = fragmentManager.beginTransaction();
//				fragmentStack1.lastElement().onPause();
//				ft.remove(fragmentStack1.pop());
//				fragmentStack1.lastElement().onResume();
//				ft.show(fragmentStack1.lastElement());
//				ft.commit();
//				Log.e("size", ""+fragmentStack.size());
//			} else {
//				super.onBackPressed();
//			}
//			
//		}
//		else if(lastselectedposition==2){
//			if (fragmentStack2.size()>1) {
//				FragmentTransaction ft = fragmentManager.beginTransaction();
//				fragmentStack2.lastElement().onPause();
//				ft.remove(fragmentStack2.pop());
//				fragmentStack2.lastElement().onResume();
//				ft.show(fragmentStack2.lastElement());
//				ft.commit();
//			} else {
//				super.onBackPressed();
//			}
//		
//		}
//
//		
//	}
//	
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		FragmentManager fragmentManager = getFragmentManager();
//		if(fragmentManager.getBackStackEntryCount()<0){
//			super.onBackPressed();
//		}
//		
//	}

}