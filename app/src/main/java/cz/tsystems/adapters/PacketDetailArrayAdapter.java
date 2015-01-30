package cz.tsystems.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;

import cz.tsystems.data.DMPacketDetail;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 29.1.2015.
 */
public class PacketDetailArrayAdapter extends ArrayAdapter<DMPacketDetail> implements PinnedSectionListView.PinnedSectionListAdapter {

    public PacketDetailArrayAdapter(Context context, List<DMPacketDetail> objects) {
        super(context, R.layout.item_packet, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_packet, null);
        }
        DMPacketDetail packet = getItem(position);

        if(packet != null) {
            ImageView imgView = (ImageView)v.findViewById(R.id.packetImageView);
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_tools_petrol));

            TextView tv = (TextView)v.findViewById(R.id.packetText);
            tv.setText(packet.capaket_item_description);

            tv = (TextView)v.findViewById(R.id.detailText);
            tv.setText(packet.capaket_item_number);

            tv = (TextView)v.findViewById(R.id.lblCena);
            ((RelativeLayout.LayoutParams)tv.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            if(packet.sell_price != null)
                tv.setText(String.valueOf(packet.sell_price));
            else
                tv.setText("");

            v.findViewById(R.id.btnInfo).setVisibility(View.GONE);
            v.findViewById(R.id.chkPaket).setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
