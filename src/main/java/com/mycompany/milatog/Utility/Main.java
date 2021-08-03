/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.milatog.Utility;

import Utility.Dati;
import static Utility.Dati.Costo_stoccaggio;
import static Utility.Dati.Distanze;
import static Utility.Dati.Domande;
import static Utility.Dati.numero_siti_clienti;
import static Utility.Dati.numero_siti_potenziali;
import ilog.concert.IloException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author diego
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, IloException {
        
        System.setOut(new PrintStream("Capacitated Plant Location.log"));
        double CT = Dati.Costo_trasporto();
        double[] Cs = Dati.Costo_stoccaggio();
        double[] dom = Dati.Domande();
        double[][] distanze = Dati.Distanze();
        int n = Dati.numero_siti_potenziali();
        int m = Dati.numero_siti_clienti();
        double[][] costi = Dati.Costi();
        double[] cap = Dati.Capacità();
        double[] CF = Dati.Costi_Fissi();

        Modello_CPL model = new Modello_CPL(CT, Cs, dom, distanze, n, m, costi, cap, CF);
        model.risolviModello();
    }
}
