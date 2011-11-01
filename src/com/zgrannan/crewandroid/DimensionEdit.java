package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This is the activity for dimension editing. Used in DimensionCalculator and
 * DimensionEdit, but never called for itself
 * 
 * @author Zack Grannan.
 * @version 0.9
 */
public class DimensionEdit extends Activity {

	// UI and API elements
	CheckBox negativeCheckBox;
	Context context;
	EditText feetEdit, inchEdit, manualEdit;
	Button addButton, subtractButton, multiplyButton, divideButton;
	Spinner fractionSpinner;
	Intent intent;

	/**
	 * Allows the user to make the dimension negative.
	 */
	boolean allowNegative;

	/**
	 * Allows the textfields to do stuff if they are being changed.
	 */
	boolean watchText = true;

	/**
	 * The dimension generated from manual edit field.
	 */
	public Dimension manualDim;

	/**
	 * The dimension generated from the wizard field.
	 */
	public Dimension wizardDim;

	/**
	 * The current actual dimension represented.
	 */
	public Dimension dim;

	/**
	 * Checks to see if the dimension calculated from the wizard UI is the same
	 * as that calculated from the manual UI.
	 * 
	 * @return true if wizardDim and manualDim represent the same value,
	 *         otherwise returns false
	 */
	private boolean dimensionsMatch() {
		return wizardDim.equals(manualDim);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		intent = getIntent();

		/*
		 * Check to see if the intent wants negative dimensions allowed, and
		 * save to the variable
		 */
		allowNegative = intent.getBooleanExtra("allow_negative", false);

		// If the user hits enter, then close the keyboard
		OnKeyListener enterKeyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}

