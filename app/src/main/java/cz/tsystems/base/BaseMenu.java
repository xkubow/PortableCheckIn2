package cz.tsystems.base;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.adapters.CustomerInfoAdapter;
import cz.tsystems.adapters.VehicleHistoryArrayAdapter;
import cz.tsystems.adapters.VehicleInfoAdapter;
import cz.tsystems.data.DMVehicleHistory;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public final class BaseMenu extends Object {
//	private PortableCheckin app;
	
	static public void show(MenuItem item, final Activity activity)
	{
		switch(item.getOrder())
		{
			case 2:
				showStrucHistory(activity);
				break;
			case 3:
				showHistory(activity);
				break;
			case 4:
				showVozidloInfo(activity);
				break;
			case 5:
				showZkaznikInfo(activity);
				break;				
			default:
		}
	}
	
	static public void showStrucHistory(final Activity activity)
	{
		List<DMVehicleHistory> shortVehicleHistory = new ArrayList<DMVehicleHistory>();
		for (DMVehicleHistory vh : PortableCheckin.vehicleHistoryList) {
			if (vh.brief_history)
				shortVehicleHistory.add(vh);
		}
		VehicleHistoryArrayAdapter vehicleShortHistoryAdapter = new VehicleHistoryArrayAdapter(
				activity, 0, android.R.layout.simple_list_item_1,
				shortVehicleHistory);
		
		AlertDialog.Builder b = new Builder(activity);
	    b.setTitle("Example");
	
		LinearLayout layout = new LinearLayout(activity);
		layout.setGravity(Gravity.LEFT|Gravity.TOP);
		layout.setOrientation(LinearLayout.VERTICAL);	
		
	    ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate( R.layout.item_vehicle_history, null );
	    layout.addView(view);
	    
		ListView listView = new ListView(activity);
		listView.setSelector(android.R.color.transparent);
		listView.setAdapter(vehicleShortHistoryAdapter);
		layout.addView(listView);
	    Dialog d = b.setView(layout).create();

	    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(d.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;    
	    d.show();
	    d.getWindow().setAttributes(lp);
	    
	    	
	}
	
	static public void showHistory(final Activity activity)
	{
		AlertDialog.Builder b = new Builder(activity);
	    b.setTitle("Example");
	    
		VehicleHistoryArrayAdapter vehicleHistoryAdapter = new VehicleHistoryArrayAdapter(
				activity, 0, android.R.layout.simple_list_item_1,
				PortableCheckin.vehicleHistoryList);
		
		LinearLayout layout = new LinearLayout(activity);
		layout.setGravity(Gravity.LEFT|Gravity.TOP);
		layout.setOrientation(LinearLayout.VERTICAL);	
		
	    ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate( R.layout.item_vehicle_history, null );
	    layout.addView(view);
	    
		ListView listView = new ListView(activity);
		listView.setSelector(android.R.color.transparent);
		listView.setAdapter(vehicleHistoryAdapter);
		layout.addView(listView);
	    Dialog d = b.setView(layout).create();

	    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(d.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;    
	    d.show();
	    d.getWindow().setAttributes(lp);
	}	
	
	static public void showVozidloInfo(final Activity activity)
	{
		AlertDialog.Builder b = new Builder(activity);
	    b.setTitle("Example");
		VehicleInfoAdapter vehicleInfoAdapter = new VehicleInfoAdapter(
				activity, 0, android.R.layout.simple_list_item_1,
				PortableCheckin.vehicleInfoList);
		
		LinearLayout layout = new LinearLayout(activity);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.VERTICAL);		
	    ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate( R.layout.item_info_list, null );
	    layout.addView(view);
	    
		ListView listView = new ListView(activity);
		listView.setSelector(android.R.color.transparent);
		listView.setAdapter(vehicleInfoAdapter);
		layout.addView(listView);
	    b.setView(layout);	    
	    b.show();
	}	
	
	static public void showZkaznikInfo(final Activity activity)
	{
		
		AlertDialog.Builder b = new Builder(activity);
	    b.setTitle("Example");
	    CustomerInfoAdapter customerInfoAdapter = new CustomerInfoAdapter(
				activity, 0, android.R.layout.simple_list_item_1,
				PortableCheckin.custumerInfoList);
	    
		LinearLayout layout = new LinearLayout(activity);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.VERTICAL);		
	    ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate( R.layout.item_info_list, null );
	    layout.addView(view);
	    
		ListView listView = new ListView(activity);
		listView.setSelector(android.R.color.transparent);
		listView.setAdapter(customerInfoAdapter);
		layout.addView(listView);
	    b.setView(layout);	    
	    b.show();
	}	

}
