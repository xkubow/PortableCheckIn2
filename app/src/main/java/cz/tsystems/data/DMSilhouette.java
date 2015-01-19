package cz.tsystems.data;

import java.util.ArrayList;


import java.util.List;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DMSilhouette extends Object {

    public class SilhouettePoint {
        public int coord_x;
        public int coord_y;
        public int damage_enum;
        public String description="";
        public int image_enum;
        public int order;

        public SilhouettePoint(final int newCoord_x, final int newCoord_y, final int newDamage_enum, final int newImage_enum, final int newOrder) {
            coord_x = newCoord_x;
            coord_y = newCoord_y;
            damage_enum = newDamage_enum;
            image_enum = newImage_enum+1;
            order = newOrder+1;
        }
        @JsonIgnore
        public int[] getPointArray() {
            return new int[] {coord_x, coord_y, damage_enum};
        }
    }

	public class SilhouetteImage {

        public Bitmap image;// = new ArrayList<Bitmap>(5);

        public List<SilhouettePoint> points = new ArrayList<>();
        public List<String> photoFileName = new ArrayList<String>();

        public List<int[]> getAllPointArray() {
            List<int[]> listPoints = new ArrayList<>();
            for (SilhouettePoint p : points)
                listPoints.add(p.getPointArray());
            return listPoints;
        }
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

    public List<SilhouettePoint> getAllPoints() {
        List<SilhouettePoint> thePoints = new ArrayList<>();
        thePoints.addAll(silImageData[0].points);
        thePoints.addAll(silImageData[1].points);
        thePoints.addAll(silImageData[2].points);
        thePoints.addAll(silImageData[3].points);
        thePoints.addAll(silImageData[4].points);
        return thePoints;
    }

	public List<int[]> getPoints(final short location) {
		return silImageData[location].getAllPointArray();
	}

	public void setPoints(final short location, List<SilhouettePoint> points) {
		this.silImageData[location].points = points;
	}
	
	public void addPoint(final short location, final int X, final int Y, final int typ) {
        final int pos = this.silImageData[location].points.size();
		this.silImageData[location].points.add(new SilhouettePoint(X, Y, typ, location, pos));
	}

    public void deletePoint(final short location, final short index) {
        this.silImageData[location].points.remove(index);
    }
	
	public int[] getpoint(final short location, final int index) {
		return this.silImageData[location].points.get(index).getPointArray();
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
