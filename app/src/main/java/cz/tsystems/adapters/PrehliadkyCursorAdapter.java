package cz.tsystems.adapters;

import cz.tsystems.data.DMPrehliadky;
import cz.tsystems.portablecheckin.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.TextView;

public class PrehliadkyCursorAdapter  extends ResourceCursorAdapter {

	public PrehliadkyCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c, true);
	}

	@Override
	public void bindView(View view, Context ctx, Cursor c) {
		DMPrehliadky prehliadky = DMPrehliadky.fromCursor(ctx, c);
		
		TextView text = (TextView) view.findViewById(R.id.lblPrehliadka);
		text.setText(prehliadky.getText());		
		
	}
	
	
}
