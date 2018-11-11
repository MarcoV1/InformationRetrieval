package weights;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentWeighter {

    private Map<String, Map<Integer, Double>> scorer;
    private final Map<Integer, List<String>> doc_terms;
    private final Map<Integer, Double> documentSize;

    public DocumentWeighter(Map<Integer, List<String>> doc_terms) {
        this.scorer = new HashMap<>();
        this.documentSize = new HashMap<>();
        this.doc_terms = doc_terms;
    }

    public Map<String, Map<Integer, Double>> calculateTF(Map<String, Map<Integer, Integer>> indexer) {
        
        Map<Integer, Double> tf;
        
        for (Map.Entry<String, Map<Integer, Integer>> term : indexer.entrySet()) {
            Map<Integer, Integer> termDocuments = term.getValue();
            
            tf = new HashMap();
            for (Map.Entry<Integer, Integer> docId_freq : termDocuments.entrySet()) {
                int docId = docId_freq.getKey();
                
                double frequency = 1 + Math.log10(docId_freq.getValue());
                
                tf.put(docId, frequency);
            }
            scorer.put(term.getKey(), new HashMap<Integer, Double>(tf));
        }
        calculateDocSize();
        normalizeDocs();
        return scorer;
    }

    /**
     * Calcula o tamanho do documento
     * Irá somar a frequencia de termos de cada documento ao quadrado e
     * será aplicada a raiz quadrada no resultado 
    */
    private void calculateDocSize() {
        doc_terms.entrySet().forEach((document) -> {
            int docId = document.getKey();
            double sum = 0;
            Set<String> words = new HashSet<>(document.getValue());
            sum = words.stream().map((word) -> 
                    scorer.get(word)).filter((tf) -> 
                            (tf != null)).map((tf) -> 
                                    Math.pow(tf.get(docId), 2)).reduce(sum, (accumulator, _item) -> 
                                            accumulator + _item);
                documentSize.put(docId, Math.sqrt(sum));
        });
    }
    
    /**
     * Normalizar os tf de cada documento
     * Para cada documento, o valor de tf será dividida com o tamanho de cada um
     */
    private void normalizeDocs() {
        scorer.entrySet().forEach((term) -> {
            Map<Integer, Double> termDoc = term.getValue();
            termDoc.entrySet().forEach((doc_tf) -> {
                int docId = doc_tf.getKey();
                double size = documentSize.get(docId);
                double normal = doc_tf.getValue() / size;
                termDoc.put(docId, normal);
            });
            scorer.put(term.getKey(), new HashMap(termDoc));
        }); 
    }
    
}
