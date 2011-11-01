package com.zgrannan.crewandroid;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * User interface for building set pieces.
 * 
 * @author Zack Grannan
 * @version 0.94
 */
public class Builder extends Activity {

	/**
	 * The current set piece.
	 */
	public static Buildable toBuild;

	// Declare UI / API objects

	/**
	 * The context for this activity.
	 */
	Context context;

	/**
	 * Press this button to build the set piece.
	 */
	Button buildButton;

	DimensionButton lengthButton, widthButton, doorHeightButton,
			doorWidthButton;

	Dimension length, width, doorHeight, doorWidth;
	Spinner toBuildSpinner, frameSpinner, lidSpinner;
	View doorframeDesign, standardDesign;

	/**
	 * The intent that started this activity. It may contain information about a
	 * saved set piece.
	 */
	Intent intent;

	/**
	 * Attempts to build the set piece when clicked.
	 */
	OnClickListener buildButtonListener;

	/**
	 * If false, the user is unaware that there is a height and width spacer
	 * associated with a doorframe.
	 */
	boolean hasDisplayedDoorframeMessage = false;

	/**
	 * Refers to the type of object to build, updated by toBuildSpinner. If this
	 * is changed, update toBuildSpinner accordingly
	 */
	int buildType = -1;

	private BuildResult attemptBuild() {
		return attemptBuild(true);
	}

	/**
	 * Attempts to build a set piece, using data from the UI. If successful, it
	 * will construct {@link toBuild}.
	 * <p>
	 * Step 1 of build. Step 1.5 is the static check() function, Step 2 is the
	 * constructor call for the specific set piece being built, with all of the
	 * data.
	 * <p>
	 * 
	 * @param forced
	 *            If true, the set piece will be built regardless of any
	 *            warnings. If false, any warning will cause a "premature"
	 *            return of the result with the warning attached. The user will
	 *            be prompted with a dialog, allowing her to modify the
	 *            parameters, or force the building.
	 * @return Any warnings or errors associated with the build. If any errors
	 *         occur toBuild will not be modified and the user will have an
	 *         appropriate message displayed to her in a Toast.
	 */
	private BuildResult attemptBuild(boolean forced) {
		BuildResult result = new BuildResult();

		/*
		 * Get the woodstick and the sheet from the spinners.
		 */
		WoodStick woodstick = (WoodStick) frameSpinner.getSelectedItem();
		Sheet sheet = (Sheet) lidSpinner.getSelectedItem();

		/*
		 * If any field is empty, set the result to "failed"
		 */

		if (!width.hasContent()) {
			result.fail();
			result.addMessage(getString(R.string.no_width_error));
		}

		if (!length.hasContent()) {
			if (buildType == C.DOORFRAME || buildType == C.STUDWALL) {
				result.fail();
				result.addMessage(getString(R.string.no_height_error));
			} else {
				result.fail();
				result.addMessage(getString(R.string.no_length_error));
			}
		}

		if (buildType == C.DOORFRAME && !doorWidth.hasContent()) {
			result.fail();
			result.addMessage(getString(R.string.no_door_width_error));
		}

		if (buildType == C.DOORFRAME && !doorHeight.hasContent()) {
			result.fail();
			result.addMessage(getString(R.string.no_door_height_error));
		}

		/*
		 * For platformType, it is recommended that length is longer than width,
		 * so it is changed here. If you need to create a platformType where
		 * width is greater you can call the constructor directly
		 */

		if (buildType == C.PLATFORM || buildType == C.HOLLYWOOD
				|| buildType == C.BROADWAY) {
			if (length.lessThan(width)) {
				Dimension temp = new Dimension(length);
				length = width;
				width = temp;
				lengthButton.setDimension(length);
				widthButton.setDimension(width);
			}
		}

		/*
		 * If the build has already failed, don't allow it to continue.
		 */
		if (!result.success()) {
			return result;
		}

		/*
		 * Tries to create the set piece. First, a static check() called by the
		 * class of the set piece type to see if the build will work. If the
		 * build won't work, the result is returned. If it will, toBuild is
		 * created, and the result is returned(with no error messages).
		 */
		switch (buildType) {
		case C.BROADWAY:
			result = PlatformType.check(context, length, width, woodstick, sheet);
			if (result.hasWarning() && forced == false) {
				return result;
			}
			if (result.success()) {
				toBuild = new Broadway(context, length, width, woodstick, sheet);
			}
			break;
		case C.DOORFRAME:
			result = Doorframe.check(context, length, width, doorHeight,
					doorWidth, woodstick, sheet);
			if (result.hasWarning() && forced == false) {
				return result;
			}
			if (result.success()) {
				toBuild = new Doorframe(context, length, width, doorHeight,
						doorWidth, woodstick, sheet);
			}
			break;
		case C.HOLLYWOOD:
			result = PlatformType.check(context, length, width, woodstick, sheet);
			if (result.hasWarning() && forced == false) {
				return result;
			}
			if (result.success()) {
				toBuild = new Hollywood(context, length, width, woodstick,
						sheet);
			}
			break;
		case C.PLATFORM:
			result = PlatformType.check(context, length, width, woodstick, sheet);
			if (result.hasWarning() && forced == false) {
				return result;
			}
			if (result.success()) {
				toBuild = new Platform(context, length, width, woodstick, sheet);
			}
			break;
		case C.STUDWALL:
			result = Studwall.check(context, length, width, woodstick, sheet);
			if (result.hasWarning() && forced == false) {
				return result;
			}
			if (result.success()) {
				toBuild = new Studwall(context, length, width, woodstick, sheet);
			}
			break;
		}

		// It looks like the set piece was built.

		/*
		 * If the user wants to build a doorframe, and the build was successful,
		 * remind about the spacers.
		 */
		if (buildType == C.DOORFRAME && !hasDisplayedDoorframeMessage
				&& result.success()) {
			hasDisplayedDoorframeMessage = true;
			Toast.makeText(
					context,
					String.format(getString(R.string.doorframe_message),
							new Dimension(Doorframe.WIDTH_SPACER),
							new Dimension(Doorframe.HEIGHT_SPACER)),
					Toast.LENGTH_LONG).show();
		}
		return result;
	}

