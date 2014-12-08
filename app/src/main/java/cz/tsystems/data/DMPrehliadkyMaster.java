package cz.tsystems.data;

/**
 * Created by KUBO on 8. 12. 2014.
 */
public class DMPrehliadkyMaster {
    public static int eSTATIC = 0;
    public static int eGROUP = 1;
    public static int eUNIT = 2;

    public int rowId;
    public String text;
    public int unitId = -1;
    public int groupNr = -1;
    public boolean mandatory = false;
    public int typ;

    public DMPrehliadkyMaster( int newRowId, String newText, int newTheId, boolean newMandatory, int newTyp) {
        rowId = newRowId;
        text = newText;
        mandatory = newMandatory;
        typ = newTyp;
        if(typ == eUNIT)
            unitId = newTheId;
        else if(typ == eGROUP)
            groupNr = newTheId;
    }

}
