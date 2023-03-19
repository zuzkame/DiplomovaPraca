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

        var deanonymization = new Deanonymization(anonymizedGraphResult, anonymization.get_originalGraph(), 0.02);
        deanonymization.Deanonymize();

    }
    public static void main(String[] args){
        var dp = new DiplomovaPraca();
    }
}
