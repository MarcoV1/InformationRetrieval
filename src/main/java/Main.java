
import corpus.CorpusReader;
import corpus.TSVReader;
import documents.Document;
import indexer.Indexer;
import indexer.WeightedIndexer;
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
        int maxMem = 0;
        try {
            // src\main\java\text\amazon_reviews_us_Wireless_v1_00.tsv 256
            corpus = new TSVReader(args[0]);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            maxMem = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            System.out.println("Usage: target_file [max_memory(MB)]" + args[1]);
        }
        Tokenizer tokenizer = null;
        WeightedIndexer indexer = new WeightedIndexer();

        Scanner sc = new Scanner(System.in);
        tokenizer = new ImprovedTokenizer(new englishStemmer());


        Document doc = corpus.nextDocument();
        System.out.println("Starting block by block index");
        while (doc != null) {
            if (checkMem(maxMem)) {
                System.out.println("Saving Block");
                indexer.saveBlock();
                System.gc();
                //System.out.println((runtime.totalMemory() - runtime.freeMemory()) / 1000000);
            }
            indexer.addToSPIMIIndex(tokenizer.tokenize(doc), doc.getId());
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

    private static boolean checkMem(int maxMem) {
        Runtime runtime = Runtime.getRuntime();
        //System.out.println(runtime.totalMemory()/ 1000000 + "  " + runtime.freeMemory()/ 1000000);
        if (maxMem > 0) {
            return (runtime.totalMemory() / 1000000 >= maxMem && (runtime.totalMemory() - runtime.freeMemory()) / 1000000 >= (maxMem * 0.80));
        } else {
            return (runtime.freeMemory() / runtime.totalMemory() < 0.20);
        }
    }
}
