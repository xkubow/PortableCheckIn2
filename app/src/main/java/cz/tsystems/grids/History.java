package cz.tsystems.grids;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.adapters.VehicleHistoryArrayAdapter;
import cz.tsystems.data.DMVehicleHistory;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 9.1.2015.
 */
public class History extends BaseGridActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getBooleanExtra("shortVersion", false))
            getActionBar().setTitle(getResources().getString(R.string.historia));
        else
            getActionBar().setTitle(getResources().getString(R.string.strucHistory));
    }

    @Override
    public View getCaptionView()
    {
        View view = getLayoutInflater().inflate(R.layout.item_vehicle_history, null);
        view.setBackgroundColor(getResources().getColor(R.color.grid_caption));
        ((TextView)view.findViewById(R.id.personalIdColor)).setText("");
        ((TextView)view.findViewById(R.id.lblhistory_type_txt)).setText(getResources().getText(R.string.typ));
        ((TextView)view.findViewById(R.id.lblHistory_description)).setText(getResources().getText(R.string.popis));
        ((TextView)view.findViewById(R.id.lblItem_no)).setText(getResources().getText(R.string.pp_nd_c));
        ((TextView)view.findViewById(R.id.lblPersonal_tag)).setText(getResources().getText(R.string.os_c));

        return view;
    }



    @Override
    public void setListView() {
        List<DMVehicleHistory> theVehicleHistory = null;

        if(getIntent().getBooleanExtra("shortVersion", false)) {
            theVehicleHistory = new ArrayList<DMVehicleHistory>();
            for (DMVehicleHistory vh : PortableCheckin.vehicleHistoryList) {
                if (vh.brief_history)
                    theVehicleHistory.add(vh);
            }
        }else
            theVehicleHistory = PortableCheckin.vehicleHistoryList;

        if(theVehicleHistory == null) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam),Toast.LENGTH_SHORT).show();
            return;
        }

        VehicleHistoryArrayAdapter vehicleHistoryAdapter = new VehicleHistoryArrayAdapter(
                this, 0, android.R.layout.simple_list_item_1,
                theVehicleHistory);
        listView.setAdapter(vehicleHistoryAdapter);
    }
}
