package com.zgrannan.crewandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class contains all the data saved to the phone storage.
 * 
 * @author Zack Grannan
 * @version 0.9
 * 
 */
public class SavedData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Buildable> setPieces;

	public ArrayList<Buildable> getSetPieces() {
		return setPieces;
	}

	public void setSetPieces(ArrayList<Buildable> setPieces) {
		this.setPieces = setPieces;
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		GetField field = in.readFields();
		setPieces = (ArrayList<Buildable>) field.get("setPieces", null);
	}
}
