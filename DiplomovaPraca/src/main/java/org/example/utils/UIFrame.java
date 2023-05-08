package org.example.utils;

import org.example.algorithms.Anonymization;
import org.example.algorithms.Deanonymization;
import org.example.data.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

public class UIFrame extends JFrame {
    private Color successMessageColor = new Color(18,138,66);
    private JPanel panel;
    private JPanel panelGraph;
    private SimpleGraph<Integer, DefaultEdge> g;

    private JPanel panelSimulatedGraph;
    private final JLabel numOfVertexesLabel = new JLabel("Počet vrcholov:");
    private final JLabel numOfEdgesLabel = new JLabel("Počet hrán:");
    private JCheckBox withClusters = new JCheckBox("So zhlukmi");
    private JTextField numOfVertexes = new JTextField(10);
    private JTextField numOfEdges = new JTextField(10);
    private final JButton submitGenerateDataset;
    private JLabel panelSimulatedGraphMessage = new JLabel("");

    private JPanel panelRealGraph;
    private File edgesFile;
    private final JButton chooseEdgesFileButton;
    private final JButton submitLoadDataset;
    private JLabel panelRealGraphMessage = new JLabel("");

    private JPanel panelAnon;
    JRadioButton optionKAnon;
    JRadioButton optionRandomAnon;
    ButtonGroup group = new ButtonGroup();
    private JCheckBox copyPasteReconstruct = new JCheckBox("Spätná rekonštrukcia s obmedzeniami");
    private final JLabel kInputLabel = new JLabel("k:");
    private final JLabel fractionLabel = new JLabel("Počet hrán pre modifikáciu (%):");
    private JTextField kInput = new JTextField(10);
    private JTextField fractionInput = new JTextField(10);
    private final JButton submitAnonymization;
    private final JLabel anonymizationMessage = new JLabel("");
    private Anonymization anonymization;

    private JPanel panelDeanon;
    private final JButton submitDeanonymization;
    private final JLabel deanonymizationRelativeSuccessLabel = new JLabel("Relatívna úspešnosť deanonymizácie: ");
    private final JLabel deanonymizationRelativeSuccess = new JLabel("");
    private final JLabel deanonymizationAbsoluteSuccessLabel = new JLabel("Absolútna úspešnosť deanonymizácie: ");
    private final JLabel deanonymizationAbsoluteSuccess = new JLabel("");
    private final JLabel deanonymizationMessage = new JLabel("");
    private Deanonymization deanonymization;


    public UIFrame(){
        super("Anonymizácia a deanonymizácia");
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panelGraph = new JPanel();
        panelGraph.setLayout(new BoxLayout(panelGraph, BoxLayout.X_AXIS));
        panelSimulatedGraph = new JPanel();
        panelSimulatedGraph.setLayout(new GridBagLayout());
        panelRealGraph = new JPanel();
        panelRealGraph.setLayout(new GridBagLayout());
        panelAnon = new JPanel();
        panelAnon.setLayout(new GridBagLayout());
        panelDeanon = new JPanel();
        panelDeanon.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10,10,10,10);
        panelSimulatedGraph.setSize(500, 500);
        panelRealGraph.setSize(500, 500);
        panelAnon.setSize(1000, 1000);
        panelDeanon.setSize(1000, 500);

