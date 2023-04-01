/**
 * @author Bc. Zuzana Medzihradska
 **/

package org.example;

import org.example.algorithms.Anonymization;
import org.example.algorithms.Deanonymization;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class DiplomovaPraca {

    public DiplomovaPraca(){
        var anonymization = new Anonymization(2);
        anonymization.AnonymizeGreedy();
        Graph<Integer, DefaultEdge> anonymizedGraphResult = anonymization.get_anonymizedGraphResult();

        if(anonymizedGraphResult == null) return;
        System.out.println(anonymizedGraphResult);
        var deanonymization = new Deanonymization(anonymizedGraphResult, anonymization.get_originalGraph(), 0.001);
        deanonymization.Deanonymize();
//        System.out.println(deanonymization.get_correspondenceMatrix());
        for(var r: deanonymization.get_correspondenceMatrix()){
            for(var c : r){
                System.out.println(c);
            }
            System.out.println("\n");
        }

    }
    public static void main(String[] args){
        var dp = new DiplomovaPraca();
    }
}
