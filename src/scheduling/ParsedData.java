package scheduling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;


public class ParsedData {

	
	private int n;
	private int H;
	private int[] Tj;
	private int[] Deltaj;
	private boolean[][] aij;
	private boolean[][] cij;
	
	
	public ParsedData(String path){
		File file = new File(path);
		int boolint;
		boolean bool;
	    try {
	        Scanner sc = new Scanner(file);
	            this.n = sc.nextInt();
	            System.out.println("this instance has "+this.n+" cities \n");
	            
	            this.Tj=new int[n];
	            this.Deltaj= new int[n];
	            this.aij = new boolean[n][n];
	            this.cij = new boolean[n][n];
	            
	            this.H = sc.nextInt();
	            for(int i=0;i<n;i++){
	            	int affect =sc.nextInt();
	            	this.Tj[i] = affect;
	            }
	            
	            for(int i=0;i<n;i++){
	            	int affect = sc.nextInt();
	            	this.Deltaj[i] = affect;
	            }
	            
	            for(int i=0;i<n;i++){
		            for(int j=0;j<n;j++){
		            	boolint = sc.nextInt();
		            	bool = (boolint==1);
		            	//System.out.println(bool);
		            	this.aij[i][j] = bool;
		            }
	            }
	            
	            for(int i=0;i<n;i++){
		            for(int j=0;j<n;j++){
		            	boolint = sc.nextInt();
		            	bool = (boolint==1);
		            	//System.out.println(bool);
		            	this.cij[i][j] = bool;
		            }
	            }
	        sc.close();
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    System.out.println(this.toString());
	}


	@Override
	public String toString() {
		return "ParsedData [n=" + n + ", H=" + H + ", Tj=" + Arrays.toString(Tj) + ", Deltaj=" + Arrays.toString(Deltaj)
				+ "]";
	}
	
	
	public void cplex(){
		
		int sol;
		try {
			IloCplex cplex = new IloCplex();
			
			
			// Decision Variables
			IloIntVar[] y = new IloIntVar[n];
			IloIntVar[][] x = new IloIntVar[n][n];
			
			//Initializing variables
			for (int i=0; i<n; i++) {
				y[i] = cplex.boolVar();
				for (int j=0; j<n; j++) {
					x[i][j] = cplex.boolVar();
				}
			}
			
			// Objective function
			IloLinearIntExpr expr = cplex.linearIntExpr();
			for (int i=0; i<n; i++) {
				expr.addTerm(1, y[i]);
			}
			
			// Adding the objective to the cplex model
			IloObjective obj = cplex.minimize(expr);
			cplex.add(obj);
			
			// Constraints of the problem
			
			// every village must be served by exactly one day
			
			IloLinearIntExpr[] constraint1 = new IloLinearIntExpr[n];
			
			for (int i=0; i<n; i++) {
				constraint1[i] = cplex.linearIntExpr();
			}
			
			//j : villages ; i : jours
			
			for (int j=0; j<n; j++) {
				for (int i=0; i<n; i++) {
					constraint1[j].addTerm(1, x[i][j]);
				}
				cplex.addEq(constraint1[j], 1);
			}
			
			// Each day cannot last longer than 2H
			IloLinearNumExpr[] constraint2 = new IloLinearNumExpr[n];
			for (int i=0; i<n; i++) {
				constraint2[i] = cplex.linearNumExpr();
			}
			
			for (int i=0; i<n; i++) {
				for (int j=0; j<n; j++) {
					int time = (2*Tj[j]+Deltaj[j]);
					constraint2[i].addTerm(time ,x[i][j] );
				}
				int timeday= 2*this.H;
				cplex.addLe(constraint2[i], timeday);
			}
			
			// Defining each yi as intended
			IloLinearIntExpr[][] constraint3 = new IloLinearIntExpr[n][n];
			
			for (int i=0; i<n; i++) {
			
				for (int j=0; j<n; j++) {
					
					constraint3[i][j] = cplex.linearIntExpr();
					constraint3[i][j].addTerm(1, x[i][j]);
					constraint3[i][j].addTerm(-1, y[i]);
					cplex.addLe(constraint3[i][j], 0);
					
					
				}
				
				
				
			}
			
			
			
			// force the days to be in order
			IloLinearIntExpr[] constraint4 = new IloLinearIntExpr[n-1];
			for (int i=0; i <n-1; i++) {
				constraint4[i] = cplex.linearIntExpr();
				constraint4[i].addTerm(1, y[i+1]);
				constraint4[i].addTerm(-1, y[i]);
				cplex.addLe(constraint4[i], 0);
			}
			
			// force the tasks to be in the same day if they have to
			
			IloLinearIntExpr[][][] constraint5 = new IloLinearIntExpr[n][n][n];
			for (int i=0; i <n; i++) {
				for(int j1=0;j1<n;j1++){
					for(int j2=0;j2<n;j2++){
						
						int aj1j2;
			
						if(this.aij[j1][j2]){aj1j2=1;} else{aj1j2=0;}
						
						constraint5[i][j1][j2] = cplex.linearIntExpr();
						constraint5[i][j1][j2].addTerm(aj1j2,x[i][j1]);
						constraint5[i][j1][j2].addTerm(-aj1j2, x[i][j2]);
						cplex.addLe(constraint5[i][j1][j2], 0);
					}
				}
			}
			
			
			
			IloLinearIntExpr[][][] constraint6 = new IloLinearIntExpr[n][n][n];
			for (int i=0; i <n; i++) {
				for(int j1=0;j1<n;j1++){
					for(int j2=0;j2<n;j2++){
						
						int cj1j2;
						boolean cjj=this.cij[j1][j2];
						if(this.cij[j1][j2]){cj1j2=1;} else{cj1j2=0;}
						
						constraint6[i][j1][j2] = cplex.linearIntExpr();
						constraint6[i][j1][j2].addTerm(1,x[i][j1]);
						constraint6[i][j1][j2].addTerm(1,x[i][j2]);
						cplex.addLe(constraint6[i][j1][j2], (1+cj1j2));
					}
				}
			}
			
			
			// solving the model
					cplex.solve();
					
					
					cplex.output().println("Solution status = " + cplex.getStatus());
					cplex.output().println("nombre de jours = " + cplex.getObjValue());
					
					double[][] xVal = new double[n][n];
					double [] yVal = new double[n];
					yVal = cplex.getValues(y);
					
					sol = (int) cplex.getObjValue();
					for (int i=0; i<n; i++) {
						xVal[i] = cplex.getValues(x[i]);
						
					}
					for (int i=0; i<n; i++) {
						
						for (int j=0; j<n; j++) {
							

					//System.out.println("valeur du tableau du x["+i+"] ["+j+"] "+xVal[i][j]);
						}
					}
					
					for (int i=0; i<n; i++) {
						//System.out.println("valeur du tableau du y[" + i+"] " + yVal[i]);
					}
					
					LinkedList<Integer> Schedule[] = new LinkedList[(int) cplex.getObjValue()];
					
					for (int j=0; j<(int) cplex.getObjValue(); j++) {
						
						Schedule[j] = new LinkedList<Integer>();
						
						for (int i=0; i<n; i++) {
							if ( xVal[j][i] == 1 ) {
								//cplex.output().print(i + ", ");
								Schedule[j].add(i);
							}
						}
						
						//cplex.output().print("\n");
					}
					
					for (int j=0;j<sol;j++){System.out.println(Schedule[j].toString());}
				
				
				cplex.end();
		
	}




		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}

		
	
	}
	
	
	
}
