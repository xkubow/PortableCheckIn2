package cz.tsystems.portablecheckin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.tsystems.base.BaseFragment;
import cz.tsystems.data.PortableCheckin;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;

public class BodyActivity extends BaseFragment {
	final String TAG = "BodyActivity";
	PortableCheckin app;
	RelativeLayout values1, pointsLayout;
	RadioGroup rbtnSilouettes;
	LinearLayout imageLayout;
	ImageView imgView;
	Button selectedPoint, btnPhoto;
	Switch chkPointType;
	PointF startPoint;
	Boolean move = true;
	float oldXvalue, oldYvalue;
	
	private OnClickListener btnPhotoClickLisener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dispatchTakePictureIntent();
		}
	};
	
	OnTouchListener btnPointTouchListener = new OnTouchListener() {
		int[] location = new int[2];
		
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
//			if (event.getAction() == MotionEvent.ACTION_DOWN){
//                oldXvalue = event.getX();
//                oldYvalue = event.getY();
//                Log.i(myTag, "Action Down " + oldXvalue + "," + oldYvalue);
//            }else 
			if (event.getAction() == MotionEvent.ACTION_MOVE  ){
                pointsLayout.getLocationInWindow(location);
				Button b = (Button)v;				
//				Log.i(TAG, "****  B U T T O N :" + b.toString());
//               LayoutParams params = new LayoutParams(v.getWidth(), v.getHeight(),(int)(me.getRawX() - (v.getWidth() / 2)), (int)(event.getRawY() - (v.getHeight())));
				RelativeLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
				params.setMargins((int)(event.getRawX() - location[0] - 20),  (int)(event.getRawY() - location[1]-20), 0, 0);
//				int[] point = app.getSilhouette().getPoints(getRbtnSilueteIndex()).get(0);
//				point[0] = (int)(event.getRawX() - location[0] - 20);
//				point[1] = (int)(event.getRawY() - location[1]-20);
				b.setLayoutParams(params);
            }
            return true;
		}
	};
	
	OnTouchListener imageOnTouchListener = new OnTouchListener()
    {

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            ImageView view = imgView;//(ImageView) v;
            view.setScaleType(ImageView.ScaleType.MATRIX);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
               	final int X = (int)event.getX() - 20;
               	final int Y = (int)event.getY() - 20;
	           	app.getSilhouette().addPoint(getRbtnSilueteIndex(), X, Y, chkPointType.isChecked()?2:1);               	
	           	addPoint(X, Y, app.getSilhouette().getPoints(getRbtnSilueteIndex()).size(), chkPointType.isChecked());
                break;
            }
            return true;
        }
   };

   
   private void addPoint(final int x, final int y, final int index, final boolean typ) {
		selectedPoint = new Button(app);
		selectedPoint.setOnTouchListener(btnPointTouchListener);
		 selectedPoint.setLayoutParams(new RelativeLayout.LayoutParams(40, 40));
		if(typ)
			selectedPoint.setBackgroundResource(R.drawable.stop);			
		else
			selectedPoint.setBackgroundResource(R.drawable.point);
		// selectedPoint.setImageResource(R.drawable.point);
		RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(40, 40);
		lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lay.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lay.setMargins(x, y, 0, 0);
//		final int imgIndex = getRbtnSilueteIndex();
		// app.addPoint(new PointF(X, Y), imgIndex);
//		final int index = selectedSilhouette.getPoints().size();// app.getPoints(imgIndex).size();
		selectedPoint.setText(String.valueOf(index));
//		Log.i(TAG, "##### THE  B U T T O N :" + selectedPoint.toString());
		pointsLayout.addView(selectedPoint, lay);	   
   }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		app = (PortableCheckin)getActivity().getApplicationContext();
		View rootView = inflater.inflate(R.layout.activity_body, container, false);
		
//		View layout = rootView.findViewById(R.id.llRootLayout);
		values1 = (RelativeLayout) rootView.findViewById(R.id.rlTopControls);
//		values2 = (RelativeLayout) rootView.findViewById(R.id.rlControls);
//		imgSilLayout = (RelativeLayout) rootView.findViewById(R.id.rlImageSilueta);
		pointsLayout = (RelativeLayout) rootView.findViewById(R.id.rlPoints);
		imgView =  (ImageView) rootView.findViewById(R.id.imgViewSilueta);
		imgView.setOnTouchListener(imageOnTouchListener);
		chkPointType = (Switch) rootView.findViewById(R.id.chkOderky);
		btnPhoto = (Button) rootView.findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(btnPhotoClickLisener);
		imageLayout = (LinearLayout) rootView.findViewById(R.id.llPhotos);
		
		
		rbtnSilouettes = (RadioGroup) rootView.findViewById(R.id.rdbPohledy);
		rbtnSilouettes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				changeSiluet();
			}
		});
		return rootView;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		changeSiluet();
		super.onStart();
	}
	
	private final short getRbtnSilueteIndex()
	{
		int checkedRadioButton = rbtnSilouettes.getCheckedRadioButtonId();
		RadioButton rb = (RadioButton) rbtnSilouettes.findViewById(checkedRadioButton);
		return Short.parseShort(rb.getTag().toString());		
	}
	
	public void changeSiluet() {
		final short checkedRadioButton = (short) getRbtnSilueteIndex();
//		selectedSilhouette = app.getSilhouette(checkedRadioButton);
		
		pointsLayout.removeAllViews();
		imageLayout.removeAllViews();
		imgView.setImageBitmap(app.getSilhouette().getImage(checkedRadioButton));
		 
		
		chkPointType.setChecked(false);
		int i = 1;
		for(int[] point : app.getSilhouette().getPoints(getRbtnSilueteIndex()))
			addPoint(point[0], point[1], i++, (point[2] == 2));
		
		i = 1;
		for(String photoPath : app.getSilhouette().getPhotoPath(getRbtnSilueteIndex()))
			addImageView(photoPath);

	}
	
	public void showData(Intent intent)
	{
		
	}

	@Override
	public void updateData(Intent intent) {
		// TODO Auto-generated method stub
		changeSiluet();
	}

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	            app.getDialog(getActivity(), "error", ex.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON);
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, PortableCheckin.REQUEST_TAKE_PHOTO);
	        }
	    }
	}	
	
	String mCurrentPhotoPath;

	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}
	
	private void addImageView(final String thefilepath)
	{
    	final String filepath = thefilepath.replaceFirst("file:", "");
    	File file = new File(filepath);
    	if(!file.exists())   
    		Log.e(TAG, "File not exists : " + mCurrentPhotoPath);
        Bitmap imageBitmap = BitmapFactory.decodeFile(filepath);	        
        ImageView mImageView = new ImageView(getActivity());
//        mImageView.setLayoutParams(new LinearLayout.LayoutParams(imageLayout.getHeight()-5, imageLayout.getHeight() - 5));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageLayout.getHeight()-5, imageLayout.getHeight() - 5);
        params.setMargins(5, 0, 0, 0);
        imageLayout.addView(mImageView, params);
        mImageView.setImageBitmap(imageBitmap);		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == PortableCheckin.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
	    	app.getSilhouette().AddPhotoPath(getRbtnSilueteIndex(), mCurrentPhotoPath);
	    	app.isTakeImage = true;
	    	addImageView(mCurrentPhotoPath);

	    }
	}
	
	
}
