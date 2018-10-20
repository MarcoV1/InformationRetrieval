/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import documents.Document;
import documents.TSVDocument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TSVReader implements CorpusReader{
    
    List<Document> documents;

    public TSVReader(String dir) {
        this.documents = new ArrayList();
        
        File file = checkFile(dir);
        readFile(file);  
    }
    
    private File checkFile(String dir) {
        File file = new File(dir);
        if (file.isDirectory() || file.exists()) {
            return file;
        }
        else {
            throw new IllegalArgumentException("O diretório fornecido não existe.");
        }
    }
    
    public void readFile(File file) {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String current;
            int i = 0;
            String[] split;
            while ((current = br.readLine()) != null) {
                if (i == 0) {
                    // System.out.println(current);
                } else {
                    //5 , 12, 13
                    split = current.split("\\t");
                    // juntar os três atributos necessários -> o titulo, o review body e headline
                    String docText = split[5] + " " + split[12] + " " + split[13];
                    addDoc(new TSVDocument(i, docText));
                   // System.out.println(Arrays.toString(split));
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addDoc(Document doc) {
        documents.add(doc);
    }

    public List<Document> getDocuments() {
        return documents;
    }
    
    
}
