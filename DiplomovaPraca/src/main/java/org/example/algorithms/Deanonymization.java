package org.example.algorithms;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class Deanonymization {

    private Graph<Integer, DefaultEdge> _anonymizedGraph;
    private Graph<Integer, DefaultEdge> _knowledgeGraph;
    private int _numberOfVertexes;
    private Vector<Vector<Double>> _correspondenceMatrix;

    private Double _threshold;

    public Deanonymization() {
    }

    public Deanonymization(Graph<Integer, DefaultEdge> _anonymizedGraph, Graph<Integer, DefaultEdge> _knowledgeGraph, double threshold) {
        this._anonymizedGraph = _anonymizedGraph;
        this._knowledgeGraph = _knowledgeGraph;
        this._numberOfVertexes = _anonymizedGraph.vertexSet().size();
        this._threshold = threshold;
    }

    public Vector<Vector<Double>> get_correspondenceMatrix() {
        return _correspondenceMatrix;
    }

    public void Deanonymize(){
        InitializeCorrespondenceMatrix();

        MapVertexes();
    }

    private void InitializeCorrespondenceMatrix(){
        _correspondenceMatrix = new Vector<Vector<Double>>();

        for (var i = 0; i < _numberOfVertexes; i++){
            _correspondenceMatrix.add(new Vector<>());
            for (var j = 0; j < _numberOfVertexes; j++){
                _correspondenceMatrix.get(i).add((1.0/_numberOfVertexes));
            }
        }

        System.out.println(_correspondenceMatrix);
    }

    private void MapVertexes(){
        Double difference;
        do {
            difference = 0.0;

            //create temporary matrix with copied values of correspondence matrix
            Vector<Vector<Double>> newMatrix = new Vector<>();
            for (var i = 0; i < _numberOfVertexes; i++){
                newMatrix.add(new Vector<>(_correspondenceMatrix.get(i)));
            }

            //count new values of matrix
            for (var i = 0; i < _numberOfVertexes; i++){
                for (var j = 0; j < _numberOfVertexes; j++){
                    newMatrix.get(i).set(j, CountNewMatrixValue(
                            newMatrix.get(i).get(j),
                            Graphs.neighborListOf(_knowledgeGraph, i+1),
                            Graphs.neighborListOf(_anonymizedGraph, j+1)));
                }

                //normalize values
                newMatrix.set(i, NormalizeVector(newMatrix.get(i)));

                //count difference between old and new matrix
                var oldSum = _correspondenceMatrix.get(i).stream().mapToDouble(v1 -> v1).sum();
                var newSum = newMatrix.get(i).stream().mapToDouble(v2 -> v2).sum();
                difference += Math.abs(oldSum - newSum);
            }

            _correspondenceMatrix = newMatrix;
        } while(difference > _threshold);
    }

    private Double CountNewMatrixValue(
            double originalValue,
            List<Integer> neighboursKnowledgeGraph,
            List<Integer> neighboursAnonymizedGraph){
        var similarity = 0.0; //TODO: count similarity with Hungarian algo

        var result = (originalValue + similarity) / (1 + Math.max(neighboursKnowledgeGraph.size(), neighboursAnonymizedGraph.size()));
        return result;
    }

    private Vector<Double> NormalizeVector(Vector<Double> list){
        var sum = list.stream().mapToDouble(v -> v).sum();

        for (final ListIterator<Double> i = list.listIterator(); i.hasNext();) {
            final Double element = i.next();
            i.set(element/sum);
        }

        return list;
    }
}
