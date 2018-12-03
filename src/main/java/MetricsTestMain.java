
import corpus.CorpusReader;
import corpus.CranfieldReader;
import corpus.TSVReader;
import documents.Documento;
import documents.GoldStandard;
import documents.QueryDocument;
import indexer.WeightedIndexer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import metrics.MetricsMethods;
import org.tartarus.snowball.ext.englishStemmer;
import parser.GSParser;
import parser.QueryParser;
import tokenizer.ImprovedTokenizer;
import tokenizer.Tokenizer;

public class MetricsTestMain {

    public static void main(String[] args) {

        String relevanceDir = "src/main/java/text/cranfield.query.relevance.txt";
        String queryDir = "src/main/java/text/cranfield.queries.txt";

        CorpusReader corpus = null;
        try {
            corpus = new CranfieldReader("src/main/java/text/cranfield");
        } catch (IOException ex) {
            Logger.getLogger(MetricsTestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        int maxMem = 1024;
        WeightedIndexer indexer = new WeightedIndexer();
        Tokenizer tokenizer = new ImprovedTokenizer(new englishStemmer());

        Documento currentDoc = corpus.nextDocument();
        System.out.println("Starting block by block index");
        while (currentDoc != null) {
            if (checkMem(maxMem)) {
                System.out.println("Saving Block");
                indexer.saveBlock();
                System.gc();
                //System.out.println((runtime.totalMemory() - runtime.freeMemory()) / 1000000);
            }
            indexer.addToSPIMIIndex(tokenizer.tokenize(currentDoc), currentDoc.getId());
            currentDoc = corpus.nextDocument();
            //System.out.println(doc);
        }
        int b = indexer.saveBlock();
        System.out.println("Saving Last Block");
        try {
            indexer.mergeBlocks(b);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldStandard gs;
        GSParser gsp = new GSParser(relevanceDir);
        // parse do ficheiro query.relevance que o prof fornece
        gs = gsp.parseFile();

        QueryParser query = new QueryParser(queryDir);
        QueryDocument doc = query.getDoc();

        List<String> tokens = tokenizer.tokenize(doc);
        query.addTokens(tokens);
        System.out.println("Calculating Query Weight");
        query.queryWeight();
        System.out.println("Loading relevant index tokens");
        query.loadIndex();
        System.out.println("Calculating scores");

        Map<Integer, Double> scores = query.calculateQueryTFIDF();
        scores.entrySet().forEach((s) -> {
            System.out.println(s);
        });

        MetricsMethods metrics = new MetricsMethods(scores, gs);

        metrics.calculateMeasures();

    }

    public static boolean checkMem(int maxMem) {
        Runtime runtime = Runtime.getRuntime();
        //System.out.println(runtime.totalMemory()/ 1000000 + "  " + runtime.freeMemory()/ 1000000);
        if (maxMem > 0) {
            return (runtime.totalMemory() / 1000000 >= maxMem && (runtime.totalMemory() - runtime.freeMemory()) / 1000000 >= (maxMem * 0.80));
        } else {
            return (runtime.freeMemory() / runtime.totalMemory() < 0.20);
        }
    }
}
