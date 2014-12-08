package cz.tsystems.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMPrehliadkyMaster;
import cz.tsystems.data.DMScenar;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.data.SQLiteDBProvider;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class PrehliadkyModel extends Model {
	private final static String TAG = PrehliadkyModel.class.getSimpleName();
    public static enum PREHLAIDKA_ENUM {eVYBAVY, ePOV_VYBAVY, eSERVIS};
    public final static int[] prehliadka_id= {-1, -2, -3};
	private SQLiteDatabase mDb;

	public PrehliadkyModel(Context mContext) {
		super(mContext);
		mDb = ((PortableCheckin)mContext.getApplicationContext()).getTheDBProvider().getReadableDatabase();
	}
	
	public List<DMPrehliadkyMaster> getPrehliadky() {
        List<DMPrehliadkyMaster> prehliadkyMasterList = new ArrayList<DMPrehliadkyMaster>();
        final PortableCheckin app = (PortableCheckin)getContext().getApplicationContext();
		final DMScenar scenar = app.getSelectedScenar();
		final String query = "SELECT DATA.ROWID AS _id, DATA.TEXT AS TEXT, DATA.CHCK_UNIT_ID AS CHCK_UNIT_ID, SU.MANDATORY AS MANDATORY, -1 AS PACKET_GROUP_NR FROM "
		       	+ "(SELECT ifnull(UL.CHCK_UNIT_TXT_LOC, U.CHCK_UNIT_TXT_DEF) AS TEXT, U.CHCK_UNIT_ID "
		       	+ "FROM CHCK_UNIT U LEFT OUTER JOIN CHCK_UNIT_LOC UL ON U.CHCK_UNIT_ID = UL.CHCK_UNIT_ID AND UL.LANG_ENUM = ?) DATA, CHECK_SCENARIO_UNIT SU "
		       	+ "WHERE DATA.CHCK_UNIT_ID = SU.CHCK_UNIT_ID "
		       	+ "AND SU.CHECK_SCENARIO_ID = ?";
		Cursor c = mDb.rawQuery(query,new String[] {Locale.getDefault().getLanguage(), String.valueOf(scenar.check_scenario_id)} );
        int lastId = 0;

        c.moveToFirst();
        while(!c.isAfterLast()&& !c.isBeforeFirst()) {
            prehliadkyMasterList.add(new DMPrehliadkyMaster(lastId++, c.getString(c.getColumnIndex("TEXT")), c.getInt(c.getColumnIndex("CHCK_UNIT_ID")), (c.getInt(c.getColumnIndex("MANDATORY"))==1),DMPrehliadkyMaster.eUNIT));
            c.moveToNext();
        }
        prehliadkyMasterList.add(new DMPrehliadkyMaster(prehliadka_id[PREHLAIDKA_ENUM.eVYBAVY.ordinal()], getContext().getResources().getString(R.string.Vybavy), -1, scenar.equipment_mandat,DMPrehliadkyMaster.eSTATIC));
        prehliadkyMasterList.add(new DMPrehliadkyMaster(prehliadka_id[PREHLAIDKA_ENUM.ePOV_VYBAVY.ordinal()], getContext().getResources().getString(R.string.PovinneVybavy), -1, scenar.oblig_equipment_mandat,DMPrehliadkyMaster.eSTATIC));
        prehliadkyMasterList.add(new DMPrehliadkyMaster(prehliadka_id[PREHLAIDKA_ENUM.eSERVIS.ordinal()], getContext().getResources().getString(R.string.service), -1, scenar.services_mandat,DMPrehliadkyMaster.eSTATIC));

        if(app.getPackets() != null) {
            List<Integer> groupsNr = new ArrayList<Integer>();
            for (Iterator<DMPacket> i = app.getPackets().iterator(); i.hasNext(); ) {
                final DMPacket packet = i.next();
                if (!groupsNr.contains(packet.group_nr)) {
                    groupsNr.add(packet.group_nr);
                    prehliadkyMasterList.add(new DMPrehliadkyMaster(++lastId, packet.group_text, packet.group_nr, false,DMPrehliadkyMaster.eGROUP));
                }
            }
            groupsNr = null;
        }

    	return prehliadkyMasterList;
	}

    public int getPrehliadkaId(PREHLAIDKA_ENUM prehlaidka) {
        return prehliadka_id[prehlaidka.ordinal()];
    }
	

}
