package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

public abstract class ValueButton extends Button {
	public abstract boolean hasValue();

	public abstract void setOnClickListener(final Activity activity);

	/**
	 * The text that this button displays when it does not have a value
	 * associated with it.
	 */
	protected String prompt;

	/**
	 * The prefix to the value that the button will display. Don't forget to add
	 * a ":"
	 */
	protected String prefix;

	/**
	 * The post-text.
	 */
	protected String post;

	/**
	 * The code that will identify the result of editing activities spawned by
	 * this button.
	 */
	protected int requestCode;

	/**
	 * If true, a negative number could be stored here.
	 */
	protected boolean allowNegative;

	/**
	 * If true, the button should no longer be clickable once a value is entered
	 * into it. The text will also become red.
	 */
	protected boolean lockValue;

	/**
	 * Creates a value button from an XML specification
	 * 
	 * @param context
	 *            The set of attributes for the button.
	 * @param attrs
	 */
	public ValueButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ValueButton);
		requestCode = a.getInteger(R.styleable.ValueButton_requestCode, -1);
		prompt = a.getString(R.styleable.ValueButton_prompt);
		prefix = a.getString(R.styleable.ValueButton_prefix);
		post = a.getString(R.styleable.ValueButton_post);
		allowNegative = a.getBoolean(R.styleable.ValueButton_allowNegative,
				false);
		lockValue = a.getBoolean(R.styleable.ValueButton_lockValue, false);
		setText(prompt);
	}

	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		if (lockValue && hasValue()) {
			setClickable(false);
			setTextColor(Color.RED);
		}
	}

	public ValueButton(Context context) {
		super(context);
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

}
