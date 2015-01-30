package cz.tsystems.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.hb.views.PinnedSectionListView;

import java.util.List;

import cz.tsystems.data.DMOffers;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 30.1.2015.
 */
public class OffersArrayAdapter extends ArrayAdapter<DMOffers> implements PinnedSectionListView.PinnedSectionListAdapter{
    PortableCheckin app;

    public OffersArrayAdapter(Context context, int resource, List<DMOffers> objects) {
        super(context, resource, objects);
        app = (PortableCheckin)context.getApplicationContext();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.item_offer, null);
        }
        DMOffers offer = getItem(position);

        TextView textView = (TextView)view.findViewById(R.id.txtCena);
        if(offer.sell_price != null)
            textView.setText(String.valueOf(offer.sell_price)  + " " + PortableCheckin.setting.currency_abbrev );
        else
            textView.setText("");

        textView = (TextView)view.findViewById(R.id.txtText);
        if(offer.check_offer_txt != null && offer.show_txt == 1)
            textView.setText(offer.check_offer_txt);
        else
            textView.setText("");

        ImageView imgView = (ImageView)view.findViewById(R.id.imgOffer);

        imgView.setImageBitmap(app.getOffersBanner(offer.check_offer_id));

        CheckBox chkOffer = (CheckBox) view.findViewById(R.id.chkOffer);
        chkOffer.setStaticChecked(offer.checked);
        chkOffer.setTag(position);
        chkOffer.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(CheckBox checkBox, boolean isChecked) {
                final int pos = (int)checkBox.getTag();
                getItem(pos).checked = isChecked;
            }
        });

        return  view;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }
}
