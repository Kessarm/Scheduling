package scheduling;

import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

public class Main {

	
	public static void main (String[] args){
		
		//ParsedData instance = new ParsedData("C:\\Users\\Mehdi\\Documents\\scheduling_case-study\\dat1\\inst215");
                ParsedData instance = new ParsedData("./dat/inst210");
		//instance.cplex();
		instance.firstfit();
		}
	
	
	
	
	
}
