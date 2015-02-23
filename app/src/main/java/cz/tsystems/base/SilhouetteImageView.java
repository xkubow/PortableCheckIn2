package cz.tsystems.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cz.tsystems.data.PortableCheckin;

/**
 * Created by kubisj on 23.1.2015.
 */

public class SilhouetteImageView extends ImageView {
    Context context;
    PortableCheckin app;
    SilhouetteImgListener onSizeChanget = null;

    public SilhouetteImageView(Context context) {
        super(context);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SilhouetteImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public SilhouetteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SilhouetteImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        app = (PortableCheckin)context.getApplicationContext();
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        if(PortableCheckin.silhuetteSize != null) {
            PortableCheckin.silhuetteSize.setHeight(h);
            PortableCheckin.silhuetteSize.setWidth(w);
        }
        if(onSizeChanget != null)
            onSizeChanget.onSizeChanged(w,h);
    }

    public void setImageListener(SilhouetteImgListener imgListener) {
        onSizeChanget = imgListener;
    }
}
