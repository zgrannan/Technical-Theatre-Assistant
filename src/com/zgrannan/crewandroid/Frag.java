package com.zgrannan.crewandroid;

import java.util.ArrayList;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Pieces.Piece;
import com.zgrannan.crewandroid.Util.CompleteInstruction;
import com.zgrannan.crewandroid.Util.Cutlist;
import com.zgrannan.crewandroid.Util.Dimension;
import com.zgrannan.crewandroid.Util.Instruction;
import com.zgrannan.crewandroid.Util.PartialInstruction;

/**
 * This class contains class description for a "Frag": a basic building-block of
 * a set piece.
 * 
 * @author Zack Grannan
 * @version 0.96
 */
public abstract class Frag {

	/**
	 * Used for instructions to determine the visibility of a piece within a
	 * fragment.
	 * 
	 * @author Zack Grannan
	 * @version 0.96
	 * 
	 */
	public static class PieceVisibilityObject {

		/**
		 * The index of the piece in the fragment {@link pieces} array.
		 */
		private int pieceIndex;

		/**
		 * An integer representing the visibility of the piece.
		 * 
		 * @see Piece.setVisibility()
		 */
		private int visibility;

		public PieceVisibilityObject(int pieceIndex, int visibility) {
			this.pieceIndex = pieceIndex;
			this.visibility = visibility;
		}

		public int getPieceIndex() {
			return pieceIndex;
		}

		public int getVisibility() {
			return visibility;
		}

		public void setPieceIndex(int pieceIndex) {
			this.pieceIndex = pieceIndex;
		}

		public void setVisibility(int visibility) {
			this.visibility = visibility;
		}
	}

	protected Dimension width, length, woodThickness;
	protected double widthD, lengthD, woodThicknessD;
	protected WoodStick woodstick;
	protected Sheet sheet;
	protected Piece[] pieces;
	protected Cutlist cutlist;
	protected ArrayList<Instruction> instructions;

	public WoodStick getWoodstick() {
		return woodstick;
	}

