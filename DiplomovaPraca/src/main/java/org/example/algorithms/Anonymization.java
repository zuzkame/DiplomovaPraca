package org.example.algorithms;

import org.example.data.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

public class Anonymization {
    private int k;
    private List<Integer> _degrees;
    private List<Integer> _degreesSorted;
    private List<Integer> _degreesResult;

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
        if(k <= 1) return;
        try{
            GraphInitialization();

            _degreesResult = GreedyAlgorithm(new ArrayList<>(_degreesSorted));
            System.out.println(_degrees.size());
            System.out.println(_degreesSorted.size());
            System.out.println(_degreesResult.size());
            System.out.println(_degrees.stream().mapToInt(i -> i).sum()/2);
            System.out.println(_degreesResult.stream().mapToInt(i -> i).sum()/2);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void AnonymizeDynamicProg(){

    }

    private void GraphInitialization() throws URISyntaxException {
        var graphGenerator = GraphGenerator.getInstance();
        var fr = graphGenerator.getFr();
        fr.setPathToCsvFile(ClassLoader.getSystemResource("musae_git_edges-test.csv").toURI());
        var listOfEdgeTuples = fr.getEdgesListFromCsv();

        SimpleGraph<Integer, DefaultEdge> graph = graphGenerator.GenerateGraphWithXVertexes(fr.getMinNumber(), fr.getMaxNumber());
        graph = graphGenerator.AddEdgesFromList(graph, listOfEdgeTuples);

        setDegreeList(graph);
        setSortedDegreeList();
//
//        System.out.println(_degrees);
//        System.out.println(_degreesSorted);
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
}
