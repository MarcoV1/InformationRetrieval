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
import java.util.List;
import org.tartarus.snowball.SnowballStemmer;

public class ImprovedTokenizer implements Tokenizer{
    
    private SnowballStemmer stemmer = null;
    private static final String stopWordsFile = "src/main/java/text/stop.txt";
    private List<String> stopWordList;

    public ImprovedTokenizer() {       
        stopWordList = getStopWords(stopWordsFile);
    }
    
    public ImprovedTokenizer(SnowballStemmer stemmer) {
        this.stemmer = stemmer;
        this.stopWordList = getStopWords(stopWordsFile);
    }
        
    @Override
    public List<String> tokenize(Document doc) {
    
        // falta não filtar termos com "-" ou "."  
        String text = doc.getText();
        //text = text.replaceAll("[*+/)\"\\|(,:;'?!\n]", "");

         List<String> tokens = new ArrayList<>();
         
        // substituir tudo o que não caracteres alfabeticos, e meter tudo para minusculo
        String alpText = text.replaceAll("[^A-Za-z ]", "").toLowerCase();
        String[] textArray = alpText.split(" ");
        for (String s : textArray) {
            String temp = s.trim();
            if (temp.length() >= 3) {
                // adicionar os termos
                tokens.add(temp);
            }
        }

    //    System.out.println(tokens);

        // remover stop words dos tokens
        tokens = filterWithWordList(tokens, stopWordList);

        if (stemmer != null) {
            // stemmer    
            tokens = useStemmer(tokens);
        }
        return tokens; 
    }
    
    public List<String> useStemmer(List<String> tokens) {
//        int value;
//        for (int i = 0; i < tokens.size(); i++) {
//            value = tokens.get(i).getValue();
//            stemmer.setCurrent(tokens.get(i).getKey());
//            stemmer.stem();
//            tokens.set(i, new Pair<>(stemmer.getCurrent(), value));
//        }
        int i = 0;
        for (String s : tokens) {
            stemmer.setCurrent(s);
            stemmer.stem();
            tokens.set(i, stemmer.getCurrent());
            i++;
        }
        return tokens;
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
    
    public List<String> filterWithWordList(List<String> tokens, List<String> wordList) {   

//        List<Pair<String, Integer>> termos = new ArrayList();
//        Iterator<Pair<String, Integer>> it = tokens.iterator();
//        while(it.hasNext()) {
//            Pair<String, Integer> termo = it.next();
//            if(!wordList.contains(termo.getKey())) {
//                termos.add(termo);      
//            }
//        }
        tokens.removeAll(wordList);
        return tokens;
    }

    public List<String> getStopWordList() {
        return stopWordList;
    }

}
