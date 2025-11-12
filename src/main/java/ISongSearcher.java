import java.util.List;

public interface ISongSearcher {
    public List<Song> getSimilarSongs(List<Double> embedding); // Embedding of type List<Float>
}
