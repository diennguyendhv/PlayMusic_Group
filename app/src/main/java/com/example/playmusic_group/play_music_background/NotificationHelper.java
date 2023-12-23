package com.example.playmusic_group.play_music_background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.playmusic_group.R;
import com.example.playmusic_group.shared.context.ContextHelper;

public class NotificationHelper {

    public static final int MUSIC_NOTIFICATION_ID = 12345678;

    public static final String MUSIC_CHANNEL_ID = "music_channel_id";

    public static Notification getMusicNotificationBuilder(Context context, String nameMusic, boolean isPlaying) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_widget);
        remoteViews.setImageViewResource(R.id.ic_play, isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        remoteViews.setOnClickPendingIntent(R.id.ic_previous, MusicPendingIntentHelper.instance.getPendingIntent(context, MusicConfig.Actions.PREV_ACTION));
        remoteViews.setOnClickPendingIntent(R.id.ic_play, MusicPendingIntentHelper.instance.getPendingIntent(context, isPlaying ? MusicConfig.Actions.PAUSE_ACTION : MusicConfig.Actions.PLAY_ACTION));
        remoteViews.setOnClickPendingIntent(R.id.ic_next, MusicPendingIntentHelper.instance.getPendingIntent(context, MusicConfig.Actions.NEXT_ACTION));
        remoteViews.setTextViewText(R.id.nw_tv_description, nameMusic);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MUSIC_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Music Service")
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentText("Running Music Service")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCustomContentView(remoteViews);

        String name = context.getString(R.string.app_name) + MusicConfig.MUSIC_CHANNEL_NAME;
        NotificationChannel notificationChannel = new NotificationChannel(MUSIC_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setSound(null, null);
        notificationChannel.setDescription(MusicConfig.MUSIC_CHANNEL_NAME);
        ContextHelper.getNotificationManager(context).createNotificationChannel(notificationChannel);

        return builder.build();
    }
}
