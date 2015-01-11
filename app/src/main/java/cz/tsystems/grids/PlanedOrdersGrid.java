package cz.tsystems.grids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.tsystems.adapters.PlannedOrderAdapter;
import cz.tsystems.communications.CommunicationService;
import cz.tsystems.data.DMPlannedOrder;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;

/**
 * Created by KUBO on 9. 12. 2014.
 */
public class PlanedOrdersGrid extends BaseGridActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(getResources().getString(R.string.Naplanovane_zakazky));
        View include = findViewById(R.id.captionInclude);
        ViewGroup viewGroup = (ViewGroup) include.getParent();
        viewGroup.removeView(include);
        type = eGRDPLANZAK;
    }


    @Override
    public View getCaptionView()
    {
        View view = this.getLayoutInflater().inflate(R.layout.item_planned_order, null);
        view.setBackgroundColor(this.getResources().getColor(R.color.grid_caption));
        ((TextView)view.findViewById(R.id.lbldataSource)).setText(this.getResources().getText(R.string.data_source));
        ((TextView)view.findViewById(R.id.lblPlannedOrderStatus)).setText(this.getResources().getText(R.string.Status));
        ((TextView)view.findViewById(R.id.lblLicenseTag)).setText(this.getResources().getText(R.string.RZV));
        ((TextView)view.findViewById(R.id.lblVehicleDescription)).setText(this.getResources().getText(R.string.Vozidlo));
        ((TextView)view.findViewById(R.id.lblCustomerLabel)).setText(this.getResources().getText(R.string.Zakaznik));
        ((TextView)view.findViewById(R.id.lblPlannedOrderNo)).setText(this.getResources().getText(R.string.plan_zak_cis));

        return  view;
    }

    @Override
    public void setListView() {
        final List<DMPlannedOrder> planedOrderList = ((PortableCheckin)getApplicationContext()).getPlanZakazk();
        PlannedOrderAdapter plannedOrderAdapter = new PlannedOrderAdapter( this, R.layout.item_planned_order, planedOrderList);

        if(plannedOrderAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.PrazdnyZoznam), Toast.LENGTH_SHORT).show();
            return;
        }

        listView.setAdapter(plannedOrderAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DMPlannedOrder plannedOrder = (DMPlannedOrder) listView.getAdapter().getItem(position);

                if(plannedOrder.type == DMPlannedOrder.SECTION)
                    return;

                Intent msgIntent = new Intent(PlanedOrdersGrid.this, CommunicationService.class);
//                msgIntent.putExtra("ACTIONURL", "pchi/DataForCheckIn");
//                msgIntent.putExtra("ACTION", "DataForCheckIn");type
                msgIntent.putExtra("type", type);
                if(plannedOrder.planned_order_id != null && plannedOrder.planned_order_id.length() > 0)
                    msgIntent.putExtra("plannedorderid", plannedOrder.planned_order_id);
                if(plannedOrder.checkin_id > 0)
                    msgIntent.putExtra("checkin_id", String.valueOf(plannedOrder.checkin_id));
                if(plannedOrder.license_tag != null && plannedOrder.license_tag.length() > 0)
                    msgIntent.putExtra("licenseTag", plannedOrder.license_tag);
//                app.showProgrssDialog(PlanedOrdersGrid.this);
//                PlanedOrdersGrid.this.startService(msgIntent);


                setResult(RESULT_OK,msgIntent);
                finish();
            }
        });
    }
}
