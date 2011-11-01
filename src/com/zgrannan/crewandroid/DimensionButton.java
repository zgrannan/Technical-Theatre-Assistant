package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This is the class for a button that contains a dimension that is editable by
 * the user.
 * 
 * @author Zack Grannan
 * @version 0.93
 * 
 */
public class DimensionButton extends ValueButton {

	/**
	 * The dimension stored by this button.
	 */
	private Dimension dimension;

	@Override
	public boolean hasValue() {
		return dimension.hasContent();
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	@Override
	public String toString() {
		return "Button containing dimension: " + dimension;
	}

	public DimensionButton(Context context) {
		super(context);
	}

	/**
	 * Creates a DimensionButton from an XML specification.
	 * 
	 * @param context
	 * @param attrs
	 *            The set of attributes for the button.
	 */
	public DimensionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		dimension = new Dimension();
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
				DimensionEditDialog.class);
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
		if (dimension.hasContent()) {
			setText(prefix + ": " + dimension);
		} else {
			setText(prompt);
		}
		super.onDraw(canvas);
	}

	/**
	 * Saves the dimension associated with this button when it is flattened into
	 * a state.
	 */
	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return new DimensionState(superState, dimension);
	}

	/**
	 * Properly restores the dimension when it is unflattened.
	 */
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		DimensionState ss = (DimensionState) state;

		super.onRestoreInstanceState(ss.getSuperState());
		dimension = ss.getDimension();
	}

}
