package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.data.DMUnit;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

public class UnitArrayAdapter extends ArrayAdapter<DMUnit> {

	private Context context;
	private List<DMUnit> data;
    private List<DMUnit> filteredData;
	
	public UnitArrayAdapter(Context context, int resource, int textViewResourceId, List<DMUnit> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
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
        if(unit.sell_price >= 0.0)
            text.setText(String.valueOf(unit.sell_price) + "Kč");
        else
            text.setText("");

        CheckBox vybCheck = (CheckBox)v.findViewById(R.id.checkBox);
        vybCheck.setChecked((unit.chck_status_id == 1));
		return v;
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
