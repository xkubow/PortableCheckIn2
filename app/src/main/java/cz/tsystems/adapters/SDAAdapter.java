package cz.tsystems.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hb.views.PinnedSectionListView;
import java.util.List;
import cz.tsystems.data.DMSDA;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 12.1.2015.
 */
public class SDAAdapter extends ArrayAdapter<DMSDA> implements PinnedSectionListView.PinnedSectionListAdapter {
    private Context context;

    public SDAAdapter(Context context, int resource, int textViewResourceId, List<DMSDA> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_sda, null);
        }
        DMSDA dmsda = getItem(position);


        if(dmsda != null) {
            TextView tv = (TextView)v.findViewById(R.id.lblActionNo);
            tv.setText(dmsda.action_no);

            tv = (TextView)v.findViewById(R.id.lblActionType);
            tv.setText(dmsda.action_type);

            tv = (TextView)v.findViewById(R.id.lblActionTxt);
            tv.setText(dmsda.action_txt);
        }

        return v;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
