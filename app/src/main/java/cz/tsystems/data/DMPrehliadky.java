package cz.tsystems.data;

import android.content.Context;
import android.database.Cursor;

public class DMPrehliadky implements Comparable<DMPrehliadky> {
	private final static String TAG = DMPrehliadky.class.getSimpleName();
	private long id;
	private int sectionId;
	private String text;
	private boolean mandatory;
	private Context context;
	
	public DMPrehliadky(Context context, final long id, final int sectionId, final String text, final boolean obligatory) {
		this.setId(id);
		this.setSectionId(sectionId);
		this.setText(text);
		this.setMandatory(obligatory);
		this.context = context;
	}
	
	public static DMPrehliadky fromCursor(Context ctx,Cursor c) {
		final long newId = c.getLong(c.getColumnIndex("CHCK_UNIT_ID"));
		final String newText = c.getString(c.getColumnIndex("TEXT"));
		final int newSectionId = 1;//c.getInt(c.getColumnIndex("SECTION_ID"));
		final boolean newObligatory = c.getInt(c.getColumnIndex("MANDATORY")) == 1;		
		return new DMPrehliadky(ctx, newId, newSectionId, newText, newObligatory);
	}
	
	@Override
	public int compareTo(DMPrehliadky another) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean obligatory) {
		this.mandatory = obligatory;
	}

	public int getSectionId() {
		return sectionId;
	}

	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
