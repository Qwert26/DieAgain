package com.github.qwert26.dieAgain.simulation;

import java.util.*;

public class ParkingLotSimulation {
	private Random r = new Random();
	private int dimensions = 2;
	private double length = 100;

	public ParkingLotSimulation() {
		super();
	}

	public int runForTries(long tries) {
		List<double[]> points = new LinkedList<>();
		double[] point = new double[dimensions];
		boolean crashed = false;
		for (int d = 0; d < dimensions; d++) {
			point[d] = r.nextDouble() * length;
		}
		points.add(point);
		for (; tries > 0; tries--) {
			point = new double[dimensions];
			crashed = false;
			for (int d = 0; d < dimensions; d++) {
				point[d] = r.nextDouble() * length;
			}
			for (double[] parked : points) {
				int tcc = 0;
				for (int d = 0; d < dimensions; d++) {
					if (Math.abs(point[d] - parked[d]) <= 1.0) {
						tcc++;
					}
				}
				if (tcc == dimensions) {
					crashed = true;
					break;
				}
			}
			if (!crashed) {
				points.add(point);
			}
		}
		return points.size();
	}

	public int runUntilCrash() {
		List<double[]> points = new LinkedList<>();
		double[] point = new double[dimensions];
		boolean crashed = false;
		for (int d = 0; d < dimensions; d++) {
			point[d] = r.nextDouble() * length;
		}
		points.add(point);
		do {
			point = new double[dimensions];
			for (int d = 0; d < dimensions; d++) {
				point[d] = r.nextDouble() * length;
			}
			for (double[] parked : points) {
				int tcc = 0;
				for (int d = 0; d < dimensions; d++) {
					if (Math.abs(point[d] - parked[d]) <= 1.0) {
						tcc++;
					}
				}
				if (tcc == dimensions) {
					crashed = true;
					break;
				}
			}
			if (!crashed) {
				points.add(point);
			}
		} while (!crashed);
		return points.size();
	}

	public static void main(String... args) {
		ParkingLotSimulation sim = new ParkingLotSimulation();
		sim.dimensions = 2;
		sim.length = 100;
		int lines = 50, numbersPerLine = 20;
		for (long count = lines * numbersPerLine; count > 0; count--) {
			System.out.print(sim.runUntilCrash() + "\t");
			if ((count - 1) % numbersPerLine == 0) {
				System.out.println();
			}
		}
	}
}