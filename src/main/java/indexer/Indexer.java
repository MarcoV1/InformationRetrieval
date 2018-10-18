/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.util.Pair;

public class Indexer {

    private Map<String, Map<Integer, Integer>> index;
    private int currentBlock, inmem;

    public Indexer() {
        index = new HashMap<>();
        currentBlock = 1;
        inmem = 0;
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
                index.get(x.getKey()).put(x.getValue(), cur + 1);
            }
        }
        return index;
    }

    public void saveBlock() {
        File outputFile = new File("index_block_" + currentBlock+++".txt");
        SortedSet<String> sorted = new TreeSet<>(index.keySet());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (String termo : sorted) {
                bw.write(termo);
                for (Integer i : index.get(termo).keySet()) {
                    String w = "," + i + ":" + index.get(termo).get(i);
                    bw.write(w);
                }
                bw.newLine();
                
            }
            bw.close();
        } catch (IOException e) {

            e.printStackTrace();

        }
        index = null;
        index = new HashMap<>();
    }

    // Math.pow(1024, 8)
    // 4gb 4294967296
    public void addToSPIMIIndex(List<Pair<String, Integer>> list) {
        //System.out.println(Runtime.getRuntime().freeMemory()+ " "+ 1024*256);
        for (Pair<String, Integer> x : list) {
            if (!index.containsKey(x.getKey())) {
                index.put(x.getKey(), new HashMap<>());
            }
            if (!index.get(x.getKey()).containsKey(x.getValue())) {
                index.get(x.getKey()).put(x.getValue(), 1);
            } else {
                int cur = index.get(x.getKey()).get(x.getValue());
                index.get(x.getKey()).put(x.getValue(), cur + 1);
            }
        }

    }
}
