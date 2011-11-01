package com.zgrannan.crewandroid;

/**
 * Constants
 * 
 * @author Zack Grannan
 * @version 0.9
 */
public class C {

	/*
	 * Piece visibility IDs
	 */
	public static final int INVISIBLE = 0, VISIBLE = 1, SELECTED = 2,
			UNDER = 3;

	/*
	 * Index of spinner positions for building.
	 */
	public static final int PLATFORM = 0, HOLLYWOOD = 1, BROADWAY = 2,
			DOORFRAME = 3, STUDWALL = 4;

	/*
	 * Homescreen interface IDs
	 */
	public static final int BUILD = 0, SAVED_PIECES = 1, DIM_CALCULATOR = 2,
			STAIR_CALCULATOR = 3, TRIG_CALCULATOR = 4, ROPE_CALCULATOR = 5,
			VIEW_MATERIAL = 6, CONTACT = 7, BACKSTAGE_BADGER = 8, QUIT = 9;

	/*
	 * Geometry stuff
	 */
	public static final boolean HORIZONTAL = false, VERTICAL = true;
	public static final int BELOW = 0, ABOVE = 1;
	public static final int LEFT = 2, RIGHT = 3;

	/*
	 * Utility stuff
	 */
	public static final int FAIL = 0, PASS = 1;

	/*
	 * Arithmetic stuff
	 */
	public static final int ADD = 0, SUBTRACT = 1, MULTIPLY = 2, DIVIDE = 3;

	/*
	 * Graphic stuff
	 */
	public static final int UNDER_LINE_DENSITY = 2;

	/*
	 * Builder stuff
	 */
	public static final int WIDTH_EDIT = 5, LENGTH_EDIT = 6,
			DOORHEIGHT_EDIT = 7, DOORWIDTH_EDIT = 8;

	/**
	 * The String representing the filename that saves the set pieces.
	 */
	public static final String FILENAME = "SavedData";

	/**
	 * The ID for the dialog that occurs when the user first launches the
	 * application.
	 */
	public static final int FIRST_START_DIALOG = 0;

	/**
	 * The ID for the dialog that occurs when the user hits the "Contact" Link
	 */
	public static final int CONTACT_DIALOG = 1;

	/**
	 * Any number less than this can be treated as if it's zero. Equal to 1/32
	 * of an inch.
	 */
	public static final double EPSILON = 0.03125;

	/**
	 * The smallest significant value of a dimension.
	 */
	public static final double SIGDIM = 1.0 / 16.0;
}
