/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

import documents.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.tartarus.snowball.SnowballStemmer;


public class ImprovedTokenizer implements Tokenizer{
    
    private SnowballStemmer stemmer = null;
    private static final String stopWordsFile = "src/main/java/text/stop.txt";
    private List<String> stopWordList;
    
    private List<Pair<String, Integer>> tokens;

    
    public ImprovedTokenizer() {       
        tokens = new ArrayList();
        stopWordList = getStopWords(stopWordsFile);
    }
    
    public ImprovedTokenizer(SnowballStemmer stemmer) {
        this.stemmer = stemmer;
        this.tokens = new ArrayList();
        this.stopWordList = getStopWords(stopWordsFile);
    }
        
    @Override
    public void tokenize(List<Document> lista) {
                  
        // falta não filtar termos com "-" ou "."
        
        for (Iterator<Document> it = lista.iterator(); it.hasNext();) {
            Document doc = it.next();
            String text = doc.getText();
           // text = text.replaceAll("[*+/)\"\\|(,:;'?!\n]", "");
            
           Stream.of(text)
                    .map(line -> line.split("\\s+"))
                    .flatMap(Arrays::stream).parallel()
                    .map(w -> w.replaceAll("[^\\w\\s]+", ""))
                    .map(w -> w.trim()).parallel()
                    .map(String::toLowerCase)
                    .forEach(word -> {
                        if (word.length()>0)
                            tokens.add(new Pair<>(word, doc.getId()));
                   });
                         
//            String [] split = text.split(" ");   
//            for (String term : split) {
//
//                if (term.trim().length() > 0 )
//                    tokens.add(new Pair<>(term, doc.getId()));
//            }
            
        }
        System.out.println(tokens);
        
//        for (Pair<String, Integer> terms : tokens) {
//            System.out.println(terms);
//        }
        // remover stop words dos tokens
        filterWithWordList(stopWordList);
                
        if (stemmer != null) {
            // stemmer    
            useStemmer();   
        }
               
    }
    
    public void useStemmer() {
        int value;
        for (int i = 0; i < tokens.size(); i++) {
            value = tokens.get(i).getValue();
            stemmer.setCurrent(tokens.get(i).getKey());
            stemmer.stem();
            tokens.set(i, new Pair<>(stemmer.getCurrent(), value));
        }
    }
    
    public List<String> getStopWords(String fileName) {
        List<String> lista = new ArrayList();
         try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String current;
            while ((current = br.readLine()) != null) {     
                String removed = current.trim();          
                if (removed.length() > 0) {
                    if (removed.contains("|")) {  
                        removed = removed.substring(0, removed.indexOf("|"));
                        if (removed.length() > 0 ) { lista.add(removed.trim()); }
                    }
                    else {
                        lista.add(removed);
                    }
                }                       
            }   
        } catch (IOException e) { }
        return lista;
    }
    
    public List<Pair<String, Integer>> filterWithWordList(List<String> wordList) {   

        List<Pair<String, Integer>> termos = new ArrayList();
        Iterator<Pair<String, Integer>> it = tokens.iterator();
        while(it.hasNext()) {
            Pair<String, Integer> termo = it.next();
            if(!wordList.contains(termo.getKey())) {
                termos.add(termo);      
            }
        }
        return termos;
    }

    public List<String> getStopWordList() {
        return stopWordList;
    }
  
    @Override
    public List<Pair<String, Integer>> getTermos() {
        return tokens;
    } 
}
