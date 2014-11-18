package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMSetting {

	public short generation;
	public short db_minor_version;	
	public short db_major_version;
	public int silhouette_id;	
	public boolean auto_show_planned;
	public boolean auto_show_akt_checkins;
	public short auto_log_off_minutes;
	public boolean show_def_ti_valid_until;
	public boolean show_def_ec_valid_until;
	public boolean workshop_packets_allowed;
	public String currency_abbrev;
	public String default_printer_name;
	public boolean obligatory_equipment_checked;
	public boolean show_def_odometer;
	
	public DMSetting() {
		
	}
}
