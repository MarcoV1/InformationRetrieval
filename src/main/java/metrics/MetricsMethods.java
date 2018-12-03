
package metrics;

import documents.GoldStandard;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricsMethods {
    
    private Map<Integer, Map<Integer,Double>> index;
    private GoldStandard gs;
    
    double precision, recall, fmeasure, avgPrec, precision10, ndcg;

    public MetricsMethods(Map<Integer, Map<Integer,Double>> index, GoldStandard gs) {
    
        precision = recall = fmeasure = avgPrec = precision10 = ndcg = 0;
        
        this.index = index;
        this.gs = gs;
    }

    public void calculateMeasures() {
    
        for (Map.Entry<Integer,HashMap<Integer,Double>> relevant : gs.getRelevants().entrySet()) {
            
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

                fmeasure += (double) (2 * recall * precision) / (recall + precision);

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
                // Calculate average of precision
                avgPrec += queryPrecision / relevantDocs.size();
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
     * @param indexValue      
     * @return firstTen
    **/
    public Map<Integer,Double> getFirstTen(Map<Integer, Double> indexValue) {
        
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

    public double getNdcg() {
        return ndcg;
    }
       
}
