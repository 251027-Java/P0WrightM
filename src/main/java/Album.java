public class Album {

    private final int album_id;
    private final int artist_id;
    private final String title;

    public Album(int album_id, int artist_id, String title) {
        this.album_id = album_id;
        this.artist_id = artist_id;
        this.title = title;
    }

    public int getAlbum_id() {
        return this.album_id;
    }

    public int getArtist_id() {
        return this.artist_id;
    }

    public String getTitle() {
        return this.title;
    }
}
