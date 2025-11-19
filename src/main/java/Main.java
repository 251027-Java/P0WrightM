import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // Start Searcher
        IEmbedder embedder;
        log.info("Loading MusicApp Resources");
        System.out.println("MusicApp is starting...");
        try {
            log.info("Loading IEmbedder");
            embedder = new Doc2VecEmbedder();
            log.info("Loaded IEmbedder");
        } catch (IOException e) {
            log.error("Unable to create IEmbedder. Exiting.");
            System.out.println("Failed to load music app.\nError was:");
            return;
        }
        IRepository repo;
        try {
            log.info("Loading IRepository");
            repo = new PostgreSQLRepository(embedder); //postgres needs an embedder to handle creating vectors
            log.info("Loaded IRepository");
        } catch (SQLException e) {
            log.error("Unable to create IRepository. Exiting.");
            System.out.println("Failed to load music app.");
            return;
        }

        log.info("Loading SongSearcher");
        ISongSearcher searcher = (ISongSearcher) repo; //Since postgreSql implements both!!
        log.info("Loaded SongSearcher");
        log.info("Loaded all MusicApp Resources");
        log.info("Creating MusicApp Service");
        MusicApp music = new MusicApp(repo, searcher, embedder);
        log.info("Created MusicApp Service");
        log.info("Starting MusicApp Service");
        music.start();
        log.info("Closing MusicApp Service");
        music.close();
    }
}
