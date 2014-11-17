package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMCustomerInfo {
	public String description;
	public String sort_idx;
	public String value;
}
