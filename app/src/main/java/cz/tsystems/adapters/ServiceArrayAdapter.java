package cz.tsystems.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import cz.tsystems.data.DMService;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 21.11.2014.
 */
public class ServiceArrayAdapter extends ArrayAdapter<DMService> {
    private Context context;
    private List<DMService> data;

    public ServiceArrayAdapter(Context context, int resource, int textViewResourceId, List<DMService> objects) {
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
        DMService service = data.get(position);

        TextView text = (TextView) v.findViewById(R.id.lblVybavaText);
        text.setText(service.text);

        CheckBox vybCheck = (CheckBox)v.findViewById(R.id.checkBox);
        vybCheck.setChecked(service.checked);
        return v;
    }
}
