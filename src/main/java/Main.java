
import corpus.CorpusReader;
import corpus.TSVReader;
import documents.Document;
import indexer.Indexer;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tartarus.snowball.ext.englishStemmer;
import tokenizer.ImprovedTokenizer;
import tokenizer.SimpleTokenizer;
import tokenizer.Tokenizer;

public class Main {

    public static void main(String[] args) {

        CorpusReader corpus = null;
        try {
            corpus = new TSVReader(args[0]);
        } catch (IOException ex) {
            System.out.println("No file :(");
        }
        Tokenizer tokenizer = null;
        Indexer indexer = new Indexer();

        Scanner sc = new Scanner(System.in);
        System.out.println("Tipo de tokenizer a ser usado: \n>Simples\n>Improved");
        String typeTokenizer;
        do {
            typeTokenizer = sc.nextLine().toLowerCase();
        } while (!typeTokenizer.equals("simples") && !typeTokenizer.equals("improved"));

        if (typeTokenizer.equals("simples")) {
            tokenizer = new SimpleTokenizer();
        }
        else if (typeTokenizer.equals("improved")) {
            System.out.println("Com ou sem stemmer? (c/s)");
             do{
                typeTokenizer = sc.nextLine().toLowerCase();
            
            }while(!typeTokenizer.equals("c") && !typeTokenizer.equals("s"));
     
            if (typeTokenizer.equals("s")) {
                tokenizer = new ImprovedTokenizer();
                //tokenizer.tokenize(documentos);
            }
            else if (typeTokenizer.equals("c")) {
                tokenizer = new ImprovedTokenizer(new englishStemmer());
               // tokenizer.tokenize(documentos);
            }
        }
        Runtime runtime = Runtime.getRuntime();
        
        Document doc = corpus.nextDocument();
        System.out.println("Starting block by block index");
        while (doc != null) {
            if ((runtime.totalMemory() - runtime.freeMemory()) / 1000000 >= (1024*0.85)) {
                System.out.println((runtime.totalMemory() - runtime.freeMemory()) / 1000000);
                System.out.println("Saving Block");
                indexer.saveBlock();
                System.gc();
                System.out.println((runtime.totalMemory() - runtime.freeMemory()) / 1000000);
            }
            indexer.addToSPIMIIndex(tokenizer.tokenize(doc));
            doc = corpus.nextDocument();
            //System.out.println(doc);
        }
        int b = indexer.saveBlock();
        System.out.println("Saving Last Block");
        try {
            indexer.mergeBlocks(b);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
