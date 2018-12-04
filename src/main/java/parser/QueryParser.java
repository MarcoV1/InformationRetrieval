/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import documents.QueryDocument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryParser {

    private static int docId = 0;
    private File file;
    private Map<String, Double> query_weight = new HashMap();
    private Map<String, Map<Integer, Double>> index_weight = new HashMap();
    private static final String index_dir = "index.txt";
    private int indexSize = 0;
    private List<String> tokens;
    private BufferedReader br;

    public QueryParser(String dir) {

        countDocumentSize();
        // countDocumentSize(dir);

        this.file = checkFile(dir);
        try {
            this.br = new BufferedReader(new FileReader(dir));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public File checkFile(String dir) {
        File file = new File(dir);
        if (file.isDirectory() || file.exists()) {
            return file;
        } else {
            throw new IllegalArgumentException("O diretório fornecido não existe.");
        }
    }

    public void queryWeight() {

        Map<String, Integer> contagem = new HashMap<>();

        // occorencia de cada token no documento
        for (String t : tokens) {
            if (!contagem.containsKey(t)) {
                contagem.put(t, 1);
            } else {
                int cur = contagem.get(t);
                contagem.put(t, cur + 1);
            }
        }
        double sum_f = 0d;
        // calcular os pesos do documento
        for (String x : contagem.keySet()) {
            if (!query_weight.containsKey(x)) {
                query_weight.put(x, 0d);
            }
            double frequency = 1 + Math.log10(contagem.get(x));
            sum_f += Math.pow(frequency, 2);
            query_weight.put(x, frequency);
        }
        // normalização
        for (String x : contagem.keySet()) {
            double f = query_weight.get(x);
            query_weight.put(x, f / Math.sqrt(sum_f));
        }

    }

    public void countDocumentSize() {
        File index_file = new File(index_dir);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(index_file));
            br.readLine();
            this.indexSize++;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void loadIndex() {
        // ordenar os termos 
        SortedSet<String> termos = new TreeSet<>(query_weight.keySet());

        File index_file = new File(index_dir);

        try {
            BufferedReader br = new BufferedReader(new FileReader(index_file));
            String[] split;
            String readS = br.readLine();
            while (readS != null) {
                // ler todos os termos do index
                split = readS.split(",");
                if (termos.contains(split[0])) {
                    for (int i = 1; i < split.length; i++) {
                        // inserir os termos com os devidos pesos e o document id associado
                        index_weight.put(split[0], new HashMap());
                        index_weight.get(split[0]).put(
                                Integer.parseInt(split[i].split(":")[0]),
                                Double.parseDouble(split[i].split(":")[1]));
                    }
                }
                readS = br.readLine();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<Integer, Double> calculateQueryTFIDF() {

        Map<Integer, Double> scores = new HashMap();

        // usando os pesos da query, e os pesos do tokens adquiridos do index
        // para cada par doc, tf - o score de um documento vai ser igual ao 
        // weight t,d * weight t,q 
        // percorer o index, e os pesos das queries, para calcular o score
        query_weight.keySet().forEach((query) -> {
            if (index_weight.containsKey(query)) {
                index_weight.get(query).keySet().stream().map((document_id) -> {
                    if (!scores.containsKey(document_id)) {
                        scores.put(document_id, 0d);
                    }
                    return document_id;
                }).forEachOrdered((document_id) -> {
                    double temp = scores.get(document_id);
                    // scores[d] += w t,d * w t,q
                    scores.put(document_id, temp + index_weight.get(query).get(document_id)
                            * query_weight.get(query));
                });
            }
        });
        return scores;
    }

    public void addTokens(List<String> tokens) {
        this.query_weight = new HashMap();
        this.tokens = tokens;
    }

    public QueryDocument nextDoc() {
        try {
            String line = br.readLine();
            QueryDocument query = new QueryDocument(docId++, line);
            if (query.getText() == null)
                return null;
            return query;
        } catch (IOException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static int curId(){
        return docId;
    }
}
