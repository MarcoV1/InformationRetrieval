/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

import documents.Document;
import java.util.List;

public interface Tokenizer {
    
    public List<String> tokenize(Document doc);
    
}
