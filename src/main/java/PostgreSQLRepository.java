import java.sql.*;
import java.util.List;

//Want to use pgvector to contain lyric vectors inside postgresql database
public class PostgreSQLRepository implements IRepository, ISongSearcher {

    private String POSTGRE_URL = "jdbc:postgresql://localhost:5432/musicappdb";
    private String POSTGRE_USER = "postgres";
    private String POSTGRE_PW = "postgres";
    private IEmbedder embedder;
    private Connection connection;

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
        try{
            this.connection = DriverManager.getConnection(POSTGRE_URL, POSTGRE_USER, POSTGRE_PW);

            try ( Statement stmt = connection.createStatement() ) {
                String sql =
                        "CREATE EXTENSION IF NOT EXISTS vector; " + "CREATE SCHEMA IF NOT EXISTS Music;" +
                                "CREATE SEQUENCE IF NOT EXISTS \"artist_artist_id_seq\"" +
                                " INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;" +
                                "CREATE SEQUENCE IF NOT EXISTS \"album_album_id_seq\"" +
                                " INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;" +
                                "CREATE SEQUENCE IF NOT EXISTS \"song_song_id_seq\"" +
                                " INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;" +
                                "CREATE TABLE IF NOT EXISTS Music.Artist ( " +
                                "artist_id int DEFAULT nextval('artist_artist_id_seq'::regclass) NOT NULL, " +
                                "name VARCHAR(120), " +
                                "CONSTRAINT pk_artist PRIMARY KEY (artist_id));" +
                                "CREATE TABLE IF NOT EXISTS Music.Album ( " +
                                "album_id int DEFAULT nextval('album_album_id_seq'::regclass) NOT NULL, " +
                                "title VARCHAR(120), " +
                                "artist_id int, " +
                                "release_year int, " +
                                "CONSTRAINT pk_album PRIMARY KEY (album_id), " +
                                "CONSTRAINT fk_album_artist_id FOREIGN KEY (artist_id) " +
                                "  REFERENCES Music.Artist (artist_id) ON DELETE NO ACTION ON UPDATE NO ACTION);" +
                                "CREATE TABLE IF NOT EXISTS Music.Song ( " +
                                "song_id int DEFAULT nextval('song_song_id_seq'::regclass) NOT NULL, " +
                                "title VARCHAR(120), " +
                                "artist_id int, " +
                                "album_id int, " +
                                "duration int NOT NULL, " +
                                "lyrics TEXT, " +
                                "embedding vector(100)," +
                                "CONSTRAINT pk_song PRIMARY KEY (song_id), " +
                                "CONSTRAINT fk_song_artist_id FOREIGN KEY (artist_id) " +
                                "  REFERENCES Music.Artist (artist_id) ON DELETE NO ACTION ON UPDATE NO ACTION, " +
                                "CONSTRAINT fk_song_album_id FOREIGN KEY (album_id) " +
                                "  REFERENCES Music.Album (album_id) ON DELETE NO ACTION ON UPDATE NO ACTION);";
                stmt.execute(sql);
                System.out.println("Successful connection to PostgreSQL Database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int createArtist(Artist artist) {
        return 0;
    }

    private int getArtistId(String name) {
        String sql =
                "SELECT artist_id FROM Music.Artist WHERE name like \"%?%\";";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            int id = -1;
            while (rs.next()) {
                id = rs.getInt("artist_id"); // By column name
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int createAlbum(Album album) {
        return 0;
    }

    private int getAlbumId(String title) {
        String sql =
                "SELECT album_id FROM Music.Album WHERE title like \"%?%\";";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            int id = -1;
            while (rs.next()) {
                id = rs.getInt("album_id"); // By column name
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int createSong(Song song) {
        return 0;
    }

    //Plan on storing the vectors inside postgre, so to search we should implement songsearcher.
    @Override
    public List<Song> getSimilarSongs(List<Double> embedding) {
        return List.of();
    }
}
