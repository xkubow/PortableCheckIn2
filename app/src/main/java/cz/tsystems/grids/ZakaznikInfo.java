package cz.tsystems.grids;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cz.tsystems.adapters.CustomerInfoAdapter;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 9.1.2015.
 */
public class ZakaznikInfo extends BaseGridActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = eZAKAZNIKINFO;
        getActionBar().setTitle(getResources().getString(R.string.Informace_o_zakaznikovi));
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
        if(PortableCheckin.custumerInfoList == null) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam), Toast.LENGTH_SHORT).show();
            return;
        }

        CustomerInfoAdapter customerInfoAdapter = new CustomerInfoAdapter(
                this, 0, android.R.layout.simple_list_item_1,
                PortableCheckin.custumerInfoList);
        listView.setAdapter(customerInfoAdapter);
    }

    @Override
    public void setWindowParams() {
    }
};
