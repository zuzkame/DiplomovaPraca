package org.example.algorithms;

import org.example.data.GraphGenerator;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Anonymization {
    private int k;
    private List<Integer> _degrees;
    private List<Integer> _degreesSorted;
    private List<Integer> _degreesResult;

    private Graph<Integer, DefaultEdge> _anonymizedGraphResult;
    private static GraphGenerator graphGenerator = GraphGenerator.getInstance();

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

    public Graph<Integer, DefaultEdge> get_anonymizedGraphResult() {
        return _anonymizedGraphResult;
    }

    public void AnonymizeGreedy(){
        if(k <= 1) return;
        try{
            GraphInitialization();

            _degreesResult = GreedyAlgorithm(new ArrayList<>(_degreesSorted));
            System.out.println(_degreesResult.size());
            System.out.println(_degrees.stream().mapToInt(i -> i).sum()/2);
            System.out.println(_degreesResult.stream().mapToInt(i -> i).sum()/2);

            _anonymizedGraphResult = GraphReconstruction();
            System.out.println(_anonymizedGraphResult.edgeSet().size());

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void AnonymizeDynamicProg(){

    }

    private void GraphInitialization() throws URISyntaxException {
        var fr = graphGenerator.getFr();
        fr.setPathToCsvFile(ClassLoader.getSystemResource("musae_git_edges-test.csv").toURI());
        var listOfEdgeTuples = fr.getEdgesListFromCsv();

        SimpleGraph<Integer, DefaultEdge> graph = graphGenerator.GenerateGraphWithXVertexes(fr.getMinNumber(), fr.getMaxNumber());
        graph = graphGenerator.AddEdgesFromList(graph, listOfEdgeTuples);

        setDegreeList(graph);
        setSortedDegreeList();
    }

    private void setDegreeList(SimpleGraph<Integer, DefaultEdge> graph){
        _degrees = new ArrayList<>();
        for (var i :
                graph.vertexSet()) {
            _degrees.add(graph.degreeOf(i));
        }
    }

    private void setSortedDegreeList(){
        _degreesSorted = new ArrayList<>(_degrees);
        Collections.sort(_degreesSorted);
        Collections.reverse(_degreesSorted);
    }

    private int degAnonCostI(List<Integer> sublist){
        int sum = 0;
        for (var i : sublist ){
            sum += sublist.get(0) - i;
        }
        return sum;
    }

    private List<Integer> GreedyAlgorithm(List<Integer> sortedCopy){
        if(sortedCopy.size() == 0) return sortedCopy;

        var firstKElements = new ArrayList<>(Collections.nCopies(k, sortedCopy.get(0)));

        try{
            int CMerge = countMerge(sortedCopy);
            int CNew = countNew(sortedCopy);

            sortedCopy = sortedCopy.subList(k, sortedCopy.size());
            if(CNew >= CMerge){
                firstKElements.add(sortedCopy.get(0));
                sortedCopy.remove(0);
//
//                CMerge = countMerge(sortedCopy);
//                CNew = countNew(sortedCopy);
            }
        } catch(IndexOutOfBoundsException ex){
            System.out.println("kedy to skoncilo: " + sortedCopy.size());
            return Collections.nCopies(sortedCopy.size(), sortedCopy.get(0));
        }

        return Stream.concat(firstKElements.stream(), GreedyAlgorithm(sortedCopy).stream()).toList();
    }

    private int countMerge(List<Integer> list) throws IndexOutOfBoundsException{
        return list.get(0) - list.get(k) + degAnonCostI(list.subList(k+1, 2*k+1));
    }

    private int countNew(List<Integer> list) throws IndexOutOfBoundsException{
        return degAnonCostI(list.subList(k, 2*k));
    }

    private SimpleGraph<Integer, DefaultEdge> GraphReconstruction(){
        if(_degreesResult.isEmpty()){
            return null;
        }
        if(_degreesResult.stream().mapToInt(i -> i).sum() % 2 != 0){
            return null;
        }

        SimpleGraph<Integer, DefaultEdge> anonymizedGraph = graphGenerator.GenerateGraphWithXVertexes(1, _degreesResult.size());

        List<Integer> degreesResultCopy = new ArrayList<>(_degreesResult);
        while(true){
            if(degreesResultCopy.stream().filter(x -> x < 0).toList().size() > 0){
                return null;
            }
            if(degreesResultCopy.stream().filter(x -> x == 0).toList().size() == degreesResultCopy.size()){
                return anonymizedGraph;
            }

            int randomVertexDegIndex;
            int randomVertexDeg;
            do{
                randomVertexDegIndex = new Random().nextInt(degreesResultCopy.size());
            } while (degreesResultCopy.get(randomVertexDegIndex) == 0);
            randomVertexDeg = degreesResultCopy.get(randomVertexDegIndex);
            var chosenVertex = randomVertexDegIndex+1;
            degreesResultCopy.set(randomVertexDegIndex, 0);

            List<Integer> vertexesWithHighestDegree = GetVertexesWithHighestDegree(degreesResultCopy, chosenVertex, randomVertexDeg);

            for (var v :
                    vertexesWithHighestDegree) {
                anonymizedGraph.addEdge(v, chosenVertex);
                degreesResultCopy.set(v-1, degreesResultCopy.get(v-1)-1);
            }
        }
    }

    private List<Integer> GetVertexesWithHighestDegree(List<Integer> degrees, Integer chosenVertex, int chosenVertexDegree){
        List<Integer> vertexesWithHighestDegree = new ArrayList<>();
        List<Integer> tempVertDegrees = new ArrayList<>(degrees);
        
        tempVertDegrees.set(chosenVertex-1, -1);

        while(vertexesWithHighestDegree.size() < chosenVertexDegree){
            var max = tempVertDegrees.indexOf(Collections.max(tempVertDegrees));
            vertexesWithHighestDegree.add(max+1);
            tempVertDegrees.set(max, -1);
        }

        return vertexesWithHighestDegree;
    }
}
