package com.zgrannan.crewandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;

import com.zgrannan.crewandroid.Frag.PieceVisibilityObject;
import com.zgrannan.crewandroid.Pieces.Piece;

/**
 * Class for miscellaneous functionality
 * 
 * @author Zack Grannan
 * @version 0.94
 * 
 */
public class Util {

	/**
	 * Shows a dialog, with a title, a message, and an OK button.
	 * 
	 * @param activity The calling activity.
	 * @param title The title of the dialog.
	 * @param text The text of the dialog.
	 */
	public static void showQuickDialog(Activity activity, String title, String text){
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title)
				.setPositiveButton(activity.getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.dismiss();

							}
						})
				.setMessage(text);
		dialog = builder.create();
		dialog.show();
	}
	public static TextWatcher getProperFormatTextWatcher(final boolean integer,
			final boolean allowNegative) {
		return new TextWatcher() {
			/**
			 * Ensures that the user put in a valid input.
			 */
			@Override
			public void afterTextChanged(Editable s) {
				String oldText = s.toString();
				String newText;
				if (integer)
					newText = Util.formatAsInt(oldText);
				else
					newText = Util.formatAsDouble(oldText);

				if (!allowNegative)
					newText = Util.abs(newText);
				if (!newText.equals(oldText)) {
					s.clear();
					s.append(newText);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}
		};
	}

	/**
	 * This exception is thrown when there is a problem building a set piece
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class BuildException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BuildException(String msg) {
			super(msg);
		}
	}

	public static class CompleteInstruction extends Instruction implements
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * The settings for piece visibility
		 */
		private int[] pieceSettings;

		public CompleteInstruction(String instruction, int[] pieceSettings) {

			super(instruction);
			this.pieceSettings = new int[pieceSettings.length];
			System.arraycopy(pieceSettings, 0, this.pieceSettings, 0,
					pieceSettings.length);
		}

		/**
		 * Returns the piece settings
		 * 
		 * @return
		 */
		public int[] getPieceSettings() {
			return pieceSettings;
		}

		/**
		 * Set the piece visibility settings
		 * 
		 * @param pieceSettings
		 *            An integer array representing the piece settings.
		 */
		public void setPieceSettings(int[] pieceSettings) {
			this.pieceSettings = new int[pieceSettings.length];
			System.arraycopy(pieceSettings, 0, this.pieceSettings, 0,
					pieceSettings.length);
		}

	}

	/**
	 * This class stores a cutlist for a set of pieces.
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class Cutlist implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * A list of pieces that a cutlistObject describes.
		 */
		private List<Piece> pieceList;

		/**
		 * The list of cutlistObjects that makes up the cutlist.
		 */
		private List<CutlistObject> cutlist;

		/**
		 * Constructs a cutlist from an array of pieces.
		 * 
		 * @param piece
		 *            The array of pieces.
		 */
		Cutlist(Piece[] piece) {

			// Generate lists
			cutlist = new LinkedList<CutlistObject>();
			pieceList = new LinkedList<Piece>();

			// Sort the piece array
			Arrays.sort(piece);

			// Generate a sorted list of pieces
			for (int i = 0; i < piece.length - 1; i++) {
				/*
				 * Don't add the piece if it is a "no sheet" piece.
				 */
				if (piece[i].getMaterial().equals(Consumables.noSheet)) {
					continue;
				}
				pieceList.add(piece[i]); // Add this piece to the piecelist
				if (piece[i + 1] == null)

					/*
					 * We've reached the end of the piece array
					 */
					break;
				if (piece[i + 1].compareTo(piece[i]) != 0) {

					/*
					 * If this piece is different than the previous one
					 */
					cutlist.add(new CutlistObject(pieceList));

					/*
					 * Create a cutlistobject from the piecelist, and add it to
					 * the cutlist
					 */
					pieceList = new LinkedList<Piece>(); // Empty the piecelist
				}

			}
			// Add the last piece to the piecelist, and add that to the cutlist
			pieceList.add(piece[piece.length - 1]);
			cutlist.add(new CutlistObject(pieceList));
		}

		public String[] getStringArray() {
			// Returns an array of string objects that correspond to the cutlist
			CutlistObject[] cutlistObjects = cutlist
					.toArray(new CutlistObject[cutlist.size()]);
			String[] result = new String[cutlistObjects.length];
			for (int i = 0; i < cutlistObjects.length; i++) {
				result[i] = cutlistObjects[i].toString();
			}
			return result;
		}

	};

	public static class CutlistObject implements Serializable {

		// This class describes a series of pieces which are the same

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Piece[] pieces; // The pieces in this cutlistobject
		private int number; // The number of pieces in this cutlistobject
		private String names; // The names of these pieces in one big string

		CutlistObject(List<Piece> pieceList) {
			// Convert the pieceList into an array
			pieces = pieceList.toArray(new Piece[pieceList.size()]);

			// Get the # of pieces and store
			number = pieces.length;

			// Set the value of names
			names = "";
			for (int i = 0; i < pieces.length - 1; i++) {
				names += pieces[i].name + ",";
			}
			names += pieces[pieces.length - 1].name;
		}

		@Override
		public String toString() {
			return number + ": " + pieces[0] + " {" + names + "} ";
		}
	}

	/**
	 * This exception is thrown when there the user attempts to divide by zero
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class DivideByZeroException extends ArithmeticException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DivideByZeroException(String msg) {
			super(msg);
		}
	}

	/**
	 * Class for an instruction step
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static abstract class Instruction implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * The prompt for the user
		 */
		private String instruction;

		/**
		 * Copy the info from the constructor into this class
		 * 
		 * @param instruction
		 *            A string representing the instruction the user will read.
		 * @param pieceSettings
		 *            An integer array representing visibility settings.
		 */
		public Instruction(String instruction) {
			this.instruction = instruction;
		}

		/**
		 * Returns the prompt for the user
		 * 
		 * @return
		 */
		public String getInstruction() {
			return instruction;
		}

		/**
		 * Set the prompt for the user
		 * 
		 * @param instruction
		 *            A string representing the instruction.
		 */
		public void setInstruction(String instruction) {
			this.instruction = instruction;
		}

	}

	public static class PartialInstruction extends Instruction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PieceVisibilityObject[] pieceSettings;

		public PartialInstruction(String instruction,
				PieceVisibilityObject[] pieceSettings) {
			super(instruction);
			this.pieceSettings = pieceSettings;
		}

		public PieceVisibilityObject[] getPieceSettings() {
			return pieceSettings;
		}

	}

	public static class FragVisibilityInstruction extends Instruction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int x, y;
		private boolean showAll;

		public FragVisibilityInstruction(String instruction) {
			super(instruction);
			showAll = true;
		}

		public FragVisibilityInstruction(String instruction, int x, int y) {
			super(instruction);
			this.x = x;
			this.y = y;
			showAll = false;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public boolean showAll() {
			return showAll;
		}
	}

	/**
	 * Returns the index of the minimum value in an array.
	 * 
	 * @param array
	 *            An array of doubles.
	 * @return The index of the minimum value in the array.
	 */
	public static int indexOfMinValue(double[] array) {

		int index = 0;
		double value = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < value) {
				index = i;
				value = array[i];
			}
		}
		return index;
	}

	/**
	 * Formats the given CharSequence as representation of an integer, removing
	 * any unallowed characters.
	 * 
	 * @param s
	 *            The CharSequence that will be formatted.
	 */
	public static String formatAsInt(CharSequence s) {
		String temp = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '0' || s.charAt(i) == '1' || s.charAt(i) == '2'
					|| s.charAt(i) == '3' || s.charAt(i) == '4'
					|| s.charAt(i) == '5' || s.charAt(i) == '6'
					|| s.charAt(i) == '7' || s.charAt(i) == '8'
					|| s.charAt(i) == '9')
				temp += s.charAt(i);
		}
		return temp;
	}

	/**
	 * Formats the given CharSequence as representation of an double, removing
	 * any unallowed characters.
	 * 
	 * @param s
	 *            The CharSequence that will be formatted.
	 * @param allowNegative
	 *            Whether or not the '-' character is allowed.
	 */
	public static String formatAsDouble(CharSequence s) {
		boolean hasPeriod = false;
		if (s.length() == 0)
			return "";
		String temp = "";
		if (s.charAt(0) == '-')
			temp += s.charAt(0);
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '0' || s.charAt(i) == '1' || s.charAt(i) == '2'
					|| s.charAt(i) == '3' || s.charAt(i) == '4'
					|| s.charAt(i) == '5' || s.charAt(i) == '6'
					|| s.charAt(i) == '7' || s.charAt(i) == '8'
					|| s.charAt(i) == '9')
				temp += s.charAt(i);
			if (s.charAt(i) == '.' && !hasPeriod) {
				hasPeriod = true;
				temp += '.';
			}
		}
		return temp;
	}

	public static String abs(String text) {
		return text.replace("-", "");
	}

	public static OnClickListener dimensionButtonListener(
			final Activity activity, final int requestCode, final String title,
			final String prompt, final boolean allowNegative) {

		final Intent intent = new Intent(activity.getBaseContext(),
				DimensionEditDialog.class);
		intent.putExtra("title", title);
		intent.putExtra("prompt", prompt);
		intent.putExtra("allowNegative", allowNegative);
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				activity.startActivityForResult(intent, requestCode);
			};
		};
	}

	/**
	 * Rounds a number to a certain value.
	 * 
	 * @param input
	 *            The value to be rounded.
	 * @param roundTo
	 *            The multiple that will the input will be rounded to.
	 * @return The input, rounded.
	 */
	public static double round(double input, double roundTo) {
		double lower = roundTo * (int) (input / roundTo);
		if (Math.abs(input - lower) < roundTo / 2) {
			return lower;
		}
		return lower + roundTo;
	}

	static class Dimension implements Serializable {
		/**
			 * 
			 */

		private static final long serialVersionUID = 1L;

		/**
		 * Takes a fraction as an integer and returns a simplified string
		 * representation of it.
		 * 
		 * @param fraction
		 *            An integer representing the numerator of the fraction. The
		 *            denominator is 16.
		 * @return A string representing the value of this fraction.
		 */
		private static String simplify(int fraction) {
			// TODO V2 Maybe there's a way to show a fraction in a "pretty" way

			switch (fraction) {
			case 0:
				return "0/16";
			case 1:
				return "1/16";
			case 2:
				return "1/8";
			case 3:
				return "3/16";
			case 4:
				return "1/4";
			case 5:
				return "5/16";
			case 6:
				return "3/8";
			case 7:
				return "7/16";
			case 8:
				return "1/2";
			case 9:
				return "9/16";
			case 10:
				return "5/8";
			case 11:
				return "11/16";
			case 12:
				return "3/4";
			case 13:
				return "13/16";
			case 14:
				return "7/8";
			case 15:
				return "15/16";
			default:
				return "error";
			}
		}

		/**
		 * If hasContent is false, then this is just a wrapper for a dimension
		 * that will exist one day.
		 */
		private boolean hasContent = true;
		private boolean negative = false;
		private int feet, inches, fraction;

		/**
		 * Creates an empty dimension. Dimensions should always be initialized.
		 */
		public Dimension() {
			hasContent = false;
		}

		/**
		 * Create a dimension from another dimension.
		 * 
		 * @param other
		 *            The dimension to copy values from.
		 */
		public Dimension(Dimension other) {
			hasContent = true;
			feet = other.feet;
			inches = other.inches;
			fraction = other.fraction;
			negative = other.negative;
			check();
		}

		/**
		 * Create a dimension from a double.
		 * 
		 * @param input
		 *            The double that this dimension will be created from.
		 */
		public Dimension(double input) {
			hasContent = true;
			setDimension(input);
		}

		/**
		 * Create a dimension from a feet,inches,fraction and ensures that the
		 * dimension is formatted properly.
		 * 
		 * @param feet
		 * @param inches
		 * @param fraction
		 *            An integer representing the numerator of the fraction. The
		 *            denominator is 16.
		 */
		public Dimension(int feet, int inches, int fraction) {
			hasContent = true;
			this.feet = feet;
			this.inches = inches;
			this.fraction = fraction;
			check();
		}

		/**
		 * Create a dimension from a feet,inches,fraction and ensures that the
		 * dimension is formatted properly. If negative is true, this dimension
		 * will be negative.
		 * 
		 * @param feet
		 * @param inches
		 * @param fraction
		 *            An integer representing the numerator of the fraction. The
		 *            denominator is 16.
		 * @param negative
		 *            If true, the fraction will be negative. If false, the
		 *            fraction will not be negative.
		 */
		public Dimension(int feet, int inches, int fraction, boolean negative) {
			this(feet, inches, fraction);
			this.negative = negative;
			check();
		}

		/**
		 * Add another dimension to this dimension
		 * 
		 * @param dim
		 *            The dimension that will be added to this one.
		 */
		public void add(Dimension dim) {
			setDimension(toDouble() + dim.toDouble());
		}

		/**
		 * Properly formats the dimension.
		 */
		public void check() {

			if (toDouble() < 0 ^ negative) {
				negative = true;
			}
			if (fraction > 15) {
				inches += fraction / 16;
				fraction %= 16;
			}
			if (inches > 11) {
				feet += inches / 12;
				inches %= 12;
			}
			while (fraction < 0) {
				inches -= 1;
				fraction += 16;
			}
			while (inches < 0) {
				feet -= 1;
				inches += 12;
			}
		}

		/**
		 * Divides this dimension by a number.
		 * 
		 * @param number
		 *            The number to divide this dimension by.
		 */
		public void divide(double number) throws DivideByZeroException {
			if (number == 0)
				throw new DivideByZeroException(":-(");
			setDimension(toDouble() / number);
		}

		/**
		 * Determines if this dimension represents the same value as another
		 * dimension.
		 * 
		 * @param other
		 *            The other dimension.
		 * @return True if the dimensions represent the same value, false if
		 *         they represent different values.
		 */
		public boolean equals(Dimension other) {
			if (!hasContent)
				return other.equals(0);
			return toDouble() == other.toDouble();
		}

		/**
		 * Determines if this dimension represents the same value as the input.
		 * 
		 * @param length
		 *            A double representing a dimension.
		 * @return True if the dimension is equal to the input, false otherwise.
		 */
		public boolean equals(double length) {
			if (!hasContent)
				return length == 0;
			return toDouble() == length;
		}

		public int getFeet() {
			return feet;
		}

		/**
		 * Returns the fractional part associated with this dimension
		 * 
		 * @return An integer representing the numerator of the fraction. The
		 *         denominator is 16.
		 */
		public int getFraction() {
			return fraction;
		}

		public int getInches() {
			return inches;
		}

		public boolean greaterThan(Dimension other) {
			return toDouble() > other.toDouble();
		}

		public boolean isNegative() {
			return negative;
		}

		public boolean lessThan(Dimension other) {
			return toDouble() < other.toDouble();
		}

		/**
		 * Multiplies this dimension by a number.
		 * 
		 * @param number
		 */
		public void multiply(double number) {
			setDimension(toDouble() * number);
		}

		/**
		 * Returns a dimension that is the negative version of this one
		 */
		public Dimension negative() {
			return new Dimension(0 - toDouble());
		}

		/**
		 * Sets the dimension from a number representing the value of the
		 * dimension.
		 * 
		 * @param input
		 *            The number that the dimension will be set from.
		 */
		public void setDimension(double input) {

			hasContent = true;
			input = Util.round(input, C.SIGDIM);

			feet = 0;
			inches = 0;
			fraction = 0;
			if (input < 0) {
				negative = true;
				input = Math.abs(input);
			} else {
				negative = false;
			}
			feet = (int) (input / 12);
			input %= 12;
			inches = (int) input;
			input -= (int) input;
			fraction = (int) (16 * input);

		}

		/**
		 * Subtracts another dimension from this one.
		 * 
		 * @param dim
		 *            The dimension that will be subtracted.
		 */
		public void subtract(Dimension dim) {
			setDimension(toDouble() - dim.toDouble());
		}

		/**
		 * Returns the double that this dimension represents. Useful for
		 * arithmetic operations.
		 * 
		 * @return
		 */
		public double toDouble() {
			double value = (double) feet * 12 + inches + (double) fraction / 16;
			return negative ? 0 - value : value;
		}

		/**
		 * Returns the float that this dimension represents. Useful for
		 * graphical operations.
		 * 
		 * @return
		 */
		public float toFloat() {
			return (float) toDouble();
		}

		/**
		 * Returns a pretty string representing this dimension
		 */
		@Override
		public String toString() {
			if (!hasContent) {
				return "No Dimension";
			}
			String value = "";
			if (feet != 0) {
				value += feet + "' ";
				if (inches != 0) {
					value += inches;
					if (fraction != 0) {
						value += " + " + simplify(fraction) + "\"";
					} else {
						value += "\"";
					}
				} else {
					if (fraction != 0) {
						value += "0 + " + simplify(fraction) + "\"";
					}
				}
			} else {
				if (inches != 0) {
					value += inches;
					if (fraction != 0) {
						value += " + " + simplify(fraction) + "\"";
					} else {
						value += "\"";
					}
				} else {
					if (fraction != 0) {
						value += simplify(fraction) + "\"";
					} else {
						value = "0\"";
					}
				}
			}
			return value;
		}

		/**
		 * Sets the value of this dimension equal to the value of another
		 * dimension.
		 * 
		 * @param other
		 *            The dimension with that value.
		 */
		public void setValue(Dimension other) {
			hasContent = true;
			setDimension(other.toDouble());
		}

		/**
		 * If a double doesn't have content, it can't be used.
		 * 
		 * @return true if the dimensions is usable, false otherwise
		 */
		public boolean hasContent() {
			return hasContent;
		}

		public Dimension positive() {
			return new Dimension(Math.abs(this.toDouble()));
		}

		protected void readObject(ObjectInputStream in) throws IOException,
				ClassNotFoundException, BuildException {
			GetField field = in.readFields();
			feet = field.get("feet", 0);
			inches = field.get("inches", 0);
			fraction = field.get("fraction", 0);
			negative = field.get("negative", false);
			hasContent = true;
		}

	}

}
