package com.example.sumit.todo3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class AlarmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        //Toast.makeText(context,"Alarm Received",Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder mbuilder  = new NotificationCompat.Builder(context).setAutoCancel(true).setContentText("Notified").setContentTitle("Alarm notice").setSmallIcon(android.R.drawable.ic_menu_report_image);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mbuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,mbuilder.build());


    }
}
