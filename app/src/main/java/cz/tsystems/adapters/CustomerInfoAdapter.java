package cz.tsystems.adapters;

import java.util.List;

import cz.tsystems.data.DMCustomerInfo;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomerInfoAdapter extends ArrayAdapter<DMCustomerInfo> {
	private Context context;
	
	public CustomerInfoAdapter(Context context, int resource, int textViewResourceId, List<DMCustomerInfo> objects) {
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
        DMCustomerInfo customerInfo = PortableCheckin.custumerInfoList.get(position);
        
        
        if(customerInfo != null) {
        	TextView tv = (TextView)v.findViewById(R.id.lblValName);
        	tv.setText(customerInfo.description);
        	        	
        	tv = (TextView)v.findViewById(R.id.lblValue);
        	tv.setText(customerInfo.value);
        }
        
        return v;
	}

}
