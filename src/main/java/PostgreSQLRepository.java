import java.util.List;

//Want to use pgvector to contain lyric vectors inside postgresql database
public class PostgreSQLRepository implements IRepository, ISongSearcher {

    private String POSTGRE_URL = "";
    private String POSTGRE_USER = "";
    private String POSTGRE_PW = "";

    public PostgreSQLRepository() {
        this.constructor_helper();
    }

    public PostgreSQLRepository(String url, String user, String password) {
        this.POSTGRE_URL = url;
        this.POSTGRE_USER = user;
        this.POSTGRE_PW = password;
        this.constructor_helper();
    }

    private void constructor_helper() {
        // do stuff
    }

    @Override
    public void createArtist(Artist artist) {

    }

    @Override
    public void createAlbum(Album album) {

    }

    @Override
    public void createSong(Song song) {

    }

    //Plan on storing the vectors inside postgre, so to search we should implement songsearcher.
    @Override
    public List<Song> getSimilarSongs(List<Float> embedding) {
        return List.of();
    }
}
