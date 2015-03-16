package cz.tsystems.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.base.LowerCaseNamingStrategy;
import cz.tsystems.communications.CommunicationService;
import cz.tsystems.model.PrehliadkyModel;
import cz.tsystems.model.UnitsModel;
import cz.tsystems.portablecheckin.R;
import cz.tsystems.portablecheckin.ServiceActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class PortableCheckin extends Application {
		
	final String TAG = PortableCheckin.class.getSimpleName();	
	private ProgressDialog pPrograssDialog;

    public static enum DialogType {
        SINGLE_BUTTON
    }
    public static class Size {
        public static double height;
        public static double width;
        public static double heightTo1024, widthTo1024, heightFrom1024, widthFrom1024;;

        public static void setHeight(double height) {
            Size.height = height;
            Size.heightTo1024 = 1024.0/height;
            Size.heightFrom1024 = height/1024.0;
        }

        public static void setWidth(double width) {
            Size.width = width;
            Size.widthTo1024 = 1024.0/width;
            Size.widthFrom1024 = width/1024.0;
        }
    }

	public static DMScenar selectedScenar;
	public DMBrand selectedBrand;
	public static List<DMOffers> offers;
    public static List<DMPacket> packets;
    public static List<DMPrehliadkyMaster> prehliadkyMasters;
	public static List<List<DMUnit>> unitList;
	static public List<DMVybava> vybavaList = new ArrayList<DMVybava>();
    static public List<DMService> serviceList = new ArrayList<DMService>();
	static public DMSilhouette selectedSilhouette;
	private String login;
	private String deviceID;
	private String localHostName;
	private SQLiteDBProvider theDBProvider;
	
	public static final int REQUEST_TAKE_PHOTO = 1;
	public List<DMPlannedOrder> plannedOrderList;
    static public List<DMPlannedActivities>plannedActivitiesList;
    static public List<DMOdlozenePolozky>odlozenePolozky;
    static public List<DMSDA>sda;
	static public DMCheckin checkin;
	static public DMUser user;
	static public DMSetting setting;
    static public Size silhuetteSize = new Size();
	
	static public TypeReference<List<DMCustomerInfo>> customerInfoTypeRef = new TypeReference<List<DMCustomerInfo>>(){};
	static public List<DMCustomerInfo> custumerInfoList;
	static public TypeReference<List<DMVehicleInfo>> vehicleInfoTypeRef = new TypeReference<List<DMVehicleInfo>>(){};	
	static public List<DMVehicleInfo> vehicleInfoList;
	static public TypeReference<List<DMVehicleHistory>> vehicleHistoryTypeRef = new TypeReference<List<DMVehicleHistory>>(){};	
	static public List<DMVehicleHistory> vehicleHistoryList;
    private Activity actualActivity;
	
	

	@Override
	public void onCreate() {
		super.onCreate();
		setTheDBProvider();

        SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);
        SharedPreferences.Editor spe= sp.edit();
        if(!sp.contains("PacketDetail"))
            spe.putBoolean("PacketDetail", false);
        if(!sp.contains("TimeOut"))
            spe.putInt("TimeOut", 5000);
        if(!sp.contains("PhotoResolution"))
            spe.putInt("PhotoResolution", 80);
        spe.commit();
	}
 
	@Override
	public void onLowMemory() {
		Log.v(TAG, "onLowMemory()");
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		Log.v(TAG, "onTerminate()");
		super.onTerminate();
	}
	
	
	// re-use a single ObjectMapper so we're not creating multiple object mappers
	private static ObjectMapper sObjectMapper = new ObjectMapper().setPropertyNamingStrategy(new LowerCaseNamingStrategy());
	public static ObjectMapper defaultMapper() {
	    return sObjectMapper;
	}
	
	// this helper method can be used to make JSON parsing a one-line operation
	public static <T> T parseJson(JsonNode node, Class<T> mClass) {
	    try {
	        return defaultMapper().treeToValue(node, mClass);
	    } catch (IOException e) {
	        Log.e("JSON SERIALIZER", "Failed to parse JSON entity " + mClass.getSimpleName(), e);
	        throw new RuntimeException(e);
	    }
	}
	
	public static <T> List<T> parseJsonArray(JsonNode node, Class<T> mClass) {
	    try {
	    	JavaType type = defaultMapper().getTypeFactory().constructCollectionType(List.class, mClass);
	        return defaultMapper().readValue(node.traverse(), type);
	    } catch (IOException e) {
	        Log.e("JSON SERIALIZER", "Failed to parse JSON array entity ", e);
	        throw new RuntimeException(e);
	    }
	}	
	
	public void setLogetUser(JsonNode logetUser) {
		PortableCheckin.user = PortableCheckin.parseJson(logetUser, DMUser.class);		
	}
	public void setSetting(JsonNode setting) {
		PortableCheckin.setting = PortableCheckin.parseJson(setting, DMSetting.class);	
	}	
	public void setCheckin(JsonNode checkinData) {
		this.checkin = PortableCheckin.parseJson(checkinData, DMCheckin.class);	
	}
	public DMCheckin getCheckin() {
		if(this.checkin == null)
			loadDefaultCheckin();
		return this.checkin;
	}
	public void setVozInfo(JsonNode vozidloInfo) {
        if(!vozidloInfo.isMissingNode())
	    	PortableCheckin.vehicleInfoList = parseJsonArray(vozidloInfo, DMVehicleInfo.class);
        else
            PortableCheckin.vehicleInfoList = null;
    }
	public void setZakInfo(JsonNode zakInfo) {
        if(!zakInfo.isMissingNode())
		    PortableCheckin.custumerInfoList = parseJsonArray(zakInfo, DMCustomerInfo.class);
        else
            PortableCheckin.custumerInfoList = null;
    }
	public void setVozHistory(JsonNode vozHistory) {
        if(!vozHistory.isMissingNode())
		    PortableCheckin.vehicleHistoryList = parseJsonArray(vozHistory, DMVehicleHistory.class);
        else
            PortableCheckin.vehicleHistoryList = null;
	}

    public void setPlannedActivitiesList(JsonNode plannedActivities) {
        if(!plannedActivities.isMissingNode())
            PortableCheckin.plannedActivitiesList = parseJsonArray(plannedActivities, DMPlannedActivities.class);
        else
            PortableCheckin.plannedActivitiesList = null;
    }
    public void setOdlozenePolozky(JsonNode node) {
        if(!node.isMissingNode())
            PortableCheckin.odlozenePolozky = parseJsonArray(node, DMOdlozenePolozky.class);
        else
            PortableCheckin.odlozenePolozky = null;
    }
    public void setSDA(JsonNode node) {
        if(!node.isMissingNode())
            PortableCheckin.sda = parseJsonArray(node, DMSDA.class);
        else
            PortableCheckin.sda = null;
    }
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}	
	
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getLocalHostName() {
		return localHostName;
	}
	public void setLocalHostName(String localHostName) {
		this.localHostName = localHostName;
	}

	public SQLiteDBProvider getTheDBProvider() {
		return theDBProvider;
	}
	public void setTheDBProvider() {		
		this.theDBProvider = new SQLiteDBProvider(this);
		
		try {
			this.theDBProvider.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		try {
			this.theDBProvider.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}		
	}
	
	public List<DMPlannedOrder> getPlanZakazk() {
		return this.plannedOrderList;
	}
	public void setPlanZakazk(JsonNode planZakazk) {
		this.plannedOrderList = parseJsonArray(planZakazk.get("CHECKIN_ORDER_LIST"), DMPlannedOrder.class);
        Collections.sort(this.plannedOrderList, new DMPlannedOrder());
    }
	public void loadDefaultCheckin() {
		if(user == null)
			return;
		final String brandId = user.brand_id;
		checkin = new DMCheckin();
        checkin.checkin_id = null;
		checkin.brand_id = brandId;
        selectedBrand = this.getBrand(brandId);
        selectedScenar = this.getScenarForId(selectedBrand.check_scenario_id_def);
        selectedSilhouette = null;
        serviceList.clear();
        checkin.check_scenario_id = selectedScenar.check_scenario_id;
        checkin.fuel_id = 1;
        plannedActivitiesList = null;
        odlozenePolozky = null;
        sda = null;
        setSelectedBrand(brandId);
//        setSelectedScenar(checkin.check_scenario_id);
		setOffers(this.getOffers(brandId));
		loadSilhouette();
        loadUnits();
        deletePackets();
	}
	

	public DMScenar getSelectedScenar() {
		return selectedScenar;
	}
	public void setSelectedScenar(DMScenar newSelectedScenar) {
		this.selectedScenar = newSelectedScenar;
	}
	public void setSelectedScenar(Integer scenarId) {
        if(scenarId == null) {
            checkin.check_scenario_id = 1;
            scenarId =  1;
        }
		this.selectedScenar = this.getScenarForId(scenarId);
        //TODO vymaz stare volne vybavy a nacitaj nove pre dany scenar
	}

	public DMBrand getSelectedBrand() {
		return selectedBrand;
	}
	public void setSelectedBrand(DMBrand selectedBrand) {
		this.selectedBrand = selectedBrand;
		checkin.brand_id = selectedBrand.brand_id;
		loadVybavy();
	}
	
	public void setSelectedBrand(final String brandId) {
		this.selectedBrand = this.getBrand(brandId);
		checkin.brand_id = brandId;
        checkin.silhouette_id = selectedBrand.silhouette_id;
        loadSilhouette();
		loadVybavy();
        loadServices();
        setOffers(this.getOffers(brandId));
	}	
	
	public Cursor getPaliva() {
		return theDBProvider.executeQuery("SELECT F.ROWID AS _id, F.FUEL_ID, ifnull(FL.FUEL_TXT_LOC, F.FUEL_TXT_DEF) AS TEXT " + " FROM  FUEL F LEFT OUTER JOIN FUEL_LOC FL ON F.FUEL_ID = FL.FUEL_ID "
				+ " AND FL.LANG_ENUM = ?", new String[] { Locale.getDefault().getLanguage() });
	}
	
	public DMScenar getScenarForId(final long scenarId) {
		final String locale = Locale.getDefault().getLanguage();
		final Cursor cursor = theDBProvider.executeQuery(
				"SELECT DATA.ROWID AS _id, DATA.* FROM ( SELECT CS.*, ifnull(CSL.CHECK_SCENARIO_TXT_LOC, CS.CHECK_SCENARIO_TXT_DEF) AS TEXT "
						+ "FROM CHECK_SCENARIO CS LEFT OUTER JOIN CHECK_SCENARIO_LOC CSL ON CS.CHECK_SCENARIO_ID = CSL.CHECK_SCENARIO_ID AND CSL.LANG_ENUM = ?) AS DATA "
						+ "WHERE DATA.CHECK_SCENARIO_ID = ? ", new String[] { locale, String.valueOf(scenarId) });
		if(cursor.moveToFirst())
			return new DMScenar(cursor);
		else
			return null;
	}
	
	public Cursor getScenare(final String brandId) {
		return theDBProvider.executeQuery("SELECT ROWID AS _id, * FROM ( SELECT CS.CHECK_SCENARIO_ID, CS.BRAND_ID, CS.SHOW_SCENARIO, "
				+ "ifnull(CSL.CHECK_SCENARIO_TXT_LOC, CS.CHECK_SCENARIO_TXT_DEF) AS TEXT "
				+ "FROM CHECK_SCENARIO CS LEFT OUTER JOIN CHECK_SCENARIO_LOC CSL ON CS.CHECK_SCENARIO_ID = CSL.CHECK_SCENARIO_ID " + "AND CSL.LANG_ENUM = ?) "
				+ "WHERE ifnull(BRAND_ID, ?) = ? ",
				new String[] { Locale.getDefault().getLanguage(), brandId, brandId });		
	}

    public Bitmap getOffersBanner(final int offerId) {
        Cursor cursor =  theDBProvider.executeQuery("SELECT ADVERT_BANNER " +
                "FROM CHECK_OFFER " +
                "WHERE CHECK_OFFER_ID = ?", new String[] { String.valueOf(offerId)});

        cursor.moveToFirst();
        if(cursor.getCount() >0)
            return BitmapFactory.decodeStream(new ByteArrayInputStream(cursor.getBlob(cursor.getColumnIndex("ADVERT_BANNER")))) ;
        else
            return null;
    }
	
	public List<DMOffers> getOffers(final String brandId) {
		List<DMOffers> offersList = new ArrayList<DMOffers>();
		Cursor cursor =  theDBProvider.executeQuery("SELECT * FROM " +
                "(SELECT CO.ROWID AS _id, CO.BRAND_ID, CO.CHECK_OFFER_ID, CO.SHOW_OFFER, " +
				"ifnull(COL.CHECK_OFFER_TXT_LOC, CO.CHECK_OFFER_TXT_DEF) AS TEXT, " +
                "strftime('%%Y-%%m-%%dT%%H:%%M:%%S', DATETIME(CO.INSERTED, 'unixepoch')) AS INSERTED_UTC, " +
				"strftime('%%Y-%%m-%%dT%%H:%%M:%%S', DATETIME(CO.LAST_UPDATED, 'unixepoch')) AS LAST_UPDATED_UTC, CO.SELL_PRICE, CO.SHOW_TXT, " +
				"strftime('%%Y-%%m-%%dT%%H:%%M:%%S', DATETIME(CO.VALID_FROM, 'unixepoch')) AS VALID_FROM_UTC, " +
				"strftime('%%Y-%%m-%%dT%%H:%%M:%%S', DATETIME(CO.VALID_UNTIL, 'unixepoch')) AS VALID_UNTIL_UTC " +
				"FROM CHECK_OFFER CO LEFT OUTER JOIN CHECK_OFFER_LOC COL ON COL.CHECK_OFFER_ID = CO.CHECK_OFFER_ID " +
				"AND CO.VALID_FROM < datetime('NOW') AND CO.VALID_UNTIL > datetime('NOW') AND COL.LANG_ENUM = ? )" +
                " WHERE ifnull(BRAND_ID, ?) = ? AND SHOW_OFFER = 'true'", new String[] { Locale.getDefault().getLanguage(), brandId, brandId });

		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			offersList.add(new DMOffers(cursor));
			cursor.moveToNext();
		}
		return offersList;		
	}
	
	public Cursor getBrands() {
		Cursor cursor = theDBProvider.executeQuery(
				"SELECT B.ROWID AS _id, B.BRAND_ID, ifnull(BL.BRAND_TXT_LOC, B.BRAND_TXT_DEF) AS BRAND_TXT "
				+ " FROM BRAND B LEFT OUTER JOIN BRAND_LOC BL ON BL.BRAND_ID = B.BRAND_ID AND BL.LANG_ENUM = ?", 
				new String[] { Locale.getDefault().getLanguage()});
		return cursor;
	}
	
	public DMBrand getBrand(final String brandId) {
		Cursor cursor =  theDBProvider.executeQuery(
				"SELECT * FROM (SELECT B.ROWID AS _id, B.*, ifnull(BL.BRAND_TXT_LOC, B.BRAND_TXT_DEF) AS TEXT "
				+ " FROM BRAND B LEFT OUTER JOIN BRAND_LOC BL ON BL.BRAND_ID = B.BRAND_ID AND BL.LANG_ENUM = ?) WHERE BRAND_ID = ?", 
				new String[] { Locale.getDefault().getLanguage(), brandId });
		
		if(cursor.moveToFirst())		
			return new DMBrand(cursor);
		else
			return null;
	}

