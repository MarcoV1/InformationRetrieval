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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.tartarus.snowball.SnowballStemmer;

public class ImprovedTokenizer implements Tokenizer {

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
        //text = text.replaceAll("[*+/)\"\\|=(,:;'?!\n&]<>", "").toLowerCase();

        List<String> tokens = Stream
                .of(text)
                .map(w -> w.replaceAll("[^\\w\\s]+", "")).parallel()
                .map(w -> w.trim()).parallel()
                .filter(w -> w.length() > 3).parallel()
                .map(String::toLowerCase).parallel()
                .map(s -> s.split("\\s+")).flatMap(Arrays::stream).parallel()
                .collect(Collectors.toList());
        // remover stop words dos tokens
        tokens = filterWithWordList(tokens, stopWordList);

        if (stemmer != null) {
            // stemmer    
            tokens = useStemmer(tokens);
        }
        return tokens;
    }

    // remover termos que usem vários '-' seguidos
    public static String recursiveNegativeNumberCheck(String temp) {
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
                        if (removed.length() > 0) {
                            lista.add(removed.trim());
                        }
                    } else {
                        lista.add(removed);
                    }
                }
            }
        } catch (IOException e) {
        }
        return lista;
    }

    public List<String> filterWithWordList(List<String> tokens, List<String> wordList) {
        tokens.removeAll(wordList);
        return tokens;
    }
}
