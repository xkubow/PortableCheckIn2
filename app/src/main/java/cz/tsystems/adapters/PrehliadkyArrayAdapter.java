package cz.tsystems.adapters;

import cz.tsystems.data.DMPrehliadky;
import cz.tsystems.data.DMPrehliadkyMaster;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;
import cz.tsystems.portablecheckin.ServiceActivity;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.hb.views.PinnedSectionListView;

import java.util.ArrayList;
import java.util.List;

public class PrehliadkyArrayAdapter extends ArrayAdapter<DMPrehliadkyMaster> implements PinnedSectionListView.PinnedSectionListAdapter{
    private Context context;
    ServiceActivity serviceActivity;

	public PrehliadkyArrayAdapter(Fragment owner, Context context, int resource, int textViewResourceId, List<DMPrehliadkyMaster> objects) {
        super(context, resource, textViewResourceId);// objects);
        this.context = context;
        this.serviceActivity = (ServiceActivity) owner;
        setObjets(objects);
	}

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return getItem(position).viewType;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_prehliadky, null);
        }

		DMPrehliadkyMaster prehliadka = getItem(position);

        if(prehliadka.viewType == prehliadka.eSECTION) {
            v.setBackgroundColor(context.getResources().getColor(R.color.grid_caption));
            ((TextView)v.findViewById(R.id.lblPrehliadka)).setText(prehliadka.sectionCaption);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.findViewById(R.id.lblPrehliadka).getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.leftMargin = 10;
            v.findViewById(R.id.lblPrehliadka).setLayoutParams(params);
            v.findViewById(R.id.chkPrehliadky).setVisibility(View.GONE);
        } else {
            TextView text = (TextView) v.findViewById(R.id.lblPrehliadka);
            CheckBox checkBox = (CheckBox)v.findViewById(R.id.chkPrehliadky);
            text.setText(prehliadka.text);
            if(!prehliadka.mandatory) {
                v.findViewById(R.id.chkPrehliadky).setVisibility(View.INVISIBLE);
            } else {
                v.findViewById(R.id.chkPrehliadky).setVisibility(View.VISIBLE);
                checkBox.setStaticChecked(prehliadka.opened);
            }
            checkBox.setTag(R.id.ListLine, v);
            checkBox.setTag(R.id.listPosition, position);
            checkBox.setOncheckListener(new CheckBox.OnCheckListener() {
                @Override
                public void onCheck(CheckBox checkBox, boolean isChecked) {
                    serviceActivity.masterClicked((View) checkBox.getTag(R.id.ListLine), (int) checkBox.getTag(R.id.listPosition));
                }
            });
        }

        return v;
	}

    public void addAll(List<DMPrehliadkyMaster> objects) {
        setObjets(objects);
    }

    private void setObjets(List<DMPrehliadkyMaster> objects) {

        int listPosition = 0;
        int sectionPosition = 0;
        int lastType = -1;
        String[] prehliadkaCaptions = context.getResources().getStringArray(R.array.Prehliadka);
        for (DMPrehliadkyMaster item : objects) {
            if(item.type != lastType) {
                // TODO prepisat titles
                DMPrehliadkyMaster section = new DMPrehliadkyMaster(prehliadkaCaptions[sectionPosition]);
                section.sectionPosition = sectionPosition;
                section.listPosition = listPosition++;
                add(section);
            }
            item.listPosition = listPosition++;
            item.sectionPosition = sectionPosition;
            add(item);

            if(item.type != lastType) {
                sectionPosition++;
                lastType = item.type;
            }
        }
    }


    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
