package cz.tsystems.base;

import cz.tsystems.data.PortableCheckin;
import cz.tsystems.grids.History;
import cz.tsystems.grids.VozidloInfo;
import cz.tsystems.grids.ZakaznikInfo;
import cz.tsystems.portablecheckin.LoginActivity;
import cz.tsystems.portablecheckin.R;
import cz.tsystems.portablecheckin.SettingAppDialog;
import cz.tsystems.portablecheckin.WelcomeActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public final class BaseMenu extends Object {
//	private PortableCheckin app;

	static public void show(Context context, MenuItem item, final Activity activity)
	{
		switch(item.getOrder())
		{
            case 0:
                setDefaultValues(context);
                break;
			case 1:
				showStrucHistory(activity);
				break;
			case 2:
				showHistory(activity);
				break;
			case 3:
				showVozidloInfo(activity);
				break;
			case 4:
				showZkaznikInfo(activity);
				break;
            case 5:
                showSetting(activity);
                break;
            case 6:
                odhlaseni(activity);
                break;
			default:
		}
	}

    private static void odhlaseni(Activity activity) {
/*        SharedPreferences sp = activity.getSharedPreferences("cz.tsystems.portablecheckin", activity.MODE_PRIVATE);
        SharedPreferences.Editor spe= sp.edit();
        spe.putString("DB_UPDATE_DATE", "16 Feb 2011 14:59:59 GMT");
        spe.commit();*/
        setDefaultValues(activity.getApplicationContext());
        Intent i = new Intent(activity, WelcomeActivity.class);
        i.putExtra("Odhlaseni", true);
        activity.startActivity(i);
        activity.finish();
    }

    private static void showSetting(Activity activity) {
        SettingAppDialog settingAppDialog = new SettingAppDialog(activity);
        settingAppDialog.setTitle(activity.getResources().getString(R.string.action_settings));
        settingAppDialog.show();
    }

    private static void setDefaultValues(Context context) {
        PortableCheckin app = (PortableCheckin)context;
        app.loadDefaultCheckin();
        ((FragmentPagerActivity)app.getActualActivity()).updateFragments();
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
