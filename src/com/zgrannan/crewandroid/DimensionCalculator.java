package com.zgrannan.crewandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zgrannan.crewandroid.Util.Dimension;
import com.zgrannan.crewandroid.Util.DivideByZeroException;

/**
 * This is the activity for the dimension calculator.
 * <p>
 * This gets rather confusing, as this activity spawns another activity, and
 * both of these activities extend the same activity dim is the name of the
 * dimension used for DimensionEdit, so when the field on the homepage is
 * modified, dim is modified When a dimension needs to be added or subtracted,
 * thisDimension gets the value of dim. dim is modified by the spawned activity
 * When this activity wants to handle the result of and add or subtract
 * operation, it sums the value of dim and thisDimension, and sets the UI to
 * have the value of thisDimension
 * 
 * @author Zack Grannan
 * @version 0.9
 */
public class DimensionCalculator extends DimensionEdit {

	Dimension thisDimension;
	Button addButton, subtractButton, multiplyButton, divideButton;
	int requestCode; // For adding, subtracting, multiplying, dividing, etc
	Intent calculateIntent; // Intent for arithmetic dialogs

	/**
	 * This is called when the value for an arithmetic operation is returned by
	 * the user.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Dimension dim;
		if (resultCode == C.FAIL) // If the user canceled, leave
			return;
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == C.ADD || requestCode == C.SUBTRACT) {

			/*
			 * If the user was trying to add or subtract, we're working with
			 * dimensions
			 */
			dim = new Dimension(
					(Dimension) data.getSerializableExtra("dimension"));
		} else {
			dim = new Dimension(0); // Otherwise
		}
		switch (requestCode) {
		// Perform the operation
		case C.ADD:
			thisDimension.add(dim);
			break;
		case C.SUBTRACT:
			thisDimension.subtract(dim);
			break;
		case C.MULTIPLY:
			thisDimension.multiply(data.getDoubleExtra("number", 1));
			break;
		case C.DIVIDE:
			try {
				thisDimension.divide(data.getDoubleExtra("number", 1));
			} catch (DivideByZeroException e) {
				Toast.makeText(getBaseContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
			break;
		}

		/*
		 * Copy the dimension from the calculation back into this interface (or
		 * rather, the one we are extending)
		 */
		super.dim = new Dimension(thisDimension);
		super.updateUI(thisDimension, true);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		/*
		 * API stuff Load correct layout for orientation
		 */
		WindowManager winMan = (WindowManager) getBaseContext()
				.getSystemService(Context.WINDOW_SERVICE);
		switch (winMan.getDefaultDisplay().getOrientation()) {
		case 0: { // Portrait
			setContentView(R.layout.dimension_calculator_portrait);
			break;
		}
		case 1: { // Landscape
			setContentView(R.layout.dimension_calculator_landscape);
			break;
		}
		}
		super.onCreate(savedInstanceState);
		context = getApplicationContext();

		// UI stuff
		addButton = (Button) findViewById(R.id.add_button);
		subtractButton = (Button) findViewById(R.id.subtract_button);
		multiplyButton = (Button) findViewById(R.id.multiply_button);
		divideButton = (Button) findViewById(R.id.divide_button);

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				thisDimension = new Dimension(dim);
				calculateIntent = new Intent(context, DimensionEditDialog.class);
				calculateIntent.putExtra("prompt",
						getString(R.string.enter_dimension_to_add));
				calculateIntent.putExtra("title", getString(R.string.add));
				requestCode = C.ADD;
				startActivityForResult(calculateIntent, requestCode);
			}

		});
		subtractButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				thisDimension = new Dimension(dim);
				calculateIntent = new Intent(context, DimensionEditDialog.class);
				calculateIntent.putExtra("prompt",
						getString(R.string.enter_dimension_to_subtract));
				calculateIntent.putExtra("title", getString(R.string.subtract));
				requestCode = C.SUBTRACT;
				startActivityForResult(calculateIntent, requestCode);
			}

		});
		multiplyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				thisDimension = new Dimension(dim);
				calculateIntent = new Intent(context, NumberEditDialog.class);
				calculateIntent.putExtra("prompt",
						getString(R.string.enter_number_to_multiply));
				calculateIntent.putExtra("title", getString(R.string.multiply));
				requestCode = C.MULTIPLY;
				startActivityForResult(calculateIntent, requestCode);
			}

		});

		divideButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				thisDimension = new Dimension(dim);
				calculateIntent = new Intent(context, NumberEditDialog.class);
				calculateIntent.putExtra("prompt",
						getString(R.string.enter_number_to_divide));
				calculateIntent.putExtra("title", getString(R.string.divide));
				requestCode = C.DIVIDE;
				startActivityForResult(calculateIntent, requestCode);
			}

		});

		/*
		 * If we're coming from a saved state, ensure thisDimension is
		 * up-to-date
		 */
		if (savedInstanceState != null) {
			thisDimension = (Dimension) savedInstanceState
					.getSerializable("thisDimension");
		}

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		state.putSerializable("thisDimension", thisDimension);
		super.onSaveInstanceState(state);
	}
}
