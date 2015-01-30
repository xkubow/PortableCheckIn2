package cz.tsystems.portablecheckin;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.tsystems.base.BaseFragment;
import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.base.vinEditText;
import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.DMCheckin;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.grids.OdlozenePolozky;
import cz.tsystems.grids.PlanActivities;
import cz.tsystems.grids.PlanedOrdersGrid;
import cz.tsystems.grids.SDA;

//import android.app.DialogFragment;
import android.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.Slider;

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
	EditText txtSTKDate, txtEmiseDate, txtScenar, txtRZV, txtZakaznik, txtStavTachometru;
	Spinner spScenare, spTypPaliva;
	vinEditText txtVIN;
	Button btnSPZ, btnSTK, btnEmise;// btnPalivo;// btnScenare;
    com.gc.materialdesign.views.Switch chkPoistPrip, chkOtp, chkServisKniz;
    ButtonFloat fbtPZ, fbtMajak, fbtMegafon;
    Slider stavPaliva, stavInterieru, stavOleja;
	PortableCheckin app;
    private boolean scenarClicked = false;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int povinneNevyplnene = 0;
            povinneNevyplnene += (txtRZV.length() > 0)?1:0;
            povinneNevyplnene += (txtVIN.length() > 0)?1:0;
            povinneNevyplnene += (txtZakaznik.length() > 0)?1:0;
            povinneNevyplnene += (txtStavTachometru.length() > 0)?1:0;
            povinneNevyplnene += (txtSTKDate.length() > 0)?1:0;
            povinneNevyplnene += (txtEmiseDate.length() > 0)?1:0;

            ActionBar.Tab tab = getActivity().getActionBar().getSelectedTab();
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_badge);
            textView.setText(String.valueOf(povinneNevyplnene) + "/6");
        }
    };

	OnItemSelectedListener scenarSelectedListener =  new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(scenarClicked) {
                Cursor cursor = (Cursor) spScenare.getSelectedItem();
                app.getCheckin().check_scenario_id = cursor.getInt(cursor.getColumnIndex("CHECK_SCENARIO_ID"));
                app.setSelectedScenar(app.getCheckin().check_scenario_id);
                app.loadUnits();
                scenarClicked = false;
            }
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

    private OnClickListener theFloatButtonClickLisener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ButtonFloat theButton = (ButtonFloat) v;
            if(theButton.equals(fbtPZ)) {
                ((FragmentPagerActivity)getActivity()).setCheckLogin(false);
                Intent myIntent = new Intent(getActivity(), PlanActivities.class);
                startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
            } else if (theButton.equals(fbtMajak)) {
                ((FragmentPagerActivity)getActivity()).setCheckLogin(false);
                Intent myIntent = new Intent(getActivity(), OdlozenePolozky.class);
                startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
            } else if (theButton.equals(fbtMegafon)) {
                ((FragmentPagerActivity)getActivity()).setCheckLogin(false);
                Intent myIntent = new Intent(getActivity(), SDA.class);
                startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
            }
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

    private View.OnTouchListener scenarOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            scenarClicked = true;
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
//		View layout = rootView.findViewById(R.id.dataContainer);
//		View topContainer = rootView.findViewById(R.id.TopContainer);
		
		values1 = (RelativeLayout) rootView.findViewById(R.id.mainValuelayout1);
		values2 = (RelativeLayout) rootView.findViewById(R.id.mainValuelayout2);

		theDatePicker = new DatePicker(getActivity());
		txtRZV = (EditText) values1.findViewById(R.id.txtSPZ);
		txtRZV.setOnEditorActionListener(rzvOnEditorActionListener);
        EditText edtTmp = (EditText) values2.findViewById(R.id.txtVIN);
		txtVIN = (vinEditText) edtTmp;
		txtVIN.setOnEditorActionListener(vinEditorActionListener);

        txtZakaznik = (EditText) values2.findViewById(R.id.txtZakaznik);
        txtStavTachometru = (EditText) values2.findViewById(R.id.txtStavTachomeru);
		txtSTKDate = (EditText)values1.findViewById(R.id.txtSTK);
		txtEmiseDate = (EditText)values2.findViewById(R.id.txtEmise);
		spScenare = (Spinner)values1.findViewById(R.id.spScenar);
        spTypPaliva = (Spinner)values1.findViewById(R.id.spTypPaliva);

        txtRZV.addTextChangedListener(textWatcher);
        txtVIN.addTextChangedListener(textWatcher);
        txtZakaznik.addTextChangedListener(textWatcher);
        txtStavTachometru.addTextChangedListener(textWatcher);
        txtSTKDate.addTextChangedListener(textWatcher);
        txtEmiseDate.addTextChangedListener(textWatcher);

        scenarClicked = false;
		spScenare.setOnItemSelectedListener(scenarSelectedListener);
        spScenare.setOnTouchListener(scenarOnTouchListener);

		btnSPZ = (Button) values1.findViewById(R.id.btnSPZ);
		btnSTK = (Button) values1.findViewById(R.id.btnSTK);
		btnEmise = (Button) values2.findViewById(R.id.btnEmise);

        chkPoistPrip = (com.gc.materialdesign.views.Switch) values1.findViewById(R.id.chkPoist_prp);
        chkOtp  = (com.gc.materialdesign.views.Switch) values1.findViewById(R.id.chkOTP);
        chkServisKniz  = (com.gc.materialdesign.views.Switch) values2.findViewById(R.id.chkServisKnizka);

        stavPaliva = (Slider) values1.findViewById(R.id.slaiderStavPaliva);
        stavInterieru = (Slider) values2.findViewById(R.id.slaiderInterier);
        stavOleja = (Slider) values2.findViewById(R.id.slaiderStavOleje);

        fbtPZ = (ButtonFloat) rootView.findViewById(R.id.fbtnPZ);
        fbtPZ.setOnClickListener(theFloatButtonClickLisener);
        fbtMajak = (ButtonFloat) rootView.findViewById(R.id.fbtnMajak);
        fbtMajak.setOnClickListener(theFloatButtonClickLisener);
        fbtMegafon = (ButtonFloat) rootView.findViewById(R.id.fbtnMegafon);
        fbtMegafon.setOnClickListener(theFloatButtonClickLisener);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		app = (PortableCheckin)getActivity().getApplicationContext();
		if(PortableCheckin.user != null) {
			setClickLiseners();
			if (spScenare.getAdapter() == null)
				setScenareSpinner();
            setPalivoSpinner();
//			setBrandImage();
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
//		spScenare.setSelection(pos, true);
	}

    private void setPalivoSpinner() {
        Cursor cursor = app.getPaliva();
        final int columnIndex = cursor.getColumnIndex("FUEL_ID");
        final int fuelId = app.getCheckin().fuel_id;
        int pos = -1;
        while(!cursor.isAfterLast()) {
            pos++;
            if(cursor.getInt(columnIndex) == fuelId)
                break;
            cursor.moveToNext();
        }

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, cursor, new String[] { "TEXT" }, new int[] { android.R.id.text1 }, 0);
        spTypPaliva.setAdapter(adapter);
        spTypPaliva.setSelection(pos, true);
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
		super.onStart();
        getActivity().getActionBar().setTitle(R.string.Informace_o_Vozidle);
	}
	
	private void setClickLiseners() {
		btnSPZ.setOnClickListener(theButtonClickLisener);
		btnSTK.setOnClickListener(theButtonClickLisener);
		btnEmise.setOnClickListener(theButtonClickLisener);	

        chkPoistPrip.setOncheckListener(new com.gc.materialdesign.views.Switch.OnCheckListener() {
            @Override
            public void onCheck(boolean isChecked) {
                app.getCheckin().insurance_case = isChecked;
            }
        });
        chkServisKniz.setOncheckListener(new com.gc.materialdesign.views.Switch.OnCheckListener() {
            @Override
            public void onCheck(boolean isChecked) {
                app.getCheckin().servbook_exists = isChecked;
            }
        });
        chkOtp.setOncheckListener(new com.gc.materialdesign.views.Switch.OnCheckListener() {
            @Override
            public void onCheck(boolean isChecked) {
                app.getCheckin().crw_exists = isChecked;
            }
        });
        chkPoistPrip.setChecked(app.getCheckin().insurance_case);
        chkOtp.setChecked(app.getCheckin().crw_exists);
        chkServisKniz.setChecked(app.getCheckin().servbook_exists);

        stavPaliva.setValue(app.getCheckin().fuel_level);
        stavPaliva.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                app.getCheckin().fuel_level = (short) value;
            }
        });
        stavInterieru.setValue(app.getCheckin().interior_state);
        stavInterieru.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                app.getCheckin().interior_state = (short) value;
            }
        });
        stavOleja.setValue(app.getCheckin().oil_level);
        stavOleja.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                app.getCheckin().oil_level = (short) value;
            }
        });

