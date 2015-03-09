package cz.tsystems.portablecheckin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import cz.tsystems.adapters.PacketsArrayAdapter;
import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMPrehliadkyMaster;
import cz.tsystems.data.DMUnit;
import cz.tsystems.data.PortableCheckin;

/**
 * Created by kubisj on 9.3.2015.
 */
public class UnitServiceActivity extends Activity {
    final String TAG = MailActivity.class.getSimpleName();

    private DMUnit unit;
    private DMPacket selectedPaked;
    private TextView txtCena;
    private View selectedView;
    private android.widget.Button btnClear, btnComma;

    View.OnClickListener onKalkulackaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.widget.Button btn = (android.widget.Button) v;

            if(btn == btnClear) {
                String cena = txtCena.getText().toString();
                txtCena.setText(cena.substring(0, cena.length() - 1));
                return;
            } else if(btn == btnComma) {
                String cena = txtCena.getText().toString();
                if(cena.contains(","))
                    return;
            }

            String newChar = btn.getText().toString();
            txtCena.append(newChar);
        }
    };

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(selectedView != null)
                selectedView.setBackgroundColor(Color.WHITE);
            selectedView = view;
            selectedView.setBackgroundColor(getResources().getColor(R.color.grid_caption));
            selectedPaked = (DMPacket)parent.getItemAtPosition(position);//packetList.get(position);
            if(selectedPaked.sell_price != null )
                txtCena.setText(NumberFormat.getNumberInstance().format(selectedPaked.sell_price));
            else
                txtCena.setText("");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_service);

        txtCena = (TextView)findViewById(R.id.txtCena);
        ButtonFlat btnOk = (ButtonFlat)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OnOkClick(selectedPaked, txtCena.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                UnitServiceActivity.this.finish();
            }
        });


        btnClear = (android.widget.Button) findViewById(R.id.btnC);
        btnComma = (android.widget.Button) findViewById(R.id.btnComa);
        setKalkulackaOnClickListeners();

        ((TextView)findViewById(R.id.lblMena)).setText(PortableCheckin.setting.currency_abbrev);

        txtCena.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                unit.sell_price = Double.valueOf(txtCena.getText().toString());
                return false;
            }
        });

        PortableCheckin app = (PortableCheckin) getApplicationContext();

        final List<DMPacket> packetList = new ArrayList<DMPacket>();
        List<DMPacket> packets = app.getPackets();


        final int unit_id = getIntent().getIntExtra("unit_id", -1);
        final int part_id = getIntent().getIntExtra("part_id", -1);
        final int part_position_id = getIntent().getIntExtra("part_position_id", -1);
        unit = app.getUnit(unit_id, part_position_id);

        String title = "";
        for(DMPrehliadkyMaster prehliadkyMaster  : PortableCheckin.prehliadkyMasters) {
            if(prehliadkyMaster.unitId == unit_id)
                title = prehliadkyMaster.text;
        }

        title = title + " - " + unit.chck_part_txt;
        if(!unit.chck_position_abbrev_txt.equalsIgnoreCase("-"))
            title += " " + unit.chck_position_txt;
        setTitle(title);

        if(packets != null) {
            Iterator<DMPacket> packetIterator = app.getPackets().iterator();
            DMPacket packet = null;

            while (packetIterator.hasNext()) {
                try {
                    packet = packetIterator.next();
                    if (packet.chck_unit_id == unit_id
                            && packet.chck_part_id == part_id)
                        packetList.add(packet);
                } catch (NoSuchElementException e) {
                    app.getDialog(this, "error", e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON);
                }
            }
        }

        packetList.addAll(app.getUnitService(unit));

        ListView listView = (ListView) findViewById(R.id.serviceList);
        PacketsArrayAdapter packetsArrayAdapter = new PacketsArrayAdapter( this, android.R.layout.simple_spinner_dropdown_item, 0, packetList, true);
        listView.setAdapter(packetsArrayAdapter);
        listView.setOnItemClickListener(onItemClick);
    }

    void setKalkulackaOnClickListeners() {
        ViewGroup viewGroup = (ViewGroup )findViewById(R.id.rlKalkulacka);
        final int count = viewGroup.getChildCount();

        for(int i =0; i<count; i++)
            ((android.widget.Button)viewGroup.getChildAt(i)).setOnClickListener(onKalkulackaClickListener);
    }

    public void OnOkClick(final DMPacket selectedPaked, final String cena) throws ParseException {
        if(selectedPaked == null) //Nevybrany paket
            return;
        unit.chck_status_id = DMUnit.eStatus_problem;
        if(selectedPaked.workshop_packet_number != null) {
            unit.workshop_packet_number = selectedPaked.workshop_packet_number;
            unit.workshop_packet_description = selectedPaked.workshop_packet_description;
            unit.economic = selectedPaked.economic;
            unit.spare_part_dispon_id = selectedPaked.spare_part_dispon_id;
            unit.chck_required_id = 19;
            unit.chck_required_txt = selectedPaked.workshop_packet_description;
            NumberFormat numberFormat = NumberFormat.getInstance();
            Number number = numberFormat.parse(cena);
            unit.sell_price = number.doubleValue();
            if(selectedPaked.restrictions != null)
                unit.chck_required_txt += " " + selectedPaked.restrictions;
            unit.updatePacket();
        } else {
            unit.workshop_packet = null;
            unit.workshop_packet_number = null;
            unit.workshop_packet_description = null;
            unit.spare_part_dispon_id = null;
            unit.economic = null;
            unit.chck_required_txt = selectedPaked.workshop_packet_description;
            unit.chck_required_id = selectedPaked.chck_required_id;
            NumberFormat numberFormat = NumberFormat.getInstance();
            Number number = numberFormat.parse(cena);
            unit.sell_price = number.doubleValue();
        }
        setResult(RESULT_OK);
        finish();
    }
    public TextView getTxtCena() {
        return txtCena;
    }

}
