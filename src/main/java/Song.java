public class Song {

    //private final int song_id;
    //private final int artist_id;
    private final String artist;
    //private final int album_id;
    private final String album;
    private final String title;
    private final double length;
    private final String lyrics;
    //Not sure if want to include embedding here?

    public Song(String artist, String album, String title, double length, String lyrics) {
        //this.song_id = song_id;
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.length = length;
        this.lyrics = lyrics;
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

    public String getArtist() {
        return this.artist;
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

    @Override
    public String toString() {
        return String.format("Song: \"%s\", Artist: %s, Album: %s, Duration: %.2f",
                this.title, this.artist, this.album, this.length);
    }
}
