/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package documents;

import java.util.HashMap;
import java.util.Map;

public class GoldStandard {
    
    private Map<Integer, HashMap<Integer, Double>> relevanceMap;

    public GoldStandard() {
    
        this.relevanceMap = new HashMap();
    }

    public GoldStandard(int docId, Map<Integer, HashMap<Integer, Double>> relevantMap) {
        this.relevanceMap = relevantMap;
    }


    public Map<Integer, HashMap<Integer, Double>> getRelevants() {
        return relevanceMap;
    }

    public void setRelevants(Map<Integer, HashMap<Integer, Double>> relevants) {
        this.relevanceMap = relevants;
    }
    
    
    
    
    
    
    
}
