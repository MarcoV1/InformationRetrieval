
import documents.QueryDocument;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.tartarus.snowball.ext.englishStemmer;
import queries.QueryParser;
import tokenizer.ImprovedTokenizer;
import tokenizer.Tokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marco 76667
 * @author dimitri 80013
 */
public class QueryMain {
    
     public static final String dir = "src/main/java/text/query2.txt";
    
    public static void main(String[] args) {
        
        QueryParser query = new QueryParser(dir);
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
        for(Entry<Integer, Double> s: scores.entrySet()) {
            System.out.println(s);
        }
        
    }
    
}
