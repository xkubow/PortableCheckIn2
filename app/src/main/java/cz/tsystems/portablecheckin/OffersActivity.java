package cz.tsystems.portablecheckin;

import cz.tsystems.base.BaseFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OffersActivity extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_offers, container, false);
        return rootView;
	}
	
	public void showData(Intent intent)
	{
		
	}

	@Override
	public void updateData(Intent intent) {
		// TODO Auto-generated method stub
		
	}
}
