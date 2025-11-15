import java.util.List;
import java.util.Scanner;

public class MusicSearch {

    //private final IRepository repo;
    private final ISongSearcher searcher;
    private final IEmbedder embedder;
    private final Scanner scan;

    public MusicSearch(ISongSearcher searcher, IEmbedder embedder, Scanner scan) {
        //this.repo = repo;
        this.searcher = searcher;
        this.embedder = embedder;
        this.scan = scan;
    }

    public void start() {
        String userInput = getUserText();
        List<Song> similarSongs = this.search(userInput);
        this.displaySongs(similarSongs);
    }

    private String getUserText() {
        String input = "";
        do {
            //Get song artist name
            System.out.print("Text to Search: ");
            input = scan.nextLine();
            // validate input
            break;
        } while (true);

        return input;
    }

    private void displaySongs(List<Song> songs) {
        //displaySongs
        System.out.println();
        for (int i = 0; i < songs.size(); i++) {
            System.out.print(String.format("\t%d: ", i));
            System.out.println(songs.get(i));
        }
        System.out.println();
    }

    private List<Song> search(String text) {
        float[] embedding = this.embedder.getEmbedding(text);

        List<Song> similarSongs = this.searcher.getSimilarSongs(embedding, 5);

        return similarSongs;
    }
}
