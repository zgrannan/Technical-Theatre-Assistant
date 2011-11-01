package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This is the class for the stair calculator interface
 * 
 * @author Zack Grannan
 * @version 0.92
 * 
 */
public class StairCalculator extends Activity {

	private Dimension treadHeight, treadLength, staircaseHeight,
			staircaseLength;

	private DimensionButton treadHeightButton, treadLengthButton,
			staircaseHeightButton, staircaseLengthButton;

	private IntButton numStepsButton;

	private DoubleButton angleButton;

	private ValueButton[] valueButtons;

	private Button startOverButton;

	/**
	 * The user has just finished entering information about some dimension or
	 * number. Or maybe not.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == C.PASS) {
			Dimension dim;
			dim = (Dimension) data.getSerializableExtra("dimension");
			if (requestCode == getResources().getInteger(R.integer.treadHeight)
					|| requestCode == getResources().getInteger(
							R.integer.treadLength)
					|| requestCode == getResources().getInteger(
							R.integer.staircaseHeight)
					|| requestCode == getResources().getInteger(
							R.integer.staircaseLength)) {

				if (dim == null || dim.equals(0))
					return;
			}
			if (requestCode == getResources().getInteger(R.integer.treadHeight)) {
				if (staircaseHeight.hasContent()) {
					if (dim.greaterThan(staircaseHeight)) {
						Toast.makeText(
								getBaseContext(),
								R.string.tread_height_larger_than_staircase_height,
								Toast.LENGTH_LONG).show();
						return;
					}
					if (staircaseHeight.toDouble() % dim.toDouble() - C.EPSILON > 0) {
						Toast.makeText(getBaseContext(),
								R.string.staircase_doesnt_divide_evenly,
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				treadHeight.setValue((Dimension) data
						.getSerializableExtra("dimension"));
			}
			if (requestCode == getResources().getInteger(R.integer.treadLength)) {
				if (staircaseLength.hasContent()) {
					if (dim.greaterThan(staircaseLength)) {
						Toast.makeText(
								getBaseContext(),
								R.string.tread_length_larger_than_staircase_length,
								Toast.LENGTH_LONG).show();
						return;
					}
					if (staircaseLength.toDouble() % dim.toDouble() - C.EPSILON > 0) {
						Toast.makeText(getBaseContext(),
								R.string.staircase_doesnt_divide_evenly,
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				treadLength.setValue((Dimension) data
						.getSerializableExtra("dimension"));
			}
			if (requestCode == getResources().getInteger(
					R.integer.staircaseLength)) {
				if (treadLength.hasContent()) {
					if (dim.lessThan(treadLength)) {
						Toast.makeText(
								getBaseContext(),
								R.string.tread_length_larger_than_staircase_length,
								Toast.LENGTH_LONG).show();
						return;
					}
					if (dim.toDouble() % treadLength.toDouble() - C.EPSILON > 0) {
						Toast.makeText(getBaseContext(),
								R.string.staircase_doesnt_divide_evenly,
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				staircaseLength.setValue((Dimension) data
						.getSerializableExtra("dimension"));
			}
			if (requestCode == getResources().getInteger(
					R.integer.staircaseHeight)) {
				if (treadHeight.hasContent()) {
					if (dim.lessThan(treadHeight)) {
						Toast.makeText(
								getBaseContext(),
								R.string.tread_height_larger_than_staircase_height,
								Toast.LENGTH_LONG).show();
						return;
					}
					if (dim.toDouble() % treadHeight.toDouble() - C.EPSILON > 0) {
						Toast.makeText(getBaseContext(),
								R.string.staircase_doesnt_divide_evenly,
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				staircaseHeight.setValue((Dimension) data
						.getSerializableExtra("dimension"));
			}
			if (requestCode == getResources().getInteger(R.integer.numSteps)) {
				if (data.getIntExtra("number", 0) == 0) {
					Toast.makeText(getBaseContext(), R.string.zero_steps_error,
							Toast.LENGTH_LONG).show();
					return;
				}
				numStepsButton.setValue(data.getIntExtra("number", 0));
			}
			if (requestCode == getResources().getInteger(R.integer.angle)) {
				if (data.getDoubleExtra("number", 0) <= 0
						|| data.getDoubleExtra("number", 0) >= 90) {
					Toast.makeText(getBaseContext(),
							R.string.invalid_angle_error, Toast.LENGTH_LONG)
							.show();
					return;
				}
				angleButton.setValue(data.getDoubleExtra("number", 0));
			}

			/*
			 * See if we can draw any more conclusions from this data.
			 */
			updateDimensions();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// Load the interface
		setContentView(R.layout.stair_calculator);

		// Associate the buttons and checkboxes with appropriate listeners

