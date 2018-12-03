
import corpus.CorpusReader;
import corpus.CranfieldReader;
import corpus.TSVReader;
import documents.Documento;
import documents.GoldStandard;
import documents.QueryDocument;
import indexer.WeightedIndexer;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
        String cranfieldDir = "src/main/java/text/cranfield";
        
        
        Map<Integer, Map<Integer, Double>> scores = new HashMap();
        SortedSet<Double> latencies = new TreeSet();
        CorpusReader corpus = null;
        try {
            corpus = new CranfieldReader(cranfieldDir);
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
            }
            indexer.addToSPIMIIndex(tokenizer.tokenize(currentDoc), currentDoc.getId());
            currentDoc = corpus.nextDocument();
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

        // inicio do processamento das queries
        long totalStartTime = System.currentTimeMillis();    
        QueryParser query = new QueryParser(queryDir);
        while (true) {
            long startTime = System.currentTimeMillis();          
            QueryDocument doc = query.nextDoc();
            if (doc == null) {
                break;
            }
            List<String> tokens = tokenizer.tokenize(doc);
            query.addTokens(tokens);
            query.queryWeight();
            query.loadIndex();
            
            Map<Integer, Double> queryScores = query.calculateQueryTFIDF();
            scores.put(doc.getId(), queryScores);
            // fim do processamento das queries
            long endTime = System.currentTimeMillis();
            latencies.add((double) (endTime - startTime) / 1000);
        }
        long totalEndTime = System.currentTimeMillis();
        double medianLatency = median(latencies);
        double queryThroughtput = 1/medianLatency;
        
        MetricsMethods metrics = new MetricsMethods(scores, gs);

        metrics.calculateMeasures();

        System.out.println("\nAssignment 3 Metrics");
        System.out.println("--------------------------------------------------------------------");

        System.out.format("Median Query Latency: %.3f \n", medianLatency);
        System.out.format("Query Throughput: %.3f\n", queryThroughtput);

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
    
    public static Double median(Set<Double> set) {
        Double[] d = new Double[set.size()];
        set.toArray(d);
        if (set.size() % 2 == 0)
            return d[(int) (set.size()/2)] + d[(int) (set.size()/2) +1];
        else
            return d[(int) (set.size()/2) +1];
    }
}
