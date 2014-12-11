package cz.tsystems.portablecheckin;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cz.tsystems.adapters.PacketsArrayAdapter;
import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMUnit;


public class UnitServiceActivity extends Dialog {

    private DMUnit unit;
    private DMPacket selectedPaked;
    private EditText txtCena;

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedPaked = (DMPacket)parent.getItemAtPosition(position);//packetList.get(position);
            txtCena.setText(selectedPaked.sell_price);
//            if(packet.workshop_packet_number != null) {
//                unit.packet = packet;
//                unit.chck_required_id = 19;
//                unit.chck_required_txt = packet.workshop_packet_description;
//                if(packet.restrictions != null)
//                    unit.chck_required_txt += " " + packet.restrictions;
//            }
        }
    };

    public UnitServiceActivity(Context context, DMUnit theUnit, List<DMPacket> packetList) {
        super(context);
        this.unit = theUnit;
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.activity_unit_service, null);
        txtCena = (EditText)view.findViewById(R.id.txtCena);
        Button btnOk = (Button)view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnOkClick(selectedPaked, txtCena.getText().toString());
            }
        });

        txtCena.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                unit.sell_price = txtCena.getText().toString();
                return false;
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.serviceList);
        PacketsArrayAdapter packetsArrayAdapter = new PacketsArrayAdapter( context, android.R.layout.simple_spinner_dropdown_item, 0, packetList, true);
        listView.setAdapter(packetsArrayAdapter);
        listView.setOnItemClickListener(onItemClick);
        setContentView(view);
    }

    public void OnOkClick(final DMPacket selectedPaked, final String cena) {

    }
    public EditText getTxtCena() {
        return txtCena;
    }


}
