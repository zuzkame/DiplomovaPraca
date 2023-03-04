package org.example.algorithms;

import org.example.data.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Anonymization {

    private int k;

    public Anonymization(){}

    public Anonymization(int k){
        setK(k);
    }

    public int getK() {
        return k;
    }

    private void setK(int k) {
        this.k = k;
    }


    public void AnonymizeGreedy(){
        GraphInitialization();
    }

    public void AnonymizeDynamicProg(){

    }

    private void GraphInitialization(){
        var graphGenerator = GraphGenerator.getInstance();
        var fr = graphGenerator.getFr();
        fr.setPathToCsvFile("C:\\Users\\zuzka\\Documents\\SCHOOL\\Ing-SEM4\\DP\\datasets\\musae_git_edges-test.csv");
        var listOfEdgeTuples = fr.getEdgesListFromCsv();

        SimpleGraph<Integer, DefaultEdge> graph = graphGenerator.GenerateGraphWithXVertexes(fr.getMaxNumber());
        graph = graphGenerator.AddEdgesFromList(graph, listOfEdgeTuples);
        System.out.println(graph.edgeSet());
    }
}
