/**
 * @author Bc. Zuzana Medzihradska
 **/

package org.example;

import org.example.algorithms.Anonymization;
import org.example.algorithms.Deanonymization;
import org.example.data.GraphGenerator;
import org.example.utils.ChartUtil;
import org.example.utils.UIFrame;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Arrays;
import java.util.HashSet;

public class DiplomovaPraca {

    public DiplomovaPraca(){

//        Graph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
//        String v1 = "A";
//        String v2 = "B";
//        String v3 = "C";
//        String v4 = "D";
//        graph.addVertex(v1);
//        graph.addVertex(v2);
//        graph.addVertex(v3);
//        graph.addVertex(v4);
//        graph.setEdgeWeight(graph.addEdge(v1, v3), -1.5651651894); // Negate the edge weights
//        graph.setEdgeWeight(graph.addEdge(v1, v4), -2.321541);
//        graph.setEdgeWeight(graph.addEdge(v2, v3), -0.65114);
//        graph.setEdgeWeight(graph.addEdge(v2, v4), -1.619815);

        // Compute the maximum weight bipartite matching
//        KuhnMunkresMinimalWeightBipartitePerfectMatching<String, DefaultWeightedEdge> matching = new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(
//                graph,
//                new HashSet<>(Arrays.asList("A", "B")),
//                new HashSet<>(Arrays.asList("C", "D")));
//        MatchingAlgorithm.Matching<String, DefaultWeightedEdge> bipartiteMatching = matching.getMatching();
//        System.out.println("KuhnMunkresMinimalWeightBipartitePerfectMatching" + -bipartiteMatching.getWeight());
//
//
//        var g = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
//        g.addVertex(1);
//        g.addVertex(2);
//        g.addVertex(3);
//        g.addVertex(4);
//        g.addVertex(5);
//        g.addVertex(6);
//        g.addVertex(7);

//        g.addEdge(1, 3);
//        g.setEdgeWeight(1, 3, 1.5651651894);
//        g.addEdge(1, 4);
//        g.setEdgeWeight(1, 4, 2.321541);
//        g.addEdge(1, 7);
//        g.setEdgeWeight(1, 7, 1);

//        g.addEdge(2, 3);
//        g.setEdgeWeight(2, 3, 0.65114);
//        g.addEdge(2, 4);
//        g.setEdgeWeight(2, 4, 1.619815);
//        g.addEdge(2, 7);
//        g.setEdgeWeight(2, 7, 1);

//        g.addEdge(3, 5);
//        g.setEdgeWeight(3, 5, 1);
//        g.addEdge(3, 6);
//        g.setEdgeWeight(3, 6, 1);
//        g.addEdge(3, 7);
//        g.setEdgeWeight(3, 7, 1);
//
//        g.addEdge(4, 5);
//        g.setEdgeWeight(4, 5, 1);
//        g.addEdge(4, 6);
//        g.setEdgeWeight(4, 6, 1);
//        g.addEdge(4, 7);
//        g.setEdgeWeight(4, 7, 3);

//        var maxMatching = new MaximumWeightBipartiteMatching<Integer, DefaultWeightedEdge>(
//                g,
//                new HashSet<>(Arrays.asList(1, 2)),
//                new HashSet<>(Arrays.asList(3,4)));
//        maxMatching.getMatching();
//        System.out.println("MaximumWeightBipartiteMatching: " + maxMatching.getMatchingWeight().doubleValue());
        var g =GraphGenerator.getInstance(500).GenerateRandomSocialGraphWithClusters(300, (int)Math.round(2.3*300));

        var anonymization = new Anonymization(2, "");
        anonymization.AnonymizeGreedy(g);
        Graph<Integer, DefaultEdge> anonymizedGraphResult = anonymization.get_anonymizedGraphResult();

        if(anonymizedGraphResult == null) return;
        System.out.println(anonymizedGraphResult);

        var chart = ChartUtil.getInstance();
        chart.createChartPocetnostVrcholovSoStupnom(anonymization.get_originaldegrees(), "Početnosť vrcholov s daným stupňom");
        chart.ShowChart("Twitch dataset");
        var deanonymization = new Deanonymization(anonymizedGraphResult, anonymization.get_originalGraph(), 0.001);
        deanonymization.Deanonymize();
//        System.out.println(deanonymization.get_correspondenceMatrix());
        var count = 0.0;
        var countWithPocetnost = 0.0;
        var mapping = anonymization.getCorrespondenceVertexesKToA();
        for(var r=0; r < deanonymization.get_numberOfVertexes(); r++){
            var maxvalue = Arrays.stream(deanonymization.get_correspondenceMatrix()[r]).max().getAsDouble();
            if (maxvalue == deanonymization.get_correspondenceMatrix()[r][mapping.get(r+1)-1]){
                count++;
                var freq = 0;
                for(var c : deanonymization.get_correspondenceMatrix()[r]){
                    if(c == maxvalue)   freq++;
                }
                System.out.println("pocetnost maxvalue je: " + freq + " = " + 1.0/freq*100 + "%");
                countWithPocetnost += 1.0/freq;
            }
            System.out.println("maxValue: " + Arrays.stream(deanonymization.get_correspondenceMatrix()[r]).max());
//            for(var c : deanonymization.get_correspondenceMatrix()[r]){
//                System.out.println(c);
//            }
            System.out.println("\n");
        }
        System.out.println("uspesnost: " + count/deanonymization.get_numberOfVertexes());
        System.out.println("uspesnost vzhladom na pocetnost maxval: " + countWithPocetnost/deanonymization.get_numberOfVertexes());
        System.out.println("pocet iteracii: " + deanonymization.getNumOfIterations());
    }
    public static void main(String[] args){
        var dp = new DiplomovaPraca();
//        UIFrame frame = new UIFrame();
//        frame.setVisible(true);
    }
}
