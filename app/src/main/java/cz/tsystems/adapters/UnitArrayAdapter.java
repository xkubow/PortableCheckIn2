package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMUnit;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;
import cz.tsystems.portablecheckin.UnitServiceActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UnitArrayAdapter extends ArrayAdapter<DMUnit> {

    private final static String TAG = UnitArrayAdapter.class.getSimpleName();
	private Context context;
	private List<DMUnit> data;
    private List<DMUnit> filteredData;
    private PortableCheckin app;
    private Button selectedButton;
	
	public UnitArrayAdapter(Context context, int resource, int textViewResourceId, List<DMUnit> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
        app = (PortableCheckin)context.getApplicationContext();
		this.data = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_unit, null);
        }
        DMUnit unit = data.get(position);

        TextView text = (TextView) v.findViewById(R.id.lblText);
		text.setText(unit.chck_part_txt);

        text = (TextView) v.findViewById(R.id.lblPosition);
        text.setText(unit.chck_position_abbrev_txt);

        text = (TextView) v.findViewById(R.id.lblRequired);
        text.setText(unit.chck_required_txt);

        text = (TextView) v.findViewById(R.id.lblCena);
        if(unit.sell_price != null)
            text.setText(String.valueOf(unit.sell_price) + "Kč");
        else
            text.setText("");

        CheckBox chkUnit = (CheckBox)v.findViewById(R.id.checkBox);
        chkUnit.setChecked((unit.chck_status_id == 1));
        chkUnit.setTag(position);
        chkUnit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chckBtn = (CheckBox)v;
                int position = (Integer)chckBtn.getTag();
                DMUnit unit = data.get(position);
                unit.chck_status_id = chckBtn.isChecked()?1:0;
            }
        });
        Button b = (Button)v.findViewById(R.id.btnService);
        b.setTag(position);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showServiceView((Button)v);
            }
        });


		return v;
	}

    private void showServiceView(final Button unitButtonView) {
        final DMUnit u = data.get((Integer)unitButtonView.getTag());
        final List<DMPacket> packetList = new ArrayList<DMPacket>();
        List<DMPacket> packets = app.getPackets();
        if(packets != null) {
            Iterator<DMPacket> packetIterator = app.getPackets().iterator();
            DMPacket packet = null;

            while (packetIterator.hasNext()) {
                try {
                    packet = packetIterator.next();
                    if (packet.chck_unit_id == u.chck_unit_id
                            && packet.chck_part_id == u.chck_part_id)
                        packetList.add(packet);
                } catch (NoSuchElementException e) {
                    app.getDialog(getContext(), "error", e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON);
                }
            }
        }

        packetList.addAll(app.getUnitService(u));

        UnitServiceActivity b = new UnitServiceActivity(getContext(), u, packetList){
            @Override
            public void OnOkClick(final DMPacket selectedPaked, final String cena){
                if(selectedPaked.workshop_packet_number != null) {
                    u.packet = selectedPaked;
                    u.chck_required_id = 19;
                    u.chck_required_txt = selectedPaked.workshop_packet_description;
                    if(selectedPaked.restrictions != null)
                        u.chck_required_txt += " " + selectedPaked.restrictions;
                } else {
                    u.packet = null;
                    u.chck_required_txt = selectedPaked.workshop_packet_description;
                    u.chck_required_id = selectedPaked.chck_required_id;
                }
                View v = (View)unitButtonView.getParent();
                TextView tv = (TextView)v.findViewById(R.id.lblRequired);
                tv.setText(u.chck_required_txt);
                tv = (TextView)v.findViewById(R.id.lblCena);
                u.sell_price = cena;
                tv.setText(u.sell_price);
                unitButtonView.setBackground(selectedPaked.getPacketIcon(getContext()));
            }
        };
        b.show();

    }


    private class UnitFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String obligatoryStr = constraint.toString();

            if (obligatoryStr == null || obligatoryStr.length() == 0){
                List<DMUnit> list = new ArrayList<DMUnit>(data);
                results.values = list;
                results.count = list.size();
            }else{
                final ArrayList<DMUnit> list = new ArrayList<DMUnit>(data);
                final ArrayList<DMUnit> nlist = new ArrayList<DMUnit>();
                int count = list.size();

                for (int i = 0; i<count; i++){
                    final DMUnit unit = list.get(i);


                    final long obligatory_equipment = unit.chck_unit_id;
                    final long value = Long.parseLong(obligatoryStr);

                    //TODO do the filtering

                    if(value == obligatory_equipment){
                        nlist.add(unit);
                    }
                    results.values = nlist;
                    results.count = nlist.size();
                }
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (List<DMUnit>)results.values;
            notifyDataSetChanged();
            clear();
            int count = filteredData.size();
            for(int i = 0; i<count; i++){
                add(filteredData.get(i));
                notifyDataSetInvalidated();
            }
            if(filteredData == null)
                filteredData =  new ArrayList<DMUnit>();

        }

    }
}
