package cz.tsystems.adapters;

import java.util.List;

import cz.tsystems.data.DMUnit;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class UnitArrayAdapter extends ArrayAdapter<DMUnit> {

	private Context context;
	private List<DMUnit> data;
	
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
            v = vi.inflate(R.layout.item_vybava, null);
        }
        DMUnit unit = data.get(position);

        TextView text = (TextView) v.findViewById(R.id.lblVybavaText);
		text.setText(unit.chck_part_txt);
		return v;
	}	
}
