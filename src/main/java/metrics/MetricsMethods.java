package metrics;

import corpus.CorpusReader;
import corpus.CranfieldReader;
import corpus.TSVReader;
import documents.Documento;
import documents.GoldStandard;
import documents.QueryDocument;
import indexer.WeightedIndexer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import metrics.MetricsMethods;
import org.tartarus.snowball.ext.englishStemmer;
import parser.GSParser;
import parser.QueryParser;
import tokenizer.ImprovedTokenizer;
import tokenizer.Tokenizer;

public class MetricsMethods {

    private Map<Integer, Map<Integer, Double>> index;
    private GoldStandard gs;

    double precision, recall, fmeasure, avgPrec, precision10, ndcg;

    public MetricsMethods(Map<Integer, Map<Integer, Double>> index, GoldStandard gs) {

        precision = recall = fmeasure = avgPrec = precision10 = ndcg = 0;

        this.index = index;
        this.gs = gs;
    }

    public void calculateMeasures() {

        for (Map.Entry<Integer, HashMap<Integer, Double>> relevant : gs.getRelevants().entrySet()) {

            // Documentos da query no GS
            Map<Integer, Double> gsValues = relevant.getValue();
            int queryId = relevant.getKey();
            // Documentos da query presentes no index
            Map<Integer, Double> indexValue = index.get(queryId);

            // caso não hajam queries que intercalem com o index, ignorar
            if (indexValue != null) {

                // usar apenas os 10 primeiros docs
                Map<Integer, Double> firstTenDoc = getFirstTen(indexValue);
                // número de relevantes encontrado nos documentos
                int nRelevants = findRelevantsSize(gsValues, indexValue);
                // número de relevantes nos 10 primeiros docs
                int firstTenRelevants = findRelevantsSize(gsValues, firstTenDoc);
                precision += (double) (nRelevants / indexValue.size());
                precision10 += (double) (firstTenRelevants / indexValue.size());
                recall += (double) (nRelevants / gsValues.size());
                double prec = (double) (nRelevants / indexValue.size());
                double prec10 = (double) (firstTenRelevants / indexValue.size());
                double rec = (double) (nRelevants / gsValues.size());
                double temp = 2 * rec * prec;
                double temp2 = rec + prec;
                //System.out.println(temp + " / " + temp2 + " = " + temp / temp2);
                fmeasure += (double) (2 * rec * prec) / (rec + prec);

                // documentos que são relevantes
                Map<Integer, Double> relevantDocs = findRelevants(gsValues, indexValue);

                int readDocs = 0, relevantReadDocs = 0;
                double queryPrecision = 0;
                for (Integer relevantKey : relevant.getValue().keySet()) {
                    // incrementar sempre que um documento é lido
                    readDocs++;
                    // caso o documento relevanto lido seja encontrado, incrementar precisão da query
                    if (relevantDocs.containsKey(relevantKey)) {
                        relevantReadDocs++;
                        queryPrecision += (double) (relevantReadDocs / readDocs);
                    }
                }
                // calcular precisão média
                avgPrec += (double) (queryPrecision / relevantDocs.size());
            }
        }

        // Calcular as médias 
        precision = precision / index.size();
        precision10 = precision10 / index.size();
        recall = recall / index.size();
        fmeasure = fmeasure / index.size();
        avgPrec /= index.size();

    }

    /**
     * Obter os 10 primeiros documentos do indexer
     *
     * @param indexValue
     * @return firstTen
     *
     */
    public Map<Integer, Double> getFirstTen(Map<Integer, Double> indexValue) {

        return indexValue.entrySet()
                .stream()
                .limit(10)
                .collect(Collectors.toMap(c -> c.getKey(), c -> c.getValue()));
    }

    /**
     *
     * @param gsValues
     * @param indexValue
     * @return relevantes
     */
    public Map<Integer, Double> findRelevants(Map<Integer, Double> gsValues, Map<Integer, Double> indexValue) {
        return gsValues.entrySet()
                .stream()
                .filter(value -> indexValue.containsKey(value.getKey()))
                .collect(Collectors.toMap(c -> c.getKey(), c -> c.getValue()));
    }

    /**
     *
     * @param gsValues
     * @param indexValue
     * @return tamanho dos relevantes
     */
    public int findRelevantsSize(Map<Integer, Double> gsValues, Map<Integer, Double> indexValue) {
        return gsValues.entrySet()
                .stream()
                .filter(value -> indexValue.containsKey(value.getKey()))
                .collect(Collectors.toMap(c -> c.getKey(), c -> c.getValue()))
                .size();
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getFmeasure() {
        return fmeasure;
    }

    public double getAvgPrec() {
        return avgPrec;
    }

    public double getPrecision10() {
        return precision10;
    }
    // calcula o NDCG para todas as querys e faz as médias
    // se não houver NDCG para um determinado rank de uma query esta não é contabilizada para o cálculo da média
    public List<Double> getNdcg() {
        List<Double> l = new ArrayList();
        List<Integer> count = new ArrayList();
        l.add(0d);
        count.add(0);
        for (Integer i : index.keySet()) {
            int cont = 0;
            for (Integer h : NDCG(index.get(i)).keySet()) {
                if (cont == l.size()) {
                    l.add(0d);
                    count.add(0);
                }
                double temp = l.get(cont);
                int temp_count = count.get(cont);
                l.set(cont, temp + index.get(i).get(h));
                count.set(cont++, ++temp_count);
            }
        }
        for (int i = 0; i < l.size(); i++) {
            double temp = l.get(i);
            l.set(i, temp/count.get(i));
        }
        return l;
    }

    public static Map<Integer, Double> NDCG(Map<Integer, Double> scores) {
        Map<Integer, Double> dcg = DCG(scores);
        Map<Integer, Double> perfect_dcg = DCG(sortMap(scores));
        Map<Integer, Double> ndcg = new HashMap();
        Iterator<Entry<Integer, Double>> itr_dcg = dcg.entrySet().iterator();
        Iterator<Entry<Integer, Double>> itr_perfect_dcg = perfect_dcg.entrySet().iterator();
        while (itr_dcg.hasNext()) {
            Entry<Integer, Double> dcg_s = itr_dcg.next();
            Entry<Integer, Double> perfect_dcg_s = itr_perfect_dcg.next();
            ndcg.put(dcg_s.getKey(), dcg_s.getValue() / perfect_dcg_s.getValue());
        }
        return ndcg;
    }

    public static Map<Integer, Double> DCG(Map<Integer, Double> scores) {
        Map<Integer, Double> dcg_scores = new LinkedHashMap();
        int cont = 1;
        double previous = 0d;
        for (Entry<Integer, Double> entry : scores.entrySet()) {
            if (++cont == 1) {
                dcg_scores.put(entry.getKey(), entry.getValue());
                previous = entry.getValue();
            } else {
                previous = previous + entry.getValue() / log2(cont);
                dcg_scores.put(entry.getKey(), previous);
            }
        }
        return dcg_scores;
    }

    private static Map<Integer, Double> sortMap(Map<Integer, Double> m) {
        List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(m.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<Integer, Double> sorted = new LinkedHashMap();
        for (Entry<Integer, Double> entry : list) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    public static void printMap(Map<Integer, Double> map) {
        for (Entry<Integer, Double> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
        }
    }

    public static double log2(double n) {
        return (Math.log(n) / Math.log(2));
    }
}
