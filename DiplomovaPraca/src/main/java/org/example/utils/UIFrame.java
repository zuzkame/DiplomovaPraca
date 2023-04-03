package org.example.utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UIFrame extends JFrame {
    private JPanel panel;
    private File edgesFile;
    private final JButton chooseEdgesFileButton;

    public UIFrame(){
        super("Anonymizácia a deanonymizácia");
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.setSize(500, 700);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
//                    chooseSecretImgBut.setEnabled(true);
                }
            }
        });
        gbc.gridwidth = 6;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(chooseEdgesFileButton, gbc);



        this.add(panel);
        this.pack();
    }
}
