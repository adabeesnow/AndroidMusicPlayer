package adsal.cs3270.musicplayer;


import android.net.Uri;

/**
 * Container class for track metadata.
 */
class Track {

    private String  trackName;
    private String  trackArtistName;
    private String  trackAlbumName;
    private String  trackDuration;
    private String  trackFullPath;
    private Uri     trackUri;
    private int     trackId;

    Track() {}


    String getTrackName() {
        return trackName;
    }

    void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    String getTrackArtistName() {
        return trackArtistName;
    }

    void setTrackArtistName(String trackArtistName) {
        this.trackArtistName = trackArtistName;
    }

    String getTrackAlbumName() {
        return trackAlbumName;
    }

    void setTrackAlbumName(String trackAlbumName) {
        this.trackAlbumName = trackAlbumName;
    }

    String getTrackDuration() {
        return trackDuration;
    }

    void setTrackDuration(String trackDuration) {
        this.trackDuration = trackDuration;
    }

    void setTrackFullPath(String trackFullPath) {
        this.trackFullPath = trackFullPath;
    }

    Uri getTrackUri() {
        return trackUri;
    }

    void setTrackUri(Uri trackUri) {
        this.trackUri = trackUri;
    }

    void setTrackId(int trackId) {
        this.trackId = trackId;
    }
}
