package adsal.cs3270.musicplayer;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private MediaPlayer mediaPlayer;
    private Uri  trackUri; // Is actually accessed.
    private ArrayList<Track> trackList;
    private LocalBroadcastManager broadcaster;

    private final IBinder       trackBinder =      new PlayerBinder();
    private final String        ACTION_STOP =      "adsal.cs3270.MusicPlayer.STOP";
    private final String        ACTION_NEXT =      "adsal.cs3270.MusicPlayer.NEXT";
    private final String        ACTION_PREVIOUS =  "adsal.cs3270.MusicPlayer.PREVIOUS";
    private final String        ACTION_PAUSE=      "adsal.cs3270.MusicPlayer.PAUSE";
    private int                 TRACK_POS =                0;
    private int                 state =                    0;
    private static final int    STATE_PAUSED =             1;
    private static final int    STATE_PLAYING =            2;
    private static final int    REQUEST_CODE_PAUSE =       101;
    private static final int    REQUEST_CODE_PREVIOUS =    102;
    private static final int    REQUEST_CODE_NEXT =        103;
    private static final int    REQUEST_CODE_STOP =        104;
    static final public String  UPDATE_CUR_TRACK_INFO =    "adsal.cs3270.MusicPlayer.UPDATE";
    public static int           NOTIFICATION_ID =          11;

    private Notification.Builder notificationBuilder;
    private Notification mNotification;

    public class PlayerBinder extends Binder {
        public MediaPlayerService getService() {
            Log.d("test", "getService()");
            return MediaPlayerService.this;
        }
    }

    public void sendUpdate(String message) {
        Intent intent = new Intent(UPDATE_CUR_TRACK_INFO);
        if(message != null)
            intent.putExtra(UPDATE_CUR_TRACK_INFO, message);
        broadcaster.sendBroadcast(intent);
    }

    public int getPlayerDuration() {
        return mediaPlayer.getDuration();
    }

    public int getPlayerCurPos() {
        return mediaPlayer.getCurrentPosition();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("test", "onBind called");
        return trackBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        mediaPlayer = new MediaPlayer();
        initPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        notificationBuilder = new Notification.Builder(getApplicationContext());
    }

    private void initPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        sendUpdate(UPDATE_CUR_TRACK_INFO);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void startTrack(Uri trackUri, String trackName) {
        mediaPlayer.reset();
        state = STATE_PLAYING;
        this.trackUri = trackUri;
        try{
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MediaPlayerService", "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
        updateNotification(trackName);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.reset();
        try {
            if(TRACK_POS != trackList.size() - 1) {
                TRACK_POS++;
            } else
                TRACK_POS = 0;

            mediaPlayer.setDataSource(getApplicationContext(), trackList.get(TRACK_POS).getTrackUri());
        } catch (Exception e) {
            Log.e("MediaPlayerService", "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
    }

    public String getTrackName() {
        return trackList.get(TRACK_POS).getTrackName();
    }

    public String getArtistName() {
        return trackList.get(TRACK_POS).getTrackArtistName();
    }

    public String getAlbumName() {
        return trackList.get(TRACK_POS).getTrackAlbumName();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String action = intent.getAction();
            if(!TextUtils.isEmpty(action)) {
                switch (action) {
                    case ACTION_PAUSE:
                        playPauseTrack();
                        break;
                    case ACTION_NEXT:
                        nextTrack();
                        break;
                    case ACTION_PREVIOUS:
                        previousTrack();
                        break;
                    case ACTION_STOP:
                        stopTrack();
                        stopSelf();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void setSelectedTrack(int pos, int notification_id) {
        TRACK_POS = pos;
        NOTIFICATION_ID = notification_id;
        setTrackUri(trackList.get(TRACK_POS).getTrackUri());
        showNotification();
        startTrack(trackList.get(TRACK_POS).getTrackUri(),
                trackList.get(TRACK_POS).getTrackName());
    }

    public void setTrackList(ArrayList<Track> trackList) {this.trackList = trackList;}

    public void showNotification() {
        PendingIntent pendingIntent;
        Intent intent;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_mediacontroller);

        notificationView.setTextViewText(R.id.notify_song_name, trackList.get(TRACK_POS).getTrackName());

        intent = new Intent(ACTION_STOP);
        pendingIntent = PendingIntent.getService(getBaseContext(), REQUEST_CODE_STOP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.notify_btn_stop, pendingIntent);

        intent = new Intent(ACTION_PAUSE);
        pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_PAUSE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.notify_btn_pause, pendingIntent);

        intent = new Intent(ACTION_PREVIOUS);
        pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_PREVIOUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.notify_btn_previous, pendingIntent);

        intent = new Intent(ACTION_NEXT);
        pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_NEXT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.notify_btn_next, pendingIntent);

        mNotification = notificationBuilder
                .setSmallIcon(R.drawable.appicon).setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContent(notificationView)
                .setDefaults(Notification.FLAG_NO_CLEAR)
                .build();
        notificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    private void updateNotification(String songName) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification.contentView.setTextViewText(R.id.notify_song_name, songName);

        notificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public void setTrackUri(Uri uri) {
        this.trackUri = uri;
    }

    public void playPauseTrack() {
        if (state == STATE_PAUSED) {
            state = STATE_PLAYING;
            mediaPlayer.start();
        } else {
            state = STATE_PAUSED;
            mediaPlayer.pause();
        }
    }

    public void stopTrack() {
        mediaPlayer.stop();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void previousTrack() {
        if(TRACK_POS - 1 >= 0) {
            startTrack(trackList.get(TRACK_POS - 1).getTrackUri(),
                    trackList.get(TRACK_POS - 1).getTrackName());
            TRACK_POS--;
        } else {
            startTrack(trackList.get(TRACK_POS).getTrackUri(),
                    trackList.get(TRACK_POS).getTrackName());
        }
    }

    public void nextTrack() {
        if((trackList.size() > TRACK_POS + 1)) {
            startTrack(trackList.get(TRACK_POS + 1).getTrackUri(),
                    trackList.get(TRACK_POS + 1).getTrackName());
            TRACK_POS++;
        } else {
            startTrack(trackList.get(TRACK_POS).getTrackUri(),
                    trackList.get(TRACK_POS).getTrackName());
        }
    }

    public void updateTrackProgress(int progress) {
        mediaPlayer.seekTo(progress);
    }

    public void onDestroy() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }
}
