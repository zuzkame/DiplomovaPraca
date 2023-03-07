/**
 * @author Zuzana Medzihradska
 */

package org.example;

import org.example.algorithms.Anonymization;

public class DiplomovaPraca {

    public DiplomovaPraca(){
        var anonymization = new Anonymization(2);
        anonymization.AnonymizeGreedy();

    }
    public static void main(String[] args){
        var dp = new DiplomovaPraca();
    }
}
