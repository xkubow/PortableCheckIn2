package cz.tsystems.base;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.*;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class FragmentPagerActivity extends FragmentActivity implements TabListener {

//	ViewPager mViewPager;
//	SectionsPagerAdapter mSectionsPagerAdapter;
	private Time stopTime;
	private BaseFragment theFragment;
	FragmentManager fm;
	PortableCheckin app;
	List<Fragment> theFragments = new ArrayList<Fragment>(4);	
	

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
		
			theFragment = (BaseFragment) theFragments.get(getActionBar().getSelectedTab().getPosition());
//			Log.d("FRAGMENT", theFragment.getClass().getSimpleName());
			theFragment.showData(intent);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_pager);
		app = (PortableCheckin)getApplicationContext();
		// uz by mali byt natiahnute vsetky data - mozno siluety a banery este nebudu??
		if(savedInstanceState == null)
			app.loadDefaultCheckin();

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		fm = getSupportFragmentManager();
		
		theFragments.add(new MainActivity());
		theFragments.add(new BodyActivity());
		theFragments.add(new ServiceActivity());
		theFragments.add(new OffersActivity());
		
		actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.car_icon_riadky2));
		actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.car_icon_body2));
		actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.car_icon_sipka2));
		actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.car_icon_nabidka2));

        app.setActualActivity(this);

	}

	private void registerRecaiver() {
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction("recivedData");
		filterSend.addAction("grdResponse");
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filterSend);
	}
	
	public void updateData() {
		theFragment = (BaseFragment) theFragments.get(0);
		if(theFragment != null) {
			theFragment.updateData(null);
		}
	}

/*	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {

				switch ((BaseGridActivity.eGrdTypes) data.getExtras().get("type")) {
				case eGRDPLANZAK:
					loadCheckinData(data);					
					break;				
				default:
					break;
				}
			}
		}

	}
	
	public void loadCheckinData(Intent data)
	{
		Intent msgIntent = new Intent(this, CommunicationService.class);
		msgIntent.putExtra("ACTIONURL", "pchi/DataForCheckIn");
		msgIntent.putExtra("ACTION", "DataForCheckIn");
		if(data.hasExtra("PLANNED_ORDER_ID"))
			msgIntent.putExtra("plannedorderid", data.getStringExtra("PLANNED_ORDER_ID"));
		if(data.hasExtra("CHECKIN_ID"))
			msgIntent.putExtra("checkin_id", data.getStringExtra("CHECKIN_ID"));
		if(data.hasExtra("licenseTag"))
			msgIntent.putExtra("licenseTag", data.getStringExtra("licenseTag"));
		if(data.hasExtra("VIN"))
			msgIntent.putExtra("vin", data.getStringExtra("VIN"));			
		this.startService(msgIntent);
	}*/

	@Override
	protected void onStart() {
		
		if(app.isTakeImage) {
			app.isTakeImage = false;
		} else if(PortableCheckin.user == null) {
			Intent it = new Intent(this, LoginActivity.class);
			it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(it, 1);			
		}else if (stopTime != null) {
			Time now = new Time();
			now.setToNow();
			long backgroundTime = now.toMillis(true) - stopTime.toMillis(true);
			if (backgroundTime > 30) {
				Intent it = new Intent(this, LoginActivity.class);
				it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(it, 1);
			}
			stopTime = null;
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		registerRecaiver();
		super.onResume();
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		stopTime = new Time();
		stopTime.setToNow();
		super.onStop();
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		BaseMenu.show(item, this);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.themenu, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	final int pos = tab.getPosition();		
    	Fragment f = theFragments.get(pos);
    	android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction(); 
    	transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);		    	
    	transaction.replace(R.id.TheFragment, f);
//    	transaction.addToBackStack(null); //nechcem fungujuce Back tlacitko

	   	transaction.commit();			    
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
