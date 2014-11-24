package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DMVybava extends DMBaseItem {
	final static String TAG = DMVybava.class.getSimpleName();
	public boolean obligatory_equipment;
    public boolean editable = false;
	private Context context;
	
	public DMVybava(Context context, long id, String text, final boolean checked, final boolean editable) {
		this.item_id = id;
		this.text = text;
		this.checked = checked;
		this.context = context;
        this.obligatory_equipment = false;
        this.editable = editable;
    }
	
	public DMVybava(Cursor c, boolean checked) {
		this.item_id = c.getLong(c.getColumnIndex("CAR_EQUIPMENT_ID"));
		this.text = c.getString(c.getColumnIndex("TEXT"));
		this.obligatory_equipment = Boolean.valueOf( c.getString(c.getColumnIndex("OBLIGATORY_EQUIPMENT")));
		Log.v(TAG, String.valueOf(this.obligatory_equipment ) + " :" + String.valueOf(this.obligatory_equipment));
		this.checked = checked;
	}

}
