package cz.tsystems.data;

import java.util.Comparator;

/**
 * Created by kubisj on 9.1.2015.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMPlannedActivities {
    public String planned_order_id;
    public String workshop_description;
    public String team_description;
    public String planned_activity;
    public int timeunits;
}
