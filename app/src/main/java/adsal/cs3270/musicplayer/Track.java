package adsal.cs3270.musicplayer;


import android.net.Uri;

public class Track {

    private String trackName;
    private String trackArtistName;
    private String trackAlbumName;
    private String trackDuration;
    private String trackFullPath; // Is actually accessed.
    private Uri trackUri;
    private int trackId; // Is actually accessed.

    public Track() {}

/*    public Track(String trackName, String trackArtistName, String trackAlbumName,
                 String trackDuration, String trackFullPath, Uri trackUri, int trackId) {
        this.trackName = trackName;
        this.trackArtistName = trackArtistName;
        this.trackAlbumName = trackAlbumName;
        this.trackDuration = trackDuration;
        this.trackFullPath = trackFullPath;
        this.trackUri = trackUri;
        this.trackId = trackId;
    }*/


    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackArtistName() {
        return trackArtistName;
    }

    public void setTrackArtistName(String trackArtistName) {
        this.trackArtistName = trackArtistName;
    }

    public String getTrackAlbumName() {
        return trackAlbumName;
    }

    public void setTrackAlbumName(String trackAlbumName) {
        this.trackAlbumName = trackAlbumName;
    }

    public String getTrackDuration() {
        return trackDuration;
    }

    public void setTrackDuration(String trackDuration) {
        this.trackDuration = trackDuration;
    }

    /*public String getTrackFullPath() {
        return trackFullPath;
    }*/

    public void setTrackFullPath(String trackFullPath) {
        this.trackFullPath = trackFullPath;
    }

    public Uri getTrackUri() {
        return trackUri;
    }

    public void setTrackUri(Uri trackUri) {
        this.trackUri = trackUri;
    }

    /*public int getTrackId() {
        return trackId;
    }*/

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }
}
