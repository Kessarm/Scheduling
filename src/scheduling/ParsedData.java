package scheduling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

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
	            	this.Tj[i] = sc.nextInt();
	            }
	            
	            for(int i=0;i<n;i++){
	            	this.Deltaj[i] = sc.nextInt();
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
	
	
	
}
