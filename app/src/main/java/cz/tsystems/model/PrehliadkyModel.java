package cz.tsystems.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMPrehliadky;
import cz.tsystems.data.DMPrehliadkyMaster;
import cz.tsystems.data.DMScenar;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class PrehliadkyModel extends Model {
	private final static String TAG = PrehliadkyModel.class.getSimpleName();
    public static int eVYBAVY = -1, ePOV_VYBAVY = -2, eSERVIS = -3;
	private SQLiteDatabase mDb;

	public PrehliadkyModel(Context mContext) {
		super(mContext);
		mDb = ((PortableCheckin)mContext.getApplicationContext()).getTheDBProvider().getReadableDatabase();
	}
	
	public List<DMPrehliadkyMaster> getPrehliadky() {
        PortableCheckin.selectedScenar.mandatoryCount = 0;
        List<DMPrehliadkyMaster> prehliadkyMasterList = new ArrayList<DMPrehliadkyMaster>();
        final PortableCheckin app = (PortableCheckin)getContext().getApplicationContext();
        final DMScenar scenar = app.getSelectedScenar();

        prehliadkyMasterList.add(new DMPrehliadkyMaster(eVYBAVY, getContext().getResources().getString(R.string.Vybavy), -1, scenar.equipment_mandat,DMPrehliadkyMaster.eVYBAVY));
        prehliadkyMasterList.add(new DMPrehliadkyMaster(ePOV_VYBAVY, getContext().getResources().getString(R.string.PovinneVybavy), -1, scenar.oblig_equipment_mandat,DMPrehliadkyMaster.eVYBAVY));
        prehliadkyMasterList.add(new DMPrehliadkyMaster(eSERVIS, getContext().getResources().getString(R.string.service), -1, scenar.services_mandat,DMPrehliadkyMaster.eSLUZBY));

        for(DMPrehliadkyMaster prehliadkyMaster : prehliadkyMasterList)
            if(prehliadkyMaster.mandatory)
                PortableCheckin.selectedScenar.mandatoryCount++;

		final String query = "SELECT DATA.ROWID AS _id, DATA.TEXT AS TEXT, DATA.CHCK_UNIT_ID AS CHCK_UNIT_ID, SU.MANDATORY AS MANDATORY, -1 AS PACKET_GROUP_NR FROM "
		       	+ "(SELECT ifnull(UL.CHCK_UNIT_TXT_LOC, U.CHCK_UNIT_TXT_DEF) AS TEXT, U.CHCK_UNIT_ID "
		       	+ "FROM CHCK_UNIT U LEFT OUTER JOIN CHCK_UNIT_LOC UL ON U.CHCK_UNIT_ID = UL.CHCK_UNIT_ID AND UL.LANG_ENUM = ?) DATA, CHECK_SCENARIO_UNIT SU "
		       	+ "WHERE DATA.CHCK_UNIT_ID = SU.CHCK_UNIT_ID "
		       	+ "AND SU.CHECK_SCENARIO_ID = ?";
		Cursor c = mDb.rawQuery(query,new String[] {Locale.getDefault().getLanguage(), String.valueOf(scenar.check_scenario_id)} );
        int lastId = 0;

        c.moveToFirst();
        Log.d(TAG, Locale.getDefault().getLanguage() + ", " + String.valueOf(scenar.check_scenario_id));
        Log.d(TAG, String.valueOf(c.getCount()));
        while(!c.isAfterLast()&& !c.isBeforeFirst()) {
            DMPrehliadkyMaster prehliadkyMaster = new DMPrehliadkyMaster(lastId++, c.getString(c.getColumnIndex("TEXT")), c.getInt(c.getColumnIndex("CHCK_UNIT_ID")), (c.getInt(c.getColumnIndex("MANDATORY"))==1),DMPrehliadkyMaster.eUNIT);
            if(prehliadkyMaster.mandatory)
                PortableCheckin.selectedScenar.mandatoryCount++;
            prehliadkyMasterList.add(prehliadkyMaster);
            c.moveToNext();
        }

        setPackets(getContext(), prehliadkyMasterList);

/*        if(app.getPackets() != null) {
            prehliadkyMasterList.add(new DMPrehliadkyMaster(++lastId, "all", -1, false,DMPrehliadkyMaster.ePAKETY));
            List<Integer> groupsNr = new ArrayList<Integer>();
            for (Iterator<DMPacket> i = app.getPackets().iterator(); i.hasNext(); ) {
                final DMPacket packet = i.next();
                if (!groupsNr.contains(packet.group_nr)) {
                    groupsNr.add(packet.group_nr);
                    prehliadkyMasterList.add(new DMPrehliadkyMaster(++lastId, packet.group_text, packet.group_nr, false,DMPrehliadkyMaster.ePAKETY));
                }
            }
        }*/

    	return prehliadkyMasterList;
	}

    public static void setPackets(Context context, List<DMPrehliadkyMaster> prehliadkyMasterList) {
        if(prehliadkyMasterList == null)
            prehliadkyMasterList = new ArrayList<>();

        DMPrehliadkyMaster prehliadkyMaster = prehliadkyMasterList.get(prehliadkyMasterList.size()-1);

        if(prehliadkyMaster.type == DMPrehliadkyMaster.ePAKETY && prehliadkyMaster.groupNr == -1)
            prehliadkyMasterList.remove(prehliadkyMasterList.size()-1);

        int lastId = prehliadkyMasterList.size() -1;
        if(PortableCheckin.packets != null) {
            prehliadkyMasterList.add(new DMPrehliadkyMaster(++lastId, String.format("%s [%d]", context.getResources().getString(R.string.All_packets), PortableCheckin.packets.size()), -1, false,DMPrehliadkyMaster.ePAKETY));
            List<Integer> groupsNr = new ArrayList<Integer>();
            for (Iterator<DMPacket> i = PortableCheckin.packets.iterator(); i.hasNext(); ) {
                final DMPacket packet = i.next();
                if (!groupsNr.contains(packet.group_nr)) {
                    groupsNr.add(packet.group_nr);
                    prehliadkyMasterList.add(new DMPrehliadkyMaster(++lastId, String.format("%s [%d]", packet.group_text, PortableCheckin.getPaket(packet.group_nr, null).size()), packet.group_nr, false,DMPrehliadkyMaster.ePAKETY));
                }
            }
        }
    }

}
