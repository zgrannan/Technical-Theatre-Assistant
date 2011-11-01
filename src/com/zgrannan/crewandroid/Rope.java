package com.zgrannan.crewandroid;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This class defines the rope information used in the rope calculator.
 * 
 * @author Zack Grannan
 * @version 0.94
 */
public class Rope {

	/**
	 * The minimum safety factor for rope. If the safety factor is less than
	 * this, the user will be warned.
	 */
	public static final double MINIMUM_SAFETY_FACTOR = 5;

	/**
	 * The name of this rope
	 */
	private String name;

	/**
	 * The co-efficient to be used when calculating the strength of used rope.
	 */
	private double usedCoefficient;

	/**
	 * An array of doubles indicating the new strength of rope.
	 */
	private double newStrength[];

	/**
	 * Calculates the maximum capacity of this rope if it is used.
	 * 
	 * @param lineDiameter
	 *            A dimension representing the diameter of the rope.
	 * @param safetyFactor
	 * @return The maximum capacity of the rope in pounds.
	 */
	public double maximumUsedCapacity(Dimension lineDiameter,
			double safetyFactor) {
		double numerator = lineDiameter.toDouble() * 8.0;
		return numerator * numerator * usedCoefficient / safetyFactor;
	}

	/**
	 * Calculates the maximum capacity of this rope if it is new.
	 * 
	 * @param index
	 *            The index of the dimension that represents the line diameter.
	 * @param safetyFactor
	 *            The maximum capacity of the rope in pounds.
	 * @return
	 */
	public double maximumNewCapacity(int index, double safetyFactor) {
		return newStrength[index] / safetyFactor;
	}

	/**
	 * Contructor for a new rope.
	 * 
	 * @param name
	 *            The name of this rope
	 * @param usedCoefficient
	 *            The co-efficient to be used when calculating the strength of
	 *            used rope.
	 * @param newStrength
	 *            An array of doubles indicating the new strength of rope.
	 */
	public Rope(String name, double usedCoefficient, double[] newStrength) {
		this.name = name;
		this.usedCoefficient = usedCoefficient;
		this.newStrength = newStrength;
	}

	/**
	 * Returns an array of dimensions created from a corresponding array of
	 * doubles.
	 * 
	 * @param values
	 *            The values that the dimension array will be created from.
	 * @return An array of corresponding dimensions.
	 */
	public static Dimension[] getDimensions(double... values) {
		Dimension[] dimensions = new Dimension[values.length];
		for (int i = 0; i < values.length; i++) {
			dimensions[i] = new Dimension(values[i]);
		}
		return dimensions;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Rope manilla = new Rope("Manilla", 100, new double[] { 500.0,
			600.0, 1000.0, 1350.0, 2650.0, 4400.0, 5400.0, 7700.0, 9000.0,
			12000.0, 13500.0, 18500.0, 22500.0, 26500.0, 31000.0 });
	public static Rope nylon = new Rope("Nylon", 300, new double[] { 1000.0,
			1500.0, 2500.0, 3500.0, 6250.0, 10000.0, 14000.0, 19000.0, 24000.0,
			31500.0, 36000.0, 51000.0, 62000.0, 75000.0, 89500.0 });
	public static Rope polypropylene = new Rope("Polypropylene", 200,
			new double[] { 750.0, 1250.0, 2000.0, 2500.0, 4150.0, 6500.0,
					8500.0, 11000.0, 14500.0, 18750.0, 21000.0, 30000.0,
					36500.0, 43500.0, 52000.0 });
	public static Rope polyester = new Rope("Polyester", 300, new double[] {
			1000.0, 1500.0, 2500.0, 3500.0, 6000.0, 9500.0, 12000.0, 17000.0,
			21000.0, 28000.0, 31500.0, 44500.0, 54000.0, 64500.0, 76000.0 });
	public static Rope polyethylene = new Rope("Polyethylene", 175,
			new double[] { 750.0, 1250.0, 1750.0, 2500.0, 4000.0, 5250.0,
					7500.0, 10500.0, 12500.0, 16500.0, 18500.0, 26500.0,
					32500.0, 39500.0, 47500.0 });

	public static Rope[] ropes = { manilla, nylon, polypropylene, polyester,
			polyethylene };
	public static Dimension[] dimensions = getDimensions(3.0 / 16, 1.0 / 4,
			5.0 / 16, 3.0 / 8, 1.0 / 2, 5.0 / 8, 3.0 / 4, 7.0 / 8, 1.0,
			1.0 + 1.0 / 8.0, 1.0 + 1.0 / 4.0, 1.5, 1 + 5.0 / 8, 1.75, 2.0);

	/**
	 * Tests to ensure that all of the ropes contain a valid amount of
	 * dimensions.
	 * 
	 * @return True if the ropes are valid, false otherwise.
	 */
	public static boolean test() {
		int length = dimensions.length;
		for (Rope rope : ropes) {
			if (rope.newStrength.length != length) {
				return false;
			}
		}
		return true;
	}
}
