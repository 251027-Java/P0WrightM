import java.util.List;

//Want to use pgvector to contain lyric vectors inside postgresql database
public class PostgreSQLRepository implements IRepository, ISongSearcher {

    private String POSTGRE_URL = "";
    private String POSTGRE_USER = "";
    private String POSTGRE_PW = "";
    private IEmbedder embedder;

    public PostgreSQLRepository(IEmbedder embedder) {
        this.constructor_helper(embedder);
    }

    public PostgreSQLRepository(IEmbedder embedder, String url, String user, String password) {
        this.POSTGRE_URL = url;
        this.POSTGRE_USER = user;
        this.POSTGRE_PW = password;
        this.constructor_helper(embedder);
    }

    private void constructor_helper(IEmbedder embedder) {
        // do stuff
    }

    @Override
    public int createArtist(Artist artist) {
        return 0;
    }

    @Override
    public int createAlbum(Album album) {
        return 0;
    }

    @Override
    public int createSong(Song song) {
        return 0;
    }

    //Plan on storing the vectors inside postgre, so to search we should implement songsearcher.
    @Override
    public List<Song> getSimilarSongs(List<Float> embedding) {
        return List.of();
    }
}
