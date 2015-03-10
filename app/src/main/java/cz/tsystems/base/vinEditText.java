package cz.tsystems.base;

import android.content.Context;
//import android.support.v7.internal.widget.TintEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.Locale;

import cz.tsystems.data.PortableCheckin;

public class vinEditText extends EditText {
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
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if(offTextChange)
			return;
        if(lengthBefore>lengthAfter)
            return;
		StringBuilder sb = new StringBuilder(this.getText().toString().replace(" ", "").toUpperCase(Locale.US));
		
		int len = sb.length();
//		Log.d("TEXT", sb.toString() + ", len:" + len);		
		if(len > 17)
			sb.delete(17, len);
//		Log.d("TEXT", sb.toString() + ", len:" + sb.length());
		if(len >= 3)
			sb.insert(3, ' ');
        if(len >= 7)
			sb.insert(7, ' ');
        if(len >= 10)
			sb.insert(10, ' ');
        if(len >= 14)
			sb.insert(14, ' ');
		offTextChange = true;
		this.setText(sb.toString());
        setSelection(sb.length());
		offTextChange = false;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            this.clearFocus();
        }
        return super.dispatchKeyEvent(event);
    }

}
