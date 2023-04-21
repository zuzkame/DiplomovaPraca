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

    public GraphGenerator(){
    }

    public static GraphGenerator getInstance(){
        if(generator == null){
            generator = new GraphGenerator();
        }
        return generator;
    }

    public static GraphGenerator getInstance(int maxVal){
        MAX_VERTEXES = maxVal;
        return getInstance();
    }

    public FileReader getFr() {
        return fr;
    }

    public SimpleGraph<Integer, DefaultEdge> GenerateGraphWithXVertexes(int firstVertexNumber, int lastVertexNumber){
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        VertexMap = new HashMap<>();
        List<Integer> vertexes = IntStream.range(firstVertexNumber, lastVertexNumber+1).boxed().collect(Collectors.toList());

        if(MAX_VERTEXES != 0 && vertexes.size() > MAX_VERTEXES){
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

    public SimpleGraph<Integer, DefaultEdge> GenerateRandomSocialGraphWithClusters(int numOfVertexes){
        int numOfEdges = (int)Math.round(4.27*numOfVertexes);
        var graph = GenerateGraphWithXVertexes(1, numOfVertexes);

        double highestDegreeVertexesFraction = 0.05;
        int numHighDegreeVertices = (int) Math.round(highestDegreeVertexesFraction * numOfVertexes);

        int highestDegreeRangeEnd = (int) ((numOfEdges / numHighDegreeVertices)*0.9);
        int highestDegreeRangeStart = highestDegreeRangeEnd/4;
        Random rand = new Random();


        var edgesLeft = numOfEdges;
        for (var i = 1; i <= numHighDegreeVertices; i++){
            int deg;
            do{
                deg = rand.nextInt(highestDegreeRangeStart, highestDegreeRangeEnd+1);
                if(i == 1){
                    deg = numOfVertexes/3;
                }
            } while(deg>edgesLeft);
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

        for(var vertex : graph.vertexSet()){
            if(vertex > numHighDegreeVertices && edgesLeft > 0){
                int deg;
                do{
                    deg = rand.nextInt(4);
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

    public SimpleGraph<Integer, DefaultEdge> GenerateRandomSocialGraph(int numOfVertexes){
        int numOfEdges = (int)Math.round(4.27*numOfVertexes);
        var graph = GenerateGraphWithXVertexes(1, numOfVertexes);
        Random rand = new Random();

        var edgesLeft = numOfEdges;

        while(edgesLeft>0){
            var v1 = rand.nextInt(1, numOfVertexes+1);
            var v2 = rand.nextInt(1, numOfVertexes+1);
            if (v1 != v2 && graph.getEdge(v1, v2) == null) {
                graph.addEdge(v1, v2);
                edgesLeft--;
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

        return graph;
    }
}
