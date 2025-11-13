import java.sql.*;
import java.util.List;
import java.util.Map;

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
        this.embedder = embedder;
        try{
            this.connection = DriverManager.getConnection(POSTGRE_URL, POSTGRE_USER, POSTGRE_PW);

            try ( Statement stmt = connection.createStatement() ) {
                String sql =
                        "CREATE EXTENSION IF NOT EXISTS vector;" +
                                "CREATE SCHEMA IF NOT EXISTS Music;" +
                                "CREATE SEQUENCE IF NOT EXISTS \"artist_artist_id_seq\" " +
                                " INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;" +
                                "CREATE SEQUENCE IF NOT EXISTS \"album_album_id_seq\" " +
                                " INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;" +
                                "CREATE SEQUENCE IF NOT EXISTS \"song_song_id_seq\" " +
                                " INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;" +
                                "CREATE TABLE IF NOT EXISTS Music.Artist (  " +
                                "    artist_id int DEFAULT nextval('artist_artist_id_seq'::regclass) NOT NULL," +
                                "    name VARCHAR(120) UNIQUE," +
                                "    CONSTRAINT pk_artist PRIMARY KEY (artist_id));" +
                                "CREATE TABLE IF NOT EXISTS Music.Album (  " +
                                "    album_id int DEFAULT nextval('album_album_id_seq'::regclass) NOT NULL," +
                                "    title VARCHAR(120)," +
                                "    release_year int," +
                                "    CONSTRAINT pk_album PRIMARY KEY (album_id)," +
                                "    CONSTRAINT unique_album_per_album_artist_year UNIQUE (title, release_year));" +
                                "CREATE TABLE IF NOT EXISTS Music.Artist_Album (" +
                                "    artist_id int," +
                                "    album_id int," +
                                "    CONSTRAINT pk_artist_album PRIMARY KEY (artist_id, album_id)," +
                                "    CONSTRAINT fk_artist_album_artist_id FOREIGN KEY (artist_id)" +
                                "        REFERENCES Music.Artist (artist_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                                "    CONSTRAINT fk_artist_album_album_id FOREIGN KEY (album_id)" +
                                "        REFERENCES Music.Album (album_id) ON DELETE NO ACTION ON UPDATE NO ACTION);" +
                                "CREATE TABLE IF NOT EXISTS Music.Song (  " +
                                "    song_id int DEFAULT nextval('song_song_id_seq'::regclass) NOT NULL," +
                                "    title VARCHAR(120)," +
                                "    album_id int," +
                                "    duration decimal NOT NULL," +
                                "    lyrics TEXT," +
                                "    embedding vector(100)," +
                                "    CONSTRAINT pk_song PRIMARY KEY (song_id)," +
                                "    CONSTRAINT fk_song_album_id FOREIGN KEY (album_id)" +
                                "        REFERENCES Music.Album (album_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                                "    CONSTRAINT unique_song_per_album UNIQUE (title, album_id));";
                stmt.execute(sql);
                System.out.println("Successful connection to PostgreSQL Database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createArtist(Artist artist) {
        String name = artist.getName();

        String sql =
                "INSERT INTO Music.Artist (name)" +
                        "VALUES (?)" +
                        "ON CONFLICT (name) DO NOTHING;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getArtistId(String name) {
        String sql =
                "SELECT artist_id FROM Music.Artist WHERE name = ?;";
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
    public void createAlbum(Album album) {
        String title = album.getTitle();

        String[] artists = album.getArtists();

        //int artist_id = this.getArtistId(album.getArtist());

        int release_year = album.getReleaseYear();

        String sql =
                "WITH returned_album_id AS ( " +
                        "INSERT INTO Music.Album (title, release_year) " +
                        "VALUES (?, ?)" +
                        "ON CONFLICT (title, release_year) DO NOTHING " +
                        "RETURNING album_id) " +
                        "INSERT INTO Music.Artist_Album (artist_id, album_id) " +
                        "SELECT ?, returned_album_id.album_id " +
                        "FROM returned_album_id " +
                        "ON CONFLICT (artist_id, album_id) DO NOTHING; ";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, title);
            //stmt.setInt(2, artist_id);
            stmt.setInt(2, release_year);
            stmt.setInt(3, artist_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getAlbumId(String title) {
        String sql =
                "SELECT album_id FROM Music.Album WHERE title = ?;";
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
    public void createSong(Song song) {
        //int artist_id = this.getArtistId(song.getArtist());
        int album_id = this.getAlbumId(song.getAlbum());
        String lyrics = song.getLyrics();
        double duration = song.getLength();
        String title = song.getTitle();

        double[] embedding = this.embedder.getEmbedding(lyrics);

        String sql =
                "INSERT INTO Music.Song (title, album_id, duration, lyrics, embedding)" +
                        "VALUES (?, ?, ?, ?, ?)" +
                        "ON CONFLICT (title, album_id) DO NOTHING;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, title);
            //stmt.setInt(2, artist_id);
            stmt.setInt(2, album_id);
            stmt.setDouble(3, duration);
            stmt.setString(4, lyrics);
            stmt.setObject(5, embedding);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSongId(String title) {
        String sql =
                "SELECT song_id FROM Music.Song WHERE title = ?;";
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

    //Plan on storing the vectors inside postgre, so to search we should implement songsearcher.
    @Override
    public List<Song> getSimilarSongs(double[] embedding) {
        return List.of();
    }
}
