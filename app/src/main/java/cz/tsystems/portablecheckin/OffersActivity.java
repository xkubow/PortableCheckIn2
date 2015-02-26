package cz.tsystems.portablecheckin;

import cz.tsystems.adapters.OffersArrayAdapter;
import cz.tsystems.base.BaseFragment;
import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.data.DMOffers;
import cz.tsystems.data.PortableCheckin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gc.materialdesign.views.CheckBox;

public class OffersActivity extends BaseFragment {
    PortableCheckin app;
    ListView offerList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_offers, container, false);
        app = (PortableCheckin)getActivity().getApplicationContext();

        offerList = (ListView) rootView.findViewById(R.id.offersList);

        offerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((FragmentPagerActivity)getActivity()).unsavedCheckin();
                CheckBox chkOffer = (CheckBox) view.findViewById(R.id.chkOffer);
                DMOffers offer = (DMOffers) offerList.getAdapter().getItem(position);
                chkOffer.setChecked(!offer.checked);
                offer.checked = !offer.checked;
            }
        });

        OffersArrayAdapter offersArrayAdapter = new OffersArrayAdapter(getActivity(), R.layout.item_offer, PortableCheckin.offers);
        offerList.setAdapter(offersArrayAdapter);

        return rootView;
	}

    @Override
	public void showData(Intent intent)
	{
        app.dismisProgressDialog();
        String action = intent.getAction();
        String serviceAction = null;
        if (action.equalsIgnoreCase("recivedData")) {
            try {
                Bundle b = intent.getExtras().getBundle("requestData");
                if(b == null)
                    return;
                serviceAction = b.getString("ACTION");
                if(serviceAction == null)
                    return;
                Log.d("SHOWDATA", intent.getExtras().toString());
            } catch (NullPointerException e) {
                app.getDialog(getActivity(), "error", e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON).show();
            }
        }
	}

	@Override
	public void updateData(Intent intent) {
		// TODO Auto-generated method stub
		
	}
}
