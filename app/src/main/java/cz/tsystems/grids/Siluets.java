package cz.tsystems.grids;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import cz.tsystems.adapters.SilhouetteCursorAdapter;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by KUBO on 11. 1. 2015.
 */
public class Siluets extends Activity {
    PortableCheckin app;
    SilhouetteCursorAdapter silhouetteCursorAdapter;
    protected ListView listView;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object object = parent.getItemAtPosition(position);

            Cursor cursor = (Cursor) object;
            final int silhouetteId = cursor.getInt(cursor.getColumnIndex("ID"));
            app.getCheckin().silhouette_id = silhouetteId;
            app.loadSilhouette();
            Intent intent = new Intent();
            intent.putExtra("type", BaseGridActivity.eSILHOUETTES);
            Siluets.this.setResult(RESULT_OK, intent);
            Siluets.this.finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silhouette_grid);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.color.transparent);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);

        app = (PortableCheckin)getApplicationContext();
        listView = (ListView)findViewById(R.id.grid);
        listView.setOnItemClickListener(onItemClickListener);

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

        setListView();
    }

    public void setListView() {
        Cursor cursor = app.getSilhouetteImagesForBrand();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam), Toast.LENGTH_SHORT).show();
            return;
        }

//        silhouetteCursorAdapter = new SilhouetteCursorAdapter( this, cursor);
        SilhouetteCursorAdapter simpleCursorAdapter = new SilhouetteCursorAdapter(this, R.layout.item_siluets, cursor, new String[] {"img1", "img3", "img2", "img4"}, new int[] {R.id.ingSilhouette1, R.id.ingSilhouette2, R.id.ingSilhouette3, R.id.ingSilhouette4}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(simpleCursorAdapter);
    }
}
