import java.util.List;

public interface ISongSearcher {
    public List<Song> getSimilarSongs(double[] embedding); // Embedding of type List<Float>
}
