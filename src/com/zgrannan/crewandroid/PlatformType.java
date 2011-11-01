package com.zgrannan.crewandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Util.BuildException;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * Class used for building {@link Platform}s, {@link Broadway}s,and
 * {@link Hollywood}s
 * 
 * @author Zack Grannan
 * @version 0.9
 * 
 */
public abstract class PlatformType extends Buildable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Generates the PlatformType from the given parameters.
	 * 
	 * @param length
	 *            The length of the PlatformType (vertical).
	 * @param width
	 *            The width of the PlatformType (horizontal).
	 * @param woodstick
	 *            The woodstick that makes up the frame.
	 * @param sheet
	 *            The sheet that covers the frame.
	 * @throws BuildException
	 *             Thrown if the given dimensions are invalid, or if anything
	 *             else went wrong.
	 */
	PlatformType(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {
		make(context, length, width, woodstick, sheet);

	}

	/**
	 * Ensures that the PlatformType will build correctly.
	 * 
	 * @param length
	 * @param width
	 * @param woodstick
	 * @param sheet
	 * @return A {@link BuildResult} with details.
	 */
	public static BuildResult check(Context context, Dimension length,
			Dimension width, WoodStick woodstick, Sheet sheet) {
		final Dimension woodThickness = woodstick.getWidth();
		final Dimension minWidth = new Dimension(2 * woodThickness.toDouble());
		final Dimension minLength = new Dimension(2 * woodThickness.toDouble());
		final Dimension warnMaxWidth = new Dimension(96 * 4);
		final Dimension warnMaxHeight = new Dimension(96 * 4);

		BuildResult result = Buildable.check(context, length, width);

		if (width.lessThan(minWidth)) {
			result.addMessage(context.getString(
					R.string.width_must_be_at_least, minWidth));
			result.fail();
		}
		if (length.lessThan(minLength)) {
			result.addMessage(context.getString(
					R.string.length_must_be_at_least, minLength));
			result.fail();
		}
		if (!result.success())
			return result;
		if (width.greaterThan(warnMaxWidth)) {
			result.warn(context
					.getString(R.string.this_is_very_wide_are_you_sure_you_want_to_build_this));
		}
		if (length.greaterThan(warnMaxHeight)) {
			result.warn(context
					.getString(R.string.this_is_very_long_are_you_sure_you_want_to_build_this));
		}
		return result;
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
	 *            The objectStream representing this particular class, it does
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

	/**
	 * No-arg constructor, for use in Builder, and for getting the list of valid
	 * sheets and woodsticks.
	 */
	public PlatformType() {
	};

}