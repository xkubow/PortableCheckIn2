package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by kubisj on 21.11.2014.
 */
public class DMService extends DMBaseItem {

    public boolean editable = false;
    private Context context;

    public DMService(Cursor c, boolean checked) {
        this.item_id = c.getLong(c.getColumnIndex("CHECK_SERVICE_ID"));
        this.text = c.getString(c.getColumnIndex("TEXT"));
        this.checked = checked;
    }

    public DMService(Context context, final long newService_id, final String newTtext, final boolean checked, final boolean newEditable) {
        this.item_id = newService_id;
        this.text = newTtext;
        this.checked = checked;
        this.context = context;
        this.editable = newEditable;
    }

    public int compareTo(DMService another) {
        return (this.item_id == another.item_id)?1:0;
    }
}
