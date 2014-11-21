package cz.tsystems.portablecheckin;

import java.util.List;

import cz.tsystems.adapters.PrehliadkyCursorAdapter;
import cz.tsystems.adapters.ServiceArrayAdapter;
import cz.tsystems.adapters.UnitArrayAdapter;
import cz.tsystems.adapters.VybavaArrayAdapter;
import cz.tsystems.base.BaseFragment;
import cz.tsystems.data.DMPrehliadky;
import cz.tsystems.data.DMService;
import cz.tsystems.data.DMUnit;
import cz.tsystems.data.DMVybava;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.model.PrehliadkyModel;
import cz.tsystems.model.UnitsModel;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import static cz.tsystems.portablecheckin.R.*;
import static cz.tsystems.portablecheckin.R.drawable.celky_povinny_ok;

public class ServiceActivity extends BaseFragment {
	
    private final static String TAG = ServiceActivity.class.getSimpleName();	
//    public static final String ARG_SECTION_NUMBER = "section_number";
	private PrehliadkyModel prehliadkaModel;
	private PrehliadkyCursorAdapter prehliadkyAdapter;
	private VybavaArrayAdapter vybavaAdapter;
    private ServiceArrayAdapter serviceAdapter;
	
    private static View rootView;
    ListView listMaster, listDetail;
    private Cursor masterCursor = null;
	private UnitArrayAdapter unitAdapter;
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
				Cursor c = prehliadkaModel.getPrehliadky();
				c.moveToPosition(position);
				Bundle data = new Bundle();
//				long unitId = c.getLong(c.getColumnIndex("CHCK_UNIT_ID"));
//				if(c.getLong(c.getColumnIndex("CHCK_UNIT_ID")) == -1) {
                    data.putLong("PREHLIADKA_ID", c.getLong(c.getColumnIndex("_id")));
					data.putLong("CHCK_UNIT_ID", c.getLong(c.getColumnIndex("CHCK_UNIT_ID")));
					data.putBoolean("OBLIGATORY", c.getInt(c.getColumnIndex("_id")) == -2); //povinne vybavy
//				}
				refreshDetail(data);
			}
		});

        listDetail.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DMVybava vybava = vybavaAdapter.getItem(position);
                CheckBox b = (CheckBox)view.findViewById(R.id.btnCheck);
                vybava.checked = !vybava.checked;
                b.setChecked(vybava.checked);

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
    	
    	masterCursor = prehliadkaModel.getPrehliadky();
    	
		if (prehliadkyAdapter == null) {
			prehliadkyAdapter = new PrehliadkyCursorAdapter(getActivity(), layout.item_prehliadky, masterCursor);
			listMaster.setAdapter(prehliadkyAdapter);
		} else
			prehliadkyAdapter.changeCursor(masterCursor);
    }
    
    public void refreshDetail(Bundle data) {
        final long id = data.getLong("PREHLIADKA_ID");
        final long unit_id = data.getLong("CHCK_UNIT_ID");
		final boolean mandatory = data.getBoolean("OBLIGATORY");

		if (unit_id == -1) {
            if(id == PrehliadkyModel.prehliadka_id[PrehliadkyModel.PREHLAIDKA_ENUM.eVYBAVY.ordinal()]
                || id == PrehliadkyModel.prehliadka_id[PrehliadkyModel.PREHLAIDKA_ENUM.ePOV_VYBAVY.ordinal()])
            {
                List<DMVybava> vybava = ((PortableCheckin) getActivity().getApplicationContext()).getVybavaList();

                if (vybavaAdapter == null) {
                    vybavaAdapter = new VybavaArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, vybava);
                    listDetail.setAdapter(vybavaAdapter);
                } else if (listDetail.getAdapter() != vybavaAdapter)
                    listDetail.setAdapter(vybavaAdapter);
                else
                    vybavaAdapter.notifyDataSetChanged();

                vybavaAdapter.getFilter().filter(String.valueOf(mandatory));
            } else if(id == PrehliadkyModel.prehliadka_id[PrehliadkyModel.PREHLAIDKA_ENUM.eSERVIS.ordinal()])
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
		} else {

			List<DMUnit> unit = ((PortableCheckin) getActivity().getApplicationContext()).getUnitListByUnitId(unit_id); 

//			if (unitAdapter == null) {
//				unitAdapter = new UnitArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, unit);
				listDetail.setAdapter(new UnitArrayAdapter(getActivity(), 0, android.R.layout.simple_list_item_1, unit));
//			} else if(listDetail.getAdapter() != unitAdapter)		
//				listDetail.setAdapter(unitAdapter);
//			else
//				unitAdapter.notifyDataSetChanged();		
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
