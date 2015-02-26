package cz.tsystems.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.TreeSet;

import cz.tsystems.data.DMPacketDetail;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 29.1.2015.
 */
public class PacketDetailArrayAdapter extends ArrayAdapter<DMPacketDetail> implements PinnedSectionListView.PinnedSectionListAdapter {

//    List<DMPacketDetail> packetDetailList;
    Context context;
    public double cenaND = 0.0, cenaPP = 0.0, cenaCelek = 0.0;

    public PacketDetailArrayAdapter(Context context, List<DMPacketDetail> objects) {
        super(context, R.layout.item_packet);
        this.context = context;
//        packetDetailList = objects;

        setObjets(objects);
    }

    private void setObjets(List<DMPacketDetail> objects) {

        int listPosition = 0;
        int sectionPosition = 0;
        int lastType = -1;
        String[] prehliadkaCaptions = context.getResources().getStringArray(R.array.DetailPaketuList);
        for (DMPacketDetail item : objects) {
            if(item.viewType != lastType) {

                DMPacketDetail section = new DMPacketDetail(prehliadkaCaptions[sectionPosition]);
                section.sectionPosition = sectionPosition;
                section.listPosition = listPosition++;
                add(section);
            }
            item.listPosition = listPosition++;
            item.sectionPosition = sectionPosition;
            if(item.sell_price != null) {
                cenaCelek += item.sell_price;
                if(item.viewType == DMPacketDetail.ND
                        || item.viewType == DMPacketDetail.SP)
                    cenaND += item.sell_price;
                else
                    cenaPP += item.sell_price;
            }

            add(item);

            if(item.viewType != lastType) {
                sectionPosition++;
                lastType = item.viewType;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (this.getItem(position).viewType == DMPacketDetail.eSECTION)?1:0;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
            final ImageView imgView = (ImageView) v.findViewById(R.id.packetImageView);
            final TextView packetText = (TextView) v.findViewById(R.id.packetText);
            final TextView detaiText = (TextView) v.findViewById(R.id.detailText);
            final TextView cenaText = (TextView) v.findViewById(R.id.lblCena);
            if(packet.viewType != DMPacketDetail.eSECTION) {
                Drawable drawable;
                if(packet.viewType == DMPacketDetail.PP)
                    drawable = getContext().getResources().getDrawable(R.drawable.ic_tools_petrol);
                else
                    drawable = getContext().getResources().getDrawable((packet.spare_part_dispon_id == 1)?R.drawable.tag_green:R.drawable.tag_orange);
                imgView.setImageDrawable(drawable);
                packetText.setText(packet.capaket_item_description);
                detaiText.setText(packet.capaket_item_number);
                ((RelativeLayout.LayoutParams) cenaText.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                if (packet.sell_price != null)
                    cenaText.setText(String.valueOf(packet.sell_price));
                else
                    cenaText.setText("");
            } else {
//                v.setLayoutParams( new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 200));
                v.setBackgroundColor(context.getResources().getColor(R.color.grid_caption));
                imgView.setVisibility(View.GONE);
                packetText.setText(packet.sectionCaption);
                detaiText.setVisibility(View.GONE);
                cenaText.setVisibility(View.GONE);
            }
            v.findViewById(R.id.btnInfo).setVisibility(View.GONE);
            v.findViewById(R.id.chkPaket).setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public boolean isEnabled (int position) {
        return false;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
