package cz.tsystems.portablecheckin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.tsystems.base.BaseFragment;
import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.grids.Siluets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.gc.materialdesign.views.ButtonFloat;

public class BodyActivity extends BaseFragment {
	final String TAG = "BodyActivity";
	PortableCheckin app;
	RelativeLayout values1, pointsLayout;
	RadioGroup rbtnSilouettes;
	LinearLayout imageLayout;
	ImageView imgView;
	Button selectedPoint, btnSiluets;
    com.gc.materialdesign.views.ButtonFloat btnPhoto;
    com.gc.materialdesign.views.Switch chkPointType;
    boolean isTakeImage;

	private OnClickListener btnPhotoClickLisener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dispatchTakePictureIntent();
		}
	};
	
	OnTouchListener btnPointTouchListener = new OnTouchListener() {
		int[] location = new int[2];
        boolean moved = false;
		
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
            Button button = (Button) v;
            if (event.getAction() == MotionEvent.ACTION_UP ) {
                if(moved) {
                    moved = false;
                    return true;
                }
                final int index = Integer.valueOf( ((Button)v).getText().toString() ) -1 ;
                app.getSilhouette().deletePoint(getRbtnSilueteIndex(), (short) index);
                pointsLayout.removeView(v);
                reloadPoits();
            }
			else if (event.getAction() == MotionEvent.ACTION_MOVE  ){
                moved = true;
                pointsLayout.getLocationInWindow(location);
                float X = event.getRawX() - location[0] - 20;
                float Y = event.getRawY() - location[1] - 20;
                final float XBoudary = pointsLayout.getWidth()-v.getWidth();
                final float YBoudary = pointsLayout.getHeight()-v.getHeight();
                if(X < 0)
                    X = 0;
                if(X > XBoudary)
                    X = XBoudary;
                if(Y < 0)
                    Y = 0;
                if(Y > YBoudary)
                    Y = YBoudary;

				RelativeLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
				params.setMargins((int)X,  (int)Y, 0, 0);
				button.setLayoutParams(params);
                int[] point = app.getSilhouette().getpoint(getRbtnSilueteIndex(),Integer.valueOf(button.getText().toString())-1);
                point[0] = (int)X;
                point[1] = (int)Y;
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
		RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(40, 40);
		lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lay.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lay.setMargins(x, y, 0, 0);
		selectedPoint.setText(String.valueOf(index));
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
		chkPointType = (com.gc.materialdesign.views.Switch) rootView.findViewById(R.id.chkOderky);
		btnPhoto = (com.gc.materialdesign.views.ButtonFloat) rootView.findViewById(R.id.btnPhoto);
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

        btnSiluets = (Button)rootView.findViewById(R.id.btnSiulety);
        btnSiluets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentPagerActivity)getActivity()).setCheckLogin(false);
                Intent myIntent = new Intent(getActivity(), Siluets.class);
                startActivityForResult(myIntent, FragmentPagerActivity.eGRID_RESULT);
            }
        });
		return rootView;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
        if(!isTakeImage)
            changeSiluet();
        isTakeImage = false;
		super.onStart();
	}
	
	private final short getRbtnSilueteIndex()
	{
		int checkedRadioButton = rbtnSilouettes.getCheckedRadioButtonId();
		RadioButton rb = (RadioButton) rbtnSilouettes.findViewById(checkedRadioButton);
		return Short.parseShort(rb.getTag().toString());		
	}

    private void reloadPoits() {
        pointsLayout.removeAllViews();
        int i = 1;
        for(int[] point : app.getSilhouette().getPoints(getRbtnSilueteIndex()))
            addPoint(point[0], point[1], i++, (point[2] == 2));
    }
	
	public void changeSiluet() {
		final short checkedRadioButton = (short) getRbtnSilueteIndex();

		imageLayout.removeAllViews();
		imgView.setImageBitmap(app.getSilhouette().getImage(checkedRadioButton));

		chkPointType.setChecked(false);
        reloadPoits();
		
		for(String photoPath : app.getSilhouette().getPhotoNames(getRbtnSilueteIndex()))
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
	            app.getDialog(getActivity(), "error", ex.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON).show();
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
        File checkinPhotoDir = new File(storageDir, "CheckInPhotos");
        checkinPhotoDir.mkdir();
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
            checkinPhotoDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}
	
	private void addImageView(final String thefileName)
	{
        if(getActivity() == null)
            return;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.setMargins(0, 5, 0, 0);

        try {
            File myDir = getActivity().getDir("thumbnails", Context.MODE_PRIVATE);
            File file = new File(myDir + File.separator + thefileName);
            if(!file.exists())
                Log.e(TAG, "File not exists : " + mCurrentPhotoPath);

            Bitmap imageBitmap = BitmapFactory.decodeStream(new FileInputStream(file));

            ProgressBar progressView = (ProgressBar) imageLayout.findViewWithTag(thefileName);
            int index = -1;
            if(progressView != null) {
                ViewGroup parent = (ViewGroup) progressView.getParent();
                index = parent.indexOfChild(progressView);
                parent.removeView(progressView);
            }

            View imgView = new View(getActivity());
            imgView.setTag(thefileName);
            imgView.setBackground(new BitmapDrawable(getResources(), imageBitmap));
            if (index != -1) {
                imageLayout.addView(imgView, index, params);
            } else
                imageLayout.addView(imgView, params);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}

    private String decodeFile(File f, final String thumbNailDir){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            double bitmapRatio = 190.0 /  ((o.outWidth > o.outHeight)?o.outWidth:o.outHeight);
            final int height_tmp = (int) (o.outHeight * bitmapRatio);
            final int width_tmp = (int) (o.outWidth * bitmapRatio);

            Bitmap imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(new FileInputStream(f)), width_tmp, height_tmp, true);
//            File myDir = getActivity().getDir("thumbnails", Context.MODE_PRIVATE);
            File file = new File (thumbNailDir + File.separator + f.getName());
            if (file.exists ())
                file.delete ();

            FileOutputStream out = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            return file.getPath();
        } catch (FileNotFoundException e) {
            Log.i("ExifInteface .........", "FileNotFoundException =" + e.getLocalizedMessage());
        }

        return null;
    }

    private void rotateImage(final String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            Bitmap imageBitmap = BitmapFactory.decodeFile(path);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.i("ExifInteface .........", "rotation =" + orientation);
            Matrix m = new Matrix();

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
            }

            imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), m, true);
            FileOutputStream out = new FileOutputStream(path);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
            exif.saveAttributes();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == PortableCheckin.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            File file = new File(mCurrentPhotoPath.replaceFirst("file:", ""));
            if(!file.exists()) {
                Log.e(TAG, "File not exists : " + mCurrentPhotoPath);
            }
	    	app.getSilhouette().addPhotoName(getRbtnSilueteIndex(), file.getName());
            ((FragmentPagerActivity)getActivity()).setCheckLogin(false);
	    	isTakeImage = true;

            ProgressBar progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
            progressBar.setTag(file.getName());
//            ProgressBarCircularIndeterminate progressBar = new ProgressBarCircularIndeterminate(getActivity(), getActivity().getResources().get com.gc.materialdesign.R.attr.indeterminateProgressStyle);
            imageLayout.addView(progressBar);

            File myDir = getActivity().getDir("thumbnails", Context.MODE_PRIVATE);
            new ImageOperations().execute(mCurrentPhotoPath.replaceFirst("file:", ""), myDir.getAbsolutePath());

	    }
	}


    private class ImageOperations extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            final String imgFilePath = params[0];
            File file = new File(imgFilePath);
            if(!file.exists()) {
                Log.e(TAG, "File not exists : " + imgFilePath);
                return "";
            }
            rotateImage(imgFilePath);
            file = new File(imgFilePath);
            if(!file.exists()) {
                Log.e(TAG, "File not exists : " + imgFilePath);
                return "";
            }
            final String thumbNailpath = decodeFile(file, params[1]);
            file = new File(thumbNailpath);
            if(!file.exists()) {
                Log.e(TAG, "File not exists : " + thumbNailpath);
                return "";
            }
            return file.getName();
        }

        @Override
        protected void onPostExecute(String result) {
            addImageView(result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
