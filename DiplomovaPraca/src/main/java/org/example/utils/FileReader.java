package org.example.utils;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.util.List;

public class FileReader {
    private String PathToCsvFile;
    private List<List<Integer>> Edges;
    private int MaxNumber;

    public FileReader(){}
    public FileReader(String pathToCsvFile){
        setPathToCsvFile(pathToCsvFile);
    }

    public String getPathToCsvFile() {
        return PathToCsvFile;
    }

    public void setPathToCsvFile(String pathToCsvFile) {
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

    private void setMaxNumber(int maxNumber) {
        MaxNumber = maxNumber;
    }

    /**
     * Load csv with edges.
     * @return List of integer tuples
     */
    public List<List<Integer>> getEdgesListFromCsv(){
        var listOfEdges = new ArrayList<List<Integer>>();
        try{
            Scanner sc = new Scanner(new File(PathToCsvFile));
            sc.useDelimiter(",");
            int max = 0;
            while(sc.hasNextLine()){
                List<Integer> tuple = new ArrayList<>();
                for(String v : sc.nextLine().split(",")){
                    tuple.add(Integer.parseInt(v));
                }
                listOfEdges.add(tuple);
                max = Math.max(max, Collections.max(tuple));
            }
            setMaxNumber(max);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return listOfEdges;
    }
}
