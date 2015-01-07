package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.DMVehicleHistory;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

import android.R.integer;
import android.content.Context;
import android.content.pm.LabeledIntent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

public class VehicleHistoryArrayAdapter extends ArrayAdapter<DMVehicleHistory> implements PinnedSectionListView.PinnedSectionListAdapter{
	final String TAG = VehicleHistoryArrayAdapter.class.getSimpleName();
	private Context context;
	private final String checkinCustomerId;
	private int zakazkaColor;
	private int historyType = -1;
	List<Integer> rowColorList = new ArrayList<Integer>();
	
	public VehicleHistoryArrayAdapter(Context context, int resource, int textViewResourceId, List<DMVehicleHistory> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		PortableCheckin app = (PortableCheckin) context.getApplicationContext();
		
    	if(app.getCheckin().customer_id.length() > 0)
    		checkinCustomerId = app.getCheckin().customer_id;
    	else
    		checkinCustomerId = "";
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_vehicle_history, null);
        }
        DMVehicleHistory vehicleHistory = PortableCheckin.vehicleHistoryList.get(position);
        
        
        if(vehicleHistory != null) {
        	
        	if(rowColorList.size() <= position) {
	        	if(vehicleHistory.history_type == 10)
	        			zakazkaColor = (zakazkaColor == 0)?0x440000FF:0;	
    	        rowColorList.add(zakazkaColor);	        	
        	}
    		v.setBackgroundColor(rowColorList.get(position));        	
        	
        	TextView tv = (TextView)v.findViewById(R.id.personalIdColor);
        	tv.setText("");
        	if(checkinCustomerId.equals(vehicleHistory.customer_id))
        		tv.setBackgroundColor(0xFF076414);
        	else
        		tv.setBackgroundColor(0xFFE50505);
        
    		tv = (TextView)v.findViewById(R.id.lblhistory_type_txt);        	
        	if(historyType != vehicleHistory.history_type) {
        		tv.setText(vehicleHistory.history_type_txt);
        		historyType = vehicleHistory.history_type;
        	}
        	else
        		tv.setText("");
        	
        	
        	tv = (TextView)v.findViewById(R.id.lblHistory_description);
        	tv.setText(vehicleHistory.history_description);
        	
        	tv = (TextView)v.findViewById(R.id.lblItem_no);
        	tv.setText(vehicleHistory.item_no);
        	
        	tv = (TextView)v.findViewById(R.id.lblPersonal_tag);
        	tv.setText(vehicleHistory.personal_tag);        	
        }
        
        return v;
}

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
