package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by kubisj on 27.1.2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMVybavaFree extends DMBaseItem {
    @JsonIgnore
    final static String TAG = DMVybavaFree.class.getSimpleName();
    public Long checkin_equipment_free_id;
    public String text;
    public boolean checked;

    @Override
    public Long get_id() {
        return checkin_equipment_free_id;
    }

    @Override
    public void set_id(long id) {
        checkin_equipment_free_id = id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean getChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public DMVybavaFree(){}

    public DMVybavaFree(DMVybava vybava) {
        checkin_equipment_free_id = vybava.car_equipment_id;
        text = vybava.text;
        checked = vybava.checked;
    }
}
