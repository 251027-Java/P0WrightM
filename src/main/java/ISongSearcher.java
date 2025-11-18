import java.util.List;

public interface ISongSearcher {
    public List<Song> getSimilarSongsByLyrics(float[] embedding, int limit); // Embedding of type List<Float>
    public List<Song> getSongsByTitle(String title, int limit);
    public List<Song> getSongsByArtist(String artist, int limit);
    public List<Song> getSongsByAlbum(String album, int limit);
}