//		btnScenare.setOnClickListener(theButtonClickLisener);
	}

	public void showData(Intent intent) {
		app.dismisProgressDialog();
		String action = intent.getAction();
        String serviceAction = null;
		if (action.equalsIgnoreCase("recivedData")) {
            try {
                Bundle b = intent.getExtras().getBundle("requestData");
                if(b == null)
                    return;
                serviceAction = b.getString("ACTION");
                if(serviceAction == null)
                    return;
                Log.d("SHOWDATA", intent.getExtras().toString());
            } catch (NullPointerException e) {
                app.getDialog(getActivity(), "error", e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON).show();
            }
			if (serviceAction.equalsIgnoreCase("CheckinOrderList"))
				showPlanZakazky();
			else if (serviceAction.equalsIgnoreCase("DataForCheckIn"))
				loadCheckinData();
		}
	}

	private void loadCheckinData() {
		try {
			populateView();
            Intent msgIntent = new Intent(getActivity(), CommunicationService.class);
            msgIntent.putExtra("ACTIONURL", "pchi/WorkshopPackets");
            msgIntent.putExtra("ACTION", "WorkshopPackets");
            msgIntent.putExtra("vin", app.getCheckin().vin);
            msgIntent.putExtra("brandId", app.getSelectedBrand().brand_id);
            msgIntent.putExtra("detail", true);
            getActivity().startService(msgIntent);
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
        ((FragmentPagerActivity)getActivity()).setCheckLogin(false);
        Intent myIntent = new Intent(getActivity(), PlanedOrdersGrid.class);
        getActivity().startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
	}
	
	private void getSTKDatePicker()
	{
        Calendar c = Calendar.getInstance();
        String strDate = txtSTKDate.getText().toString(); 
		if(strDate.length() > 0)
		{
			String parts[] = strDate.split("[.]");
            if(parts.length >= 2) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, Integer.parseInt(parts[0]));
                c.set(Calendar.YEAR, Integer.parseInt(parts[1]));
            }
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
            if(parts.length >= 2) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, Integer.parseInt(parts[0]));
                c.set(Calendar.YEAR, Integer.parseInt(parts[1]));
            }
		}
		
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)-1;
        int day = 1;
       
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), DialogFragment.STYLE_NO_FRAME | DialogFragment.STYLE_NORMAL, onEmiseDateChangedListener, year, month, day);
        dialog.show();		
	}
	

	
	private void populateView() throws ParseException {

		app.dismisProgressDialog();
		populateTextsAndRbtn(values1);
		populateTextsAndRbtn(values2);			
		DMCheckin checkinData = app.getCheckin();

//		getActivity().setTitle(checkinData.vehicle_description);
		
		if(checkinData.check_scenario_id <= 0)
			checkinData.check_scenario_id = app.getSelectedBrand().check_scenario_id_def;
		
		app.setSelectedScenar(checkinData.check_scenario_id);
        app.loadUnits();

        if(checkinData.fuel_id <= 0)
            checkinData.fuel_id = 0;

        Cursor cursor = app.getPaliva();
        cursor.moveToFirst();
        final int searchedId = cursor.getColumnIndex("FUEL_ID");
        int pos = -1;
        while (!cursor.isAfterLast()) {
            pos++;
            if(cursor.getInt(searchedId) == checkinData.fuel_id)
                break;
            cursor.moveToNext();
        }
        spTypPaliva.setSelection(pos, true);

        cursor = null;
		cursor = app.getScenare(checkinData.brand_id);
		final int checkScenarioIdIndex = cursor.getColumnIndex("CHECK_SCENARIO_ID");
		final int checkScenarioDefTextIndex = cursor.getColumnIndex("TEXT");
		final int scenarId = app.getSelectedScenar().check_scenario_id;
		pos = -1;
        cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			pos++;
			Log.v(TAG, String.valueOf(scenarId) + " == " + cursor.getString(checkScenarioIdIndex) + ", " + cursor.getString(checkScenarioDefTextIndex));
			if(cursor.getInt(checkScenarioIdIndex) == scenarId)
				break;
            cursor.moveToNext();
		}
		spScenare.setSelection(pos, true);
		

		app.loadSilhouette();
		((FragmentPagerActivity)getActivity()).updateData();

        com.gc.materialdesign.views.Slider rg = (com.gc.materialdesign.views.Slider)values1.findViewById(R.id.slaiderStavPaliva);
        rg.setValue(checkinData.fuel_level);

		rg = (com.gc.materialdesign.views.Slider)values2.findViewById(R.id.slaiderInterier);
        rg.setValue(checkinData.interior_state);
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

                    EditText editText = (EditText) v;
                    editText.setText("");
					if (field.getType() == String.class)// jnCheckin.hasNonNull(fieldName))
                        editText.setText((String) field.get(checkinData));
					else if (field.getType() == int.class)
                        editText.setText(String.valueOf(field.getInt(checkinData)));
					else if (field.getType() == short.class)
                        editText.setText(String.valueOf(field.getShort(checkinData)));
					else if (field.getType() == double.class)
                        editText.setText(String.valueOf((int)field.getDouble(checkinData)));
					else if (field.getType() == Date.class)
                        editText.setText(sdto.format((Date) field.get(checkinData)));

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
