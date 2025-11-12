import java.util.List;

public interface ISongSearcher {
    public List<Song> getSimilarSongs(List<Float> embedding); // Embedding of type List<Float>
}
