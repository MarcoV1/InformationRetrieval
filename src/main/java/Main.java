
import corpus.CorpusReader;
import corpus.TSVReader;
import documents.Document;
import java.util.List;
import java.util.Scanner;
import org.tartarus.snowball.ext.englishStemmer;
import tokenizer.ImprovedTokenizer;
import tokenizer.SimpleTokenizer;
import tokenizer.Tokenizer;


public class Main {
    
    public static void main(String[] args) {

        CorpusReader corpus = new TSVReader(args[0]);

        List<Document> documentos = corpus.getDocuments();
        
//        for (Document d : documentos) {    
//            System.out.println(d);
//        }
//        
        Scanner sc = new Scanner(System.in);
        System.out.println("Tipo de tokenizer a ser usado: \n>Simples\n>Improved");
        String typeTokenizer;
        do{
            typeTokenizer = sc.nextLine().toLowerCase();
            
        }while(!typeTokenizer.equals("simples") && !typeTokenizer.equals("improved"));
        
        Tokenizer tokenizer;
        if (typeTokenizer.equals("simples")) {
            tokenizer = new SimpleTokenizer();
            
            tokenizer.tokenize(documentos);
            System.out.println(tokenizer.getTermos());
        }
        else if (typeTokenizer.equals("improved")) {
            System.out.println("Com ou sem stemmer? (c/s)");
             do{
                typeTokenizer = sc.nextLine().toLowerCase();
            
            }while(!typeTokenizer.equals("c") && !typeTokenizer.equals("s"));
     
            if (typeTokenizer.equals("s")) {
                tokenizer = new ImprovedTokenizer();
                tokenizer.tokenize(documentos);
                System.out.println(tokenizer.getTermos());
            }
            else if (typeTokenizer.equals("c")) {
                tokenizer = new ImprovedTokenizer(new englishStemmer());
                tokenizer.tokenize(documentos);
                System.out.println(tokenizer.getTermos());
            }
             
           
        }
    }
   
}
