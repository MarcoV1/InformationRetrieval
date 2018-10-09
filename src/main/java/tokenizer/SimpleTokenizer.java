/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

import documents.Document;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;


public class SimpleTokenizer implements Tokenizer{

    private List<Pair<String, Integer>> termos;

    public SimpleTokenizer() {
        termos = new ArrayList();
    }
    
    @Override
    public void tokenize(List<Document> lista) {
        
        // ler documentos ou lista de documentos
        for (Document doc : lista) {
           String text = doc.getText();
           // substituir tudo o que não caracteres alfabeticos, e meter tudo para minusculo
           String alpText = text.replaceAll("[^A-Za-z ]", "").toLowerCase();
           String[] textArray = alpText.split(" ");
           
           for( String s : textArray) {
               String temp = s.trim();
               if (temp.length() >= 3) {
                   // adicionar os termos
                   termos.add(new Pair<String, Integer>(temp, doc.getId()));
               }
           }
        }
        
       
   
    }

    public List<Pair<String, Integer>> getTermos() {
        return termos;
    }
    
}
