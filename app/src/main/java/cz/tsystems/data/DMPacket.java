package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 27.11.2014.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMPacket implements Comparable<DMPacket> {

    public long workshop_packet_id;
    public String workshop_packet_description;
    public String workshop_packet_number;
    public String brand_id;
    public int chck_required_id = 19;
    public int chck_part_id;
    public String chck_part_txt;
    public int chck_unit_id;
    public String chck_unit_txt;
    @JsonProperty("DETAIL_LIST")
    public List<DMPacketDetail> detail_list; //Detail paketu - cinnosti, material
    public boolean economic;
    public int group_nr;
    public String group_text;
    public String restrictions;
    public Double sell_price;
    public int spare_part_dispon_id;
    public String spare_part_dispon_txt;

    public DMPacket(){}

    public DMPacket(Cursor cursor) {
        workshop_packet_description = cursor.getString(cursor.getColumnIndex("CHCK_REQUIRED_TXT"));
        chck_required_id = cursor.getInt(cursor.getColumnIndex("CHCK_REQUIRED_ID"));
    }

    @Override
    public int compareTo(DMPacket another) {
        return this.workshop_packet_number.compareTo(another.workshop_packet_number);
    }


/*    public void setSell_price(String sell_price) {
        if(sell_price != null)
            this.sell_price = sell_price;
        else
            this.sell_price = "";
    }*/

    static public Drawable getPacketIcon(Context context, final int spare_part_dispon_id, final boolean economic) {
        if(spare_part_dispon_id == 1)
            return context.getResources().getDrawable((economic)? R.drawable.packet_green_e:R.drawable.packet_green);
        else
            return context.getResources().getDrawable((economic)?R.drawable.packet_orange_e:R.drawable.packet_orange);
    }

    public Drawable getCelkyIcon(Context context) {
        if(workshop_packet_number == null)
            return context.getResources().getDrawable(R.drawable.celky_servis);
        else
            return getPacketIcon(context, this.spare_part_dispon_id, this.economic);
    }
}
