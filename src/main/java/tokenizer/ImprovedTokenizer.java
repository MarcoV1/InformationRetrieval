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
    
        String text = doc.getText(); 
        text = text.replaceAll("[*+/)\"\\|=(,:;'?!\n]", "").toLowerCase();

        List<String> tokens = new ArrayList<>();       
        String sucCar; int j = 0; boolean checked = true;

        String[] textArray = text.split(" ");
        for (String s : textArray) {
            String temp = s.trim();
            if (temp.length() >= 3 && temp.length() < 40) {        
                // ignorar termos com sequencias sucessivas de caracteres iguais: e.g. aaaaaa
                j = 0;
                for (int i = 0; i < temp.length(); i++) {
                    if (temp.charAt(i) == temp.charAt(i+1)) {
                        // j -> numero de sucessoes
                        j++;
                        if (j > 4) {
                            checked = false;
                            break;
                        }
                    }  
                }
                if (checked) { // caso não tenha os tais caracteres sucessivos
                    // encurtar termos que tenham @, por exemplo mails
                    if (temp.contains("@")) {
                        tokens.add(temp.split("@")[0]);
                    }
                    else if (temp.contains("-")) {
                        // remover termos que comecem por - e de seguida uma letra
                        if(temp.length() > 1 && temp.startsWith("-") && Character.isLetter(temp.charAt(1))) {
                            tokens.add(temp.substring(0, temp.length()));
                        }
                        else { // adicionar o resto
                            tokens.add(temp.replaceAll("-", ""));
                        }
                        // para termos que tenham e.g. --2, mete-se só -2
                        // se calhar usar recursividade?
                    }
                    else if (temp.contains(".")) {
                        // caso seja um numero real: 14.67
                        if (Character.isDigit(temp.charAt(temp.indexOf(".") - 1 ))
                                && Character.isDigit(temp.charAt(temp.indexOf(".") + 1))) {
                              // remover casos extra de '-' 
                              temp = temp.replaceAll("-", "");
                              if ( (temp.charAt(temp.length() - 1) + "").matches(".")) {
                                  // caso o termo acabe em '.'
                                  tokens.add(temp.substring(0, temp.length()-1));
                              }
                              else {
                                  tokens.add(temp.replaceAll(".", ""));
                              }
                        }
                    }
                    else {
                        tokens.add(temp);
                    }
                }
            }
        }

        // remover stop words dos tokens
        tokens = filterWithWordList(tokens, stopWordList);

        if (stemmer != null) {
            // stemmer    
            tokens = useStemmer(tokens);
        }
        return tokens; 
    }
    
    public List<String> useStemmer(List<String> tokens) {
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
        tokens.removeAll(wordList);
        return tokens;
    }
}