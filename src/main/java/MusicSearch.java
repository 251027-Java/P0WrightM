import java.util.List;

public class MusicSearch {

    //private final IRepository repo;
    private final ISongSearcher searcher;
    private final IEmbedder embedder;

    public MusicSearch(ISongSearcher searcher, IEmbedder embedder) {
        //this.repo = repo;
        this.searcher = searcher;
        this.embedder = embedder;
    }

    public void start() {
        String userInput = "";
        List<Song> similarSongs = this.search(userInput);
        this.displaySongs(similarSongs);
    }

    private void displaySongs(List<Song> songs) {
        //displaySongs
    }

    private List<Song> search(String text) {
        List<Double> embedding = this.embedder.getEmbedding(text);

        List<Song> similarSongs = this.searcher.getSimilarSongs(embedding);

        return similarSongs;
    }
}
