package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zgrannan.crewandroid.Pieces.Piece;
import com.zgrannan.crewandroid.Util.Instruction;

/**
 * Class for viewing instructions.
 * 
 * @author Zack Grannan
 * @version 0.96
 * 
 */
public class ViewInstructions extends Activity {

	CustomDrawableView diagram;
	int currentStep = 0; // The last step of building
	Buildable setpiece;

	private void makeSetPieceInvisible() {
		Piece[] pieces = setpiece.getPieces();
		for (int i = 0; i < pieces.length; i++) {
			if ( pieces[i] != null )
				pieces[i].setVisibility(C.INVISIBLE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Context context = getBaseContext();

		// UI / API stuff
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instruction);

		final Button goBack_button = (Button) findViewById(R.id.goback_button);
		final Button nextButton = (Button) findViewById(R.id.next_button);
		final Button previousButton = (Button) findViewById(R.id.previous_button);
		final TextView instructionText = (TextView) findViewById(R.id.instruction_textview);

		// Load the setpiece from intent
		Intent intent = getIntent();
		setpiece = (Buildable) intent.getSerializableExtra("setpiece");
		setpiece.make(context);
		diagram = (CustomDrawableView) findViewById(R.id.diagram);

		/*
		 * Figure out how many instructions there are Assumes this doesn't
		 * change over the lifetime of this activity
		 */
		final int numSteps = setpiece.numInstructions();

		/*
		 * If restoring from a saved state, go to the correct step Ensure the
		 * needed buttons are visible
		 */
		if (savedInstanceState != null) {
			currentStep = savedInstanceState.getInt("currentStep");
			if (currentStep == 0) {
				previousButton.setVisibility(View.INVISIBLE);
			} else {
				previousButton.setVisibility(View.VISIBLE);
			}
			if (currentStep == numSteps - 1) {
				nextButton.setText(getString(R.string.done));
			}
		}

		// Initially, make every piece invisible.
		makeSetPieceInvisible();

		// Load initial instruction
		Instruction i = setpiece.getInstruction(currentStep);
		instructionText.setText(i.getInstruction());
		setpiece.parseInstruction(i);
		diagram.setDrawable(setpiece);
		if (setpiece.onlyOneFrag()) {
			setTitle(getString(R.string.step) + " " + (currentStep + 1));
		} else {
			setTitle(getString(R.string.step) + " " + (currentStep + 1)
					+ getString(R.string.fragment) + ": "
					+ setpiece.getCurrentFragNum());
		}

		goBack_button.setOnClickListener(new OnClickListener() {
			// Let the user go back
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		nextButton.setOnClickListener(new OnClickListener() {
			// Next step

			@Override
			public void onClick(View arg0) {
				if (currentStep == numSteps - 1) {
					finish();
					return;
				}
				currentStep++;
				Instruction i = setpiece.getInstruction(currentStep);
				instructionText.setText(i.getInstruction());
				setpiece.parseInstruction(i);
				previousButton.setVisibility(View.VISIBLE);
				if (currentStep == numSteps - 1) {
					nextButton.setText(getString(R.string.done));
					setTitle(getString(R.string.final_step));
				} else {
					if (setpiece.onlyOneFrag()) {
						setTitle(getString(R.string.step) + " "
								+ (currentStep + 1));
					} else {
						setTitle(getString(R.string.step) + " "
								+ (currentStep + 1)
								+ getString(R.string.fragment) + ": "
								+ setpiece.getCurrentFragNum());
					}
				}
			}

		});
		previousButton.setOnClickListener(new OnClickListener() {
			// Previous step
			@Override
			public void onClick(View arg0) {
				if (currentStep == 0) {

					/*
					 * Something went wrong TODO V2 throw exception
					 */
					return;
				}
				currentStep--;
				/*
				 * Because we're going back, we need to remake the piece
				 * visibility settings from the beginning, because otherwise
				 * partial (and some complete) steps will not be properly
				 * rendered.
				 */
				setpiece.resetInstructions();
				makeSetPieceInvisible();
				for (int i = 0; i <= currentStep; i++) {
					Instruction temp = setpiece.getInstruction(i);
					setpiece.parseInstruction(temp);
				}
				Instruction instr = setpiece.getInstruction(currentStep);
				instructionText.setText(instr.getInstruction());
				nextButton.setVisibility(View.VISIBLE);
				nextButton.setText(getString(R.string.next_step));
				if (currentStep == 0) {
					previousButton.setVisibility(View.INVISIBLE);
				}
				if (setpiece.onlyOneFrag()) {
					setTitle(getString(R.string.step) + " " + (currentStep + 1));
				} else {
					setTitle(getString(R.string.step) + " " + (currentStep + 1)
							+ getString(R.string.fragment) + ": "
							+ setpiece.getCurrentFragNum());
				}
			}

		});

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putInt("currentStep", currentStep);
	}

}
