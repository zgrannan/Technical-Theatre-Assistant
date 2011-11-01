package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zgrannan.crewandroid.Pieces.RightTriPiece;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This is the activity for the trig calculator
 * 
 * @version 0.93
 * @author Zack Grannan
 * 
 */
public class TrigCalculator extends Activity {

	CustomDrawableView diagram;

	DimensionButton heightButton, widthButton;

	DoubleButton primaryAngleButton, secondaryAngleButton;

	Dimension height, width;

	Button startOverButton;

	ValueButton[] valueButtons;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == C.PASS) {
			Dimension dim;
			dim = (Dimension) data.getSerializableExtra("dimension");
			double value = data.getDoubleExtra("number", 0);
			if (requestCode == getResources().getInteger(R.integer.width)) {
				if (dim == null || dim.equals(0))
					return;
				width.setValue(dim);
			}
			if (requestCode == getResources().getInteger(R.integer.height)) {
				if (dim == null || dim.equals(0))
					return;
				height.setValue(dim);
			}
			if (requestCode == getResources()
					.getInteger(R.integer.primaryAngle)) {
				if (value <= 0 || value > 90) {
					Toast.makeText(getBaseContext(),
							getString(R.string.invalid_angle_error),
							Toast.LENGTH_LONG).show();
					return;
				}
				primaryAngleButton.setValue(value);
			}
			if (requestCode == getResources().getInteger(
					R.integer.secondaryAngle)) {
				if (value <= 0 || value > 90) {
					Toast.makeText(getBaseContext(),
							getString(R.string.invalid_angle_error),
							Toast.LENGTH_LONG).show();
					return;
				}
				secondaryAngleButton.setValue(value);
			}
			updateUI();
		}
	}

	private void updateUI() {
		/*
		 * First, check if the angles can be updated.
		 */
		if (primaryAngleButton.hasValue()) {
			secondaryAngleButton.setValue(90 - primaryAngleButton.getValue());
		}
		if (secondaryAngleButton.hasValue()) {
			primaryAngleButton.setValue(90 - secondaryAngleButton.getValue());
		}
		if (height.hasContent() && width.hasContent()) {
			primaryAngleButton.setValue(Geometry.arctan(height.toDouble()
					/ width.toDouble()));
			secondaryAngleButton.setValue(90 - primaryAngleButton.getValue());
		}

		/*
		 * Check if the lengths of the triangle can be updated
		 */
		if (width.hasContent() && !height.hasContent()
				&& primaryAngleButton.hasValue()) {
			height.setDimension(width.toDouble()
					* Geometry.tan(primaryAngleButton.getValue()));
		}
		if (!width.hasContent() && height.hasContent()
				&& primaryAngleButton.hasValue()) {
			width.setDimension(height.toDouble()
					* Geometry.cot(primaryAngleButton.getValue()));
		}

		/*
		 * See if the triangle should be updated.
		 */
		if (primaryAngleButton.hasValue() && width.hasContent()) {
			RightTriPiece triangle = new RightTriPiece(height.positive(),
					width.positive());
			triangle.showAngles();
			triangle.opposite.showDimLine();
			triangle.adjacent.showDimLine();
			triangle.hypotenuse.showDimLine();
			diagram.setDrawable(triangle);
		} else if (primaryAngleButton.hasValue()) {
			RightTriPiece triangle = new RightTriPiece(new Dimension(32),
					new Dimension(32 * Geometry.cot(primaryAngleButton
							.getValue())));
			triangle.showAngles();
			diagram.setDrawable(triangle);
		}

		/*
		 * Make sure the buttons that were clickable are now not.
		 */

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * Load the UI
		 */
		WindowManager winMan = (WindowManager) getBaseContext()
				.getSystemService(Context.WINDOW_SERVICE);
		switch (winMan.getDefaultDisplay().getOrientation()) {
		case 0: { // Portrait
			setContentView(R.layout.trig_calculator_portrait);
			break;
		}
		case 1: { // Landscape
			setContentView(R.layout.trig_calculator_landscape);
			break;
		}
		}

		heightButton = (DimensionButton) findViewById(R.id.height_button);
		widthButton = (DimensionButton) findViewById(R.id.width_button);
		primaryAngleButton = (DoubleButton) findViewById(R.id.primary_angle_button);
		secondaryAngleButton = (DoubleButton) findViewById(R.id.secondary_angle_button);

		startOverButton = (Button) findViewById(R.id.start_over_button);

		startOverButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(),
						TrigCalculator.class);
				startActivity(intent);
				finish();
			}

		});
		diagram = (CustomDrawableView) findViewById(R.id.diagram);

		valueButtons = new ValueButton[] { widthButton, heightButton,
				primaryAngleButton, secondaryAngleButton };
		for (ValueButton button : valueButtons) {
			button.setOnClickListener(this);
		}

		height = heightButton.getDimension();
		width = widthButton.getDimension();

		diagram.setDrawable(new RightTriPiece(new Dimension(32), new Dimension(
				32)));
	}

	@Override
	public void onRestoreInstanceState(Bundle state) {
		/*
		 * Re-associate dimensions when state is restored.
		 */
		super.onRestoreInstanceState(state);
		height = heightButton.getDimension();
		width = widthButton.getDimension();
		updateUI();
	}
}
