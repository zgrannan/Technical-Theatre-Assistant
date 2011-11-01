package com.zgrannan.crewandroid;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.zgrannan.crewandroid.Util.Cutlist;

public class ViewCutlist extends ListActivity {
	@Override
	// This class allows the viewing of a cutlist for a piece
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		// Get the cutlist from an intent
		Cutlist cutlist = (Cutlist) intent.getSerializableExtra("cutlist");

		// Populate the UI with the stringArray from the cutlist
		setListAdapter(new ArrayAdapter<String>(this, R.layout.cutlist_items,
				cutlist.getStringArray()));

	}
}
