package cz.tsystems.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;

import cz.tsystems.data.DMPlannedActivities;
import cz.tsystems.data.DMVehicleInfo;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

public class PlannedActivitiesAdapter extends ArrayAdapter<DMPlannedActivities> implements PinnedSectionListView.PinnedSectionListAdapter {
	private Context context;

	public PlannedActivitiesAdapter(Context context, int resource, int textViewResourceId, List<DMPlannedActivities> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_plan_activities, null);
        }
        DMPlannedActivities planedActivities = getItem(position);
        
        
        if(planedActivities != null) {
            ((TextView)v.findViewById(R.id.lblProvozovna)).setText(planedActivities.workshop_description);
            ((TextView)v.findViewById(R.id.lblTym)).setText(planedActivities.team_description);
            ((TextView)v.findViewById(R.id.lblCinnost)).setText(planedActivities.planned_activity);
        }
        
        return v;
	}

    @Override
    public boolean isEnabled (int position) {
        return false;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
