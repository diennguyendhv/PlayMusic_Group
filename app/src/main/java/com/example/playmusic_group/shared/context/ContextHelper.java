package com.example.playmusic_group.shared.context;

import android.app.NotificationManager;
import android.content.Context;

public class ContextHelper {
    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
