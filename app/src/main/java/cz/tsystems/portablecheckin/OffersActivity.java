package cz.tsystems.portablecheckin;

import cz.tsystems.base.BaseFragment;
import cz.tsystems.data.PortableCheckin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OffersActivity extends BaseFragment {
    PortableCheckin app;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_offers, container, false);
        app = (PortableCheckin)getActivity().getApplicationContext();
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
