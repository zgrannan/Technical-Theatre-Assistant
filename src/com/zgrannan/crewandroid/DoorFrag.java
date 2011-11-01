package com.zgrannan.crewandroid;

import java.util.ArrayList;

import android.content.Context;

import com.zgrannan.crewandroid.Consumables.Fastener;
import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Geometry.Vertex;
import com.zgrannan.crewandroid.Pieces.RectPiece;
import com.zgrannan.crewandroid.Util.CompleteInstruction;
import com.zgrannan.crewandroid.Util.Dimension;
import com.zgrannan.crewandroid.Util.Instruction;

/**
 * This is a fragment for a door frame. Note that doorframes only ever have one
 * fragment.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public class DoorFrag extends Frag {

	/**
	 * This is the amount of space between the bottom of the door and the
	 * ground.
	 */
	public static final double HEIGHT_SPACER = 0.5;

	/**
	 * This is the amount of space between the sides of the door and the door
	 * itself.
	 */
	public static final double WIDTH_SPACER = 0.25;

	/**
	 * A double representing the width of the door frame.
	 */
	private double doorWidthD;

	/**
	 * A double representing the height of the door frame.
	 */
	private double doorHeightD;

	@Override
	public Dimension minWidth() {
		return new Dimension(4 * super.woodstick.getWidth().toDouble()
				+ doorWidthD + WIDTH_SPACER);
	}

	@Override
	public Dimension maxWidth() {
		return new Dimension(96);
	}

	@Override
	public Dimension minLength() {
		return new Dimension(2 * super.woodstick.getWidth().toDouble()
				+ doorHeightD + HEIGHT_SPACER);
	}

	@Override
	public Dimension maxLength() {
		return new Dimension(96);
	}

	@Override
	protected void setInstructions(Context context) {
		instructions = new ArrayList<Instruction>();
		Fastener frameFastener = woodstick.getFastener(woodstick);
		CompleteInstruction one = new CompleteInstruction(
				context.getString(
						R.string.attach_topmost_piece_to_the_side_pieces_using_frameFasteners,
						frameFastener), new int[] { C.SELECTED, C.SELECTED,
						C.SELECTED });
		CompleteInstruction two = new CompleteInstruction(
				context.getString(
						R.string.attach_bottommost_pieces_to_side_pieces_using_frameFasteners,
						frameFastener), new int[] { C.VISIBLE, C.VISIBLE,
						C.VISIBLE, C.INVISIBLE, C.INVISIBLE, C.INVISIBLE,
						C.INVISIBLE, C.SELECTED, C.SELECTED });
		CompleteInstruction three = new CompleteInstruction(
				context.getString(
						R.string.attach_inner_side_pieces_to_bottom_pieces_using_frameFasteners,
						frameFastener), new int[] { C.VISIBLE, C.VISIBLE,
						C.VISIBLE, C.SELECTED, C.SELECTED, C.INVISIBLE,
						C.INVISIBLE, C.VISIBLE, C.VISIBLE });

		CompleteInstruction four = new CompleteInstruction(
				context.getString(
						R.string.attach_inner_top_piece_to_outer_and_inner_side_pieces_using_frameFasteners,
						frameFastener), new int[] { C.VISIBLE, C.VISIBLE,
						C.VISIBLE, C.VISIBLE, C.VISIBLE, C.SELECTED,
						C.INVISIBLE, C.VISIBLE, C.VISIBLE });

		CompleteInstruction five = new CompleteInstruction(
				context.getString(R.string.attach_outer_and_inner_side_pieces_using_toggles),
				new int[] { C.VISIBLE, C.VISIBLE, C.VISIBLE, C.VISIBLE,
						C.VISIBLE, C.VISIBLE, C.INVISIBLE, C.VISIBLE,
						C.VISIBLE, C.SELECTED, C.SELECTED });

		CompleteInstruction six = new CompleteInstruction(
				context.getString(R.string.attach_inner_top_piece_to_outer_top_piece_using_a_toggle),
				new int[] { C.VISIBLE, C.VISIBLE, C.VISIBLE, C.VISIBLE,
						C.VISIBLE, C.VISIBLE, C.SELECTED, C.VISIBLE, C.VISIBLE,
						C.VISIBLE, C.VISIBLE });

		CompleteInstruction seven = null;
		if (sheet != Consumables.noSheet) {
			if (width.toDouble() <= 48) {
				seven = new CompleteInstruction(
						context.getString(R.string.attach_lid_to_door_frame_and_route_out_the_door),
						new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER,
								C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.UNDER,
								C.UNDER, C.UNDER, C.SELECTED });
			} else {
				seven = new CompleteInstruction(
						context.getString(R.string.attach_lid_to_door_frame_and_route_out_the_door),
						new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER,
								C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.UNDER,
								C.UNDER, C.UNDER, C.SELECTED, C.SELECTED });
			}
		}

		setNextInstruction(one);
		setNextInstruction(two);
		setNextInstruction(three);
		setNextInstruction(four);
		setNextInstruction(five);
		setNextInstruction(six);
		if (sheet != Consumables.noSheet && !(width.toDouble() > 96)
				&& !(length.toDouble() > 96)) {
			setNextInstruction(seven);
		}

	}

	@Override
	public BuildResult make(Context context) {

		// Start making pieces
		if (width.toDouble() <= 48) {
			pieces = new RectPiece[12];
		} else {
			pieces = new RectPiece[13];
		}

		pieces[0] = new RectPiece(Geometry.origin, widthD, woodstick,
				C.HORIZONTAL, C.ABOVE);

		/*
		 * Top pieces
		 */

		// Pieces 1 and 2 are the outer side pieces of the doorframe
		pieces[1] = new RectPiece(new Vertex(widthD - woodThicknessD,
				woodThicknessD), lengthD - 2 * woodThicknessD, woodstick,
				C.VERTICAL);
		pieces[2] = new RectPiece(new Vertex(0, woodThicknessD), lengthD - 2
				* woodThicknessD, woodstick, C.VERTICAL, C.LEFT);

		// Pieces 3 and 4 are the inner side pieces of the doorframe
		pieces[3] = new RectPiece(new Vertex(widthD
				- (widthD - doorWidthD - WIDTH_SPACER) / 2, lengthD
				- doorHeightD - HEIGHT_SPACER), doorHeightD - woodThicknessD
				+ HEIGHT_SPACER, woodstick, C.VERTICAL, C.LEFT);
		pieces[4] = new RectPiece((RectPiece) pieces[3]);
		pieces[4].move(new Dimension(0 - doorWidthD - WIDTH_SPACER
				- woodThicknessD), new Dimension(0));
		((RectPiece) pieces[4]).setDimLinePos(C.RIGHT);

		// Piece 5 is the lower horizontal top pieces
		pieces[5] = new RectPiece(new Vertex(woodThicknessD, lengthD
				- doorHeightD - HEIGHT_SPACER - woodThicknessD), widthD - 2
				* woodThicknessD, woodstick, C.HORIZONTAL);

		// Piece 6 connects the bottom horizontal pieces to the top one
		pieces[6] = new RectPiece(new Vertex(widthD / 2 - woodThicknessD / 2,
				woodThicknessD), lengthD - doorHeightD - HEIGHT_SPACER - 2
				* woodThicknessD, woodstick, C.VERTICAL);

		// Piece 7 and 8 are the pieces touching the floor
		pieces[7] = new RectPiece(new Vertex(0, lengthD - woodThicknessD),
				(widthD - doorWidthD - WIDTH_SPACER) / 2, woodstick,
				C.HORIZONTAL);
		pieces[8] = new RectPiece((RectPiece) pieces[7]);
		pieces[8].move(new Dimension(((RectPiece) pieces[7]).e.getLength()
				+ doorWidthD + WIDTH_SPACER), new Dimension(0));

		// Piece 9 and 10 are the midpieces for the sides of the doorframe
		pieces[9] = new RectPiece(new Vertex(woodThicknessD,
				((RectPiece) pieces[4]).f.getMidpoint().y.toDouble()),
				((RectPiece) pieces[8]).e.getLength() - 2 * woodThicknessD,
				woodstick, C.HORIZONTAL);
		pieces[10] = new RectPiece((RectPiece) pieces[9]);
		pieces[10].move(new Dimension(((RectPiece) pieces[9]).e.getLength()
				+ doorWidthD + WIDTH_SPACER + 2 * woodThicknessD),
				new Dimension(0));

		// Piece 11 is the pieces for the doorframe lid.
		if (width.toDouble() > 48) {
			pieces[11] = new RectPiece(Geometry.origin, new Dimension(48),
					length, sheet, C.INVISIBLE);
			pieces[12] = new RectPiece(Geometry.origin, new Dimension(
					width.toDouble() - 48), length, sheet, C.INVISIBLE);
			pieces[12].move(new Dimension(48), new Dimension(0));
		}else{
			pieces[11] = new RectPiece(Geometry.origin, new Dimension(width),
					length, sheet, C.INVISIBLE);
		}
		setInstructions(context);
		return new BuildResult(context, true);
	}

	/**
	 * Loads {@link doorWidth} and {@link doorHeight} into this doorFrag, and
	 * then calls the generic {@link Frag} function to load the rest of the
	 * data.
	 * 
	 * @param length
	 * @param width
	 * @param doorHeight
	 * @param doorWidth
	 * @param woodstick
	 * @param sheet
	 * 
	 * @return
	 */
	public BuildResult make(Context context, Dimension length, Dimension width,
			Dimension doorHeight, Dimension doorWidth, WoodStick woodstick,
			Sheet sheet) {
		doorWidthD = doorWidth.toDouble();
		doorHeightD = doorHeight.toDouble();
		return super.make(context, length, width, woodstick, sheet);
	}
}
