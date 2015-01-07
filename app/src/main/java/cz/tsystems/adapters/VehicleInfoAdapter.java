package cz.tsystems.adapters;

import java.util.List;

import cz.tsystems.data.DMVehicleInfo;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

public class VehicleInfoAdapter extends ArrayAdapter<DMVehicleInfo> implements PinnedSectionListView.PinnedSectionListAdapter {
	private Context context;
	
	public VehicleInfoAdapter(Context context, int resource, int textViewResourceId, List<DMVehicleInfo> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_info_list, null);
        }
        DMVehicleInfo vehicleinfo = PortableCheckin.vehicleInfoList.get(position);
        
        
        if(vehicleinfo != null) {
        	TextView tv = (TextView)v.findViewById(R.id.lblValName);
        	tv.setText(vehicleinfo.description);
        	        	
        	tv = (TextView)v.findViewById(R.id.lblValue);
        	tv.setText(vehicleinfo.value);
        }
        
        return v;
	}

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
