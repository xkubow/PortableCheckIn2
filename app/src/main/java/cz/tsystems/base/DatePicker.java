package cz.tsystems.base;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
//import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

public class DatePicker extends DialogFragment implements OnDateChangedListener, OnDateSetListener {
	EditText txtDate, txtDate2;
	
	public DatePicker() {
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it

        return new DatePickerDialog(getActivity(), STYLE_NO_FRAME | STYLE_NO_TITLE, this, year, month, day);
    }
    
	@Override
	public void onDateChanged(android.widget.DatePicker view, int year,
			int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDateSet(android.widget.DatePicker view, int year,
			int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub
		
		
	}

}
