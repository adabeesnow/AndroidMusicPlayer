package adsal.cs3270.musicplayer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TrackListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Track> trackList;

    public TrackListAdapter(Context context, ArrayList<Track> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @Override
    public int getCount() {
        return trackList.size();
    }

    @Override
    public Object getItem(int position) {
        return trackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            //convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null);
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_track, parent, false);
        }

        TextView txvTrackName = (TextView) convertView.findViewById(R.id.txvTrackName);
        TextView txvArtistName = (TextView) convertView.findViewById(R.id.txvArtistName);
        TextView txvDuration = (TextView) convertView.findViewById(R.id.txvDuration);

        txvTrackName.setText(trackList.get(position).getTrackName());
        txvArtistName.setText(trackList.get(position).getTrackArtistName());
        txvDuration.setText(trackList.get(position).getTrackDuration());

        return convertView;
    }

/*    public void setTrackList(ArrayList<Track> trackList) {
        this.trackList = trackList;
        this.notifyDataSetChanged();
    }*/
}
