/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries;

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
    
    private int docId = -1;
    private File file;
    private QueryDocument doc;
    private Map<String, Double> query_weight = new HashMap();
    private Map<String, Map<Integer,Double>> index_query = new HashMap();
    private static final String index_dir = "index.txt";
    private int indexSize = 0;

    public QueryParser(String dir) {
        countDocumentSize();
        this.file = checkFile(dir); 
        this.doc = parseQuery(file);
    }
    
    
    public File checkFile(String dir) {
        File file = new File(dir);
        if (file.isDirectory() || file.exists()) {
            return file;
        } else {
            throw new IllegalArgumentException("O diretório fornecido não existe.");
        }
    }
    
    public void queryWeight(List<String> tokens) {
        
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
        for (String x: contagem.keySet()) {
            if (!query_weight.containsKey(x)) {
                query_weight.put(x, 0d);
            }
            double frequency = 1 + Math.log10(contagem.get(x)) * Math.log10(this.indexSize / index_query.get(x).size() );
            sum_f += Math.pow(frequency, 2);
            query_weight.put(x, frequency);            
        }
        // normalização
        for (String x: contagem.keySet()) {
            double f = query_weight.get(x);
            query_weight.put(x, f/Math.sqrt(sum_f));
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
            String [] split;
            String readS = br.readLine();
            for (String t : termos) {
                while (readS != null){
                    // ler todos os termos do index
                    split = readS.split(",");
                    if (t.equals(split[0])) {
                        for (int i = 1; i < split.length; i++) {
                            // inserir os termos com os devidos pesos e o document id associado
                            index_query.put(t, new HashMap());
                            index_query.get(t).put(
                                    Integer.parseInt(split[i].split(":")[0]),
                                    Double.parseDouble(split[i].split(":")[1]));
                        }
                    }
                    readS= br.readLine();
                } 

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void calculateQueryTFIDF() {
        
        // usando os pesos da query, e os pesos do tokens adquiridos do index
        Map<Integer, Double> scores = new HashMap();
        
        int N = this.indexSize;
       // int df = this.index_query.get(N)
        
       for (String termo : index_query.keySet()) {
           
                      
       }
       
        
    }
    
    private QueryDocument parseQuery(File file) {
        
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            line = br.readLine();
            QueryDocument query = new QueryDocument(docId++, line);
            
            return query;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public QueryDocument getDoc() {
        return doc;
    }
    
    
    
}
