/**
 * @author Bc. Zuzana Medzihradska
 **/

package org.example;

import org.example.algorithms.Anonymization;
import org.example.algorithms.Deanonymization;
import org.example.utils.UIFrame;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Arrays;

public class DiplomovaPraca {

    public DiplomovaPraca(){
        var anonymization = new Anonymization(2, "musae_git_edges-test.csv");
        anonymization.AnonymizeGreedy();
        Graph<Integer, DefaultEdge> anonymizedGraphResult = anonymization.get_anonymizedGraphResult();

        if(anonymizedGraphResult == null) return;
        System.out.println(anonymizedGraphResult);
        var deanonymization = new Deanonymization(anonymizedGraphResult, anonymization.get_originalGraph(), 0.001);
        deanonymization.Deanonymize();
//        System.out.println(deanonymization.get_correspondenceMatrix());
        var count = 0.0;
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
            }
            System.out.println("maxValue: " + Arrays.stream(deanonymization.get_correspondenceMatrix()[r]).max());
            for(var c : deanonymization.get_correspondenceMatrix()[r]){
                System.out.println(c);
            }
            System.out.println("\n");
        }
        System.out.println("uspesnost: " + count/deanonymization.get_numberOfVertexes());
        System.out.println("pocet iteracii: " + deanonymization.getNumOfIterations());
    }
    public static void main(String[] args){
        var dp = new DiplomovaPraca();
//        UIFrame frame = new UIFrame();
//        frame.setVisible(true);
    }
}