	public void setWoodstick(WoodStick woodstick) {
		this.woodstick = woodstick;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	/**
	 * Modifies the visibility of the pieces in this fragment to correspond to
	 * an instruction
	 * 
	 * @param instruction
	 */
	public void parseInstruction(CompleteInstruction instruction) {

		int[] pieceSettings = instruction.getPieceSettings();
		for (int i = 0; i < pieceSettings.length; i++) {
			if ( pieces[i] != null)
				pieces[i].setVisibility(pieceSettings[i]);
		}

	}

	/**
	 * Forwards the given instruction to the proper instruction handler, or
	 * throws a ClassCastException if the instruction isn't valid.
	 * 
	 * @param instruction
	 */
	public void parseInstruction(Instruction instruction) {
		if (instruction instanceof CompleteInstruction) {
			parseInstruction((CompleteInstruction) instruction);
		} else if (instruction instanceof PartialInstruction) {
			parseInstruction((PartialInstruction) instruction);
		} else {
			throw new ClassCastException(
					"Invalid instruction. Instructions must be either partial or complete");
		}
	}

	/**
	 * Modifies the visibility of the pieces in this fragment to correspond to
	 * an instruction
	 * 
	 * @param instruction
	 */
	public void parseInstruction(PartialInstruction instruction) {

		// If any pieces are currently selected, they must be unselected.
		for (int i = 0; i < pieces.length; i++) {
			if (pieces[i].isSelected())
				pieces[i].unselect();
		}

		PieceVisibilityObject[] pieceSettings = instruction.getPieceSettings();
		for (int i = 0; i < pieceSettings.length; i++) {
			pieces[pieceSettings[i].getPieceIndex()]
					.setVisibility(pieceSettings[i].getVisibility());
		}
	}

	public Cutlist getCutlist() {
		return cutlist;
	}

	public void setCutlist(Cutlist cutlist) {
		this.cutlist = cutlist;
	}

	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(ArrayList<Instruction> instructions) {
		this.instructions = instructions;
	}

	public int numInstructions() {
		return instructions.size();
	}

	public Piece[] getPieces() {
		return pieces;
	}

	public void setPieces(Piece[] pieces) {
		this.pieces = pieces;
	}

	protected void setNextInstruction(Instruction instruction) {
		instructions.add(instruction);
	}

	/**
	 * 
	 * @return The minimum allowed width for the fragment.
	 */
	public abstract Dimension minWidth();

	/**
	 * 
	 * @return The maximum allowed width for the fragment.
	 */
	public abstract Dimension maxWidth();

	/**
	 * 
	 * @return The minimum allowed length for the fragment.
	 */
	public abstract Dimension minLength();

	/**
	 * 
	 * @return The maximum allowed length for the fragment.
	 */
	public abstract Dimension maxLength();

	/**
	 * Creates this fragment after all of the data has been input.
	 * <p>
	 * 
	 * Step 9 of build, called from {@link
	 * make(Dimension,Dimension,WoodStick,Sheet)}.
	 * 
	 * @return
	 */
	public abstract BuildResult make(Context context);

	/**
	 * After the fragment has been build, creates the instructions for building
	 * it.
	 * <p>
	 * 
	 * Step 10 of build, called from {@link make()}.
	 */
	protected abstract void setInstructions(Context context);

	/**
	 * Loads data into the fragment, and then constructs it.
	 * <p>
	 * 
	 * Step 8 of build, called from {@link Frag.make()}.
	 * 
	 * @param length
	 * @param width
	 * @param woodstick
	 * @param sheet
	 * @return
	 */
	public BuildResult make(Context context, Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {
		loadData(length, width, woodstick, sheet);
		return make(context);
	}

	/**
	 * Loads the data into the fragment, based on input from a set piece.
	 * <p>
	 * Step 7.1, inside of {@link Frag.make(Buildable, Class)}.
	 * 
	 * @param setPiece
	 *            The set piece that this fragment will get data from.
	 * 
	 * @see Frag.make(Buildable, Class);
	 */
	public BuildResult make(Context context, Buildable setPiece) {
		this.woodstick = setPiece.woodstick;
		this.sheet = setPiece.sheet;
		woodThickness = woodstick.getWidth();
		woodThicknessD = woodThickness.toDouble();
		return new BuildResult(context, true);
	}

	public void move(Dimension dispX, Dimension dispY) {
		for (Piece piece : pieces) {
			if (piece != null)
				piece.move(dispX, dispY);
		}
	}

	/**
	 * Creates the matrix of fragments used in the set piece.
	 * <p>
	 * 
	 * Step 7 of build. Called from the set pieces make() function.
	 * 
	 * @param setpiece
	 *            The set piece that will contain this fragment array.
	 * @param fragType
	 *            The class of fragments that will be made.
	 * @return The matrix of fragments.
	 */
	protected static Frag[][] make(Context context, Buildable setpiece,
			Class<? extends Frag> fragType) {

		Frag[][] frags;
		Dimension length = setpiece.length;
		Dimension width = setpiece.width;
		WoodStick woodstick = setpiece.woodstick;
		Sheet sheet = setpiece.sheet;
		Frag frag = null;
		try {
			frag = fragType.asSubclass(Frag.class).newInstance();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frag.make(context, setpiece);
		int numFragsX = 0, numFragsY = 0;

		/**
		 * If true, then the set piece will be split so that the bottom-most
		 * fragment is one foot long.
		 */
		boolean mustFixLength = false;

		/**
		 * If true, then the set piece will be split so that the right-most
		 * fragment is one foot long.
		 */
		boolean mustFixWidth = false;

		/**
		 * The amount of length that this set piece has that doesn't divide
		 * nicely into a full-length fragment.
		 */
		double extraLength = length.toDouble() % frag.maxLength().toDouble();

		/**
		 * The amount of width that this set piece has that doesn't divide
		 * nicely into a full-width fragment.
		 */
		double extraWidth = width.toDouble() % frag.maxWidth().toDouble();

		/*
		 * Calculate the number of horizontal and vertical fragments.
		 */
		if (extraLength > 12) {
			numFragsY = (int) (length.toDouble() / frag.maxLength().toDouble()) + 1;
		} else if (extraLength < C.EPSILON) {
			numFragsY = (int) (length.toDouble() / frag.maxLength().toDouble());
		} else {
			numFragsY = (int) (length.toDouble() / frag.maxLength().toDouble()) + 1;
			mustFixLength = true;
		}

		if (extraWidth > 12) {
			numFragsX = (int) (width.toDouble() / frag.maxWidth().toDouble()) + 1;
		} else if (extraWidth < C.EPSILON) {
			numFragsX = (int) (width.toDouble() / frag.maxWidth().toDouble());
		} else {
			numFragsX = (int) (width.toDouble() / frag.maxWidth().toDouble()) + 1;
			mustFixWidth = true;
		}

		/*
		 * Make all of the fragments.
		 */
		frags = new Frag[numFragsX][numFragsY];
		
		if ( numFragsX == 1 && numFragsY == 1){
			try {
				frags[0][0] = fragType.newInstance();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (fragType.getSuperclass() == PlatTypeFrag.class) {
				frags[0][0].loadData(length, width, woodstick, sheet);
				((PlatTypeFrag) frags[0][0]).make(context, true);
			} else {
				frags[0][0].make(context, length, width, woodstick, sheet);
			}
		}else{
			for (int i = 0; i < numFragsX; i++) {
				for (int j = 0; j < numFragsY; j++) {
					Dimension w = null, l = null;
					if ((!mustFixLength && (j < numFragsY - 1)) || extraLength == 0
							|| (mustFixLength && j < numFragsY - 2)) {
						l = new Dimension(frag.maxLength());
					} else if (mustFixLength && j == numFragsY - 2) {
						l = new Dimension(frag.maxLength().toDouble()
								- (12 - extraLength));
					} else if (mustFixLength && j == numFragsY - 1) {
						l = new Dimension(12);
					} else {
						l = new Dimension(extraLength);
					}
	
					if ((!mustFixWidth && (i < numFragsX - 1)) || extraWidth == 0
							|| (mustFixWidth && i < numFragsX - 2)) {
						w = new Dimension(frag.maxWidth());
					} else if (mustFixWidth && i == numFragsX - 2) {
						w = new Dimension(frag.maxWidth().toDouble()
								- (12 - extraWidth));
					} else if (mustFixWidth && i == numFragsX - 1) {
						w = new Dimension(12);
					} else {
						w = new Dimension(extraWidth);
					}
					try {
						frags[i][j] = fragType.newInstance();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					frags[i][j].make(context, l, w, woodstick, sheet);
					Dimension dispX = new Dimension(0), dispY = new Dimension(0);
					for (int a = 0; a < j; a++) {
						dispY.add(frags[i][a].length);
					}
					for (int a = 0; a < i; a++) {
						dispX.add(frags[a][j].width);
					}
					frags[i][j].move(dispX, dispY);
				}
			}
		}
		return frags;
	}

	/**
	 * Loads all the data necessary to construct a fragment.
	 * 
	 * @param length
	 * @param width
	 * @param woodstick
	 * @param sheet
	 */
	private void loadData(Dimension length, Dimension width,
			WoodStick woodstick, Sheet sheet) {
		this.length = length;
		this.width = width;
		this.woodstick = woodstick;
		this.sheet = sheet;
		woodThickness = woodstick.getWidth();
		lengthD = length.toDouble();
		widthD = width.toDouble();
		woodThicknessD = woodThickness.toDouble();
	}

	/**
	 * Hides this fragment.
	 * 
	 * @see ViewInstructions
	 */
	public void hide() {
		for (Piece piece : pieces) {
			piece.setVisibility(C.INVISIBLE);
		}
	}

	/**
	 * Shows this fragment.
	 * 
	 * @see ViewInstructions.
	 */
	public void show() {
		for (Piece piece : pieces) {
			piece.setVisibility(C.VISIBLE);
		}
	}
}