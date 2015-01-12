package cz.tsystems.portablecheckin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.PortableCheckin;

/**
 * Created by kubisj on 12.1.2015.
 */
public class Protocol extends Activity {
    ImageView imgProtocol;
    PortableCheckin app;
    ProgressBar progressBar;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("recivedData")) {
                final Bundle b = intent.getBundleExtra("requestData");
                if(b != null && b.getString("ACTION").equalsIgnoreCase("GetProtokolImg")) {
                    Protocol.this.progressBar.setVisibility(View.GONE);
                } else if(b != null && b.getString("ACTION").equalsIgnoreCase("ChiReport")) {
                    requestReportPDF();
                    byte[] bmpArray = b.getByteArray("ReportImg");
                    Bitmap bmp = BitmapFactory.decodeByteArray(bmpArray, 0 , bmpArray.length);
                    Protocol.this.imgProtocol.setImageBitmap(bmp);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        app = (PortableCheckin)getApplicationContext();

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
}
