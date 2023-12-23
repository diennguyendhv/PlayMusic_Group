package com.example.playmusic_group.play_music_background;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.playmusic_group.R;

public class MusicService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            String title = intent.getStringExtra(MusicConfig.MUSIC_TITLE_KEY);
            String artists = intent.getStringExtra(MusicConfig.MUSIC_ARTISTS_KEY);

            switch (action) {
                case MusicConfig.Actions.CANCEL_ACTION -> {
                    stopSelf();
                }
                case MusicConfig.Actions.PLAY_ACTION, MusicConfig.Actions.NEXT_ACTION, MusicConfig.Actions.PREV_ACTION -> {
                    showPlayNotification(title, artists);
                }
                case MusicConfig.Actions.PAUSE_ACTION -> {
                    showPauseNotification(title, artists);
                }
            }
        }
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void showPlayNotification(String title, String artists) {
        Notification notification = NotificationHelper.getMusicNotificationBuilder(getApplicationContext(), title +" | " + artists, true);
        startForeground(NotificationHelper.MUSIC_NOTIFICATION_ID, notification);
    }

    void showPauseNotification(String title, String artists ) {
        Notification notification = NotificationHelper.getMusicNotificationBuilder(getApplicationContext(), title +" | " + artists, false);
        startForeground(NotificationHelper.MUSIC_NOTIFICATION_ID, notification);
    }
}
