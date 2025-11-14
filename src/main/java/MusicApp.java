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

    private Artist[] insertArtistRepo() {
        String[] names;
        do {
            //Get song artist name
            System.out.print("Artists (Separate by '#'): ");
            names = scan.nextLine().split("#");
            // validate input
            break;
        } while (true);

        Artist[] artists = new Artist[names.length];
        for (int i = 0; i < names.length; i++) {
            Artist artist = new Artist(names[i].strip());
            repo.createArtist(artist);
            artists[i] = artist;
        }

        return artists;
    }

    private Album insertAlbumRepo() {
        String name;
        int release_year;

        Artist[] artists = this.insertArtistRepo();

        do {
            //Get album name
            System.out.print("Album Name: ");
            name = scan.nextLine().strip();
            // validate input
            break;
        } while (true);

        do {
            //Get release year
            System.out.print("Release year: ");
            String input = scan.nextLine();
            try {
                release_year = Integer.parseInt(input.strip());
            } catch (NumberFormatException e) {
                // validate input
                continue;
            }
            break;
        } while (true);

        String[] artist_names = new String[artists.length];
        for (int i = 0; i < artists.length; i++) {
            artist_names[i] = artists[i].getName();
        }

        Album album = new Album(artist_names, name, release_year);
        repo.createAlbum(album);

        return album;
    }

    private Song insertSongRepo() {
        String title;
        Album album;
        double secs = 0;
        String lyrics;

        do {
            //Get Song Name
            System.out.print("Song Title: ");
            title = scan.nextLine();
            // validate input
            break;
        } while (true);

        //artists = insertArtistRepo();

        album = insertAlbumRepo();
        String album_title = album.getTitle();

        do {
            //Get song duration
            System.out.print("Song Length (in seconds): ");
            String input = scan.nextLine();
            try {
                secs = Double.parseDouble(input.strip());
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

        String[] artist_names = album.getArtists();

        Song song = new Song(artist_names, album_title, title.strip(), secs, lyrics.strip());
        repo.createSong(song);

        return song;
    }
}
