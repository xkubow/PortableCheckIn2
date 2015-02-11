package cz.tsystems.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.DMBrand;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.grids.BaseGridActivity;
import cz.tsystems.portablecheckin.*;

import android.annotation.TargetApi;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class FragmentPagerActivity extends Activity implements TabListener {

//	ViewPager mViewPager;
//	SectionsPagerAdapter mSectionsPagerAdapter;
    public static final int eTabVozidlo = 0, eTabPoskodenie = 1, eTabService = 2, eTabNabidka = 3;
    public static int eGRID_RESULT = 2;
	private Time stopTime;
	private BaseFragment theFragment;
    private Boolean checkLogin = true, newCheckin = false;
	FragmentManager fm;
	PortableCheckin app;
	List<Fragment> theFragments = new ArrayList<Fragment>(4);
    Button btnBrand;
	TextView lblLoggetUser, lblCheckinNR, lblVehicleCaption;
    Menu myMenu;
    private int largestHeight;

    public void setCheckLogin(Boolean checkLogin) {
        this.checkLogin = checkLogin;
    }

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

            String errorMsg = intent.getStringExtra("ErrorMsg");

            if(errorMsg != null && errorMsg.length() > 0)
            {
                app.getDialog(FragmentPagerActivity.this, "error", errorMsg, PortableCheckin.DialogType.SINGLE_BUTTON).show();
                return;
            }

            String action = intent.getAction();
            final Bundle b = intent.getExtras().getBundle("requestData");
            if(b != null && b.getString("ACTION").equalsIgnoreCase("WorkshopPackets")) {
                if(getActionBar().getSelectedTab().getPosition() == 2)
                    updateServiceFragment(); //TODO dakedy padne kvoli broadcastreciveru
            }
            if (b != null && b.getString("ACTION").equalsIgnoreCase("SaveCheckin")) {

                if(PortableCheckin.selectedSilhouette.getPhotosCount() > 0) {
                    Intent msgIntent = new Intent(FragmentPagerActivity.this, CommunicationService.class);
                    msgIntent.putExtra("ACTIONURL", "pchi/DataForCheckIn");
                    msgIntent.putExtra("ACTIONURL", "pchi/SavePhotos/");
                    msgIntent.putExtra("ACTION", "SavePhotos");
                    msgIntent.putExtra("checkinID", String.valueOf(app.getCheckin().checkin_id));
                    app.showProgrssDialog(FragmentPagerActivity.this);
                    FragmentPagerActivity.this.startService(msgIntent);
                    return;
                } else
                    saveCheckingDone();
            } else if(b.getString("ACTION").equalsIgnoreCase("SavePhotos")) {
                    saveCheckingDone();
            } else if (b.getString("ACTION").equalsIgnoreCase("DataForCheckIn")) {

                final boolean isCheckin = (PortableCheckin.checkin.checkin_id != null && PortableCheckin.checkin.checkin_id > 0);

                MenuItem button = myMenu.findItem(R.id.action_send);
                Drawable resIcon = getResources().getDrawable(R.drawable.ic_send_white_36dp);
                button.setIcon(resIcon);

                if(!isCheckin) {
                    ActionBar.Tab tab = getActionBar().getTabAt(eTabNabidka);
                    tab.getCustomView().findViewById(R.id.tab_badge).setVisibility(View.VISIBLE);
                    tab = getActionBar().getTabAt(eTabService);
                    tab.getCustomView().findViewById(R.id.tab_badge).setVisibility(View.VISIBLE);
                    tab = getActionBar().getTabAt(eTabNabidka);
                    tab.getCustomView().findViewById(R.id.tab_badge).setVisibility(View.VISIBLE);
                } else {
                    ActionBar.Tab tab = getActionBar().getTabAt(eTabNabidka);
                    tab.getCustomView().findViewById(R.id.tab_badge).setVisibility(View.INVISIBLE);
                    tab = getActionBar().getTabAt(eTabService);
                    tab.getCustomView().findViewById(R.id.tab_badge).setVisibility(View.INVISIBLE);
                    tab = getActionBar().getTabAt(eTabNabidka);
                    tab.getCustomView().findViewById(R.id.tab_badge).setVisibility(View.INVISIBLE);
                }


            } else if(b.getString("ACTION").equalsIgnoreCase("XyzmoResponse")) {
                getProtokol();
            }


			theFragment = (BaseFragment) theFragments.get(getActionBar().getSelectedTab().getPosition());
//			Log.d("FRAGMENT", theFragment.getClass().getSimpleName());
			theFragment.showData(intent);
		}
	};

    private void saveCheckingDone() {
        MenuItem button = myMenu.findItem(R.id.action_send);
        Drawable resIcon = getResources().getDrawable(R.drawable.ic_send_white_36dp);
        resIcon.mutate().setColorFilter(R.color.green, PorterDuff.Mode.MULTIPLY);
        button.setIcon(resIcon);
        app.dismisProgressDialog();
    }

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
        lblVehicleCaption.setText(R.string.vozidlo_nevybrano);
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
        setBrand(app.getSelectedBrand().brand_id);

		theFragments.add(new MainActivity());
		theFragments.add(new BodyActivity());
		theFragments.add(new ServiceActivity());
		theFragments.add(new OffersActivity());
		
		actionBar.addTab(actionBar.newTab().setCustomView(renderTabView(FragmentPagerActivity.this, eTabVozidlo)).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setCustomView(renderTabView(FragmentPagerActivity.this, eTabPoskodenie)).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setCustomView(renderTabView(FragmentPagerActivity.this, eTabService)).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setCustomView(renderTabView(FragmentPagerActivity.this, eTabNabidka)).setTabListener(this));

        app.setActualActivity(this);

	}

    public static View renderTabView(Context context, int tabnr) {
        FrameLayout view = (FrameLayout ) LayoutInflater.from(context).inflate(R.layout.textview_tab, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView txtBadge = (TextView) view.findViewById(R.id.tab_badge);
        TextView txtTabText = (TextView) view.findViewById(R.id.tab_text);

        switch(tabnr) {
            case eTabVozidlo:
                txtTabText.setText(R.string.VOZIDLO);
                txtBadge.setVisibility(View.VISIBLE);
                txtBadge.setText("0/6");
                break;
            case eTabPoskodenie:
                txtTabText.setText(R.string.POSKODENI);
                txtBadge.setVisibility(View.GONE);
                break;
            case eTabService:
                txtTabText.setText(R.string.PROHLIDKA);
                txtBadge.setVisibility(View.VISIBLE);
                txtBadge.setText("0/" + String.valueOf(PortableCheckin.selectedScenar.mandatoryCount));
                break;
            case eTabNabidka:
                txtTabText.setText(R.string.NABIDKA);
                txtBadge.setVisibility(View.VISIBLE);
                txtBadge.setText("");
                break;
        }
        return view;
    }

	private void registerRecaiver() {
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction("recivedData");
		filterSend.addAction("grdResponse");
		registerReceiver(receiver, filterSend);
	}

    public void updateFragments() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> iter = runningAppProcesses.iterator();

        while(iter.hasNext()){
            ActivityManager.RunningAppProcessInfo next = iter.next();

            String pricessName = getPackageName() + ":service";
            pricessName = "cz.tsystems.communications.CommunicationService";

            if(next.processName.equals(pricessName)){
                android.os.Process.killProcess(next.pid);
                break;
            }
        }

        MainActivity mainActivity = (MainActivity) theFragments.get(0);
        mainActivity.isNewCheckin = true;
        if(this.getActionBar().getSelectedTab().getPosition() == eTabVozidlo)
            mainActivity.loadDefaultCheckIn();
        else
            this.getActionBar().setSelectedNavigationItem(eTabVozidlo);
    }
	
	public void updateData() {
        lblVehicleCaption.setText(app.getCheckin().vehicle_description);
        setBrand(app.getCheckin().brand_id);
        setCheckinNr();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
                if(data.getExtras() == null)
                    return;

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

        if(app.getCheckin().checkin_number == null)
            lblCheckinNR.setText("");

        if(checkLogin) {
            if (PortableCheckin.user == null) {
                Intent it = new Intent(this, LoginActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(it, 1);
            } else if (stopTime != null) {
                Time now = new Time();
                now.setToNow();
                long backgroundTime = now.toMillis(true) - stopTime.toMillis(true);
                if (backgroundTime > 30000) {
                    Intent it = new Intent(this, LoginActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(it, 1);
                }
                stopTime = null;
            }
        } else
            checkLogin = true;
        super.onStart();
	}

	@Override
	protected void onResume() {
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String workstepId = uri.getQueryParameter("WorkstepId");
            Intent msgIntent = new Intent(this, CommunicationService.class);
            msgIntent.putExtra("ACTIONURL", "Signing/XyzmoResponse");
            msgIntent.putExtra("ACTION", "XyzmoResponse");
            msgIntent.putExtra("WorkstepId", workstepId);
            this.startService(msgIntent);
        }


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
		BaseMenu.show(getApplicationContext(), item, this);
		return super.onOptionsItemSelected(item);
	}

    void getProtokol() {
        checkLogin = false;
        Intent myIntent = new Intent(FragmentPagerActivity.this, Protocol.class);
        startActivity(myIntent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
		getMenuInflater().inflate(R.menu.themenu, menu);
        menu.findItem(R.id.action_note_add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                checkLogin = false;
                Intent myIntent = new Intent(FragmentPagerActivity.this, Poznamka.class);
                startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
                return false;
            }
        });
        menu.findItem(R.id.action_Protokol).setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getProtokol();
                return  false;
            }
        });
        menu.findItem(R.id.action_send).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean doSave = true;
                doSave &= getActionBar().getTabAt(eTabVozidlo).getCustomView().findViewById(R.id.tab_badge).getVisibility() == View.INVISIBLE;
                doSave &= getActionBar().getTabAt(eTabService).getCustomView().findViewById(R.id.tab_badge).getVisibility() == View.INVISIBLE;
                doSave &= getActionBar().getTabAt(eTabNabidka).getCustomView().findViewById(R.id.tab_badge).getVisibility() == View.INVISIBLE;

                if(doSave) {
                    app.showProgrssDialog(FragmentPagerActivity.this);
                    Intent msgIntent = new Intent(FragmentPagerActivity.this, CommunicationService.class);
                    msgIntent.putExtra("ACTIONURL", "pchi/SaveCheckin");
                    msgIntent.putExtra("ACTION", "SaveCheckin");
                    startService(msgIntent);
                } else
                    app.getDialog(FragmentPagerActivity.this, "", getResources().getString(R.string.PozadPoleNevypl), PortableCheckin.DialogType.SINGLE_BUTTON).show();

                return false;
            }
        });

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
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

        if(pos == eTabNabidka) {
            tab.getCustomView().findViewById(R.id.tab_badge).setVisibility(View.INVISIBLE);
        }
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

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
        b.setTitle(R.string.brands);
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

    public void setBrand(final String brandId) {
        if(brandId != null && brandId.length() > 0) {
            btnBrand.setEnabled(false);
            if(app.getCheckin().checkin_id != null)
                app.selectedBrand = app.getBrand(app.getCheckin().brand_id); // vybavy aj servisi uz existuju zo serveru
            else
                app.setSelectedBrand(brandId); // nastavuju sa aj vybavy aj servisi
            setBrandImage();
        } else {
            btnBrand.setEnabled(true);
        }
    }

    public void setCheckinNr() {
        if(app.getCheckin().checkin_number != null && app.getCheckin().checkin_number > 0) {
            final String planZakPrefix = getResources().getString(R.string.CisloCheckinu);
            final Spanned theNR = Html.fromHtml(planZakPrefix + ": <b>"+String.valueOf(app.getCheckin().checkin_number)+"</b>");
            lblCheckinNR.setText(theNR);
        }
        else if(app.getCheckin().planned_order_no != null && app.getCheckin().planned_order_no.length() > 0) {
            final String planZakPrefix = getResources().getString(R.string.CisloPlanZakazky);
            final Spanned theNR = Html.fromHtml(planZakPrefix + ": <b>"+String.valueOf(app.getCheckin().planned_order_no)+"</b>");
            lblCheckinNR.setText(theNR );
        }
        else
            lblCheckinNR.setText("");
    }

    private void updateServiceFragment() {
        ServiceActivity serviceFragment = (ServiceActivity) theFragments.get(eTabService);
        serviceFragment.refreshMaster();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, getResources().getString(R.string.Smazat_foto));
        menu.add(0, v.getId(), 1, getResources().getString(R.string.Nahled));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BodyActivity bodyActivity = (BodyActivity) theFragments.get(eTabPoskodenie);
        if(item.getOrder() == 0){bodyActivity.deleteImage(item.getItemId());}
        else if(item.getOrder() == 1){bodyActivity.showImage(item.getItemId());}
        else {return false;}
        return true;
    }

}
