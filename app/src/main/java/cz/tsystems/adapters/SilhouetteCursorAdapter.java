package cz.tsystems.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import cz.tsystems.grids.Siluets;
import cz.tsystems.portablecheckin.R;

/**
 * Created by KUBO on 11. 1. 2015.
 */
public class SilhouetteCursorAdapter extends SimpleCursorAdapter {
    Context context;
    LayoutInflater inflater;
    byte[] img;
    Bitmap bitmap;
    Bitmap tempBitmap;
    Canvas canvas;
    Paint p = new Paint();
    Matrix scaleMatrix = new Matrix();

    private class ViewHolder {
        ImageView img1, img2, img3, img4;

        ViewHolder(View v) {
            img1 = (ImageView)v.findViewById(R.id.ingSilhouette1);
            img2 = (ImageView)v.findViewById(R.id.ingSilhouette2);
            img3 = (ImageView)v.findViewById(R.id.ingSilhouette3);
            img4 = (ImageView)v.findViewById(R.id.ingSilhouette4);
        }
    }

    public SilhouetteCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        scaleMatrix.reset();
        scaleMatrix.setScale(0.3f,0.3f);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View vView =  inflater.inflate(R.layout.item_siluets, parent, false);
        ViewHolder vh = new ViewHolder(vView);
        vView.setTag(vh);

        // no need to bind data here. you do in later
        return vView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder vh = (ViewHolder) view.getTag();

//        final int imgWidth = (view.getWidth() - 5*16)/4;
/*        if(tempBitmap == null) {
            tempBitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.RGB_565);
            tempBitmap.eraseColor(Color.WHITE);
            canvas = new Canvas(tempBitmap);
        }
        p.setAntiAlias(true);
        p.setFilterBitmap(true);*/
        view.setBackgroundColor(Color.WHITE);
        this.img = cursor.getBlob(cursor.getColumnIndex("img1"));
        this.bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//        canvas.drawBitmap(this.bitmap,scaleMatrix, p);
//        this.img = new BitmapDrawable(context.getResources(), bitmap);
        vh.img1.setImageBitmap(this.bitmap);

        this.img = cursor.getBlob(cursor.getColumnIndex("img3"));
        this.bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//        this.ob = new BitmapDrawable(context.getResources(), bitmap);
        vh.img2.setImageBitmap(this.bitmap);

        this.img = cursor.getBlob(cursor.getColumnIndex("img2"));
        this.bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//        this.ob = new BitmapDrawable(context.getResources(), bitmap);
        vh.img3.setImageBitmap(this.bitmap);

        this.img = cursor.getBlob(cursor.getColumnIndex("img4"));
        this.bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//        this.ob = new BitmapDrawable(context.getResources(), bitmap);
        vh.img4.setImageBitmap(this.bitmap);
    }
}
