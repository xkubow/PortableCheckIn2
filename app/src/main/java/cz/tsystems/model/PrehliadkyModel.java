package cz.tsystems.model;

import java.util.Locale;

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
    public static int[] prehliadka_id= {-1, -2, -3};
	private SQLiteDatabase mDb;

	public PrehliadkyModel(Context mContext) {
		super(mContext);
		mDb = ((PortableCheckin)mContext.getApplicationContext()).getTheDBProvider().getReadableDatabase();
	}
	
	public Cursor getPrehliadky() {		
		final DMScenar scenar = ((PortableCheckin)getContext().getApplicationContext()).getSelectedScenar();
		final String query = "SELECT DATA.ROWID AS _id, DATA.TEXT AS TEXT, DATA.CHCK_UNIT_ID AS CHCK_UNIT_ID, SU.MANDATORY AS MANDATORY FROM "
		       	+ "(SELECT ifnull(UL.CHCK_UNIT_TXT_LOC, U.CHCK_UNIT_TXT_DEF) AS TEXT, U.CHCK_UNIT_ID "
		       	+ "FROM CHCK_UNIT U LEFT OUTER JOIN CHCK_UNIT_LOC UL ON U.CHCK_UNIT_ID = UL.CHCK_UNIT_ID AND UL.LANG_ENUM = ?) DATA, CHECK_SCENARIO_UNIT SU "
		       	+ "WHERE DATA.CHCK_UNIT_ID = SU.CHCK_UNIT_ID "
		       	+ "AND SU.CHECK_SCENARIO_ID = ?";
		Cursor c = mDb.rawQuery(query,new String[] {Locale.getDefault().getLanguage(), String.valueOf(scenar.check_scenario_id)} );

				
		// Create a MatrixCursor filled with the rows you want to add.
		MatrixCursor matrixCursor = new MatrixCursor(new String[] { "_id", "TEXT", "CHCK_UNIT_ID", "MANDATORY" });
		matrixCursor.addRow(new Object[] { prehliadka_id[PREHLAIDKA_ENUM.eVYBAVY.ordinal()], getContext().getResources().getString(R.string.Vybavy), (long) -1,  scenar.equipment_mandat?1:0});
		matrixCursor.addRow(new Object[] { prehliadka_id[PREHLAIDKA_ENUM.ePOV_VYBAVY.ordinal()], getContext().getResources().getString(R.string.PovinneVybavy), (long) -1,  scenar.oblig_equipment_mandat?1:0});
        matrixCursor.addRow(new Object[] { prehliadka_id[PREHLAIDKA_ENUM.eSERVIS.ordinal()], getContext().getResources().getString(R.string.service), (long) -1,  scenar.services_mandat?1:0});

		// Merge your existing cursor with the matrixCursor you created.
		MergeCursor mergeCursor = new MergeCursor(new Cursor[] { matrixCursor, c });
		mergeCursor.moveToNext();
		return mergeCursor;
	}

    public int getPrehliadkaId(PREHLAIDKA_ENUM prehlaidka) {
        return prehliadka_id[prehlaidka.ordinal()];
    }
	

}
