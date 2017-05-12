package com.youyi.weigan.moudul;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.beans.Mag;
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.Pulse;

import java.util.ArrayList;

/**
 * Created by user on 2017/4/5.
 */
public class ControlDeviceImp {
//    private final Intent intentResult;
    private Context context;
    private Toast toast;

    public ControlDeviceImp(Context context) {
        this.context = context.getApplicationContext();
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
//        intentResult = new Intent(context, SearchResultActivity.class);
    }

/*    public void searchPulseHis(ArrayList<Pulse> pulseArrayList) {
        intentResult.putExtra("instruct", "PulseHis");
        intentResult.putParcelableArrayListExtra("pulseArrayList", pulseArrayList);
        context.startActivity(intentResult);
    }

    public void searchAOG(ArrayList<GravA> gravAArrayList) {
        intentResult.putExtra("instruct", "AOGHis");
        intentResult.putParcelableArrayListExtra("gravAArrayList", gravAArrayList);
        context.startActivity(intentResult);

    }

    public void searchMagnetism(ArrayList<Mag> magArrayList) {
        intentResult.putExtra("instruct", "Mag");
        intentResult.putParcelableArrayListExtra("magArrayList", magArrayList);
        context.startActivity(intentResult);
    }

    public void searchPalstance(ArrayList<AngV> angVArrayList) {
        intentResult.putExtra("instruct", "AngV");
        intentResult.putParcelableArrayListExtra("angVArrayList", angVArrayList);
        context.startActivity(intentResult);
    }

    public void searchPressure(ArrayList<Pressure> pressureArrayList) {
        intentResult.putExtra("instruct", "Pressure");
        intentResult.putParcelableArrayListExtra("pressureArrayList", pressureArrayList);
        context.startActivity(intentResult);
    }*/

    public void showToast(String str) {
        if (toast.getView().getParent() != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.show();
    }

}
