package com.zgrannan.crewandroid;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

import com.zgrannan.crewandroid.Util.Dimension;

public class DimensionState extends BaseSavedState {

	private Dimension dimension;

	public DimensionState(Parcel in) {
		super(in);
		dimension = (Dimension) in.readSerializable();
	}

	public DimensionState(Parcelable superState, Dimension dimension) {
		super(superState);
		this.dimension = dimension;
	}

	public Dimension getDimension() {
		return dimension;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeSerializable(dimension);
	}

	public static final Parcelable.Creator<DimensionState> CREATOR = new Creator<DimensionState>() {

		@Override
		public DimensionState createFromParcel(Parcel in) {
			return new DimensionState(in);
		}

		@Override
		public DimensionState[] newArray(int size) {
			return new DimensionState[size];
		}
	};
}
