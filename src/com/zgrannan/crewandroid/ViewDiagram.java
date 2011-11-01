package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ViewDiagram extends Activity {
	CustomDrawableView diagram;

	// This class allows the viewing of a diagram of a set piece

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Context context = getBaseContext();

		// UI stuff
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diagram);
		diagram = (CustomDrawableView) findViewById(R.id.diagram);
		TextView diagramText = (TextView) findViewById(R.id.diagram_textview);

		// Load the set piece from the intent
		Intent intent = getIntent();
		Buildable setpiece = (Buildable) intent
				.getSerializableExtra("setpiece");
		setpiece.make(context);

		// Set the title with the name of the set piece
		diagramText.setText(getString(R.string.diagram) + ": "
				+ setpiece.getName(context));

		// Load the set piece into the drawable view
		diagram.setDrawable(setpiece);

		// Allow the user to go back with this button
		final Button goBack_button = (Button) findViewById(R.id.goback_button);
		goBack_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
