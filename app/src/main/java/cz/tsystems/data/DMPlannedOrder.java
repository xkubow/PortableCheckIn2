package cz.tsystems.data;

import java.util.Comparator;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class DMPlannedOrder implements Comparator<DMPlannedOrder> {
    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public final int type;
    public final String sectionCaption;

    public int sectionPosition;
    public int listPosition;

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
        type = ITEM;
        sectionCaption = null;
    }

    public DMPlannedOrder(final String caption) {
        type = SECTION;
        sectionCaption = caption;
    }

    @Override public String toString() {
        return sectionCaption;
    }

    @Override
    public int compare(DMPlannedOrder lhs, DMPlannedOrder rhs) {
        return lhs.personal_id.compareTo(rhs.personal_id);
    }
}