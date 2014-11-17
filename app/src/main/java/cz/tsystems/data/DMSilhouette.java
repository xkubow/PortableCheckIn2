package cz.tsystems.data;

import java.util.ArrayList;


import java.util.List;

import android.graphics.Bitmap;

public class DMSilhouette extends Object {
	
	private class SilhouetteImage {
		public Bitmap image;// = new ArrayList<Bitmap>(5);
		public List<int[]> points = new ArrayList<int[]>();		
		public List<String> photoPhats = new ArrayList<String>();		
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

	public List<int[]> getPoints(final short location) {
		return silImageData[location].points;
	}

	public void setPoints(final short location, List<int[]> points) {
		this.silImageData[location].points = points;
	}
	
	public void addPoint(final short location, final int X, final int Y, final int typ) {
		this.silImageData[location].points.add(new int[] {X, Y, typ});
	}	
	
	public int[] getpoint(final short location, final int index) {
		return this.silImageData[location].points.get(index);
	}

	public int getSilhuetteId() {
		return silhuetteId;
	}

	public void setSilhuetteId(int silhuetteId) {
		this.silhuetteId = silhuetteId;
	}
	
	public String getPhotoPath(final short location, final int index) {
		return silImageData[location].photoPhats.get(index);
	}
	
	public List<String> getPhotoPath(final short location) {
		return silImageData[location].photoPhats;
	}	
	
	public void AddPhotoPath(final short location, final String photoPath) {
		silImageData[location].photoPhats.add(photoPath);
	}
	
	public void DeletePhoto(final short location, final int index) {
		silImageData[location].photoPhats.remove(index);
	}

}
