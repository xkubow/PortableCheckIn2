package cz.tsystems.portablecheckin;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.adapters.PacketDetailArrayAdapter;
import cz.tsystems.adapters.PacketsArrayAdapter;
import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMPacketDetail;
import cz.tsystems.data.PortableCheckin;

/**
 * Created by kubisj on 29.1.2015.
 */
public class PacketDetailDialog extends Dialog{
    private PacketDetailArrayAdapter packetDetailArrayAdapter;
    List<DMPacketDetail> packetDetailList;
    ListView listView;

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };


    public PacketDetailDialog(Context context, List<DMPacketDetail> packetDetails) {
        super(context);
        packetDetailList = packetDetails;
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.activity_packet_info, null);
        packetDetailArrayAdapter = new PacketDetailArrayAdapter(getContext(), packetDetailList);

        TextView cena = (TextView) view.findViewById(R.id.cenaND);
        cena.setText(String.valueOf(packetDetailArrayAdapter.cenaND) + " " + PortableCheckin.setting.currency_abbrev);
        cena = (TextView) view.findViewById(R.id.cenaPP);
        cena.setText(String.valueOf(packetDetailArrayAdapter.cenaPP) + " " + PortableCheckin.setting.currency_abbrev);
        cena = (TextView) view.findViewById(R.id.cenaCelk);
        cena.setText(String.valueOf(packetDetailArrayAdapter.cenaCelek) + " " + PortableCheckin.setting.currency_abbrev);

        listView = (ListView) view.findViewById(R.id.packetDetailList);
        listView.setAdapter(packetDetailArrayAdapter);

        setContentView(view);
    }

}
