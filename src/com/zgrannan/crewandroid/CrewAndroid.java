package com.zgrannan.crewandroid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This is the activity that the user will first view.j
 * 
 * @author Zack Grannan.
 * @version 0.94
 * 
 */
public class CrewAndroid extends ListActivity {

	/**
	 * List that contains all of the user's set pieces This list should be
	 * accessible to other activities that require it through the use of
	 * getSetPieces().
	 */
	private static ArrayList<Buildable> setpieces;

	/**
	 * Used for reading the file containing the saved set pieces.
	 */
	private static FileInputStream fileIn;

	/**
	 * Used for reading the saved data from the file input stream.
	 */
	private static ObjectInputStream objectIn;

	/**
	 * Class containing data saved on the phone. During initialization, data
	 * will be loaded from this class.
	 */
	private static SavedData savedData;

	/**
	 * Used for writing the file containing saved set pieces.
	 */
	private static FileOutputStream fileOut;

	/**
	 * Used for writing the saved data to the file output stream.
	 */
	private static ObjectOutputStream objectOut;

	/**
	 * @param context
	 * @throws FileNotFoundException
	 *             , IOException
	 * 
	 */
	public static void updateFile(Context context)
			throws FileNotFoundException, IOException {
		fileOut = context.openFileOutput(C.FILENAME, Context.MODE_PRIVATE);
		objectOut = new ObjectOutputStream(fileOut);
		objectOut.writeObject(savedData);
		objectOut.close();
		fileOut.close();
	}

	/**
	 * Deletes the set piece with the given filename.
	 * 
	 * @param filename
	 *            The filename of the set piece to be deleted.
	 * @throws FileNotFoundException
	 *             If no set piece with the given filename can be found.
	 */
	public static void deleteSetPiece(String filename, Context context)
			throws NoSuchElementException, IOException {
		// Get the index of the set piece
		int index = CrewAndroid.findIndex(filename);

		if (index == -1) // If the set piece can't be found, throw an exception
			throw new NoSuchElementException();

		deleteSetPiece(index, context);
	}

	/**
	 * If true, the activity is being launched from scratch, and it should load
	 * saved information from the phone storage.
	 */
	private static boolean initialLaunch = true;

	/**
	 * Deletes the set piece at an indicated index in the list
	 * 
	 * @param index
	 *            The location of the list that contains the set piece you wish
	 *            to delete.
	 * @param context
	 *            The context of the command.
	 * @throws IOException
	 */
	public static void deleteSetPiece(int index, Context context)
			throws IOException {
		setpieces.remove(index);
		updateFile(context);
	}

