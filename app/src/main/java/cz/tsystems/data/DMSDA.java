package cz.tsystems.data;

/**
 * Created by kubisj on 12.1.2015.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMSDA {
    public String action_no;
    public String action_type;
    public String action_txt;

}
