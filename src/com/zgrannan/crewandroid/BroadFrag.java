package com.zgrannan.crewandroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

import com.zgrannan.crewandroid.Util.CompleteInstruction;
import com.zgrannan.crewandroid.Util.Dimension;
import com.zgrannan.crewandroid.Util.Instruction;

/**
 * This class defines a fragment for a broadway. Broadway fragments have their own
 * setInstructions() method because they use corner braces and keystones rather than screws
 * 
 * @author Zack Grannan
 * @version 0.96
 *
 */
public class BroadFrag extends PlatTypeFrag {

	@Override
	protected int getCrossbeams() {
		return (int) (length.toDouble() / 40);
	}
	/**
	 * Creates the instructions for this broadway, using toggles and corner
	 * braces.
	 */
	@Override
	protected void setInstructions(Context context) {
		instructions = new ArrayList<Instruction>();
		int[] instr1, instr2, instr3;
		if (getCrossbeams() == 2) {
			instr1 = new int[] { C.SELECTED, C.SELECTED, C.SELECTED, C.SELECTED, C.INVISIBLE, C.INVISIBLE,
					C.INVISIBLE };
			instr2 = new int[] { C.VISIBLE, C.VISIBLE, C.VISIBLE, C.VISIBLE, C.SELECTED, C.SELECTED, C.INVISIBLE };
			instr3 = new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.SELECTED };

			setNextInstruction(new CompleteInstruction(context.getString(R.string.attach_frame_using_corner_braces), instr1));
			setNextInstruction(new CompleteInstruction(String.format(context.getString(R.string.measure_from_end_using_keystones), new Dimension(length.toDouble() / 3)),instr2));
			setNextInstruction(new CompleteInstruction(String.format(context.getString(R.string.attach_lid),sheet.getFastener(woodstick)), instr3));
			
		}

		if (getCrossbeams() == 1) {
			instr1 = new int[] { C.SELECTED, C.SELECTED, C.SELECTED, C.SELECTED, C.INVISIBLE, C.INVISIBLE };
			instr2 = new int[] { C.VISIBLE, C.VISIBLE, C.VISIBLE, C.VISIBLE, C.SELECTED, C.INVISIBLE };
			instr3 = new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.SELECTED };

			setNextInstruction(new CompleteInstruction(context.getString(R.string.attach_frame_using_corner_braces), instr1));
			setNextInstruction(new CompleteInstruction(String.format(context.getString(R.string.measure_from_end_using_keystones), new Dimension(length.toDouble() / 2)),instr2));
			setNextInstruction(new CompleteInstruction(String.format(context.getString(R.string.attach_lid),sheet.getFastener(woodstick)), instr3));
		}

		if (getCrossbeams() == 0) {
			instr1 = new int[] { C.SELECTED, C.SELECTED, C.SELECTED, C.SELECTED, C.INVISIBLE };
			instr2 = new int[] { C.UNDER, C.UNDER, C.UNDER, C.UNDER, C.SELECTED };
			setNextInstruction(new CompleteInstruction(context.getString(R.string.attach_frame_using_corner_braces), instr1));
			setNextInstruction(new CompleteInstruction(String.format(context.getString(R.string.attach_lid),sheet.getFastener(woodstick)), instr2));

		}

	}
}
