import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.pgvector.PGvector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Want to use pgvector to contain lyric vectors inside postgresql database
public class PostgreSQLRepository implements IRepository, ISongSearcher {

    private String POSTGRE_URL = "jdbc:postgresql://localhost:5432/musicappdb";
    private String POSTGRE_USER = "postgres";
    private String POSTGRE_PW = "postgres";
    private IEmbedder embedder;
    private Connection connection;
    private static final Logger log = LoggerFactory.getLogger(PostgreSQLRepository.class);

    public PostgreSQLRepository(IEmbedder embedder) throws SQLException {
        log.info("Initializing PostgreSQL Repository with default connection");
        this.constructor_helper(embedder);
        log.info("Initialized Repository");
    }

    public PostgreSQLRepository(IEmbedder embedder, String url, String user, String password) throws SQLException {
        log.info("Initializing PostgreSQL Repository with custom connection");
        this.POSTGRE_URL = url;
        this.POSTGRE_USER = user;
        this.POSTGRE_PW = password;
        this.constructor_helper(embedder);
        log.info("Initialized Repository");
    }

    private void constructor_helper(IEmbedder embedder) throws SQLException {
        this.embedder = embedder;
        try{
            log.info("Establishing Connection to PostgresSQL Database");
            this.connection = DriverManager.getConnection(POSTGRE_URL, POSTGRE_USER, POSTGRE_PW);
            log.info("Established Connection");

            log.info("Creating Database Structure");
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
                                "    CONSTRAINT unique_album_per_album_release_year UNIQUE (title, release_year));" +
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
                log.info("Created Structure");
                //System.out.println("Successful connection to PostgreSQL Database");
            }
        } catch (SQLException e) {
            log.warn("Unable to Establish Connection and Create Structure. Exiting.", e);
            throw e;
        }
    }

    @Override
    public boolean createArtist(Artist artist) {
        log.info("Attempting to insert Artist into Postgres Database");
        log.debug(artist.toString());
        String name = artist.getName();

        String sql =
                "INSERT INTO Music.Artist (name)" +
                        "VALUES (?)" +
                        "ON CONFLICT (name) DO NOTHING;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            log.info("Artist insertion successful");
            return true;
        } catch (SQLException e) {
            log.warn("Failed to insert Artist into Postgres Database", e);
        }
        return false;
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
    public boolean createAlbum(Album album) {
        // Database ignore case sensitivity for now.
        log.info("Attempting to insert Album into Postgres Database");
        log.debug(album.toString());
        String title = album.getTitle();
        int release_year = album.getReleaseYear();

        /*
        The Album Table has a constraint that album title and year must be unique!
        So, the problem of same name albums made in the same year with different artists
        just can't happen. We can't have more than one album title in a single year.
         */

        String[] artists = album.getArtists();

        int[] artist_ids = new int[artists.length];
        for (int i = 0; i < artists.length; i++) {
            artist_ids[i] = this.getArtistId(artists[i]);
        }

        String sql = "INSERT INTO Music.Album (title, release_year) " +
                        "VALUES (?, ?) " +
                        "RETURNING album_id; " ;
        ResultSet rs = null;
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, title);
            stmt.setInt(2, release_year);
            rs = stmt.executeQuery();
            sql = "INSERT INTO Music.Artist_Album (artist_id, album_id) " +
                    "VALUES (?, ?) " +
                    "ON CONFLICT (artist_id, album_id) DO NOTHING;";
            try ( PreparedStatement artist_album_stmt = connection.prepareStatement(sql) ) {
                int album_id = -1;
                while (rs.next()) {
                    album_id = rs.getInt("album_id"); // By column name
                }
                for (int artist_id : artist_ids) {
                    artist_album_stmt.setInt(1, artist_id);
                    artist_album_stmt.setInt(2, album_id);
                    artist_album_stmt.executeUpdate();
                }
            } catch (SQLException e) {
                log.warn("Error occurred when inserting album/artist into Artist_Album Table", e);
                return false;
            }
            log.info("Album insertion successful");
            return true;
        } catch (SQLException e) {
            log.info("Attempted to insert Album that already exists.");
            return true;
        }
    }

    private int getAlbumId(String title) {
        //TODO: Need to handle where multiple albums have same title.
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
    public boolean createSong(Song song) {
        log.info("Attempting to insert Song into Postgres Database");
        log.debug(song.toString());
        int album_id = this.getAlbumId(song.getAlbum());
        String lyrics = song.getLyrics();
        double duration = song.getLength();
        String title = song.getTitle();

        float[] embedding = this.embedder.getEmbedding(lyrics);

        String sql =
                "INSERT INTO Music.Song (title, album_id, duration, lyrics, embedding)" +
                        "VALUES (?, ?, ?, ?, ?)" +
                        "ON CONFLICT (title, album_id) DO NOTHING;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setString(1, title);
            stmt.setInt(2, album_id);
            stmt.setDouble(3, duration);
            stmt.setString(4, lyrics);
            stmt.setObject(5, new PGvector(embedding));
            stmt.executeUpdate();
            log.info("Song insertion successful");
            return true;
        } catch (SQLException e) {
            log.warn("Error occurred while insert song into Postgres Database", e);
            return false;
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
    public List<Song> getSimilarSongsByLyrics(float[] embedding, int limit) {
        List<Song> similarSongs = new ArrayList<>();
        String sql =
                "SELECT Song.title as song_title, Album.title as album_title, " +
                        "ARRAY_AGG(Artist.name ORDER BY Artist.name) AS artists, " +
                        "Song.duration as duration, Song.lyrics as lyrics " +
                        "FROM Music.Song " +
                        "JOIN Music.Album ON Album.album_id = Song.album_id " +
                        "JOIN Music.Artist_Album ON Artist_Album.album_id = Album.album_id " +
                        "JOIN Music.Artist ON Artist.artist_id = Artist_Album.artist_id " +
                        "GROUP BY Song.song_id, Song.title, Album.title " +
                        "ORDER BY embedding <=> ? " +
                        "LIMIT ?;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            stmt.setObject(1, new PGvector(embedding));
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            //int id = -1;
            while (rs.next()) {
                String songTitle = rs.getString("song_title");
                String albumTitle = rs.getString("album_title");
                String[] artists = (String[]) rs.getArray("artists").getArray();
                int duration = rs.getInt("duration");
                String lyrics = rs.getString("lyrics");
                similarSongs.add(new Song(artists, albumTitle, songTitle, duration, lyrics));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return similarSongs;
    }

    @Override
    public List<Song> getSongsByTitle(String title, int limit) {
        List<Song> songs = new ArrayList<>();
        String sql =
                "SELECT Song.title as song_title, Album.title as album_title, " +
                        "ARRAY_AGG(Artist.name ORDER BY Artist.name) AS artists, " +
                        "Song.duration as duration, Song.lyrics as lyrics " +
                        "FROM Music.Song " +
                        "JOIN Music.Album ON Album.album_id = Song.album_id " +
                        "JOIN Music.Artist_Album ON Artist_Album.album_id = Album.album_id " +
                        "JOIN Music.Artist ON Artist.artist_id = Artist_Album.artist_id " +
                        "WHERE Song.title ILIKE ? " +
                        "GROUP BY Song.song_id, Song.title, Album.title " +
                        "LIMIT ?;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            title = "%" + title + "%";
            stmt.setString(1, title);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            //int id = -1;
            while (rs.next()) {
                String songTitle = rs.getString("song_title");
                String albumTitle = rs.getString("album_title");
                String[] artists = (String[]) rs.getArray("artists").getArray();
                int duration = rs.getInt("duration");
                String lyrics = rs.getString("lyrics");
                songs.add(new Song(artists, albumTitle, songTitle, duration, lyrics));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    @Override
    public List<Song> getSongsByArtist(String artist, int limit) {
        List<Song> songs = new ArrayList<>();
        String sql =
                "SELECT Song.title as song_title, Album.title as album_title, " +
                        "ARRAY_AGG(DISTINCT Artist.name ORDER BY Artist.name) AS artists, " +
                        "Song.duration as duration, Song.lyrics as lyrics " +
                        "FROM Music.Song " +
                        "JOIN Music.Album ON Album.album_id = Song.album_id " +
                        "JOIN Music.Artist_Album ON Artist_Album.album_id = Album.album_id " +
                        "JOIN Music.Artist ON Artist.artist_id = Artist_Album.artist_id " +
                        "WHERE Album.album_id IN (" +
                        "    SELECT DISTINCT Album.album_id " +
                        "    FROM Music.Album " +
                        "    JOIN Music.Artist_Album ON Artist_Album.album_id = Album.album_id " +
                        "    JOIN Music.Artist ON Artist.artist_id = Artist_Album.artist_id " +
                        "    WHERE Artist.name ILIKE ?" +
                        ") " +
                        "GROUP BY Song.song_id, Song.title, Album.title " +
                        "LIMIT ?;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            artist = "%" + artist + "%";
            stmt.setString(1, artist);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            //int id = -1;
            while (rs.next()) {
                String songTitle = rs.getString("song_title");
                String albumTitle = rs.getString("album_title");
                String[] artists = (String[]) rs.getArray("artists").getArray();
                int duration = rs.getInt("duration");
                String lyrics = rs.getString("lyrics");
                songs.add(new Song(artists, albumTitle, songTitle, duration, lyrics));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    @Override
    public List<Song> getSongsByAlbum(String album, int limit) {
        List<Song> songs = new ArrayList<>();
        String sql =
                "SELECT Song.title as song_title, Album.title as album_title, " +
                        "ARRAY_AGG(Artist.name ORDER BY Artist.name) AS artists, " +
                        "Song.duration as duration, Song.lyrics as lyrics " +
                        "FROM Music.Song " +
                        "JOIN Music.Album ON Album.album_id = Song.album_id " +
                        "JOIN Music.Artist_Album ON Artist_Album.album_id = Album.album_id " +
                        "JOIN Music.Artist ON Artist.artist_id = Artist_Album.artist_id " +
                        "WHERE Album.title ILIKE ? " +
                        "GROUP BY Song.song_id, Song.title, Album.title " +
                        "LIMIT ?;";
        try ( PreparedStatement stmt = connection.prepareStatement(sql) ) {
            album = "%" + album + "%";
            stmt.setString(1, album);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            //int id = -1;
            while (rs.next()) {
                String songTitle = rs.getString("song_title");
                String albumTitle = rs.getString("album_title");
                String[] artists = (String[]) rs.getArray("artists").getArray();
                int duration = rs.getInt("duration");
                String lyrics = rs.getString("lyrics");
                songs.add(new Song(artists, albumTitle, songTitle, duration, lyrics));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }
}
