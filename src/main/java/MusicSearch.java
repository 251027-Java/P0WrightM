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

//    public void start() {
//
//    }

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

    public List<Song> searchByLyrics() {
        System.out.println("Search By Lyrics:\n");
        String userInput = getUserText();

        float[] embedding = this.embedder.getEmbedding(userInput);

        List<Song> similarSongs = this.searcher.getSimilarSongsByLyrics(embedding, 5);

        return similarSongs;
    }

    public List<Song> searchByTitle() {
        System.out.println("Search By Title:\n");
        String userInput = getUserText();

        List<Song> songs = this.searcher.getSongsByTitle(userInput, 5);

        return songs;
    }

    public List<Song> searchByAlbum() {
        System.out.println("Search By Album:\n");
        String userInput = getUserText();

        List<Song> songs = this.searcher.getSongsByAlbum(userInput, 5);

        return songs;
    }

    public List<Song> searchByAuthor() {
        System.out.println("Search By Author:\n");
        String userInput = getUserText();

        List<Song> songs = this.searcher.getSongsByAuthor(userInput, 5);

        return songs;
    }
}