	/**
	 * This function is called when loading a set piece. It sets the interface
	 * to correspond with the set piece and loads the data from the set piece
	 * into the interface. The onItemSelected call for toBuildSpinner does not
	 * happen in time for lengthView to be attached, So setInterface must be
	 * called explicitly.
	 */
	private void loadFromIntent() {
		toBuild = (Buildable) intent.getSerializableExtra("setpiece");
		if (toBuild instanceof Doorframe) {
			toBuildSpinner.setSelection(C.DOORFRAME);
			setInterface(C.DOORFRAME);
			doorWidth.setValue(((Doorframe) toBuild).doorWidth);
			doorHeight.setValue(((Doorframe) toBuild).doorHeight);
		} else if (toBuild instanceof Broadway) {
			toBuildSpinner.setSelection(C.BROADWAY);
			setInterface(C.BROADWAY);
		} else if (toBuild instanceof Platform) {
			toBuildSpinner.setSelection(C.PLATFORM);
			setInterface(C.PLATFORM);

		} else if (toBuild instanceof Hollywood) {
			toBuildSpinner.setSelection(C.HOLLYWOOD);
			setInterface(C.HOLLYWOOD);

		} else if (toBuild instanceof Studwall) {
			toBuildSpinner.setSelection(C.STUDWALL);
			setInterface(C.STUDWALL);
		}
		length.setValue(toBuild.length);
		width.setValue(toBuild.width);
	}

	/**
	 * This gets the result of the dimension editing activities. The dimension
	 * is loaded with no error checking.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == C.FAIL)
			return;
		Dimension dim = (Dimension) data.getSerializableExtra("dimension");

		if (requestCode == getResources().getInteger(R.integer.width)) {
			width.setValue(dim);
		}
		if (requestCode == getResources().getInteger(R.integer.length)) {
			length.setValue(dim);
		}
		if (requestCode == getResources().getInteger(R.integer.doorHeight)) {
			doorHeight.setValue(dim);
		}
		if (requestCode == getResources().getInteger(R.integer.doorWidth)) {
			doorWidth.setValue(dim);
		}
	}

	/**
	 * Called when the activity is capable of displaying a menu. A menu is
	 * displayed if their is already something in toBuild.
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (toBuild != null) {
			/*
			 * Since we're the set piece already exists, the user probably wants
			 * to see the menu.
			 */
			openOptionsMenu();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		/*
		 * Set some API stuff
		 */
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		intent = getIntent();

