package cz.tsystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.tsystems.data.DMUnit;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.data.SQLiteDBProvider;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UnitsModel extends Model {
	private final static String TAG = UnitsModel.class.getSimpleName();
	private SQLiteDatabase mDb;
	
	public UnitsModel(Context mContext) {
		super(mContext);
		mDb = ((PortableCheckin)mContext.getApplicationContext()).getTheDBProvider().getReadableDatabase();
	}
	
	public List<List<DMUnit>> loadUnits(Cursor cursor) {
		
		List<List<DMUnit>> units = new ArrayList<List<DMUnit>>();
		final int columnIndex = cursor.getColumnIndex("CHCK_UNIT_ID");
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			final int unitId = cursor.getInt(columnIndex);
			if(unitId > 0 )
				units.add(loadUnit(unitId));
			cursor.moveToNext();
		}
		return units;
	}
	
	private List<DMUnit> loadUnit(final int unitId) {
		final String query = "SELECT DATAPOS.*, DATAPART.*, PP.CHCK_PART_POSITION_ID AS CHCK_PART_POSITION_ID, '' AS SELL_PRICE "
				+ "FROM (SELECT ifnull(PL.CHCK_POSITION_TXT_LOC, P.CHCK_POSITION_TXT_DEF) AS CHCK_POSITION_TXT, "
				+ "     ifnull(PL.CHCK_POSITION_ABBREV_TXT_LOC, P.CHCK_POSITION_ABBREV_TXT_DEF) AS CHCK_POSITION_ABBREV_TXT, P.CHCK_POSITION_ID "  
				+ "     FROM CHCK_POSITION P LEFT OUTER JOIN CHCK_POSITION_LOC PL ON PL.CHCK_POSITION_ID = P.CHCK_POSITION_ID AND PL.LANG_ENUM = ?) DATAPOS, "
				+ "      (SELECT ifnull(PL.CHCK_PART_TXT_LOC, P.CHCK_PART_TXT_DEF) AS CHCK_PART_TXT, P.CHCK_PART_ID, P.CHCK_UNIT_ID "
				+ "      FROM CHCK_PART P LEFT OUTER JOIN CHCK_PART_LOC PL ON PL.CHCK_PART_ID = P.CHCK_PART_ID  AND PL.LANG_ENUM = ? " 
				+ "      WHERE P.CHCK_UNIT_ID = ?) DATAPART, CHCK_PART_POSITION PP "
				+ "WHERE DATAPOS.CHCK_POSITION_ID = PP.CHCK_POSITION_ID "
				+ "AND PP.CHCK_PART_ID = DATAPART.CHCK_PART_ID";
		Cursor c = mDb.rawQuery(query,new String[] {Locale.getDefault().getLanguage(), Locale.getDefault().getLanguage(), String.valueOf(unitId)} );
		
		List<DMUnit> unitList = new ArrayList<DMUnit>();
		c.moveToFirst();
		while(!c.isAfterLast()) {
			unitList.add(new DMUnit(c));
			c.moveToNext();
		}
		
		return unitList;
	}

}
