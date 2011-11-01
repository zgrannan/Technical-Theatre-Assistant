package com.zgrannan.crewandroid;

import java.util.ArrayList;

import android.content.Context;

import com.zgrannan.crewandroid.Geometry.DimLine;
import com.zgrannan.crewandroid.Geometry.Vertex;
import com.zgrannan.crewandroid.Pieces.RectPiece;
import com.zgrannan.crewandroid.Util.CompleteInstruction;
import com.zgrannan.crewandroid.Util.Dimension;
import com.zgrannan.crewandroid.Util.Instruction;

/**
 * A fragment used for making {@link PlatformType}s.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public abstract class PlatTypeFrag extends Frag {

	@Override
	public Dimension minWidth() {
		return new Dimension(2 * super.woodstick.getWidth().toDouble());
	}

	@Override
	public Dimension maxWidth() {
		return new Dimension(48);
	}

	@Override
	public Dimension minLength() {
		return new Dimension(2 * super.woodstick.getWidth().toDouble());
	}

	@Override
	public Dimension maxLength() {
		return new Dimension(96);
	}

	/**
	 * Makes this PlatTypeFrag, {@link DimLine}s are not shown.
	 * 
	 */
	@Override
	public BuildResult make(Context context) {
		return make(context, false);
	}

	/**
	 * Makes this PlatTypeFrag.
	 * 
	 * @param showDimLines
	 *            If true, {@link DimLine}s will be shown. This is the case when
	 *            the {@link PlatformType} only contains one fragment.
	 * @return
	 */
	public BuildResult make(Context context, boolean showDimLines) {
		int crossbeams = getCrossbeams();
		pieces = new RectPiece[5 + crossbeams];

		pieces[0] = new RectPiece(Geometry.origin, widthD, woodstick,
				C.HORIZONTAL, C.ABOVE);

		/*
		 * Top pieces
		 */

		pieces[1] = new RectPiece(new Vertex(widthD - woodThicknessD,
				woodThicknessD), lengthD - 2 * woodThicknessD, woodstick,
				C.VERTICAL); // Right pieces

		pieces[2] = new RectPiece(new Vertex(0, lengthD - woodThicknessD),
				widthD, woodstick, C.HORIZONTAL);

		/*
		 * Bottom pieces
		 */

		pieces[3] = new RectPiece(new Vertex(0, woodThicknessD), lengthD - 2
				* woodThicknessD, woodstick, C.VERTICAL, C.LEFT); // Left pieces

		// Add dimlines to bottom and right pieces
		if (showDimLines) {
			((RectPiece) pieces[2]).showDimLine();
			((RectPiece) pieces[1]).showDimLine();
		}

		// Create crossbeams
		if (crossbeams == 0) {
			// No crossbeams, draw the sheet
			pieces[4] = new RectPiece(new Vertex(Geometry.origin), width,
					length, sheet, C.INVISIBLE);
		}
		if (crossbeams == 1) {
			// One crossbeam
			pieces[4] = new RectPiece(new Vertex(woodThicknessD, lengthD / 2
					- woodThicknessD / 2), widthD - 2 * woodThicknessD,
					woodstick, C.HORIZONTAL);
			if (showDimLines) {
				((RectPiece) pieces[4]).showDimLine();
			}

			// Draw the sheet
			pieces[5] = new RectPiece(new Vertex(Geometry.origin), width,
					length, sheet, C.INVISIBLE);
		}
		if (crossbeams == 2) {
			// Two crossbeams
			pieces[4] = new RectPiece(new Vertex(woodThicknessD, lengthD / 3
					- woodThicknessD / 2), widthD - 2 * woodThicknessD,
					woodstick, C.HORIZONTAL); // Top
			if (showDimLines) {
				((RectPiece) pieces[4]).showDimLine();
			}

			pieces[5] = new RectPiece(new Vertex(woodThicknessD, 2 * lengthD
					/ 3 - woodThicknessD / 2), widthD - 2 * woodThicknessD,
					woodstick, C.HORIZONTAL); // Bottom

			// Draw the sheet
			pieces[6] = new RectPiece(new Vertex(Geometry.origin), width,
					length, sheet, C.INVISIBLE);
		}
		setInstructions(context);
		return new BuildResult(context, true);
	}

	/**
	 * 
	 * @return The necessary amount of crossbeams required to build this
	 *         fragment.
	 */
	protected abstract int getCrossbeams();

	@Override
	protected void setInstructions(Context context) {
		instructions = new ArrayList<Instruction>();
		int[] instr1, instr2, instr3;

		if (getCrossbeams() == 2) {
			instr1 = new int[] { C.SELECTED, C.SELECTED, C.SELECTED,
					C.SELECTED, C.INVISIBLE, C.INVISIBLE, C.INVISIBLE };
			instr2 = new int[] { C.VISIBLE, C.VISIBLE, C.VISIBLE, C.VISIBLE,
					C.SELECTED, C.SELECTED, C.INVISIBLE };
			instr3 = new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.UNDER,
					C.UNDER, C.SELECTED };

			setNextInstruction(new CompleteInstruction(context.getString(
					R.string.attach_frame_using_fastener,
					woodstick.getFastener(woodstick)), instr1));
			setNextInstruction(new CompleteInstruction(
					context.getString(
							R.string.measure_DIM_from_each_end_and_attach_the_crossbeams_using_SCREW,
							new Dimension(length.toDouble() / 3),
							woodstick.getScrew(woodstick)), instr2));
			if (!sheet.equals(Consumables.noSheet))
				setNextInstruction(new CompleteInstruction(context.getString(
						R.string.attach_lid_using_FASTENER,
						sheet.getFastener(woodstick)), instr3));
		}

		if (getCrossbeams() == 1) {
			instr1 = new int[] { C.SELECTED, C.SELECTED, C.SELECTED,
					C.SELECTED, C.INVISIBLE, C.INVISIBLE };
			instr2 = new int[] { C.VISIBLE, C.VISIBLE, C.VISIBLE, C.VISIBLE,
					C.SELECTED, C.INVISIBLE };
			instr3 = new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.UNDER,
					C.SELECTED };

			setNextInstruction(new CompleteInstruction(context.getString(
					R.string.attach_frame_using_fastener,
					woodstick.getFastener(woodstick)), instr1));
			setNextInstruction(new CompleteInstruction(
					context.getString(
							R.string.measure_DIM_from_each_end_and_attach_the_crossbeam_using_SCREW,
							new Dimension(length.toDouble() / 2),
							woodstick.getScrew(woodstick)), instr2));

			if (!sheet.equals(Consumables.noSheet))
				setNextInstruction(new CompleteInstruction(context.getString(
						R.string.attach_lid_using_FASTENER,
						sheet.getFastener(woodstick)), instr3));
		}

		if (getCrossbeams() == 0) {
			instr1 = new int[] { C.SELECTED, C.SELECTED, C.SELECTED,
					C.SELECTED, C.INVISIBLE };
			instr2 = new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.SELECTED };

			setNextInstruction(new CompleteInstruction(context.getString(
					R.string.attach_frame_using_fastener,
					woodstick.getFastener(woodstick)), instr1));
			if (!sheet.equals(Consumables.noSheet))
				setNextInstruction(new CompleteInstruction(
						"Connect the lid to the frame using "
								+ sheet.getFastener(woodstick) + "s", instr2));

		}

	}

}
