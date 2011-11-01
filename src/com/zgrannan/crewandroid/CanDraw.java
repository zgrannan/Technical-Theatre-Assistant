package com.zgrannan.crewandroid;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * Anything that implements this interface can be drawn by
 * {@link CustomDrawableView}.
 * 
 * @author Zack Grannan
 * @version 0.93
 * 
 */
public interface CanDraw {

	/**
	 * Gets the height of this object.
	 * 
	 * @return A dimension representing the height or length of the drawable
	 *         object.
	 */
	Dimension getHeight();

	/**
	 * Gets the height of this object.
	 * 
	 * @return A dimension representing the height or length of the drawable
	 *         object.
	 */
	Dimension getWidth();

	/*
	 * TODO: Objects should draw themselves, so a void draw(Canvas c, float
	 * scale) should be added eventually
	 */

}
