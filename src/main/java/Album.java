public class Album {

    //private final int album_id;
    //private final int artist_id;
    private final String artist;
    private final String title;
    private final int release_year;

    public Album(String artist, String title, int release_year) {
        this.artist = artist;
        this.title = title;
        this.release_year = release_year;
    }

//    public int getAlbum_id() {
//        return this.album_id;
//    }
//
//    public int getArtist_id() {
//        return this.artist_id;
//    }

    public String getTitle() {
        return this.title;
    }
}
