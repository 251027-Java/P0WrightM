import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MusicApp {

    private final IRepository repo;
    private final ISongSearcher searcher;
    private final IEmbedder embedder;
    MusicSearch musicSearch;
    private final Scanner scan;
    private static final Logger log = LoggerFactory.getLogger(MusicApp.class);

    public MusicApp(IRepository repo, ISongSearcher searcher, IEmbedder embedder) {
        log.info("Initializing MusicApp");
        this.repo = repo;
        this.searcher = searcher;
        this.embedder = embedder;
        this.scan = new Scanner(System.in);
        log.info("Creating new MusicSearch Object");
        this.musicSearch = new MusicSearch(this.searcher, this.embedder, this.scan);
    }

    public void start() {
        // Do stuff
        log.info("Beginning MusicApp Service");
        System.out.println("Welcome to Music App!\n");
        System.out.println("Please select one of the options from the menu below to begin\n");

        //boolean hasNotQuit = true;
        String input;
        List<Song> returnedSongs = List.of();
        do {
            System.out.println("Options are:\n");
            System.out.println("\t1: Add Artist");
            System.out.println("\t2: Add Album");
            System.out.println("\t3: Add Song");
            System.out.println("\t4: Search by Artist");
            System.out.println("\t5: Search by Album");
            System.out.println("\t6: Search by Song Title");
            System.out.println("\t7: Search for Similar Songs by Lyrics");
            System.out.println("\t8: Quit");

            System.out.print("\nSelection: ");
            input = scan.nextLine().strip();
            log.debug("User Input Selection: {}", input);

            if (input.equals("1")) {
                Artist[] artists = getAndInsertArtists();
                if (artists != null) System.out.println(Arrays.toString(artists));
                continue;
            } else if (input.equals("2")) {
                Album album = getAndInsertAlbum();
                if (album != null) System.out.println(album);
                continue;
            } else if (input.equals("3")) {
                Song song = getAndInsertSong();
                if (song != null) System.out.println(song);
                continue;
            } else if (input.equals("4")) {
                returnedSongs = musicSearch.searchByArtist();
            } else if (input.equals("5")) {
                returnedSongs = musicSearch.searchByAlbum();
            } else if (input.equals("6")) {
                returnedSongs = musicSearch.searchByTitle();
            } else if (input.equals("7")) {
                returnedSongs = musicSearch.searchByLyrics();
            } else if (input.equals("8")) {
                break;
            } else {
                System.out.println("Invalid Selection. Please try again");
                continue;
            }

            if (returnedSongs.isEmpty()) {
                System.out.println("\nUnable to find songs matching criteria.\n");
            } else {
                this.displaySongs(returnedSongs);
            }

        } while (true);

        System.out.println("Thanks for using MusicApp!");
    }

    public void close() {
        this.scan.close();
        this.musicSearch = null;
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

    /*
    Separated Get and Insert Logic so that the Insert Logic can be
    tested separately. The Get logic requires system.in input, so I don't
    want to test it with the Insert Logic.
     */
    private Artist[] getAndInsertArtists() {
        String[] names;
        boolean isInvalid = false;
        log.info("Getting artist information from user");
        do {
            //Get song artist name
            System.out.print("Artists (Separate by '#'): ");
            names = scan.nextLine().split("#");
            // validate input
            for (String name : names) {
                if (name.isBlank()) {
                    System.out.println("Invalid artist name. Please try again.");
                    isInvalid = true;
                    break;
                } else {
                    isInvalid = false;
                }
            }
        } while (isInvalid);

        return insertArtistsRepo(names);
    }

    public Artist insertArtistRepo(String name) {

        if (repo.getArtist(name) != null) {
            return null;
        }

        Artist artist = new Artist(name.strip());
        boolean didInsert = repo.createArtist(artist);
        if (!didInsert) {
            log.warn("Failed to insert artist into IRepository.");
            System.out.println("An error occurred when adding new Artist");
            return null;
        } else {
            log.info("Successfully inserted");
        }
        return artist;
    }

    public Artist[] insertArtistsRepo(String[] names) {

        log.info("Attempting to insert Artists into IRepository");
        Artist[] artists = new Artist[names.length];
        for (int i = 0; i < names.length; i++) {
            artists[i] = insertArtistRepo(names[i]);
        }

        return artists;
    }

    public boolean deleteArtistRepo(String name) {
        if (repo.getArtist(name) == null) {
            return false;
        }
        return repo.deleteArtist(name);
    }

    private Album getAndInsertAlbum() {
        String name;
        int release_year;

        Artist[] artists = this.getAndInsertArtists();
        if (artists == null) {
            return null;
        }

        log.info("Getting Album information from user");

        do {
            //Get album name
            System.out.print("Album Name: ");
            name = scan.nextLine().strip();
            if (name.isBlank()) {
                System.out.println("Invalid Album Name. Please try again");
            } else { break; }
        } while (true);

        do {
            //Get release year
            System.out.print("Release year: ");
            String input = scan.nextLine();
            try {
                release_year = Integer.parseInt(input.strip());
                if (release_year < 0 || release_year > Year.now().getValue()) {
                    System.out.println("Invalid Release Year. Please try again.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Release Year. Please try again.");
                continue;
            }
            break;
        } while (true);

        String[] artist_names = new String[artists.length];
        for (int i = 0; i < artists.length; i++) {
            artist_names[i] = artists[i].getName();
        }

        return insertAlbumRepo(artist_names, name, release_year);
    }

    public Album insertAlbumRepo(String[] artist_names, String album_name, int release_year) {

        if (repo.getAlbum(album_name, release_year) != null) {
            return null;
        }

        log.info("Attempting to insert Album into IRepository");
        Album album = new Album(artist_names, album_name, release_year);
        boolean didInsert = repo.createAlbum(album);
        if (!didInsert) {
            log.warn("Failed to insert album into IRepository.");
            System.out.println("An error occurred when adding new Album");
            return null;
        }

        return album;
    }

    public boolean deleteAlbumRepo(String name, int release_year) {
        if (repo.getAlbum(name, release_year) == null) {
            return false;
        }
        return repo.deleteAlbum(name, release_year);
    }

    private Song getAndInsertSong() {
        String title;
        Album album;
        double secs = 0;
        String lyrics = "";

        album = getAndInsertAlbum();
        if (album == null) {
            return null;
        }
        //String album_title = album.getTitle();

        log.info("Getting Song information from user");

        do {
            //Get Song Name
            System.out.print("Song Title: ");
            title = scan.nextLine();
            if (title.isBlank()) {
                System.out.println("Invalid Song Title. Please try again");
            } else { break; }
        } while (true);

        do {
            //Get song duration
            System.out.print("Song Length (in seconds): ");
            String input = scan.nextLine();
            try {
                secs = Double.parseDouble(input.strip());
                if (secs < 0 ) {
                    System.out.println("Invalid Song Length. Please try again.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Song Length. Please try again.");
                continue;
            }
            break;
        } while (true);

        do {
            //Get Song Lyrics
            System.out.print("Enter Song Lyrics (Followed by 'DONE' on a new line): ");
            StringBuilder builder = new StringBuilder();
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.equals("DONE")) {
                    break;
                }
                builder.append(line).append(" ");
            }
            lyrics = builder.toString();
            if (lyrics.isBlank()) {
                System.out.println("Invalid Lyrics. Please try again");
            } else { break; }
        } while (true);

        String[] artist_names = album.getArtists();

        return insertSongRepo(artist_names, album, title, secs, lyrics);
    }

    public Song insertSongRepo(String[] artist_names, Album album, String song_title, double secs, String lyrics) {

        if (repo.getSong(song_title, album.getTitle(), album.getReleaseYear()) != null) {
            return null;
        }

        log.info("Attempting to insert Song into IRepository");
        Song song = new Song(artist_names, album.getTitle(), album.getReleaseYear(), song_title.strip(), secs, lyrics.strip());
        boolean didInsert = repo.createSong(song);
        if (!didInsert) {
            log.warn("Failed to insert song into IRepository.");
            System.out.println("An error occurred when adding new Song");
            return null;
        }

        return song;
    }

    public boolean deleteSongRepo(String title, String album_name, int release_year) {
        if (repo.getSong(title, album_name, release_year) == null) {
            return false;
        }
        return repo.deleteSong(title, album_name, release_year);
    }
}
