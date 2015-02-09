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

import java.util.ArrayList;
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

    private static class ViewHolder {
        public int typ;
        public TextView lblService;
        public EditText txtService, txtPC;
        public com.gc.materialdesign.views.CheckBox checkBox;
    }

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
            if(selectedText == null || selectedText.getEditableText() != s)
                return;
            ViewHolder vh = (ViewHolder) selectedText.getTag(R.id.ViewHolder);
            final int pos = (int) selectedText.getTag(R.id.listPosition);
            Log.d(TAG, "--------------------------------------------------------------------");
            Log.d(TAG, String.valueOf(pos) + ", " + selectedText.toString() + ", " + vh.txtService.toString());
            getItem(pos).text = s.toString();
            vh.checkBox.setChecked((s.length()>0));
            getItem(pos).setChecked(vh.checkBox.isChecked());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher textWatcherPC = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(selectedText == null || selectedText.getEditableText() != s)
                return;

            final int pos = (int) selectedText.getTag(R.id.listPosition);
            Log.d(TAG, String.valueOf(pos));
            getItem(pos).sell_price = Double.parseDouble(s.toString());
            ViewHolder vh = (ViewHolder) selectedText.getTag(R.id.ViewHolder);
            vh.checkBox.setChecked((s.length()>0));
            getItem(pos).setChecked(vh.checkBox.isChecked());
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
        ViewHolder vh = null;
        final DMService service = getItem(position);//data.get(position);
        int type = getItemViewType(position);

        if (v == null) {
            Log.d(TAG, "View == NULL, " + String.valueOf(position));
            switch (type) {
                case FREE:
                    v = layoutInflater.inflate(R.layout.item_free_vybavy, null);
                    break;
                case STATIC:
                    v = layoutInflater.inflate(R.layout.item_vybava, null);
                    break;
            }

            vh = new ViewHolder();
            vh.typ = type;
            vh.txtService = (EditText) v.findViewById(R.id.txtVybavaText);
            vh.lblService = (TextView) v.findViewById(R.id.lblVybavaText);
            vh.txtPC = (EditText) v.findViewById(R.id.txtPC);
            vh.checkBox = (com.gc.materialdesign.views.CheckBox) v.findViewById(R.id.checkBox);

            switch (type) {
                case FREE:
                    vh.txtService.setTag(R.id.listPosition, position);
                    vh.txtService.setOnTouchListener(touchListener);
                    vh.txtService.addTextChangedListener(textWatcher);
                    vh.txtService.setOnEditorActionListener(editorActionListener);
                    vh.txtService.setTag(R.id.ViewHolder, vh);
                    vh.txtPC.setTag(R.id.listPosition, position);
                    vh.txtPC.setEnabled(true);
                    vh.txtPC.setVisibility(View.VISIBLE);
                    vh.txtPC.setOnTouchListener(touchListener);
                    vh.txtPC.addTextChangedListener(textWatcherPC);
                    break;
                case STATIC:
                    Log.d(TAG, "FINAL - " + String.valueOf(service.get_id()) + ", " + String.valueOf(service.editable) + ", " + service.text);
                    vh.txtPC.setEnabled(false);
                    break;
            }

            vh.checkBox.setTag(position);
            vh.checkBox.setOncheckListener(new com.gc.materialdesign.views.CheckBox.OnCheckListener() {
                @Override
                public void onCheck(com.gc.materialdesign.views.CheckBox checkBox, boolean isChecked) {
                    DMService s = data.get((Integer) checkBox.getTag());
                    s.checked = checkBox.isChecked();
                }
            });

            vh.txtPC.setTag(R.id.ViewHolder, vh);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        //FREE
        if (vh.typ == FREE) {
            if (service.text != null && service.text.length() > 0)
                vh.txtService.setText(service.text.toString());
            else
                vh.txtService.setText("");
        } else {
            vh.txtPC.setHint("");
            vh.lblService.setText(service.text);
        }
        ////////////////////
        vh.checkBox.setChecked(service.checked);
        if (service.sell_price != null) {
            vh.txtPC.setVisibility(View.VISIBLE);
            vh.txtPC.setText(String.valueOf(service.sell_price));
        }else {
            vh.txtPC.setText("");
        }

        return v;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
