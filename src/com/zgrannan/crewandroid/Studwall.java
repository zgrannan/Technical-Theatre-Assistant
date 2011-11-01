package com.zgrannan.crewandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * Makes a studwall.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public class Studwall extends Buildable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for studwall.
	 * <p>
	 * 
	 * Step 2 of build, right after static check call. Calls to this function
	 * should come from {@link Builder.attemptBuild()}.
	 * 
	 * @param length
	 * @param width
	 * @param woodstick
	 * @param sheet
	 */
	public Studwall(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {

		make(context, length, width, woodstick, sheet);

	}

	/**
	 * Makes this set piece by calling the "super" make function.
	 * <p>
	 * Step 3 of build, called from the constructor: {@link
	 * Studwall(Dimension,Dimension,WoodStick,Sheet)}
	 */
	@Override
	public BuildResult make(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {
		return super.make(context, length, width, woodstick, sheet);
	}

	@Override
	protected BuildResult make(Context context) {
		frags = Frag.make(context, this, StudFrag.class);
		setInstructions(context);
		return new BuildResult(context, true);
	}

	@Override
	public String getName(Context context) {
		return context.getString(R.string.studwall) + ": " + length + " "
				+ context.getString(R.string.by) + " " + width;
	}

	/**
	 * Sets the instructions for the studwall.
	 */

	/**
	 * Reads this object from a stream. This implementation manually copies the
	 * serializable versions from the stream instead of relying on the default
	 * implementation to ensure that this can open set pieces created from an
	 * older version.
	 * <p>
	 * Effective August 23rd, versions prior to this one should be orphaned and
	 * we should be able to rely on the default implementation.
	 * <p>
	 * 
	 * DO NOT CHANGE THIS METHOD SIGNATURE. It will break the functionality.
	 * 
	 * @param in
	 *            The ObjectStream representing this particular class, it does
	 *            not extend to superclass or subclasses.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		GetField field = in.readFields();
		if (sheet == null) {
			try {
				sheet = (Sheet) field.get("sheet", null);
			} catch (IllegalArgumentException e) {
				sheet = null;
			}
		}
		// make(length,width,woodstick,sheet);
	}

	@Override
	public Sheet[] getValidSheets() {
		return new Sheet[] { Consumables.luaun, Consumables.oneQuarterPly,
				Consumables.threeQuartersPly, Consumables.cloth,
				Consumables.noSheet, };
	}

	@Override
	public WoodStick[] getValidWoodSticks() {
		return new WoodStick[] { Consumables.oneByThree, Consumables.oneByFour,
				Consumables.twoByFour };
	}

	@Override
	public String toString() {
		return "Studwall";
	}

	/**
	 * Checks to see if this studwall could be built.
	 * 
	 * @param length
	 * @param width
	 * @param woodstick
	 * @param sheet
	 * @return A {@link BuildResult} with the result of the check.
	 */
	public static BuildResult check(Context context, Dimension length,
			Dimension width, WoodStick woodstick, Sheet sheet) {
		final Dimension woodThickness = woodstick.getWidth();
		final Dimension minWidth = new Dimension(2 * woodThickness.toDouble());
		final Dimension warnMaxWidth = new Dimension(96 * 4);
		final Dimension minLength = new Dimension(2 * woodThickness.toDouble());
		final Dimension warnMaxLength = new Dimension(96 * 4);

		BuildResult result = Buildable.check(context, length, width);

		if (width.lessThan(minWidth)) {
			result.addMessage("Width must be at least: " + minWidth);
			result.fail();
		}
		if (length.lessThan(minLength)) {
			result.addMessage("Heigth must be at least: " + minWidth);
			result.fail();
		}
		if (!result.success()) {
			return result;
		}
		if (width.greaterThan(warnMaxWidth)) {
			result.warn("This is very wide, are you sure that you want to build this?");
		}
		if (length.greaterThan(warnMaxLength)) {
			result.warn("This is very high, are you sure you want to build this?");
		}
		return result;
	}

	/**
	 * No-arg constructor, for use in Builder, and for getting the list of valid
	 * sheets and woodsticks.
	 */
	public Studwall() {
	}
}