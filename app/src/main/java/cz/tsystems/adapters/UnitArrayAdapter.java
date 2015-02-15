package cz.tsystems.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import cz.tsystems.data.DMPacket;
import cz.tsystems.data.DMUnit;
import cz.tsystems.data.PortableCheckin;
import cz.tsystems.portablecheckin.R;
import cz.tsystems.portablecheckin.UnitServiceDialog;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

public class UnitArrayAdapter extends ArrayAdapter<DMUnit> implements PinnedSectionListView.PinnedSectionListAdapter{

    private final static String TAG = UnitArrayAdapter.class.getSimpleName();
	private Context context;
	private List<DMUnit> data;
//    private List<DMUnit> filteredData;
    private PortableCheckin app;

	public UnitArrayAdapter(Context context, int resource, int textViewResourceId, List<DMUnit> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
        app = (PortableCheckin)context.getApplicationContext();
		this.data = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_unit, null);
        }
        DMUnit unit = getItem(position);

        TextView text = (TextView) v.findViewById(R.id.lblText);
		text.setText(unit.chck_part_txt);

        text = (TextView) v.findViewById(R.id.lblPosition);
        text.setText(unit.chck_position_abbrev_txt);

        text = (TextView) v.findViewById(R.id.lblRequired);
        if(unit.chck_required_id != null && unit.chck_required_id != DMUnit.eRequired_odlozit) {
            text.setText(unit.chck_required_txt);
        }
        else
            text.setText("");

        text = (TextView) v.findViewById(R.id.lblCena);
        if(unit.sell_price != null)
            text.setText(unit.getSellPriceAsString(getContext()) +" "+ PortableCheckin.setting.currency_abbrev);
        else
            text.setText("");

        com.gc.materialdesign.views.CheckBox chkUnit = ( com.gc.materialdesign.views.CheckBox)v.findViewById(R.id.checkBox);
        chkUnit.setTag(position);
        chkUnit.setOncheckListener(new com.gc.materialdesign.views.CheckBox.OnCheckListener() {
            @Override
            public void onCheck(com.gc.materialdesign.views.CheckBox checkBox, boolean isChecked) {
                int position = (Integer)checkBox.getTag();
                DMUnit unit = UnitArrayAdapter.this.getItem(position);
                unit.chck_status_id = checkBox.isChecked()?1:0;
            }
        });

        Button serviseButton = (Button)v.findViewById(R.id.btnService);
        serviseButton.setTag(position);
        serviseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showServiceView((Button) v);
            }
        });

        Button odlozButton = (Button)v.findViewById(R.id.btnOdloz);
        odlozButton.setTag(position);
        odlozButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnOdloz = (Button) v;
                DMUnit u = UnitArrayAdapter.this.getItem((int)btnOdloz.getTag());
                if(u.chck_status_id != null) {
                    if(u.chck_required_id != null && u.chck_required_id == DMUnit.eRequired_odlozit) {
                        u.chck_status_id = null;
                        u.chck_required_id = null;
                        btnOdloz.setBackground(context.getResources().getDrawable(R.drawable.ic_drawer_grey));
                    }
                    return;
                }
                u.chck_status_id = DMUnit.eStatus_problem;
                u.chck_required_id = DMUnit.eRequired_odlozit;
                u.sell_price = null;
                btnOdloz.setBackground(context.getResources().getDrawable(R.drawable.ic_drawer_petrol));

                View btnOdlozParent = (View)btnOdloz.getParent();
                TextView tv = (TextView)btnOdlozParent.findViewById(R.id.lblRequired);
                tv.setText("");
                tv = (TextView)btnOdlozParent.findViewById(R.id.lblCena);
                tv.setText("");
                u.workshop_packet = null;
                u.workshop_packet_number = null;
                u.workshop_packet_description = null;
                u.spare_part_dispon_id = null;
                u.economic = null;
                btnOdlozParent.findViewById(R.id.btnService).setBackground(context.getResources().getDrawable(R.drawable.ic_tools_grey));
                com.gc.materialdesign.views.CheckBox chkUnit = ( com.gc.materialdesign.views.CheckBox)btnOdlozParent.findViewById(R.id.checkBox);
                chkUnit.setChecked(false);
            }
        });

        serviseButton.setBackground(context.getResources().getDrawable(R.drawable.ic_tools_grey));
        odlozButton.setBackground(context.getResources().getDrawable(R.drawable.ic_drawer_grey));
        chkUnit.setStaticChecked(false);
        if(unit.chck_status_id != null) {
            if (unit.chck_required_id != null) {
                if (unit.chck_required_id == DMUnit.eRequired_odlozit)
                    odlozButton.setBackground(context.getResources().getDrawable(R.drawable.ic_drawer_petrol));
                else if (unit.chck_required_id == DMUnit.eRequired_packet)
                    serviseButton.setBackground(DMPacket.getPacketIcon(this.getContext(), unit.spare_part_dispon_id, unit.economic));
                else
                    serviseButton.setBackground(context.getResources().getDrawable(R.drawable.ic_tools_petrol));
            } else
                chkUnit.setChecked((unit.chck_status_id == 1));
        }

        v.setOnTouchListener(new OnSwipeTouchListener());

		return v;
	}

    private void showServiceView(final Button unitButtonView) {
        final DMUnit u = data.get((Integer)unitButtonView.getTag());
        if(u.chck_status_id != 0)
            return;
        final List<DMPacket> packetList = new ArrayList<DMPacket>();
        List<DMPacket> packets = app.getPackets();
        if(packets != null) {
            Iterator<DMPacket> packetIterator = app.getPackets().iterator();
            DMPacket packet = null;

            while (packetIterator.hasNext()) {
                try {
                    packet = packetIterator.next();
                    if (packet.chck_unit_id == u.chck_unit_id
                            && packet.chck_part_id == u.chck_part_id)
                        packetList.add(packet);
                } catch (NoSuchElementException e) {
                    app.getDialog(getContext(), "error", e.getLocalizedMessage(), PortableCheckin.DialogType.SINGLE_BUTTON);
                }
            }
        }

        packetList.addAll(app.getUnitService(u));

        UnitServiceDialog unitServiceDialog = new UnitServiceDialog(getContext(), u, packetList){
            @Override
            public void OnOkClick(final DMPacket selectedPaked, final String cena){
                if(selectedPaked == null) //Nevybrany paket
                    return;
                u.chck_status_id = DMUnit.eStatus_problem;
                if(selectedPaked.workshop_packet_number != null) {
                    u.workshop_packet_number = selectedPaked.workshop_packet_number;
                    u.workshop_packet_description = selectedPaked.workshop_packet_description;
                    u.economic = selectedPaked.economic;
                    u.spare_part_dispon_id = selectedPaked.spare_part_dispon_id;
                    u.chck_required_id = 19;
                    u.chck_required_txt = selectedPaked.workshop_packet_description;
                    u.sell_price = Double.valueOf(cena);
                    if(selectedPaked.restrictions != null)
                        u.chck_required_txt += " " + selectedPaked.restrictions;
                    u.updatePacket();
                } else {
                    u.workshop_packet = null;
                    u.workshop_packet_number = null;
                    u.workshop_packet_description = null;
                    u.spare_part_dispon_id = null;
                    u.economic = null;
                    u.chck_required_txt = selectedPaked.workshop_packet_description;
                    u.chck_required_id = selectedPaked.chck_required_id;
                }
                View v = (View)unitButtonView.getParent();
                TextView tv = (TextView)v.findViewById(R.id.lblRequired);
                tv.setText(u.chck_required_txt);
                tv = (TextView)v.findViewById(R.id.lblCena); //TODO chack if is corect double
                if(cena.length() > 0) {
                    u.sell_price = Double.valueOf(cena);
                    tv.setText(u.sell_price + " " + PortableCheckin.setting.currency_abbrev);
                } else {
                    u.sell_price = null;
                    tv.setText("");
                }
                v.findViewById(R.id.btnOdloz).setBackground(context.getResources().getDrawable(R.drawable.ic_drawer_grey));
                com.gc.materialdesign.views.CheckBox chkUnit = ( com.gc.materialdesign.views.CheckBox)v.findViewById(R.id.checkBox);
                chkUnit.setChecked(false);

                unitButtonView.setBackground(selectedPaked.getCelkyIcon(getContext()));
            }
        };
        unitServiceDialog.show();

    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            super.onTouch(v,event);
//            return false;
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                onSwipeBottom();
                            } else {
                                onSwipeTop();
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        public void onSwipeRight() {}
        public void onSwipeLeft() {}
        public void onSwipeTop() {}
        public void onSwipeBottom() {}
    }

}
