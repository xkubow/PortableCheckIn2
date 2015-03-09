package cz.tsystems.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cz.tsystems.data.DMPacket;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.PacketDetailDialog;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 2.12.2014.
 */
public class PacketsArrayAdapter extends ArrayAdapter<DMPacket> implements PinnedSectionListView.PinnedSectionListAdapter {
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
            TextView tv = (TextView)v.findViewById(R.id.packetText);
            tv.setText(packet.workshop_packet_description);

            tv = (TextView)v.findViewById(R.id.detailText);
            tv.setText(packet.restrictions);

            tv = (TextView)v.findViewById(R.id.lblCena);
            if(packet.sell_price != null) {
                NumberFormat numberFormat = NumberFormat.getInstance();
                tv.setText(numberFormat.format(packet.sell_price) + " " + PortableCheckin.setting.currency_abbrev);
            } else
                tv.setText("");

            if(isUnitPaket) {
                v.findViewById(R.id.chkPaket).setVisibility(View.GONE);
                v.findViewById(R.id.btnInfo).setVisibility(View.GONE);
                ((RelativeLayout.LayoutParams)tv.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                ImageButton btnInfo = (ImageButton) v.findViewById(R.id.btnInfo);
                btnInfo.setTag(position);
                btnInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DMPacket packet1 = getItem((int)v.getTag());
                        PacketDetailDialog packetDetailDialog = new PacketDetailDialog(getContext(),packet1.detail_list);
                        packetDetailDialog.setTitle(packet1.workshop_packet_number + " - " + packet1.workshop_packet_description);
                        packetDetailDialog.show();
                    }
                });
                com.gc.materialdesign.views.CheckBox chkPaket = (com.gc.materialdesign.views.CheckBox)v.findViewById(R.id.chkPaket);
                chkPaket.setTag(position);
                chkPaket.setStaticChecked(packet.checked);
                chkPaket.setOncheckListener( new com.gc.materialdesign.views.CheckBox.OnCheckListener() {
                    @Override
                    public void onCheck(com.gc.materialdesign.views.CheckBox checkBox, boolean isChecked) {
                        PacketsArrayAdapter.this.getItem((int) checkBox.getTag()).checked = isChecked;
                    }
                });
            }

            ImageView imgView = (ImageView)v.findViewById(R.id.packetImageView);
            imgView.setImageDrawable(packet.getCelkyIcon(context));
        }

        return v;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }

}
