/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import documents.Documento;
import documents.TSVDocument;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Stream.builder;
import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
//import jdk.internal.org.xml.sax.InputSource;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CranfieldReader implements CorpusReader {

    List<Documento> documents;
    Iterator<String> files;
    int i;
    BufferedReader br;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public CranfieldReader(String dir) throws FileNotFoundException, IOException {
        i = 0;
        files = this.getFolder(new File(dir)).iterator();

    }

    public final List<String> getFolder(File folder) {
        List<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                files.addAll(getFolder(fileEntry));
            } else {
                files.add(fileEntry.getPath());
            }
        }
        return files;
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
        File file = null;
        try {
            file = new File(files.next());
        } catch (NoSuchElementException e) {
            return null;
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CranfieldReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        Document doc = null;
        try {
            doc = documentBuilder.parse(file);
        } catch (SAXException ex) {
            Logger.getLogger(CranfieldReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CranfieldReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        String current = doc.getElementsByTagName("TITLE").item(0).getTextContent() + " " + doc.getElementsByTagName("AUTHOR").item(0).getTextContent() + " " + doc.getElementsByTagName("TEXT").item(0).getTextContent();;

        if (current == null) {
            return null;
        } else {
            return new TSVDocument(Integer.parseInt(doc.getElementsByTagName("DOCNO").item(0).getTextContent().replaceAll("\n", "")), current);
        }
    }

}
