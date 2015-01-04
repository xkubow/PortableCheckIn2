package cz.tsystems.base;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.DMBrand;
import cz.tsystems.data.DMCheckin;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.dialogs.BaseGridActivity;
import cz.tsystems.portablecheckin.*;

import android.annotation.TargetApi;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FragmentPagerActivity extends Activity implements TabListener {

//	ViewPager mViewPager;
//	SectionsPagerAdapter mSectionsPagerAdapter;
    public static int eGRID_RESULT = 2;
	private Time stopTime;
	private BaseFragment theFragment;
	FragmentManager fm;
	PortableCheckin app;
	List<Fragment> theFragments = new ArrayList<Fragment>(4);
    Button btnBrand;
	TextView lblLoggetUser, lblCheckinNR, lblVehicleCaption;
	

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action.equalsIgnoreCase("recivedData")) {
                final Bundle b = intent.getExtras().getBundle("recivedData");
                if(b != null && b.getString("ACTION").equalsIgnoreCase("WorkshopPackets")) {
                    ServiceActivity serviceFragment = (ServiceActivity) theFragments.get(3);
                    serviceFragment.refreshMaster();
                }
            }


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

        lblLoggetUser = (TextView) findViewById(R.id.lblLoggetUser);
        lblCheckinNR = (TextView) findViewById(R.id.lblCheckIn_nr);
        lblVehicleCaption = (TextView)findViewById(R.id.lblCarCaption);
        btnBrand = (Button)findViewById(R.id.btnBrand);

        btnBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showBrandLogos();
            }
        });

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		fm = getFragmentManager();
		
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
		registerReceiver(receiver, filterSend);
	}
	
	public void updateData() {
		theFragment = (BaseFragment) theFragments.get(0);
		if(theFragment != null) {
			theFragment.updateData(null);
		}

        lblVehicleCaption.setText(app.getCheckin().vehicle_description);
        setBrand(app.getCheckin());
        setCheckinNr(app.getCheckin());
        updateLblCheckinNr();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {

				switch (data.getExtras().getInt("type")) {
				case BaseGridActivity.eGRDPLANZAK:
					loadCheckinData(data);					
					break;				
				default:
					break;
				}
			}
		} else if (requestCode == 1) {
            app.dismisProgressDialog();
        }

	}

	public void loadCheckinData(Intent data)
	{
        Intent msgIntent = new Intent(this, CommunicationService.class);
        msgIntent.fillIn(data, Intent.FILL_IN_DATA);
        msgIntent.putExtra("ACTIONURL", "pchi/DataForCheckIn");
        msgIntent.putExtra("ACTION", "DataForCheckIn");
        app.showProgrssDialog(this);
        this.startService(msgIntent);
	}

	@Override
	protected void onStart() {

        final String poradce = getResources().getString(R.string.Poradce);

        if(PortableCheckin.user != null)
            lblLoggetUser.setText(poradce + ": " + PortableCheckin.user.name + " " + PortableCheckin.user.surname);
        else
            lblLoggetUser.setText(poradce + ": ");

        if(app.getCheckin().checkin_number <= 0)
            lblCheckinNR.setText("");
		
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
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e){}
		stopTime = new Time();
		stopTime.setToNow();
		super.onStop();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
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
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);		    	
    	transaction.replace(R.id.TheFragment, f);
//    	transaction.addToBackStack(null); //nechcem fungujuce Back tlacitko

	   	transaction.commit();			    
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}



    //Main Top Bar
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void setBrandImage()
    {
        Drawable d = app.getSelectedBrand().getBrandImage(app);
//        int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            btnBrand.setBackgroundDrawable(d);
//        }
//        else {
//            btnBrand.setBackground(d);
//        }
    }

    private void showBrandLogos() {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Example");
        final Cursor cursor = app.getBrands();
        int index = 0;
        final int brandIdIndex = cursor.getColumnIndex(DMBrand.columnNames[DMBrand.ColumnsEnum.BRAND_ID.ordinal()]);
        cursor.moveToFirst();
        while(!cursor.isAfterLast() && !cursor.getString(brandIdIndex).equals(app.getSelectedBrand().brand_id)) {
            index++;
            cursor.moveToNext();
        }
        cursor.moveToFirst();
        b.setSingleChoiceItems(cursor, index, "BRAND_TXT" , new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                cursor.moveToPosition(which);
                app.setSelectedBrand(cursor.getString(cursor.getColumnIndex("BRAND_ID")));
                setBrandImage();
                dialog.dismiss();
            }
        });

        b.show();
    }

    public void setBrand(DMCheckin checkinData) {
        if(checkinData.brand_id != null && checkinData.brand_id.length() > 0) {
            btnBrand.setEnabled(false);
            app.setSelectedBrand(checkinData.brand_id);
            setBrandImage();
        } else {
            btnBrand.setEnabled(true);
        }
    }

    private void updateLblCheckinNr()
    {
        if(app.getCheckin().checkin_number > 0)
            lblCheckinNR.setText(String.valueOf(app.getCheckin().checkin_number));
        else if(app.getCheckin().planned_order_no != null && app.getCheckin().planned_order_no.length() > 0) {
            final String planZakPrefix = getResources().getString(R.string.CisloPlanZakazky);
            lblCheckinNR.setText(planZakPrefix + ": " + String.valueOf(app.getCheckin().planned_order_no));
        }
        else
            lblCheckinNR.setText("");
    }

    public void setCheckinNr(DMCheckin checkinData) {
        if(checkinData.planned_order_id != null && checkinData.checkin_id <= 0) {
//            lblCheckinNR.setTextColor(getResources().getColor(R.color.blue));
            lblCheckinNR.setText(checkinData.planned_order_no);
        }

        this.updateLblCheckinNr();
    }


}
