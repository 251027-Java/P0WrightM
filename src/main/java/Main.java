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
        try {
            log.info("Loading IEmbedder");
            embedder = new Doc2VecEmbedder();
            log.info("Loaded IEmbedder");
        } catch (IOException e) {
            log.error("Unable to create IEmbedder. Exiting.");
            System.out.println("Failed to load music app.\nError was:");
            e.printStackTrace();
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
            e.printStackTrace();
            return;
        }

        log.info("Loading SongSearcher");
        ISongSearcher searcher = (ISongSearcher) repo; //Since postgreSql implements both!!
        log.info("Loaded SongSearcher");
        log.info("Loaded all MusicApp Resources");
        log.info("Creating MusicApp Service");
        MusicApp music = new MusicApp(repo, searcher, embedder);
        log.info("Created MusicApp Service");
        log.info("Beginning MusicApp Service");
        music.start();

        //System.out.println("Compiled!");
    }
}
