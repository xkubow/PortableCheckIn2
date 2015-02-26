package cz.tsystems.portablecheckin;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    NumberFormat numberFormat = new DecimalFormat("##0.00");

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

        Spanned cenaSpanned;

        TextView cena = (TextView) view.findViewById(R.id.cenaND);
        cenaSpanned = Html.fromHtml("<b>" + context.getResources().getString(R.string.Material) + "</b>" +
                ":<br/>" + numberFormat.format(packetDetailArrayAdapter.cenaND) + " " + PortableCheckin.setting.currency_abbrev);
        cena.setText(cenaSpanned);
        cena = (TextView) view.findViewById(R.id.cenaPP);
        cenaSpanned = Html.fromHtml("<b>" + context.getResources().getString(R.string.Prace) + "</b>" +
                ":<br/>" + numberFormat.format(packetDetailArrayAdapter.cenaPP) + " " + PortableCheckin.setting.currency_abbrev);
        cena.setText(cenaSpanned);
        cena = (TextView) view.findViewById(R.id.cenaCelk);
        cenaSpanned = Html.fromHtml("<b>" + context.getResources().getString(R.string.Celkem) + "</b>" +
                ":<br/>" + numberFormat.format(packetDetailArrayAdapter.cenaCelek) + " " + PortableCheckin.setting.currency_abbrev);
        cena.setText(cenaSpanned);

        listView = (ListView) view.findViewById(R.id.packetDetailList);
        listView.setAdapter(packetDetailArrayAdapter);

        setContentView(view);
    }

}
