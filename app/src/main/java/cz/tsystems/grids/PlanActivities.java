package cz.tsystems.grids;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cz.tsystems.adapters.PlannedActivitiesAdapter;
import cz.tsystems.adapters.VehicleInfoAdapter;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 9.1.2015.
 */
public class PlanActivities extends BaseGridActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = ePLANACTIVITIES;
        getActionBar().setTitle(getResources().getString(R.string.Planovane_cinnosti));
    }

    @Override
    public View getCaptionView()
    {
        View view = getLayoutInflater().inflate(R.layout.item_plan_activities, null);
        view.setBackgroundColor(getResources().getColor(R.color.grid_caption));
        ((TextView)view.findViewById(R.id.lblProvozovna)).setText(getResources().getText(R.string.provozovna));
        ((TextView)view.findViewById(R.id.lblTym)).setText(getResources().getText(R.string.tym));
        ((TextView)view.findViewById(R.id.lblCinnost)).setText(getResources().getText(R.string.cinnost));
        return view;
    }

    @Override
    public void setListView() {
        if(PortableCheckin.plannedActivitiesList == null) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam), Toast.LENGTH_SHORT).show();
            return;
        }

        PlannedActivitiesAdapter plannedActivitiesAdapter = new PlannedActivitiesAdapter(
                this, 0, android.R.layout.simple_list_item_1,
                PortableCheckin.plannedActivitiesList);
        listView.setAdapter(plannedActivitiesAdapter);
    }
};
