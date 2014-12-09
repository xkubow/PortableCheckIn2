package cz.tsystems.portablecheckin;

import java.util.Iterator;
import java.util.List;

import cz.tsystems.adapters.PacketsArrayAdapter;
import cz.tsystems.adapters.PrehliadkyArrayAdapter;
import cz.tsystems.adapters.ServiceArrayAdapter;
import cz.tsystems.adapters.UnitArrayAdapter;
import cz.tsystems.adapters.VybavaArrayAdapter;
import cz.tsystems.base.BaseFragment;
import cz.tsystems.data.DMBaseItem;
import cz.tsystems.data.DMPrehliadky;
import cz.tsystems.data.DMPrehliadkyMaster;
import cz.tsystems.data.DMService;
import cz.tsystems.data.DMUnit;
import cz.tsystems.data.DMVybava;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.model.PrehliadkyModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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
	
    private static View rootView;
    ListView listMaster, listDetail;
    private List<DMPrehliadkyMaster> masterList = null;
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
        app = (PortableCheckin) getActivity().getApplicationContext();
        if (rootView != null) {
        	Log.v(TAG, rootView.getParent().getClass().toString());
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        	return rootView;            
        }    	
        rootView = inflater.inflate(layout.activity_service, container, false);
        listMaster = (ListView) rootView.findViewById(id.listMaster);
//        listMaster.setAdapter(prehliadkyAdapter);
        listDetail = (ListView) rootView.findViewById(id.listDetail);

        listMaster.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DMPrehliadkyMaster prehliadkyMaster = masterList.get(position);
                ServiceActivity.this.selectedPrehliadky = prehliadkyMaster;
				refreshDetail(null);
			}
		});

        listDetail.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                final int prehlaidkaId = ServiceActivity.this.selectedPrehliadky;
                CheckBox b = (CheckBox)view.findViewById(R.id.checkBox);

                DMPrehliadkyMaster prehliadkaMaster = ServiceActivity.this.selectedPrehliadky;

                if(prehliadkaMaster.typ == DMPrehliadkyMaster.eSTATIC) {
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
                        item.checked = !item.checked;
                        b.setChecked(item.checked);
                    }
                } else if(prehliadkaMaster.typ == DMPrehliadkyMaster.eUNIT) {
                    DMUnit item = unitAdapter.getItem(position);

                    if(item != null) {
                        item.chck_status_id = (item.chck_status_id == 1)?0:1;
                        b.setChecked(item.chck_status_id == 1);
                    }
                }
            }
        });



        return rootView;
    }
    
    @Override
    public void onResume() {
    	refreshMaster();
    	super.onResume();
    }
    
    public void refreshMaster() {
    	if(prehliadkaModel == null)
    		prehliadkaModel = new PrehliadkyModel(getActivity());
    	
    	masterList = prehliadkaModel.getPrehliadky();

		if (prehliadkyAdapter == null) {
			prehliadkyAdapter = new PrehliadkyArrayAdapter(getActivity(), 0, layout.item_prehliadky, masterList);
			listMaster.setAdapter(prehliadkyAdapter);
		} else {
            prehliadkyAdapter.clear();
            prehliadkyAdapter.addAll(masterList);
        }
    }
    
    public void refreshDetail(Bundle data) {
        final long id = selectedPrehliadky.rowId;

		if (selectedPrehliadky.typ == DMPrehliadkyMaster.eSTATIC) {
            if(id == prehliadkaModel.eVYBAVY
                || id == prehliadkaModel.ePOV_VYBAVY)
            {
                List<DMVybava> vybava = ((PortableCheckin) getActivity().getApplicationContext()).getVybavaList((id == prehliadkaModel.ePOV_VYBAVY));
                vybavaAdapter = new VybavaArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, vybava);
                listDetail.setAdapter(vybavaAdapter);
                vybavaAdapter.notifyDataSetChanged();
            } else if(id == prehliadkaModel.eSERVIS)
            {
                List<DMService> service = ((PortableCheckin) getActivity().getApplicationContext()).getServiceList();

                if (serviceAdapter == null) {
                    serviceAdapter = new ServiceArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, service);
                    listDetail.setAdapter(serviceAdapter);
                } else if (listDetail.getAdapter() != serviceAdapter)
                    listDetail.setAdapter(serviceAdapter);
                else
                    serviceAdapter.notifyDataSetChanged();
            }
		} else if(selectedPrehliadky.typ == DMPrehliadkyMaster.eUNIT) {
			List<DMUnit> unit = ((PortableCheckin) getActivity().getApplicationContext()).getUnitListByUnitId(selectedPrehliadky.unitId);
			listDetail.setAdapter(new UnitArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, unit));
		} else if (selectedPrehliadky.typ == DMPrehliadkyMaster.eGROUP) {
            paketArrayAdapter = new PacketsArrayAdapter(getActivity(), 0, layout.item_unit_packet, app.getPaket(selectedPrehliadky.groupNr));
            listDetail.setAdapter(paketArrayAdapter);
        }
    }
    
	@Override
	public void updateData(Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showData(Intent intent) {
		// TODO Auto-generated method stub
		
	}
}
