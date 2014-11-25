package cz.tsystems.portablecheckin;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.tsystems.adapters.PlannedOrderAdapter;
import cz.tsystems.base.BaseFragment;
import cz.tsystems.base.BaseGrid;
import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.base.vinEditText;
import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.DMBrand;
import cz.tsystems.data.DMCheckin;
import cz.tsystems.data.DMPlannedOrder;
import cz.tsystems.data.PortableCheckin;

//import android.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends BaseFragment {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	final String TAG = MainActivity.class.getSimpleName();	
	private RelativeLayout values1, values2;
	private DatePicker theDatePicker;
	private final SimpleDateFormat sdfrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private final SimpleDateFormat sdto = new SimpleDateFormat("MM.yyyy");
	TextView lblLoggetUser, lblCheckinNR;
	EditText txtSTKDate, txtEmiseDate, txtScenar, txtRZV;
	Spinner spScenare;
	vinEditText txtVIN;
	Button btnSPZ, btnSTK, btnEmise, btnPalivo, btnBrand;// btnScenare;	
	PortableCheckin app;
	
	
	OnItemSelectedListener scenarSelectedListener =  new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Cursor cursor = (Cursor) spScenare.getSelectedItem();
			app.getCheckin().check_scenario_id = cursor.getInt(cursor.getColumnIndex("CHECK_SCENARIO_ID"));
			app.setSelectedScenar(app.getCheckin().check_scenario_id);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};
	
	OnDateSetListener onSTKDateChangedListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			txtSTKDate.setText(++monthOfYear + "." + year);
			txtEmiseDate.setText(monthOfYear + "." + year);			
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, 1, 0, 0, 0);
			app.getCheckin().ti_valid_until = c.getTime();
			app.getCheckin().ec_valid_until = c.getTime();
		}
	};
	
	OnDateSetListener onEmiseDateChangedListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			txtEmiseDate.setText(++monthOfYear + "." + year);
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, 1, 0, 0, 0);
			app.getCheckin().ec_valid_until = c.getTime();
			
		}
	};	
	
	private OnClickListener theButtonClickLisener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Button theButton = (Button) v;

			if (theButton.equals(btnSPZ))
				getPlanZakzky();
			else if (theButton.equals(btnSTK))
				getSTKDatePicker();	
			else if(theButton.equals(btnEmise))
				getEmiseDatePicker();
			else if(theButton.equals(btnPalivo))
				showPalivoTypPicker();
			else if(theButton.equals(btnBrand))
				showBrandLogos();			
		}
	};
	
	private TextView.OnEditorActionListener vinEditorActionListener =  new TextView.OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				final String vinStr = txtVIN.getText().toString().replace(" ", "");
				if(vinStr.length() > 0) {
					app.showProgrssDialog(getActivity());
					Intent msgIntent = new Intent(getActivity(), CommunicationService.class);
					msgIntent.putExtra("ACTIONURL", "pchi/DataForCheckIn");
					msgIntent.putExtra("ACTION", "DataForCheckIn");
					msgIntent.putExtra("vin", vinStr);			
					getActivity().startService(msgIntent);
				}
				
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);					
                return true;
            }
			return false;
		}
	};
	
	private TextView.OnEditorActionListener rzvOnEditorActionListener =  new TextView.OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {

				final String licenseTag = txtRZV.getText().toString();
				if(licenseTag.length() > 0) {
					app.showProgrssDialog(getActivity());
					Intent msgIntent = new Intent(getActivity(), CommunicationService.class);
					msgIntent.putExtra("ACTIONURL", "pchi/DataForCheckIn");
					msgIntent.putExtra("ACTION", "DataForCheckIn");
					msgIntent.putExtra("licenseTag", licenseTag);			
					getActivity().startService(msgIntent);
				}				
				
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);					
                return true;
            }
			return false;
		}
	};

	public MainActivity() {
		setRetainInstance(true);				
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_main, container, false);
		
//		View baseContainer = rootView.findViewById(R.id.baseContainer);
		View layout = rootView.findViewById(R.id.dataContainer);
