package com.zgrannan.crewandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * Describes a doorframe.
 * 
 * @version 0.9
 * @author Zack Grannan
 * 
 */
public class Doorframe extends Buildable {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the amount of space between the bottom of the door and the
	 * ground.
	 */
	public static final double HEIGHT_SPACER = 0.5;

	/**
	 * This is the amount of space between the sides of the door and the door
	 * itself.
	 */
	public static final double WIDTH_SPACER = 0.25;

	public Dimension doorHeight, doorWidth;

	/**
	 * Starts the building of this doorframe.
	 * <p>
	 * 
	 * Calls {@link
	 * Doorframe.make(Dimension,Dimension,Dimension,Dimension,WoodStick,Sheet)}.
	 * 
	 * @param length
	 * @param width
	 * @param doorHeight
	 * @param doorWidth
	 * @param woodstick
	 * @param sheet
	 */
	public Doorframe(Context context, Dimension length, Dimension width,
			Dimension doorHeight, Dimension doorWidth, WoodStick woodstick,
			Sheet sheet) {
		make(context, length, width, doorHeight, doorWidth, woodstick, sheet);
	}

	/**
	 * Makes the single fragment necessary to create this Doorframe.
	 */
	@Override
	protected BuildResult make(Context context) {
		frags = new DoorFrag[1][1];
		frags[0][0] = new DoorFrag();
		((DoorFrag) frags[0][0]).make(context, length, width, doorHeight,
				doorWidth, woodstick, sheet);
		setInstructions(context);
		return new BuildResult(context, true);
	}

	/**
	 * Checks to ensure that this doorframe will be able to be built.
	 * 
	 * @param length
	 * @param width
	 * @param doorHeight
	 * @param doorWidth
	 * @param woodstick
	 * @param sheet
	 * @return The result of the check.
	 */
	public static BuildResult check(Context context, Dimension length,
			Dimension width, Dimension doorHeight, Dimension doorWidth,
			WoodStick woodstick, Sheet sheet) {

		Dimension woodThickness = woodstick.getWidth();
		Dimension minLength = new Dimension(doorHeight.toDouble() + 2
				* woodThickness.toDouble() + HEIGHT_SPACER);
		Dimension minWidth = new Dimension(doorWidth.toDouble() + 4
				* woodThickness.toDouble() + WIDTH_SPACER);
		Dimension maxLength = new Dimension(96);
		Dimension maxWidth = new Dimension(96);

		BuildResult result = new BuildResult();

		if (width.lessThan(minWidth) && !minWidth.greaterThan(maxWidth)) {
			result.addMessage(context.getString(
					R.string.frame_width_must_be_at_least, minWidth));
			result.fail();
		}
		if (length.lessThan(minLength) && !minLength.greaterThan(maxLength)) {
			result.addMessage(context.getString(
					R.string.frame_height_must_be_at_least, minLength));
			result.fail();
		}
		if (width.greaterThan(maxWidth)) {
			result.addMessage(context.getString(
					R.string.frame_width_must_be_no_greater_than, maxWidth));
			result.fail();
		}
		if (length.greaterThan(maxLength)) {
			result.addMessage(context.getString(
					R.string.frame_height_must_be_no_greater_than, maxLength));
			result.fail();
		}
		if (minWidth.greaterThan(maxWidth)) {
			result.addMessage(context.getString(
					R.string.door_width_must_be_no_greater_than,
					new Dimension(maxWidth.toDouble()
							- (4 * woodThickness.toDouble() + WIDTH_SPACER))));
			result.fail();
		}
		if (minLength.greaterThan(maxLength)) {
			result.addMessage(context.getString(
					R.string.door_height_must_be_no_greater_than,
					new Dimension(maxLength.toDouble()
							- (2 * woodThickness.toDouble() + HEIGHT_SPACER))));
			result.fail();
		}
		return result;
	}

	@Override
	public Sheet[] getValidSheets() {
		return new Sheet[] { Consumables.luaun, Consumables.oneQuarterPly,
				Consumables.threeQuartersPly, Consumables.cloth,
				Consumables.noSheet };
	}

	@Override
	public WoodStick[] getValidWoodSticks() {
		return new WoodStick[] { Consumables.oneByThree, Consumables.oneByFour,
				Consumables.twoByFour };
	}

	@Override
	public String getName(Context context) {
		return context.getString(R.string.doorframe) + ": " + length + " "
				+ context.getString(R.string.by) + " " + width;
	}

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
	 *            The object-stream representing this particular class, it does
	 *            not extend to superclass or subclasses.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		GetField field = in.readFields();
		doorHeight = (Dimension) field.get("doorHeight", null);
		doorWidth = (Dimension) field.get("doorWidth", null);
		if (sheet == null) {
			try {
				sheet = (Sheet) field.get("sheet", null);
			} catch (IllegalArgumentException e) {
				sheet = null;
			}
		}
		// make(length,width,doorHeight,doorWidth,woodstick,sheet);

	}

	@Override
	public String toString() {
		return "Doorframe";
	}

	/**
	 * Loads {@link doorHeight} and {@link doorWidth} into this Doorframe, then
	 * calls the generic {@link
	 * Buildable.make(Dimension,Dimension,WoodStick,Sheet)} function to load the
	 * rest of the information.
	 * 
	 * @param length
	 * @param width
	 * @param doorHeight
	 * @param doorWidth
	 * @param woodstick
	 * @param sheet
	 * @return
	 */
	public BuildResult make(Context context, Dimension length, Dimension width,
			Dimension doorHeight, Dimension doorWidth, WoodStick woodstick,
			Sheet sheet) {
		this.doorHeight = doorHeight;
		this.doorWidth = doorWidth;
		return super.make(context, length, width, woodstick, sheet);
	}

	/**
	 * No-arg constructor, for use in Builder, and for getting the list of valid
	 * sheets and woodsticks.
	 */
	public Doorframe() {
	}
}