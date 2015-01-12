package cz.tsystems.grids;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cz.tsystems.adapters.OdlozenePolozkyAdapter;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 12.1.2015.
 */
public class OdlozenePolozky extends BaseGridActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = eODLOZENEPOLOZKY;
        getActionBar().setTitle(getResources().getString(R.string.Odlozene_sluzby));
    }

    @Override
    public View getCaptionView()
    {
        View view = getLayoutInflater().inflate(R.layout.item_odlozene_polozky, null);
        view.setBackgroundColor(getResources().getColor(R.color.grid_caption));
        ((TextView)view.findViewById(R.id.lblDemandDescription)).setText(getResources().getText(R.string.Popis));
        ((TextView)view.findViewById(R.id.lblSellPrice)).setText(getResources().getText(R.string.Cena));
        return view;
    }

    @Override
    public void setListView() {
        if(PortableCheckin.odlozenePolozky == null) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam), Toast.LENGTH_SHORT).show();
            return;
        }

        OdlozenePolozkyAdapter odlozenePolozkyAdapter = new OdlozenePolozkyAdapter(
                this, 0, android.R.layout.simple_list_item_1,
                PortableCheckin.odlozenePolozky);
        listView.setAdapter(odlozenePolozkyAdapter);
    }
}
