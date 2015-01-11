package cz.tsystems.base;

import cz.tsystems.grids.History;
import cz.tsystems.grids.VozidloInfo;
import cz.tsystems.grids.ZakaznikInfo;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

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
        ((FragmentPagerActivity)activity).setCheckLogin(false);
        Intent myIntent = new Intent(activity.getApplicationContext(), History.class);
        myIntent.putExtra("shortVersion", true);
        activity.startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
	}
	
	static public void showHistory(final Activity activity)
	{
        ((FragmentPagerActivity)activity).setCheckLogin(false);
        Intent myIntent = new Intent(activity.getApplicationContext(), History.class);
        activity.startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
	}
	
	static public void showVozidloInfo(final Activity activity)
	{
        ((FragmentPagerActivity)activity).setCheckLogin(false);
        Intent myIntent = new Intent(activity.getApplicationContext(), VozidloInfo.class);
        activity.startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
    }

	static public void showZkaznikInfo(final Activity activity)
	{
        ((FragmentPagerActivity)activity).setCheckLogin(false);
        Intent myIntent = new Intent(activity.getApplicationContext(), ZakaznikInfo.class);
        activity.startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
	}

}
