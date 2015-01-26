package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kubisj on 21.11.2014.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DMService extends DMBaseItem {

    public long check_service_id;
    @JsonIgnore
    public String text;
    public boolean checked;
    @JsonIgnore
    public boolean editable = false;
    public Double sell_price;
    private Context context;

    @JsonIgnore
    public long get_id(){return check_service_id;};
    @JsonIgnore()
    public void set_id(final long id){ check_service_id = id;};
    @JsonProperty("CHECKIN_SERVICE_FREE_ID")
    public void set_free_id(final long id){ check_service_id = id;};
    public String getText(){return text;};
    public void setText(final String newText){this.text = newText;};
    @JsonProperty("CHECK_SERVICE_TXT")
    public void setCheck_service_txt(final String newText){this.text = newText;};
    @JsonProperty("CHECKIN_SERVICE_FREE_TXT")
    public void setCheck_free_service_txt(final String newText){this.text = newText;};
    public boolean getChecked(){return checked;};
    public void setChecked(final boolean newChecked) {this.checked = newChecked;};

    public DMService(){};
    public DMService(Cursor c, boolean checked) {
        this.check_service_id = c.getLong(c.getColumnIndex("CHECK_SERVICE_ID"));
        this.text = c.getString(c.getColumnIndex("TEXT"));
        if(!c.isNull(c.getColumnIndex("SELL_PRICE")))
            this.sell_price = c.getDouble(c.getColumnIndex("SELL_PRICE"));
        this.checked = checked;
        this.editable = false;
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

    public boolean getEditable(){return this.editable;};
}
