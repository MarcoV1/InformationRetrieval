/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main2_3 {

    private static int checkNewMax(Map<String, Integer> top10) {
        int min = Integer.MAX_VALUE;
        for (String s: top10.keySet()) {
            if (top10.get(s) < min)
                min = top10.get(s);
        }
        return min;
    }

    public void readFile(File f) {

    }

    public static void main(String[] args) {
        BufferedReader br = null;
        int currentMax = 0;
        int vocabulary = 0;
        Map<String, Integer> top10 = new HashMap<>();
        List<String> oneDoc10 = new ArrayList<>();
        try {
            File file = new File("index.txt");
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                vocabulary++;
                int sum = 0;
                String[] data = line.split(",");
                Map<String, Integer> map = new TreeMap<>();
                for (int i = 1; i < data.length; i++) {
                    String[] entry = data[i].split(":");
                    sum += Integer.parseInt(entry[1]);
                }
                //System.out.println(top10);
                if (top10.size() < 10) {
                    top10.put(data[0], sum);
                    currentMax = checkNewMax(top10);
                } else if (sum > currentMax) {
                    if (top10.size() == 10) {
                        SortedSet<String> temp = new TreeSet<>();
                        temp.addAll(top10.keySet());
                        for (String x : temp) {
                            if (top10.get(x) < sum) {
                                top10.remove(x);
                                break;
                            }
                        }
                    }
                    top10.put(data[0], sum);
                    currentMax = checkNewMax(top10);
                }
                if (oneDoc10.size() < 10 && sum == 1) {
                    oneDoc10.add(data[0]);
                }
                line = br.readLine();
            }

        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        } finally {
            try {
                br.close();
            } catch (IOException ex) {

            }
        }
        System.out.println("Termos no vocabulário: " + vocabulary);
        System.out.println("Primeiros 10 termos que aparecem num só documento: ");
        for (String s : oneDoc10) {
            System.out.println("\t" + s);
        }
        System.out.println("10 termos que aparecem mais vezes: ");
        for (String s : top10.keySet()) {
            System.out.println("\t" + s + ": " + top10.get(s));
        }
    }
}
