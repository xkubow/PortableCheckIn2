package cz.tsystems.data;

/**
 * Created by KUBO on 8. 12. 2014.
 */
public class DMPrehliadkyMaster {
    public static int eSECTION = 0;
    public static int eNOSECTION = 1;
    public static int eVYBAVY = 2;
    public static int eSLUZBY = 3;
    public static int ePAKETY = 4;
    public static int eUNIT = 5;

    public final String sectionCaption;

    public int sectionPosition;
    public int listPosition;

    public int rowId;
    public String text;
    public int unitId = -1;
    public int groupNr = -1;
    public boolean mandatory = false;
    public boolean opened = false;
    public int type;
    public int viewType;


    public DMPrehliadkyMaster(final String caption) {
        viewType = eSECTION;
        type = eSECTION;
        sectionCaption = caption;
    }

    public DMPrehliadkyMaster( int newRowId, String newText, int newTheId, boolean newMandatory, int newTyp) {

        sectionCaption = null;
        rowId = newRowId;
        text = newText;
        mandatory = newMandatory;
        type = newTyp;
        viewType = eNOSECTION;
        if(type == eUNIT)
            unitId = newTheId;
        else if(type == ePAKETY)
            groupNr = newTheId;
    }

}
