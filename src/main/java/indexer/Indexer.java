/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class Indexer {

    public Indexer() {
        
        
    }

    public Map<String, Map<Integer, Integer>> index(List<Pair<String, Integer>> termos) {
        Map<String, Map<Integer, Integer>> index = new HashMap<>();
        for (Pair<String, Integer> x : termos) {
            if (!index.containsKey(x.getKey())) {
                index.put(x.getKey(), new HashMap<>());
            }
            if (!index.get(x.getKey()).containsKey(x.getValue())) {
                index.get(x.getKey()).put(x.getValue(), 1);
            } else {
                int cur = index.get(x.getKey()).get(x.getValue());
                index.get(x.getKey()).put(x.getValue(), cur+1);
            }
        }
        return index;
    }
     
}
