package cz.tsystems.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import cz.tsystems.portablecheckin.R;

/**
 * Created by KUBO on 11. 1. 2015.
 */
public class SilhouetteCursorAdapter extends CursorAdapter {
    Context context;
    public SilhouetteCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_siluets, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        byte[] img = cursor.getBlob(cursor.getColumnIndex("img1"));
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        BitmapDrawable ob = new BitmapDrawable(context.getResources(), bitmap);
        ((ImageView) view.findViewById(R.id.ingSilhouette1)).setBackground(ob);

        img = cursor.getBlob(cursor.getColumnIndex("img3"));
        bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        ob = new BitmapDrawable(context.getResources(), bitmap);
        ((ImageView) view.findViewById(R.id.ingSilhouette2)).setBackground(ob);

        img = cursor.getBlob(cursor.getColumnIndex("img2"));
        bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        ob = new BitmapDrawable(context.getResources(), bitmap);
        ((ImageView) view.findViewById(R.id.ingSilhouette3)).setBackground(ob);

        img = cursor.getBlob(cursor.getColumnIndex("img4"));
        bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        ob = new BitmapDrawable(context.getResources(), bitmap);
        ((ImageView) view.findViewById(R.id.ingSilhouette4)).setBackground(ob);
    }
}
