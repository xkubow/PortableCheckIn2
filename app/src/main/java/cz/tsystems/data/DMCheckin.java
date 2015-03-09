package cz.tsystems.data;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMCheckin {

    public int silhouette_id;
    public Double odometer;
    public short fuel_level;
    public Date inserted;
    public Date last_updated;
    public String driver_name_surn;
    public boolean servbook_exists;
    public boolean crw_exists;
    public boolean insurance_case;
    public String note_protocol;
    public Date ti_valid_until;
    public Date ec_valid_until;
    public Integer check_scenario_id;
    public short interior_state;
    public short exterior_state = 2;
    public String note_order_list;
    public Date closed;
    public Date printed;
    public String brand_id;
    public String vehicle_type;
    public String license_tag;
    public String my;
    public String vin;
    public String vehicle_id;
    public String customer_id;
    public String planned_order_id;
    public String personal_id;
    public short fuel_id;
    public String mobile_device_id;
    public String planned_order_no;
    public Date planned;
    public String vehicle_description;
    public String colour_code;
    public String colour_description;
    public String customer_label;
    public Date sended;
    public Integer checkin_id;
    public Integer checkin_number;
    public Integer customer_category_id;
    public String customer_city;
    public String customer_zip;
    public String customer_address;
    public String customer_contact_phone;
    public String customer_contact_email;
    public String vehicle_caption;
    public String brnad_txt;
    public String fuel_txt;
    public short oil_level;

    @JsonIgnore()
    public List<String> workshop_pakets;

    public DMCheckin() {

    }
}
