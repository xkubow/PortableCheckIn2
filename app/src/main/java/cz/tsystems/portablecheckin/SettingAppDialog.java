package cz.tsystems.portablecheckin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gc.materialdesign.views.Switch;

/**
 * Created by kubisj on 10.2.2015.
 */
public class SettingAppDialog extends Dialog {
    Context context;
    EditText txtXyzmoUri;
    Switch chkUseExternXyzmo;

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
        txtXyzmoUri = (EditText) view.findViewById(R.id.txtXyzmoUri);
        chkUseExternXyzmo = (Switch) view.findViewById(R.id.chkUseExternalXyzmo);

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
        spe.commit();
    }

    public SettingAppDialog(Context context, int theme) {
        super(context, theme);
    }

    protected SettingAppDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
