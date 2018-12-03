
package metrics;

import documents.GoldStandard;
import java.util.HashMap;
import java.util.Map;

public class MetricsMethods {
    
    private Map<Integer, Map<Integer,Double>> scores;
    private GoldStandard gs;
    
    int precision, recall, fmeasure, avgPrec, prec10, ndcg;

    public MetricsMethods(Map<Integer, Map<Integer,Double>> scores, GoldStandard gs) {
    
        precision = recall = fmeasure = avgPrec = prec10 = ndcg = 0;
        
        this.scores = scores;
        this.gs = gs;
        
    }

    public void calculateMeasures() {
    
        for (Map.Entry<Integer,HashMap<Integer,Double>> query : gs.getRelevants().entrySet()) {
            
            
            Map<Integer, Double> gsValues = query.getValue();
            
            
        }
    
    }
    
        
}
