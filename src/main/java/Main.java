
import corpus.CorpusReader;
import corpus.TSVReader;
import documents.Document;
import java.util.List;
import java.util.Scanner;
import tokenizer.SimpleTokenizer;
import tokenizer.Tokenizer;


public class Main {
    
    public static void main(String[] args) {

        CorpusReader corpus = new TSVReader(args[0]);

        List<Document> documentos = corpus.getDocuments();
        
        for (Document d : documentos) {    
            System.out.println(d);
        }
        
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
    }
   
}
