package com.zgrannan.crewandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Pieces.Piece;
import com.zgrannan.crewandroid.Util.BuildException;
import com.zgrannan.crewandroid.Util.CompleteInstruction;
import com.zgrannan.crewandroid.Util.Cutlist;
import com.zgrannan.crewandroid.Util.Dimension;
import com.zgrannan.crewandroid.Util.FragVisibilityInstruction;
import com.zgrannan.crewandroid.Util.Instruction;
import com.zgrannan.crewandroid.Util.PartialInstruction;

/**
 * This class describes set pieces. All set pieces are made of {@link Frag}
 * ments. Every set piece has it's own type of fragment.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public abstract class Buildable implements Serializable, CanDraw {

	/**
	 * The maximum width of the set piece. If the set piece is larger than this,
	 * {@link check()} will return an error.
	 */
	private static final Dimension maxSetpieceWidth = new Dimension(12 * 96);

	/**
	 * The maximum length (or height) of the set piece. If the set piece is
	 * larger than this, {@link check()} will return an error.
	 */
	private static final Dimension maxSetpieceLength = new Dimension(12 * 96);

	/*
	 * Serializable Entities
	 */
	protected Dimension length, width;
	protected WoodStick woodstick;
	protected Sheet sheet;
	private String filename;
	private static final long serialVersionUID = 1L;
	/*
	 * Non-serializable entities. Remember to recreate these when the set piece
	 * is being restored from a flattened file.
	 */
	transient protected Dimension woodThickness;
	transient protected Dimension minWidth, maxWidth, minLength, maxLength;
	transient private List<Instruction> instructions;
	transient protected Frag[][] frags;
	transient boolean made;

	/**
	 * When running through instructions, this is the horizontal index of the
	 * current fragment.
	 */
	transient private int currentFragX;

	/**
	 * When running through instructions, this is the vertical index of the
	 * current fragment.
	 */
	transient private int currentFragY;

	/**
	 * When running through instructions, this the "number" of the current
	 * fragment this is used in {@link ViewInstructions}.
	 */
	transient private int currentFragNum;

	/**
	 * Why does this exist, you may ask? Currently it is necessary in order to
	 * recreate set pieces by using saved data from older versions of the
	 * program that serialized everything. It doesn't actually do anything. If
	 * you delete it, {@link CrewAndroid.loadSavedDataFromFile()} will fail to
	 * load the set pieces.
	 * 
	 * @author Zack Grannan
	 * @version 0.96
	 * 
	 */
	public static class PieceVisibilityObject implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * Create the buildable, and calls {@link Piece.resetChar()}. Also creates
	 * the instruction list.
	 * 
	 * @param length
	 *            The length (or height) of the set piece (vertical)
	 * @param width
	 *            The width of the set piece (horizontal)
	 * @param woodstick
	 *            The type of wood that makes the frame of the set piece
	 * @throws BuildException
	 *             Thrown if the set piece can't be built, probably because the
	 *             dimensions are invalid. See {@link check()}
	 */
	public Buildable(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) throws BuildException {
		make(context, length, width, woodstick, sheet);
	}

	/**
	 * No-arg constructor. Resets the piece labeling..
	 */
	public Buildable() {
		Piece.resetName();
	}

	/**
	 * Generates a new cutlist and returns it to the user.
	 * 
	 * @return The cutlist generated from the pieces in the set piece.
	 */
	public Cutlist getCutlist() {
		return new Cutlist(getPieces());
	}

	/**
	 * Returns the filename of the set piece.
	 * 
	 * @return The filename of the set piece.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Retrieves the instruction at the given index
	 * 
	 * @param index
	 *            The position of the instruction in {@link instructions}
	 * @return The {@link Instruction} at the given index, or null if no
	 *         instruction is found.
	 */
	public Instruction getInstruction(int index) {
		try {
			return instructions.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Returns the name that corresponds to the piece type and dimensions. This
	 * is NOT the same as the filename
	 * 
	 * @return The name
	 */
	public abstract String getName(Context context);

	/**
	 * 
	 * @return The number of instructions that the set piece requires in order
	 *         to be built
	 * @see Util.Instruction
	 */
	public int numInstructions() {
		return instructions.size();
	}

	/**
	 * Changes the visibility of the pieces in the set piece to reflect the
	 * given instruction
	 * 
	 * @param instruction
	 *            The instruction that the set piece will use to get visibility
	 *            info from
	 * @see Util.Instruction
	 */
	public void parseInstruction(CompleteInstruction instruction) {
		frags[currentFragX][currentFragY].parseInstruction(instruction);
	}

	/**
	 * Changes the visibility of the fragments in the piece.
	 * 
	 * @param instruction
	 */
	public void parseInstruction(FragVisibilityInstruction instruction) {
		if (instruction.showAll()) {
			for (int i = 0; i < frags.length; i++) {
				for (int j = 0; j < frags[i].length; j++) {
					frags[i][j].show();
				}
			}
		} else {
			frags[currentFragX][currentFragY].hide();
			if (frags[currentFragX].length - 1 == instruction.getY()) {
				currentFragX++;
				currentFragY = 0;
			} else {
				currentFragY++;
			}
			currentFragNum++;
		}
	}

	/**
	 * Takes an Instruction and forwards it to the proper parseInstruction
	 * method depending on whether it is partial or complete. If it is neither
	 * partial nor complete a ClassCastException is thrown.
	 * 
	 * @param instruction
	 *            The instruction.
	 */
	public void parseInstruction(Instruction instruction) {
		if (instruction instanceof CompleteInstruction) {
			parseInstruction((CompleteInstruction) instruction);
		} else if (instruction instanceof PartialInstruction) {
			parseInstruction((PartialInstruction) instruction);
		} else {
			parseInstruction((FragVisibilityInstruction) instruction);
		}
	}

	/**
	 * Changes the visibility of the pieces in the set piece to reflect the
	 * given instruction
	 * 
	 * @param instruction
	 *            The instruction that the set piece will use to get visibility
	 *            info from.
	 * @see Util.Instruction
	 */
	public void parseInstruction(PartialInstruction instruction) {
		frags[currentFragX][currentFragY].parseInstruction(instruction);
	}

	/**
	 * Sets the filename of the set piece. Performs no error-checking, yet.
	 * 
	 * @param string
	 *            The filename that the piece will be set to.
	 */
	public void setFilename(String string) {
		filename = string;
	}

	/**
	 * 
	 * @return An array of sheets that can be used to build this set piece.
	 */
	public abstract Sheet[] getValidSheets();

	/**
	 * 
	 * @return An array of wood sticks that can be used to build this set piece.
	 */
	public abstract WoodStick[] getValidWoodSticks();

	/**
	 * Adds another instruction to the set piece to the end of the instruction
	 * list.
	 * 
	 * @param instruction
	 *            The instruction that is to be added to the list of
	 *            instructions.
	 * @see Util.Instruction
	 */

	/**
	 * Returns the array of pieces that make up this object.
	 * 
	 * @return The piece array.
	 */
	public Piece[] getPieces() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int i = 0; i < frags.length; i++) {
			for (int j = 0; j < frags[i].length; j++) {
				for (Piece piece : frags[i][j].getPieces()) {
					pieces.add(piece);
				}
			}
		}
		Piece[] pieceArray = new Piece[pieces.size()];
		pieces.toArray(pieceArray);
		return pieceArray;
	}

	@Override
	public Dimension getHeight() {
		return length;
	}

	@Override
	public Dimension getWidth() {
		return width;
	}

	/**
	 * Copies the given information into this piece.This should be called in any
	 * subclass that implements make(). Resets the labeling mechanism, calls
	 * upon the subclass to {@link make()} itself, and creates instructions for
	 * the set piece.
	 * <p>
	 * 
	 * Step 5 of build, called from the subclass's make(...) function. That
	 * function was passed all the neccesary data to create the set piece as
	 * arguments. By this point, all of the setpiece-specific data should be
	 * loaded into the set piece.
	 * 
	 * 
	 * @param length
	 * 
	 * @param width
	 * 
	 * @param woodstick
	 * 
	 * @param sheet
	 */
	protected BuildResult make(Context context, Dimension length,
			Dimension width, WoodStick woodstick, Sheet sheet) {
		this.length = new Dimension(length);
		this.width = new Dimension(width);
		this.woodstick = woodstick;
		this.sheet = sheet;
		woodThickness = new Dimension(woodstick.getWidth());
		Piece.resetName();
		make(context);

		return new BuildResult(context, true);
	}

	/**
	 * Creates the instructions for the set piece. Called from {@link
	 * make(Context)}.
	 * 
	 * @param contex
	 */
	protected void setInstructions(Context context) {
		instructions = new LinkedList<Instruction>();
		if (frags.length == 1 && frags[0].length == 1) {
			instructions
					.addAll(frags[0][0].instructions);
		} else {
			for (int i = 0; i < frags.length; i++) {
				for (int j = 0; j < frags[i].length; j++) {
					instructions
							.addAll(frags[i][j].instructions);
					instructions.add(new FragVisibilityInstruction(context
							.getString(R.string.fragment_done), i, j));
				}
			}
			instructions.add(new FragVisibilityInstruction(context
					.getString(R.string.attach_fragments)));
		}
		currentFragNum = 1;
		made = true;

	}

	/**
	 * After all of the data about the set piece has been input, this method
	 * does the job of actually creating the fragments of the set piece.
	 * <p>
	 * 
	 * Also calls {@link setInstructions()}, loading the instructions from the
	 * fragment into the set piece and creates {@link FragVisibilityInstruction}
	 * s.
	 * 
	 * Step 6 of build, called from {@link
	 * Buildable.make(Dimension,Dimension,WoodStick,Sheet)}
	 */
	protected abstract BuildResult make(Context context);

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
	 *            The objectstream representing this particular class, it does
	 *            not extend to superclass or subclasses.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		GetField field = in.readFields();
		length = (Dimension) field.get("length", null);
		width = (Dimension) field.get("width", null);
		woodstick = (WoodStick) field.get("woodstick", null);
		sheet = (Sheet) field.get("sheet", null);
		filename = (String) field.get("filename", null);
		made = false;
		Piece.resetName();
	}

	/**
	 * The different types of things that can be built. Please don't change the
	 * order of these objects until the {@link Builder} UI is updated.
	 */
	public static Buildable[] types = new Buildable[] { new Platform(),
			new Hollywood(), new Broadway(), new Doorframe(), new Studwall() };

	/**
	 * Resets the frag-pointing mechanism to the first frag, so that
	 * {@link Util.FragVisibilityInstruction} works. This is called by
	 * {@link ViewInstructions} when the user pressess the back button and all
	 * of the instructions need to be re-applied.
	 */
	public void resetInstructions() {
		currentFragX = 0;
		currentFragY = 0;
		currentFragNum = 1;
	}

	/**
	 * 
	 * @return True if the set piece only contains one fragment, false
	 *         otherwise. Used for determining the drawing of DimLines in
	 *         {@link ViewDiagram}.
	 */
	public boolean onlyOneFrag() {
		return (frags[0].length == 1 && frags.length == 1);
	}

	/**
	 * 
	 * @return The current fragment pointed to for instructions. Used in
	 *         {@link ViewInstructions}.
	 */
	public int getCurrentFragNum() {
		return currentFragNum;
	}

	/**
	 * Creates an array of vertexes for the piece.
	 * 
	 * @param scale
	 * @return
	 */
	public List<float[]> toVertices() {
		float offX = -width.toFloat() / 2;
		float offY = -length.toFloat() / 2;
		Piece[] pieces = getPieces();

		ArrayList<float[]> vertices = new ArrayList<float[]>();
		for (int i = 0; i < pieces.length; i++) {
			if (pieces[i] != null && pieces[i].material != null
					&& !(pieces[i].material instanceof Sheet)) {
				vertices.add(pieces[i].toVertices(offX, offY));
			}
		}
		return vertices;
	}

	public List<float[]> sheetVertices() {
		float offX = -width.toFloat() / 2;
		float offY = -length.toFloat() / 2;
		Piece[] pieces = getPieces();

		ArrayList<float[]> vertices = new ArrayList<float[]>();
		for (int i = 0; i < pieces.length; i++) {
			if (pieces[i] != null
					&& !pieces[i].material.equals(Consumables.noSheet)
					&& (pieces[i].material instanceof Sheet)) {
				vertices.add(pieces[i].toVertices(offX, offY));
			}
		}
		return vertices;
	}

	public static BuildResult check(Context context, Dimension length,
			Dimension width) {
		BuildResult result = new BuildResult();
		if (width.greaterThan(maxSetpieceWidth)) {
			result.addMessage(String.format(
					context.getString(R.string.width_must_be_no_greater_than),
					maxSetpieceWidth));
			result.fail();
		}
		if (length.greaterThan(maxSetpieceLength)) {
			result.addMessage(String.format(
					context.getString(R.string.length_must_be_no_greater_than),
					maxSetpieceLength));
			result.fail();
		}
		return result;
	}

	/**
	 * Determines whether or not the piece has been made.
	 * 
	 * @return True if the piece is made, false otherwise.
	 */
	public boolean isMade() {
		return made;
	}
}