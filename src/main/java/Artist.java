public class Artist {

    private final String name;
    //private final int artist_id;

    // Albums ?
    // Songs ?

    public Artist(String name) {
        //this.artist_id = artist_id;
        this.name = name;
    }

//    public int getArtist_id() {
//        return this.artist_id;
//    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "Artist: {" + this.getName() + "}";
    }
}
