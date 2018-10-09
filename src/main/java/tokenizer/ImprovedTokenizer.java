/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

// importar e usar a cena do snowball stemmer

import documents.TSVDocument;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.tartarus.snowball.SnowballStemmer;


public class ImprovedTokenizer {
    
    private SnowballStemmer stemmer;
    private final String stopWordsFile = "../text/stop.txt";
    private List<String> stopWordList;
    
    public ImprovedTokenizer() {
    
        getStopWords(stopWordsFile);
        
    
    }
    
    
    
    public void getStopWords(String fileName) {
        
         try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String current;
            String[] split;
            while ((current = br.readLine()) != null) {
                
                // falta remover os comentarios
                
                stopWordList.add(current.trim());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getStopWordList() {
        return stopWordList;
    }
    
    
    
}
