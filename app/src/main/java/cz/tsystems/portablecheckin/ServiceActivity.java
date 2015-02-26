package cz.tsystems.portablecheckin;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.adapters.PacketsArrayAdapter;
import cz.tsystems.adapters.PrehliadkyArrayAdapter;
import cz.tsystems.adapters.ServiceArrayAdapter;
import cz.tsystems.adapters.UnitArrayAdapter;
import cz.tsystems.adapters.VybavaArrayAdapter;
import cz.tsystems.base.BaseFragment;
import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.data.DMBaseItem;
import cz.tsystems.data.DMPrehliadkyMaster;
import cz.tsystems.data.DMService;
import cz.tsystems.data.DMUnit;
import cz.tsystems.data.DMVybava;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.model.PrehliadkyModel;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;

import static cz.tsystems.portablecheckin.R.*;

public class ServiceActivity extends BaseFragment {
	
    private final static String TAG = ServiceActivity.class.getSimpleName();	
//    public static final String ARG_SECTION_NUMBER = "section_number";
	private PrehliadkyModel prehliadkaModel;
	private PrehliadkyArrayAdapter prehliadkyAdapter;
	private VybavaArrayAdapter vybavaAdapter;
    private ServiceArrayAdapter serviceAdapter;
    private PacketsArrayAdapter paketArrayAdapter;
    private PortableCheckin app;
    public String packetQuery;
	
//    private static View rootView;
    public ListView listMaster, listDetail;
	private UnitArrayAdapter unitAdapter;
    private DMPrehliadkyMaster selectedPrehliadky;
    private OnItemClickListener onDetialClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, view.toString());
        }
    };

    public ServiceActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(layout.activity_service, container, false);
        app = (PortableCheckin) getActivity().getApplicationContext();
        listMaster = (ListView) rootView.findViewById(id.listMaster);
        listDetail = (ListView) rootView.findViewById(id.listDetail);

        listMaster.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                masterClicked(view, position);
			}
		});

        listDetail.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.gc.materialdesign.views.CheckBox b = (com.gc.materialdesign.views.CheckBox)view.findViewById(R.id.checkBox);

                DMPrehliadkyMaster prehliadkaMaster = ServiceActivity.this.selectedPrehliadky;

                if(prehliadkaMaster.type == DMPrehliadkyMaster.eVYBAVY
                        || prehliadkaMaster.type == DMPrehliadkyMaster.eSLUZBY) {
                    DMBaseItem item = null;
                    switch (prehliadkaMaster.rowId) {
                        case -1:
                        case -2:
                            item = vybavaAdapter.getItem(position);
                        break;
                        case -3:
                            item = serviceAdapter.getItem(position);
                            break;
                    }
                    if(item != null) {
                        ((FragmentPagerActivity)getActivity()).unsavedCheckin();
                        if(item.getEditable()) {
                            EditText editText = (EditText)view.findViewById(R.id.txtVybavaText);
                            editText.requestFocusFromTouch();
                            InputMethodManager lManager = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            lManager.showSoftInput(editText, 0);
                        }
                        item.setChecked(!item.getChecked());
                        b.setChecked(item.getChecked());
                    }
                } else if(prehliadkaMaster.type == DMPrehliadkyMaster.eUNIT) {
                    DMUnit item = unitAdapter.getItem(position);

                    if(item != null && item.chck_status_id != null) {
                        ((FragmentPagerActivity)getActivity()).unsavedCheckin();
                        item.chck_status_id = (item.chck_status_id == 1)?0:1;
                        b.setChecked(item.chck_status_id == 1);
                    }
                }
            }
        });



        return rootView;
    }

    public void masterClicked(View view, int position) {
        DMPrehliadkyMaster prehliadkyMaster = (DMPrehliadkyMaster) listMaster.getAdapter().getItem(position);//masterList.get(position);
        if(prehliadkyMaster.type != DMPrehliadkyMaster.eSECTION) {
            if(view != null)
                ((CheckBox)view.findViewById(R.id.chkPrehliadky)).setChecked(true);
            ServiceActivity.this.selectedPrehliadky = prehliadkyMaster;

            ActionBar.Tab tab = getActivity().getActionBar().getSelectedTab();
            TextView txtBadge = (TextView) tab.getCustomView().findViewById(R.id.tab_badge);
            String[] badgeStr = txtBadge.getText().toString().split("/");


            if(txtBadge.getVisibility() == View.VISIBLE ) {
                if (prehliadkyMaster.mandatory && !prehliadkyMaster.opened) {
                    int openedCount = Integer.valueOf(badgeStr[0]) + 1;
                    txtBadge.setText(String.valueOf(openedCount) + "/" + badgeStr[1]);
                }

                if ((Integer.valueOf(badgeStr[0])+1) == Integer.valueOf(badgeStr[1]))
                    txtBadge.setVisibility(View.INVISIBLE);
            }

            prehliadkyMaster.opened = true;
            refreshDetail(null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onResume() {
   	    refreshMaster();
        if(selectedPrehliadky != null)
            refreshDetail(null);
        filterPackets();
    	super.onResume();
    }
    
    public void refreshMaster() {

    	List<DMPrehliadkyMaster> masterList = app.prehliadkyMasters;

		if (prehliadkyAdapter == null) {
			prehliadkyAdapter = new PrehliadkyArrayAdapter(ServiceActivity.this, getActivity(), 0, layout.item_prehliadky, masterList);
			listMaster.setAdapter(prehliadkyAdapter);
		} else {
            prehliadkyAdapter.clear();
            prehliadkyAdapter.addAll(masterList);
            prehliadkyAdapter.notifyDataSetChanged();
            listMaster.setAdapter(prehliadkyAdapter);
        }
    }
    
    public void refreshDetail(Bundle data) {
        final long type = selectedPrehliadky.type;


        if (type == DMPrehliadkyMaster.eVYBAVY) {
            List<DMVybava> vybava = ((PortableCheckin) getActivity().getApplicationContext()).getVybavaList((selectedPrehliadky.rowId == prehliadkaModel.ePOV_VYBAVY));
            vybavaAdapter = new VybavaArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, vybava);
            listDetail.setAdapter(vybavaAdapter);
            vybavaAdapter.notifyDataSetChanged();
        } else if (type == DMPrehliadkyMaster.eSLUZBY) {
            List<DMService> service = ((PortableCheckin) getActivity().getApplicationContext()).getServiceList();
            if (serviceAdapter == null || listDetail.getAdapter() != serviceAdapter) {
                serviceAdapter = new ServiceArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, service);
                listDetail.setAdapter(serviceAdapter);
            }
        } else if (type == DMPrehliadkyMaster.eUNIT) {
            List<DMUnit> unit = ((PortableCheckin) getActivity().getApplicationContext()).getUnitListByUnitId(selectedPrehliadky.unitId);
            listDetail.setAdapter(new UnitArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, unit));
        } else if (type == DMPrehliadkyMaster.ePAKETY) {
            paketArrayAdapter = new PacketsArrayAdapter(getActivity(), 0, layout.item_unit_packet, app.getPaket(selectedPrehliadky.groupNr, packetQuery), false);
            packetQuery = "";
            listDetail.setAdapter(paketArrayAdapter);
        }
    }
    
	@Override
	public void updateData(Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
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
        }
		// TODO Auto-generated method stub
		
	}

    public void filterPackets() {
        if(packetQuery == null || packetQuery.length() == 0)
            return;

        final int pos = prehliadkyAdapter.getAllPacketsPos();
        if(pos > 0) {
            listMaster.smoothScrollToPosition(pos);
            masterClicked(null, pos);
        }
    }

    public void resetService() {
        selectedPrehliadky = null;
    }
}
