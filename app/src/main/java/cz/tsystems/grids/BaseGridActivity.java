package cz.tsystems.grids;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;


public class BaseGridActivity extends Activity {

    protected ListView listView;
    protected PortableCheckin app;
    public static final int eBASEGRID = 0, eGRDPLANZAK = 1, eHISTORY = 2, ePLANACTIVITIES = 3, eVOZIDLOINFO = 4, eZAKAZNIKINFO = 5;
    public int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_grid);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.color.transparent);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);

        app = (PortableCheckin)getApplicationContext();
        listView = (ListView)findViewById(R.id.grid);

        ((TextView)findViewById(R.id.lblCarCaption)).setText(app.getCheckin().vehicle_description);
        TextView lblLoggetUser = (TextView)findViewById(R.id.lblLoggetUser);
        TextView lblCheckinNR = (TextView)findViewById(R.id.lblCheckIn_nr);

        final String poradce = getResources().getString(R.string.Poradce);
        if(PortableCheckin.user != null)
            lblLoggetUser.setText(poradce + ": " + PortableCheckin.user.name + " " + PortableCheckin.user.surname);
        else
            lblLoggetUser.setText(poradce + ": ");

        if(app.getCheckin().checkin_number > 0)
            lblCheckinNR.setText(String.valueOf(app.getCheckin().checkin_number));
        else if(app.getCheckin().planned_order_no != null && app.getCheckin().planned_order_no.length() > 0) {
            final String planZakPrefix = getResources().getString(R.string.CisloPlanZakazky);
            final Spanned theNR = Html.fromHtml(planZakPrefix + ": <b>" + String.valueOf(app.getCheckin().planned_order_no) + "</b>");
            lblCheckinNR.setText(theNR );
        }
        else
            lblCheckinNR.setText("");

        Drawable d = app.getSelectedBrand().getBrandImage(app);
        ((android.widget.Button)findViewById(R.id.btnBrand)).setBackgroundDrawable(d);

        View v = findViewById(R.id.baseGrdLayaut);
        View v2 = getCaptionView();
        if(v2 != null) {
            ((LinearLayout) v).addView(v2, 1);
        }

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
//        getMenuInflater().inflate(R.menu.menu_base_grid, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent intent = new Intent();
                intent.putExtra("type", type);
                this.setResult(RESULT_OK, intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