/*	public Cursor getSilhouettesForBrand() {
		if(checkin.brand_id == null || checkin.brand_id.length() == 0)
			return null;
		
		final String brand_id = checkin.brand_id;
        final String query = "SELECT S.ROWID AS _id, SI.SILHOUETTE_ID, S.TEXT AS STEXT, SI.SILHOUETTE_TYPE_ID, ST.TEXT AS STTEXT, SI.IMAGE "
                + "FROM SILHOUETTE_IMAGE SI, SILHOUETTE S, SILHOUETTE_TYPE ST "
                + "WHERE S.ID=SI.SILHOUETTE_ID "
                + "AND ST.ID=SI.SILHOUETTE_TYPE_ID "
                + " AND SI.SILHOUETTE_ID IN ("
                + " SELECT SILHOUETTE_ID FROM BRAND_SILHOUETTE WHERE BRAND_ID LIKE ?)";
		
		return theDBProvider.executeQuery(query, new String[] {brand_id});
	}*/

    public Cursor getSilhouetteImagesForBrand() {
        if(checkin.brand_id == null || checkin.brand_id.length() == 0)
            return null;

        final String brand_id = checkin.brand_id;
        final String query = "SELECT ROWID as _id, ID,\n" +
                "( SELECT IMAGE FROM SILHOUETTE_IMAGE SI1 WHERE SI1.SILHOUETTE_ID = ID AND SI1.SILHOUETTE_TYPE_ID = 1) AS img1,\n" +
                "( SELECT IMAGE FROM SILHOUETTE_IMAGE SI2 WHERE SI2.SILHOUETTE_ID = ID AND SI2.SILHOUETTE_TYPE_ID = 2) AS img2,\n" +
                "( SELECT IMAGE FROM SILHOUETTE_IMAGE SI3 WHERE SI3.SILHOUETTE_ID = ID AND SI3.SILHOUETTE_TYPE_ID = 3) AS img3,\n" +
                "( SELECT IMAGE FROM SILHOUETTE_IMAGE SI4 WHERE SI4.SILHOUETTE_ID = ID AND SI4.SILHOUETTE_TYPE_ID = 4) AS img4\n" +
                "FROM SILHOUETTE\n" +
                "WHERE SILHOUETTE.ID IN ( SELECT SILHOUETTE_ID FROM BRAND_SILHOUETTE WHERE BRAND_ID LIKE ?)";

        return theDBProvider.executeQuery(query, new String[] {brand_id});
    }
	
	private Cursor getSilhouetteFromDB(final long silId) {
		final String query = "SELECT S.ROWID AS _id, SI.SILHOUETTE_ID, S.TEXT AS STEXT, SI.SILHOUETTE_TYPE_ID, ST.TEXT AS STTEXT, SI.IMAGE "
				+ "FROM SILHOUETTE_IMAGE SI, SILHOUETTE S, SILHOUETTE_TYPE ST "
				+ "WHERE S.ID=SI.SILHOUETTE_ID "
				+ "AND ST.ID=SI.SILHOUETTE_TYPE_ID "
				+ " AND SI.SILHOUETTE_ID=?";

		Cursor cursor = theDBProvider.executeQuery(query, new String[] {String.valueOf(silId)});
		return cursor;
	}
	
	private Cursor getSilhouetteTypCodes() {
		if(checkin.brand_id == null || checkin.brand_id.length() == 0)
			return null;		
		final String brand_id = checkin.brand_id;
		final String query = "SELECT ROWID AS _id, * FROM( "
            	+ "SELECT TK.VALUE||'.*' AS KOD, TK.SILHOUETTE_ID, '1' AS I "
            	+ "FROM TYPE_CODE TK "
            	+ "WHERE TK.BRAND_ID LIKE ? "
            	+ "UNION "
            	+ "SELECT '..'||VVK.CODE||'.*' AS KOD, VVK.SILHOUETTE_ID, '2' AS I "
            	+ "FROM BRAND_CARBODYCODE VVK "
            	+ "WHERE VVK.BRAND_ID LIKE ? "
            	+ "UNION "
            	+ "SELECT '.*' AS KOD, VV.SILHOUETTE_ID, '3' AS I "
            	+ "FROM BRAND VV "
            	+ "WHERE VV.BRAND_ID LIKE ? "
            	+ "UNION "
            	+ "SELECT '.*' AS KOD, C.SILHOUETTE_ID, '4' AS I "
            	+ "FROM SYS_CONFIG C) DATA "
            	+ "ORDER BY I";//, vyrobekVoz, vyrobekVoz, vyrobekVoz];	
		
		
		return theDBProvider.executeQuery(query, new String[] {brand_id, brand_id, brand_id});
	}
	
	private void loadSilhouetteData(final long silID) {
		Log.v(TAG, "loadSilhouetteData()");
		Cursor cSil = getSilhouetteFromDB(silID);
		cSil.moveToFirst();
		final int iImage = cSil.getColumnIndex("IMAGE");
		final int iSilTyp = cSil.getColumnIndex("SILHOUETTE_TYPE_ID");
		final int iSilId = cSil.getColumnIndex("SILHOUETTE_ID");
		if(selectedSilhouette == null)
			selectedSilhouette = new DMSilhouette();
		while(!cSil.isAfterLast()) {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(cSil.getBlob(iImage));
			Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
			selectedSilhouette.setSilhuetteId(cSil.getInt(iSilId));
			selectedSilhouette.addImage(mBitmap, (short) (cSil.getShort(iSilTyp)-1));
			cSil.moveToNext();
		}
	}
	
	public void loadSilhouette() {
		
		Log.v(TAG, "loadSilhouette()");
        if(selectedSilhouette == null)
		    selectedSilhouette = new DMSilhouette();
		
		if(checkin.silhouette_id > 0) {
			this.loadSilhouetteData(checkin.silhouette_id);
			return;
		}

		final String vozTyp;
		if(checkin.vehicle_type != null)
			vozTyp = checkin.vehicle_type;
		else 
			vozTyp = "";

		Cursor SiluetIds = getSilhouetteTypCodes();
		final int iKod = SiluetIds.getColumnIndex("KOD");
		final int iSiluetaID = SiluetIds.getColumnIndex("SILHOUETTE_ID");
			
		while(!SiluetIds.isAfterLast()) {
			if(SiluetIds.isNull(iSiluetaID)) {
				SiluetIds.moveToNext();
				continue;
			}
			
			if(vozTyp.matches(SiluetIds.getString(iKod))) {
				this.loadSilhouetteData(SiluetIds.getInt(iSiluetaID));
				
				return;
			}
			SiluetIds.moveToNext();
		}

	}
	
	public List<DMOffers> getOffers() {
		return offers;
	}
	public void setOffers(List<DMOffers> offers) {
		this.offers = offers;
	}

    public void setOffers(JsonNode jsonOffers) {
        if(!jsonOffers.isMissingNode()) {
            offers = parseJsonArray(jsonOffers, DMOffers.class);
        }
        else
            offers = this.getOffers(checkin.brand_id);
    }

	public DMSilhouette getSilhouette() {
		if(selectedSilhouette == null) {
			Toast.makeText(getApplicationContext(), "Silhouettes havent bean loaded", Toast.LENGTH_SHORT).show();
			return null;
		}
		return this.selectedSilhouette;
	}

    public void loadUnits() {
//        if(checkin.checkin_id != null && ) {
//            ActionBar.Tab tab = this.getActualActivity().getActionBar().getTabAt(FragmentPagerActivity.eTabService);
//            TextView txtBadge = (TextView) tab.getCustomView().findViewById(R.id.tab_badge);
//            txtBadge.setVisibility(View.INVISIBLE);
//            return;
//        }

        PrehliadkyModel pm = new PrehliadkyModel(this);
        UnitsModel um = new UnitsModel(this);
        if(prehliadkyMasters != null)
            prehliadkyMasters.clear();
        prehliadkyMasters = pm.getPrehliadky();
        setUnitList(um.loadUnits(prehliadkyMasters));

        if(this.getActualActivity() != null) {
            ActionBar.Tab tab = this.getActualActivity().getActionBar().getTabAt(FragmentPagerActivity.eTabService);
            TextView txtBadge = (TextView) tab.getCustomView().findViewById(R.id.tab_badge);
            txtBadge.setText("0/" + String.valueOf(PortableCheckin.selectedScenar.mandatoryCount));
        }
    }

    public void setPrehlaidkyOpened(final boolean opened) {
        for(DMPrehliadkyMaster prehliadka : prehliadkyMasters) {
            prehliadka.opened = opened;
        }
    }

    public DMUnit getUnit(final int unit_id, final int part_position_id) {
        if(this.unitList == null)
            return null;
        List<DMUnit> units = getUnitListByUnitId(unit_id);
        for(DMUnit unit : units)
            if(unit.chck_part_position_id == part_position_id)
                return unit;
        return null;
    }

	public List<DMUnit> getUnitListByUnitId(final long id) {
		if(this.unitList == null)
			this.unitList = new ArrayList<List<DMUnit>>();
		for(List<DMUnit> units : this.unitList)
			if(units.get(0).chck_unit_id == id)
				return units;
		return null;
	}

	public List<DMUnit> getUnitList(final int location) {
		if(this.unitList == null)
			this.unitList = new ArrayList<List<DMUnit>>();
		return unitList.get(location);
	}

    public static List<DMUnit> getAllUnitList() {
        if(unitList == null)
            return null;
        List<DMUnit> units = new ArrayList<>();
        for(List<DMUnit> unit : unitList)
            units.addAll(unit);
        return units;
    }

	public void setUnitList(List<List<DMUnit>> unitList) {
		this.unitList = unitList;
	}
	
	public void addUnit(List<DMUnit> unitList) {
		if(this.unitList == null)
			this.unitList = new ArrayList<List<DMUnit>>();
		this.unitList.add(unitList);
			
	}

    public void setUnits(JsonNode unitNode) {
        if(unitList != null)
            unitList.clear();
        PrehliadkyModel pm = new PrehliadkyModel(this);
        prehliadkyMasters = pm.getPrehliadky();
        if(!unitNode.isMissingNode()) {
            List<DMUnit> units = parseJsonArray(unitNode, DMUnit.class);
            List<DMPrehliadkyMaster> prehliadkyMasters = pm.getPrehliadky();

            for (Iterator<DMPrehliadkyMaster> iterator = prehliadkyMasters.iterator(); iterator.hasNext();) {
                DMPrehliadkyMaster prehliadkyMaster = iterator.next();
                final int unitId = prehliadkyMaster.unitId;
                if (unitId > 0) {
                    unitList.add(new ArrayList<DMUnit>());
                    for (DMUnit unit : units) {
                        if (unit.chck_unit_id == unitId)
                            unitList.get(unitList.size()-1).add(unit);
                    }
                }
            }

        } else {
            UnitsModel um = new UnitsModel(this);
            this.prehliadkyMasters = pm.getPrehliadky();
            setUnitList(um.loadUnits(this.prehliadkyMasters));
        }
    }

    public static void deletePackets(){
        if(packets != null)
            packets.clear();
    }

    public void setCheckedPackets(JsonNode checkedPackets) {
        if(!checkedPackets.isMissingNode())
            checkin.workshop_pakets = parseJsonArray(checkedPackets, String.class);
        else
            checkin.workshop_pakets = null;
    }

    public List<DMPacket> getCheckedPackets() {
        List<DMPacket> checkedPackets = new ArrayList<>();
        if(packets == null)
            return checkedPackets;
        for (DMPacket packet : packets) {
            if(packet.checked)
                checkedPackets.add(packet);
        }

        return checkedPackets;
    }

    public List<DMPacket> getPackets(){
        return packets;
    }

    public void setPackets(JsonNode newPackets) {
        if(!newPackets.isMissingNode()) {
            packets = parseJsonArray(newPackets, DMPacket.class);
            if(checkin.workshop_pakets != null) {
                for (DMPacket paket : packets) {
                    if (checkin.workshop_pakets.contains(paket.workshop_packet_number))
                        paket.checked = true;
                }
            }
            PrehliadkyModel.setPackets(getApplicationContext(), this.prehliadkyMasters);
        }
        else
            packets = null;
    }

    static public List<DMPacket> getPaket(final int groupNr, final String query) {
        boolean noQuery = (query == null || query.length() == 0);
        List<DMPacket> filtered = new ArrayList<DMPacket>();
        for(Iterator<DMPacket> iterator = packets.iterator(); iterator.hasNext();) {
            DMPacket paket = iterator.next();
            String packetDescription = paket.workshop_packet_description;
            if ((noQuery || packetDescription.toLowerCase().contains(query)) && (paket.group_nr == groupNr || groupNr == -1))
                filtered.add(paket);
        }

        return filtered;
    }

    public List<DMPacket> getUnitService(DMUnit unit) {
        final String[] columnsNames = {"_id", "CHCK_REQUIRED_TXT", "CHCK_REQUIRED_ID", "PPS.CHCK_STATUS_ID", "PPS.CHCK_REQUIRED_ID", "PPS.SELL_PRICE"};


        final String query = "SELECT  DATA.ROWID AS _id, DATA.*, PPS.CHCK_STATUS_ID, PPS.CHCK_REQUIRED_ID, PPS.SELL_PRICE "
                            + "FROM "
                            + "(SELECT ifnull(RL.CHCK_REQUIRED_TXT_LOC, R.CHCK_REQUIRED_TXT_DEF) AS CHCK_REQUIRED_TXT, R.CHCK_REQUIRED_ID "
                            + "FROM CHCK_REQUIRED R LEFT OUTER JOIN CHCK_REQUIRED_LOC RL "
                            + "ON R.CHCK_REQUIRED_ID = RL.CHCK_REQUIRED_ID AND RL.LANG_ENUM = ?) DATA, "
                            + "CHCK_PART_POSITION_STATUS PPS, CHCK_PART_POSITION PP "
                            + "WHERE DATA.CHCK_REQUIRED_ID = PPS.CHCK_REQUIRED_ID "
                            + "AND PPS.CHCK_PART_POSITION_ID = PP.CHCK_PART_POSITION_ID "
                            + "AND pp.CHCK_PART_ID = ? "
                            + "AND pp.CHCK_POSITION_ID = ? "
                            + "AND DATA.CHCK_REQUIRED_ID != 20 ";
        Log.v(TAG, query + ", language :" + Locale.getDefault().getLanguage() + ", part :" +  unit.chck_part_id + ", position:" + unit.chck_position_id);

        List<DMPacket> packetList = new ArrayList<DMPacket>();

        try {
            Cursor c = theDBProvider.executeQuery(query, new String[]{Locale.getDefault().getLanguage(), String.valueOf(unit.chck_part_id), String.valueOf(unit.chck_position_id)});
            c.moveToFirst();

            while (!c.isAfterLast()) {
                packetList.add(new DMPacket(c));
                c.moveToNext();
            }
        }catch (SQLiteException e) {
            getDialog(actualActivity, "error", e.getLocalizedMessage(), DialogType.SINGLE_BUTTON ).show();
        }
        return packetList;
    }
	
	public void loadVybavy() {
		final String query = "SELECT ROWID AS _id, * FROM ( SELECT CE.*, CEL.*, ifnull(CEL.CAR_EQUIPMENT_TXT_LOC, CE.CAR_EQUIPMENT_TXT_DEF) AS TEXT "
				+ "FROM CAR_EQUIPMENT CE LEFT OUTER JOIN CAR_EQUIPMENT_LOC CEL ON CE.CAR_EQUIPMENT_ID = CEL.CAR_EQUIPMENT_ID "
				+ "AND CEL.LANG_ENUM = ?) "
				+ "WHERE ifnull(BRAND_ID, ?) = ? ";
		Log.v(TAG, query + ", language :" + Locale.getDefault().getLanguage() + ", brand :" + checkin.brand_id );
        vybavaList.clear();
		Cursor c = theDBProvider.executeQuery(query,new String[] {Locale.getDefault().getLanguage(), checkin.brand_id, checkin.brand_id} );
		c.moveToFirst();
		while(!c.isAfterLast()) {
			Log.v(TAG, String.valueOf(c.getInt(c.getColumnIndex("CAR_EQUIPMENT_ID"))) + " :" + String.valueOf(c.getString(c.getColumnIndex("OBLIGATORY_EQUIPMENT"))));
			vybavaList.add(new DMVybava(c));
			c.moveToNext();
		}

        for(int i=0; i< selectedScenar.equipment_free_count; i++)
            vybavaList.add(new DMVybava(this, null, null, false, true));
	}

    public void loadServices() {
        android.text.format.Time now = new android.text.format.Time();
        now.setToNow();
        final String query = "SELECT ROWID AS _id, * FROM ( SELECT S.*, SL.*, ifnull(SL.CHECK_SERVICE_TXT_LOC, S.CHECK_SERVICE_TXT_DEF) AS TEXT "
                + "FROM CHECK_SERVICE S LEFT OUTER JOIN CHECK_SERVICE_LOC SL ON S.CHECK_SERVICE_ID = SL.CHECK_SERVICE_ID "
                + "AND SL.LANG_ENUM =  ?) "
                + "WHERE ifnull(BRAND_ID, ?) = ? "
                + "AND SHOW_SERVICE = 'true' AND ? BETWEEN VALID_FROM AND VALID_UNTIL ";

        Log.v(TAG, query + ", language :" + Locale.getDefault().getLanguage() + ", BRAND_ID:" + checkin.brand_id + ", now :" + now.format("%Y-%m-%d %H:%M:%S") );
        serviceList.clear();

        Cursor c = theDBProvider.executeQuery(query,new String[] {Locale.getDefault().getLanguage(), checkin.brand_id, checkin.brand_id, now.format("%Y-%m-%d %H:%M:%S")} );
        c.moveToFirst();
        while(!c.isAfterLast()) {

//            Log.v(TAG, String.valueOf(c.getInt(c.getColumnIndex("CAR_EQUIPMENT_ID"))) + " :" + String.valueOf(c.getString(c.getColumnIndex("OBLIGATORY_EQUIPMENT"))));
            serviceList.add(new DMService(c, false));
            c.moveToNext();
        }

        for(int i=0; i< selectedScenar.service_free_count; i++)
            serviceList.add(new DMService(this, null, null, false, true));
    }

	public DMVybava getVybava(final int location) {
        if(vybavaList == null || vybavaList.size() <= location)
            return null;
        return vybavaList.get(location);
    }
    public List<DMVybavaFree> getFreeVybava() {
        List<DMVybavaFree> filtered = new ArrayList<>();

        if(vybavaList == null)
            return new ArrayList<DMVybavaFree>();

        for(Iterator<DMVybava> iterator = vybavaList.iterator(); iterator.hasNext();) {
            final DMVybava vybava = iterator.next();
            if(vybava.editable)
                filtered.add(new DMVybavaFree(vybava));
        }
        return filtered;
    }

    public List<DMVybava> getStaticVybava() {
        List<DMVybava> filtered = new ArrayList<DMVybava>();

        if(vybavaList == null)
            return new ArrayList<DMVybava>();

        for(Iterator<DMVybava> iterator = vybavaList.iterator(); iterator.hasNext();) {
            final DMVybava vybava = iterator.next();
            if(!vybava.editable)
                filtered.add(vybava);
        }
        return filtered;
    }

    public List<DMVybava> getVybavaList(final boolean obligatory) {
        List<DMVybava> filtered = new ArrayList<DMVybava>();

        if(vybavaList == null)
            return new ArrayList<DMVybava>();

        for(Iterator<DMVybava> iterator = vybavaList.iterator(); iterator.hasNext();) {
            final DMVybava vybava = iterator.next();
            if(vybava.obligatory_equipment == obligatory)
                filtered.add(vybava);
        }
        return filtered;
    }

    public void setVybavaList(JsonNode vybavaNode) {
        if(!vybavaNode.isMissingNode())
            PortableCheckin.vybavaList = parseJsonArray(vybavaNode, DMVybava.class);
        else
            loadVybavy();
    }

    public void addFreeVybavaList(JsonNode vybavaNode) {
        if(!vybavaNode.isMissingNode()) {
            List<DMVybava> vybavas = parseJsonArray(vybavaNode, DMVybava.class);
            for(int i=0; i< selectedScenar.service_free_count; i++) {
                if(vybavas.size() > i) {
                    vybavas.get(i).editable = true;
                    vybavas.get(i).setChecked(true);
                } else
                    vybavas.add(i, new DMVybava(this, null, null, false, true));
            }
            PortableCheckin.vybavaList.addAll(vybavas);
        } else {
            for(int i=0; i< selectedScenar.service_free_count; i++)
                PortableCheckin.vybavaList.add(new DMVybava(this, null, null, false, true));
        }

    }

    public void setVybavaList(List<DMVybava> vybavaList) {
        this.vybavaList = vybavaList;
    }

    public void addVybava(DMVybava vybava) {
        if(vybavaList == null)
            vybavaList = new ArrayList<DMVybava>();
        vybavaList.add(vybava);
    }

    public DMService getService(final int location) {
        if(serviceList == null || serviceList.size() <= location)
            return null;
        return serviceList.get(location);
    }

    public List<DMServiceFree> getFreeService() {
        List<DMServiceFree> filtered = new ArrayList<>();
        if(serviceList == null)
            return new ArrayList<DMServiceFree>();
        for(Iterator<DMService> iterator = serviceList.iterator(); iterator.hasNext();) {
            final DMService service = iterator.next();
            if(service.editable == true)
                filtered.add(new DMServiceFree(service));
        }

        return filtered;
    }

    public List<DMService> getStaticService() {
        List<DMService> filtered = new ArrayList<DMService>();
        if(serviceList == null)
            return new ArrayList<DMService>();
        for(Iterator<DMService> iterator = serviceList.iterator(); iterator.hasNext();) {
            final DMService service = iterator.next();
            if(service.editable == false)
                filtered.add(service);
        }
        return filtered;
    }

    public List<DMService> getServiceList() {
        if(serviceList == null)
            serviceList = new ArrayList<DMService>();
        return serviceList;
    }

    public void setServiceList(List<DMService>newServiceList) {
        this.serviceList = newServiceList;
    }

    public void setServiceList(JsonNode serviceNode) {
        if(!serviceNode.isMissingNode())
            PortableCheckin.serviceList = parseJsonArray(serviceNode, DMService.class);
        else
            loadServices();
    }

    public void addFreeServiceList(JsonNode serviceNode) {
        if(!serviceNode.isMissingNode()) {
            List<DMService> services = parseJsonArray(serviceNode, DMService.class);
            for(int i=0; i< selectedScenar.service_free_count; i++) {
                if(services.size() > i) {
                    services.get(i).editable = true;
                    services.get(i).setChecked(true);
                } else
                    services.add(i, new DMService(this, null, null, false, true));
            }
            PortableCheckin.serviceList.addAll(services);
        } else {
            for(int i=0; i< selectedScenar.service_free_count; i++)
                PortableCheckin.serviceList.add(new DMService(this, null, null, false, true));
        }

    }

    public void addService(DMService service) {
        if(serviceList == null)
            serviceList = new ArrayList<DMService>();
        serviceList.add(service);
    }

    public void showProgrssDialog(Context context)
    {
        showProgrssDialog(context, getResources().getString(R.string.Loading_Data));
    }

	public void showProgrssDialog(Context context, final String caption)
	{
		if(pPrograssDialog == null)
			pPrograssDialog = ProgressDialog.show(context, caption, getResources().getString(R.string.Please_wait), true,false);
	}
	
	public void dismisProgressDialog()
	{
		if(pPrograssDialog != null) {
			pPrograssDialog.dismiss();
			pPrograssDialog = null;
		}
	}
	
	public Dialog getDialog(Context context,String title, String message, DialogType typeButtons ) 
	{
		dismisProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
        .setMessage(message)
               .setCancelable(false);

        if (typeButtons == DialogType.SINGLE_BUTTON) {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        //do things
                   }
               });
        }

        AlertDialog alert = builder.create();
        return alert;
    }

    public Activity getActualActivity() {
        return actualActivity;
    }

    public void setActualActivity(Activity actualActivity) {
        this.actualActivity = actualActivity;
    }

    static public void updateDB(Context context)
    {
        Intent msgIntent = new Intent(context, CommunicationService.class);

        msgIntent.putExtra("ACTIONURL", "database/StaticData");
        msgIntent.putExtra("ACTION", "GetStaticData");
        context.startService(msgIntent);

        msgIntent.putExtra("ACTIONURL", "silhouette/SFiles");
        msgIntent.putExtra("ACTION", "GetSilhouette");
        context.startService(msgIntent);
        msgIntent.putExtra("ACTIONURL", "pchi/Banners");
        msgIntent.putExtra("ACTION", "GetBanners");
        context.startService(msgIntent);
    }

    public static boolean isMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void removeVehicle() {
        PortableCheckin.checkin.checkin_number = null;
        PortableCheckin.checkin.checkin_id = null;
        PortableCheckin.checkin.vehicle_id = null;
        PortableCheckin.checkin.vehicle_description = null;
        PortableCheckin.checkin.vehicle_caption = null;
    }
}
