package com.zgrannan.crewandroid;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;

import com.zgrannan.crewandroid.Pieces.Piece;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * //This class defines fasteners, wood, etc...
 * 
 * @author Zack Grannan
 * @version 0.94
 * 
 */
public class Consumables {

	/**
	 * A fastener is anything that can fasten two materials.
	 * 
	 * @author Zack Grannan
	 * @version 0.94
	 * 
	 */
	public abstract static class Fastener implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * type + length, can be used to find a fastener
		 */
		private String name;
		protected Dimension length;

		public Fastener(Dimension length) {
			this.length = length;
			name = length + " " + getType();
		}

		public Fastener(double length) {
			this(new Dimension(length));
		}

		/**
		 * Returns the type of fastener this is.
		 * 
		 * @return screw,staple,hand staple, etc as a String.
		 */
		abstract String getType();

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * Attaches cloth to wood.
	 * 
	 * @author Zack Grannan
	 * @version 0.94
	 * 
	 */
	public static class HandStaple extends Fastener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HandStaple() {
			super(0.125); // Length = 1/8"
		}

		@Override
		protected String getType() {
			return "hand staple";
		}

	}

	/**
	 * A material can be a stick of wood, a sheet of wood, a sheet of cloth, etc
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 */
	public abstract static class Material implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected Dimension width, depth;

		/**
		 * The name uniquely identifies the material
		 */
		private String name;

		Material(Dimension width, Dimension depth, String name) {
			this(depth, name);
			this.width = new Dimension(width);
		}

		Material(Dimension depth, String name) {
			this.depth = new Dimension(depth);
			this.name = name;
		}

		Material(double width, double depth, String name) {
			this(new Dimension(width), new Dimension(depth), name);
		}

		Material(double width, String name) {
			this(new Dimension(width), name);
		}

		public Dimension getDepth() {
			return depth;
		}

		public Dimension getWidth() {
			return width;
		}

		/**
		 * This function is only used when comparing pieces for sorting.
		 * 
		 * @return A highly arbitrary unique integer.
		 */
		public int id() {

			for (int i = 0; i < woodstick.length; i++) {
				if (this == woodstick[i]) {
					return i;
				}
			}
			for (int i = 0; i < sheet.length; i++) {
				if (this == sheet[i]) {
					return i + woodstick.length;
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Determines if this material is the same as another material, does
		 * this by checking the name to see if it is the same.
		 * 
		 * @param other
		 *            The material that will be tested against this one.
		 * @return True if the materials are the same, false otherwise.
		 */
		public boolean equals(Material other) {
			if (other != null)
				return name.equals(other.name);
			else
				return false;
		}

		/**
		 * Shows a dialog with information about the material.
		 * @param context
		 */
		public abstract void showDialog(Activity activity);

	}

	public static class Screw extends Fastener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Finds the ideal screw to fasten a sheet to a woodstick. If the ideal
		 * screw can't be found, returns null.
		 * 
		 * @param sheet
		 * @param wood
		 * @return A screw that will fasten the sheet to the wood.
		 */
		public static Screw getScrew(Sheet sheet, WoodStick wood) {

			if (sheet.toString().equals("3/4\" Plywood")) {
				if (wood.toString().equals("2x4"))
					return Consumables.getScrew(new Dimension(1.625));
				if (wood.toString().equals("1x3") || wood.toString().equals("1x4"))
					if (wood.getDepth().toFloat() < 1.0f)
						return Consumables.getScrew(new Dimension(1.25));
					else
						return Consumables.getScrew(new Dimension(1.625));
			}
			if (sheet.toString().equals("1/4\" Plywood")
					|| sheet.toString().equals("Luaun")) {
				if (wood.toString().equals("1x4")
						|| wood.toString().equals("1x3"))
					return Consumables.getScrew(new Dimension(1.25));
			}
			return null;
		}

		/**
		 * Finds the ideal screw to fasten a woodstick to another woodstick. If
		 * there is no ideal screw, returns null. Note that the first argument
		 * is the woodstick where the head of the screw will reside. i.e wood1
		 * is fastened into wood2, not the other way around
		 * 
		 * @param wood1
		 * @param wood2
		 * @return A screw that will fasten wood1 into wood2, or null if no
		 *         screw can be found.
		 */
		public static Screw getScrew(WoodStick wood1, WoodStick wood2) {

			if (wood1.toString().equals("2x4")
					&& wood2.toString().equals("2x4")) {
				return Consumables.getScrew(new Dimension(3));
			}
			if (wood1.toString().equals("1x3")
					&& wood2.toString().equals("1x3")) {
				return Consumables.getScrew(new Dimension(1.625));
			}
			if (wood1.toString().equals("1x4")
					&& wood2.toString().equals("1x4")) {
				return Consumables.getScrew(new Dimension(1.625));
			}
			return null;
		}

		Screw(double length) {
			super(length);
		}

		@Override
		String getType() {
			return "screw";
		}
	}

	/**
	 * Sheets can either be sheets of wood or cloth.
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class Sheet extends Material {

		private static final long serialVersionUID = 1L;

		Sheet(Dimension depth, String name) {
			super(depth, name);
		}

		Sheet(double depth, String name) {
			super(depth, name);
		}

		/**
		 * Tries to find a fastener that will attach a woodstick to this sheet,
		 * or returns null if no fastener can be found.
		 * 
		 * @param woodstick
		 *            The woodstick that this sheet will attach to.
		 * @return An appropriate fastener, or null
		 */
		public Fastener getFastener(WoodStick woodstick) {

			Screw s = Screw.getScrew(this, woodstick);
			if (s != null) // If no screw is found, checks to see if it is cloth
				return s;
			if (this.toString().equals("Cloth"))

				/*
				 * If this is cloth, then return handStaple
				 */
				return handStaple;
			return null;

			/*
			 * Otherwise return nothing TODO V2 There's a better way to do this
			 * that involves a getFastener() method in Fastener
			 */

		}

		/**
		 * Tries to find a screw that will attach a woodstick to this sheet.
		 * 
		 * @param woodstick
		 * @return A screw that works, or null.
		 */
		public Screw getScrew(WoodStick woodstick) {
			return Screw.getScrew(this, woodstick);
		}

		public Dimension maxWidth() {
			return new Dimension(4);
		}

		@Override
		public void showDialog(Activity activity) {
			Util.showQuickDialog(activity, toString(), activity.getString(R.string.depth) + ": " + depth);
		}

	}

	public static class WoodStick extends Material {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		WoodStick(Dimension width, Dimension depth, String name) {
			super(width, depth, name);
		}

		WoodStick(double width, double depth, String name) {
			super(width, depth, name);
		}

		/**
		 * Finds a fastener that will attach another woodstick to this one, or
		 * returns null if none can be found.
		 * 
		 * @param woodstick
		 * @return An appropriate fastener, or null
		 */
		public Fastener getFastener(WoodStick woodstick) {
			return Screw.getScrew(this, woodstick);
		}

		/**
		 * Finds a screw that will attach another woodstick to this one or
		 * return null if no screw can be found.
		 * 
		 * @param woodstick
		 * @return A screw that will attach another woodstick to this one, or
		 *         null.
		 */
		public Screw getScrew(WoodStick woodstick) {
			return Screw.getScrew(this, woodstick);
		}

		@Override
		public void showDialog(Activity context) {
			Util.showQuickDialog(context, toString(), 
					context.getString(R.string.depth) + ": " + depth + "\n\n" + context.getString(R.string.width) +": "
					+ width);
		}

	}

	public final static Screw[] screw = { new Screw(3), new Screw(1.625),
			new Screw(1.25) };
	public final static HandStaple handStaple = new HandStaple();

	public static final WoodStick oneByFour = new WoodStick(0.75, 3.5, "1x4"),
			oneByFourOnFlat = new WoodStick(3.5, 0.75, "1x4"),
			twoByFour = new WoodStick(1.5, 3.5, "2x4"),
			oneByThree = new WoodStick(0.75, 2.75, "1x3"),
			oneByThreeOnFlat = new WoodStick(2.75, 0.75, "1x3");

	/**
	 * All of the wood sticks that can be used.
	 */
	public static final WoodStick[] woodstick = { oneByFour, oneByFourOnFlat,
			twoByFour, oneByThree, oneByThreeOnFlat };

	public static final Sheet threeQuartersPly = new Sheet(0.75,
			"3/4\" Plywood"), oneQuarterPly = new Sheet(0.25, "1/4\" Plywood"),
			luaun = new Sheet(3.0 / 16.0, "Luaun"), cloth = new Sheet(
					1.0 / 16.0, "Cloth"), noSheet = new Sheet(0, "None");

	/**
	 * All of the sheets that can be used.
	 */
	public static final Sheet[] sheet = { threeQuartersPly, oneQuarterPly,
			luaun, cloth, noSheet };
	
	/**
	 * The sheets that are viewable for the material manager.
	 */
	public static final Sheet[] sheetForManage = {threeQuartersPly, oneQuarterPly,
		luaun, cloth};
	
	/**
	 * The woodsticks that are viewable for the material manager.
	 */
	public static final WoodStick[] woodstickForManage = {oneByThree,oneByFour,twoByFour};

	public static Screw getScrew(Dimension dim) {
		for (int i = 0; i < screw.length; i++) {
			if (screw[i].length.equals(dim))
				return screw[i];
		}
		return null;
	}

	public static Sheet getSheet(String name) {
		for (int i = 0; i < sheet.length; i++) {
			if (sheet[i].toString().equals(name)) {
				return sheet[i];
			}
		}
		return null;
	}

	public static WoodStick getWoodStick(String name) {
		for (int i = 0; i < woodstick.length; i++) {
			if (woodstick[i].toString().equals(name)) {
				return woodstick[i];
			}
		}
		return null;
	}

	public static boolean setMaterial(Piece[] pieces, Material material) {
		if (pieces == null)
			return false;
		for (int i = 0; i < pieces.length; i++) {
			pieces[i].setMaterial(material);
		}
		return true;
	}
}