				/*
				 * This must return false so that other views can intercept the
				 * key
				 */
				return false;
			}
		};

		super.onCreate(savedInstanceState);
		context = getApplicationContext();

		if (allowNegative) {

			/*
			 * If the user is allowed to enter negative numbers, adjust the UI
			 * accordingly And add the listener
			 */
			negativeCheckBox = (CheckBox) findViewById(R.id.negative_checkbox);
			negativeCheckBox.setVisibility(View.VISIBLE);
			negativeCheckBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean arg1) {
							if (watchText) {
								watchText = false;
								updateDimensions();
								if (!dimensionsMatch()) {
									manualDim = new Dimension(wizardDim);
									manualEdit.setText(new String(""
											+ wizardDim.toDouble()));
									dim = new Dimension(manualDim);
								}
								watchText = true;
							}
						}

					});
		}

		// UI stuff

		feetEdit = (EditText) findViewById(R.id.feet_edit);
		inchEdit = (EditText) findViewById(R.id.inches_edit);
		manualEdit = (EditText) findViewById(R.id.manual_inch_edit);
		fractionSpinner = (Spinner) findViewById(R.id.fraction_spinner);

		feetEdit.setOnKeyListener(enterKeyListener);
		inchEdit.setOnKeyListener(enterKeyListener);
		manualEdit.setOnKeyListener(enterKeyListener);

		final ArrayAdapter<CharSequence> fractionAdapter = ArrayAdapter
				.createFromResource(this, R.array.fraction_array,
						android.R.layout.simple_spinner_item);
		fractionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fractionSpinner.setAdapter(fractionAdapter);

		// Initiate the dimensions
		dim = new Dimension(0);
		manualDim = new Dimension(0);
		wizardDim = new Dimension(0);

		// Ensure the texts are synchronized
		TextWatcher dimensionWizardTextEditListener = new TextWatcher() {

			/**
			 * Ensures that the user put in a valid input.
			 */
			@Override
			public void afterTextChanged(Editable s) {
				String oldText = s.toString();
				String newText = Util.formatAsInt(oldText);
				if (!newText.equals(oldText)) {
					s.clear();
					s.append(newText);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// If the dimension wizard text field is edited,
				if (watchText) { // Provided that it's allowed to do stuff
					watchText = false;

					/*
					 * Prevent other UI elements from doing stuff on their own
					 */
					updateDimensions(); // Update dimension objects from UI
					if (!dimensionsMatch()) {
						/*
						 * If the dimensions no longer match, update the UI
						 * accordingly.
						 */
						updateUI(wizardDim, false);
					}
					watchText = true; // Give privileges back to UI
				}
			}

		};

		TextWatcher manualDimensionTextEditListener = new TextWatcher() {

			/**
			 * Ensures that the user put in a valid input.
			 */
			@Override
			public void afterTextChanged(Editable s) {
				String oldText = s.toString();
				String newText = Util.formatAsDouble(oldText);
				if (!allowNegative)
					newText = Util.abs(newText);
				if (!newText.equals(oldText)) {
					s.clear();
					s.append(newText);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// If the manual wizard text field is edited,
				if (watchText) { // Provided that it's allowed to do stuff
					watchText = false;

					/*
					 * Prevent other UI elements from doing stuff on their own
					 */
					updateDimensions(); // Update dimension objects from UI
					if (!dimensionsMatch()) {
						/*
						 * If the dimensions no longer match, update the UI
						 * accordingly.
						 */
						updateUI(manualDim, false);
					}
					watchText = true; // Give UI it's action privileges back
				}
			}
		};

		/**
		 * This updates the wizard dimensions so that there is never more than
		 * 12 inches in the inch field. Note that this doesn't change the value
		 * of the dimension, so there is no need to edit any dimension objects
		 */
		OnFocusChangeListener inchFocusListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				/*
				 * Incorporate functionality from removeZeroListener
				 */
				if (((EditText) v).getText().toString().equals("0") && hasFocus) {
					((EditText) v).setText("");
					return;
				}
				if (((EditText) v).getText().toString().equals("") && !hasFocus) {
					((EditText) v).setText("0");
					return;
				}

				// If the inch textview is selected or unselected
				watchText = false;

				/*
				 * Prevent other UI elements from doing things
				 */
				if (inchEdit.length() == 0)

					/*
					 * Provided that there is stuff in the inch textview
					 */
					return;
				int inches = 0;
				try {
					inches = Integer.parseInt(inchEdit.getText().toString());
				} catch (NumberFormatException e) {
					inchEdit.setText("0");
					Toast.makeText(getBaseContext(),
							getString(R.string.that_number_is_invalid),
							Toast.LENGTH_LONG).show();
				}

				/*
				 * Get the amount of inches
				 */
				if (inches >= 12) {

					/*
					 * If inches greater than 12 Update the UI so that it looks
					 * like it should
					 */
					int feet = 0;
					if (feetEdit.length() != 0) {
						try {
							feet = Integer.parseInt(feetEdit.getText()
									.toString());
						} catch (NumberFormatException e) {
							feetEdit.setText("0");
							Toast.makeText(getBaseContext(),
									getString(R.string.that_number_is_invalid),
									Toast.LENGTH_LONG).show();
						}
					}
					feet += inches / 12;
					inches = inches % 12;
					feetEdit.setText("" + feet);
					inchEdit.setText("" + inches);
				}
				watchText = true; // Allow UI elements to do stuff
			}

		};

		/**
		 * If the text field only has "0" in it, remove it for easier entry for
		 * the user. If the user is leaving a field blank, set it to 0.
		 */
		OnFocusChangeListener removeZeroListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean isFocused) {
				if (((EditText) view).getText().toString().equals("0")
						&& isFocused)
					((EditText) view).setText("");
				if (((EditText) view).getText().toString().equals("")
						&& !isFocused)
					((EditText) view).setText("0");
			}

		};
		OnItemSelectedListener fractionSpinnerListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				updateDimensions(); // Update the dimensions from UI
				if (!dimensionsMatch()) { // If they don't match
					manualDim = new Dimension(wizardDim);

					/*
					 * Set the value of the manual dimension to the wizard one
					 */
					watchText = false; // Prevent UI elements from doing things
					manualEdit.setText("" + wizardDim.toDouble());

					/*
					 * Change manual dimension
					 */
					watchText = true; // Allow UI elements to do stuff again
					dim = new Dimension(manualDim); // Synchronize dim
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		};

		/*
		 * Prior to implementing the textlisteners, update the dimension if
		 * we're coming from a saved state
		 */
		if (savedInstanceState != null) {
			dim = (Dimension) savedInstanceState.getSerializable("dim");
			manualDim = new Dimension(dim);
			wizardDim = new Dimension(dim);
		}

		// Implement these listeners

		feetEdit.addTextChangedListener(dimensionWizardTextEditListener);
		inchEdit.addTextChangedListener(dimensionWizardTextEditListener);
		manualEdit.addTextChangedListener(manualDimensionTextEditListener);

		inchEdit.setOnFocusChangeListener(inchFocusListener);
		feetEdit.setOnFocusChangeListener(removeZeroListener);
		manualEdit.setOnFocusChangeListener(removeZeroListener);

		fractionSpinner.setOnItemSelectedListener(fractionSpinnerListener);

	}

	/**
	 * Saves the instance state, and adds the supposedly correct dimension to
	 * the state into "dim".
	 */
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putSerializable("dim", dim);
	}

	/**
	 * Updates dimension from UI. Does not change dim. If any field is blank or
	 * invalid it is set to 0.
	 */
	protected void updateDimensions() {

		boolean negative;
		int feet, inches, fraction;

		if (feetEdit.length() > 0) {
			try {
				feet = Integer.parseInt(feetEdit.getText().toString());
			} catch (NumberFormatException e) {
				feetEdit.setText("0");
				feet = 0;
			}
		} else {
			feet = 0;
		}

		if (inchEdit.length() > 0) {
			try {
				inches = Integer.parseInt(inchEdit.getText().toString());
			} catch (NumberFormatException e) {
				inches = 0;
			}
		} else {
			inches = 0;
		}

		fraction = fractionSpinner.getSelectedItemPosition();
		if (allowNegative) {
			negative = negativeCheckBox.isChecked();
		} else {
			negative = false;
		}

		wizardDim = new Dimension(feet, inches, fraction, negative);

		try {
			manualDim = new Dimension(
					manualEdit.length() > 0 ? Double.parseDouble(manualEdit
							.getText().toString()) : 0);
		} catch (NumberFormatException e) {
			manualDim = new Dimension(0);
		}

	}

	/**
	 * UI //Updates UI from dimension.
	 * 
	 * @param dim
	 *            The dimension that the UI will be updated from.
	 * @param forced
	 *            If true, all EditTexts are modified to have their contents
	 *            reflect the current dimension, even if they are in focus by
	 *            the user. If false, EditTexts that have user focus will not be
	 *            modified
	 * 
	 */
	protected void updateUI(Dimension dim, boolean forced) {

		// Disable text watching while the UI is changed.
		watchText = false;

		/*
		 * Set the negative checkbox, if neccesary.
		 */
		if (dim.isNegative()) {
			if (allowNegative && negativeCheckBox != null) {
				negativeCheckBox.setChecked(true);
			} else {
				// TODO V2 Uh-oh
			}
		} else {
			if (negativeCheckBox != null)
				negativeCheckBox.setChecked(false);
		}

		if (!forced) {
			/*
			 * Change the text fields, except the one that the user has focused.
			 */
			if (!feetEdit.isFocused() && !inchEdit.isFocused())
				feetEdit.setText(new String("" + dim.getFeet()));
			if (!inchEdit.isFocused())
				inchEdit.setText(new String("" + dim.getInches()));
			if (!manualEdit.isFocused())
				manualEdit.setText("" + dim.toDouble());
		} else {
			/*
			 * Change all text fields, regardless of focus.
			 */
			feetEdit.setText(new String("" + dim.getFeet()));
			inchEdit.setText(new String("" + dim.getInches()));
			manualEdit.setText("" + dim.toDouble());
		}

		fractionSpinner.setSelection(dim.getFraction());
		/*
		 * Assume this means that dim is valid.
		 */
		this.dim = dim;

		/*
		 * Allow text watching again.
		 */
		watchText = true;
	}
}
