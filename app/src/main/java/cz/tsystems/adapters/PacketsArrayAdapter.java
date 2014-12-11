package cz.tsystems.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cz.tsystems.data.DMPacket;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 2.12.2014.
 */
public class PacketsArrayAdapter extends ArrayAdapter<DMPacket> {
    Context context;
    boolean isUnitPaket;
    public PacketsArrayAdapter(Context context, int resource, int textViewResourceId, List<DMPacket> objects, final boolean isUnitPaket) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.isUnitPaket = isUnitPaket;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_packet, null);
        }


        DMPacket packet = getItem(position);

//        DMCustomerInfo customerInfo = PortableCheckin.custumerInfoList.get(position);

        if(packet != null) {
            TextView tv = (TextView)v.findViewById(R.id.text);
            tv.setText(packet.workshop_packet_description);

            tv = (TextView)v.findViewById(R.id.detailText);
            tv.setText(packet.restrictions);

            tv = (TextView)v.findViewById(R.id.lblCena);
            tv.setText(String.valueOf(packet.sell_price));

            if(!isUnitPaket) {
                CheckBox chkPaket = (CheckBox) v.findViewById(R.id.chkPaket);
                chkPaket.setVisibility(View.VISIBLE);
            }

            ImageView imgView = (ImageView)v.findViewById(R.id.packetImageView);
            imgView.setImageDrawable(packet.getPacketIcon(context));


        }

        return v;
    }
}
