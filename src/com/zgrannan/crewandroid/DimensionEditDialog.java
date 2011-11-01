package com.zgrannan.crewandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This creates the dialog for entering a dimension. Used in the builder and in
 * dimension calculator.
 * <p>
 * Add "title" to intent to display a title for this dialog.<br>
 * Add "prompt" to intent to display a prompt for this dialog.
 * 
 * @author Zack Grannan
 * @version 0.9
 * 
 */
public class DimensionEditDialog extends DimensionEdit {

	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		intent = getIntent();
		if (intent.getStringExtra("title") != null)
			setTitle(intent.getStringExtra("Dimension"));

		// Load correct layout for orientation
		WindowManager winMan = (WindowManager) getBaseContext()
				.getSystemService(Context.WINDOW_SERVICE);
		switch (winMan.getDefaultDisplay().getOrientation()) {
		case 0: { // Portrait
			setContentView(R.layout.dimension_edit_dialog_portrait);
			break;
		}
		case 1: { // Landscape
			setContentView(R.layout.dimension_edit_dialog_landscape);

			// Also fill the screen
			LayoutParams params = getWindow().getAttributes();
			params.height = LayoutParams.FILL_PARENT;
			params.width = LayoutParams.FILL_PARENT;
			getWindow().setAttributes(
					(android.view.WindowManager.LayoutParams) params);

			break;
		}
		}

		// Set the dialog to fill the screen

		super.onCreate(savedInstanceState);

		Button confirmButton = (Button) findViewById(R.id.confirm_button);
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		TextView promptView = (TextView) findViewById(R.id.prompt_text);

		String prompt = intent.getStringExtra("prompt");
		if (prompt == null)
			prompt = getString(R.string.enter_a_dimension);
		promptView.setText(prompt);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*
				 * If the user pressed OK, add the dimension to returned intent
				 * and Indicate that the prompt executed successfully
				 */
				intent.putExtra("dimension", new Dimension(dim));
				setResult(C.PASS, intent);
				finish();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// If the user canceled, indicate that the prompt failed
				setResult(C.FAIL, intent);
				finish();
			}
		});

	}

}
