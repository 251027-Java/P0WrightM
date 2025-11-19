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
            //System.out.print("Text to Search: ");
            input = scan.nextLine();
            // validate input
            break;
        } while (true);

        return input;
    }

    public List<Song> searchByLyrics() {
        System.out.print("\nSearch By Lyrics: ");
        String userInput = getUserText();

        float[] embedding = this.embedder.getEmbedding(userInput);

        List<Song> similarSongs = this.searcher.getSimilarSongsByLyrics(embedding, 5);

        return similarSongs;
    }

    public List<Song> searchByTitle() {
        System.out.print("\nSearch By Title: ");
        String userInput = getUserText();

        List<Song> songs = this.searcher.getSongsByTitle(userInput, 5);

        return songs;
    }

    public List<Song> searchByAlbum() {
        System.out.print("\nSearch By Album: ");
        String userInput = getUserText();

        List<Song> songs = this.searcher.getSongsByAlbum(userInput, 5);

        return songs;
    }

    public List<Song> searchByArtist() {
        System.out.print("\nSearch By Artist: ");
        String userInput = getUserText();

        List<Song> songs = this.searcher.getSongsByArtist(userInput, 5);

        return songs;
    }
}
