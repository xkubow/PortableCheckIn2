package cz.tsystems.data;

/**
 * Created by kubisj on 12.1.2015.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMOdlozenePolozky {
    public String demand_description;
    public String sell_price;

}
