package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMUser {
	
	public String name;
	public String surname;
	public String personal_id;
	public String brand_id;
	
	public DMUser() {
		
	}
}
