package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 27.11.2014.
 */
public class DMPacket implements Comparable<DMPacket> {
    public String brand_id;
    public int chck_required_id = 19;
    public int chck_part_id;
    public String chck_part_txt;
    public int chck_unit_id;
    public String chck_unit_txt;
    @JsonProperty("DETAIL_LIST")
    public List<DMPacketDetail> detail_list;
    public boolean economic;
    public int group_nr;
    public String group_text;
    public String restrictions;
    public String sell_price;
    public int spare_part_dispon_id;
    public String spare_part_dispon_txt;
    public String workshop_packet_description;
    public String workshop_packet_number;

    public DMPacket(){}

    public DMPacket(Cursor cursor) {
        workshop_packet_description = cursor.getString(cursor.getColumnIndex("CHCK_REQUIRED_TXT"));
        chck_required_id = cursor.getInt(cursor.getColumnIndex("CHCK_REQUIRED_ID"));
        this.sell_price = "";
    }
    public void setDetail_list(ArrayList<DMPacketDetail> _detail_list) {
        this.detail_list = _detail_list;
    }

    @Override
    public int compareTo(DMPacket another) {
        return 0;
    }


    public void setSell_price(String sell_price) {
        if(sell_price != null)
            this.sell_price = sell_price;
        else
            this.sell_price = "";
    }

    public Drawable getPacketIcon(Context context) {
        Drawable packetIcon = null;

        if(workshop_packet_number == null)
            packetIcon = context.getResources().getDrawable(R.drawable.celky_servis);
        else if(this.spare_part_dispon_id == 1)
            packetIcon = context.getResources().getDrawable((this.economic)? R.drawable.packet_green_e:R.drawable.packet_green);
        else
            packetIcon = context.getResources().getDrawable((this.economic)?R.drawable.packet_orange_e:R.drawable.packet_orange);

        return packetIcon;
    }
}
