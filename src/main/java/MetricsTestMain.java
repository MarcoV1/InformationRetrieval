
import documents.GoldStandard;
import documents.QueryDocument;
import java.util.List;
import java.util.Map;
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

        GoldStandard gs;
        GSParser gsp = new GSParser(relevanceDir);
        // parse do ficheiro query.relevance que o prof fornece
        gs = gsp.parseFile();

        QueryParser query = new QueryParser(queryDir);
        QueryDocument doc = query.getDoc();
        Tokenizer tokenizer = new ImprovedTokenizer(new englishStemmer());

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

}
