package com.zgrannan.crewandroid;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This class describes the building of a broadway.
 * 
 * @author Zack Grannan
 * @version 0.96
 */
public class Broadway extends PlatformType {

	private static final long serialVersionUID = 1L;

	@Override
	protected BuildResult make(Context context) {
		frags = Frag.make(context, this, BroadFrag.class);
		setInstructions(context);
		return new BuildResult(context, true);
	}

	@Override
	public String getName(Context context) {
		return context.getString(R.string.broadway) + ": " + length + " "
				+ context.getString(R.string.by) + " " + width;
	}

	@Override
	public Sheet[] getValidSheets() {
		return new Sheet[] { Consumables.luaun, Consumables.cloth,
				Consumables.oneQuarterPly, Consumables.threeQuartersPly,
				Consumables.noSheet };
	}

	@Override
	public WoodStick[] getValidWoodSticks() {
		return new WoodStick[] { Consumables.oneByThreeOnFlat,
				Consumables.oneByFourOnFlat };
	}

	@Override
	public String toString() {
		return "Broadway";
	}

	/**
	 * No-arg constructor, for use in Builder, and for getting the list of valid
	 * sheets and woodsticks.
	 */
	public Broadway() {
	}

	public Broadway(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {
		super.make(context, length, width, woodstick, sheet);
	}
}