import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        // Start Searcher
        IEmbedder embedder;
        System.out.println("Loading");
        try {
            embedder = new Doc2VecEmbedder();
        } catch (IOException e) {
            System.out.println("Unable to create embedder. Exiting...");
            return;
        }
        IRepository repo = new PostgreSQLRepository(embedder); //postgres needs an embedder to handle creating vectors
        ISongSearcher searcher = (ISongSearcher) repo; //Since postgreSql implements both!!
        MusicApp music = new MusicApp(repo, searcher, embedder);
        music.start();

        //System.out.println("Compiled!");
    }
}
