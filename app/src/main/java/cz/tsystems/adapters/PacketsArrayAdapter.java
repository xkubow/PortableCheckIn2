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

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.data.DMPacket;
import cz.tsystems.portablecheckin.PacketDetailDialog;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 2.12.2014.
 */
public class PacketsArrayAdapter extends ArrayAdapter<DMPacket> implements PinnedSectionListView.PinnedSectionListAdapter {
    Context context;
    boolean isUnitPaket;
    List<DMPacket> filteredData;
    List<DMPacket> mOriginalValues;


    public PacketsArrayAdapter(Context context, int resource, int textViewResourceId, List<DMPacket> objects, final boolean isUnitPaket) {
        super(context, resource, textViewResourceId, objects);
        filteredData = objects;
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
            if(packet.sell_price != null)
                tv.setText(String.valueOf(packet.sell_price));
            else
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

//    @Override
//    public DMPacket getItem(int position) {
//        return filteredData.get(position);
//    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }

    @Override
    public Filter getFilter() {
//http://stackoverflow.com/questions/13371160/arrayadapter-filtering-with-multiple-search-terms
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<DMPacket> FilteredArrList = new ArrayList<DMPacket>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<DMPacket>(filteredData); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).workshop_packet_description;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<DMPacket>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }
        };

        return filter;
    }
}
