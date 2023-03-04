package org.example.data;

import org.example.utils.FileReader;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.List;

public class GraphGenerator {
    private static GraphGenerator generator = null;

    private final FileReader fr = new FileReader();

    public GraphGenerator(){
    }

    public static GraphGenerator getInstance(){
        if(generator == null){
            generator = new GraphGenerator();
        }
        return generator;
    }

    public FileReader getFr() {
        return fr;
    }

    public SimpleGraph<Integer, DefaultEdge> GenerateGraphWithXVertexes(int numOfVertexes){
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for (var i=1; i<=numOfVertexes; i++){
            graph.addVertex(i);
        }
        return graph;
    }

    public SimpleGraph<Integer, DefaultEdge> AddEdgesFromList(SimpleGraph<Integer, DefaultEdge> graph, List<List<Integer>> edges){
        for (var e :
                edges) {
            graph.addEdge(e.get(0), e.get(1));
        }
        return graph;
    }
}
