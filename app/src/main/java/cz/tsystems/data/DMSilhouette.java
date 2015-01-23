package cz.tsystems.data;

import java.util.ArrayList;


import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

public class DMSilhouette extends Object {
    final String TAG = DMSilhouette.class.getSimpleName();
	public class SilhouetteImage {

        public Bitmap image;// = new ArrayList<Bitmap>(5);

        public List<DMDamagePoint> points = new ArrayList<>();
        public List<String> photoFileName = new ArrayList<String>();
	}

	private int silhuetteId;
	private SilhouetteImage[] silImageData = new DMSilhouette.SilhouetteImage[5];
	
	public DMSilhouette() {
		for(int i =0; i< silImageData.length; i++)
			silImageData[i] = new SilhouetteImage();
	}

	public Bitmap getImage(final short location) {
		return silImageData[location].image;
	}

	public void addImage(Bitmap image, final short location) {
		this.silImageData[location].image = image;
	}

    public List<DMDamagePoint> getAllPointsTo1024() {
        List<DMDamagePoint> thePoints = new ArrayList<>();

        thePoints.addAll(silImageData[0].points);
        thePoints.addAll(silImageData[1].points);
        thePoints.addAll(silImageData[2].points);
        thePoints.addAll(silImageData[3].points);
        thePoints.addAll(silImageData[4].points);
        return thePoints;
    }

	public List<DMDamagePoint> getPoints(final short location) {
		return silImageData[location].points;
	}

    public void removeAllPoints() {
        for(SilhouetteImage silhouetteImage : this.silImageData)
            silhouetteImage.points.clear();
    }

	public void setPoints(final short location, List<DMDamagePoint> points) {
		this.silImageData[location].points = points;
	}

    public void setPointsFromJson(JsonNode jsonArray) {
        List<DMDamagePoint> silhouettePoints;// = new ArrayList<>();

        removeAllPoints();

        if(!jsonArray.isMissingNode())
            silhouettePoints = PortableCheckin.parseJsonArray(jsonArray, DMDamagePoint.class);
        else
            silhouettePoints = null;

        if(silhouettePoints != null)
            for(DMDamagePoint damagePoint : silhouettePoints)
                silImageData[damagePoint.image_enum-1].points.add(damagePoint);
//        this.silImageData[location].points = points;
    }
	
	public void addPoint(final int location, final int X, final int Y, final int typ) {
        final int pos = this.silImageData[location].points.size();
		this.silImageData[location].points.add(new DMDamagePoint(X, Y, typ, location+1, pos));
	}

    public void deletePoint(final short location, final short index) {
        this.silImageData[location].points.remove(index);
    }

    public DMDamagePoint getDMPoint(final short location, final int index) {
        Log.i(TAG, String.valueOf(location) + ", " + String.valueOf(index));
        return this.silImageData[location].points.get(index);
    }
	
	public int getSilhuetteId() {
		return silhuetteId;
	}

	public void setSilhuetteId(int silhuetteId) {
		this.silhuetteId = silhuetteId;
	}
	
	public String getPhotoPath(final short location, final int index) {
		return silImageData[location].photoFileName.get(index);
	}
	
	public List<String> getPhotoNames(final short location) {
		return silImageData[location].photoFileName;
	}	
	
	public void addPhotoName(final short location, final String photoName) {
		silImageData[location].photoFileName.add(photoName);
	}
	
	public void deletePhoto(final short location, final int index) {
		silImageData[location].photoFileName.remove(index);
	}

}
