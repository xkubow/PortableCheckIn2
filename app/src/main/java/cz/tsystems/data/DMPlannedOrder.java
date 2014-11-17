package cz.tsystems.data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class DMPlannedOrder {
	public String planned_order_id;
	public int checkin_id;
	public int checkin_number;
	public String data_source;
	public String planned_order_status;
	public String planned_order_no;
	public String license_tag;
	public String customer_label;
	public String vehicle_description;
	public String vehicle_caption;
	public Date planned;
	public String personal_tag;
	public String personal_id;
	public String chi_po_number;
	
	public DMPlannedOrder() {
		
	}
}
