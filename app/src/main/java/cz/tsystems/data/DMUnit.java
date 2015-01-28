package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMUnit implements Comparable<DMUnit> {
    public static int eStatus_problem = 1;
    public static int eRequired_opravit = 1;
    public static int eRequired_vymenit = 2;
    public static int eRequired_seridit = 5;
    public static int eRequired_update = 7;
    public static int eRequired_odlozit = 20;
    public static int eRequired_packet = 19;

    @JsonIgnoreProperties(ignoreUnknown=true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Workshop_packet {
        public String workshop_packet_number;
        public int group_nr;
        public String group_txt;
        public String restrictions;
        public String workshop_packet_description;
        public int spare_part_dispon_id;
        public String spare_part_dispon_txt;
        public boolean economic;
        public Double sell_price;

        public Workshop_packet(){};
    }

    @JsonProperty("CHCK_POSITION_TXT")
    public void setChck_position_txt(String chck_position_txt) {
        this.chck_position_txt = chck_position_txt;
    }
    @JsonIgnore
    public String getChck_position_txt() {
        return chck_position_txt;
    }
	public String chck_position_txt;

    @JsonProperty("CHCK_POSITION_ABBREV_TXT")
    public void setChck_position_abbrev_txt(String chck_position_abbrev_txt) {
        this.chck_position_abbrev_txt = chck_position_abbrev_txt;
    }
    @JsonIgnore
    public String getChck_position_abbrev_txt() {
        return chck_position_abbrev_txt;
    }
	public String chck_position_abbrev_txt;

    @JsonProperty("CHCK_POSITION_ID")
    public void setChck_position_id(int chck_position_id) {
        this.chck_position_id = chck_position_id;
    }
    @JsonIgnore
    public int getChck_position_id() {
        return chck_position_id;
    }
	public int chck_position_id;

    @JsonProperty("CHCK_PART_TXT")
    public void setChck_part_txt(String chck_part_txt) {
        this.chck_part_txt = chck_part_txt;
    }
    @JsonIgnore
    public String getChck_part_txt() {
        return chck_part_txt;
    }
	public String chck_part_txt;

	public int chck_part_id;
	public int chck_unit_id;
	public int chck_part_position_id;
    public Integer chck_status_id;
    public Integer chck_required_id;
    public String chck_required_txt;
	public Double sell_price;
//    public DMPacket workshop_packet;
    public Long workshop_packet_id;
    public Boolean economic;
    public Integer spare_part_dispon_id;
    public String workshop_packet_number;
    public String workshop_packet_description;
    public Workshop_packet workshop_packet;

    public DMUnit(){}

	public DMUnit(Cursor cursor) {
		int columnName;
		if((columnName = cursor.getColumnIndex("CHCK_POSITION_TXT")) != -1)
			chck_position_txt = cursor.getString(columnName);
		if((columnName = cursor.getColumnIndex("CHCK_POSITION_ABBREV_TXT")) != -1)
			chck_position_abbrev_txt = cursor.getString(columnName);
		if((columnName = cursor.getColumnIndex("CHCK_POSITION_ID")) != -1)
			chck_position_id = cursor.getInt(columnName);
		if((columnName = cursor.getColumnIndex("CHCK_PART_TXT")) != -1)
			chck_part_txt = cursor.getString(columnName);
		if((columnName = cursor.getColumnIndex("SELL_PRICE")) != -1 && !cursor.getString(columnName).isEmpty())
			sell_price = cursor.getDouble(columnName);
		if((columnName = cursor.getColumnIndex("CHCK_PART_ID")) != -1)
			chck_part_id = cursor.getInt(columnName);
		if((columnName = cursor.getColumnIndex("CHCK_UNIT_ID")) != -1)
			chck_unit_id = cursor.getInt(columnName);
		if((columnName = cursor.getColumnIndex("CHCK_PART_POSITION_ID")) != -1)
			chck_part_position_id = cursor.getInt(columnName);
        if((columnName = cursor.getColumnIndex("CHCK_STATUS_ID")) != -1)
            chck_status_id = cursor.getInt(columnName);
        if((columnName = cursor.getColumnIndex("CHCK_REQUIRED_ID")) != -1)
            chck_required_id = cursor.getInt(columnName);
        if((columnName = cursor.getColumnIndex("CHCK_REQUIRED_TXT")) != -1)
            chck_required_txt = cursor.getString(columnName);
//		if((columnName = cursor.getColumnIndex("sell_price")) != -1)
//			sell_price = (cursor.isNull(columnName))?-1.0:cursor.getDouble(columnName);
	}

    @JsonIgnore
    public String getSellPriceAsString(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        return  nf.format(sell_price);
    }

    public void initWorkshopPacket(){
        this.workshop_packet = new Workshop_packet();
    }

    public void updatePacket() {
        if(this.workshop_packet == null)
            this.workshop_packet = new Workshop_packet();
        this.workshop_packet.workshop_packet_number = this.workshop_packet_number;
//        this.workshop_packet.group_nr
//        this.workshop_packet.group_txt;
//        this.workshop_packet.restrictions = this.wo;
        this.workshop_packet.workshop_packet_description = this.workshop_packet_description;
        this.workshop_packet.spare_part_dispon_id = this.spare_part_dispon_id;
//        this.workshop_packet.spare_part_dispon_txt;
        this.workshop_packet.economic = this.economic;
        this.workshop_packet.sell_price = this.sell_price;
    }

    @Override
	public int compareTo(DMUnit another) {
		
		return (this.chck_part_position_id == another.chck_part_position_id
				&& this.chck_unit_id == another.chck_unit_id)?1:0;
	}

}
