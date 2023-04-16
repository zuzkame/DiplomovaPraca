/**
@see https://www.javaguides.net/2021/01/jfreechart-tutorial-create-charts-in-java.html
**/

package org.example.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartUtil extends JFrame {
//    public CategoryDataset dataset;
    public PieDataset dataset;
    public JFreeChart barChart;
    private static ChartUtil instance = null;
    private ChartUtil(){}
    public static ChartUtil getInstance(){
        if(instance == null){
            instance = new ChartUtil();
        }
        return instance;
    }

    public void createChartPocetnostVrcholovSoStupnom(List<Integer> degreesList, String chartTitle){
        var countMap = degreesList.stream().collect(Collectors.groupingBy(e -> e,Collectors.counting()));
        String rowKey = "Počet vrcholov";
        CreateDatasetFromCountMap(countMap, rowKey);
        CreateChart("Stupeň", rowKey, chartTitle);
    }

    private void CreateDatasetFromCountMap(Map<Integer, Long> countMap, String rowKey){
//        DefaultCategoryDataset data = new DefaultCategoryDataset();
//
//        for(var entry: countMap.entrySet()){
//            data.setValue(entry.getValue(), rowKey, entry.getKey());
//        }

        DefaultPieDataset data = new DefaultPieDataset();
        for(var entry: countMap.entrySet()){
            data.setValue(entry.getKey(), entry.getValue());
        }
        dataset = data;
    }

    private void CreateChart(String x_title, String rowKey, String chartTitle){
//        barChart = ChartFactory.createBarChart(
//                chartTitle,
//                x_title,
//                rowKey,
//                dataset,
//                PlotOrientation.VERTICAL,
//                true, true, false
//        );

        barChart = ChartFactory.createPieChart(
                chartTitle,
                dataset,
                false, true, false
        );
    }

    public void ShowChart(String windowTitle){
        initUI(windowTitle);
        setVisible(true);
    }

    private void initUI(String windowTitle){

        //customize chart colors
        PiePlot cplot = (PiePlot) barChart.getPlot();
        cplot.setBackgroundPaint(Color.lightGray);
//        ((BarRenderer)cplot.getRenderer()).setBarPainter(new StandardBarPainter());
//        BarRenderer r = (BarRenderer)barChart.getCategoryPlot().getRenderer();
//        r.setSeriesPaint(0, Color.darkGray);
//        ((NumberAxis)cplot.getRangeAxis()).setTickUnit(new NumberTickUnit(1.0));


        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setMouseZoomable(true);
        add(chartPanel);

        pack();
        setTitle(windowTitle);
        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
