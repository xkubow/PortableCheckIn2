package cz.tsystems.portablecheckin;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import cz.tsystems.data.PortableCheckin;

/**
 * Created by kubisj on 9.1.2015.
 */
public class Poznamka extends Activity {
    PortableCheckin app;
    EditText txtProtokol, txtZakazList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poznamka);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.color.transparent);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);

        app = (PortableCheckin)getApplicationContext();

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

        ((TextView) findViewById(R.id.lblProtokol)).setText(getResources().getText(R.string.Protokol));
        ((TextView) findViewById(R.id.lblZakazkoviList)).setText(getResources().getText(R.string.Zakazkovi_list));
        txtProtokol = (EditText) findViewById(R.id.txtProtokol);
        txtZakazList = (EditText) findViewById(R.id.txtZakazList);

        txtProtokol.setText(app.getCheckin().note_protocol);
        txtZakazList.setText(app.getCheckin().note_order_list);

        txtProtokol.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                app.getCheckin().note_protocol = s.toString();
            }
        });

        txtZakazList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                app.getCheckin().note_order_list = s.toString();
            }
        });


    }
}
