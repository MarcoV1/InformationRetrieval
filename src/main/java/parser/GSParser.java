/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import documents.GoldStandard;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GSParser {

    File file;
    private int documentId = 0;
    
    public GSParser(String dir) {
        
        file = new File(dir);     
    }
     
    public GoldStandard parseFile() {
        
        Map<Integer, HashMap<Integer,Double>> relevanceMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(file));
            
            String line;      
            int lastID = 1;
            Map<Integer, Double> value = new HashMap();

            while ((line = br.readLine()) != null) {
                String [] split = line.split("\\s+");
                
                int queryId = Integer.parseInt(split[0]);
                int docId = Integer.parseInt(split[1]);
                int relevanceValue = Integer.parseInt(split[2]);
                
                if (relevanceValue > 0 && relevanceValue <= 4) {
                    // caso o id da query não exista, é criado um mapa novo para os valores 
                     if (queryId != lastID) {
                        relevanceMap.put(lastID, new HashMap(value));
                        value = new HashMap<>();
                        lastID++;
                    }
                    value.put(docId, (double)relevanceValue);
                }
            }
            
            relevanceMap.put(lastID, new HashMap(value));
            return new GoldStandard(documentId++, relevanceMap);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GSParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GSParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
