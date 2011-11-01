package com.zgrannan.crewandroid;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Util.BuildException;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This class describes a hollywood.
 * 
 * @author Zack Grannan
 * @version 0.9
 * 
 */
public class Hollywood extends PlatformType {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	/**
	 * Create a hollywoods from specification
	 * 
	 * @param length
	 *            The length of the hollywood (vertical).
	 * @param width
	 *            The width of the hollywood (horizontal).
	 * @param woodstick
	 *            The woodstick that makes up the frame.
	 * @param sheet
	 *            The sheet that covers the frame.
	 * @throws BuildException
	 *             Thrown if the given dimensions are invalid, or if anything
	 *             else went wrong.
	 */
	public Hollywood(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {
		super(context, length, width, woodstick, sheet);
	}

	@Override
	public String getName(Context context) {
		return context.getString(R.string.hollywood) + ": " + length + " "
				+ context.getString(R.string.by) + " " + width;
	}

	@Override
	public Sheet[] getValidSheets() {
		return new Sheet[] { Consumables.luaun, Consumables.oneQuarterPly,
				Consumables.cloth, Consumables.threeQuartersPly,
				Consumables.noSheet };
	}

	@Override
	public WoodStick[] getValidWoodSticks() {
		return new WoodStick[] { Consumables.oneByThree, Consumables.oneByFour };
	}

	@Override
	public String toString() {
		return "Hollywood";
	}

	@Override
	protected BuildResult make(Context context) {
		frags = Frag.make(context, this, HollyFrag.class);
		setInstructions(context);
		return new BuildResult(context, true);
	}

	/**
	 * No-arg constructor, for use in Builder, and for getting the list of valid
	 * sheets and woodsticks.
	 */
	public Hollywood() {
	}
}
