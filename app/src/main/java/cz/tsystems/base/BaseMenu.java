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
import android.widget.TextView;

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

        final BaseGrid baseGrid = new BaseGrid(activity) {
            @Override
            public View getCaptionView()
            {
                View view = activity.getLayoutInflater().inflate(R.layout.item_vehicle_history, null);
                view.setBackgroundColor(activity.getResources().getColor(R.color.grid_caption));
                ((TextView)view.findViewById(R.id.personalIdColor)).setText("");
                ((TextView)view.findViewById(R.id.lblhistory_type_txt)).setText(activity.getResources().getText(R.string.typ));
                ((TextView)view.findViewById(R.id.lblHistory_description)).setText(activity.getResources().getText(R.string.popis));
                ((TextView)view.findViewById(R.id.lblItem_no)).setText(activity.getResources().getText(R.string.pp_nd_c));
                ((TextView)view.findViewById(R.id.lblPersonal_tag)).setText(activity.getResources().getText(R.string.os_c));

                return view;
            }

            @Override
            public void setListView(ListView listView) {
                List<DMVehicleHistory> shortVehicleHistory = new ArrayList<DMVehicleHistory>();
                for (DMVehicleHistory vh : PortableCheckin.vehicleHistoryList) {
                    if (vh.brief_history)
                        shortVehicleHistory.add(vh);
                }
                VehicleHistoryArrayAdapter vehicleShortHistoryAdapter = new VehicleHistoryArrayAdapter(
                        activity, 0, android.R.layout.simple_list_item_1,
                        shortVehicleHistory);
                listView.setAdapter(vehicleShortHistoryAdapter);
            }
        };
        baseGrid.setTitle(activity.getResources().getText(R.string.historia_vozu));
        baseGrid.show();
	}
	
	static public void showHistory(final Activity activity)
	{
        final BaseGrid baseGrid = new BaseGrid(activity) {
            @Override
            public View getCaptionView()
            {
                View view = activity.getLayoutInflater().inflate(R.layout.item_vehicle_history, null);
                view.setBackgroundColor(activity.getResources().getColor(R.color.grid_caption));
                ((TextView)view.findViewById(R.id.personalIdColor)).setText("");
                ((TextView)view.findViewById(R.id.lblhistory_type_txt)).setText(activity.getResources().getText(R.string.typ));
                ((TextView)view.findViewById(R.id.lblHistory_description)).setText(activity.getResources().getText(R.string.popis));
                ((TextView)view.findViewById(R.id.lblItem_no)).setText(activity.getResources().getText(R.string.pp_nd_c));
                ((TextView)view.findViewById(R.id.lblPersonal_tag)).setText(activity.getResources().getText(R.string.os_c));

                return view;
            }

            @Override
            public void setListView(ListView listView) {
                VehicleHistoryArrayAdapter vehicleHistoryAdapter = new VehicleHistoryArrayAdapter(
                        activity, 0, android.R.layout.simple_list_item_1,
                        PortableCheckin.vehicleHistoryList);
                listView.setAdapter(vehicleHistoryAdapter);
            }
        };
        baseGrid.setTitle(activity.getResources().getText(R.string.historia_vozu));
        baseGrid.show();
	}
	
	static public void showVozidloInfo(final Activity activity)
	{
        final BaseGrid baseGrid = new BaseGrid(activity) {
            @Override
            public View getCaptionView()
            {
                View view = activity.getLayoutInflater().inflate(R.layout.item_info_list, null);
                view.setBackgroundColor(activity.getResources().getColor(R.color.grid_caption));
                ((TextView)view.findViewById(R.id.lblValName)).setText(activity.getResources().getText(R.string.typ));
                ((TextView)view.findViewById(R.id.lblValue)).setText(activity.getResources().getText(R.string.popis));
                return view;
            }

            @Override
            public void setListView(ListView listView) {
                VehicleInfoAdapter vehicleInfoAdapter = new VehicleInfoAdapter(
                        activity, 0, android.R.layout.simple_list_item_1,
                        PortableCheckin.vehicleInfoList);
                listView.setAdapter(vehicleInfoAdapter);
            }

            @Override
            public void setWindowParams() {
            }
        };
        baseGrid.setTitle(activity.getResources().getText(R.string.infVozidla));
        baseGrid.show();
    }
	
	static public void showZkaznikInfo(final Activity activity)
	{

        final BaseGrid baseGrid = new BaseGrid(activity) {
            @Override
            public View getCaptionView()
            {
                View view = activity.getLayoutInflater().inflate(R.layout.item_info_list, null);
                view.setBackgroundColor(activity.getResources().getColor(R.color.grid_caption));
                ((TextView)view.findViewById(R.id.lblValName)).setText(activity.getResources().getText(R.string.typ));
                ((TextView)view.findViewById(R.id.lblValue)).setText(activity.getResources().getText(R.string.popis));
                return view;
            }

            @Override
            public void setListView(ListView listView) {
                CustomerInfoAdapter customerInfoAdapter = new CustomerInfoAdapter(
                        activity, 0, android.R.layout.simple_list_item_1,
                        PortableCheckin.custumerInfoList);
                listView.setAdapter(customerInfoAdapter);
            }

            @Override
            public void setWindowParams() {
            }
        };
        baseGrid.setTitle(activity.getResources().getText(R.string.InfZakaznik));
        baseGrid.show();
	}	

}
