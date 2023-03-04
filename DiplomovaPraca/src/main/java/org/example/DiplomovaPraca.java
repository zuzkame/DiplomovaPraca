/**
 * @author Zuzana Medzihradska
 */

package org.example;

import org.example.Algorithms.Anonymization;
import org.example.data.GraphGenerator;
import org.example.utils.FileReader;

import java.util.Collections;
import java.util.stream.Collectors;

public class DiplomovaPraca {

    public DiplomovaPraca(){

//        var edges = getEdgesListFromCsv("C:\\Users\\zuzka\\Documents\\SCHOOL\\Ing-SEM4\\DP\\datasets\\musae_git_edges-test.csv");

//        var graph = GraphGenerator.getInstance().GenerateGraphWithXVertexes(7);

        var anonymization = new Anonymization();
        anonymization.AnonymizeGreedy();

    }
    public static void main(String[] args){
        var dp = new DiplomovaPraca();
    }
}
