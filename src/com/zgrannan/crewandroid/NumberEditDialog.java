package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Generates a prompt where a user can enter a number. Add "title" and "prompt"
 * to any intent.
 * 
 * @author Zack Grannan
 * @version 0.9
 * 
 */
public class NumberEditDialog extends Activity {

	// UI stuff
	TextView promptView;
	EditText numberEdit;
	Button confirmButton, cancelButton;
	Intent intent;
	boolean allowNegative, integer;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// UI stuff
		intent = getIntent();
		if (intent.getStringExtra("title") != null)
			setTitle(intent.getStringExtra("title"));

		/*
		 * The caller of this activity may have specified that the result must
		 * be positive, or must be an integer.
		 */
		allowNegative = intent.getBooleanExtra("allowNegative", true);
		integer = intent.getBooleanExtra("integer", false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.number_dialog);
		promptView = (TextView) findViewById(R.id.prompt_text);
		numberEdit = (EditText) findViewById(R.id.number_edit);
		confirmButton = (Button) findViewById(R.id.confirm_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		String prompt = intent.getStringExtra("prompt");
		if (prompt != null)
			promptView.setText(prompt);
		/**
		 * 
		 */

		OnKeyListener enterKeyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				/*
				 * If the enter key is pressed, submit the content of the dialog
				 * if the contents are valid
				 */
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					if (numberEdit.length() > 0) {
						// Put the result into "number" and send "PASS"
						if (integer) {
							intent.putExtra("number", Integer
									.parseInt(numberEdit.getText().toString()));
						} else {
							intent.putExtra("number", Double
									.parseDouble(numberEdit.getText()
											.toString()));
						}
						setResult(C.PASS, intent);
						finish();
					}
					return true;
				}
				return false;
			}
		};
		numberEdit.addTextChangedListener(Util.getProperFormatTextWatcher(
				integer, allowNegative));
		numberEdit.setOnKeyListener(enterKeyListener);
		confirmButton.setOnClickListener(new OnClickListener() {

			/**
			 * If the confirm button is pressed, and the content of the dialog
			 * is valid ,then submit results
			 * 
			 */
			@Override
			public void onClick(View v) {
				if (numberEdit.length() > 0) {
					// Put the result into "number" and send "PASS"
					if (integer) {
						intent.putExtra("number", Integer.parseInt(numberEdit
								.getText().toString()));
					} else {
						intent.putExtra("number", Double.parseDouble(numberEdit
								.getText().toString()));
					}
					setResult(C.PASS, intent);
					finish();
				}
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			/**
			 * If the cancel button is pressed, let the world know of the
			 * failure.
			 */
			@Override
			public void onClick(View v) {
				setResult(C.FAIL, intent);
				finish();
			}

		});
		numberEdit.requestFocus();

		/*
		 * Automatically set the focus to the number edit field
		 */
	}
}
