
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
        text = text.replaceAll("[^A-Za-z0-9 \\.-]", "").toLowerCase();

        List<String> tokens = new ArrayList<>();       
        int j; boolean checked;

        String[] textArray = text.split(" ");
        for (String s : textArray) {
            checked = true;
            String temp = s.trim();
            if (temp.length() >= 3 && temp.length() < 30) {    
                // ignorar termos com sequencias sucessivas de caracteres iguais: e.g. aaaaaa
                j = 0;
                // caso sejam digitos, ignorar se é ou não sequência
                for (int i = 0; i < temp.length() - 1; i++) {
                    if (temp.charAt(i) == temp.charAt(i+1) && 
                            Character.isLetter(temp.charAt(i)) && Character.isLetter(temp.charAt(i)) ) {
                        // j -> numero de sucessoes
                        j++;
                        if (j > 3) {
                            checked = false;
                            break;
                        }
                    }  
                }
                if (checked) { // caso não tenha os tais caracteres sucessivos

                    if (temp.contains("-")) {
                        // remover termos que comecem por - e de seguida uma letra
                        if (temp.indexOf(0) == '-' && Character.isLetter(temp.charAt(1))) {
                            temp = temp.substring(1, temp.length()).trim();
                            if (temp.length() >= 3) {
                                tokens.add(temp);
                            }
                        } else if (temp.matches(".*\\d+.*")) {
                            // para termos que tenham e.g. ---2, mete-se só -2
                            temp = recursiveNegativeNumberCheck(temp);
                            temp = temp.replaceAll(".", "").trim();
                            if (temp.length() >= 3) {
                                tokens.add(temp);
                            }
                        } else { // adicionar o resto
                            temp = temp.replaceAll("[-.]", "").trim();
                            if (temp.length() >= 3) {
                                tokens.add(temp);
                            }
                        }
                    } else if (temp.contains(".")) {
                        // se os primeiros caracteres forem '.' ignorar
                        do {
                            if (temp.charAt(0) == '.') {
                                temp = temp.substring(1, temp.length());
                                if (temp.length() == 0) {
                                    break;
                                }
                            } else
                                break;
                        } while (true);
                        if (temp.length() <= 3) {
                        } else if (!temp.contains(".")) {
                            temp = temp.replaceAll("[-]", "").trim();
                            if (temp.length() >= 3)
                                tokens.add(temp);
                        }
                        // caso seja um numero real: 14.67
                        else if (Character.isDigit(temp.charAt(temp.indexOf(".") - 1))) {
                            // condição por causa de indexes out of range
                            if (temp.length() > temp.indexOf('.') + 1) {
                                if (Character.isDigit(temp.charAt(temp.indexOf(".") + 1))) {
                                    // remover casos extra de '-'
                                    temp = temp.replaceAll("[A-Za-z -]", "");
                                    if (temp.length() >= 3) {
                                         if (temp.endsWith("."))
                                             tokens.add(temp.substring(0, temp.length()-1));
                                         else
                                            tokens.add(temp);
                                    }
                                }
                            }
                        } else {
                            temp = temp.replaceAll(".", "");
                            if (temp.length() >= 3)
                                tokens.add(temp);
                        }
                    }
                    // encurtar termos que tenham @, por exemplo mails
 /*                   else if (temp.contains("@")) {
                        try {
                            // adicionar emails, e.g. teste@ua.pt - adicionar só "teste"
                            System.out.println("2 " + temp + " " + temp.split("@")[0]);
                            tokens.add(temp.split("@")[0]);
                        } catch (Exception e) {
                        }*/
                    //}
                    else if (temp.length() > 3) { // caso nenhuma condição seja despoletada, adicionar token à lista
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

    // remover termos que usem vários '-' seguidos
    public static  String recursiveNegativeNumberCheck(String temp) {   
        if (temp.charAt(0) == '-') {
            return recursiveNegativeNumberCheck(temp.substring(1, temp.length()));
        }
        return "-" + temp;
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