package com.zgrannan.crewandroid;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity shows the set pieces that are saved.
 * 
 * @author Zack Grannan
 * @version 0.92
 * 
 */
public class SavedPieces extends ListActivity {

	/*
	 * Adapter that allows buildable objects to be placed in the list
	 */
	private class BuildableAdapter extends ArrayAdapter<Buildable> {

		// Reference to the setPieces available to the adapter

		// Creates the adapter and associates setPieces
		public BuildableAdapter(Context context, int viewResourceId) {
			super(context, viewResourceId, setpieces);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = convertView; // v is the View for a list item
			if (v == null) {
				// Load the layout for each list entry from saved_items
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.saved_items, null);
			}
			// Get the set piece for this list item
			setpiece = setpieces[position];

			// Render the filename for the set piece
			TextView filenameView = (TextView) v
					.findViewById(R.id.setpiece_list_text);
			filenameView.setText(setpiece.getFilename());

			// Open Button
			Button openButton = (Button) v.findViewById(R.id.open_button);
			openButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getBaseContext(), Builder.class);
					intent.putExtra("setpiece", setpieces[position]);
					startActivity(intent);

					// Don't let them come back here.
					finish();
				}

			});

			// Delete Button
			Button deleteButton = (Button) v.findViewById(R.id.delete_button);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						CrewAndroid.deleteSetPiece(position, getBaseContext());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(getBaseContext(),
							getString(R.string.delete_successful),
							Toast.LENGTH_SHORT).show();
					startActivity(getIntent());
					finish();
				}
			});

			return v;
		}
	}

	private BuildableAdapter adapter;
	private Buildable[] setpieces;
	private Buildable setpiece;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Get a reference to the setPieces array
		setpieces = new Buildable[CrewAndroid.getSetpieces().size()];
		CrewAndroid.getSetpieces().toArray(setpieces);

		/*
		 * If there aren't any set pieces, don't let the user view this
		 * interface.
		 */
		
		if (setpieces.length == 0) {
			Toast.makeText(getBaseContext(),
					getString(R.string.no_set_pieces_error), Toast.LENGTH_LONG)
					.show();
			finish();
		}

		// Create an adapter from this array
		adapter = new BuildableAdapter(this, R.layout.saved_items);

		// Create the UI from the adapter
		setListAdapter(adapter);

	}

}