package org.example.algorithms;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedMatching;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

public class Deanonymization {

    private Graph<Integer, DefaultEdge> _anonymizedGraph;
    private Graph<Integer, DefaultEdge> _knowledgeGraph;
    private int _numberOfVertexes;
    private double[][] _correspondenceMatrix;

    private Double _threshold;
    private int numOfIterations = 0;

    public Deanonymization() {
    }

    public Deanonymization(Graph<Integer, DefaultEdge> _anonymizedGraph, Graph<Integer, DefaultEdge> _knowledgeGraph, double threshold) {
        this._anonymizedGraph = _anonymizedGraph;
        this._knowledgeGraph = _knowledgeGraph;
        this._numberOfVertexes = _anonymizedGraph.vertexSet().size();
        this._threshold = threshold;
    }

    public double[][] get_correspondenceMatrix() {
        return _correspondenceMatrix;
    }

    public int get_numberOfVertexes() {
        return _numberOfVertexes;
    }

    public int getNumOfIterations() {
        return numOfIterations;
    }

    public void Deanonymize(){
        InitializeCorrespondenceMatrix();

        MapVertexes();
    }

    private void InitializeCorrespondenceMatrix(){
        _correspondenceMatrix = new double[_numberOfVertexes][_numberOfVertexes];

        for (var i = 0; i < _numberOfVertexes; i++){
            for (var j = 0; j < _numberOfVertexes; j++){
                _correspondenceMatrix[i][j] = (1.0/_numberOfVertexes);
            }
        }

//        System.out.println(_correspondenceMatrix);
    }

    private void MapVertexes(){
        double difference;
        do {
            numOfIterations++;
            difference = 0.0;

            //count new values of matrix
            for (var i = 0; i < _numberOfVertexes; i++){
                //create temporary row with copied values of correspondence matrix row
                var newMatrixRow = Arrays.copyOf(_correspondenceMatrix[i], _numberOfVertexes);
                for (var j = 0; j < _numberOfVertexes; j++){
                    newMatrixRow[j] = CountNewMatrixValue(
                            newMatrixRow[j],
                            Graphs.neighborListOf(_knowledgeGraph, i+1),
                            Graphs.neighborListOf(_anonymizedGraph, j+1));
                }

                //normalize values
                newMatrixRow = NormalizeVector(newMatrixRow);

                //count difference between old and new matrix
                for(var k = 0; k < _numberOfVertexes; k++){
                    difference += Math.abs(Math.pow(_correspondenceMatrix[i][k], 2) - Math.pow(newMatrixRow[k], 2));
                }
                _correspondenceMatrix[i] = newMatrixRow;
            }
            System.out.println("rozdiel: " + difference);
        } while(difference > _threshold);
    }

    private double CountNewMatrixValue(
            double oldValue,
            List<Integer> neighboursKnowledgeGraph,
            List<Integer> neighboursAnonymizedGraph){

        Set<String> setKnowledge = new HashSet<>();
        for(var k : neighboursKnowledgeGraph){
            setKnowledge.add(k+"K");
        }
        Set<String> setAnonymized = new HashSet<>();
        for(var a : neighboursAnonymizedGraph){
            setAnonymized.add(a+"A");
        }
        var bipartiteGraph = CreateBipartiteWeightedGraph(neighboursKnowledgeGraph, neighboursAnonymizedGraph);

        var similarity = CountMaxMatchingValue(bipartiteGraph, setKnowledge, setAnonymized);

        var result = (oldValue + similarity) / (1.0 + Math.max(neighboursKnowledgeGraph.size(), neighboursAnonymizedGraph.size()));
        return result;
    }

    private double[] NormalizeVector(double[] list){
        var sum = Arrays.stream(list).sum();

        for (var d = 0; d<list.length; d++) {
            list[d] = list[d]/sum;
        }

        return list;
    }

    private Double CountMaxMatchingValue(
            Graph<String, DefaultWeightedEdge> g,
            Set<String> setKnowledge,
            Set<String> setAnonymized){
        var maxMatching = new MaximumWeightBipartiteMatching<String, DefaultWeightedEdge>(g, setKnowledge, setAnonymized);
        maxMatching.getMatching();
        return maxMatching.getMatchingWeight().doubleValue();
    }

    /***
     * Creates complete weighted bipartite graph given lists of required vertexes partitions.
     * @param neighboursKnowledgeGraph partition 1
     * @param neighboursAnonymizedGraph partition 2
     * @return Complete weighted bipartite graph
     */
    private Graph<String, DefaultWeightedEdge> CreateBipartiteWeightedGraph(
            List<Integer> neighboursKnowledgeGraph,
            List<Integer> neighboursAnonymizedGraph){
        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        for (var i : neighboursAnonymizedGraph) {
            g.addVertex(i+"A");
        }

        for (var j: neighboursKnowledgeGraph) {
            g.addVertex(j+"K");
            for (var a : neighboursAnonymizedGraph) {
                g.addEdge(j+"K", a+"A");
                g.setEdgeWeight(j+"K", a+"A", _correspondenceMatrix[j-1][a-1]);
            }
        }

        return g;
    }
}
