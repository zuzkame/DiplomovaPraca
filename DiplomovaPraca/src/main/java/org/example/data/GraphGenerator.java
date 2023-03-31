package org.example.data;

import org.example.utils.FileReader;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphGenerator {

    final static int MAX_VERTEXES = 1000;
    private static GraphGenerator generator = null;
    private static Map<Integer, Integer> VertexMap;
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

    public SimpleGraph<Integer, DefaultEdge> GenerateGraphWithXVertexes(int firstVertexNumber, int lastVertexNumber){
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        VertexMap = new HashMap<>();
        List<Integer> vertexes = IntStream.range(firstVertexNumber, lastVertexNumber+1).boxed().collect(Collectors.toList());

        if(vertexes.size() > MAX_VERTEXES){
            Collections.shuffle(vertexes);
            vertexes = vertexes.subList(0, MAX_VERTEXES);
        }

        for(var i = 0; i < vertexes.size(); i++){
            VertexMap.put(vertexes.get(i), i+1);
            graph.addVertex(i+1);
        }
        return graph;
    }

    public SimpleGraph<Integer, DefaultEdge> AddEdgesFromList(SimpleGraph<Integer, DefaultEdge> graph, List<List<Integer>> edges){
        for (var e :
                edges) {
            var v1 = VertexMap.get(e.get(0));
            var v2 = VertexMap.get(e.get(1));
            if(v1 != null && v2 != null){
                graph.addEdge(v1, v2);
            }
        }
        return graph;
    }
}
