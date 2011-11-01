package com.zgrannan.crewandroid;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

/**
 * This is a state that contains the information for a intButton
 * 
 * @author Zack Grannnan
 * @version 0.93
 * 
 */
public class IntState extends BaseSavedState {

	private int value;
	private boolean hasValue;

	public IntState(Parcel in) {
		super(in);
		boolean[] temp = new boolean[1];
		value = in.readInt();
		in.readBooleanArray(temp);
		hasValue = temp[0];
	}

	public IntState(Parcelable superState, int value, boolean hasValue) {
		super(superState);
		this.value = value;
		this.hasValue = hasValue;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(value);
		dest.writeBooleanArray(new boolean[] { hasValue });
	}

	public static final Parcelable.Creator<IntState> CREATOR = new Creator<IntState>() {

		@Override
		public IntState createFromParcel(Parcel in) {
			return new IntState(in);
		}

		@Override
		public IntState[] newArray(int size) {
			return new IntState[size];
		}
	};

	public boolean getHasValue() {
		return hasValue;
	}
}
