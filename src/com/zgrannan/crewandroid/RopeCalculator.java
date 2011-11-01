package com.zgrannan.crewandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * The activity for the rope strength calculator.
 * 
 * @author Zack Grannan
 * @version 0.94
 * 
 */
public class RopeCalculator extends Activity {
	private static final int ROPE_WARNING = 0, SOURCE = 1;
	Spinner ropeSpinner, diameterSpinner;
	RadioButton newButton, usedButton;
	EditText safetyFactorEdit;
	TextView ropeNameText, capacityText, warningText;

	/**
	 * The value of the UI "used" radioButton
	 * 
	 * @return True if the rope is used, false otherwise.
	 */
	boolean used() {
		return usedButton.isChecked();
	}

	/**
	 * The value of the entry in the safety factor textEdit
	 * 
	 * @return The value, or 0 if the value is invalid.
	 */
	double safetyFactor() {
		double value = 0;
		try {
			value = Double.parseDouble(safetyFactorEdit.getText().toString());
		} catch (NumberFormatException e) {
			value = 0;
		}
		if (value < 0)
			value = 0;
		return value;
	}

	/**
	 * The current contents of the diameter spinner.
	 * 
	 * @return The diameter.
	 */
	Dimension diameter() {
		return (Dimension) diameterSpinner.getSelectedItem();
	}

	/**
	 * The current value of the rope spinner.
	 * 
	 * @return The currently selected rope.
	 */
	Rope rope() {
		return (Rope) ropeSpinner.getSelectedItem();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager winMan = (WindowManager) getBaseContext()
				.getSystemService(Context.WINDOW_SERVICE);
		switch (winMan.getDefaultDisplay().getOrientation()) {
		case 0: { // Portrait
			setContentView(R.layout.rope_calculator_portrait);
			break;
		}
		case 1: { // Landscape
			setContentView(R.layout.rope_calculator_landscape);
			break;
		}
		}

		ropeSpinner = (Spinner) findViewById(R.id.rope_spinner);
		diameterSpinner = (Spinner) findViewById(R.id.diameter_spinner);
		newButton = (RadioButton) findViewById(R.id.new_button);
		usedButton = (RadioButton) findViewById(R.id.used_button);
		safetyFactorEdit = (EditText) findViewById(R.id.safety_factor_edit);
		ropeNameText = (TextView) findViewById(R.id.rope_name_text);
		capacityText = (TextView) findViewById(R.id.capacity_text);
		warningText = (TextView) findViewById(R.id.warning_text);

		ArrayAdapter<Dimension> diameterAdapter = new ArrayAdapter<Dimension>(
				getBaseContext(), android.R.layout.simple_spinner_item,
				Rope.dimensions);
		ArrayAdapter<Rope> ropeAdapter = new ArrayAdapter<Rope>(
				getBaseContext(), android.R.layout.simple_spinner_item,
				Rope.ropes);
		ropeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		diameterAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ropeSpinner.setAdapter(ropeAdapter);
		diameterSpinner.setAdapter(diameterAdapter);
		safetyFactorEdit.addTextChangedListener(Util
				.getProperFormatTextWatcher(false, false));

		/**
		 * This listener forces the UI to be updated whenever a view is
		 * selected.
		 */
		OnCheckedChangeListener updateUIonCheckedChangedListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				updateUI();

			}
		};

		newButton.setOnCheckedChangeListener(updateUIonCheckedChangedListener);
		usedButton.setOnCheckedChangeListener(updateUIonCheckedChangedListener);

		/**
		 * This listener forces the UI to be updated whenever a view is
		 * selected.
		 */
		OnItemSelectedListener updateUIonSelectedListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				updateUI();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		};

		ropeSpinner.setOnItemSelectedListener(updateUIonSelectedListener);
		diameterSpinner.setOnItemSelectedListener(updateUIonSelectedListener);
		safetyFactorEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				updateUI();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

		});
		/*
		 * Check to see if the user has been warned about the rope calculator.
		 */

		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		if (!settings.getBoolean("hasWarnedAboutRope", false)) {
			showDialog(ROPE_WARNING);
			settings.edit().putBoolean("hasWarnedAboutRope", true).commit();
		}
	}

	/**
	 * Updates all of the text fields.
	 */
	private void updateUI() {
		String warningMessage = "";
		if (safetyFactor() <= 0) {
			ropeNameText.setText(getString(R.string.invalid_safety_factor));
			capacityText.setText("");
			return;
		} else if (safetyFactor() < Rope.MINIMUM_SAFETY_FACTOR) {
			warningMessage += String.format(
					getString(R.string.safety_factor_too_low),
					Rope.MINIMUM_SAFETY_FACTOR)
					+ "\n";
		}
		if (used()) {
			warningMessage += getString(R.string.used_rope_warning);
		}
		ropeNameText.setText(getString(R.string.maximum_safe_capacity) + " "
				+ diameter() + " " + rope() + " with a safety factor of "
				+ safetyFactor());
		capacityText.setText(String.format("%.2f", getCapacity()) + " "
				+ getString(R.string.pounds));
		warningText.setText(warningMessage);
	}

	/**
	 * 
	 * @return The capacity of the rope currently displayed, taking into account
	 *         rope type, diameter, used/new, and safety factor. If the safety
	 *         factor is invalid, the value -1 is returned.
	 */
	private double getCapacity() {
		if (safetyFactor() <= 0) {
			return -1;
		}
		if (used()) {
			return rope().maximumUsedCapacity(diameter(), safetyFactor());
		} else {
			return rope().maximumNewCapacity(
					diameterSpinner.getSelectedItemPosition(), safetyFactor());
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		/**
		 * The dialog that will be returned
		 */
		Dialog dialog;
		switch (id) {
		case ROPE_WARNING: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.hey_there))
					.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();

								}
							})
					.setMessage(getResources().getString(R.string.rope_warning));
			dialog = builder.create();

			/*
			 * Also inform the user about the material information.
			 */
			break;
		}
		case SOURCE: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.rope_data_source))
					.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();

								}
							})
					.setMessage(
							getResources().getString(R.string.rope_source_text));
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

	/**
	 * Looks like the options menu was selected for the first time. Create it
	 * ... and then never get called again.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.rope_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		showDialog(SOURCE);
		return true;
	}
}
