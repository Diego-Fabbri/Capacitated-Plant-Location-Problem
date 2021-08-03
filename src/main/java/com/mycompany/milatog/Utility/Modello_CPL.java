/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.milatog.Utility;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

/**
 *
 * @author diego
 */
public class Modello_CPL {

    protected IloCplex modello;
    protected double CT;
    protected double[] Cs;
    protected double[] dom;// domande giornaliere
    protected double[][] distanze;
    int numero_siti_potenziali;
    int numero_siti_clienti;
    protected double[][] costi;

    protected double[] cap;// capacità giornaliere
    protected double[] CF;// capacità giornaliere
    protected IloIntVar[] y;
    //NOTA: questo problema, poichè adopera un grafo completo A= V1 x V2,
//fa uso delle matrici per modellare le variabili associate agli archi (i,j) anzichè la classe jung graph, poichè non avremo elementi vuoti
    protected IloNumVar[][] x;

    Modello_CPL(double CT, double[] Cs, double[] dom, double[][] distanze, int n, int m, double[][] costi, double[] cap, double[] CF) throws IloException {
        this.modello = new IloCplex();// assegnamo il modello del costruttore ad un nuovo modello
        this.CT = CT;
        this.Cs = Cs;
        this.dom = dom;
        this.distanze = distanze;
        this.numero_siti_potenziali = n;
        this.numero_siti_clienti = m;
        this.costi = costi;
        this.cap = cap;
        this.y = new IloIntVar[n];
        this.x = new IloNumVar[n][m];
        this.CF = CF;
    }

    protected void addVariables() throws IloException {
        // COMINCIAMO CON DEFINIRE LE VARIABILI 
        for (int i = 0; i < numero_siti_potenziali; i++) { // definizione variabili y
            int pos_i = i + 1;
            y[i] = modello.boolVar("y[" + pos_i + "]");// definiamo variabili binarie y

        }

        for (int i = 0; i < numero_siti_potenziali; i++) { // definizione variabili x
            for (int j = 0; j < numero_siti_clienti; j++) {
                int pos_i = i + 1;
                int pos_j = j + 1;
                x[i][j] = modello.numVar(0, 1, IloNumVarType.Float, "x[" + pos_i + "][" + pos_j + "]");// definiamo campo esistenza variabili
            }
        }

    }

    protected void addObjective() throws IloException {
        IloLinearNumExpr obiettivo = modello.linearNumExpr();// creiamo un oggetto espressione che contenga la funzione obiettivo

        //AGGIUNGIAMO PARTE DEI COSTI FISSI
        for (int i = 0; i < numero_siti_potenziali; i++) {
            obiettivo.addTerm(y[i], CF[i]);// stiamo aggiungendo il termine f_i*y_i
        }

        // AGGIUGIAMO PARTE RELATIVA ALLE VARIABILI X    
        for (int i = 0; i < numero_siti_potenziali; i++) {
            for (int j = 0; j < numero_siti_clienti; j++) {
                obiettivo.addTerm(x[i][j], costi[i][j]);// stiamo aggiungendo il termine c_ij*x_ij

            }
        }

        IloObjective Obj = modello.addObjective(IloObjectiveSense.Minimize, obiettivo);
    }

    protected void addConstraints() throws IloException {
// Vincolo soddisfacimento domande clienti 
        for (int j = 0; j < numero_siti_clienti; j++) {
            IloLinearNumExpr vincolo_servizio_clienti = modello.linearNumExpr();
            for (int i = 0; i < numero_siti_potenziali; i++) {
                vincolo_servizio_clienti.addTerm(x[i][j], 1); // creiamo il vincolo nella parte destra somma delle xij
            }
            modello.addEq(vincolo_servizio_clienti, 1);// aggiungiamo il vincolo 
        }

        for (int i = 0; i < numero_siti_potenziali; i++) {
            IloLinearNumExpr vincolo_capacità = modello.linearNumExpr();
            vincolo_capacità.addTerm(y[i], -cap[i]);// aggiungiamo termine - y_i*q_i

            for (int j = 0; j < numero_siti_clienti; j++) {
                vincolo_capacità.addTerm(dom[j], x[i][j]);
                // aggiungiamo termine x_1ij*d_pj

            }
            modello.addLe(vincolo_capacità, 0);
        }

    }

    public void risolviModello() throws IloException {
        boolean condizione = Verifica_Condizione_Ammissibilità();
        if (condizione == true) {
            addVariables();
            addObjective();
            addConstraints();
            modello.exportModel("Capacitated Plant Location.lp");

            modello.solve();// questo metodo risolve il problema

            if (modello.getStatus() == IloCplex.Status.Feasible
                    | modello.getStatus() == IloCplex.Status.Optimal) {
                System.out.println();
                System.out.println("Solution status = " + modello.getStatus());
                System.out.println();

                double domanda_totale = 0;
                double capacità_totale = 0;
                for (int j = 0; j < numero_siti_potenziali; j++) {
                    capacità_totale += cap[j];
                }

                for (int j = 0; j < numero_siti_clienti; j++) {
                    domanda_totale += dom[j];

                }
                System.out.println("Total demand = " + domanda_totale);
                System.out.println("Total capacity = " + capacità_totale);
                System.out.println();
                System.out.println("Objective function value:" + modello.getObjValue());
                // System.out.println("Lo stato del modello è" + modello.getStatus())
                System.out.println();
                System.out.println("The values of variables y:");
                System.out.println();
                for (int i = 0; i < y.length; i++) {
                    if (modello.getValue(y[i]) != 0) {
                        int pos_i = i + 1;
                        System.out.print("y[" + pos_i + "]=" + modello.getValue(y[i]));
                        System.out.println();
                    }
                }
                System.out.println();
                System.out.println("The values of variables x :");
                System.out.println();
                for (int i = 0; i < x.length; i++) {
                    for (int j = 0; j < x[0].length; j++) {
                        if (modello.getValue(x[i][j]) != 0) {
                            System.out.println(x[i][j].getName() + "=" + modello.getValue(x[i][j]));
                        }
                    }
                }

            } else {
                System.out.println("Solution status = " + modello.getStatus());
            }

        } else {
            System.out.println("Preliminary condition is not satisfied");
        }
    }

    public boolean Verifica_Condizione_Ammissibilità() {
        double domanda_totale = 0;
        double capacità_totale = 0;
        for (int j = 0; j < numero_siti_potenziali; j++) {
            capacità_totale += cap[j];
        }

        for (int j = 0; j < numero_siti_clienti; j++) {
            domanda_totale += dom[j];

        }
        if (domanda_totale <= capacità_totale) {
            return true;
        } else {
            return false;
        }
    }

}
