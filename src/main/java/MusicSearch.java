import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class MusicSearch {

    //private final IRepository repo;
    private final ISongSearcher searcher;
    private final IEmbedder embedder;
    private final Scanner scan;
    private static final Logger log = LoggerFactory.getLogger(MusicSearch.class);


    public MusicSearch(ISongSearcher searcher, IEmbedder embedder, Scanner scan) {
        //this.repo = repo;
        log.info("Initializing MusicSearch");
        this.searcher = searcher;
        this.embedder = embedder;
        this.scan = scan;
    }

    private String getUserText(String searchText) {
        String input = "";
        do {
            System.out.print("\n" + searchText);
            input = scan.nextLine();
            if (input.isBlank()) {
                System.out.println("Invalid Search Criteria. Please try again.");
            } else {
                break;
            }
        } while (true);

        return input.strip();
    }

    private String getUserLyrics(String searchText) {
        String lyrics = "";
        do {
            //Get Song Lyrics
            System.out.print("\n" + searchText);
            StringBuilder builder = new StringBuilder();
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.strip().equals("DONE")) {
                    break;
                }
                builder.append(line).append(" ");
            }
            lyrics = builder.toString();
            if (lyrics.isBlank()) {
                System.out.println("Invalid Lyrics. Please try again");
            } else { break; }
        } while (true);
        return lyrics;
    }

    public List<Song> searchByLyrics() {
        String userInput = getUserLyrics("Search By Song Lyrics (Followed by 'DONE' on a new line): ");
        log.info("Searching by Lyrics");

        float[] embedding = this.embedder.getEmbedding(userInput);

        List<Song> similarSongs = this.searcher.getSimilarSongsByLyrics(embedding, 5);

        return similarSongs;
    }

    public List<Song> searchByTitle() {
        String userInput = getUserText("Search By Title: ");
        log.info("Searching by Song Title");

        List<Song> songs = this.searcher.getSongsByTitle(userInput, 5);

        return songs;
    }

    public List<Song> searchByAlbum() {
        String userInput = getUserText("Search By Album: ");
        log.info("Searching by Album Name");

        List<Song> songs = this.searcher.getSongsByAlbum(userInput, 5);

        return songs;
    }

    public List<Song> searchByArtist() {
        String userInput = getUserText("Search By Artist: ");
        log.info("Searching by Artist");

        List<Song> songs = this.searcher.getSongsByArtist(userInput, 5);

        return songs;
    }
}