	/**
	 * Returns the index of the setpiece with the given filename.
	 * 
	 * @param filename
	 *            The filename of the set piece.
	 * @return the index of the setpiece, or -1 if no setpiece exists.
	 */
	public static int findIndex(String filename) {
		Buildable temp;
		for (int i = 0; i < setpieces.size(); i++) {
			temp = setpieces.get(i);
			if (temp.getFilename().equals(filename)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns a list of all of the set pieces that are saved.
	 * 
	 * @return a list of all of the set pieces that are saved.
	 */
	public static ArrayList<Buildable> getSetpieces() {
		return setpieces;
	}

	/**
	 * Renames the set piece, or throws an exception if no piece exists with
	 * this filename,
	 * 
	 * @param filename1
	 *            The filename of the set piece that you wish to rename.
	 * @param filename2
	 *            The filename that you wish to rename this set piece to.
	 * @throws FileNotFoundException
	 *             If {@link filename1} does not refer to a piece that exists.
	 */
	public static void renameSetPiece(String filename1, String filename2)
			throws FileNotFoundException {
		// TODO V2 Implement renameSetPiece for
	}

	/**
	 * Saves a set piece.
	 * 
	 * @param setPiece
	 *            The set piece to be saved.
	 * @param context
	 */

	public static void saveSetPiece(Buildable setPiece, Context context)
			throws IOException {

		setpieces.add(setPiece);
		updateFile(context);

	}

	/**
	 * Check to see if a setpiece with a given filename exists.
	 * 
	 * @param filename
	 *            The filename of the set piece.
	 * @return True if the setpiece exists, false if the piece doesn't exist.
	 */
	public static boolean setPieceExists(String filename) {
		return findIndex(filename) >= 0;
	}

	/**
	 * Loads the data from phone storage. Should only be called once.
	 */
	private void loadSavedDataFromFile() throws FileNotFoundException,
			IOException {
		try {
			fileIn = openFileInput(C.FILENAME);

		} catch (FileNotFoundException e) {
			/*
			 * If the file doesn't exist, assume the user just downloaded the
			 * application. Show the dialog and then create the file.
			 */
			showDialog(C.FIRST_START_DIALOG);
			openFileOutput(C.FILENAME, Context.MODE_PRIVATE).close();
			return;
		}

		try {
			objectIn = new ObjectInputStream(fileIn);

			if (objectIn != null) {
				savedData = (SavedData) objectIn.readObject();
			}

		} catch (IOException e) {
			/*
			 * It appears the file is empty. Close the input stream.
			 */
			try {
				fileIn.close();
				if (objectIn != null)
					objectIn.close();
			} catch (IOException e2) {
				/*
				 * If there is an exception at this point, just ignore it.
				 */
				return;
			}
			return;
		} catch (ClassNotFoundException e) {
			Toast.makeText(getBaseContext(),
					getString(R.string.problem_loading_set_pieces_from_file),
					Toast.LENGTH_LONG).show();
		}
		if (savedData != null)
			setpieces = savedData.getSetPieces();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		/*
		 * If you would like to remove the saved data, uncomment the line below
		 * and recompile the application. Don't forget to recomment it and
		 * rebuild after, or the file will be deleted every time the application
		 * starts.
		 */
		// deleteFile(C.FILENAME);

		// UI/API stuff
		super.onCreate(savedInstanceState);
		final String[] ITEMS = getResources().getStringArray(
				R.array.homescreen_array);
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.homescreen_items, ITEMS));
		ListView lv = getListView();

		// Initialize file I/O if this hasn't happened already.
		if (initialLaunch) {
			try {
				loadSavedDataFromFile();
			} catch (FileNotFoundException e) {
				Toast.makeText(getBaseContext(),
						getString(R.string.saved_data_cannot_be_found),
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Toast.makeText(getBaseContext(),
						getString(R.string.io_exception_occurred),
						Toast.LENGTH_LONG).show();
			}
			initialLaunch = false;
		}
		// Initialize savedData, if it wasn't recovered by a file
		if (savedData == null) {
			savedData = new SavedData();
		}
		// Initialize setPieces, if it isn't associated with anything.
		if (setpieces == null) {
			setpieces = new ArrayList<Buildable>();
			savedData.setSetPieces(setpieces);
		}

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// A user has clicked an item!

				switch (position) {
				case C.BUILD: {
					// Opens the builder with no loaded set piece
					startActivity(new Intent(getBaseContext(), Builder.class));
					break;
				}
				case C.DIM_CALCULATOR: {
					// Opens the dimension calculator
					Intent intent = new Intent(getBaseContext(),
							DimensionCalculator.class);
					intent.putExtra("allow_negative", true);
					startActivity(intent);
					break;
				}
				case C.SAVED_PIECES: {
					// Opens the saved pieces for viewing
					Intent intent = new Intent(getBaseContext(),
							SavedPieces.class);
					intent.putExtra("setpieces", setpieces);
					startActivity(intent);
					break;
				}
				case C.VIEW_MATERIAL: {
					Intent intent = new Intent(getBaseContext(),
							ViewMaterials.class);
					startActivity(intent);
					break;
				}

				case C.STAIR_CALCULATOR: {
					Intent intent = new Intent(getBaseContext(),
							StairCalculator.class);
					startActivity(intent);
					break;
				}

				case C.TRIG_CALCULATOR: {
					Intent intent = new Intent(getBaseContext(),
							TrigCalculator.class);
					startActivity(intent);
					break;
				}
				case C.ROPE_CALCULATOR: {
					Intent intent = new Intent(getBaseContext(),
							RopeCalculator.class);
					startActivity(intent);
					break;
				}
				case C.BACKSTAGE_BADGER: {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://fyeahbackstagebadger.tumblr.com")));
					break;

				}
				case C.CONTACT: {
					showDialog(C.CONTACT_DIALOG);
					break;
				}
				case C.QUIT: {
					// Quit
					finish();
					break;
				}
				}
			}
		});

	}

	/**
	 * Used for the first start dialog and the contact dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {

		/**
		 * The dialog that will be returned
		 */
		Dialog dialog;
		switch (id) {
		case C.FIRST_START_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.welcome))
					.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();

								}
							})
					.setMessage(
							getResources().getString(R.string.firststart_text));
			dialog = builder.create();

			/*
			 * Also inform the user about the material information.
			 */
			break;
		}
		case C.CONTACT_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.welcome))
					.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();

								}
							})
					.setMessage(getResources().getString(R.string.contact_text));
			dialog = builder.create();
			break;
		}
		default:
			/**
			 * If an invalid ID is referenced, return null
			 */
			dialog = null;
		}
		return dialog;
	}
}
