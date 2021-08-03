/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 *
 * @author diego
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author diego
 */
public class Dati {

    public static int Numero_giorni() {
        return 1461;
    }

    public static double Costo_trasporto() {
        return 0.06;
    }

    public static int numero_siti_potenziali() {
        return 6;
    }

    public static int numero_siti_clienti() {
        return 7;
    }

    public static double[] Domande() {
        double[] dom = {36, 42, 34, 50, 27, 30, 43};
        return dom;
    }

    public static double[] Capacità() {
        double[] cap = {80, 90, 110, 120, 100, 120};
        return cap;
    }

    public static double[] Costi_Fissi() {
        int giorni = Numero_giorni();
        double[] CF = {321420, 350640, 379860, 401775, 350640, 336030};

        for (int i = 0; i < CF.length; i++) {
            CF[i] = CF[i] / giorni;
        }
        return CF;
    }

    public static double[] Costo_stoccaggio() {
        double[] Cs = {0.15, 0.18, 0.2, 0.18, 0.15, 0.17};
        return Cs;
    }

     public static double[][] Distanze(){
     double[][] distanze ={
         {18,23,19,21,24,17,9},
         {21,18,17,23,11,18,20},
         {27,18,17,20,23,9,18},
         {16,23,9,31,21,23,10},
         {31,20,18,19,10,17,18},
         {18,17,29,21,22,18,8},
     };
     return distanze;
     }
     
    public static double[][] Costi() {
        double[] Cs = Costo_stoccaggio();
        double[] dom = Domande();
        double[][] distanze = Distanze();
        int n = numero_siti_potenziali();
        int m = numero_siti_clienti();
        double CT = Costo_trasporto();
        double[][] costi = new double[n][m];
        for (int i = 0; i < costi.length; i++) {
            for (int j = 0; j < costi[0].length; j++) {
                costi[i][j] = 2 * CT * distanze[i][j] * dom[j] + dom[j] * Cs[i];

            }
        }

        return costi;
    }
}
