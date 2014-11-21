package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DMVybava implements Comparable<DMVybava> {
	final static String TAG = DMVybava.class.getSimpleName();
	public long vybava_id;
	public String vybava_txt;
	public boolean obligatory_equipment;
	public boolean checked;
    public boolean editable = false;
	private Context context;
	
	public DMVybava(Context context, long id, String text, final boolean checked, final boolean editable) {
		this.vybava_id = id;
		this.vybava_txt = text;
		this.checked = checked;
		this.context = context;
        this.obligatory_equipment = false;
        this.editable = editable;
    }
	
	public DMVybava(Cursor c, boolean checked) {
		this.vybava_id = c.getLong(c.getColumnIndex("CAR_EQUIPMENT_ID"));
		this.vybava_txt = c.getString(c.getColumnIndex("TEXT"));
		this.obligatory_equipment = Boolean.valueOf( c.getString(c.getColumnIndex("OBLIGATORY_EQUIPMENT")));
		Log.v(TAG, String.valueOf(this.obligatory_equipment ) + " :" + String.valueOf(this.obligatory_equipment));
		this.checked = checked;
	}
	
	@Override
	public int compareTo(DMVybava another) {
		return (this.vybava_id == another.vybava_id)?1:0;
	}

}
