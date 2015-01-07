package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cz.tsystems.data.DMPlannedOrder;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

public class PlannedOrderAdapter extends ArrayAdapter<DMPlannedOrder> {
	private Context context;
    public static class Captions {
        public static String caption;
        public static int position;

        public Captions(final String newCaption, final int newPosition) {
            caption = newCaption;
            position = newPosition;
        }
        public Captions(final int newPosition) {
            position = newPosition;
        }

        @Override
        public boolean equals(Object v) {
            boolean retVal = false;

            if (v instanceof Captions){
                Captions ptr = (Captions) v;
                retVal = ptr.position == this.position;
            }

            return retVal;
        }
    }

    public List<Captions> sectionCaptionPositions = new ArrayList<Captions>();
	
	public PlannedOrderAdapter(Context context, int textViewResourceId, List<DMPlannedOrder> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_planned_order, null);
        }
        PortableCheckin app = (PortableCheckin)getContext().getApplicationContext();
        DMPlannedOrder plannedOrder = app.getPlanZakazk().get(position);
        
        
        if(plannedOrder != null) {
        	TextView tv = (TextView)v.findViewById(R.id.lbldataSource);
        	tv.setText(plannedOrder.data_source);
        	
        	tv = (TextView)v.findViewById(R.id.lblPlannedOrderStatus);
        	tv.setText(plannedOrder.planned_order_status);
        	
        	tv = (TextView)v.findViewById(R.id.lblLicenseTag);
        	tv.setText(plannedOrder.license_tag);
        	
        	tv = (TextView)v.findViewById(R.id.lblVehicleDescription);
        	tv.setText(plannedOrder.vehicle_description);
        	
        	tv = (TextView)v.findViewById(R.id.lblCustomerLabel);
        	tv.setText(plannedOrder.customer_label);
        	
        	tv = (TextView)v.findViewById(R.id.lblPlannedOrderNo);
        	tv.setText(plannedOrder.planned_order_no);        	
        }
        
        return v;
	}
}