		treadHeightButton = (DimensionButton) findViewById(R.id.tread_height_button);
		treadLengthButton = (DimensionButton) findViewById(R.id.tread_length_button);
		staircaseHeightButton = (DimensionButton) findViewById(R.id.staircase_height_button);
		staircaseLengthButton = (DimensionButton) findViewById(R.id.staircase_length_button);
		numStepsButton = (IntButton) findViewById(R.id.num_steps_button);
		angleButton = (DoubleButton) findViewById(R.id.angle_button);

		treadHeight = treadHeightButton.getDimension();
		treadLength = treadLengthButton.getDimension();
		staircaseHeight = staircaseHeightButton.getDimension();
		staircaseLength = staircaseLengthButton.getDimension();

		/*
		 * Load the listeners for the buttons.
		 */
		valueButtons = new ValueButton[] { treadHeightButton,
				treadLengthButton, staircaseHeightButton,
				staircaseLengthButton, numStepsButton, angleButton };
		for (ValueButton button : valueButtons) {
			button.setOnClickListener(this);
		}

		startOverButton = (Button) findViewById(R.id.start_over_button);

		startOverButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(),
						StairCalculator.class);
				startActivity(intent);
				finish();
			}

		});
	}

	/**
	 * Updates the dimensions based on calculations with the other ones. If this
	 * forces a change, it re-runs until it no-longer has any effect.
	 */
	private void updateDimensions() {
		boolean changeMade = false;
		// Tread Height
		if (!treadHeight.hasContent()) {
			if (staircaseHeight.hasContent() && numStepsButton.hasValue()) {
				treadHeight.setDimension(staircaseHeight.toDouble()
						/ numStepsButton.getValue());
				changeMade = true;
			} else if (getSlope() != 0 && treadLength.hasContent()) {
				treadHeight.setDimension(treadLength.toDouble() * getSlope());
				changeMade = true;
			}
		}
		// Tread Length
		if (!treadLength.hasContent()) {
			if (!staircaseLength.equals(0) && numStepsButton.getValue() != 0) {
				treadLength.setDimension(staircaseLength.toDouble()
						/ numStepsButton.getValue());
				changeMade = true;
			} else if (getSlope() != 0 && !treadHeight.equals(0)) {
				changeMade = true;
				treadLength.setDimension(treadHeight.toDouble() / getSlope());
			}
		}

		// Staircase Height
		if (staircaseHeight.equals(0)) {
			if (!treadHeight.equals(0) && numStepsButton.getValue() != 0) {
				staircaseHeight.setDimension(treadHeight.toDouble()
						* numStepsButton.getValue());
				changeMade = true;
			} else if (getSlope() != 0 && !staircaseLength.equals(0)) {
				staircaseHeight.setDimension(staircaseLength.toDouble()
						* getSlope());
				changeMade = true;
			}
		}
		// Staircase Length
		if (staircaseLength.equals(0)) {
			if (!treadLength.equals(0) && numStepsButton.getValue() != 0) {
				staircaseLength.setDimension(treadLength.toDouble()
						* numStepsButton.getValue());
				changeMade = true;
			} else if (getSlope() != 0 && !staircaseHeight.equals(0)) {
				staircaseLength.setDimension(staircaseHeight.toDouble()
						/ getSlope());
				changeMade = true;
			}
		}
		// Number of steps
		if (!numStepsButton.hasValue()) {
			if (!staircaseHeight.equals(0) && !treadHeight.equals(0)) {
				if (staircaseHeight.toDouble() % treadHeight.toDouble() < C.EPSILON) {
					numStepsButton
							.setValue((int) (staircaseHeight.toDouble() / treadHeight
									.toDouble()));
					changeMade = true;
				}
			}
			if (!staircaseLength.equals(0) && !treadLength.equals(0)) {
				if (staircaseLength.toDouble() % treadLength.toDouble() < C.EPSILON) {
					numStepsButton
							.setValue((int) (staircaseLength.toDouble() / treadLength
									.toDouble()));
					changeMade = true;
				}
			}
		}

		// Angle
		if (!angleButton.hasValue()) {
			if (getSlope() != 0) {
				angleButton.setValue(Geometry.arctan(getSlope()));
				changeMade = true;
			}
		}
		if (changeMade) {
			updateDimensions();
		}
	}

	/**
	 * Returns the slope of the staircase. Generates the slope based on angle or
	 * dimensions.
	 * 
	 * @return A double representing the slope. Slope = rise / run
	 */
	private double getSlope() {
		if (angleButton.getValue() != 0)
			return Geometry.tan(angleButton.getValue());
		if (!treadHeight.equals(0) && !treadLength.equals(0))
			return treadHeight.toDouble() / treadLength.toDouble();
		if (!staircaseHeight.equals(0) && !staircaseLength.equals(0))
			return staircaseHeight.toDouble() / staircaseLength.toDouble();
		return 0;
	}

	@Override
	public void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		treadHeight = treadHeightButton.getDimension();
		treadLength = treadLengthButton.getDimension();
		staircaseHeight = staircaseHeightButton.getDimension();
		staircaseLength = staircaseLengthButton.getDimension();
	}
}
