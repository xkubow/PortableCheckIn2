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
    public int mandatoryCount = 0;
	
	
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
		show_scenario = cursor.getString(cursor.getColumnIndex("SHOW_SCENARIO")).equalsIgnoreCase("true");
		oblig_equipment_mandat = cursor.getString(cursor.getColumnIndex("OBLIG_EQUIPMENT_MANDAT")).equalsIgnoreCase("true");
		equipment_mandat = cursor.getString(cursor.getColumnIndex("EQUIPMENT_MANDAT")).equalsIgnoreCase("true");
		services_mandat = cursor.getString(cursor.getColumnIndex("SERVICES_MANDAT")).equalsIgnoreCase("true");
		service_free_count = cursor.getShort(cursor.getColumnIndex("SERVICE_FREE_COUNT")); 
		equipment_free_count = cursor.getShort(cursor.getColumnIndex("EQUIPMENT_FREE_COUNT"));
		scenario_locked = cursor.getString(cursor.getColumnIndex("SCENARIO_LOCKED")).equalsIgnoreCase("true");
		text = cursor.getString(cursor.getColumnIndex("TEXT"));		
			
	}

}
