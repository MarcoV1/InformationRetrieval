
import documents.QueryDocument;
import java.util.List;
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
    
     public static final String dir = "src/main/java/text/query.txt";
    
    public static void main(String[] args) {
        
        QueryParser query = new QueryParser(dir);
        QueryDocument doc = query.getDoc();
        
        Tokenizer tokenizer = new ImprovedTokenizer();
        
        List<String> tokens = tokenizer.tokenize(doc);
        query.addTokens(tokens);
        query.loadIndex();
        query.queryWeight();
        
        
    }
    
}
