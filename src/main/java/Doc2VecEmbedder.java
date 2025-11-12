import java.util.ArrayList;
import java.util.List;

public class Doc2VecEmbedder implements IEmbedder {

    public List<Double> getEmbedding(String text) {
        List<Double> embedding = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            embedding.add(0.1);
        }
        return embedding;
    }
}
