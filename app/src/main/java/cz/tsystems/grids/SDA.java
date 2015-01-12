package cz.tsystems.grids;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cz.tsystems.adapters.SDAAdapter;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 12.1.2015.
 */
public class SDA  extends BaseGridActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = eSDA;
        getActionBar().setTitle(getResources().getString(R.string.Svolavacie_a_dielenske_akcie));
    }

    @Override
    public View getCaptionView()
    {
        View view = getLayoutInflater().inflate(R.layout.item_sda, null);
        view.setBackgroundColor(getResources().getColor(R.color.grid_caption));
        ((TextView)view.findViewById(R.id.lblActionNo)).setText(getResources().getText(R.string.Kod_zavad));
        ((TextView)view.findViewById(R.id.lblActionType)).setText(getResources().getText(R.string.Typ_akce));
        ((TextView)view.findViewById(R.id.lblActionTxt)).setText(getResources().getText(R.string.popis));
        return view;
    }

    @Override
    public void setListView() {
        if(PortableCheckin.sda == null) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam), Toast.LENGTH_SHORT).show();
            return;
        }

        SDAAdapter sdaAdapter = new SDAAdapter(
                this, 0, android.R.layout.simple_list_item_1,
                PortableCheckin.sda);
        listView.setAdapter(sdaAdapter);
    }
}
