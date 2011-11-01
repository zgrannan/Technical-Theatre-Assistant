package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is the class for a button that contains a double that is editable by the
 * user.
 * 
 * @author Zack Grannan
 * @version 0.93
 * 
 */
public class DoubleButton extends ValueButton {

	/**
	 * The double stored by this button.
	 */
	private double value;

	/**
	 * If true, then this button has a value.
	 */
	private boolean hasValue = false;

	@Override
	public boolean hasValue() {
		return hasValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		hasValue = true;
		this.value = value;
	}

	/**
	 * The number of decimal places after the dot.
	 */
	private int roundTo;

	public DoubleButton(Context context) {
		super(context);
	}

	/**
	 * Creates a DoubleButton from an XML specification.
	 * 
	 * @param context
	 * @param attrs
	 *            The set of attributes for the button.
	 */
	public DoubleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ValueButton);
		roundTo = a.getInteger(R.styleable.ValueButton_roundTo, 2);
	}

	/**
	 * Generates a click listener that will create a dialog when the button is
	 * clicked.
	 * 
	 * @param activity
	 *            The activity that will receive the result of the dialog.
	 */

	@Override
	public void setOnClickListener(final Activity activity) {
		final Intent intent = new Intent(activity.getBaseContext(),
				NumberEditDialog.class);
		intent.putExtra("title", prompt);
		intent.putExtra("prompt", prompt);
		intent.putExtra("allowNegative", allowNegative);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				activity.startActivityForResult(intent, requestCode);
			};
		});
	}

	/**
	 * Updates the text of the button to ensure that it is valid every time it
	 * is drawn.
	 */
	@Override
	public void onDraw(Canvas canvas) {
		if (hasValue) {
			setText(prefix + ": "
					+ String.format("%1$." + roundTo + "f", (float) value)
					+ " " + post);
		} else {
			setText(prompt);
		}
		super.onDraw(canvas);
	}

	/**
	 * Saves the double associated with this button when it is flattened into a
	 * state.
	 */
	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return new DoubleState(superState, value, hasValue);
	}

	/**
	 * Properly restores the double when it is unflattened.
	 */
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		DoubleState ss = (DoubleState) state;

		super.onRestoreInstanceState(ss.getSuperState());
		value = ss.getValue();
		hasValue = ss.getHasValue();
	}

}
