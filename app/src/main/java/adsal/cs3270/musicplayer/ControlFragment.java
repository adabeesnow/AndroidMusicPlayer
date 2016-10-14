package adsal.cs3270.musicplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment {

    private SeekBar     seekbar;
    //private ImageButton btnShuffle;
    private boolean     startedPlaying;
    private boolean     playing;
    private TextView    txvName;
    private TextView    txvArtist;
    private TextView    txvAlbum;
    private TextView    txvCurPos;
    private TextView    txvDuration;
    private ImageButton btnPlayPause;

    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_control, container, false);

        ImageButton btnPrev = (ImageButton) v.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity) getActivity();
                if(startedPlaying)
                    ma.prev();
            }
        });

        btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity) getActivity();
                if(startedPlaying) {
                    ma.playPause();
                    if (playing)
                        setPlayIcon();
                    else
                        setPauseIcon();
                } else {
                    Toast.makeText(getActivity(), "Please select a song.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton btnNext = (ImageButton) v.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity) getActivity();
                if(startedPlaying)
                    ma.next();
            }
        });

        txvName = (TextView) v.findViewById(R.id.txvName);
        txvArtist = (TextView) v.findViewById(R.id.txvArtist);
        txvAlbum = (TextView) v.findViewById(R.id.txvAlbum);
        txvCurPos = (TextView) v.findViewById(R.id.txvCurPos);
        txvDuration = (TextView) v.findViewById(R.id.txvDuration);
        seekbar = (SeekBar) v.findViewById(R.id.seekbar);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainActivity ma = (MainActivity) getActivity();
                if(ma.serviceUp() && fromUser && startedPlaying){
                    ma.updateTrackProgress(progress);
                }
            }
        });

        startedPlaying = false;
        playing = false;

        return v;
    }

    public void updateTextViews(String name, String artist, String album, String duration, int seekBarMax) {
        txvName.setText(name);
        txvArtist.setText(artist);
        txvAlbum.setText(album);
        txvDuration.setText(duration);
        seekbar.setMax(seekBarMax);
        startedPlaying = true;
        setPauseIcon();
    }

    public void updateCurPos(int curPos, String curPosString) {
        seekbar.setProgress(curPos);
        txvCurPos.setText(curPosString);
    }

    public void setPlayIcon() {
        btnPlayPause.setImageResource(R.drawable.btn_play_icon_selector);
        playing = false;
    }

    public void setPauseIcon() {
        btnPlayPause.setImageResource(R.drawable.btn_pause_icon_selector);
        playing = true;
    }

}
