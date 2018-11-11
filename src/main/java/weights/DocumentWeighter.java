package weights;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentWeighter {

    private Map<String, Map<Integer, Double>> scorer;
    //private final Map<Integer, List<String>> documents;
    // Lengths of documents. Values used in normalization
    private final Map<Integer, Double> documentLength;

    public DocumentWeighter() {
        scorer = new HashMap<>();
        documentLength = new HashMap<>();
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
        
        return scorer;
    }

}
