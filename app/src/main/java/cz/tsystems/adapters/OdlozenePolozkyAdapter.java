package cz.tsystems.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;

import cz.tsystems.data.DMOdlozenePolozky;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 12.1.2015.
 */
public class OdlozenePolozkyAdapter  extends ArrayAdapter<DMOdlozenePolozky> implements PinnedSectionListView.PinnedSectionListAdapter {
    private Context context;

    public OdlozenePolozkyAdapter(Context context, int resource, int textViewResourceId, List<DMOdlozenePolozky> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_odlozene_polozky, null);
        }
        DMOdlozenePolozky odlozenePolozky = getItem(position);


        if(odlozenePolozky != null) {
            TextView tv = (TextView)v.findViewById(R.id.lblDemandDescription);
            tv.setText(odlozenePolozky.demand_description);
            if(odlozenePolozky.sell_price != "null") {
                tv = (TextView) v.findViewById(R.id.lblSellPrice);
                tv.setText(odlozenePolozky.sell_price);
            }
        }

        return v;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
