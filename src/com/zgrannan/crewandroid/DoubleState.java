package com.zgrannan.crewandroid;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

/**
 * This is a state that contains the information for a doubleButton
 * 
 * @author Zack Grannnan
 * @version 0.93
 * 
 */
public class DoubleState extends BaseSavedState {

	private double value;
	private boolean hasValue;

	public DoubleState(Parcel in) {
		super(in);
		boolean[] temp = new boolean[1];
		value = in.readDouble();
		in.readBooleanArray(temp);
		hasValue = temp[0];
	}

	public DoubleState(Parcelable superState, double value, boolean hasValue) {
		super(superState);
		this.value = value;
		this.hasValue = hasValue;
	}

	public double getValue() {
		return value;
	}

	public boolean getHasValue() {
		return hasValue;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeDouble(value);
		dest.writeBooleanArray(new boolean[] { hasValue });
	}

	public static final Parcelable.Creator<DoubleState> CREATOR = new Creator<DoubleState>() {

		@Override
		public DoubleState createFromParcel(Parcel in) {
			return new DoubleState(in);
		}

		@Override
		public DoubleState[] newArray(int size) {
			return new DoubleState[size];
		}
	};
}