//		View topContainer = rootView.findViewById(R.id.TopContainer);
		
		values1 = (RelativeLayout) layout.findViewById(R.id.mainValuelayout1);
		values2 = (RelativeLayout) layout.findViewById(R.id.mainValuelayout2);
		
		theDatePicker = new DatePicker(getActivity());		
		txtRZV = (EditText) values1.findViewById(R.id.txtSPZ);
		txtRZV.setOnEditorActionListener(rzvOnEditorActionListener);		
		txtVIN = (vinEditText) values2.findViewById(R.id.txtVIN);		
		txtVIN.setOnEditorActionListener(vinEditorActionListener);
		
		lblLoggetUser = (TextView) rootView.findViewById(R.id.lblLoggetUser);
		lblCheckinNR = (TextView) rootView.findViewById(R.id.lblCheckIn_nr);		
		txtSTKDate = (EditText)values1.findViewById(R.id.txtSTK);
		txtEmiseDate = (EditText)values2.findViewById(R.id.txtEmise);
		spScenare = (Spinner)values1.findViewById(R.id.spScenar);
		
		spScenare.setOnItemSelectedListener(scenarSelectedListener);
				
		btnSPZ = (Button) values1.findViewById(R.id.btnSPZ);
		btnSTK = (Button) values1.findViewById(R.id.btnSTK);
		btnEmise = (Button) values2.findViewById(R.id.btnEmise);
		btnPalivo = (Button) values1.findViewById(R.id.btnPalivo);
		btnBrand = (Button)  rootView.findViewById(R.id.btnBrand);
		
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		app = (PortableCheckin)getActivity().getApplicationContext();
		if(PortableCheckin.user != null) {
			setClickLiseners();
			if (spScenare.getAdapter() == null)
				setScenareSpinner();
			setBrandImage();
		}
		else {
			Intent i = new Intent(getActivity(), LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		super.onActivityCreated(savedInstanceState);
	}
	
	private void setScenareSpinner() {
		Cursor cursor = app.getScenare(app.getCheckin().brand_id);
		final int columnIndex = cursor.getColumnIndex("CHECK_SCENARIO_ID");			
		final int scenarId = app.getSelectedScenar().check_scenario_id;
		int pos = -1;
		while(!cursor.isAfterLast()) {
			pos++;
			if(cursor.getInt(columnIndex) == scenarId)
				break;
			cursor.moveToNext();
		}
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, cursor, new String[] { "TEXT" }, new int[] { android.R.id.text1 }, 0); 
		spScenare.setAdapter(adapter);
		spScenare.setSelection(pos, true);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStart() {
		if(PortableCheckin.user != null)
			lblLoggetUser.setText(PortableCheckin.user.name + " " + PortableCheckin.user.surname);
		else
			lblLoggetUser.setText("");
		
		if(app.getCheckin().checkin_number <= 0)
			lblCheckinNR.setText("");
		
		super.onStart();
	}
	
	private void setClickLiseners() {
		btnSPZ.setOnClickListener(theButtonClickLisener);
		btnSTK.setOnClickListener(theButtonClickLisener);
		btnEmise.setOnClickListener(theButtonClickLisener);	
		btnPalivo.setOnClickListener(theButtonClickLisener);
		btnBrand.setOnClickListener(theButtonClickLisener);		
//		btnScenare.setOnClickListener(theButtonClickLisener);		
	}

	public void showData(Intent intent) {
		app.dismisProgressDialog();
		String action = intent.getAction();
		if (action.equalsIgnoreCase("recivedData")) {
			Bundle b = intent.getExtras().getBundle("requestData");
			String serviceAction = b.getString("ACTION");
			Log.d("SHOWDATA", intent.getExtras().toString());
			if (serviceAction.equalsIgnoreCase("CheckinOrderList"))
				showPlanZakazky();
			else if (serviceAction.equalsIgnoreCase("DataForCheckIn"))
				loadCheckinData();
		}
	}

	private void loadCheckinData() {
		try {
			populateView();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void getPlanZakzky() {
		app.showProgrssDialog(getActivity());
		Intent msgIntent = new Intent(getActivity(), CommunicationService.class);
		msgIntent.putExtra("ACTIONURL", "pchi/CheckinOrderList");
		msgIntent.putExtra("ACTION", "CheckinOrderList");
		getActivity().startService(msgIntent);
	}

	private void showPlanZakazky() {

/*        final AlertDialog.Builder b = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));

        View v = getActivity().getLayoutInflater().inflate(R.layout.activity_title_bar, null);
        ((TextView)v.findViewById(R.id.lblTitle)).setText(getActivity().getResources().getText(R.string.naplanovane_zakazky));
        b.setCustomTitle(v);

	    final List<DMPlannedOrder> planedOrderList = ((PortableCheckin)getActivity().getApplicationContext()).getPlanZakazk();
		PlannedOrderAdapter plannedOrderAdapter = new PlannedOrderAdapter(
				getActivity(), android.R.layout.simple_list_item_1,
				planedOrderList);
		
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setGravity(Gravity.LEFT|Gravity.TOP);
		layout.setOrientation(LinearLayout.VERTICAL);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.content_background));
        } else {
            layout.setBackground(getActivity().getResources().getDrawable(R.drawable.content_background));
        }
		
	    ViewGroup view = (ViewGroup) getActivity().getLayoutInflater().inflate( R.layout.item_planned_order, null );
        view.setBackgroundColor(getActivity().getResources().getColor(R.color.grid_caption));
        ((TextView)view.findViewById(R.id.lbldataSource)).setText(getActivity().getResources().getText(R.string.data_source));
        ((TextView)view.findViewById(R.id.lblPlannedOrderStatus)).setText(getActivity().getResources().getText(R.string.Status));
        ((TextView)view.findViewById(R.id.lblLicenseTag)).setText(getActivity().getResources().getText(R.string.RZV));
        ((TextView)view.findViewById(R.id.lblVehicleDescription)).setText(getActivity().getResources().getText(R.string.Vozidlo));
        ((TextView)view.findViewById(R.id.lblCustomerLabel)).setText(getActivity().getResources().getText(R.string.Zakaznik));
        ((TextView)view.findViewById(R.id.lblPlannedOrderNo)).setText(getActivity().getResources().getText(R.string.plan_zak_cis));
        layout.addView(view);

		ListView listView = new ListView(getActivity());
		listView.setAdapter(plannedOrderAdapter);
		layout.addView(listView);
	    final AlertDialog d = b.setView(layout).create();
	    */
/*
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final DMPlannedOrder plannedOrder = planedOrderList.get(position);
				
				Intent msgIntent = new Intent(getActivity(), CommunicationService.class);
				msgIntent.putExtra("ACTIONURL", "pchi/DataForCheckIn");
				msgIntent.putExtra("ACTION", "DataForCheckIn");
				if(plannedOrder.planned_order_id != null && plannedOrder.planned_order_id.length() > 0)
					msgIntent.putExtra("plannedorderid", plannedOrder.planned_order_id);
				if(plannedOrder.checkin_id > 0)
					msgIntent.putExtra("checkin_id", String.valueOf(plannedOrder.checkin_id));
				if(plannedOrder.license_tag != null && plannedOrder.license_tag.length() > 0)
					msgIntent.putExtra("licenseTag", plannedOrder.license_tag);
				app.showProgrssDialog(getActivity());				
				getActivity().startService(msgIntent);			
				d.dismiss();
			}
		});
		*/
        BaseGrid d = new BaseGrid(getActivity());
	    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(d.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	    app.dismisProgressDialog();

        d.show();
	    d.getWindow().setAttributes(lp);

	}
	
	private void showPalivoTypPicker()
	{
		AlertDialog.Builder b = new Builder(getActivity());
	    b.setTitle("Example");
	    final Cursor cursor = app.getPaliva();
	    b.setSingleChoiceItems(cursor, 0, "TEXT" , new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cursor.moveToPosition(which);
				app.getCheckin().fuel_id = cursor.getShort(cursor.getColumnIndex("FUEL_ID"));
				dialog.dismiss();
			}
		});

	    b.show();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setBrandImage()
	{
		Drawable d = app.getSelectedBrand().getBrandImage(app);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				btnBrand.setBackgroundDrawable(d);
		} else {
				btnBrand.setBackground(d);
		}
	}
	
	private void showBrandLogos() {
		
		//TODO oweride
		
		AlertDialog.Builder b = new Builder(getActivity());
	    b.setTitle("Example");
	    final Cursor cursor = app.getBrands();
	    int index = 0;
	    final int brandIdIndex = cursor.getColumnIndex(DMBrand.columnNames[DMBrand.ColumnsEnum.BRAND_ID.ordinal()]);
	    cursor.moveToFirst();
	    while(!cursor.isAfterLast()
	    		&& !cursor.getString(brandIdIndex).equals(app.getSelectedBrand().brand_id))  
	    		index++;	    
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
	

	private void getSTKDatePicker()
	{
        Calendar c = Calendar.getInstance();
        String strDate = txtSTKDate.getText().toString(); 
		if(strDate.length() > 0)
		{
			String parts[] = strDate.split("[.]");
			c.set(Calendar.DAY_OF_MONTH, 1);			
			c.set(Calendar.MONTH, Integer.parseInt(parts[0]));
			c.set(Calendar.YEAR, Integer.parseInt(parts[1]));			
		}
		
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)-1;
        int day = 1;
       
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), DialogFragment.STYLE_NO_FRAME | DialogFragment.STYLE_NORMAL, onSTKDateChangedListener, year, month, day);
        dialog.show();
	}
	
	private void getEmiseDatePicker()
	{
        Calendar c = Calendar.getInstance();
        String strDate = txtEmiseDate.getText().toString(); 
		if(strDate.length() > 0)
		{
			String parts[] = strDate.split("[.]");
			c.set(Calendar.DAY_OF_MONTH, 1);			
			c.set(Calendar.MONTH, Integer.parseInt(parts[0]));
			c.set(Calendar.YEAR, Integer.parseInt(parts[1]));			
		}
		
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)-1;
        int day = 1;
       
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), DialogFragment.STYLE_NO_FRAME | DialogFragment.STYLE_NORMAL, onEmiseDateChangedListener, year, month, day);
        dialog.show();		
	}
	
	private void updateLblCheckinNr()
	{
//		if(data == null)
//			data = app.getCheckinData();
//		
//		int checkinNr = Integer.parseInt(data.get("CHECKIN_NUMBER").toString());
		if(app.getCheckin().checkin_number > 0)
			lblCheckinNR.setText(String.valueOf(app.getCheckin().checkin_number));
		else if(app.getCheckin().planned_order_no != null && app.getCheckin().planned_order_no.length() > 0)
			lblCheckinNR.setText(String.valueOf(app.getCheckin().planned_order_no));
		else
			lblCheckinNR.setText("");
	}
	
	private void populateView() throws ParseException {

		app.dismisProgressDialog();
		populateTextsAndRbtn(values1);
		populateTextsAndRbtn(values2);			
		DMCheckin checkinData = app.getCheckin();

		getActivity().setTitle(checkinData.vehicle_description);
		
		if(checkinData.brand_id != null && checkinData.brand_id.length() > 0) {
			app.setSelectedBrand(checkinData.brand_id);
			setBrandImage();
		}
		

		if(checkinData.check_scenario_id <= 0)
			checkinData.check_scenario_id = app.getSelectedBrand().check_scenario_id_def;
		
		app.setSelectedScenar(checkinData.check_scenario_id);
		
		Cursor scenareCursor = app.getScenare(checkinData.brand_id);
		final int checkScenarioIdIndex = scenareCursor.getColumnIndex("CHECK_SCENARIO_ID");
		final int checkScenarioDefTextIndex = scenareCursor.getColumnIndex("TEXT"); 
		final int scenarId = app.getSelectedScenar().check_scenario_id;
		int pos = -1;
		scenareCursor.moveToFirst();
		while(!scenareCursor.isAfterLast()){
			pos++;
			Log.v(TAG, String.valueOf(scenarId) + " == " + scenareCursor.getString(checkScenarioIdIndex) + ", " + scenareCursor.getString(checkScenarioDefTextIndex));
			if(scenareCursor.getInt(checkScenarioIdIndex) == scenarId)
				break;
			scenareCursor.moveToNext();
		}
		spScenare.setSelection(pos, true);
		
		if(checkinData.planned_order_id != null && checkinData.checkin_id <= 0) {
			lblCheckinNR.setTextColor(getActivity().getResources().getColor(R.color.blue));
			lblCheckinNR.setText(checkinData.planned_order_no);
		}
				
			
		this.updateLblCheckinNr();
		
		app.loadSilhouette();
		((FragmentPagerActivity)getActivity()).updateData();
		
		RadioGroup rg = (RadioGroup)values1.findViewById(R.id.rdbStavPaliva);
		switch (checkinData.fuel_id) {
		case 0:
			rg.check(R.id.rbtnStavPal0);
			break;
		case 1:
			rg.check(R.id.rbtnStavPal1);
			break;
		case 2:
			rg.check(R.id.rbtnStavPal2);
			break;
		case 3:
			rg.check(R.id.rbtnStavPal3);
			break;
		case 4:
			rg.check(R.id.rbtnStavPal4);
			break;					
		default:
			rg.check(R.id.rbtnStavPal2);
			break;
		}	
		
		
		rg = (RadioGroup)values2.findViewById(R.id.rdbInterier);
		switch (checkinData.interior_state) {
		case 0:
			rg.check(R.id.rbtnInter0);
			break;
		case 1:
			rg.check(R.id.rbtnInter1);
			break;
		case 2:
			rg.check(R.id.rbtnSil_1);
			break;
		case 3:
			rg.check(R.id.rbtnSil_2);
			break;
		case 4:
			rg.check(R.id.rbtnSil_3);
			break;					
		default:
			rg.check(R.id.rbtnSil_1);
			break;
		}		
	}
	
	private void populateTextsAndRbtn(ViewGroup theView) {
		String fieldName = "";
		DMCheckin checkinData = app.getCheckin();
		for (int i = 0; i < theView.getChildCount(); i++) {
			View v = theView.getChildAt(i);
			Class<? extends View> c = v.getClass();
			fieldName = (String) v.getTag();
			if (fieldName == null)
				continue;

			Field field = null;
			try {
				field = DMCheckin.class.getField(fieldName.toLowerCase(Locale.ENGLISH));
			} catch (NoSuchFieldException e) {
				Log.v(TAG, "NOT FOUND :" + fieldName.toLowerCase(Locale.ENGLISH));
				continue;
			}
			Log.v(TAG, fieldName.toLowerCase(Locale.ENGLISH));

			try {
				if(field.get(checkinData) == null)
					continue;				

				if (c == EditText.class || c == vinEditText.class) {
					((EditText) v).setText("");
					if (field.getType() == String.class)// jnCheckin.hasNonNull(fieldName))
						((EditText) v).setText((String) field.get(checkinData));
					else if (field.getType() == int.class)
						((EditText) v).setText(String.valueOf(field.getInt(checkinData)));
					else if (field.getType() == short.class)
						((EditText) v).setText(String.valueOf(field.getShort(checkinData)));
					else if (field.getType() == double.class)
						((EditText) v).setText(String.valueOf((int)field.getDouble(checkinData)));
					else if (field.getType() == Date.class)
						((EditText) v).setText(sdto.format((Date) field.get(checkinData)));

				} else if (c == Switch.class) {
					((Switch) v).setChecked(field.getBoolean(checkinData));
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void updateData(Intent intent) {
		// TODO Auto-generated method stub
		
	}

}
