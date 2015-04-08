package com.albertomoral.pebblight.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PebbleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LightService.toggleLight(context);
        Log.i("PebbleReceiver", "Light toggle request received");
    }

}
