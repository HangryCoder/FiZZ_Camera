package camera.fizz5.com.cameramodule;

import android.widget.BaseAdapter;

public abstract class BaseSwipListAdapter extends BaseAdapter {

    public boolean getSwipEnableByPosition(int position){
        return true;
    }



}