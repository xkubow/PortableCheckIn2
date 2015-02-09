package cz.tsystems.data;

import android.database.Cursor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by kubisj on 27.11.2014.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DMPacketDetail {
    public static int eSECTION = 0;
    public static final int ND = 1; //spare_parts
    public static final int PP = 2; //labor_position
    public static final int SP = 3; //small parts

    public int viewType;
    public int sectionPosition;
    public int listPosition;
    public final String sectionCaption;

    public String brand_id;
    public String capaket_item_description;
    public String capaket_item_enum;
    public int capaket_item_enum_number;
    public String capaket_item_number;
    public boolean economic;
    public Double sell_price;
    public int spare_part_dispon_id;
    public String vin;
    public  String workshop_packet_number;

    public DMPacketDetail(){sectionCaption="";}

    public DMPacketDetail(final String caption) {
        viewType = eSECTION;
        sectionCaption = caption;
    }

    public void setCapaket_item_enum(String newCapaket_item_enum) {
        this.capaket_item_enum = newCapaket_item_enum;
        if(capaket_item_enum.equalsIgnoreCase("SPARE_PART"))
            viewType = ND;
        else if(capaket_item_enum.equalsIgnoreCase("LABOR_POSITION"))
            viewType = PP;
        else if(capaket_item_enum.equalsIgnoreCase("SMALL_PARTS"))
            viewType = SP;
    }


}
