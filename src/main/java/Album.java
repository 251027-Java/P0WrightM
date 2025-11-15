public class Album {

    //private final int album_id;
    //private final int artist_id;
    private final String[] artists;
    private final String title;
    private final int release_year;

    public Album(String[] artists, String title, int release_year) {
        this.artists = artists;
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

    public String[] getArtists() { return this.artists; }

    public String getTitle() {
        return this.title;
    }

    public int getReleaseYear() {
        return this.release_year;
    }

    public String toString() {
        return "Album: " + this.getTitle() + " (" + this.getReleaseYear() + ")";
    }
}
