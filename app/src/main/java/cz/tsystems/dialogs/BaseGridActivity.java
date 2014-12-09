package cz.tsystems.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;


public class BaseGridActivity extends Activity {

    protected ListView listView;
    protected PortableCheckin app;
    public static final int eGRDPLANZAK = 1;
    public int grdTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_grid);

        app = (PortableCheckin)getApplicationContext();
        listView = (ListView)findViewById(R.id.grid);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.activity_title_bar);

        View v = findViewById(R.id.baseGrdLayaut);//activity.getLayoutInflater().inflate(R.layout.activity_base_grid, null);
        ((LinearLayout) v).addView(getCaptionView(), 0);

//        ListView lv = (ListView)v.findViewById(R.id.grid);
        setListView();

        setWindowParams();
    }

    public void setWindowParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }

    public View getCaptionView() {
        return null;
    }

    public void setListView() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
