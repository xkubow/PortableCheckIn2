package cz.tsystems.adapters;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;
import java.util.TreeSet;

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
    private static final int STATIC = 0;
    private static final int FREE = 1;
    private static EditText selectedText;
    private TreeSet mSeparatorsSet = new TreeSet();
    LayoutInflater layoutInflater;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(selectedText == null)
                return;

            final int pos = (int) selectedText.getTag(R.id.listPosition);
            Log.d(TAG, String.valueOf(pos));
            if((int) selectedText.getTag(R.id.ID) == R.id.txtVybavaText)
                getItem(pos).text = s.toString();
            else if((int) selectedText.getTag(R.id.ID) == R.id.txtPC)
                getItem(pos).sell_price = Double.parseDouble(s.toString());
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
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
            return false;
        }
    };

    public ServiceArrayAdapter(Context context, int resource, int textViewResourceId, List<DMService> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.data = objects;
        layoutInflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int i = 0;
        for(DMService service : objects) {
            if(service.editable)
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
        final DMService service = getItem(position);//data.get(position);
        Log.d(TAG, String.valueOf(position ) + ":" + String.valueOf(service.get_id()) + ", " + String.valueOf(service.editable) + ", " + service.text);
        if(v!= null)
            Log.d(TAG, "REUSING VIEW :" + String.valueOf(v));
        int type = getItemViewType(position);
        Log.d(TAG, "TYPE :" + String.valueOf(type));

        if (v == null) {
            switch (type) {
                case FREE:
                    v = layoutInflater.inflate(R.layout.item_free_vybavy, null);
                    break;
                case STATIC:
                    v = layoutInflater.inflate(R.layout.item_vybava, null);
                    Log.d(TAG, "FINAL - " + String.valueOf(service.get_id()) + ", " + String.valueOf(service.editable) + ", " + service.text);
                    break;
            }
        }
        EditText txtVybavaText = (EditText)v.findViewById(R.id.txtVybavaText);
        EditText txtPC = (EditText)v.findViewById(R.id.txtPC);
        switch (type) {
            case FREE :
                txtVybavaText.setTag(R.id.listPosition, position);
                txtVybavaText.setTag(R.id.ID, R.id.txtVybavaText);
                txtVybavaText.setTag(R.id.checkBox, v.findViewById(R.id.checkBox));
                txtVybavaText.setOnTouchListener(touchListener);
                txtVybavaText.addTextChangedListener(textWatcher);
                txtVybavaText.setOnEditorActionListener(editorActionListener);
                break;
            case STATIC :
                Log.d(TAG, "FINAL - " + String.valueOf(service.get_id()) + ", " + String.valueOf(service.editable) + ", " + service.text);
                break;
        }
        txtPC.setTag(R.id.listPosition, position);
        txtPC.setTag(R.id.ID, R.id.txtPC);
        txtPC.setTag(R.id.checkBox, v.findViewById(R.id.checkBox));

        if(!service.editable) {
            TextView text = (TextView) v.findViewById(R.id.lblVybavaText);
            text.setText(service.text);
            txtPC.setEnabled(false);
        }
        else {
            if(service.text != null && service.text.length() > 0) {
                EditText text = (EditText) v.findViewById(R.id.txtVybavaText);
                text.setText(service.text.toString());
            }

            txtPC.setEnabled(true);
            txtPC.setVisibility(View.VISIBLE);
        }

        if(service.sell_price != null) {
            txtPC.setVisibility(View.VISIBLE);
            Log.d(TAG, "SETING PC :" + String.valueOf(service.sell_price));
            txtPC.setText(String.valueOf(service.sell_price));
            Log.d(TAG, "----------------------------------------------------------");
        }

        txtPC.setOnTouchListener(touchListener);
        txtPC.addTextChangedListener(textWatcher);

        final com.gc.materialdesign.views.CheckBox serCheck = (com.gc.materialdesign.views.CheckBox)v.findViewById(R.id.checkBox);
        serCheck.setTag(position);
        serCheck.setChecked(service.checked);


        Log.d(TAG, "================================================================================================");

        serCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.gc.materialdesign.views.CheckBox chkBtn = (com.gc.materialdesign.views.CheckBox)v;
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
