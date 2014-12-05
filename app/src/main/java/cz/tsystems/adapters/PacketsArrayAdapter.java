package cz.tsystems.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    public PacketsArrayAdapter(Context context, int resource, int textViewResourceId, List<DMPacket> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_unit_packet, null);
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

            ImageView imgView = (ImageView)v.findViewById(R.id.packetImageView);
            imgView.setImageDrawable(packet.getPacketIcon(context));


        }

        return v;
    }
}
