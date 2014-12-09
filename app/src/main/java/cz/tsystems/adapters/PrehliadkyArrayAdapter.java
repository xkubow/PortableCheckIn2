package cz.tsystems.adapters;

import cz.tsystems.data.DMPrehliadky;
import cz.tsystems.data.DMPrehliadkyMaster;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PrehliadkyArrayAdapter extends ArrayAdapter<DMPrehliadkyMaster> {
    private Context context;

	public PrehliadkyArrayAdapter(Context context, int resource, int textViewResourceId, List<DMPrehliadkyMaster> objects) {
        super(context, resource, textViewResourceId, objects);

        this.context = context;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_prehliadky, null);
        }

		DMPrehliadkyMaster prehliadky = getItem(position);
		
		TextView text = (TextView) v.findViewById(R.id.lblPrehliadka);
		text.setText(prehliadky.text);

        return v;
	}
	
	
}
