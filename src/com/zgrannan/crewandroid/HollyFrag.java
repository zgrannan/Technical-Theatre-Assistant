package com.zgrannan.crewandroid;

/**
 * A fragment for a Hollywood.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public class HollyFrag extends PlatTypeFrag {

	@Override
	protected int getCrossbeams() {
		if (length.toDouble() < 48)
			return 0;
		else
			return 1;
	}

}