        //SECTION 1.1
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSimulatedGraph.add(withClusters, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelSimulatedGraph.add(numOfVertexesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panelSimulatedGraph.add(numOfVertexes, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelSimulatedGraph.add(numOfEdgesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panelSimulatedGraph.add(numOfEdges, gbc);

        submitGenerateDataset = new JButton("Vygenerovať");
        submitGenerateDataset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var valid = ValidateNumOfVertAndEdgesInput();
                panelSimulatedGraphMessage.setVisible(true);
                panelGraph.revalidate();
                pack();
                if(valid){
                    // generate graph
                    if(withClusters.isSelected()){
                        g = GraphGenerator.getInstance().GenerateRandomSocialGraphWithClusters(
                                Integer.parseInt(numOfVertexes.getText()),
                                Integer.parseInt(numOfEdges.getText()));
                    }
                    else{
                        g = GraphGenerator.getInstance().GenerateRandomSocialGraph(
                                Integer.parseInt(numOfVertexes.getText()),
                                Integer.parseInt(numOfEdges.getText()));
                    }
                    if(g == null){
                        panelSimulatedGraphMessage.setText("Graf sa nepodarilo zostrojiť. Skúste to znova.");
                        panelSimulatedGraphMessage.setForeground(Color.RED);
                    }else{
                        submitLoadDataset.setEnabled(false);
                        submitGenerateDataset.setEnabled(false);
                        submitAnonymization.setEnabled(true);
                        panelSimulatedGraphMessage.setText("Graf bol úspešne zostrojený.");
                        panelSimulatedGraphMessage.setForeground(successMessageColor);
                    }
                    panelSimulatedGraphMessage.setVisible(true);
                    panelGraph.revalidate();
                    pack();
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panelSimulatedGraph.add(submitGenerateDataset, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panelSimulatedGraphMessage.setVisible(false);
        panelSimulatedGraph.add(panelSimulatedGraphMessage, gbc);

        //SECTION 1.2
        chooseEdgesFileButton = new JButton("Vybrať súbor");
        chooseEdgesFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV file", "csv");
                fileChooser.setFileFilter(filter); //nastavi filter pre typ suborov "csv"
                int showReturn = fileChooser.showOpenDialog(null); //otvori okno na vyber file-u
                if(showReturn == JFileChooser.APPROVE_OPTION){
                    edgesFile = fileChooser.getSelectedFile();
                    chooseEdgesFileButton.setText(edgesFile.getName());
                    panelGraph.revalidate();
                    pack();
                }
            }
        });
//        gbc.gridwidth = 6;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panelRealGraph.add(chooseEdgesFileButton, gbc);

        submitLoadDataset = new JButton("Načítať");
        submitLoadDataset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!ValidateEdgesFile()){
                    panelRealGraphMessage.setVisible(true);
                }else{
                    panelRealGraphMessage.setVisible(false);

                    g = GraphGenerator.getInstance().LoadGraphFromFile(edgesFile);

                    if(g == null){
                        panelRealGraphMessage.setText("Graf sa nepodarilo zostrojiť. Skúste to znova.");
                        panelRealGraphMessage.setForeground(Color.RED);
                    }else{
                        System.out.println("Zoznam hrán vstupného grafu: \n" + g.edgeSet());
                        submitGenerateDataset.setEnabled(false);
                        submitLoadDataset.setEnabled(false);
                        submitAnonymization.setEnabled(true);
                        panelRealGraphMessage.setText("Graf bol úspešne zostrojený.");
                        panelRealGraphMessage.setForeground(successMessageColor);
                    }
                    panelRealGraphMessage.setVisible(true);
                }
                panelRealGraphMessage.revalidate();
                pack();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelRealGraph.add(submitLoadDataset, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panelRealGraphMessage.setVisible(false);
        panelRealGraph.add(panelRealGraphMessage, gbc);

        panelSimulatedGraph.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Náhodný simulovaný dataset"));
        panelRealGraph.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Vlastný dataset"));
        panelGraph.add(panelSimulatedGraph);
        panelGraph.add(panelRealGraph);
        panelGraph.revalidate();

        //SECTION 2
        optionKAnon = new JRadioButton("k-stupňová anonymizácia");
        optionKAnon.setSelected(true);
        optionRandomAnon = new JRadioButton("Náhodná anonymizácia");
        group.add(optionKAnon);
        group.add(optionRandomAnon);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelAnon.add(optionKAnon, gbc);

        gbc.gridx = 1;
        panelAnon.add(optionRandomAnon, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelAnon.add(kInputLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelAnon.add(kInput, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panelAnon.add(fractionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panelAnon.add(fractionInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelAnon.add(copyPasteReconstruct, gbc);

        submitAnonymization = new JButton("Anonymizovať");
        submitAnonymization.setEnabled(false);
        submitAnonymization.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(optionKAnon.isSelected()){
                    var valid = ValidateKInput();
                    anonymizationMessage.setVisible(true);
                    panelAnon.revalidate();
                    pack();
                    if(valid){
                        anonymization = new Anonymization(
                                Integer.parseInt(kInput.getText()));
                        if(copyPasteReconstruct.isSelected()){
                            anonymization.AnonymizeGreedy(g, true);
                        }else{
                            anonymization.AnonymizeGreedy(g, false);
                        }
                        if(anonymization.get_anonymizedGraphResult() == null){
                            anonymizationMessage.setText("Graf sa nedá zostrojiť.");
                            anonymizationMessage.setForeground(Color.RED);
                        }
                        else{
                            anonymizationMessage.setText("Anonymizácia prebehla úspešne.");
                            anonymizationMessage.setForeground(successMessageColor);
                            submitAnonymization.setEnabled(false);
                            submitDeanonymization.setEnabled(true);
                        }
                    }
                } else if (optionRandomAnon.isSelected()) {
                    var valid = ValidateFractionInput();
                    anonymizationMessage.setVisible(true);
                    panelAnon.revalidate();
                    pack();
                    if(valid){
                        anonymization = new Anonymization();
                        anonymization.AnonymizeRandom(g, Integer.parseInt(fractionInput.getText())/100.0);
                        if(anonymization.get_anonymizedGraphResult() == null){
                            anonymizationMessage.setText("Graf sa nedá zostrojiť.");
                            anonymizationMessage.setForeground(Color.RED);
                        }
                        else{
                            anonymizationMessage.setText("Anonymizácia prebehla úspešne.");
                            anonymizationMessage.setForeground(successMessageColor);
                            submitAnonymization.setEnabled(false);
                            submitDeanonymization.setEnabled(true);
                        }
                    }
                }
                panelAnon.revalidate();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelAnon.add(submitAnonymization, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        anonymizationMessage.setVisible(false);
        panelAnon.add(anonymizationMessage, gbc);
        panelAnon.revalidate();

        submitDeanonymization = new JButton("Deanonymizovať");
        submitDeanonymization.setEnabled(false);
        submitDeanonymization.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deanonymization = new Deanonymization(anonymization.get_anonymizedGraphResult(), anonymization.get_originalGraph(), 0.03);
                deanonymization.Deanonymize();
                var count = 0.0;
                var countWithPocetnost = 0.0;
                var mapping = anonymization.getCorrespondenceVertexesKToA();
                for(var r=0; r < deanonymization.get_numberOfVertexes(); r++){
                    var maxvalue = Arrays.stream(deanonymization.get_correspondenceMatrix()[r]).max().getAsDouble();
                    if (maxvalue == deanonymization.get_correspondenceMatrix()[r][mapping.get(r+1)-1]){
                        count++;
                        var freq = 0;
                        for(var c : deanonymization.get_correspondenceMatrix()[r]){
                            if(c == maxvalue)   freq++;
                        }
                        countWithPocetnost += 1.0/freq;
                    }
                    System.out.println("maxValue: " + Arrays.stream(deanonymization.get_correspondenceMatrix()[r]).max());
                    System.out.println("\n");
                }
                var relativeSuccess = count/deanonymization.get_numberOfVertexes()*100.0;
                var absoluteSuccess = countWithPocetnost/deanonymization.get_numberOfVertexes()*100.0;

                deanonymizationRelativeSuccessLabel.setVisible(true);
                deanonymizationRelativeSuccess.setText(relativeSuccess + "%");
                deanonymizationRelativeSuccess.setVisible(true);
                deanonymizationAbsoluteSuccessLabel.setVisible(true);
                deanonymizationAbsoluteSuccess.setText(absoluteSuccess + "%");
                deanonymizationAbsoluteSuccess.setVisible(true);
                panelDeanon.revalidate();
                pack();
                System.out.println("pocet iteracii: " + deanonymization.getNumOfIterations());
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panelDeanon.add(submitDeanonymization, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        deanonymizationRelativeSuccessLabel.setVisible(false);
        panelDeanon.add(deanonymizationRelativeSuccessLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panelDeanon.add(deanonymizationRelativeSuccess, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        deanonymizationAbsoluteSuccessLabel.setVisible(false);
        panelDeanon.add(deanonymizationAbsoluteSuccessLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panelDeanon.add(deanonymizationAbsoluteSuccess, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        deanonymizationMessage.setVisible(false);
        panelDeanon.add(deanonymizationMessage, gbc);
        panelDeanon.revalidate();

        panelGraph.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "1. Výber datasetu"));
        panelAnon.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "2. Anonymizácia"));
        panelDeanon.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "3. Deanonymizácia"));

        panel.add(panelGraph);
        panel.add(panelAnon);
        panel.add(panelDeanon);

        this.add(panel);
        this.pack();
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private boolean ValidateNumOfVertAndEdgesInput(){
        try{
            int vertexes = Integer.parseInt(numOfVertexes.getText());
            int edges = Integer.parseInt(numOfEdges.getText());

            if(vertexes < 1 || edges < 0){
                throw new IllegalArgumentException();
            }
            if(vertexes*(vertexes-1)/2 < edges){
                panelSimulatedGraphMessage.setText("Príliš veľa hrán.");
                panelSimulatedGraphMessage.setForeground(Color.RED);
                return false;
            }
            panelSimulatedGraphMessage.setText("Graf bol úspešne vygenerovaný.");
            panelSimulatedGraphMessage.setForeground(successMessageColor);
            return true;
        } catch(IllegalArgumentException e){
            panelSimulatedGraphMessage.setText("Počet vrcholov aj hrán musí byť kladné celé číslo.");
        }
        panelSimulatedGraphMessage.setForeground(Color.RED);
        return false;
    }

    private boolean ValidateEdgesFile(){
        try{
            if(edgesFile == null || !edgesFile.isFile()){
                throw new IllegalArgumentException();
            }
            panelRealGraphMessage.setForeground(successMessageColor);
            return true;
        }catch (SecurityException e){
            panelRealGraphMessage.setText(e.getMessage());
            panelRealGraphMessage.setForeground(Color.RED);
            return false;
        }catch (IllegalArgumentException e){
            panelRealGraphMessage.setText("Súbor nebol vybratý alebo neexistuje.");
            panelRealGraphMessage.setForeground(Color.RED);
            return false;
        }
    }

    private boolean ValidateKInput(){
        try{
            int k = Integer.parseInt(kInput.getText());
            if(k<2 || k > g.vertexSet().size()){
                throw new IllegalArgumentException();
            }
            anonymizationMessage.setText("Prebieha anonymizácia...");
            anonymizationMessage.setForeground(successMessageColor);
            return true;
        }catch(IllegalArgumentException e){
            anonymizationMessage.setText("Konštanta 'k' musí byť celé číslo väčšie ako 2" +
                    " a menšie ako " + g.vertexSet().size() + ".");
        }
        anonymizationMessage.setForeground(Color.RED);
        return false;
    }

    private boolean ValidateFractionInput(){
        try{
            double fraction = Double.parseDouble(fractionInput.getText());
            if(fraction<=0.0 || fraction>100.0){
                throw new IllegalArgumentException();
            }
            anonymizationMessage.setText("Prebieha anonymizácia...");
            anonymizationMessage.setForeground(successMessageColor);
            return true;
        }catch (IllegalArgumentException e){
            anonymizationMessage.setText("Počet hrán (%) nesmie byť menší ako 0 a väčší ako 100.");
        }
        anonymizationMessage.setForeground(Color.RED);
        return false;
    }
}
