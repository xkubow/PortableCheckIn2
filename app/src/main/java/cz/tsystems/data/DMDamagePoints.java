package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by kubisj on 20.1.2015.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class DMDamagePoints {

    public int coord_x;
    public int coord_y;
    public int damage_enum;
    public String description="";
    public int image_enum;
    public int order;

    public DMDamagePoints(){};
    @JsonIgnore
    public DMDamagePoints(final int newCoord_x, final int newCoord_y, final int newDamage_enum, final int newImage_enum, final int newOrder) {
        coord_x = newCoord_x;
        coord_y = newCoord_y;
        damage_enum = newDamage_enum;
        image_enum = newImage_enum+1;
        order = newOrder+1;
    }
    @JsonIgnore
    public int[] getPointArray() {
        return new int[] {coord_x, coord_y, damage_enum};
    }
}
