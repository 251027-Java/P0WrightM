import java.util.List;

public interface ISongSearcher {
    public List<Song> getSimilarSongs(double[] embedding, int limit); // Embedding of type List<Float>
}
