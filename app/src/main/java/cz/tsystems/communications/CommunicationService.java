package cz.tsystems.communications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import cz.tsystems.base.MyPropertyNameStrategy;
import cz.tsystems.data.DMServiceFree;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.data.SQLiteDBProvider;
import cz.tsystems.portablecheckin.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CommunicationService extends IntentService {
	final String TAG = CommunicationService.class.getSimpleName();

	enum eDone {
		eDBDATA((byte) 1), eSILUETMIME((byte) 2), eBANERSMIME((byte) 4);
		private byte value;

		private eDone(byte val) {
			value = val;
		}

		public byte getValue() {
			return value;
		}
	};

	byte loadDataDone = 0;
	private NotificationManager mNM;
	// private static final int MY_NOTIFICATION_ID=1;
	private int incr = 50;
	private JsonNode rootNode;
	private SQLiteDBProvider dbProvider;
	private Notification myNotification;
	final ObjectMapper mapper = new ObjectMapper();
	PortableCheckin app;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int MY_NOTIFICATION_ID = R.string.serviceNotifier;

	// private String URL = "http://dms.t-systems.cz:8090/";

	// /pchi/ConnectionTest";
	private String URL;// = "http://192.168.100.10:8091/";

	// private String URL = "http://10.219.61.87:8080/"; //miro

	public CommunicationService() {
		super("CommunicationService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		app = (PortableCheckin) getApplicationContext();
		URL = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE)
				.getString("serviceURI", "");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("SERVICE", intent.getStringExtra("ACTION"));
		if (intent.getStringExtra("ACTION").equalsIgnoreCase("GetSilhouette")
				|| intent.getStringExtra("ACTION").equalsIgnoreCase(
						"GetBanners"))
			sendGetMime(intent.getExtras());
        else if(intent.getStringExtra("ACTION").equalsIgnoreCase("SaveCheckin"))
            sendPostJson(intent.getExtras());
//            getSaveCheckinData();
		else
			sendGetJson(intent.getExtras());
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(MY_NOTIFICATION_ID);
		super.onDestroy();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification(Bundle data) {
		Context context = getApplicationContext();

		Intent intent = new Intent(context, NotificationReceiverActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent
				.getActivity(context, 0, intent, 0);

		// generate notification
		myNotification = new NotificationCompat.Builder(getApplicationContext())
				.setContentTitle("Progress")
				.setContentText(data.getString("ACTION"))
				.setContentInfo("content info")
				.setTicker("Notification!")
				.setWhen(System.currentTimeMillis())
				// .setDefaults(Notification.DEFAULT_SOUND)
				// .setAutoCancel(true)
				.setSmallIcon(android.R.drawable.ic_notification_overlay)
				.setProgress(100, incr, false)
				// .addAction(android.R.drawable.btn_minus, "-", pIntent)
				.setContentIntent(pIntent).build();

		mNM.notify(MY_NOTIFICATION_ID, myNotification);
	}

	public void sendGetMime(Bundle data) {
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
		HttpResponse response;
		String valSeparator = "?";

		try {
			String theUrl = URL + "/" + data.getString("ACTIONURL");
			Log.d("Message type", data.getString("ACTION"));
			Set<String> keys = data.keySet();

			for (String key : keys) {
				if (key.equalsIgnoreCase("ACTION")
						|| key.equalsIgnoreCase("ACTIONURL"))
					continue;
				theUrl += valSeparator + key + ":" + data.getString(key);
				valSeparator = "&";
			}

			HttpGet get = new HttpGet(theUrl);
			get.addHeader(HTTP.CONTENT_TYPE, "application/json");
			get.addHeader("PCHI-DEVICE-NAME", app.getLocalHostName());
			Log.d("DEVICEID", app.getDeviceID());
			get.addHeader("PCHI-DEVICE-ID", app.getDeviceID());
			get.addHeader(
					"PCHI-DEVICE-VERSION",
					String.valueOf(getSharedPreferences(
							"cz.tsystems.portablecheckin", 0).getInt(
							"GENERATION_VER", -1))
							+ "."
							+ String.valueOf(getSharedPreferences(
									"cz.tsystems.portablecheckin", 0).getInt(
									"MAJOR_VER", -1))
							+ "."
							+ String.valueOf(getSharedPreferences(
									"cz.tsystems.portablecheckin", 0).getInt(
									"COMMUNICATION_VER", -1)));
			get.addHeader("Authorization", "basic " + app.getLogin());
			get.addHeader("accept-language", "cz");
			showNotification(data);
			response = client.execute(get);

			if (response != null) {

				if (response.getStatusLine().getStatusCode() != 200) {
					decodeError(response);
					return;
				}

				parseMIME(data, response);
			}
		} catch (Exception e) {
			sendErrorMsg(e.getLocalizedMessage() );		
			e.printStackTrace();
		}
	}

	public void sendGetJson(Bundle data) {
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
		HttpResponse response;
		String valSeparator = "?";
		StringBuilder total;
		String line;
		
		try {
			String theUrl = URL + "/" + data.getString("ACTIONURL");

			Set<String> keys = data.keySet();
			for (String key : keys) {
                final Object value = data.get(key);
				if (key.equalsIgnoreCase("ACTION")
						|| key.equalsIgnoreCase("ACTIONURL"))
					continue;
				theUrl += valSeparator + key + "="
						+ URLEncoder.encode(value.toString(), "UTF-8");
				valSeparator = "&";
			}

			Log.d("Message type", data.getString("ACTION") + ", " + theUrl);
			HttpGet get = new HttpGet(theUrl);

			get.addHeader(HTTP.CONTENT_TYPE, "application/json");
			get.addHeader("PCHI-DEVICE-NAME", app.getLocalHostName());
			Log.d("DEVICEID", app.getDeviceID());
			get.addHeader("PCHI-DEVICE-ID", app.getDeviceID());
			get.addHeader("Authorization", "basic " + app.getLogin());
			get.addHeader("accept-language", "cz");
			showNotification(data);
            Log.d(TAG, "starting request");
			response = client.execute(get);
            Log.d(TAG, "geting response");

			/* Checking response */
			if (response != null) {

				if (response.getStatusLine().getStatusCode() != 200) {
					decodeError(response);
					return;
				}

                if(data.getString("ACTION").equalsIgnoreCase("GetProtokolImg")
                   || data.getString("ACTION").equalsIgnoreCase("ChiReport")) {
                    decodeByteResponse(data, response);
                }
                else {
                    InputStream in = response.getEntity().getContent();
                    InputStreamReader is = new InputStreamReader(in, "UTF-8");
                    BufferedReader r = new BufferedReader(is);
                    total = new StringBuilder();
                    while ((line = r.readLine()) != null)
                        total.append(line);

                    try {
                        decodeMessage(data, total.toString());
                    } catch (JsonProcessingException e) {
                        sendErrorMsg(e.getLocalizedMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        sendErrorMsg(e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }

				// Get the data in the entity
			}
		}
        catch (HttpHostConnectException e) {
            sendErrorMsg(e.getLocalizedMessage() );
            e.printStackTrace();
            sendErrorMsg(e.getLocalizedMessage());
        }
        catch (Exception e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
            sendErrorMsg(e.getLocalizedMessage());
		}
	}

    public void sendPostJson(Bundle data) {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
        HttpResponse response;
        String valSeparator = "?";
        StringBuilder total;
        String line;

        try {
            String theUrl = URL + "/" + data.getString("ACTIONURL");

            Set<String> keys = data.keySet();
            for (String key : keys) {
                final Object value = data.get(key);
                if (key.equalsIgnoreCase("ACTION")
                        || key.equalsIgnoreCase("ACTIONURL"))
                    continue;
                theUrl += valSeparator + key + "="
                        + URLEncoder.encode(value.toString(), "UTF-8");
                valSeparator = "&";
            }

            Log.d("Message type", data.getString("ACTION") + ", " + theUrl);
            HttpPost post = new HttpPost(theUrl);

            post.addHeader(HTTP.CONTENT_TYPE, "application/json");
            post.addHeader("PCHI-DEVICE-NAME", app.getLocalHostName());
            Log.d("DEVICEID", app.getDeviceID());
            post.addHeader("PCHI-DEVICE-ID", app.getDeviceID());
            post.addHeader("Authorization", "basic " + app.getLogin());
            post.addHeader("accept-language", "cz");

            if(data.getString("ACTION").equals("SaveCheckin")) {
                JSONObject jsonObject = getSaveCheckinData();
                StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                post.setEntity(entity);
            }

            showNotification(data);
            response = client.execute(post);

			/* Checking response */
            if (response != null) {

                if (response.getStatusLine().getStatusCode() != 200) {
                    decodeError(response);
                    return;
                }

                InputStream in = response.getEntity().getContent();
                InputStreamReader is = new InputStreamReader(in, "UTF-8");
                BufferedReader r = new BufferedReader(is);
                total = new StringBuilder();
                while ((line = r.readLine()) != null)
                    total.append(line);

                try {
                    decodeMessage(data, total.toString());
                } catch (JsonProcessingException e) {
                    sendErrorMsg(e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    sendErrorMsg(e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            sendErrorMsg(e.getLocalizedMessage() );
            e.printStackTrace();
        }
    }
	
	private void sendErrorMsg(String msg)
	{
		Intent i = new Intent("recivedData");
		i.putExtra("ErrorMsg", msg );
        sendBroadcast(i);
//		LocalBroadcastManager.getInstance(this).sendBroadcast(i);
	}

	private void decodeError(HttpResponse response) {
		sendErrorMsg(response.toString() );
	}

    private void decodeByteResponse(Bundle data, HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null)
            return;

        Intent i = new Intent("recivedData");
        File tempFile = null;
        if (data.getString("ACTION").equalsIgnoreCase("GetProtokolImg")) {
            tempFile = File.createTempFile("protokol", "png");
        } else if (data.getString("ACTION").equalsIgnoreCase("ChiReport")) {
            tempFile = File.createTempFile("protokol", "pdf");
            Log.v(TAG, String.format("ChiReport DONE :%d", loadDataDone));
        }
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        entity.writeTo(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();

        i.putExtra("pdfFileName", tempFile.getAbsoluteFile().toString());
        i.putExtra("requestData", data);
        i.putExtra("loadDataDone", loadDataDone);
        sendBroadcast(i);
    }

	private void decodeMessage(Bundle data, String response)
			throws JsonProcessingException, IOException {
		Intent i = new Intent("recivedData");
		if (data.getString("ACTION").equalsIgnoreCase("Login"))
			decodeLogin(response);
		else if (data.getString("ACTION").equalsIgnoreCase("GetStaticData")) {
			decodeStaticData(response);
			Log.v(TAG, "DB Data DONE");
			loadDataDone |= eDone.eDBDATA.getValue();
		} else if (data.getString("ACTION")
				.equalsIgnoreCase("CheckinOrderList"))
			app.setPlanZakazk(mapper.readTree(response));
		else if (data.getString("ACTION").equalsIgnoreCase("DataForCheckIn")
                && response.length()>0) {
            JsonNode root = mapper.readTree(response);
            PortableCheckin.deletePackets();
            app.setVozInfo(root.path("CUSTOMER_VEHICLE_INFO"));
            app.setZakInfo(root.path("BUSINESS_PARTNER_INFO"));
            app.setVozHistory(root.path("VEHICLE_HISTORY"));
            app.setPlannedActivitiesList(root.path("PLANNED_ORDER").path("ACTIVITIES"));
            app.setOdlozenePolozky(root.path("DEFERRED_SERVICE_DEMANDS"));
            app.setSDA(root.path("RECALLS"));
            app.setCheckin(root.path("CHECKIN"));
            app.setSelectedScenar(app.checkin.check_scenario_id);
            app.loadSilhouette();
            app.getSilhouette().setPointsFromJson(root.path("DAMAGE_POINTS"));
            app.setVybavaList(root.path("EQUIPMENT"));
            app.addFreeVybavaList(root.path("EQUIPMENT_FREE"));
            app.setServiceList(root.path("SERVICE"));
            app.addFreeServiceList(root.path("SERVICE_FREE"));
            app.setUnits(root.path("UNIT"));
            app.setCheckedPackets(root.path("WORKSHOP_PACKET"));
            app.setOffers(root.path("OFFER"));

            int readedLength = 0;
            while(readedLength < response.length()) {
                final int lengthToRead = (response.length()-readedLength > 3500)?3500:response.length()-readedLength;
                final String substring = response.substring(readedLength, readedLength+lengthToRead);
                Log.i(TAG, String.format("CheckinData: %s", substring));
                readedLength += substring.length();
            }

        } else if (data.getString("ACTION").equalsIgnoreCase("WorkshopPackets")) {
Log.v(TAG, response);
            JsonNode root = mapper.readTree(response);
            app.setPackets(root.path("WORKSHOP_PACKET_DMS"));
		} else if (data.getString("ACTION").equalsIgnoreCase("GetSilhouette")) {
			loadDataDone |= eDone.eSILUETMIME.getValue();
			Log.v(TAG, String.format("Silhouette DONE :%d", loadDataDone));
		} else if (data.getString("ACTION").equalsIgnoreCase("GetBanners")) {
			loadDataDone |= eDone.eBANERSMIME.getValue();
			Log.v(TAG, String.format("Banners DONE :%d", loadDataDone));
		} else if (data.getString("ACTION").equalsIgnoreCase("SaveCheckin")) {
            i.putExtra("recivedData", data);
            JsonNode result = mapper.readTree(response).path("RESULT");
            boolean dms_save_status = result.path("SAVE_DMS_STATUS").booleanValue();
            boolean save_status = result.path("SAVE_STATUS").booleanValue();
            app.getCheckin().checkin_id = result.path("CHECKIN_ID").intValue();
            app.getCheckin().checkin_number = result.path("CHECKIN_NUMBER").intValue();
        }

		i.putExtra("requestData", data);
		i.putExtra("loadDataDone", loadDataDone);
        sendBroadcast(i);
//		LocalBroadcastManager.getInstance(this).sendBroadcast(i);
	}

	private void decodeStaticData(String response)
			throws JsonProcessingException, IOException {

		dbProvider = app.getTheDBProvider();
		rootNode = mapper.readTree(response);

		doInserTable("BRAND");
		doInserTable("BRAND_CARBODYCODE");
		doInserTable("BRAND_CARBODYTYPE");
		doInserTable("BRAND_LOC");
		doInserTable("BRAND_SILHOUETTE");
		doInserTable("CAR_EQUIPMENT");
		doInserTable("CAR_EQUIPMENT_LOC");
		doInserTable("CARBODYTYPE");
		doInserTable("CUSTOMER_CATEGORY");
		doInserTable("CUSTOMER_CATEGORY_LOC");
		doInserTable("FUEL");
		doInserTable("FUEL_LOC");
		doInserTable("HISTORY_TYPE");
		doInserTable("HISTORY_TYPE_LOC");
		doInserTable("CHCK_PART");
		doInserTable("CHCK_PART_LOC");
		doInserTable("CHCK_PART_POSITION");
		doInserTable("CHCK_PART_POSITION_STATUS");
		doInserTable("CHCK_POSITION");
		doInserTable("CHCK_POSITION_LOC");
		doInserTable("CHCK_REQUIRED");
		doInserTable("CHCK_REQUIRED_LOC");
		doInserTable("CHCK_STATUS");
		doInserTable("CHCK_STATUS_LOC");
		doInserTable("CHCK_UNIT");
		doInserTable("CHCK_UNIT_LOC");
		doInserTable("CHECK_OFFER");
		doInserTable("CHECK_OFFER_LOC");
		doInserTable("CHECK_SCENARIO");
		doInserTable("CHECK_SCENARIO_LOC");
		doInserTable("CHECK_SCENARIO_UNIT");
		doInserTable("CHECK_SERVICE");
		doInserTable("CHECK_SERVICE_LOC");
//		doInserTable("OBD_DTC");
//		doInserTable("OBD_DTC_LOC");
//		doInserTable("OBD_FUEL");
//		doInserTable("OBD_FUEL_LOC");
		doInserTable("SILHOUETTE");
		doInserTable("SILHOUETTE_IMAGE");
		doInserTable("SILHOUETTE_TYPE");
		doInserTable("SPARE_PART_DISPON");
		doInserTable("SPARE_PART_DISPON_LOC");
		doInserTable("SYS_CONFIG");
		doInserTable("TYPE_CODE");
		doInserTable("WORKSHOP_PACKET_BRIDGE");
	}

	private void doInserTable(final String tableName) {
		dbProvider.insertTableData(tableName, rootNode.path(tableName)
				.elements());
	}

	private void decodeLogin(String data) {
		try {
			rootNode = mapper.readTree(data);
			final JsonNode userNode = rootNode.path("USER");
			final JsonNode configNode = rootNode.path("CONFIGURATION");

			// JSONObject jObj = new JSONObject(data);
			app.setLogetUser(userNode);
			app.setSetting(configNode);
			Log.d("JSONUSER", userNode.get("PERSONAL_ID").textValue());// getJSONObject("USER").getString("PERSONAL_ID"));
		} catch (JsonProcessingException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		} catch (IOException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		}
	}

	void writeToFile(final String fileName, JsonNode jsonResponse) {
		File filePath = Environment.getExternalStorageDirectory();
		File file = new File(filePath, fileName);
		if (file.exists())
			file.delete();
		try {
			PortableCheckin.defaultMapper().writeValue(file, jsonResponse);
		} catch (JsonGenerationException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		} catch (JsonMappingException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		} catch (IOException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		}
	}

	void parseMIME(Bundle data, HttpResponse response)
			throws JsonProcessingException, IOException {
		try {
			final boolean isSilueta = data.getString("ACTION")
					.equalsIgnoreCase("GetSilhouette");
			final String dataFileName = "downloadMimeCache.dat";
			File filePath = getCacheDir();
			File file = new File(filePath, dataFileName);
			if (file.exists())
				file.delete();
			FileOutputStream out;
			out = new FileOutputStream(file);
			response.getEntity().writeTo(out);
			out.flush();
			out.close();

			FileInputStream fin = new FileInputStream(file);
			ByteArrayDataSource ds = new ByteArrayDataSource(fin,
					"multipart/mixed");
			MimeMultipart multipart = new MimeMultipart(ds);

			final int partsCount = multipart.getCount();
			Log.v(TAG, "partsCount :" + String.valueOf(partsCount));
			Log.v(TAG, "contentType :" + multipart.getContentType());

			if (isSilueta)
				parseSilhouettes(multipart, app.getTheDBProvider());
			else
				parseBanners(multipart, app.getTheDBProvider());

			fin.close();
			if (file.exists())
				file.delete();

		} catch (IllegalStateException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		} catch (IOException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		} catch (MessagingException e) {
			sendErrorMsg(e.getLocalizedMessage() );
			e.printStackTrace();
		}

		decodeMessage(data, "");
	}

	final public void parseSilhouettes(final MimeMultipart multipart,
			SQLiteDBProvider db) throws NumberFormatException,
			MessagingException, IOException {
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			final String silIds = bodyPart.getHeader("SiluetaID")[0];
			final String silTypIds = bodyPart.getHeader("SiluetaTypID")[0];
			byte buffer[] = new byte[bodyPart.getSize()];
			bodyPart.getInputStream().read(buffer);
			app.getTheDBProvider().insertSilueta(Integer.parseInt(silIds),
					Integer.parseInt(silTypIds), buffer);
		}
	}

	public void parseBanners(final MimeMultipart multipart, SQLiteDBProvider db)
			throws NumberFormatException, MessagingException, IOException {
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			final String BannerIds = bodyPart.getHeader("CHECK_OFFER_ID")[0];
			byte buffer[] = new byte[bodyPart.getSize()];
			bodyPart.getInputStream().read(buffer);
			app.getTheDBProvider().insertBaner(Integer.parseInt(BannerIds),
					buffer);
		}
	}

	private void saveBytes(BodyPart imagePart) throws MessagingException,
			IOException {
		final String imgFileName = imagePart.getFileName();
		File filePath = Environment
				.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES);
		File file = new File(filePath, imgFileName);
		if (file.exists())
			file.delete();
		FileOutputStream out = new FileOutputStream(file);
		Bitmap img = BitmapFactory.decodeStream(imagePart.getInputStream());
		img.compress(Bitmap.CompressFormat.PNG, 90, out);

	}

    private JSONObject getSaveCheckinData() {
        JSONObject jsonObject = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new MyPropertyNameStrategy());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        app.getCheckin().personal_id = app.user.personal_id;
        try {
//            System.out.println(mapper.writeValueAsString(app.getCheckin())); 1948, 4587
            JSONArray jsonArray;
            jsonArray = new JSONArray(mapper.writeValueAsString(app.getCheckedPackets()));
            jsonObject.put("CHECKIN_WORKSHOP_PACKET", jsonArray );
            jsonArray = new JSONArray(mapper.writeValueAsString(PortableCheckin.getAllUnitList()));
            jsonObject.put("CHECKIN_UNIT", jsonArray);
            jsonArray = new JSONArray(mapper.writeValueAsString(app.getFreeService()));
            jsonObject.put("CHECKIN_SERVICE_FREE", jsonArray);
            jsonArray = new JSONArray(mapper.writeValueAsString(app.getStaticService()));
            jsonObject.put("CHECKIN_SERVICE", jsonArray);
            jsonArray = new JSONArray(mapper.writeValueAsString(app.getOffers()));
            jsonObject.put("CHECKIN_OFFER", jsonArray);
            jsonArray = new JSONArray(mapper.writeValueAsString(app.getFreeVybava()));
            jsonObject.put("CHECKIN_EQUIPMENT_FREE", jsonArray);
            jsonArray = new JSONArray(mapper.writeValueAsString(app.getStaticVybava()));
            jsonObject.put("CHECKIN_EQUIPMENT", jsonArray);
            jsonArray = new JSONArray(mapper.writeValueAsString(app.getSilhouette().getAllPointsTo1024()));
            jsonObject.put("CHECKIN_DAMAGE_POINT", jsonArray);
            jsonObject.put("CHECKIN", new JSONObject(mapper.writeValueAsString(app.getCheckin())));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}
