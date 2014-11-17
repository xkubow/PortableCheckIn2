package cz.tsystems.data;

import java.util.Date;

import cz.tsystems.communications.CommunicationService;
import android.database.Cursor;
import android.util.Log;

public class DMScenar {
	final String TAG = DMScenar.class.getSimpleName();
	
	public int check_scenario_id;
	public String check_scenario_txt_def;
	public int check_duration_min;
	public Date inserted;
	public Date last_updated;
	public String brand_id;
	public boolean show_scenario;
	public boolean oblig_equipment_mandat;
	public boolean equipment_mandat;
	public boolean services_mandat;
	public short service_free_count;
	public short equipment_free_count;
	public boolean scenario_locked;
	public String text;
	
	
	public DMScenar(Cursor cursor) {
		if(cursor.getCount() != 1) {
			Log.e(TAG, "recordCount != 1");
			return;
		}
		
		cursor.moveToFirst();
		
		check_scenario_id = cursor.getInt(cursor.getColumnIndex("CHECK_SCENARIO_ID"));
		check_scenario_txt_def = cursor.getString(cursor.getColumnIndex("CHECK_SCENARIO_TXT_DEF"));		
		check_duration_min = cursor.getInt(cursor.getColumnIndex("CHECK_DURATION_MIN"));
		inserted = new Date(cursor.getLong(cursor.getColumnIndex("INSERTED")));
		last_updated = new Date(cursor.getLong(cursor.getColumnIndex("LAST_UPDATED")));
		brand_id = cursor.getString(cursor.getColumnIndex("BRAND_ID"));
		show_scenario = cursor.getInt(cursor.getColumnIndex("SHOW_SCENARIO")) == 1;
		oblig_equipment_mandat = cursor.getInt(cursor.getColumnIndex("OBLIG_EQUIPMENT_MANDAT")) == 1;
		equipment_mandat = cursor.getInt(cursor.getColumnIndex("EQUIPMENT_MANDAT")) == 1;
		services_mandat = cursor.getInt(cursor.getColumnIndex("SERVICES_MANDAT")) == 1;
		service_free_count = cursor.getShort(cursor.getColumnIndex("SERVICE_FREE_COUNT")); 
		equipment_free_count = cursor.getShort(cursor.getColumnIndex("EQUIPMENT_FREE_COUNT"));
		scenario_locked = cursor.getInt(cursor.getColumnIndex("SCENARIO_LOCKED")) == 1;
		text = cursor.getString(cursor.getColumnIndex("TEXT"));		
			
	}

}
