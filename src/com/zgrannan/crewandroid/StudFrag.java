package com.zgrannan.crewandroid;

import java.util.ArrayList;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Fastener;
import com.zgrannan.crewandroid.Geometry.Vertex;
import com.zgrannan.crewandroid.Pieces.RectPiece;
import com.zgrannan.crewandroid.Util.Dimension;
import com.zgrannan.crewandroid.Util.Instruction;
import com.zgrannan.crewandroid.Util.PartialInstruction;

/**
 * A fragment for a {@link Studwall}.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public class StudFrag extends Frag {

	/**
	 * The number of horizontal pieces used to build this studwall fragment,
	 * excluding toggles. Should always equal 2.
	 */
	private int numHorizontal;

	/**
	 * The number of vertical pieces used to build this studwall fragment.
	 */
	private int numVertical;

	/**
	 * The number of toggles used to build this studwall fragment.
	 */
	private int numToggle;

	/**
	 * The index of the last vertical piece used in the studwall fragment.
	 */
	private int lastSidePiece;

	@Override
	public Dimension minWidth() {
		return new Dimension(2 * super.woodstick.getWidth().toDouble());
	}

	@Override
	public Dimension maxWidth() {
		return new Dimension(96);
	}

	@Override
	public Dimension minLength() {
		return new Dimension(2 * super.woodstick.getWidth().toDouble());
	}

	@Override
	public Dimension maxLength() {
		return new Dimension(96);
	}

	@Override
	public BuildResult make(Context context) {
		numHorizontal = 2;
		// Calculate how many vertical pieces are necessary
		if (widthD <= 16) {
			numVertical = 2;
		} else {
			numVertical = (int) (widthD / 16 + (widthD % 16 == 0 ? 0 : 1)) + 1;
		}
		lastSidePiece = numVertical + 1;

		// Calculate how many toggles are necessary
		if (widthD <= 16)
			numToggle = 2;
		else if (widthD <= 32)
			numToggle = 3;
		else if (widthD <= 48)
			numToggle = 5;
		else if (widthD <= 64)
			numToggle = 6;
		else if (widthD <= 80)
			numToggle = 8;
		else
			numToggle = 9;

		pieces = new RectPiece[numHorizontal + numVertical + numToggle];

		// Piece 0 and 1 are the two pieces on the top and bottom
		pieces[0] = new RectPiece(Geometry.origin, widthD, woodstick,
				C.HORIZONTAL);
		pieces[1] = new RectPiece(new Vertex(0, lengthD - woodThicknessD),
				widthD, woodstick, C.HORIZONTAL);

		// Piece 2 is the first side pieces.
		pieces[2] = new RectPiece(new Vertex(0, woodThicknessD), lengthD - 2
				* woodThicknessD, woodstick, C.VERTICAL);

		// Generate the vertical pieces.
		for (int i = 3; i < 1 + numVertical; i++) {
			pieces[i] = new RectPiece(new Vertex(16 * (i - 2) - woodThicknessD
					/ 2, woodThicknessD), lengthD - 2 * woodThicknessD,
					woodstick, C.VERTICAL);
		}

		// The last side pieces.
		pieces[1 + numVertical] = new RectPiece(new Vertex(widthD
				- woodThicknessD, woodThicknessD),
				lengthD - 2 * woodThicknessD, woodstick, C.VERTICAL);

		// Lets put in some toggles!

		if (widthD <= 16) {
			pieces[2 + numVertical] = new RectPiece(new Vertex(woodThicknessD,
					lengthD / 3 - 0.5 * woodThicknessD), widthD - 2
					* woodThicknessD, woodstick, C.HORIZONTAL);
			pieces[3 + numVertical] = new RectPiece(new Vertex(woodThicknessD,
					2 * lengthD / 3 - 0.5 * woodThicknessD), widthD - 2
					* woodThicknessD, woodstick, C.HORIZONTAL);
		}

		else {
			pieces[2 + numVertical] = new RectPiece(new Vertex(woodThicknessD,
					lengthD / 3 - 0.5 * woodThicknessD),
					16 - 1.5 * woodThicknessD, woodstick, C.HORIZONTAL);
			pieces[3 + numVertical] = new RectPiece(new Vertex(woodThicknessD,
					2 * lengthD / 3 - 0.5 * woodThicknessD),
					16 - 1.5 * woodThicknessD, woodstick, C.HORIZONTAL);
			if (numToggle > 2) {
				if (numToggle == 3) {
					pieces[4 + numVertical] = new RectPiece(new Vertex(
							16 + woodThicknessD / 2, lengthD / 2 - 0.5
									* woodThicknessD), widthD - 16 - 1.5
							* woodThicknessD, woodstick, C.HORIZONTAL);
				} else {
					pieces[4 + numVertical] = new RectPiece(new Vertex(
							16 + woodThicknessD / 2, lengthD / 2 - 0.5
									* woodThicknessD), 16 - woodThicknessD,
							woodstick, C.HORIZONTAL);
				}
			}

			if (numToggle > 3) {
				if (numToggle == 5) {
					pieces[5 + numVertical] = new RectPiece(new Vertex(
							32 + woodThicknessD / 2, lengthD / 3 - 0.5
									* woodThicknessD), widthD - 32 - 1.5
							* woodThicknessD, woodstick, C.HORIZONTAL);
					pieces[6 + numVertical] = new RectPiece(new Vertex(
							32 + woodThicknessD / 2, 2 * lengthD / 3 - 0.5
									* woodThicknessD), widthD - 32 - 1.5
							* woodThicknessD, woodstick, C.HORIZONTAL);
				} else {
					pieces[5 + numVertical] = new RectPiece(new Vertex(
							32 + woodThicknessD / 2, lengthD / 3 - 0.5
									* woodThicknessD), 16 - woodThicknessD,
							woodstick, C.HORIZONTAL);
					pieces[6 + numVertical] = new RectPiece(new Vertex(
							32 + woodThicknessD / 2, 2 * lengthD / 3 - 0.5
									* woodThicknessD), 16 - woodThicknessD,
							woodstick, C.HORIZONTAL);
				}
			}

			if (numToggle > 5) {
				if (numToggle == 6) {
					pieces[7 + numVertical] = new RectPiece(new Vertex(
							48 + woodThicknessD / 2, lengthD / 2 - 0.5
									* woodThicknessD), widthD - 48 - 1.5
							* woodThicknessD, woodstick, C.HORIZONTAL);
				} else {
					pieces[7 + numVertical] = new RectPiece(new Vertex(
							48 + woodThicknessD / 2, lengthD / 2 - 0.5
									* woodThicknessD), 16 - woodThicknessD,
							woodstick, C.HORIZONTAL);
				}
			}

			if (numToggle > 6) {
				if (numToggle == 8) {
					pieces[8 + numVertical] = new RectPiece(new Vertex(
							64 + woodThicknessD / 2, lengthD / 3 - 0.5
									* woodThicknessD), widthD - 64 - 1.5
							* woodThicknessD, woodstick, C.HORIZONTAL);
					pieces[9 + numVertical] = new RectPiece(new Vertex(
							64 + woodThicknessD / 2, 2 * lengthD / 3 - 0.5
									* woodThicknessD), widthD - 64 - 1.5
							* woodThicknessD, woodstick, C.HORIZONTAL);
				} else {
					pieces[8 + numVertical] = new RectPiece(new Vertex(
							64 + woodThicknessD / 2, lengthD / 3 - 0.5
									* woodThicknessD), 16 - woodThicknessD,
							woodstick, C.HORIZONTAL);
					pieces[9 + numVertical] = new RectPiece(new Vertex(
							64 + woodThicknessD / 2, 2 * lengthD / 3 - 0.5
									* woodThicknessD), 16 - woodThicknessD,
							woodstick, C.HORIZONTAL);
				}
			}

			if (numToggle > 8) {
				pieces[10 + numVertical] = new RectPiece(new Vertex(
						80 + woodThicknessD / 2, lengthD / 2 - 0.5
								* woodThicknessD), widthD - 80 - 1.5
						* woodThicknessD, woodstick, C.HORIZONTAL);
			}
		}
		setInstructions(context);
		return new BuildResult(context, true);
	}

	@Override
	protected void setInstructions(Context context) {
		instructions = new ArrayList<Instruction>();
		Fastener fastener = woodstick.getFastener(woodstick);

		/*
		 * Instruction for the frame
		 */
		setNextInstruction(new PartialInstruction(context.getString(
				R.string.connect_the_frame_of_the_studwall_using_FASTENERS,
				fastener), new PieceVisibilityObject[] {
				new PieceVisibilityObject(0, C.SELECTED),
				new PieceVisibilityObject(1, C.SELECTED),
				new PieceVisibilityObject(2, C.SELECTED),
				new PieceVisibilityObject(lastSidePiece, C.SELECTED) }));

		/*
		 * Instruction for the vertical pieces.
		 */
		PieceVisibilityObject[] verticalPieceVisibility = new PieceVisibilityObject[numVertical - 2];
		for (int i = 0; i < numVertical - 2; i++) {
			verticalPieceVisibility[i] = new PieceVisibilityObject(i + 3,
					C.SELECTED);
		}
		setNextInstruction(new PartialInstruction(
				context.getString(
						R.string.connect_the_vertical_pieces_of_the_studwall_using_FASTENERS,
						fastener), verticalPieceVisibility));

		/*
		 * Instructions for the toggles
		 */
		PieceVisibilityObject[] toggleVisibility = new PieceVisibilityObject[numToggle];
		for (int i = 0; i < numToggle; i++) {
			toggleVisibility[i] = new PieceVisibilityObject(i + numVertical
					+ numHorizontal, C.SELECTED);
		}
		setNextInstruction(new PartialInstruction(context.getString(
				R.string.attach_toggles_using_FASTENERS, fastener),
				toggleVisibility));

	}

	public StudFrag() {
	}
}
