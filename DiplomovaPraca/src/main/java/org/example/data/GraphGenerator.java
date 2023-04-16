package org.example.data;

import org.example.utils.ChartUtil;
import org.example.utils.FileReader;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.text.CollationElementIterator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphGenerator {

    private static int MAX_VERTEXES;
    private static GraphGenerator generator = null;
    private static Map<Integer, Integer> VertexMap;
    private final FileReader fr = new FileReader();

    public GraphGenerator(int maxVal){
    }

    public static GraphGenerator getInstance(int maxVal){
        MAX_VERTEXES = maxVal;
        if(generator == null){
            generator = new GraphGenerator(maxVal);
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

    public SimpleGraph<Integer, DefaultEdge> GenerateRandomSocialGraphWithClusters(int numOfVertexes, int numOfEdges){
        var graph = GenerateGraphWithXVertexes(1, numOfVertexes);

        double highestDegreeVertexesFraction = 0.05;
        int highestDegreeRangeStart = (int) Math.round(numOfEdges * 0.10);
        int highestDegreeRangeEnd = (int) Math.round(numOfEdges * 0.18);
        Random rand = new Random();


        // Calculate the number of high-degree vertices
        int numHighDegreeVertices = (int) Math.round(highestDegreeVertexesFraction * numOfVertexes);

        var edgesLeft = numOfEdges;
        for (var i = 1; i <= numHighDegreeVertices; i++){
            var deg = rand.nextInt(highestDegreeRangeStart, highestDegreeRangeEnd+1);
            for (int j = 0; j < deg; j++) {
                int neighbor = rand.nextInt(1, numOfVertexes);
                if (i != neighbor && graph.getEdge(i, neighbor) == null) {
                    graph.addEdge(i, neighbor);
                    edgesLeft--;
                } else{
                    j--;
                }
            }
        }
        System.out.println(edgesLeft);

        for(var vertex : graph.vertexSet()){
            if(vertex > numHighDegreeVertices && edgesLeft > 0){
                int deg;
                do{
                    deg = rand.nextInt(3);
                } while(deg>edgesLeft);
                for (int j = 0; j < deg; j++) {
                    int neighbor = rand.nextInt(numHighDegreeVertices+1, numOfVertexes);
                    if (vertex != neighbor && graph.getEdge(vertex, neighbor) == null) {
                        graph.addEdge(vertex, neighbor);
                        edgesLeft--;
                    }
                }
            }
        }

        System.out.println(graph);
        var degrees = new ArrayList<Integer>();
        for (int vertex : graph.vertexSet()){
            degrees.add(graph.degreeOf(vertex));
        }
        Collections.sort(degrees);
        Collections.reverse(degrees);
        System.out.println(degrees);
        System.out.println("pocet hran: " + degrees.stream().mapToInt(i -> i).sum()/2);

//        var chart = ChartUtil.getInstance();
//        chart.createChartPocetnostVrcholovSoStupnom(degrees, "Početnosť vrcholov s daným stupňom");
//        chart.ShowChart("Umely dataset");
        return graph;
    }
}
