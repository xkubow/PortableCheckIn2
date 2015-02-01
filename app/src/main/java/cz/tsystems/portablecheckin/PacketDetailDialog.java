package cz.tsystems.portablecheckin;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cz.tsystems.adapters.PacketDetailArrayAdapter;
import cz.tsystems.adapters.PacketsArrayAdapter;
import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMPacketDetail;

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
        double cenaCelk=0.0, cenaPraca=0.0, cenaDiely=0.0;

        for(DMPacketDetail paket : packetDetails) {
            cenaCelk += paket.sell_price;
        }

        packetDetailArrayAdapter = new PacketDetailArrayAdapter(getContext(), packetDetailList);
        listView = (ListView) view.findViewById(R.id.packetDetailList);
        listView.setAdapter(packetDetailArrayAdapter);

        setContentView(view);
    }

}
