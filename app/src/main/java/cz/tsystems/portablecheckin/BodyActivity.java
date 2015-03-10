package cz.tsystems.portablecheckin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.tsystems.base.BaseFragment;
import cz.tsystems.base.FragmentPagerActivity;
import cz.tsystems.base.SilhouetteImageView;
import cz.tsystems.base.SilhouetteImgListener;
import cz.tsystems.data.DMDamagePoint;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.grids.BaseGridActivity;
import cz.tsystems.grids.Siluets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


public class BodyActivity extends BaseFragment {
    final String TAG = BodyActivity.class.getSimpleName();
	PortableCheckin app;
	RelativeLayout values1, pointsLayout;
	RadioGroup rbtnSilouettes, rdbStavExterieru;
    LinearLayout imageLayout;
    SilhouetteImageView imgView;
	Button selectedPoint, btnSiluets;
    com.gc.materialdesign.views.ButtonFloat btnPhoto;
    com.gc.materialdesign.views.Switch chkPointType;
    ImageView imgPreview;
    View rootView;
    boolean isTakeImage;
    final int pointSize = 50;

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
                ((FragmentPagerActivity)getActivity()).unsavedCheckin();
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
                float X = event.getRawX() - location[0] - pointSize/2;
                float Y = event.getRawY() - location[1];
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
				params.setMargins((int)X,  (int)Y-pointSize/2, 0, 0);
				button.setLayoutParams(params);
                DMDamagePoint point = app.getSilhouette().getDMPoint(getRbtnSilueteIndex(), Integer.valueOf(button.getText().toString()) - 1);
                point.setX((int)X);
                point.setY((int)Y);
            }
            return true;
		}
	};

    int getPoitType() {
        return chkPointType.isChecked()?2:1;
    }
	
	OnTouchListener imageOnTouchListener = new OnTouchListener()
    {

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            ImageView view = imgView;//(ImageView) v;
            view.setScaleType(ImageView.ScaleType.MATRIX);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ((FragmentPagerActivity)getActivity()).unsavedCheckin();
               	final int X = (int)event.getX();// - 20;
               	final int Y = (int)event.getY();
	           	app.getSilhouette().addPoint(getRbtnSilueteIndex(), X, Y, getPoitType());
	           	addPoint(X, Y, app.getSilhouette().getPoints(getRbtnSilueteIndex()).size(), getPoitType());
                break;
            }
            return true;
        }
   };

   
   private void addPoint(final int x, final int y, final int index, final int typ) {
		selectedPoint = new Button(app);
		selectedPoint.setOnTouchListener(btnPointTouchListener);
		 selectedPoint.setLayoutParams(new RelativeLayout.LayoutParams(pointSize, pointSize));
		if(typ == 1)
			selectedPoint.setBackgroundResource(R.drawable.stop);			
		else
			selectedPoint.setBackgroundResource(R.drawable.point);
		RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(pointSize, pointSize);
		lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lay.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lay.setMargins(x, y-pointSize/2, 0, 0);
		selectedPoint.setText(String.valueOf(index));
		pointsLayout.addView(selectedPoint, lay);
   }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		app = (PortableCheckin)getActivity().getApplicationContext();
		rootView = inflater.inflate(R.layout.activity_body, container, false);
		
//		View layout = rootView.findViewById(R.id.llRootLayout);
		values1 = (RelativeLayout) rootView.findViewById(R.id.rlTopControls);
