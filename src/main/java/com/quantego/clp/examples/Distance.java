package com.quantego.clp.examples;

import java.util.Random;

import com.quantego.clp.*;
import com.quantego.clp.CLP.ALGORITHM;

/**
 * Computing the distance between two discrete probability distributions as a linear program.
 * @author nils
 *
 */
public class Distance {

	public static void main(String... args) {
		CLP model = new CLP().presolve(false).algorithm(ALGORITHM.PRIMAL);
		int size1 = 10;
		int size2 = 10000;
		Random gen = new Random(11);
		CLPVariable[][] massTransport = new CLPVariable[size1][size2];
		for (int i=0; i<size1; i++) {
			double rnd = gen.nextGaussian();
			for (int j=0; j<size2; j++) 
				massTransport[i][j] = model.addVariable()
				.ub(1./size1)
				.obj(Math.pow(gen.nextGaussian()-rnd,2)); //L2-Wasserstein distance
			
		}
		for (int i=0; i<size1; i++) 
			model.createExpression().add(massTransport[i]).eq(1./size1);
		for (int j=0; j<size2; j++) {
			CLPExpression e = model.createExpression();
			for (int i=0; i<size1; i++) 
				e.add(massTransport[i][j]);
			e.eq(1./size2);
		}
		System.out.println("Number of columns: "+model.getNumVariables());
		System.out.println("Number of rows: "+model.getNumConstraints());
		model.minimize();
		System.out.println("Objective value: "+model.getObjectiveValue());
		
	}
	
	
	
	

}
