package cz.tsystems.data;

import java.io.ByteArrayInputStream;
import java.util.Date;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DMOffers {
	final String TAG = DMOffers.class.getSimpleName();


	public int check_offer_id;
	public String text;
	public short show_txt;
	public Bitmap advert_banner;
	public double sell_price;
	public Date valid_from;
	public Date valid_until;
	public Date inserted;
	public Date last_updated;
	public String brand_id;
	public String hash;
	public boolean show_offer;

	
	public DMOffers(Cursor cursor) {
		
		int columnName;
		if((columnName = cursor.getColumnIndex("CHECK_OFFER_ID")) != -1)
			check_offer_id = cursor.getShort(columnName);
		if((columnName = cursor.getColumnIndex("TEXT")) != -1)
			text = cursor.getString(columnName);
		if((columnName = cursor.getColumnIndex("SHOW_TXT")) != -1)
			show_txt = cursor.getShort(columnName);
		if((columnName = cursor.getColumnIndex("ADVERT_BANNER")) != -1)
			advert_banner = BitmapFactory.decodeStream(new ByteArrayInputStream(cursor.getBlob(columnName))) ;
		if((columnName = cursor.getColumnIndex("SELL_PRICE")) != -1)
			sell_price = cursor.getDouble(columnName);
		if((columnName = cursor.getColumnIndex("SHOW_TXT")) != -1)
			valid_from = new Date(cursor.getLong(columnName));
		if((columnName = cursor.getColumnIndex("VALID_UNTIL")) != -1)
			valid_until = new Date(cursor.getLong(columnName));
		if((columnName = cursor.getColumnIndex("INSERTED")) != -1)
			inserted = new Date(cursor.getLong(columnName));
		if((columnName = cursor.getColumnIndex("LAST_UPDATED")) != -1)
			last_updated = new Date(cursor.getLong(columnName));
		if((columnName = cursor.getColumnIndex("BRAND_ID")) != -1)
			brand_id = cursor.getString(columnName);
		if((columnName = cursor.getColumnIndex("HASH")) != -1)
			hash = cursor.getString(columnName);
		if((columnName = cursor.getColumnIndex("SHOW_OFFER")) != -1)
			show_offer = cursor.getInt(columnName) == 1;
		
	}
}
