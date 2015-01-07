package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import cz.tsystems.data.DMPlannedOrder;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

public class PlannedOrderAdapter extends ArrayAdapter<DMPlannedOrder> implements PinnedSectionListView.PinnedSectionListAdapter {
	private PortableCheckin app;
    private Context context;

	public PlannedOrderAdapter(Context context, int textViewResourceId, List<DMPlannedOrder> objects) {
		super(context, textViewResourceId, android.R.id.text1);
        this.context = context;
		this.app = (PortableCheckin) context.getApplicationContext();
        setObjets(objects);
	}

    private void setObjets(List<DMPlannedOrder> objects) {

        int listPosition = 0;
        boolean doSection = true;
        for (DMPlannedOrder item : objects) {
            if((item.personal_id == app.user.personal_id)) {
                if(doSection) {
                    DMPlannedOrder section = new DMPlannedOrder(context.getResources().getString(R.string.My));
                    section.sectionPosition = 0;
                    section.listPosition = listPosition++;
                    add(section);
                    doSection = false;
                }
                item.listPosition = listPosition++;
                item.sectionPosition = 0;
                add(item);
            }
        }

        doSection = true;
        for (DMPlannedOrder item : objects) {
            if((item.personal_id != app.user.personal_id)) {
                if(doSection) {
                    DMPlannedOrder section = new DMPlannedOrder(context.getResources().getString(R.string.Others));
                    section.sectionPosition = 1;
                    section.listPosition = listPosition++;
                    add(section);
                    doSection = false;
                }
                item.listPosition = listPosition++;
                item.sectionPosition = 1;
                add(item);
            }
        }

    }

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == DMPlannedOrder.SECTION;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_planned_order, null);
        }
        PortableCheckin app = (PortableCheckin)getContext().getApplicationContext();
        DMPlannedOrder plannedOrder = getItem(position);
        
        
        if(plannedOrder != null) {
            if(plannedOrder.type == DMPlannedOrder.SECTION) {
                v.setBackgroundColor(context.getResources().getColor(R.color.grid_caption));
                ((TextView)v.findViewById(R.id.lbldataSource)).setText(plannedOrder.sectionCaption);
                ((TextView)v.findViewById(R.id.lblPlannedOrderStatus)).setText("");
                ((TextView)v.findViewById(R.id.lblLicenseTag)).setText("");
                ((TextView)v.findViewById(R.id.lblVehicleDescription)).setText("");
                ((TextView)v.findViewById(R.id.lblCustomerLabel)).setText("");
                ((TextView)v.findViewById(R.id.lblPlannedOrderNo)).setText("");
                return v;
            }
            ((TextView)v.findViewById(R.id.lbldataSource)).setText(plannedOrder.data_source);
            ((TextView)v.findViewById(R.id.lblPlannedOrderStatus)).setText(plannedOrder.planned_order_status);
            ((TextView)v.findViewById(R.id.lblLicenseTag)).setText(plannedOrder.license_tag);
            ((TextView)v.findViewById(R.id.lblVehicleDescription)).setText(plannedOrder.vehicle_description);
            ((TextView)v.findViewById(R.id.lblCustomerLabel)).setText(plannedOrder.customer_label);
            ((TextView)v.findViewById(R.id.lblPlannedOrderNo)).setText(plannedOrder.planned_order_no);
        }
        
        return v;
	}
}
