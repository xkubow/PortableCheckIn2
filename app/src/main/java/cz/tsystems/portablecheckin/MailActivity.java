package cz.tsystems.portablecheckin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.PortableCheckin;

/**
 * Created by kubisj on 11.2.2015.
 */
public class MailActivity extends Activity {
    final String TAG = MailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.txtEmail);
                Intent msgIntent = new Intent(MailActivity.this, CommunicationService.class);
                msgIntent.putExtra("ACTIONURL", "pchi/SendEmail");
                msgIntent.putExtra("ACTION", "SendEmail");
                msgIntent.putExtra("checkinid", PortableCheckin.checkin.checkin_id);
                msgIntent.putExtra("emailAddress", text.getText().toString());
                text = (EditText) findViewById(R.id.txtSubject);
                msgIntent.putExtra("Subject", text.getText().toString());
                text = (EditText) findViewById(R.id.txtMessage);
                msgIntent.putExtra("EmailText", text.getText().toString());
                MailActivity.this.startService(msgIntent);
                MailActivity.this.finish();
            }
        });
    }

}
