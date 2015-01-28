package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kubisj on 26.1.2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMServiceFree extends DMBaseItem {
    public Long checkin_service_free_id;
    public String text;
    public boolean checked;
    public Double sell_price;

    @JsonIgnore
    @Override
    public Long get_id() {
        return checkin_service_free_id;
    }

    @JsonIgnore
    @Override
    public void set_id(long id) {
        checkin_service_free_id = id;
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

    public DMServiceFree() {}
    public DMServiceFree(DMService service) {
        this.checkin_service_free_id = service.check_service_id;
        this.text = service.text;
        this.checked = service.checked;
        this.sell_price = service.sell_price;
    }
}

