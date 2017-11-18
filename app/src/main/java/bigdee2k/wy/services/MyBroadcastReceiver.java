package bigdee2k.wy.services;

/**
 * Created by Pengqi on 11/18/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, FirebaseNotificationService.class);
        context.startService(startServiceIntent);
    }
}