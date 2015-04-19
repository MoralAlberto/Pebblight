package com.albertomoral.pebblight.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class PebbleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        UUID uuid = (UUID) intent.getExtras().getSerializable(Constants.APP_UUID);
        if (!uuid.equals(Constants.PEBBLIGHT_UUID))
            return;
        LightService.toggleLight(context);
        Log.i("PebbleReceiver", "Light toggle request received");
    }

}
