package org.example.algorithms;

import org.example.data.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Anonymization {
    private int k;
    private List<Integer> _degrees;

    private Anonymization(){}

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
        try{
            GraphInitialization();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void AnonymizeDynamicProg(){

    }

    private void GraphInitialization() throws URISyntaxException {
        var graphGenerator = GraphGenerator.getInstance();
        var fr = graphGenerator.getFr();
        fr.setPathToCsvFile(ClassLoader.getSystemResource("musae_git_edges.csv").toURI());
        var listOfEdgeTuples = fr.getEdgesListFromCsv();

        SimpleGraph<Integer, DefaultEdge> graph = graphGenerator.GenerateGraphWithXVertexes(fr.getMinNumber(), fr.getMaxNumber());
        graph = graphGenerator.AddEdgesFromList(graph, listOfEdgeTuples);

        setDegreeList(graph);

        System.out.println(_degrees);
    }

    private void setDegreeList(SimpleGraph<Integer, DefaultEdge> graph){
        _degrees = new ArrayList<>();
        for (var i :
                graph.vertexSet()) {
            _degrees.add(graph.degreeOf(i));
        }
    }
}
