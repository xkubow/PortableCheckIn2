package cz.tsystems.portablecheckin;
/**
// significant://workstep?WorkstepId=<WorkstepId>&server=<server>&protocol=<http|https>&port=<port>&path=<path>
// "<http|https>://<server>:<port>/<path>/WorkstepController.Process.asmx" is the link to the Workstep Controller Process web service
// example: http://beta2.testlab.xyzmo.com:57003/WorkstepController.Process.asmx
// This web service should be reachable from the Android device
// Example of a Workstep Link created on the xyzmo beta2 testlab server:
// http://launch.xyzmo.com/SignificantAndroidAppLauncher.aspx?WorkstepId=3BFACA02133062BA56C8B6B76D8DC6919F0206C73BE2928762CFFDDA3F830BA1C5F38E7F9414ABA0BF530531AEF8BF31&server=beta2.testlab.xyzmo.com&port=57003&protocol=http
**/

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

import cz.tsystems.adapters.ProtocolPrintAdapter;
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
    Bitmap tempBitmap;
    String fileNamePDF;
    Canvas canvas;
    Paint p = new Paint();


    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix imgMatrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    private float dx; // postTranslate X distance
    private float dy; // postTranslate Y distance
    private float[] matrixValues = new float[9];
    private float[] theMatrix = new float[9];
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
    float endY, endX;
    float lastY, lastX;
    String workstepId;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("recivedData")) {
                final Bundle b = intent.getBundleExtra("requestData");
                if(b != null && b.getString("ACTION").equalsIgnoreCase("GetProtokolImg")) {
                    Protocol.this.setProtocolImg(intent.getStringExtra("pdfFileName"));
                    requestReportPDF();
                } else if(b != null && b.getString("ACTION").equalsIgnoreCase("ChiReport")) {
                    fileNamePDF = intent.getStringExtra("pdfFileName");
                } else if(b != null && b.getString("ACTION").equalsIgnoreCase("GetWorkstepId")) {
                    workstepId = intent.getStringExtra("WorkstepId");
                    openXyzmo();
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

        p.setAntiAlias(true);
        p.setFilterBitmap(true);

        requestReportImg();
        lastY = 0;
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
            FileInputStream fileInputStream = null;


            protokol_bmp = BitmapFactory.decodeFile(filename);
            tempBitmap = Bitmap.createBitmap(imgProtocol.getMeasuredWidth(), imgProtocol.getMeasuredHeight(), Bitmap.Config.RGB_565);
            tempBitmap.eraseColor(Color.WHITE);
            canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(protokol_bmp, new Matrix(), p);
            Protocol.this.imgProtocol.setImageBitmap(tempBitmap);
            imgProtocol.setImageMatrix(matrix);
            base.set(0,0);

            endY = findViewById(R.id.contetView).getMeasuredHeight() - protokol_bmp.getHeight();//imgProtocol.getDrawable().getIntrinsicHeight();
            Protocol.this.progressBar.setVisibility(View.GONE);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
                lastY = 0;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                if(mode == DRAG)
                    base.y += dy;
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    imgProtocol.getImageMatrix().getValues(matrixValues);
                    matrix.set(savedMatrix);

                    dy = event.getY() - start.y;
                    matrix.getValues(theMatrix);

                    if(dy+base.y < endY)
                        dy = lastY;
                    else if((dy + base.y) > 0)
                        dy = lastY;

                    Log.d(TAG, String.valueOf(dy) + " : "
                            + String.valueOf(matrixValues[Matrix.MTRANS_Y]));

                    lastY = dy;
                    matrix.postTranslate(0, dy);
                }
        }

        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(protokol_bmp, matrix, p);
        Protocol.this.imgProtocol.setImageBitmap(tempBitmap);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.protocol_menu, menu);
        menu.findItem(R.id.actin_share).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sharePDF();
                return false;
            }
        });
        menu.findItem(R.id.actin_mail).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent myIntent = new Intent(Protocol.this, MailActivity.class);
                startActivity(myIntent);
                return false;
            }
        });
        menu.findItem(R.id.actin_sign).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getWorkstepId();
                return false;
            }
        });
        menu.findItem(R.id.actin_print).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                printProtocol();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    void printProtocol() {
        File file = new File(fileNamePDF);
        //https://developer.android.com/training/printing/custom-docs.html

        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            PrintManager printManager = (PrintManager) this
                    .getSystemService(Context.PRINT_SERVICE);

            String jobName = this.getString(R.string.app_name) +
                    " PCHI_Protocol";

            printManager.print(jobName, new ProtocolPrintAdapter(this, path.getPath()),
                    null);
        }
    }

    void sharePDF() {
        File file = new File(fileNamePDF);

        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(Protocol.this,
                        "No Application Available to View PDF",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getWorkstepId() {
        Intent msgIntent = new Intent(this, CommunicationService.class);
        msgIntent.putExtra("ACTIONURL", "Signing/getWorkstepId");
        msgIntent.putExtra("ACTION", "GetWorkstepId");
        msgIntent.putExtra("checkinid", app.getCheckin().checkin_id);
        this.startService(msgIntent);
    }

    void nastavPodpisovanie() {
        final SettingAppDialog settingAppDialog = new SettingAppDialog(this);
        settingAppDialog.setTitle(this.getResources().getString(R.string.action_settings));
        settingAppDialog.show();
        settingAppDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                settingAppDialog.setSharetPreferences();
                openXyzmo();
            }
        });
    }

    void openXyzmo() {
        Log.d(TAG, workstepId);
        SharedPreferences sp = getSharedPreferences("cz.tsystems.portablecheckin", MODE_PRIVATE);
        String xyzmoUriStr = sp.getString("XyzmoURI",null);
//                    if(BuildConfig.DEBUG)
//                        xyzmoUriStr = "https://sign01.xyzmo.designplus.cz:47003";
        if(xyzmoUriStr == null) {
            nastavPodpisovanie();
            return;
        }
        Uri uri = Uri.parse(xyzmoUriStr);
        xyzmoUriStr = "significant://workstep?" + //"http://launch.xyzmo.com/SignificantAndroidAppLauncher.aspx?" +
                "WorkstepId=" + workstepId +//7722DBC1986DF5A22D63BEF578FFFE7B122FD0A50DA5030B18001E26F676B85F1A9988007265309474F1B936DDA0D6DE" +
                "&server="+uri.getHost() +//beta2.testlab.xyzmo.com" +
                "&port="+String.valueOf(uri.getPort())+
                "&protocol="+uri.getScheme();
        uri = Uri.parse(xyzmoUriStr);
        Intent signIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(signIntent);
    }
}
