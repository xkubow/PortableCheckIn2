package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by kubisj on 21.11.2014.
 */
public class DMService extends DMBaseItem {

    public long check_service_id;
    @JsonIgnore
    public String text;
    public boolean checked;
    @JsonIgnore
    public boolean editable = false;
    private Context context;

    public long get_id(){return check_service_id;};
    public void set_id(final long id){
        check_service_id = id;};
    public String getText(){return text;};
    public void setText(final String newText){this.text = newText;};
    public boolean getChecked(){return checked;};
    public void setChecked(final boolean newChecked) {this.checked = newChecked;};

    public DMService(Cursor c, boolean checked) {
        this.check_service_id = c.getLong(c.getColumnIndex("CHECK_SERVICE_ID"));
        this.text = c.getString(c.getColumnIndex("TEXT"));
        this.checked = checked;
    }

    public DMService(Context context, final long newService_id, final String newTtext, final boolean checked, final boolean newEditable) {
        this.check_service_id = newService_id;
        this.text = newTtext;
        this.checked = checked;
        this.context = context;
        this.editable = newEditable;
    }

    public int compareTo(DMService another) {
        return (this.check_service_id == another.check_service_id)?1:0;
    }
}
