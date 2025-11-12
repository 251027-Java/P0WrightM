public class Main {
    public static void main(String[] args) {

        // Start Searcher
        IRepository repo = new PostgreSQLRepository();
        ISongSearcher searcher = (ISongSearcher) repo; //Since postgreSql implements both!!
        IEmbedder embedder = new Doc2VecEmbedder();
        MusicSearch search = new MusicSearch(repo, searcher, embedder);

        System.out.println("Compiled!");
    }
}
