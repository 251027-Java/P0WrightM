import java.util.Scanner;

public class MusicApp {

    private final IRepository repo;
    private final ISongSearcher searcher;
    private final IEmbedder embedder;
    MusicSearch musicSearch;
    private final Scanner scan;

    public MusicApp(IRepository repo, ISongSearcher searcher, IEmbedder embedder) {
        this.repo = repo;
        this.searcher = searcher;
        this.embedder = embedder;
        this.musicSearch = new MusicSearch(this.searcher, this.embedder);
        this.scan = new Scanner(System.in);
    }

    public void start() {
        // Do stuff
        Song song = insertSongRepo();
        System.out.println(song);
    }

    private Artist insertArtistRepo() {
        String name;
        do {
            //Get song artist name
            System.out.print("Artist Name: ");
            name = scan.nextLine();
            // validate input
            break;
        } while (true);

        Artist artist = new Artist(name);
        int id = repo.createArtist(artist);

        return artist;
    }

    private Album insertAlbumRepo(String artist) {
        String name;
        int release_year;

        do {
            //Get album name
            System.out.print("Album Name: ");
            name = scan.nextLine();
            // validate input
            break;
        } while (true);

        do {
            //Get release year
            System.out.print("Release year: ");
            String input = scan.nextLine();
            try {
                release_year = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // validate input
                continue;
            }
            break;
        } while (true);

        Album album = new Album(artist, name, release_year);
        int id = repo.createAlbum(album);

        return album;
    }

    private Song insertSongRepo() {
        String title;
        String artist;
        String album;
        double secs = 0;
        String lyrics;

        do {
            //Get Song Name
            System.out.print("Song Title: ");
            title = scan.nextLine();
            // validate input
            break;
        } while (true);

        artist = insertArtistRepo().getName();

        album = insertAlbumRepo(artist).getTitle();

        do {
            //Get song duration
            System.out.print("Song Length (in seconds): ");
            String input = scan.nextLine();
            try {
                secs = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                // validate input
                continue;
            }
            break;
        } while (true);

        do {
            //Get Song Lyrics
            System.out.print("Enter Song Lyrics: ");
            lyrics = scan.nextLine();
            // validate input
            break;
        } while (true);

        Song song = new Song(artist, album, title, secs, lyrics);
        int id = repo.createSong(song);

        return song;
    }
}
