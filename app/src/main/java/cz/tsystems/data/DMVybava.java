package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DMVybava extends DMBaseItem {
    @JsonIgnore
	final static String TAG = DMVybava.class.getSimpleName();
    public Long car_equipment_id;
    @JsonIgnore
    public String text;
    public boolean checked;
	public boolean obligatory_equipment;
    @JsonIgnore
    public boolean editable = false;
	private Context context;

    @JsonIgnore
    public Long get_id(){return car_equipment_id;};
    @JsonIgnore
    public void set_id(final long id){
        car_equipment_id = id;};
    public String getText(){return text;};
    @JsonProperty("CAR_EQUIPMENT_TXT")
    public void setText(final String newText){this.text = newText;};
//    @JsonIgnore
    public boolean getChecked(){return checked;};
//    @JsonProperty("CHECKED")
//    public int getCheckedJson(){return (checked)?1:0;};
//    @JsonProperty("OBLIGATORY_EQUIPMENT")
//    public int getobligatory_equipmentJson(){return (obligatory_equipment)?1:0;};
    public void setChecked(final boolean newChecked) {this.checked = newChecked;};

    public DMVybava(){};

	public DMVybava(Context context, long id, String text, final boolean checked, final boolean editable) {
		this.car_equipment_id = id;
		this.text = text;
		this.checked = checked;
		this.context = context;
        this.obligatory_equipment = false;
        this.editable = editable;
    }
	
	public DMVybava(Cursor c, boolean checked) {
		this.car_equipment_id = c.getLong(c.getColumnIndex("CAR_EQUIPMENT_ID"));
		this.text = c.getString(c.getColumnIndex("TEXT"));
		this.obligatory_equipment = Boolean.valueOf( c.getString(c.getColumnIndex("OBLIGATORY_EQUIPMENT")));
		this.checked = checked;
	}

}
