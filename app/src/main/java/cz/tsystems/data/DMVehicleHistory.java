package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMVehicleHistory {
	public boolean brief_history;
	public String customer_id;
	public String history_description;
	public short history_type;
	public String history_type_txt;
	public String item_no;
	public String personal_tag;
}
