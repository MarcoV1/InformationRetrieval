/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

import documents.Document;
import java.util.List;
import javafx.util.Pair;

public interface Tokenizer {
    
    public List<Pair<String, Integer>> tokenize(Document doc);
    
}
