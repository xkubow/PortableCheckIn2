package cz.tsystems.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cz.tsystems.adapters.PlannedOrderAdapter;
import cz.tsystems.data.DMPlannedOrder;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by kubisj on 25.11.2014.
 */
public class BaseGrid extends Dialog {
    private Activity activity;

    public BaseGrid(Activity activity) {
        super(activity, R.layout.activity_base_grd);
        this.activity = activity;
    }

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_grd);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.activity_title_bar);
        setTitle(activity.getResources().getText(R.string.historia_vozu));

        View v = findViewById(R.id.baseGrdLayaut);//activity.getLayoutInflater().inflate(R.layout.activity_base_grd, null);
        View v2 = activity.getLayoutInflater().inflate(R.layout.item_planned_order, null);
        ((LinearLayout) v).addView(v2, 0);

        ListView lv = (ListView)v.findViewById(R.id.grid);
        final List<DMPlannedOrder> planedOrderList = ((PortableCheckin)activity.getApplicationContext()).getPlanZakazk();
        PlannedOrderAdapter plannedOrderAdapter = new PlannedOrderAdapter( activity, R.layout.item_planned_order, planedOrderList);
        lv.setAdapter(plannedOrderAdapter);

    }

    private String getCaption()
    {
        return "";
    }
}
