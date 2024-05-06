package com.example.mangoexplorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class chargingReceiver extends BroadcastReceiver {
    private static boolean isToastShown = false;//to prevent error that toast always appear

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging) {
            if(!isToastShown) {
                Utils.better_performance = false;
                Toast.makeText(context, "Charging \n Performance mode disapplied.", Toast.LENGTH_SHORT).show();
                isToastShown=true;
            }
        }
    }
}