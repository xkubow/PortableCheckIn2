package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by kubisj on 21.11.2014.
 */
public class DMService implements Comparable<DMService> {
    public long service_id;
    public String text;
    public boolean checked;
    public boolean editable = false;
    private Context context;

    public DMService(Cursor c, boolean checked) {
        this.service_id = c.getLong(c.getColumnIndex("CAR_EQUIPMENT_ID"));
        this.text = c.getString(c.getColumnIndex("TEXT"));
        this.checked = checked;
    }

    public DMService(Context context, final long newService_id, final String newTtext, final boolean checked, final boolean newEditable) {
        this.service_id = newService_id;
        this.text = newTtext;
        this.checked = checked;
        this.context = context;
        this.editable = newEditable;
    }

    @Override
    public int compareTo(DMService another) {
        return (this.service_id == another.service_id)?1:0;
    }
}
