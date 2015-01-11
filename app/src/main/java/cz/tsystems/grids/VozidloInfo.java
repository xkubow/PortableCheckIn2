package cz.tsystems.grids;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cz.tsystems.adapters.VehicleInfoAdapter;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 9.1.2015.
 */
public class VozidloInfo extends BaseGridActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = eVOZIDLOINFO;
        getActionBar().setTitle(getResources().getString(R.string.Informace_o_Vozidle));
    }

    @Override
    public View getCaptionView()
    {
        View view = getLayoutInflater().inflate(R.layout.item_info_list, null);
        view.setBackgroundColor(getResources().getColor(R.color.grid_caption));
        ((TextView)view.findViewById(R.id.lblValName)).setText(getResources().getText(R.string.typ));
        ((TextView)view.findViewById(R.id.lblValue)).setText(getResources().getText(R.string.popis));
        return view;
    }

    @Override
    public void setListView() {
        if(PortableCheckin.vehicleInfoList == null) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam), Toast.LENGTH_SHORT).show();
            return;
        }

        VehicleInfoAdapter vehicleInfoAdapter = new VehicleInfoAdapter(
                this, 0, android.R.layout.simple_list_item_1,
                PortableCheckin.vehicleInfoList);
        listView.setAdapter(vehicleInfoAdapter);
    }
};
