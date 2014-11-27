package cz.tsystems.data;

import android.database.Cursor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kubisj on 27.11.2014.
 */
public class DMPacket implements Comparable<DMPacket> {
    public String brand_id;
    public int chck_part_id;
    public String chck_part_txt;
    public int chck_unit_id;
    public String chck_unit_txt;
    @JsonProperty("DETAIL_LIST")
    private List<DMPacketDetail> detail_list;
    public boolean economic;
    public int group_nr;
    public String group_text;
    public String restrictions;
    public double sell_price;
    public int spare_part_dispon_id;
    public String spare_part_dispon_txt;
    public String workshop_packet_description;
    public String workshop_packet_number;

    public void setDetail_list(ArrayList<DMPacketDetail> _detail_list) {
        this.detail_list = _detail_list;
    }

    @Override
    public int compareTo(DMPacket another) {
        return 0;
    }
}