//		values2 = (RelativeLayout) rootView.findViewById(R.id.rlControls);
//		imgSilLayout = (RelativeLayout) rootView.findViewById(R.id.rlImageSilueta);
		pointsLayout = (RelativeLayout) rootView.findViewById(R.id.rlPoints);
		imgView =  (SilhouetteImageView) rootView.findViewById(R.id.imgViewSilueta);
		imgView.setOnTouchListener(imageOnTouchListener);
        imgView.setImageListener(new SilhouetteImgListener() {
            @Override
            public void onSizeChanged(int w, int h) {
                changeSiluet();
            }
        });
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

        rdbStavExterieru = (RadioGroup)rootView.findViewById(R.id.rdbExterier);
        rdbStavExterieru.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                app.getCheckin().exterior_state = (short) (Short.valueOf(rb.getTag().toString())+1);
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
        int rdbPos =  (int)app.getCheckin().exterior_state-1;
        rdbStavExterieru.check(((RadioButton)rdbStavExterieru.getChildAt(rdbPos)).getId());
        super.onStart();
		// TODO Auto-generated method stub
	}

    @Override
    public void onResume() {
        super.onResume();
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
        List<DMDamagePoint> points = app.getSilhouette().getPoints(getRbtnSilueteIndex());
        for(DMDamagePoint point : points)
            addPoint(point.getX(), point.getY(), i++, point.damage_enum);
    }
	
	public void changeSiluet() {
		final short checkedRadioButton = (short) getRbtnSilueteIndex();
//        removePhotosAsyncTasks();

		imageLayout.removeAllViews();
		imgView.setImageBitmap(app.getSilhouette().getImage(checkedRadioButton));

		chkPointType.setChecked(true);
        reloadPoits();
		
		for(String photoPath : app.getSilhouette().getPhotoNames(getRbtnSilueteIndex()))
			addImageView(photoPath);

	}

    private void removePhotosAsyncTasks() {
        for( int i = 0; i < imageLayout.getChildCount(); i++) {
            final Object child = imageLayout.getChildAt(i);
            if(child.getClass().equals(ProgressBar.class)) {
                final ProgressBar progressBar = (ProgressBar) child;
                ImageOperations imageOperations = (ImageOperations) progressBar.getTag();
                if(imageOperations != null && !imageOperations.isCancelled())
                    imageOperations.cancel(true);
            }
        }
    }


	@Override
	public void showData(Intent intent)
	{
        app.dismisProgressDialog();
        String action = intent.getAction();
        String serviceAction = null;
        if (action.equalsIgnoreCase("recivedData")) {
            try {
                Bundle b = intent.getExtras().getBundle("requestData");
                if(b == null)
                    return;
                serviceAction = b.getString("ACTION");
                if(serviceAction == null)
                    return;
                Log.d("SHOWDATA", intent.getExtras().toString());
            } catch (NullPointerException e) {
                app.getDialog(getActivity(), "error", e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON).show();
            }
        }
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

        if(!app.getSilhouette().getPhotoNames(getRbtnSilueteIndex()).contains(thefileName))
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
            imgView.setId(View.generateViewId());
            imgView.setTag(thefileName);
            imgView.setBackground(new BitmapDrawable(getResources(), imageBitmap));

            registerForContextMenu(imgView);

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
            final int compress = getActivity().getSharedPreferences("cz.tsystems.portablecheckin", getActivity().MODE_PRIVATE).getInt("PhotoResolution",100);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
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
            ((FragmentPagerActivity)getActivity()).unsavedCheckin();
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
            ImageOperations io = new ImageOperations();
            io.execute(mCurrentPhotoPath.replaceFirst("file:", ""), myDir.getAbsolutePath());
//            progressBar.setTag(io);

	    } else if(requestCode == FragmentPagerActivity.eGRID_RESULT && data.getIntExtra("type",0) == BaseGridActivity.eSILHOUETTES) {
            changeSiluet();
        }
	}

    public void deleteImage(int id){
        ((FragmentPagerActivity)getActivity()).unsavedCheckin();
        View imageView = imageLayout.findViewById(id);
        String imageName = (String)imageView.getTag();
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File checkinPhotoDir = new File(storageDir, "CheckInPhotos");
        File imgFile = new File(checkinPhotoDir, imageName);
        if(imgFile.exists()) {
            app.getSilhouette().getPhotoNames(getRbtnSilueteIndex()).remove(imageName);
            imgFile.delete();
            ((ViewGroup)imageView.getParent()).removeView(imageView);
        }
    }
    public void showImage(int id){
        View imageView = imageLayout.findViewById(id);
        String imageName = (String)imageView.getTag();

        Intent i = new Intent(getActivity(), PhotoNahled.class);
        i.putExtra("photoName", imageName);
        getActivity().startActivity(i);

/*        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File checkinPhotoDir = new File(storageDir, "CheckInPhotos");
        File imgFile = new File(checkinPhotoDir, imageName);
        if(imgFile.exists()) {
            if(imgPreview != null)
                ((ViewGroup) rootView.getParent()).removeView(imgPreview);

            imgPreview = new ImageView(getActivity());
            ((ViewGroup) rootView.getParent()).addView(imgPreview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imgPreview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setVisibility(View.GONE);
//                        v.invalidate();
//                        ((ViewGroup)rootView.getParent()).removeView(v);
                    return false;
                }
            });
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgPreview.setImageBitmap(bitmap);
        }*/
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
