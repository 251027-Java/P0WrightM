import java.util.ArrayList;
import java.util.List;

public class Doc2VecEmbedder implements IEmbedder {

    public double[] getEmbedding(String text) {
        double[] embedding = new double[100];
        for (int i = 0; i < 100; i++) {
            embedding[i] = 0.1;
        }
        return embedding;
    }
}
