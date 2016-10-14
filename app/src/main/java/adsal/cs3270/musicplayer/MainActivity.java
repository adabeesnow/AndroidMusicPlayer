package adsal.cs3270.musicplayer;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Holds TrackListFragment and ControlFragment.
 * Contains methods to facilitate inter-fragment communication.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        TrackListFragment tlf = new TrackListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.listContainer, tlf, "TLF")
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.controlsContainer, new ControlFragment(), "CF")
                .commit();
    }

    public void prev() {
        TrackListFragment tlf = (TrackListFragment) getSupportFragmentManager().findFragmentByTag("TLF");
        tlf.prev();
    }

    public void playPause() {
        TrackListFragment tlf = (TrackListFragment) getSupportFragmentManager().findFragmentByTag("TLF");
        tlf.playPause();
    }

    public void next() {
        TrackListFragment tlf = (TrackListFragment) getSupportFragmentManager().findFragmentByTag("TLF");
        tlf.next();
    }

    public void updateTextViews(String name, String artist, String album, String duration, int seekBarMax) {
        ControlFragment cf = (ControlFragment) getSupportFragmentManager().findFragmentByTag("CF");
        cf.updateTextViews(name, artist, album, duration, seekBarMax);
    }

    public void updateCurPos(int curPos, String curPosString) {
        ControlFragment cf = (ControlFragment) getSupportFragmentManager().findFragmentByTag("CF");
        cf.updateCurPos(curPos, curPosString);
    }

    public boolean serviceUp() {
        TrackListFragment tlf = (TrackListFragment) getSupportFragmentManager().findFragmentByTag("TLF");
        return tlf.serviceUp();
    }

    public void updateTrackProgress(int progress) {
        TrackListFragment tlf = (TrackListFragment) getSupportFragmentManager().findFragmentByTag("TLF");
        tlf.updateTrackProgress(progress);
    }

}
