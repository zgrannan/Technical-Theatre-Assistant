package com.zgrannan.crewandroid;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * Class for making a Platform.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public class Platform extends PlatformType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a platform from parameters.
	 * 
	 * @param length
	 * @param width
	 * @param woodstick
	 * @param sheet
	 */
	public Platform(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {
		super(context, length, width, woodstick, sheet);
	}

	/**
	 * No-arg constructor, for use in Builder, and for getting the list of valid
	 * sheets and woodsticks.
	 */
	public Platform() {
	}

	@Override
	public String getName(Context context) {
		return context.getString(R.string.platform) + ": " + length + " "
				+ context.getString(R.string.by) + " " + width;
	}

	@Override
	public Sheet[] getValidSheets() {
		return new Sheet[] { Consumables.threeQuartersPly, Consumables.noSheet };
	}

	@Override
	public WoodStick[] getValidWoodSticks() {
		return new WoodStick[] { Consumables.twoByFour };
	}

	@Override
	public String toString() {
		return "Platform";
	}

	@Override
	protected BuildResult make(Context context) {
		frags = Frag.make(context, this, PlatFrag.class);
		setInstructions(context);
		return new BuildResult(context, true);
	}

}