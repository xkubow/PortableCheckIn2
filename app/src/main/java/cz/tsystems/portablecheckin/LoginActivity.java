package cz.tsystems.portablecheckin;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import cz.tsystems.adapters.PlannedOrderAdapter;
import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.DMPlannedOrder;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.data.SQLiteDBProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity {
	final String TAG = "LoginActivity";
	private EditText login, password;
	private Button btnLogin;
	private Button btnSettup;	
	private PortableCheckin app;
	
    private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle extra = intent.getBundleExtra("requestData");
			String errorMsg = intent.getStringExtra("ErrorMsg");
			
			if(errorMsg != null && errorMsg.length() > 0)
			{
				app.getDialog(LoginActivity.this, "error", errorMsg, PortableCheckin.DialogType.SINGLE_BUTTON).show();
				return;
			}
			
			if(extra.getString("ACTION").equalsIgnoreCase("Login"))
			{
				final String userId = PortableCheckin.user.personal_id;
				Log.d("BROADCAST", extra.getString("ACTION") + ", "+ userId);
				if(!userId.isEmpty())
					LoginDone();
			}
			else if(extra.getString("ACTION").equalsIgnoreCase("GetStaticData")
					|| extra.getString("ACTION").equalsIgnoreCase("GetSilhouette")
					|| extra.getString("ACTION").equalsIgnoreCase("GetBanners"))
			{
				if(intent.getByteExtra("loadDataDone", (byte) 0) != (byte)7)
					return;
				else {
					SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);        
					SharedPreferences.Editor spe= sp.edit();
					spe.putInt("MINOR_VER", app.getTheDBProvider().getDBVersion());
					spe.commit();
				}
					
				Log.v(TAG, "Checkin DB after loaded all data from server");
				if(!isDBUpToDate()) {
                    //TODO zobrazit hlasku o chybnom updatnuti DB
                    Log.d("UPDATE DB", "nepodarilo sa updatnut DB");
                    app.dismisProgressDialog();
                    app.getDialog(context, "Chyba", "nepodarilo sa updatnut DB", PortableCheckin.DialogType.SINGLE_BUTTON).show();
                }
				else
					startApp();
			}
		}
    };	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        app = (PortableCheckin) getApplicationContext();
        
        login = (EditText) this.findViewById(R.id.txtLogin);
        password = (EditText) this.findViewById(R.id.txtPassword);
        btnLogin = (Button) this.findViewById(R.id.btnLogin);
        btnSettup = (Button) this.findViewById(R.id.btnSetup);
        
        password.setOnEditorActionListener(new DoneOnEditorActionListener());    
        
/* 
 *      static VERSION setting
 *        
 * */
		SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);        
		SharedPreferences.Editor spe= sp.edit();
		spe.putInt("GENERATION_VER", 3);
		spe.putInt("MAJOR_VER", 0);
		spe.putInt("COMMUNICATION_VER", 0);
		spe.commit();
        	
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("recivedData");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filterSend);
    	super.onStart();        
    }
    
    public void onSettupClicked(View target) {
		AlertDialog.Builder b = new Builder(this);
	    b.setTitle("Settup");
			
	    final ViewGroup view = (ViewGroup) getLayoutInflater().inflate( R.layout.activity_setup, null );
	    EditText nte = (EditText) view.findViewById(R.id.txtServiceURI);
	    nte.setText(getSharedPreferences("cz.tsystems.portablecheckin", 0).getString("serviceURI", ""));
	    b.setView(view);
	    b.setPositiveButton(R.string.Setup, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText te = (EditText) view.findViewById(R.id.txtServiceURI);
				
				URL url = null;
				try {
				    url = new URL(te.getText().toString());
				} catch (MalformedURLException e) {
				    Log.v("myApp", "bad url entered");
				}
				
				SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);
				SharedPreferences.Editor spe= sp.edit();
				if (url == null)				
					spe.remove("serviceURI");
				else
					spe.putString("serviceURI", te.getText().toString());
				spe.commit();
				dialog.dismiss();				
			}
		});
	    b.show();
	    
    }
    
    public void onLoginClicked(View target) {
		startGetLogin();
    }
    
    private void startGetLogin()
    {
    	app.showProgrssDialog(this);
		String loginStr = String.format("%s:%s", login.getText(), password.getText());
		byte[] data = null;

		try {
			data = loginStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
		app.setLogin(base64);
    	
		Intent msgIntent = new Intent(this, CommunicationService.class);
		msgIntent.putExtra("ACTIONURL", "database/userdata");
    	msgIntent.putExtra("ACTION", "Login");		
		this.startService(msgIntent);
    }
    
    private void LoginDone()
    {
		if(!isDBUpToDate())
			updateDB();
		else 
			startApp();
    }
    
    private void startApp()
    {
		app.dismisProgressDialog();
		setResult(RESULT_OK);
		finish();	
    }
    
    private boolean isDBUpToDate()
    {
    	final short dbStructVersion = (short) getSharedPreferences("cz.tsystems.portablecheckin", 0).getInt("MAJOR_VER", -1);
    	SQLiteDBProvider sqlp = app.getTheDBProvider();
		
		final short actualDBStructVersion = sqlp.getDBStrutVersion();
		if(actualDBStructVersion < 0 || dbStructVersion != actualDBStructVersion) {
			return false;
		}
		
		final short dbVersion = sqlp.getDBVersion();
		final short localDBVersion = (short) getSharedPreferences("cz.tsystems.portablecheckin", 0).getInt("MINOR_VER", -1);		
		if(dbVersion < 0)
			return false;
		
		if(dbVersion == localDBVersion)
			return true;
		else
			return false;
    }
    
    private void updateDB()
    {
		Intent msgIntent = new Intent(this, CommunicationService.class);
	
		msgIntent.putExtra("ACTIONURL", "database/StaticData");
    	msgIntent.putExtra("ACTION", "GetStaticData");		
		this.startService(msgIntent);

		msgIntent.putExtra("ACTIONURL", "silhouette/SFiles");
    	msgIntent.putExtra("ACTION", "GetSilhouette");		
		this.startService(msgIntent);
		msgIntent.putExtra("ACTIONURL", "pchi/Banners");
    	msgIntent.putExtra("ACTION", "GetBanners");		
		this.startService(msgIntent);    	
    }
    
    @Override
    protected void onDestroy() {
      // Unregister since the activity is about to be closed.
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    	super.onDestroy();
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);    	
    	super.onStop();
    }
    
    class DoneOnEditorActionListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
            	if(password.getText().length() == 0)
            	{
            		password.requestFocus();
            		return false;
            	}
            	else if(login.getText().length() == 0)
            	{
            		login.requestFocus();
            		return false;
            	}
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                startGetLogin();
                return true;	
            }
			return false;
		}
    }   
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
            	moveTaskToBack(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);    	
    }
}
