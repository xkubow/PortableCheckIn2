package cz.tsystems.base;

import android.app.Fragment;
import android.content.Intent;
//import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

	public abstract void showData(Intent intent);
	public abstract void updateData(Intent intent);	
}
