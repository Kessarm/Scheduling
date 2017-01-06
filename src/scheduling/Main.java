package scheduling;

import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

public class Main {

	
	public static void main (String[] args){
		ParsedData instance = new ParsedData("C:\\Users\\Mehdiii\\Documents\\scheduling_case-study\\dat\\inst001");
		instance.cplex();
	}
	
	
	
	
}
