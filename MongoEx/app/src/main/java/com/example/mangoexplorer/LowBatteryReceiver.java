package com.example.mangoexplorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class LowBatteryReceiver extends BroadcastReceiver {
    private static boolean isToastShown = false;//to prevent error that toast always appear

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPercentage = batteryLevel * 100 / (float)batteryScale;
            if (batteryPercentage <= 20) {
                if(!isToastShown) {
                    isToastShown = true;
                    Utils.better_performance = true;
                    Toast.makeText(context, "Low battery \n Performance mode applied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}