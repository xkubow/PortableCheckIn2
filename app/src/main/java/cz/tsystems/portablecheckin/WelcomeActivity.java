package cz.tsystems.portablecheckin;

import java.io.UnsupportedEncodingException;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cz.tsystems.base.*;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.data.SQLiteDBProvider;

public class WelcomeActivity extends Activity {

	private PortableCheckin app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		app = (PortableCheckin) getApplicationContext();
		SetbaseData sbd = new SetbaseData();
		sbd.execute("");
		
//        app.setTheDBProvider();		
//		 TelephonyManager manager=(TelephonyManager)
//		 getSystemService(Context.TELEPHONY_SERVICE); 
//		 String uuid=manager.getDeviceId();

	}

	@Override
	protected void onStart() {
        Intent it = new Intent(this, LoginActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(it, 1);
        super.onStart();
	}

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Intent i = new Intent(this, FragmentPagerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("OpenPlanedOrderList", true);
                WelcomeActivity.this.startActivity(i);
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
                moveTaskToBack(true);
			}
		}

	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    }
    
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }

	public class SetbaseData extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			byte[] data = null;
			String ipString = "";

			try {

				WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo wi = wifi.getConnectionInfo();
				String ma = wi.getMacAddress();
				int ip = wi.getIpAddress();
				ipString = String.format(
						   "%d.%d.%d.%d",
						   (ip & 0xff),
						   (ip >> 8 & 0xff),
						   (ip >> 16 & 0xff),
						   (ip >> 24 & 0xff));
				
				if(ma != null)
					data = ma.getBytes("UTF-8");

			} catch (UnsupportedEncodingException e) {
				Log.e("EXCEPTION : %s", e.getLocalizedMessage());
			}
			catch(Exception e)
			{
				Log.e("EXCEPTION", e.getLocalizedMessage());
			}

			if (data != null) {
				String base64DeviceId = Base64.encodeToString(data,
						Base64.NO_WRAP);
				app.setDeviceID(base64DeviceId);
				app.setLocalHostName(ipString);	
			}
			else
			{
				app.setDeviceID("COSICONEMAMACKADRESUUUU=");
				app.setLocalHostName("SimulatorKuboPC");
			}
			Log.d("DEBUG", app.getDeviceID());
			return null;
		}

	}

}
