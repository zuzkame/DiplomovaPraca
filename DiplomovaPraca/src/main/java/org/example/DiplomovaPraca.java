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
//        String v5 = "E";
//        String v6 = "F";
//        String v7 = "G";
//        String v0 = "Z";
//        graph.addVertex(v1);
//        graph.addVertex(v2);
//        graph.addVertex(v3);
//        graph.addVertex(v4);
//        graph.addVertex(v5);
//        graph.addVertex(v6);
//        graph.addVertex(v7);
//        graph.addVertex(v0);
//        graph.setEdgeWeight(graph.addEdge(v1, v4), -0.1); // Negate the edge weights
//        graph.setEdgeWeight(graph.addEdge(v1, v5), -0.9);
//        graph.setEdgeWeight(graph.addEdge(v1, v6), 0);
//        graph.setEdgeWeight(graph.addEdge(v1, v7), 0);
//        graph.setEdgeWeight(graph.addEdge(v2, v4), -0.1);
//        graph.setEdgeWeight(graph.addEdge(v2, v5), -0.1);
//        graph.setEdgeWeight(graph.addEdge(v2, v6), -0.4);
//        graph.setEdgeWeight(graph.addEdge(v2, v7), -0.4);
//        graph.setEdgeWeight(graph.addEdge(v3, v4), -0.3);
//        graph.setEdgeWeight(graph.addEdge(v3, v5), -0.5);
//        graph.setEdgeWeight(graph.addEdge(v3, v6), -0.2);
//        graph.setEdgeWeight(graph.addEdge(v3, v7), 0);
//        graph.setEdgeWeight(graph.addEdge(v0, v4), 0);
//        graph.setEdgeWeight(graph.addEdge(v0, v5), 0);
//        graph.setEdgeWeight(graph.addEdge(v0, v6), 0);
//        graph.setEdgeWeight(graph.addEdge(v0, v7), 0);

        // Compute the maximum weight bipartite matching
//        KuhnMunkresMinimalWeightBipartitePerfectMatching<String, DefaultWeightedEdge> matching = new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(
//                graph,
//                new HashSet<>(Arrays.asList("A", "B", "C","Z")),
//                new HashSet<>(Arrays.asList("D","E","F","G")));
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
//        g.setEdgeWeight(1, 4, 0.1);
//        g.addEdge(1, 5);
//        g.setEdgeWeight(1, 5, 0.9);
//        g.addEdge(1, 6);
//        g.setEdgeWeight(1, 6, 0);
//        g.addEdge(1, 7);
//        g.setEdgeWeight(1, 7, 0);

//        g.addEdge(2, 3);
//        g.setEdgeWeight(2, 3, 0.65114);
//        g.addEdge(2, 4);
//        g.setEdgeWeight(2, 4, 0.1);
//        g.addEdge(2, 5);
//        g.setEdgeWeight(2, 5, 0.1);
//        g.addEdge(2, 6);
//        g.setEdgeWeight(2, 6, 0.4);
//        g.addEdge(2, 7);
//        g.setEdgeWeight(2, 7, 0.4);
//
//        g.addEdge(3, 4);
//        g.setEdgeWeight(3, 4, 0.3);
//        g.addEdge(3, 5);
//        g.setEdgeWeight(3, 5, 0.5);
//        g.addEdge(3, 6);
//        g.setEdgeWeight(3, 6, 0.2);
//        g.addEdge(3, 7);
//        g.setEdgeWeight(3, 7, 0);

//        g.addEdge(4, 5);
//        g.setEdgeWeight(4, 5, 1);
//        g.addEdge(4, 6);
//        g.setEdgeWeight(4, 6, 1);
//        g.addEdge(4, 7);
//        g.setEdgeWeight(4, 7, 3);

//        var maxMatching = new MaximumWeightBipartiteMatching<Integer, DefaultWeightedEdge>(
//                g,
//                new HashSet<>(Arrays.asList(1, 2,3)),
//                new HashSet<>(Arrays.asList(4,5,6,7)));
//        maxMatching.getMatching();
//        System.out.println("MaximumWeightBipartiteMatching: " + maxMatching.getMatchingWeight().doubleValue());
//        var gg =GraphGenerator.getInstance().GenerateRandomSocialGraph(1000);

        var anonymization = new Anonymization("musae_git_edges.csv");
        anonymization.AnonymizeRandom(null, 0.01);
        Graph<Integer, DefaultEdge> anonymizedGraphResult = anonymization.get_anonymizedGraphResult();

        if(anonymizedGraphResult == null) return;
        //  System.out.println(anonymizedGraphResult);

        var chart = ChartUtil.getInstance();
        chart.createChartPocetnostVrcholovSoStupnom(anonymization.get_originaldegrees(), "Početnosť vrcholov s daným stupňom vo vstupnom grafe");
        chart.ShowChart("Random dataset");
        System.out.println("pocet hran anonymizovanych: " + anonymizedGraphResult.edgeSet().size());
        System.out.println("pocet hran povodnych: " + anonymization.get_originalGraph().edgeSet().size());
        System.out.println("pocet vrcholov anonymizovanych: " + anonymizedGraphResult.vertexSet().size());
        System.out.println("pocet vrcholov povodnych: " + anonymization.get_originalGraph().vertexSet().size());
        var deanonymization = new Deanonymization(anonymizedGraphResult, anonymization.get_originalGraph(), 0.01);
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
