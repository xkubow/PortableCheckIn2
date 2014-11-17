package cz.tsystems.model;

import android.content.Context;

public class Model {
	private Context mContext;

	public Model(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public Context getContext() {
		return mContext;
	}
}
