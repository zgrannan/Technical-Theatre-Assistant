package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is the class for a button that contains a int that is editable by the
 * user.
 * 
 * @author Zack Grannan
 * @version 0.93
 * 
 */
public class IntButton extends ValueButton {

	public IntButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IntButton(Context context) {
		super(context);
	}

	/**
	 * The int stored by this button.
	 */
	private int value;

	/**
	 * If true, then this button has a value.
	 */
	private boolean hasValue = false;

	@Override
	public boolean hasValue() {
		return hasValue;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		hasValue = true;
		this.value = value;
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
		intent.putExtra("integer", true);
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
			setText(prefix + ": " + value);
		} else {
			setText(prompt);
		}
		super.onDraw(canvas);
	}

	/**
	 * Saves the int associated with this button when it is flattened into a
	 * state.
	 */
	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return new IntState(superState, value, hasValue);
	}

	/**
	 * Properly restores the int when it is unflattened.
	 */
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		IntState ss = (IntState) state;

		super.onRestoreInstanceState(ss.getSuperState());
		value = ss.getValue();
		hasValue = ss.getHasValue();
	}

}
