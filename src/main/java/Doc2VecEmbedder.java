import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
This code was sourced from :
https://github.com/deeplearning4j/deeplearning4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/advanced/modelling/embeddingsfromcorpus/paragraphvectors/ParagraphVectorsInferenceExample.java

I have modified the below code for my needs.
 */

public class Doc2VecEmbedder implements IEmbedder {

    private final String doc2vecPretrained = "./src/main/resources/dl4j-examples-data/dl4j-examples/nlp/paravec/simple.pv";
    private ParagraphVectors embedder;
    private static final Logger log = LoggerFactory.getLogger(Doc2VecEmbedder.class);

    public Doc2VecEmbedder() throws IOException {

        File resource = new File(this.doc2vecPretrained);
        log.info("Loading Tokenizer for Paragraph Vectors");
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        log.info("Loaded Tokenizer");

        try {
            log.info("Loading Pre-Trained Paragraph Vectors");
            this.embedder = WordVectorSerializer.readParagraphVectors(resource);
            log.info("Loaded Paragraph Vectors");
        } catch (IOException e) {
            log.warn("Failed to load pre-trained Paragraph Vector. Exiting.");
            //System.out.println("Error reading pretrained file.\nExiting...");
            throw e;
        }


        this.embedder.setTokenizerFactory(t);
        this.embedder.getConfiguration().setIterations(1); // please note, we set iterations to 1 here, just to speedup inference

        /*
        // here's alternative way of doing this, word2vec model can be used directly
        // PLEASE NOTE: you can't use Google-like model here, since it doesn't have any Huffman tree information shipped.

        ParagraphVectors vectors = new ParagraphVectors.Builder()
            .useExistingWordVectors(word2vec)
            .build();
        */
        // we have to define tokenizer here, because restored model has no idea about it
        log.info("Initialized Doc2Vec Embedder");
    }

    public float[] getEmbedding(String text) {
        float[] embedding;
        try {
            log.info("Creating new embedding");
            log.debug("Creating embedding from text: {}", text);
            embedding = this.embedder.inferVector(text).toFloatVector();
        } catch (ND4JIllegalStateException e) {
            log.warn("Unable to create embedding from pre-trained word vectors");
            log.info("Creating blank embedding");
            embedding = new float[100];
            for (int i = 0; i < 100; i++) {
                embedding[i] = 0;
            }
        }
        return embedding;
    }
}
