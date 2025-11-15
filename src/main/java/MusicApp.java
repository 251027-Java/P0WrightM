import java.util.Arrays;
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
        this.scan = new Scanner(System.in);
        this.musicSearch = new MusicSearch(this.searcher, this.embedder, this.scan);
    }

    public void start() {
        // Do stuff

        System.out.println("Welcome to Music App!\n");
        System.out.println("Please select one of the options from the menu below to begin\n");

        boolean hasNotQuit = true;
        String input;
        do {
            System.out.println("Options are:\n");
            System.out.println("\t1: Add Artist");
            System.out.println("\t2: Add Album");
            System.out.println("\t3: Add Song");
            System.out.println("\t4: Get Artist Information");
            System.out.println("\t5: Get Album Information");
            System.out.println("\t6: Get Song Information");
            System.out.println("\t7: Search for Songs");
            System.out.println("\t8: Quit");

            System.out.print("\nSelection: ");
            input = scan.nextLine().strip();

            if (input.equals("1")) {
                Artist[] artists = insertArtistRepo();
                System.out.println(Arrays.toString(artists));
            } else if (input.equals("2")) {
                Album album = insertAlbumRepo();
                System.out.println(album);
            } else if (input.equals("3")) {
                Song song = insertSongRepo();
                System.out.println(song);
            } else if (input.equals("4")) {
                System.out.println("TBI");
            } else if (input.equals("5")) {
                System.out.println("TBI");
            } else if (input.equals("6")) {
                System.out.println("TBI");
            } else if (input.equals("7")) {
                musicSearch.start();
            } else if (input.equals("8")) {
                hasNotQuit = false;
            } else {
                System.out.println("Invalid Selection. Please try again");
            }

        } while (hasNotQuit);

        System.out.println("Thanks for using MusicApp!");

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