		setContentView(R.layout.builder);

		doorframeDesign = findViewById(R.id.doorframe_design);
		standardDesign = findViewById(R.id.standard_design);
		/*
		 * Set the listener for the build button.
		 */
		buildButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(buildButton.getWindowToken(), 0);
				BuildResult result = attemptBuild(false);
				if (!result.success()) {
					result.showErrors(context);
				} else if (result.hasWarning()) {
					showWarnBuildDialog(result);
				} else {
					openOptionsMenu();
				}
			}
		};

		/*
		 * Spinner
		 */
		toBuildSpinner = (Spinner) findViewById(R.id.tobuild_spinner);

		/*
		 * Sets the listener for the toBuild spinner. If the spinner is changed,
		 * it attempts to update the interface.
		 */

		OnItemSelectedListener toBuildSpinListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				/*
				 * Updates the interface, unless the selected piece type is the
				 * same as the type the builder is already loaded with.
				 */
				setInterface(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		};

		/*
		 * Loads the set piece types from the XML resource See C.java for these
		 * values
		 */
		final ArrayAdapter<Buildable> buildAdapter = new ArrayAdapter<Buildable>(
				context, android.R.layout.simple_spinner_item, Buildable.types);
		buildAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		toBuildSpinner.setAdapter(buildAdapter);
		toBuildSpinner.setOnItemSelectedListener(toBuildSpinListener);

		if (intent.getExtras() != null) {
			/*
			 * If we are loading a set piece from an intent Load it now
			 */
			loadFromIntent();
		}

		/*
		 * If nothing was loaded, the onItemSelectedListener will engage and set
		 * the default layout (platform)
		 */

	}

	/**
	 * Looks like the options menu was selected for the first time. Create it
	 * ... and then never get called again.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.builder_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Handles selections from the menu.
	 * 
	 * @param item
	 *            The item that was selected.
	 * @return true, always.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.view_cutlist: {

			if (attemptBuild().success()) {
				Intent intent = new Intent(getBaseContext(), ViewCutlist.class);
				intent.putExtra("cutlist", toBuild.getCutlist());
				startActivity(intent);
			} else {
				attemptBuild().showErrors(context);
			}
			return true;
		}
		case R.id.view_diagram: {

			if (attemptBuild().success()) {
				intent = new Intent(context, ViewDiagram.class);
				intent.putExtra("setpiece", toBuild);
				startActivity(intent);
			} else {
				attemptBuild().showErrors(context);
			}
			return true;
		}
		case R.id.save_setpiece: {

			if (attemptBuild().success()) {
				showSavePrompt();
			} else {
				attemptBuild().showErrors(context);
			}
			return true;
		}
		case R.id.instructions: {

			/*
			 * Rebuild the set piece, show a instructions for it if the set
			 * piece is valid If the set piece is invalid, a buildException is
			 * thrown and the user is notified via Toast
			 */

			if (attemptBuild().success()) {
				intent = new Intent(context, ViewInstructions.class);
				intent.putExtra("setpiece", toBuild);
				startActivity(intent);
			} else {
				attemptBuild().showErrors(context);
			}
			return true;
		}
		case R.id.view_diagram_3d: {
			if (attemptBuild().success()) {
				intent = new Intent(context, ViewDiagram3d.class);
				intent.putExtra("setpiece", toBuild);
				startActivity(intent);
			} else {
				attemptBuild().showErrors(context);
			}
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/**
	 * Load anything important into the savedInstanceState if the activity needs
	 * to be restarted.
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("buildType", buildType);
		savedInstanceState.putBoolean("doorframeMessage",
				hasDisplayedDoorframeMessage);
		if (toBuild != null)
			savedInstanceState.putSerializable("toBuild", toBuild);
	}

	/**
	 * Restores the instance state, used after the screen is rotated
	 * 
	 * @param bundle
	 *            The bundle to restore from (otherwise known as
	 *            savedInstanceState)
	 */
	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		buildType = bundle.getInt("buildType");
		setInterface(buildType, true);
		hasDisplayedDoorframeMessage = bundle.getBoolean("doorframeMessage");
	}

	/**
	 * Loads the interface.
	 * 
	 * @param i
	 *            The integer representing the set piece that will be built.
	 */
	private void setInterface(int i) {
		setInterface(i, false);
	}

	/**
	 * Loads the interface.
	 * 
	 * @param i
	 *            The integer representing the set piece that will be built.
	 * @param force
	 *            Whether or not the interface-loading is forced. If true, the
	 *            interface will be updated regardless of the current buildType.
	 */
	private void setInterface(int i, boolean force) {
		if (buildType == i && !force) {
			return;
		}
		doorframeDesign.setVisibility(View.GONE);
		standardDesign.setVisibility(View.GONE);
		buildType = i;

		/*
		 * Load the correct view, but don't yet associate anything.
		 */
		if (i == C.PLATFORM || i == C.BROADWAY || i == C.HOLLYWOOD
				|| i == C.STUDWALL) {
			standardDesign.setVisibility(View.VISIBLE);
			widthButton = (DimensionButton) findViewById(R.id.width_button);
			lengthButton = (DimensionButton) findViewById(R.id.length_button);
			if (i == C.STUDWALL) {
				lengthButton.setPrompt(getString(R.string.set_height));
				lengthButton.setPrefix(getString(R.string.height));
			}
			frameSpinner = (Spinner) findViewById(R.id.frame_spinner);
			lidSpinner = (Spinner) findViewById(R.id.lid_spinner);
		} else if (i == C.DOORFRAME) {
			doorframeDesign.setVisibility(View.VISIBLE);
			widthButton = (DimensionButton) findViewById(R.id.frame_width_button);
			lengthButton = (DimensionButton) findViewById(R.id.frame_height_button);
			lengthButton.setPrompt(getString(R.string.set_frame_height));
			lengthButton.setPrefix(getString(R.string.frame_height));
			doorHeightButton = (DimensionButton) findViewById(R.id.door_height_button);
			doorWidthButton = (DimensionButton) findViewById(R.id.door_width_button);
			doorHeight = doorHeightButton.getDimension();
			doorWidth = doorWidthButton.getDimension();
			doorHeightButton.setOnClickListener(this);
			doorWidthButton.setOnClickListener(this);
			frameSpinner = (Spinner) findViewById(R.id.doorframe_frame_spinner);
			lidSpinner = (Spinner) findViewById(R.id.doorframe_lid_spinner);
		}

		widthButton.setOnClickListener(this);
		lengthButton.setOnClickListener(this);

		width = widthButton.getDimension();
		length = lengthButton.getDimension();

		buildButton = (Button) findViewById(R.id.build_button);
		buildButton.setOnClickListener(buildButtonListener);

		ArrayAdapter<WoodStick> frameAdapter = new ArrayAdapter<WoodStick>(
				getBaseContext(), android.R.layout.simple_spinner_item,
				((Buildable) toBuildSpinner.getSelectedItem())
						.getValidWoodSticks());
		ArrayAdapter<Sheet> lidAdapter = new ArrayAdapter<Sheet>(
				getBaseContext(), android.R.layout.simple_spinner_item,
				((Buildable) toBuildSpinner.getSelectedItem()).getValidSheets());

		frameAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lidAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		frameSpinner.setAdapter(frameAdapter);
		lidSpinner.setAdapter(lidAdapter);
		if (toBuild != null) {
			for (int j = 0; j < lidSpinner.getCount(); j++) {
				if (((Sheet) (lidSpinner.getItemAtPosition(j)))
						.equals(toBuild.sheet)) {
					lidSpinner.setSelection(j);
					break;
				}
			}
			for (int j = 0; j < frameSpinner.getCount(); j++) {
				if (((WoodStick) (frameSpinner.getItemAtPosition(j)))
						.equals(toBuild.woodstick)) {
					frameSpinner.setSelection(j);
					break;
				}
			}
		}

	}

	/**
	 * If the user is exiting the Builder, ensure that he knows to save the set
	 * piece if he needs to.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			/*
			 * If the user isn't actually leaving, the user hasn't built a
			 * piece, or if the piece was already saved, then just return.
			 */
			if (toBuild == null
					|| (toBuild.getFilename() != null && toBuild.getFilename()
							.length() != 0))
				return super.onKeyDown(keyCode, event);
			showWarnSavePrompt();
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Creates a dialog for warning the user that the set piece will be lost if
	 * he does not choose to save it.
	 * 
	 * @return A dialog that will warn the user.
	 */
	public Dialog warnSaveDialog() {
		final Dialog warnSaveDialog = new Dialog(Builder.this);
		warnSaveDialog.setContentView(R.layout.two_button_dialog);
		warnSaveDialog.setTitle(getString(R.string.do_you_want_to_save));

		TextView promptText = (TextView) warnSaveDialog
				.findViewById(R.id.prompt_text);
		promptText.setText(getString(R.string.changes_will_be_lost));
		return warnSaveDialog;
	}

	/**
	 * Creates a dialog based on a build result. This is for notifying the user
	 * that there was a warning when the set piece was being built.
	 * 
	 * @param result
	 *            The {@link BuildResult} that contains the warning.
	 * @return A dialog.
	 * @see showWarnBuildDialog(BuildResult)
	 */
	public Dialog warnBuildDialog(BuildResult result) {
		final Dialog warnBuildDialog = new Dialog(Builder.this);
		warnBuildDialog.setContentView(R.layout.two_button_dialog);
		warnBuildDialog.setTitle(getString(R.string.build_warning));

		TextView promptText = (TextView) warnBuildDialog
				.findViewById(R.id.prompt_text);
		Button confirmButton = (Button) warnBuildDialog
				.findViewById(R.id.confirm_button);
		Button cancelButton = (Button) warnBuildDialog
				.findViewById(R.id.cancel_button);
		confirmButton.setText(getString(R.string.build_anyways));
		cancelButton.setText(getString(R.string.dont_build));
		promptText.setText(result.getWarningMessage());
		return warnBuildDialog;
	}

	/**
	 * Shows a dialog warning the user that there was a warning that occured
	 * when building the set piece. If the user decides to continue anyways, the
	 * set piece will be built. Otherwise, the user can choose to modify the
	 * build parameters.
	 * 
	 * @param result
	 *            The {@link BuildResult} that contains the warning.
	 */
	public void showWarnBuildDialog(BuildResult result) {
		final Dialog warnBuildDialog = warnBuildDialog(result);
		Button confirmButton = (Button) warnBuildDialog
				.findViewById(R.id.confirm_button);
		Button cancelButton = (Button) warnBuildDialog
				.findViewById(R.id.cancel_button);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				attemptBuild();
				warnBuildDialog.dismiss();
				openOptionsMenu();
			}

		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				warnBuildDialog.dismiss();
			}

		});
		warnBuildDialog.show();

	}

	/**
	 * Creates a dialog that allows the user to save the set piece.
	 * 
	 * @param filename
	 *            The default filename for the set piece.
	 * @return This dialog.
	 */
	public Dialog saveDialog(String filename) {
		final Dialog saveDialog = new Dialog(Builder.this);
		saveDialog.setContentView(R.layout.save_dialog);
		((EditText) saveDialog.findViewById(R.id.name_edit)).setText(filename);
		saveDialog.setTitle(getString(R.string.save_set_piece));
		return saveDialog;
	}

	/**
	 * Creates a dialog that prompts the user to overwrite the set piece.
	 * 
	 * @return This dialog.
	 */
	public Dialog overwriteDialog() {
		final Dialog overwriteDialog = new Dialog(Builder.this);
		overwriteDialog.setContentView(R.layout.two_button_dialog);
		overwriteDialog.setTitle(getString(R.string.overwrite));
		TextView promptText = (TextView) overwriteDialog
				.findViewById(R.id.prompt_text);
		promptText
				.setText(getString(R.string.set_piece_already_exists_do_you_want_to_overwrite));
		return overwriteDialog;
	}

	/**
	 * Warns the user that if he leaves without saving, the set piece will be
	 * lost.
	 */
	private void showWarnSavePrompt() {
		final Dialog warnSaveDialog = warnSaveDialog();

		warnSaveDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				finish();
			}

		});

		Button saveButton = (Button) warnSaveDialog
				.findViewById(R.id.confirm_button);

		Button discardButton = (Button) warnSaveDialog
				.findViewById(R.id.cancel_button);

		// Implement button listeners
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (attemptBuild().success()) {
					showSavePrompt(warnSaveDialog);
				}
			}
		});
		discardButton.setText(getString(R.string.dont_save));
		discardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				warnSaveDialog.dismiss();
				return;
			}

		});
		warnSaveDialog.show();
	}

	/**
	 * Shows a save prompt to the user. When the prompt is dismissed, nothing
	 * happens.
	 */
	private void showSavePrompt() {
		showSavePrompt(null);
	}

	/**
	 * Shows a save prompt to the user. When the prompt is dismissed, the dialog
	 * that spawned it is also dismissed.
	 * 
	 * @param warnSaveDialog
	 *            The dialog that spawned this one. It is closed when this
	 *            dialog completes.
	 */
	private void showSavePrompt(final Dialog warnSaveDialog) {
		final Dialog saveDialog = saveDialog(toBuild.getName(context));
		final EditText filenameText = (EditText) saveDialog
				.findViewById(R.id.name_edit);
		saveDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (warnSaveDialog != null)
					warnSaveDialog.dismiss();
			}

		});
		Button saveButton = (Button) saveDialog.findViewById(R.id.save_button);
		Button cancelButton = (Button) saveDialog
				.findViewById(R.id.cancel_button);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toBuild.setFilename(filenameText.getText().toString());
				if (filenameText.length() == 0) {
					/*
					 * If nothing is entered for the filename, give it it's
					 * default name, and attempt to save
					 */
					filenameText.setText(toBuild.getName(context));
				}
				if (CrewAndroid.setPieceExists(filenameText.getText()
						.toString())) {
					showOverwritePrompt(saveDialog, filenameText.getText()
							.toString());
				} else {
					// Save the set piece
					try {
						CrewAndroid.saveSetPiece(toBuild, getBaseContext());
						Toast.makeText(getBaseContext(),
								getString(R.string.save_successful),
								Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						Toast.makeText(
								context,
								getString(R.string.set_piece_could_not_be_saved),
								Toast.LENGTH_SHORT);
					}
					saveDialog.dismiss();
				}
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveDialog.cancel();
			}

		});
		saveDialog.show();
	}

	/**
	 * Shows a prompt to the user, indicating that he/she will need to overwrite
	 * the set piece.
	 * 
	 * @param saveDialog
	 *            The dialog that spawned this one, it is closed if the user
	 *            decides to save the piece.
	 * @param filename
	 *            The filename of the set piece.
	 */
	private void showOverwritePrompt(final Dialog saveDialog,
			final String filename) {
		final Dialog overwriteDialog = overwriteDialog();

		Button overwriteButton = (Button) overwriteDialog
				.findViewById(R.id.confirm_button);
		Button cancelButton = (Button) overwriteDialog
				.findViewById(R.id.cancel_button);

		// Implement button listeners
		overwriteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * If we are overwriting, delete the old set piece and replace
				 * it with this new one.
				 */
				try {
					CrewAndroid.deleteSetPiece(filename, getBaseContext());
				} catch (IOException e) {
					Toast.makeText(context,
							getString(R.string.set_piece_could_not_be_deleted),
							Toast.LENGTH_SHORT);
					overwriteDialog.dismiss();
					return;
				}
				try {
					CrewAndroid.saveSetPiece(toBuild, getBaseContext());
					Toast.makeText(getBaseContext(), "Save successful.",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(
							context,
							"The set piece couldn't be saved to a file. It may not remain if the application is closed.",
							Toast.LENGTH_SHORT);
				}
				overwriteDialog.dismiss();
				saveDialog.dismiss();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * If the user doesn't want to overwrite, go back to the
				 * filename prompt
				 */
				overwriteDialog.cancel();
			}
		});

		overwriteDialog.show();
		return;
	}
}