package cz.tsystems.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 25.11.2014.
 */
public class BaseGrid extends Dialog {
    private Activity activity;

    public BaseGrid(Activity activity) {
        super(activity, R.layout.activity_base_grid);
        this.activity = activity;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.color.transparent);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
    }

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_grid);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.activity_title_bar);

        View v = findViewById(R.id.baseGrdLayaut);//activity.getLayoutInflater().inflate(R.layout.activity_base_grid, null);
        ((LinearLayout) v).addView(getCaptionView(), 0);

        ListView lv = (ListView)v.findViewById(R.id.grid);
        setListView(lv);

        setWindowParams();
    }

    public void setWindowParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }

    public View getCaptionView()
    {
        return null;
    }

    public void setListView(ListView listView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.hide();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
