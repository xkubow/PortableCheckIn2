package cz.tsystems.base;

import android.content.Context;
import android.support.v7.internal.widget.TintEditText;
import android.util.AttributeSet;

import java.util.Locale;

public class vinEditText extends TintEditText {
	private boolean offTextChange;

    public vinEditText(Context context) {
        super(context);
        init();
    }

    public vinEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public vinEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
            // set your input filter here
    	offTextChange = false;
    }
	
	
	
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		// TODO Auto-generated method stub
		if(offTextChange)
			return;
		StringBuilder sb = new StringBuilder(this.getText().toString().replace(" ", "").toUpperCase(Locale.US));
		
		int len = sb.length();
//		Log.d("TEXT", sb.toString() + ", len:" + len);		
		if(len > 17)
			sb.delete(17, len);
//		Log.d("TEXT", sb.toString() + ", len:" + sb.length());
		if(len >= 17)
		{
			sb.insert(3, ' ');
			sb.insert(7, ' ');
			sb.insert(10, ' ');
			sb.insert(14, ' ');
		}
		offTextChange = true;
		this.setText(sb.toString());
		offTextChange = false;
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}

}
