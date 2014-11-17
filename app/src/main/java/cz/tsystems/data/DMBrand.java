package cz.tsystems.data;

import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DMBrand {
	final String TAG = DMBrand.class.getSimpleName();
	public int index;
	public static enum BrandsEnum {A, C, F, N, P, S, V};
	public static String[] theBrands = {"A", "C", "F", "N", "P", "S", "V"};
	public static enum ColumnsEnum {BRAND_ID, BRAND_TXT};	
	public static String[] columnNames = {"BRAND_ID", "BRAND_TXT"};
	

	public String brand_id;
	public int silhouette_id;
	public int check_scenario_id_def;
	public String text;

	public DMBrand(Cursor cursor) {
		if(cursor.getCount() != 1) {
			Log.e(TAG, "recordCount != 1");
			return;
		}
		
		cursor.moveToFirst();
		
		brand_id = cursor.getString(cursor.getColumnIndex("BRAND_ID"));
		silhouette_id = cursor.getInt(cursor.getColumnIndex("SILHOUETTE_ID"));
		check_scenario_id_def = cursor.getInt(cursor.getColumnIndex("CHECK_SCENARIO_ID_DEF"));
		text = cursor.getString(cursor.getColumnIndex("TEXT"));
	}
	
	public Drawable getBrandImage(Context context) {
		Drawable retValue = context.getResources().getDrawable( R.drawable.brand_nologo);
		
		if(brand_id.equalsIgnoreCase(DMBrand.theBrands[DMBrand.BrandsEnum.A.ordinal()]))
			retValue = context.getResources().getDrawable( R.drawable.brand_audi);
		else if(brand_id.equalsIgnoreCase(DMBrand.theBrands[DMBrand.BrandsEnum.C.ordinal()]))
			retValue = context.getResources().getDrawable( R.drawable.brand_skoda);
//		else if(brand_id.equalsIgnoreCase(DMBrand.theBrands[DMBrand.BrandsEnum.F.ordinal()]))
//			retValue = context.getResources().getDrawable( R.drawable.brand_nologo);
		else if(brand_id.equalsIgnoreCase(DMBrand.theBrands[DMBrand.BrandsEnum.N.ordinal()]))
			retValue = context.getResources().getDrawable( R.drawable.brand_vw_uziv);
		else if(brand_id.equalsIgnoreCase(DMBrand.theBrands[DMBrand.BrandsEnum.P.ordinal()]))
			retValue = context.getResources().getDrawable( R.drawable.brand_porsche);
		else if(brand_id.equalsIgnoreCase(DMBrand.theBrands[DMBrand.BrandsEnum.S.ordinal()]))
			retValue = context.getResources().getDrawable( R.drawable.brand_seat);
		else if(brand_id.equalsIgnoreCase(DMBrand.theBrands[DMBrand.BrandsEnum.V.ordinal()]))
			retValue = context.getResources().getDrawable( R.drawable.brand_vw);
		
		return retValue;
	}
}
