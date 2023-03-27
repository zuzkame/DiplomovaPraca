/**
 * @author Bc. Zuzana Medzihradska
 **/

package org.example;

import org.example.algorithms.Anonymization;
import org.example.algorithms.Deanonymization;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.graph.SimpleWeightedGraph;

public class DiplomovaPraca {

    public DiplomovaPraca(){
        var anonymization = new Anonymization(2);
        anonymization.AnonymizeGreedy();
        Graph<Integer, DefaultEdge> anonymizedGraphResult = anonymization.get_anonymizedGraphResult();

        var deanonymization = new Deanonymization(anonymizedGraphResult, anonymization.get_originalGraph(), 0.001);
        deanonymization.Deanonymize();
//        System.out.println(deanonymization.get_correspondenceMatrix());
        for(var r: deanonymization.get_correspondenceMatrix()){
            for (var s : r){
                System.out.println(s + ", ");
            }
            System.out.println("\n");
        }

    }
    public static void main(String[] args){
        var dp = new DiplomovaPraca();
    }
}
