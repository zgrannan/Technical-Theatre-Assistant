package com.zgrannan.crewandroid;

/**
 * A building block of a {@link Platform}.
 * 
 * @author Zack Grannan
 * @version 0.96.
 * 
 */
public class PlatFrag extends PlatTypeFrag {

	@Override
	protected int getCrossbeams() {
		return (int) (length.toDouble() / 40);
	}

}
