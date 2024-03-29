package org.example.algorithms;

import org.example.data.GraphGenerator;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
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
    private Map<Integer, Integer> _degreesSorted;
    private List<Integer> _degreesResult;
    private Map<Integer, Integer> _degreesResultMap;

    private Graph<Integer, DefaultEdge> _anonymizedGraphResult;
    private Graph<Integer, DefaultEdge> _originalGraph;

    private Map<Integer, Integer> correspondenceVertexesKToA;
    private static GraphGenerator graphGenerator = GraphGenerator.getInstance();

    public Anonymization(){
        this(0);
    }

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
        AnonymizeGreedy(null, false);
    }

    public void AnonymizeGreedy(SimpleGraph<Integer, DefaultEdge> g, boolean copyPasteReconstruct){
        if(k <= 1) return;
        try{
            GraphInitialization(g);
            var degreesSortedValues = new ArrayList<Integer>();
            var degreesSortedKeys = new ArrayList<Integer>();
            for (var entry: _degreesSorted.entrySet()){
                degreesSortedValues.add(entry.getValue());
                degreesSortedKeys.add(entry.getKey());
            }
            System.out.println("Pocetnost stupnov:" + degreesSortedValues.stream().collect(Collectors.groupingBy(e -> e.toString(),Collectors.counting())));
            _degreesResult = GreedyAlgorithm(new ArrayList<>(degreesSortedValues));

            _degreesResultMap = new LinkedHashMap<>();
            for(var i = 0; i < _degreesResult.size(); i++){
                _degreesResultMap.put(degreesSortedKeys.get(i), _degreesResult.get(i));
            }
            System.out.println("Anonymizovane d* (vrchol = degree): " + _degreesResultMap);
            System.out.println("ANONYMIZOVANY GRAF");
            System.out.println("max stupen: " + _degreesResult.get(0));
            System.out.println("min stupen: " + _degreesResult.get(_degreesResult.size()-1));
            System.out.println("median: " + (_degreesResult.size()%2==0 ? ( _degreesResult.get(_degreesResult.size()/2 -1) + _degreesResult.get(_degreesResult.size()/2)) / 2 : _degreesResult.get((_degreesResult.size()+1) / 2 - 1)));
            System.out.println("priemer: " + _degreesResult.stream().mapToInt(i -> i).sum()/_degreesResult.size());

            _anonymizedGraphResult = copyPasteReconstruct ?
                    CopyPasteGraphReconstruction(degreesSortedKeys) : GraphReconstruction(degreesSortedKeys);
            if (_anonymizedGraphResult == null) {
                System.out.println("Graf sa neda zostrojit");
                return;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void GraphInitialization() throws URISyntaxException{
        GraphInitialization(null);
    }

    private void GraphInitialization(SimpleGraph<Integer, DefaultEdge> g) throws URISyntaxException {
        _originalGraph = g;
        setDegreeList(g);
        setSortedDegreeList();
    }

    private void setDegreeList(SimpleGraph<Integer, DefaultEdge> graph){
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
        System.out.println("Povodne d (vrchol = degree): " + _degreesMap);
        System .out.println("Povodne d ZORADENE (vrchol = degree): " + _degreesSorted);
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
        } catch(IllegalArgumentException ex){ //uz nevieme overovat merge/new
            sortedCopy = sortedCopy.subList(mergeElementIndex, sortedCopy.size());
        } catch(IndexOutOfBoundsException ex){ //sme na konci rekurzie
            return Collections.nCopies(sortedCopy.size(), sortedCopy.get(0));
        }

        return Stream.concat(firstKElements.stream(), GreedyAlgorithm(sortedCopy).stream()).toList();
    }

    public void AnonymizeRandom(SimpleGraph<Integer, DefaultEdge> g, double modificationFraction){
        try{
            GraphInitialization(g);
        }catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Random rand = new Random();
        int numOfVertexes = _originalGraph.vertexSet().size();
        List<Integer> anonymizedVertexes = new ArrayList<>(IntStream.range(1, numOfVertexes+1).boxed().toList());
        Collections.shuffle(anonymizedVertexes);
        SimpleGraph<Integer, DefaultEdge> anonymizedGraph = graphGenerator.GenerateGraphWithXVertexes(1, numOfVertexes);
        correspondenceVertexesKToA = new LinkedHashMap<>();
        for (var i = 1; i <= numOfVertexes; i++){
            correspondenceVertexesKToA.put(i, anonymizedVertexes.get(i-1));
        }

        //create copy of original graph with corresponding vertexes
        for(var entry : _originalGraph.edgeSet()){
            anonymizedGraph.addEdge(correspondenceVertexesKToA.get(_originalGraph.getEdgeSource(entry)),
                    correspondenceVertexesKToA.get(_originalGraph.getEdgeTarget(entry)));
        }

        int edgesModificationNumber = (int)Math.round(_originalGraph.edgeSet().size() * modificationFraction);

        //delete edges
        Set<Set<Integer>> deletedEdges = new HashSet<>();
        while (deletedEdges.size()<edgesModificationNumber){
            int v1 = rand.nextInt(1, numOfVertexes+1);
            if(anonymizedGraph.degreeOf(v1) == 0){
                continue;
            }
            int v2 = Graphs.neighborListOf(anonymizedGraph, v1).get(rand.nextInt(0, anonymizedGraph.degreeOf(v1)));

            var edge = anonymizedGraph.getEdge(v1,v2);
            deletedEdges.add(new HashSet<>(Arrays.asList(anonymizedGraph.getEdgeSource(edge), anonymizedGraph.getEdgeTarget(edge))));
            anonymizedGraph.removeEdge(edge);
        }
        System.out.println("Pocet vymazanych hran: " + deletedEdges.size());

        //add edges
        int countAddedEdges = 0;
        while (countAddedEdges < edgesModificationNumber){
            int v1;
            int v2;
            do{
                v1 = rand.nextInt(1, numOfVertexes+1);
                v2 = rand.nextInt(1, numOfVertexes+1);
            }while(v1 == v2 || anonymizedGraph.getEdge(v1,v2) != null);

            var hasBeenDeleted = false;
            for(var s : deletedEdges){
                //cannot add edge, which has already been deleted
                if(s.containsAll(Arrays.asList(v1,v2))){
                    hasBeenDeleted = true;
                    break;
                }
            }
            if(!hasBeenDeleted){
                anonymizedGraph.addEdge(v1, v2);
                countAddedEdges += 1;
            }
        }
        System.out.println("Pocet pridanych hran: " + countAddedEdges);
        _anonymizedGraphResult = anonymizedGraph;
        if (_anonymizedGraphResult == null) {
            System.out.println("Graf sa neda zostrojit");
        }

        List<Integer> degrees = new ArrayList<>();
        for(var v: anonymizedGraph.vertexSet()){
            degrees.add(anonymizedGraph.degreeOf(v));
        }
        Collections.sort(degrees);

        System.out.println("ANONYMIZOVANY GRAF");
        System.out.println("max stupen: " + degrees.get(numOfVertexes-1));
        System.out.println("min stupen: " + degrees.get(0));
        System.out.println("median: " + (degrees.size()%2==0 ? ( degrees.get(degrees.size()/2 -1) + degrees.get(degrees.size()/2)) / 2 : degrees.get((degrees.size()+1) / 2 - 1)));
        System.out.println("priemer: " + degrees.stream().mapToInt(i -> i).sum()/degrees.size());
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
        System.out.println("knowledge vrcholy -> anonymizovane vrcholy (skutocne): " + correspondenceVertexesKToA);

        List<Integer> degreesResultCopy = new ArrayList<>(_degreesResult);
        while(true){
            if(degreesResultCopy.stream().filter(x -> x < 0).toList().size() > 0){
//                System.out.println("degree result " + _degreesResult);
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
            var chosenVertex = anonymizedVertexes.get(randomVertexDegIndex);
            degreesResultCopy.set(randomVertexDegIndex, 0);

            List<Integer> vertexesIndexesWithHighestDegree = GetVertexesWithHighestDegree(degreesResultCopy,anonymizedVertexes, anonymizedGraph, chosenVertex, randomVertexDeg);

            for (var v :
                    vertexesIndexesWithHighestDegree) {
                anonymizedGraph.addEdge(anonymizedVertexes.get(v), chosenVertex);
                degreesResultCopy.set(v, degreesResultCopy.get(v)-1);
            }
        }
    }

    private SimpleGraph<Integer, DefaultEdge> CopyPasteGraphReconstruction(List<Integer> originalSortedVertexes){
        if(_degreesResult.isEmpty()){
            System.out.println("Vysledok je prazdny");

            return null;
        }
        if(_degreesResult.stream().mapToInt(i -> i).sum() % 2 != 0){
            System.out.println("Sucet stupnov je neparny");

            return null;
        }
        // new empty anonymized graph
        SimpleGraph<Integer, DefaultEdge> anonymizedGraph = graphGenerator.GenerateGraphWithXVertexes(1, _degreesResult.size());
        List<Integer> anonymizedVertexes = new ArrayList<>(IntStream.range(1, _degreesResult.size()+1).boxed().toList());
        Collections.shuffle(anonymizedVertexes);
        var degreesSortedList = _degreesSorted.values().stream().toList();
        var degreeDifferenceMap = new LinkedHashMap<Integer, Integer>();
        var degreeDifferenceSorted = new LinkedHashMap<Integer, Integer>();

        correspondenceVertexesKToA = new LinkedHashMap<>();
        for (var i = 0; i < originalSortedVertexes.size(); i++){
            correspondenceVertexesKToA.put(originalSortedVertexes.get(i), anonymizedVertexes.get(i));
            var dif = _degreesResult.get(i) - degreesSortedList.get(i);
            degreeDifferenceMap.put(anonymizedVertexes.get(i), dif);
        }
        System.out.println("Vektor a (anon. vrchol = stupen): " + degreeDifferenceMap);


        List<Integer> values = new ArrayList<>(degreeDifferenceMap.values().stream().toList());
        Collections.sort(values);
        Collections.reverse(values);

        for(var num : values){
            for (var entry : degreeDifferenceMap.entrySet()) {
                if (!degreeDifferenceSorted.containsKey(entry.getKey()) && entry.getValue().equals(num)) {
                    degreeDifferenceSorted.put(entry.getKey(), num);
                }
            }
        }

        if(!CanConstructGraph(degreeDifferenceSorted)){
            return null;
        }

        //create copy of original graph with corresponding vertexes
        for(var entry : _originalGraph.edgeSet()){
            anonymizedGraph.addEdge(correspondenceVertexesKToA.get(_originalGraph.getEdgeSource(entry)),
                    correspondenceVertexesKToA.get(_originalGraph.getEdgeTarget(entry)));
        }

        var degreeDifference = new ArrayList<>(degreeDifferenceMap.values().stream().toList());
        while(true){
            if(degreeDifference.stream().filter(x -> x < 0).toList().size() > 0){
                return null;
            }
            if(degreeDifference.stream().filter(x -> x == 0).toList().size() == degreeDifference.size()){
                return anonymizedGraph;
            }

            int randomVertexDegIndex;
            int randomVertexDeg;
            do{
                randomVertexDegIndex = new Random().nextInt(degreeDifference.size());
            } while (degreeDifference.get(randomVertexDegIndex) == 0);
            randomVertexDeg = degreeDifference.get(randomVertexDegIndex);
            var chosenVertex = anonymizedVertexes.get(randomVertexDegIndex);
            degreeDifference.set(randomVertexDegIndex, 0);

            List<Integer> vertexesIndexesWithHighestDegree = GetVertexesWithHighestDegree(degreeDifference,anonymizedVertexes, anonymizedGraph, chosenVertex, randomVertexDeg);

            for (var v :
                    vertexesIndexesWithHighestDegree) {
                anonymizedGraph.addEdge(anonymizedVertexes.get(v), chosenVertex);
                degreeDifference.set(v, degreeDifference.get(v)-1);
            }
        }
    }

    private boolean CanConstructGraph(LinkedHashMap<Integer, Integer> degreesDifference){
        var numOfVertexes = degreesDifference.size();
        var allVertexes = degreesDifference.keySet().stream().toList();
        var swappedCorrespondenceVertexes_AtoK = new LinkedHashMap<Integer, Integer>();
        for(var entry: correspondenceVertexesKToA.entrySet()){
            swappedCorrespondenceVertexes_AtoK.put(entry.getValue(), entry.getKey());
        }
        for(var i=0; i<numOfVertexes-1; i++){
            List<Integer> vertexesL = new ArrayList<>(allVertexes.subList(0, i+1));
            var l = vertexesL.size();
            List<Integer> vertexesComplement = new ArrayList<>(allVertexes.subList(i+1, numOfVertexes));

            // count L.H.S. of Equation; count R.H.S. of Equation part 1
            var sumLHS = 0;
            var sumRHS1 = 0;
            for(var v : vertexesL){
                sumLHS += degreesDifference.get(v);

                var neighboursSet = Graphs.neighborSetOf(_originalGraph, swappedCorrespondenceVertexes_AtoK.get(v));
                var vDegree = 0;
                for (var V:vertexesL){
                    if (neighboursSet.contains(swappedCorrespondenceVertexes_AtoK.get(V)))  vDegree++;
                }
                sumRHS1 += l - 1 - vDegree;
            }

            //count R.H.S. of Equation part 2
            var sumRHS2 = 0;
            for(var v:vertexesComplement){
                var neighboursSet = Graphs.neighborSetOf(_originalGraph, swappedCorrespondenceVertexes_AtoK.get(v));
                var vDegree = 0;
                for (var V:vertexesL){
                    if (neighboursSet.contains(swappedCorrespondenceVertexes_AtoK.get(V)))  vDegree++;
                }

                sumRHS2 += Math.min(l-vDegree, degreesDifference.get(v));
            }

            if(sumLHS <= sumRHS1+sumRHS2)   return true;
        }
        return false;
    }

    private List<Integer> GetVertexesWithHighestDegree(List<Integer> degrees, List<Integer> anonymizedVertexes, Graph<Integer, DefaultEdge> anonymizedGraph, Integer chosenVertex, int chosenVertexDegree){
        List<Integer> vertexesIndexesWithHighestDegree = new ArrayList<>();
        List<Integer> tempVertDegrees = new ArrayList<>(degrees);
        
//        tempVertDegrees.set(chosenVertex-1, -1);

        while(vertexesIndexesWithHighestDegree.size() < chosenVertexDegree){
            var maxDegreeIndex = tempVertDegrees.indexOf(Collections.max(tempVertDegrees));
            if(anonymizedVertexes.get(maxDegreeIndex) != chosenVertex && anonymizedGraph.getEdge(chosenVertex, anonymizedVertexes.get(maxDegreeIndex)) == null){
                vertexesIndexesWithHighestDegree.add(maxDegreeIndex);
            }
            tempVertDegrees.set(maxDegreeIndex, -1);
        }

        return vertexesIndexesWithHighestDegree;
    }
}
