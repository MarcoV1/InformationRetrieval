/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenizer;

import documents.Documento;
import java.util.ArrayList;
import java.util.List;

public class SimpleTokenizer implements Tokenizer {

    @Override
    public List<String> tokenize(Documento doc) {
        List<String> termos = new ArrayList<>();
        // ler documentos ou lista de documentos
        String text = doc.getText();
        // substituir tudo o que não caracteres alfabeticos, e meter tudo para minusculo
        String alpText = text.replaceAll("[^A-Za-z ]", "").toLowerCase();
        String[] textArray = alpText.split(" ");
        for (String s : textArray) {
            String temp = s.trim();
            if (temp.length() >= 3) {
                // adicionar os termos
                termos.add(temp);
            }
        }

        return termos;

    }
}
