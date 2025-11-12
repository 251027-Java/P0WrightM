public class Song {

    private final int song_id;
    private final int artist_id;
    private final int album_id;
    private final String title;
    private final double length;
    //Not sure if want to include embedding here?

    public Song(int song_id, int artist_id, int album_id, String title, double length) {
        this.song_id = song_id;
        this.artist_id = artist_id;
        this.album_id = album_id;
        this.title = title;
        this.length = length;
    }

    public int getSong_id() {
        return this.song_id;
    }

    public int getArtist_id() {
        return this.artist_id;
    }

    public int getAlbum_id() {
        return this.album_id;
    }

    public String getTitle() {
        return this.title;
    }

    public double getLength() {
        return this.length;
    }
}
