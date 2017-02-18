package com.example.shami.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shami on 2/18/2017.
 */

public class GeoFenceTransitionIntentService extends IntentService {

    private static final String Log_tag="[DM]Shami "+GeoFenceTransitionIntentService.class.getSimpleName();

    public GeoFenceTransitionIntentService() {
        super(Log_tag);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofenceEvent=GeofencingEvent.fromIntent(intent);

        if(geofenceEvent.hasError())
        {
            Log.e(Log_tag,"The error code is"+geofenceEvent.getErrorCode());
            return;
        }
        int geofenceTransition=geofenceEvent.getGeofenceTransition();

        if(geofenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition==Geofence.GEOFENCE_TRANSITION_EXIT)
        {

            List<Geofence>  triggeringGeofences=geofenceEvent.getTriggeringGeofences();

            String geofenceTransitionDetails=getGeoFenceTransitionDetails(this,geofenceTransition,triggeringGeofences);

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.i(Log_tag, geofenceTransitionDetails);

        }


    }
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Entering location")
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }



    private String getGeoFenceTransitionDetails(Context context,int transition,List<Geofence> TrigeringGeofences)
    {
        String geoFenceTransitionString=getTransitionString(transition);

        ArrayList trigeringGeofencesIDsList=new ArrayList();

        for(Geofence geofence:TrigeringGeofences)
        {
            trigeringGeofencesIDsList.add(geofence.getRequestId());
        }

        String triggeringGeofenceIdsString= TextUtils.join(", ",trigeringGeofencesIDsList);

        return geoFenceTransitionString +": "+triggeringGeofenceIdsString;
    }

}
