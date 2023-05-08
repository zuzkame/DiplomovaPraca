package org.example.utils;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.util.List;

public class FileReader {
    private URI PathToCsvFile;
    private List<List<Integer>> Edges;
    private int MaxNumber;
    private int MinNumber;

    public FileReader(){}
    public FileReader(URI pathToCsvFile){
        setPathToCsvFile(pathToCsvFile);
    }

    public URI getPathToCsvFile() {
        return PathToCsvFile;
    }

    public void  setPathToCsvFile(URI pathToCsvFile) {
        PathToCsvFile = pathToCsvFile;
    }

    public List<List<Integer>> getEdges() {
        return Edges;
    }

    public void setEdges(List<List<Integer>> edges) {
        Edges = edges;
    }

    public int getMaxNumber() {
        return MaxNumber;
    }
    public int getMinNumber() {
        return MinNumber;
    }

    private void setMaxNumber(int maxNumber) {
        MaxNumber = maxNumber;
    }

    private void setMinNumber(int minNumber) {
        MinNumber = minNumber;
    }

    /**
     * Load csv with edges.
     * @return List of integer tuples
     */
    public List<List<Integer>> getEdgesListFromCsv(File edges) throws IllegalArgumentException{
        var listOfEdges = new ArrayList<List<Integer>>();
        try{
//            Scanner sc = new Scanner(new File(PathToCsvFile));
            Scanner sc = new Scanner(edges);
            sc.useDelimiter(",");
            int max = 0;
            int min = 1;
            while(sc.hasNextLine()){
                List<Integer> tuple = new ArrayList<>();
                for(String v : sc.nextLine().split(",")){
                    tuple.add(Integer.parseInt(v));
                }
                if(tuple.contains(0))   min = 0;
                listOfEdges.add(tuple);
                max = Math.max(max, Collections.max(tuple));
            }

            setMaxNumber(max);
            setMinNumber(min);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return listOfEdges;
    }
}
