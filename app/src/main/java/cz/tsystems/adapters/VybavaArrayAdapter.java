package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.data.DMVybava;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

public class VybavaArrayAdapter extends ArrayAdapter<DMVybava> implements PinnedSectionListView.PinnedSectionListAdapter{
    private final static String TAG = VybavaArrayAdapter.class.getSimpleName();
	private Context context;
	private List<DMVybava> data;
	private List<DMVybava> filteredData;
	private Filter filter;
    private static EditText selectedText;
    private TreeSet mSeparatorsSet = new TreeSet();
    private static final int STATIC = 0;
    private static final int FREE = 1;
    LayoutInflater layoutInflater;

    private static class ViewHolder {
        public int typ;
        public TextView lblService;
        public EditText txtService;
        public com.gc.materialdesign.views.CheckBox checkBox;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(selectedText == null || selectedText.getEditableText() != s)
                return;

            ((FragmentPagerActivity)context).unsavedCheckin();
            final int pos = (int) selectedText.getTag(R.id.listPosition);
            getItem(pos).text = s.toString();
            ViewHolder vh = (ViewHolder) selectedText.getTag(R.id.ViewHolder);
            vh.checkBox.setChecked((s.length()>0));
            getItem(pos).setChecked(vh.checkBox.isChecked());
        }
    };

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            selectedText = (EditText) v;
            return false;
        }
    };

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return false;
        }
    };

	public VybavaArrayAdapter(Context context, int resource, int textViewResourceId, List<DMVybava> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		this.data = objects;
		this.filteredData  = objects;//new ArrayList<DMVybava>();
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int i = 0;
        for (DMVybava vybava : objects) {
            if (vybava.editable)
                mSeparatorsSet.add(i);
            i++;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? FREE : STATIC;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder;

        int type = getItemViewType(position);
        DMVybava vybava = getItem(position);
        Log.d(TAG, "TYPE :" + String.valueOf(type));

        if (v == null) {

            viewHolder = new ViewHolder();
            viewHolder.typ = type;

            switch (type) {
                case FREE:
                    v = layoutInflater.inflate(R.layout.item_free_vybavy, null);
                    viewHolder.txtService = (EditText)v.findViewById(R.id.txtVybavaText);
                    viewHolder.txtService.setTag(R.id.ViewHolder, viewHolder);
                    viewHolder.txtService.setTag(R.id.listPosition, position);
                    viewHolder.txtService.setOnTouchListener(touchListener);
                    viewHolder.txtService.addTextChangedListener(textWatcher);
                    viewHolder.txtService.setOnEditorActionListener(editorActionListener);
                    break;
                case STATIC:
                    v = layoutInflater.inflate(R.layout.item_vybava, null);
                    viewHolder.lblService = (TextView) v.findViewById(R.id.lblVybavaText);
                    break;
            }
            viewHolder.checkBox = (com.gc.materialdesign.views.CheckBox)v.findViewById(R.id.checkBox);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        if (viewHolder.typ == FREE) {
            viewHolder.txtService.setText((vybava.text != null)?vybava.text:"");
        } else
            viewHolder.lblService.setText(vybava.text);

        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setOncheckListener(new com.gc.materialdesign.views.CheckBox.OnCheckListener() {
            @Override
            public void onCheck(com.gc.materialdesign.views.CheckBox checkBox, boolean isChecked) {
                ((FragmentPagerActivity)context).unsavedCheckin();
                DMVybava vybava = filteredData.get((Integer) checkBox.getTag());
                vybava.checked = checkBox.isChecked();
            }
        });
        viewHolder.checkBox.setChecked(vybava.checked);

    	return v;
	}

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
	
}
