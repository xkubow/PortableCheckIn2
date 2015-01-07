package cz.tsystems.adapters;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;

import cz.tsystems.data.DMService;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 21.11.2014.
 */
public class ServiceArrayAdapter extends ArrayAdapter<DMService> implements PinnedSectionListView.PinnedSectionListAdapter{
    private final static String TAG = ServiceArrayAdapter.class.getSimpleName();

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
        final DMService service = data.get(position);

        TextView text = (TextView) v.findViewById(R.id.lblVybavaText);
        text.setText(service.text);

        final CheckBox serCheck = (CheckBox)v.findViewById(R.id.checkBox);
        serCheck.setTag(position);
        serCheck.setChecked(service.checked);

        serCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chkBtn = (CheckBox)v;
                DMService serice = data.get((Integer)chkBtn.getTag());
                service.checked = chkBtn.isChecked();
            }
        });

        return v;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
