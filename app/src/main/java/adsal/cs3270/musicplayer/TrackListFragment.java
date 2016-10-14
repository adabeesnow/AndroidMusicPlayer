package adsal.cs3270.musicplayer;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * A {@link Fragment} subclass.
 * Contains the tracklist; is placed in MainActivity.
 */
public class TrackListFragment extends ListFragment {

    private String[]            STAR = {"*"};
    private MediaPlayerService  mediaPlayerService;
    private ArrayList<Track>    trackList;
    private Intent              playIntent;
    private BroadcastReceiver   receiver;
    private CountDownTimer      cdt;

    public TrackListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        trackList = listAllTracks();
        TrackListAdapter trackListAdapter = new TrackListAdapter(getContext(), listAllTracks());
        setListAdapter(trackListAdapter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTextViews();
            }
        };

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private ServiceConnection mediaPlayerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.PlayerBinder binder = (MediaPlayerService.PlayerBinder) service;
            //get service
            mediaPlayerService = binder.getService();
            mediaPlayerService.setTrackList(trackList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("test", "ListView position:" + position + " - id:" + id);
        mediaPlayerService.setSelectedTrack(position,
                MediaPlayerService.NOTIFICATION_ID);
    }



    private ArrayList<Track> listAllTracks() {
        Cursor cursor;
        ArrayList<Track> trackList = new ArrayList<>();
        Uri allTracksUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        if(isSdPresent()) {
            cursor = getContext().getContentResolver().query(allTracksUri, STAR, selection, null, null);
            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        Track track = new Track();

                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String[] res = data.split("\\.");
                        track.setTrackName(res[0]);

                        track.setTrackId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                        track.setTrackFullPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                        track.setTrackArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                        track.setTrackAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                        track.setTrackUri(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                                )));
                        String duration = getDuration(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                        track.setTrackDuration(duration);

                        trackList.add(track);
                    } while (cursor.moveToNext());
                    return trackList;
                }
                cursor.close();
            }
        }
        return trackList;
    }

    private static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private static String getDuration (long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than or equal to zero");
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);


        return (minutes < 10 ? "0" + minutes : minutes) +
                ":" +
                (seconds < 10 ? "0" + seconds : seconds);
    }

    public void prev() {
        mediaPlayerService.previousTrack();
    }

    public void playPause() {
        mediaPlayerService.playPauseTrack();
    }

    public void next() {
        mediaPlayerService.nextTrack();
    }

    public boolean serviceUp() {
        return mediaPlayerService != null;
    }

    public void updateTrackProgress(int progress) {
        mediaPlayerService.updateTrackProgress(progress);
    }

    public void updateTextViews() {
        String name = mediaPlayerService.getTrackName();
        String artist = mediaPlayerService.getArtistName();
        String album = mediaPlayerService.getAlbumName();
        String duration = getDuration((mediaPlayerService.getPlayerDuration()));
        int seekBarMax = mediaPlayerService.getPlayerDuration();

        cdt = new CountDownTimer(mediaPlayerService.getPlayerDuration(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                MainActivity ma = (MainActivity) getActivity();
                ma.updateCurPos(mediaPlayerService.getPlayerCurPos(), getDuration(mediaPlayerService.getPlayerCurPos()));
            }

            @Override
            public void onFinish() {
                cdt.start();
            }
        };

        cdt.start();

        MainActivity ma = (MainActivity) getActivity();
        ma.updateTextViews(name, artist, album, duration, seekBarMax);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(getContext(), MediaPlayerService.class);
            getContext().bindService(playIntent, mediaPlayerConnection, Context.BIND_AUTO_CREATE);
            getContext().startService(playIntent);
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(MediaPlayerService.UPDATE_CUR_TRACK_INFO)
        );
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mediaPlayerService.stopService(playIntent);
        mediaPlayerService = null;
        super.onDestroy();
    }

}
