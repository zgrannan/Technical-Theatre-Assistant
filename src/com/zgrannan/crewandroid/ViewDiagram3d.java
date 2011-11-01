package com.zgrannan.crewandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ViewDiagram3d extends Activity {
	TouchSurfaceView view;
	Buildable setPiece;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Context context = getBaseContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diagram3d);
		view = (TouchSurfaceView) findViewById(R.id.diagram3d);

		Intent intent = getIntent();
		setPiece = (Buildable) intent.getSerializableExtra("setpiece");
		setPiece.make(context);
		float scale = (float) Math.abs(Math.min(view.getHeight()
				/ setPiece.getHeight().toDouble(), view.getWidth()
				/ setPiece.getWidth().toDouble()) * 2 / 3);
		view.setSetPiece(setPiece);
		view.requestFocus();
		view.setFocusableInTouchMode(true);
		final Button goBack_button = (Button) findViewById(R.id.goback_button);
		goBack_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
