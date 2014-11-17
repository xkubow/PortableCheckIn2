package cz.tsystems.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import cz.tsystems.portablecheckin.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SQLiteDBProvider extends SQLiteOpenHelper   {
	final String TAG = "SQLiteDBProvider";
	private PortableCheckin app;
	static private String DB_NAME = "mchi.db";
	private SQLiteDatabase mchiDataBase;
	
    public SQLiteDBProvider(Context context) {
        super(context, DB_NAME, null, 1);
        this.app = (PortableCheckin)context;
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}    
    
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
    		SQLiteDatabase db = this.getReadableDatabase();
        	
        	if (db.isOpen()){
        		db.close();
        	} 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
    			app.getDialog(app, app.getResources().getString(R.string.Error_title), e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON).show();
        		throw new Error("Error copying database"); 
        	}
    	}
 
    } 
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = this.app.getDatabasePath(DB_NAME).getAbsolutePath();
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
//			PortableCheckin.getDialog(theContext, theContext.getResources().getString(R.string.Error_title), e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON).show();
    		Log.e(TAG, e.getLocalizedMessage());
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	
    	InputStream myInput = app.getAssets().open("databases/mchiBase.db");
 
    	// Path to the just created empty db
    	String outFileName = this.app.getDatabasePath(DB_NAME).getAbsolutePath();
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = this.app.getDatabasePath(DB_NAME).getAbsolutePath();
    	mchiDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
 
    }
    
    public Cursor executeQuery(String query)
    {
    	return this.executeQuery(query, new String [] {});
    }
    
    public ArrayList<HashMap<String, Object>> executeQueryForList(String query, String[] podmin)
    {
    	Cursor data = executeQuery(query, podmin);
    	return getListResult(data);
    }
    
    public Cursor executeQuery(String query, String[] podmin)
    {
    	if(!query.contains("_id"))
    		query = query.replaceAll("^SELECT", "SELECT _ROWID_ AS _id, ");
    	
    	Cursor cur= mchiDataBase.rawQuery(query, podmin);
    	if(cur.getCount() > 0)
    		cur.moveToFirst();
    	return cur;    	    	
    }
    
    public short getDBStrutVersion()
    {
    	Cursor cur = this.executeQuery("SELECT DB_MAJOR_VERSION FROM SYS_CONFIG");
    	if(cur.getCount() == 0 || cur.isNull(1))
    		return -1;
    	return (short) cur.getInt(1);
    }
    
    public short getDBVersion()
    {
    	Cursor cur = this.executeQuery("SELECT DB_MINOR_VERSION FROM SYS_CONFIG");
    	if(cur.getCount() == 0 || cur.isNull(1))
    		return -1;
   		return (short) cur.getInt(1);
    }    
    
    
    public void insertTableData(final String tableName, final Iterator<JsonNode> dataArray)
    {
		try {
			mchiDataBase.beginTransaction();			
			mchiDataBase.delete(tableName, null, null);
			while (dataArray.hasNext()) {
				final JsonNode object = dataArray.next();
				final Iterator<String> iterFieldNames = object.fieldNames();
				ContentValues values = new ContentValues();
				while (iterFieldNames.hasNext()) {
					final String fieldName = iterFieldNames.next();
					if (object.hasNonNull(fieldName))
						values.put(fieldName, object.get(fieldName).asText());
					else
						values.putNull(fieldName);
				}
				mchiDataBase.insert(tableName, null, values);

			}
			mchiDataBase.setTransactionSuccessful();
		} catch (SQLException e) {
			sendErrorMsg(e.getLocalizedMessage());			
			return;
		} finally {
			mchiDataBase.endTransaction();
		}
		Log.v(TAG, "inser done :" + tableName);
    }   
    
    public void insertSilueta(final int siluetaId, final int siluetaTypId, final byte[] theImage)
    {
    	ContentValues cv = new ContentValues();
    	cv.put("IMAGE", theImage);
    	mchiDataBase.update("SILHOUETTE_IMAGE", cv, "SILHOUETTE_ID = "+siluetaId+" AND SILHOUETTE_TYPE_ID = "+siluetaTypId, null);
//    	long id = mchiDataBase.insert("SILHOUETTE_IMAGE", null, cv);
    }
    
    public void insertBaner(final int offerId, final byte[] theImage)
    {
    	ContentValues cv = new ContentValues();
    	cv.put("ADVERT_BANNER", theImage);
    	mchiDataBase.update("CHECK_OFFER", cv, "CHECK_OFFER_ID = "+offerId, null);    	
    }
    
    
    private ArrayList<HashMap<String, Object>> getListResult(Cursor data)
    {
    	HashMap<String, Object> rowData;// = new HashMap<String, Object>();
    	ArrayList<HashMap<String, Object>> mArrayList = new ArrayList<HashMap<String, Object>>();
    	for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) 
    	{
    		rowData = new HashMap<String, Object>();
    		for(int i=0; i<data.getColumnCount(); i++)
    			rowData.put(data.getColumnName(i), getCorectType(data, i));
    		mArrayList.add(rowData);
    	}
    	return mArrayList;
    }
    
    private Object getCorectType(final Cursor columnData, final int columnIndex)
    {
    	switch(columnData.getType(columnIndex))
    	{
    		case Cursor.FIELD_TYPE_INTEGER:
    			return columnData.getInt(columnIndex);
    		case Cursor.FIELD_TYPE_FLOAT:
    			return columnData.getFloat(columnIndex);
    		case Cursor.FIELD_TYPE_NULL:
    			return null;
    		case Cursor.FIELD_TYPE_STRING:
    			return columnData.getString(columnIndex);
    		case Cursor.FIELD_TYPE_BLOB:
    			return columnData.getBlob(columnIndex);
    	}
    	
    	return null;
    }
    
	private void sendErrorMsg(String msg)
	{
		Intent i = new Intent("recivedData");
		i.putExtra("ErrorMsg", msg );
		LocalBroadcastManager.getInstance(app).sendBroadcast(i);
	}    
    
}


