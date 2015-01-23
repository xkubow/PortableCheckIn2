package cz.tsystems.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by kubisj on 20.1.2015.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class DMDamagePoint {
    public int coord_x; //1024
    public int coord_y; //1024
    public int damage_enum;
    public String description="";
    public int image_enum;
    public int order;

    public DMDamagePoint(){};
    @JsonIgnore
    public DMDamagePoint(final int newCoord_x, final int newCoord_y, final int newDamage_enum, final int newImage_enum, final int newOrder) {
        coord_x = (int)(newCoord_x*PortableCheckin.silhuetteSize.widthTo1024);
        coord_y = (int)(newCoord_y*PortableCheckin.silhuetteSize.heightTo1024);
        damage_enum = newDamage_enum;
        image_enum = newImage_enum;
        order = newOrder+1;
    }

    public int getX() {
        return (int)(coord_x*PortableCheckin.silhuetteSize.widthFrom1024);
    }
    public int getY() {
        return (int)(coord_y*PortableCheckin.silhuetteSize.heightFrom1024);
    }
    public void setCoord_x(int coord_x) {
        this.coord_x = coord_x;
    }
    public void setCoord_y(int coord_y) {
        this.coord_y = coord_y;
    }
    public void setX(int x) {
        this.coord_x = (int)(x * PortableCheckin.silhuetteSize.widthTo1024);
    }
    public void setY(int y) {
        this.coord_y = (int)(y * PortableCheckin.silhuetteSize.heightTo1024);
    }
}
