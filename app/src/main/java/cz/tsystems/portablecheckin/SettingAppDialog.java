package cz.tsystems.portablecheckin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.gc.materialdesign.views.ButtonFlat;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by kubisj on 10.2.2015.
 */
public class SettingAppDialog extends Dialog {
    Context context;
    EditText txtXyzmoUri, txtTimeOut;
    Switch chkUseExternXyzmo, chkPacketDetail;
    SeekBar sbPhotoResolution;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SettingAppDialog.this.dismiss();
        }
    };

    OnDismissListener onDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            setSharetPreferences();
        }
    };

    public SettingAppDialog(Context context) {
        super(context);
        this.context = context;
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.activity_setting_app, null);
        SharedPreferences sp = context.getSharedPreferences("cz.tsystems.portablecheckin", context.MODE_PRIVATE);

        txtXyzmoUri = (EditText) view.findViewById(R.id.txtXyzmoUri);
        txtXyzmoUri.setText(sp.getString("XyzmoURI",""));
        chkUseExternXyzmo = (Switch) view.findViewById(R.id.chkUseExternalXyzmo);
        chkUseExternXyzmo.setChecked(sp.getBoolean("useExternXyzmo", false));
        txtTimeOut = (EditText) view.findViewById(R.id.txtTimeOut);
        txtTimeOut.setText(String.valueOf(sp.getInt("TimeOut", 1000)));
        chkPacketDetail = (Switch) view.findViewById(R.id.chkPacketDetail);
        chkPacketDetail.setChecked(sp.getBoolean("PacketDetail", false));
        sbPhotoResolution = (SeekBar) view.findViewById(R.id.sbPhotoResolution);
        sbPhotoResolution.setProgress(sp.getInt("PhotoResolution", 80));


        ButtonFlat btnOk = (ButtonFlat)view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(onClickListener);

        this.setOnDismissListener(onDismissListener);

        setContentView(view);
    }

    public void setSharetPreferences() {
        SharedPreferences sp = context.getSharedPreferences("cz.tsystems.portablecheckin", context.MODE_PRIVATE);
        SharedPreferences.Editor spe= sp.edit();

        if (txtXyzmoUri.getText().length() > 0)
            spe.putString("XyzmoURI", txtXyzmoUri.getText().toString());
        else
            spe.remove("XyzmoURI");
        spe.putBoolean("useExternXyzmo", chkUseExternXyzmo.isChecked());

        try {
            spe.putInt("TimeOut", NumberFormat.getNumberInstance().parse(txtTimeOut.getText().toString()).intValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spe.putBoolean("PacketDetail", chkPacketDetail.isChecked());
        spe.putInt("PhotoResolution", sbPhotoResolution.getProgress());
        spe.commit();
    }

    public SettingAppDialog(Context context, int theme) {
        super(context, theme);
    }

    protected SettingAppDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
