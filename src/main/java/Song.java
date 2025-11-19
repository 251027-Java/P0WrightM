import java.util.Arrays;

public class Song {

    //private final int song_id;
    //private final int artist_id;
    private final String[] artists;
    //private final int album_id;
    private final String album;
    private final String title;
    private final double length;
    private final String lyrics;
    private final int release_year;
    //Not sure if want to include embedding here?

    public Song(String[] artists, String album, int release_year, String title, double length, String lyrics) {
        //this.song_id = song_id;
        this.artists = artists;
        this.album = album;
        this.title = title;
        this.length = length;
        this.lyrics = lyrics;
        this.release_year = release_year;
    }


//    public int getSong_id() {
//        return this.song_id;
//    }

//    public int getArtist_id() {
//        return this.artist_id;
//    }
//
//    public int getAlbum_id() {
//        return this.album_id;
//    }

    public String[] getArtists() {
        return this.artists;
    }

    public String getAlbum() {
        return this.album;
    }

    public String getTitle() {
        return this.title;
    }

    public double getLength() {
        return this.length;
    }

    public String getLyrics() {
        return this.lyrics;
    }

    public int getRelease_year() {
        return this.release_year;
    }

    @Override
    public String toString() {
        return String.format("Song: \"%s\", Artist: %s, Album: %s (%d), Duration: %.2f",
                this.title, Arrays.toString(this.artists), this.album, this.release_year, this.length);
    }
}
