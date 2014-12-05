package cz.tsystems.data;

import android.database.Cursor;


public class DMUnit implements Comparable<DMUnit> {
	public String chck_position_txt;
	public String chck_position_abbrev_txt;
	public int chck_position_id;
	public String chck_part_txt;
	public int chck_part_id;
	public int chck_unit_id;
	public int chck_part_position_id;
    public int chck_status_id;
    public int chck_required_id;
    public String chck_required_txt;
	public String sell_price;
    public DMPacket packet;
	
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
		if((columnName = cursor.getColumnIndex("SELL_PRICE")) != -1)
			sell_price = cursor.getString(columnName);
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
	
	@Override
	public int compareTo(DMUnit another) {
		
		return (this.chck_part_position_id == another.chck_part_position_id
				&& this.chck_unit_id == another.chck_unit_id)?1:0;
	}

}
