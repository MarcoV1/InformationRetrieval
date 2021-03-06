/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

public class WeightedIndexer {

    private Map<String, Map<Integer, Double>> index;
    private File outputFile = new File("index.txt");
    private BufferedWriter bw;
    private int currentBlock, inmem;

    public WeightedIndexer() {
        try {
            this.bw = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException ex) {
            Logger.getLogger(WeightedIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        index = new HashMap<>();
        currentBlock = 1;
        inmem = 0;
    }

    public int saveBlock() {
        File outputFile = new File("index_block_" + currentBlock++ + ".txt");
        SortedSet<String> sorted = new TreeSet<>(index.keySet());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (String termo : sorted) {
                bw.write(termo);
                for (Integer i : index.get(termo).keySet()) {
                    String w = "," + i + ":" + String.format(Locale.US,"%.3f", index.get(termo).get(i) );
                    bw.write(w);
                }
                bw.newLine();

            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFile = null;
        index = null;
        index = new HashMap<>();
        return currentBlock - 1;
    }

    // Math.pow(1024, 8)
    // 4gb 4294967296
    public void addToSPIMIIndex(List<String> list, int id) {
        //System.out.println(Runtime.getRuntime().freeMemory()+ " "+ 1024*256);
        Map<String, Integer> m = new HashMap<>();
        for (String x : list) {
            if (!m.containsKey(x)) {
                m.put(x, 1);
            } else {
                int cur = m.get(x);
                m.put(x, cur + 1);
            }
        }
        double sum_f = 0d;
        for (String x: m.keySet()) {
            if (!index.containsKey(x)) {
                index.put(x, new HashMap<>());
            }
            double frequency = 1 + Math.log10(m.get(x));
            sum_f += Math.pow(frequency, 2);
            index.get(x).put(id, frequency);            
        }
        for (String x: m.keySet()) {
            double f = index.get(x).get(id);
            index.get(x).put(id, f/Math.sqrt(sum_f));
        }

    }

    private File checkFile(String dir) {
        File file = new File(dir);
        if (file.isDirectory() || file.exists()) {
            return file;
        } else {
            throw new IllegalArgumentException("O diretório fornecido não existe.");
        }
    }

    public void mergeBlocks(int num) throws IOException {
        Map<String, Map<Integer, Double>> temp_index = new TreeMap<>();
        File[] blocks = new File[num];
        BufferedReader[] readers = new BufferedReader[num];
        String[][] current = new String[num][];
        for (int i = 0; i < num; i++) {
            int n = i + 1;
            blocks[i] = new File("index_block_" + n + ".txt");
            try {
                System.out.println("Starting merge of block " + n + " i: " + i);
                readers[i] = new BufferedReader(new FileReader(blocks[i]));
                String[] data = readers[i].readLine().split(",");
                current[i] = data;
            } catch (FileNotFoundException ex) {
                System.err.println("File not found");
            }
        }
        Runtime runtime = Runtime.getRuntime();

        int lowest = 0;
        while (current != null && num > 0) {
            lowest = lowestString(current, num);
            try {
                if (current[lowest][0] == null) {
                    break;
                }
            } catch (Exception e) {
                break;
            }
            if (!temp_index.containsKey(current[lowest][0])) {
                temp_index.put(current[lowest][0], new HashMap<>());
            }
            for (int i = 1; i < current[lowest].length; i++) {
                if (!temp_index.get(current[lowest][0]).containsKey(Integer.parseInt(current[lowest][i].split(":")[0]))) {
                    temp_index.get(current[lowest][0]).put(Integer.parseInt(current[lowest][i].split(":")[0]), Double.parseDouble(current[lowest][i].split(":")[1]));
                } else {
                    double cur = temp_index.get(current[lowest][0]).get(Integer.parseInt(current[lowest][i].split(":")[0]));
                    temp_index.get(current[lowest][0]).put(Integer.parseInt(current[lowest][i].split(":")[0]), cur + Double.parseDouble(current[lowest][i].split(":")[1]));
                }
            }
            //extract next
            String previous = current[lowest][0];
            try {
                current[lowest] = readers[lowest].readLine().split(",");
            } catch (NullPointerException e) {
                current[lowest] = null;
            }
            //remove empty blocks
            if (current[lowest] == null && num > 1) {
                System.out.println("Removing Empty Block");
                System.arraycopy(blocks, lowest + 1, blocks, lowest, blocks.length - lowest - 1);
                System.arraycopy(readers, lowest + 1, readers, lowest, readers.length - lowest - 1);
                System.arraycopy(current, lowest + 1, current, lowest, current.length - lowest - 1);
                num--;
            }
            if ((runtime.totalMemory() - runtime.freeMemory()) / 1000000 >= (1024 * 0.85)) {
                System.out.println("Writing final index block");
                clearCache(previous, temp_index);
                Map<String, Map<Integer, Double>> new_index = new TreeMap<>();

                temp_index = new TreeMap<>();
                System.gc();
            }
        }
        System.out.println("Finishing index");
        clearCache(null, temp_index);
    }

    private int lowestString(String[][] current, int len) {
        int res = 0;
        for (int i = 0; i < len; i++) {
            try {
                //
                if (current[i][0].compareTo(current[res][0]) < 0) {
                    res = i;
                }
            } catch (Exception e) {
                return 0;
            }
        }
        return res;
    }

    private boolean checkIfLowest(String[][] current, String word) {
        boolean res = true;
        for (int i = 0; i < current.length; i++) {
            if (current[i][0].compareTo(word) <= 0) {
                res = false;
            }
        }
        return res;
    }

    private void clearCache(String previous, Map<String, Map<Integer, Double>> temp_index) {

        SortedSet<String> sorted = new TreeSet<>(temp_index.keySet());
        Map<String, Map<Integer, Double>> new_index = new TreeMap<>();
        for (String termo : sorted) {
            if (previous != null && termo.compareTo(previous) > 0) {
                new_index.put(termo, temp_index.get(termo));
            } else {
                try {
                    bw.write(termo);
                    for (Integer i : temp_index.get(termo).keySet()) {
                        String w = "," + i + ":" + temp_index.get(termo).get(i);
                        bw.write(w);
                    }
                    bw.newLine();
                    temp_index.remove(termo);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
        if (previous == null) {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(WeightedIndexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        temp_index = null;
        System.gc();
        temp_index = new_index;

    }

}
