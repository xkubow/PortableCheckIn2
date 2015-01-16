package cz.tsystems.portablecheckin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.FileInputStream;

import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.PortableCheckin;

/**
 * Created by kubisj on 12.1.2015.
 */
public class Protocol extends Activity implements View.OnTouchListener {
    final String TAG = Protocol.class.getSimpleName();
    ImageView imgProtocol;
    PortableCheckin app;
    ProgressBar progressBar;
    Bitmap protokol_bmp;

    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    private float dx; // postTranslate X distance
    private float dy; // postTranslate Y distance
    private float[] matrixValues = new float[9];
    float matrixX = 0; // X coordinate of matrix inside the ImageView
    float matrixY = 0; // Y coordinate of matrix inside the ImageView
    float width = 0; // width of drawable
    float height = 0; // height of drawable

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF base = new PointF();
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("recivedData")) {
                final Bundle b = intent.getBundleExtra("requestData");
                if(b != null && b.getString("ACTION").equalsIgnoreCase("GetProtokolImg")) {
                    Protocol.this.setProtocolImg(intent.getStringExtra("pdfFileName"));
                } else if(b != null && b.getString("ACTION").equalsIgnoreCase("ChiReport")) {
                    requestReportPDF();
                    Protocol.this.progressBar.setVisibility(View.GONE);
                    final String tmpFileName = intent.getStringExtra("pdfFileName");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        app = (PortableCheckin)getApplicationContext();
        imgProtocol = (ImageView) findViewById(R.id.imgProtocol);
        imgProtocol.setBackgroundColor(getResources().getColor(R.color.white));
        imgProtocol.setOnTouchListener(this);
        findViewById(R.id.contetView).setBackgroundColor(getResources().getColor(R.color.white));
        setTitle(R.string.Protokol);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.color.transparent);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);// progressBar.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addContentView(progressBar, layoutParams);

        requestReportImg();
    }

    private void requestReportPDF() {
        Intent msgIntent = new Intent(this, CommunicationService.class);
        msgIntent.putExtra("ACTIONURL", "pchi/PDFReport");
        msgIntent.putExtra("ACTION", "ChiReport");
        msgIntent.putExtra("checkinid", app.getCheckin().checkin_id);
        msgIntent.putExtra("preview", false);
        this.startService(msgIntent);
    }

    private void requestReportImg() {
        Intent msgIntent = new Intent(this, CommunicationService.class);
        msgIntent.putExtra("ACTIONURL", "pchi/PDFReportConvert");
        msgIntent.putExtra("ACTION", "GetProtokolImg");
        msgIntent.putExtra("checkinid", app.getCheckin().checkin_id);
        msgIntent.putExtra("frormat", "png");
        msgIntent.putExtra("preview", false);
        this.startService(msgIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent intent = new Intent();
                this.setResult(RESULT_OK, intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void registerRecaiver() {
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("recivedData");
        filterSend.addAction("grdResponse");
        registerReceiver(receiver, filterSend);
    }

    @Override
    protected void onResume() {
        registerRecaiver();
        super.onResume();
    }

    public void setProtocolImg(final String filename) {
        //http://stackoverflow.com/questions/3466297/how-to-display-a-part-of-an-image
        //http://stackoverflow.com/questions/15698621/how-to-scale-and-save-view-to-sdcard
        try {
            Protocol.this.progressBar.setVisibility(View.GONE);
            FileInputStream fileInputStream = null;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, imgProtocol.getWidth(), imgProtocol.getHeight());

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            protokol_bmp = BitmapFactory.decodeFile(filename, options);
//            Bitmap theBitmap = Bitmap.createBitmap(protokol_bmp, 0,0,protokol_bmp.getWidth(), protokol_bmp.getHeight());
            imgProtocol.setImageBitmap(protokol_bmp);
            imgProtocol.setScaleType(ImageView.ScaleType.MATRIX);
            imgProtocol.setImageMatrix(matrix);
            base.set(0,0);

/*        try {
            fileInputStream = new FileInputStream(new File(filename));
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            imgProtocol.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
// Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }

        return inSampleSize;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                if(mode == DRAG) {
                    base.x += dx;
                    base.y += dy;
                }
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    imgProtocol.getImageMatrix().getValues(matrixValues);
                    matrix.set(savedMatrix);

                    dx = event.getX() - start.x;
                    dy = event.getY() - start.y;

                    if((matrixValues[Matrix.MTRANS_X] + dx) < 0
                            || (matrixValues[Matrix.MTRANS_X] + dx) > imgProtocol.getDrawable().getIntrinsicWidth())
                        dx = 0;
                    if((dy + matrixValues[Matrix.MTRANS_Y]) < -(imgProtocol.getDrawable().getIntrinsicHeight() /*- imgProtocol.getHeight()/2*/))
                        dy = 0;
                    else if((dy + matrixValues[Matrix.MTRANS_Y]) > 0)
                        dy = dy - matrixValues[Matrix.MTRANS_Y];

                    matrix.postTranslate(dx, dy); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
}
