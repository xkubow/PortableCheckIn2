package cz.tsystems.portablecheckin;

import java.io.File;
import java.io.IOException;
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
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gc.materialdesign.views.Switch;

public class LoginActivity extends Activity {
	final String TAG = "LoginActivity";
	private EditText login, password;
	private com.gc.materialdesign.views.ButtonFlat btnLogin;
	private com.gc.materialdesign.views.ButtonFlat btnSettup;
	private PortableCheckin app;
    private TextView lblVerze;
    android.widget.Switch chkLogin;
    ImageButton btnShareLog;
	
    private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle extra = intent.getBundleExtra("requestData");
			String errorMsg = intent.getStringExtra("ErrorMsg");

            //updateDB();
			
			if(errorMsg != null && errorMsg.length() > 0)
			{
				app.getDialog(LoginActivity.this, "error", errorMsg, PortableCheckin.DialogType.SINGLE_BUTTON).show();
				return;
			}

            if(intent.hasExtra("Last-Modified")) {
                PortableCheckin.updateDB(LoginActivity.this);
                return;
            }
            else if(extra.getString("ACTION").equalsIgnoreCase("Login"))
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
				else if(PortableCheckin.user == null)
                    startGetLogin();
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
        btnLogin = (com.gc.materialdesign.views.ButtonFlat) this.findViewById(R.id.btnLogin);
        btnSettup = (com.gc.materialdesign.views.ButtonFlat) this.findViewById(R.id.btnSetup);
        lblVerze = (TextView) this.findViewById(R.id.lblVerze);
        
        password.setOnEditorActionListener(new DoneOnEditorActionListener());

        chkLogin = (android.widget.Switch) this.findViewById(R.id.chkLoging);
        chkLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);
                SharedPreferences.Editor spe= sp.edit();
                spe.putBoolean("enableLoging", isChecked);

                if(isChecked)
                    startLoging();
            }
        });
        btnShareLog = (ImageButton) this.findViewById(R.id.btnShareLog);
        btnShareLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File logDir = new File(Environment.getExternalStorageDirectory() + "/log");
                File file[] = logDir.listFiles();

/*                if(file.length == 0) {
                    Toast.makeText(LoginActivity.this,
                            "No Application Available to View PDF",
                            Toast.LENGTH_SHORT).show();
                    return;
                }*/


                Log.d("Files", "Size: "+ file.length);
                File lastLogFile = new File(Environment.getExternalStorageDirectory(), "portableCheckInLog.txt");

                if (lastLogFile.exists()) {
                    Uri path = Uri.fromFile(lastLogFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "text/plain");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(LoginActivity.this,
                                "No Application Available to View PDF",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        
/* 
 *      static VERSION setting
 *        
 * */
		SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);        
		SharedPreferences.Editor spe= sp.edit();
		spe.putInt("GENERATION_VER", 3);
		spe.putInt("MAJOR_VER", 2);
		spe.putInt("COMMUNICATION_VER", 0);
		spe.commit();


        lblVerze.setText(String.format("v. " + BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE)+")");
        	
    }

    void startLoging() {
        try {
            File logDir = new File(Environment.getExternalStorageDirectory() + "/log");
            boolean success = true;
            if (!logDir.exists()) {
                success = logDir.mkdir();
            }
            if (success) {
                File logFile = new File(Environment.getExternalStorageDirectory(), "portableCheckInLog.txt");
                boolean fileExist = logFile.createNewFile();
                if(fileExist) {
                    Runtime.getRuntime().exec("logcat -d -v time -f " + logFile.getAbsolutePath());
                }
            } else {
                Toast.makeText(this,
                        "Log file doesnt been created",
                        Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){}
    }
    
    @Override
    protected void onStart() {
        SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);
        chkLogin.setChecked(sp.getBoolean("enableLoging", false));
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("recivedData");
        registerReceiver(receiver, filterSend);
    	super.onStart();        
    }
    
    public void onSettupClicked(View target) {
		AlertDialog.Builder b = new Builder(this);
	    b.setTitle(getResources().getString(R.string.Nastavenie_prihlas_udajov));
			
	    final ViewGroup view = (ViewGroup) getLayoutInflater().inflate( R.layout.activity_setup, null );
	    EditText nte = (EditText) view.findViewById(R.id.txtServiceURI);
	    nte.setText(getSharedPreferences("cz.tsystems.portablecheckin", 0).getString("serviceURI", ""));
	    b.setView(view);
	    b.setPositiveButton(R.string.OK, new OnClickListener() {
			
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
			PortableCheckin.updateDB(this);
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
    
    @Override
    protected void onDestroy() {
      // Unregister since the activity is about to be closed.
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e){}
    	super.onDestroy();
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	unregisterReceiver(receiver);
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
