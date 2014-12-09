package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.data.DMVybava;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.TextView;

public class VybavaArrayAdapter extends ArrayAdapter<DMVybava> {
	private Context context;
	private List<DMVybava> data;
	private List<DMVybava> filteredData;
	private Filter filter;

	public VybavaArrayAdapter(Context context, int resource, int textViewResourceId, List<DMVybava> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		this.data = objects;
		this.filteredData  = objects;//new ArrayList<DMVybava>();
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_vybava, null);
        }
        DMVybava vybava = filteredData.get(position);
        
		TextView text = (TextView) v.findViewById(R.id.lblVybavaText);
		text.setText(vybava.text);

        CheckBox vybCheck = (CheckBox)v.findViewById(R.id.checkBox);
        vybCheck.setChecked(vybava.checked);
        vybCheck.setTag(position);

        vybCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chckBtn = (CheckBox)v;
                DMVybava vybava = filteredData.get((Integer)chckBtn.getTag());
                vybava.checked = chckBtn.isChecked();
            }
        });
    	return v;
	}

	@Override
	public Filter getFilter(){

	    if(filter == null){
	        filter = new VybavaFilter();
	    }
	    return filter;
	}
	
	
	private class VybavaFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			String obligatoryStr = constraint.toString();
			
	        if (obligatoryStr == null || obligatoryStr.length() == 0){
	            List<DMVybava> list = new ArrayList<DMVybava>(data);
	            results.values = list;
	            results.count = list.size();
	        }else{
	            final ArrayList<DMVybava> list = new ArrayList<DMVybava>(data);
	            final ArrayList<DMVybava> nlist = new ArrayList<DMVybava>();
	            int count = list.size();

	            for (int i = 0; i<count; i++){
	                final DMVybava vybava = list.get(i);
	                final boolean obligatory_equipment = vybava.obligatory_equipment;
	                final boolean value = Boolean.parseBoolean(obligatoryStr);

	                if(value == obligatory_equipment){
	                    nlist.add(vybava);
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
			filteredData = (List<DMVybava>)results.values;
	        notifyDataSetChanged();
	        clear();
	        int count = filteredData.size();
	        for(int i = 0; i<count; i++){
	            add(filteredData.get(i));
	            notifyDataSetInvalidated();
	        }
	        if(filteredData == null)
	        	filteredData =  new ArrayList<DMVybava>();
			
		}
		
	}
	
}
