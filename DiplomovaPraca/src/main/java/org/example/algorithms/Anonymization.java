package org.example.algorithms;

import org.example.data.GraphGenerator;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Anonymization {
    private int k;
    private List<Integer> _degrees;
    private Map<Integer, Integer> _degreesMap;
//    private List<Integer> _degreesSorted;
    private Map<Integer, Integer> _degreesSorted;
    private List<Integer> _degreesResult;
    private Map<Integer, Integer> _degreesResultMap;

    private Graph<Integer, DefaultEdge> _anonymizedGraphResult;
    private Graph<Integer, DefaultEdge> _originalGraph;

    private Map<Integer, Integer> correspondenceVertexesKToA;
    private static GraphGenerator graphGenerator = GraphGenerator.getInstance(500);

    private String _filename;

    private Anonymization(){}

    public Anonymization(int k, String filename){
        set_filename(filename);
        setK(k);
    }

    public int getK() {
        return k;
    }

    private void setK(int k) {
        this.k = k;
    }

    private void set_filename(String _filename) {
        this._filename = _filename;
    }

    public Graph<Integer, DefaultEdge> get_anonymizedGraphResult() {
        return _anonymizedGraphResult;
    }

    public Graph<Integer, DefaultEdge> get_originalGraph() {
        return _originalGraph;
    }

    public List<Integer> get_originaldegrees() {
        return _degrees;
    }

    public Map<Integer, Integer> getCorrespondenceVertexesKToA() {
        return correspondenceVertexesKToA;
    }

    public void AnonymizeGreedy(){
        AnonymizeGreedy(null);
    }

    public void AnonymizeGreedy(SimpleGraph<Integer, DefaultEdge> g){
        if(k <= 1) return;
        try{
            GraphInitialization(g);
            var degreesSortedValues = new ArrayList<Integer>();
            var degreesSortedKeys = new ArrayList<Integer>();
            for (var entry: _degreesSorted.entrySet()){
                degreesSortedValues.add(entry.getValue());
                degreesSortedKeys.add(entry.getKey());
            }



//            System.out.println("pocetnost stupnov:" + degreesSortedValues.stream().collect(Collectors.groupingBy(e -> e.toString(),Collectors.counting())));
            _degreesResult = GreedyAlgorithm(new ArrayList<>(degreesSortedValues));
//            System.out.println(_degreesResult.size());
//            System.out.println(_degrees.stream().mapToInt(i -> i).sum()/2);
//            System.out.println("pocet hran po anonymizacii: " + _degreesResult.stream().mapToInt(i -> i).sum()/2);
//            System.out.println("pocetnost stupnov po anonymizacii:" + _degreesResult.stream().collect(Collectors.groupingBy(e -> e.toString(),Collectors.counting())));

            _degreesResultMap = new LinkedHashMap<>();
            for(var i = 0; i < _degreesResult.size(); i++){
                _degreesResultMap.put(degreesSortedKeys.get(i), _degreesResult.get(i));
            }
//            System.out.println("anonymizovane d (vrchol=degree): " + _degreesResultMap);
            System.out.println("ANONYMIZOVANY GRAF");
            System.out.println("max stupen: " + _degreesResult.get(0));
            System.out.println("min stupen: " + _degreesResult.get(_degreesResult.size()-1));
            System.out.println("median: " + (_degreesResult.size()%2==0 ? ( _degreesResult.get(_degreesResult.size()/2 -1) + _degreesResult.get(_degreesResult.size()/2)) / 2 : _degreesResult.get((_degreesResult.size()+1) / 2 - 1)));
            System.out.println("priemer: " + _degreesResult.stream().mapToInt(i -> i).sum()/_degreesResult.size());
            _anonymizedGraphResult = GraphReconstruction(degreesSortedKeys);
            if (_anonymizedGraphResult == null) {
                System.out.println("Graf sa neda zostrojit");
                return;
            }
//            System.out.println(_anonymizedGraphResult.edgeSet());

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void AnonymizeDynamicProg(){

    }
    private void GraphInitialization() throws URISyntaxException{
        GraphInitialization(null);
    }

    private void GraphInitialization(SimpleGraph<Integer, DefaultEdge> g) throws URISyntaxException {
        SimpleGraph<Integer, DefaultEdge> graph;
        if(g == null){
            var fr = graphGenerator.getFr();
            fr.setPathToCsvFile(ClassLoader.getSystemResource(_filename).toURI());
            var listOfEdgeTuples = fr.getEdgesListFromCsv();

            graph = graphGenerator.GenerateGraphWithXVertexes(fr.getMinNumber(), fr.getMaxNumber());
            graph = graphGenerator.AddEdgesFromList(graph, listOfEdgeTuples);
        }
        else{
            graph = g;
        }

        _originalGraph = graph;
        setDegreeList(graph);
        setSortedDegreeList();
    }

    private void setDegreeList(SimpleGraph<Integer, DefaultEdge> graph){
//        _degrees = new ArrayList<>();
//        for (var i :
//                graph.vertexSet()) {
//            _degrees.add(graph.degreeOf(i));
//        }

        _degrees = new ArrayList<>();
        _degreesMap = new HashMap<>();
        for (var i : graph.vertexSet()){
            _degreesMap.put(i, graph.degreeOf(i));
            _degrees.add(graph.degreeOf(i));
        }
    }

    private void setSortedDegreeList(){
        _degreesSorted = new LinkedHashMap<>();
        var temp = new HashMap<>(_degreesMap);

        ArrayList<Integer> list = new ArrayList<>();
        for (var entry : temp.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list);
        Collections.reverse(list);

        System.out.println("POVODNY GRAF");
        System.out.println("max stupen: " + list.get(0));
        System.out.println("min stupen: " + list.get(list.size()-1));
        System.out.println("median: " + (list.size()%2==0 ? ( list.get(list.size()/2 -1) + list.get(list.size()/2)) / 2 : list.get((list.size()+1) / 2 - 1)));
        System.out.println("priemer: " + list.stream().mapToInt(i -> i).sum()/list.size());
        for (int num : list) {
            for (var entry : temp.entrySet()) {
                if (entry.getValue().equals(num)) {
                    _degreesSorted.put(entry.getKey(), num);
                }
            }
        }
//        System.out.println("povodne d (vrchol=degree): " + _degreesMap);
//        System.out.println("povodne d ZORADENE (vrchol=degree): " + _degreesSorted);
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

        int mergeElementIndex = k;
        try{
            int CMerge = countMerge(sortedCopy, mergeElementIndex);
            int CNew = countNew(sortedCopy, mergeElementIndex);
            while(CMerge <= CNew) {
                firstKElements.add(firstKElements.get(0));
                mergeElementIndex++;
                CMerge = countMerge(sortedCopy, mergeElementIndex);
                CNew = countNew(sortedCopy, mergeElementIndex);
            }
            sortedCopy = sortedCopy.subList(mergeElementIndex, sortedCopy.size());
//            int CMerge = countMerge(sortedCopy);
//            int CNew = countNew(sortedCopy);
//
//            sortedCopy = sortedCopy.subList(k, sortedCopy.size());
//            if(CNew >= CMerge){
//                firstKElements.add(sortedCopy.get(0));
//                sortedCopy.remove(0);
//            }
        } catch(IllegalArgumentException ex){ //uz nevieme overovat merge/new
            sortedCopy = sortedCopy.subList(mergeElementIndex, sortedCopy.size());
        } catch(IndexOutOfBoundsException ex){ //sme na konci rekurzie
//            System.out.println("kedy to skoncilo: " + sortedCopy.size());
            return Collections.nCopies(sortedCopy.size(), sortedCopy.get(0));
        }

        return Stream.concat(firstKElements.stream(), GreedyAlgorithm(sortedCopy).stream()).toList();
    }

    private int countMerge(List<Integer> list, int mergeElementIndex) throws IndexOutOfBoundsException, IllegalArgumentException{
        return list.get(0) - list.get(mergeElementIndex) + degAnonCostI(list.subList(mergeElementIndex+1, 2*k+1));
    }

    private int countNew(List<Integer> list, int mergeElementIndex) throws IndexOutOfBoundsException, IllegalArgumentException{
        return degAnonCostI(list.subList(mergeElementIndex, 2*k));
    }

    private SimpleGraph<Integer, DefaultEdge> GraphReconstruction(List<Integer> originalSortedVertexes){
        if(_degreesResult.isEmpty()){
            return null;
        }
        if(_degreesResult.stream().mapToInt(i -> i).sum() % 2 != 0){
            return null;
        }

        // new empty anonymized graph
        SimpleGraph<Integer, DefaultEdge> anonymizedGraph = graphGenerator.GenerateGraphWithXVertexes(1, _degreesResult.size());

        List<Integer> anonymizedVertexes = new ArrayList<>(IntStream.range(1, _degreesResult.size()+1).boxed().toList());
        Collections.shuffle(anonymizedVertexes);

        correspondenceVertexesKToA = new HashMap<>();
        for (var v = 0; v < originalSortedVertexes.size(); v++){
            correspondenceVertexesKToA.put(originalSortedVertexes.get(v), anonymizedVertexes.get(v));
        }
        System.out.println("knowledge vrcholy -> anonymizovane vrcholy: " + correspondenceVertexesKToA);

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
//            var chosenVertex = randomVertexDegIndex+1;
            var chosenVertex = anonymizedVertexes.get(randomVertexDegIndex);
            degreesResultCopy.set(randomVertexDegIndex, 0);

            List<Integer> vertexesIndexesWithHighestDegree = GetVertexesWithHighestDegree(degreesResultCopy, anonymizedVertexes, chosenVertex, randomVertexDeg);

            for (var v :
                    vertexesIndexesWithHighestDegree) {
                anonymizedGraph.addEdge(anonymizedVertexes.get(v), chosenVertex);
                degreesResultCopy.set(v, degreesResultCopy.get(v)-1);
            }
        }
    }

    private SimpleGraph<Integer, DefaultEdge> CopyPasteGraphReconstruction(List<Integer> originalSortedVertexes){
        if(_degreesResult.isEmpty()){
            return null;
        }
        if(_degreesResult.stream().mapToInt(i -> i).sum() % 2 != 0){
            return null;
        }
        // new empty anonymized graph
        SimpleGraph<Integer, DefaultEdge> anonymizedGraph = graphGenerator.GenerateGraphWithXVertexes(1, _degreesResult.size());

        return anonymizedGraph;
    }

    private List<Integer> GetVertexesWithHighestDegree(List<Integer> degrees, List<Integer> anonymizedVertexes, Integer chosenVertex, int chosenVertexDegree){
        List<Integer> vertexesIndexesWithHighestDegree = new ArrayList<>();
        List<Integer> tempVertDegrees = new ArrayList<>(degrees);
        
//        tempVertDegrees.set(chosenVertex-1, -1);

        while(vertexesIndexesWithHighestDegree.size() < chosenVertexDegree){
            var maxDegreeIndex = tempVertDegrees.indexOf(Collections.max(tempVertDegrees));
            vertexesIndexesWithHighestDegree.add(maxDegreeIndex);
            tempVertDegrees.set(maxDegreeIndex, 0);
        }

        return vertexesIndexesWithHighestDegree;
    }
}
