package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final int pos = (int) selectedText.getTag(R.id.txtVybavaText);
            Log.d(TAG, String.valueOf(pos));
            getItem(pos).text = s.toString();
            com.gc.materialdesign.views.CheckBox checkBox = (com.gc.materialdesign.views.CheckBox) selectedText.getTag(R.id.checkBox);
            checkBox.setChecked((s.length()>0));
            getItem(pos).setChecked(checkBox.isChecked());
        }

        @Override
        public void afterTextChanged(Editable s) {

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

        int type = getItemViewType(position);
        DMVybava vybava = getItem(position);
        Log.d(TAG, "TYPE :" + String.valueOf(type));

        if (v == null) {
            switch (type) {
                case FREE:
                    v = layoutInflater.inflate(R.layout.item_free_vybavy, null);
                    EditText editText = (EditText)v.findViewById(R.id.txtVybavaText);
                    editText.setTag(R.id.txtVybavaText, position);
                    editText.setTag(R.id.checkBox, v.findViewById(R.id.checkBox));
                    editText.setOnTouchListener(touchListener);
                    editText.addTextChangedListener(textWatcher);
                    editText.setOnEditorActionListener(editorActionListener);
                    break;
                case STATIC:
                    v = layoutInflater.inflate(R.layout.item_vybava, null);
                    break;
            }
        }

        if(!vybava.editable) {
            TextView text = (TextView) v.findViewById(R.id.lblVybavaText);
            text.setText(vybava.text);
        }

        com.gc.materialdesign.views.CheckBox vybCheck = (com.gc.materialdesign.views.CheckBox)v.findViewById(R.id.checkBox);
        vybCheck.setChecked(vybava.checked);
        vybCheck.setTag(position);

        vybCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.gc.materialdesign.views.CheckBox chckBtn = (com.gc.materialdesign.views.CheckBox)v;
                DMVybava vybava = filteredData.get((Integer)chckBtn.getTag());
                vybava.checked = chckBtn.isChecked();
            }
        });
    	return v;
	}

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }

 /*   @Override
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
	        for(DMVybava vybava : filteredData){
	            add(vybava);
	            notifyDataSetInvalidated();
	        }
	        if(filteredData == null)
	        	filteredData =  new ArrayList<DMVybava>();
			
		}
		
	}*/
	
}
