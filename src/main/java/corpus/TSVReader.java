/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import documents.Documento;
import documents.TSVDocument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TSVReader implements CorpusReader {

    List<Documento> documents;
    int i;
    BufferedReader br;

    public TSVReader(String dir) throws FileNotFoundException, IOException {
        File file = checkFile(dir);
        br = new BufferedReader(new FileReader(file));
        i = 0;
        //ler primeira linha
        br.readLine();

    }

    @Override
    public File checkFile(String dir) {
        File file = new File(dir);
        if (file.isDirectory() || file.exists()) {
            return file;
        } else {
            throw new IllegalArgumentException("O diretório fornecido não existe.");
        }
    }
    @Override
    public Documento nextDocument() {
        try {
            String current = br.readLine();
            if (current == null) {
                return null;
            } else {
                //5 , 12, 13
                String[] split = current.split("\\t");
                // juntar os três atributos necessários -> o titulo, o review body e headline
                String docText = split[5] + " " + split[12] + " " + split[13];
                return new TSVDocument(i++, docText);
            }
        } catch (IOException ex) {
            Logger.getLogger(TSVReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
