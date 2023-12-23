package com.example.playmusic_group.play_music_background;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MusicPendingIntentHelper {

    public static MusicPendingIntentHelper instance = new MusicPendingIntentHelper();

    public PendingIntent getPendingIntent(Context context, String musicAction) {
        Intent playIntent = new Intent(musicAction);
        return PendingIntent.getBroadcast(
                context, 0, playIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
