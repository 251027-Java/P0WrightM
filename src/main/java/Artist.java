public class Artist {

    private final String name;
    private final int artist_id;

    // Albums ?
    // Songs ?

    public Artist(int artist_id, String name) {
        this.artist_id = artist_id;
        this.name = name;
    }

    public int getArtist_id() {
        return this.artist_id;
    }

    public String getName() {
        return this.name;
    }
}
