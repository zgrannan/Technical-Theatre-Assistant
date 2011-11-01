package com.zgrannan.crewandroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class TouchSurfaceView extends GLSurfaceView {
	private float scale = -1;

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		/**
		 * Change the scale for the graphics.
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			scale /= detector.getScaleFactor();

			return true;
		}
	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private final float TRACKBALL_SCALE_FACTOR = 36.0f;
	private float mPreviousX;
	private float mPreviousY;
	private SetPieceRenderer mRenderer;
	private ScaleGestureDetector mScaleDetector;

	public TouchSurfaceView(Context context) {
		super(context);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public TouchSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public void setRenderer(SetPieceRenderer renderer) {
		mRenderer = renderer;
		super.setRenderer(renderer);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent e) {
		mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
		mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
		requestRender();
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		mScaleDetector.onTouchEvent(e);
		if (!mScaleDetector.isInProgress()) {
			float x = e.getX();
			float y = e.getY();
			switch (e.getAction()) {
			case MotionEvent.ACTION_MOVE:
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
				mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;

			}
			mPreviousX = x;
			mPreviousY = y;
		}
		requestRender();
		return true;
	}

	class SetPieceRenderer implements GLSurfaceView.Renderer {
		protected float mAngleX, mAngleY;
		private Buildable setPiece;
		private boolean showLid;
		List<float[]> vertices, sheetVertices;

		public SetPieceRenderer(Buildable setPiece) {
			this.setPiece = setPiece;
			vertices = setPiece.toVertices();
			sheetVertices = setPiece.sheetVertices();
			mAngleY = 180;
		}

		@Override
		public void onDrawFrame(GL10 gl) {

			// Clears the screen and depth buffer.
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

			short[] triIndices = { 0, 6, 4, 4, 2, 0, 2, 4, 5, 5, 3, 2, 3, 5, 7,
					7, 1, 3, 0, 6, 7, 7, 1, 0, 0, 1, 2, 2, 3, 1, 6, 7, 4, 4, 5,
					7 };

			short[] lineIndices = { 0, 1, 1, 3, 3, 2, 2, 0, 1, 7, 0, 6, 2, 4,
					3, 5, 6, 7, 7, 5, 5, 4, 4, 6 };
			gl.glLoadIdentity();
			gl.glTranslatef(0, 0, scale);
			gl.glRotatef(mAngleX, 0, 1, 0);
			gl.glRotatef(mAngleY, 1, 0, 0);

			gl.glColor4f(0.8f, 0.6f, 0, 1);
			for (int i = 0; i < vertices.size(); i++) {

				ByteBuffer ibb = ByteBuffer
						.allocateDirect(triIndices.length * 2);
				ibb.order(ByteOrder.nativeOrder());
				ShortBuffer indexBuffer = ibb.asShortBuffer();
				indexBuffer.put(triIndices);
				indexBuffer.position(0);

				ByteBuffer vbb = ByteBuffer
						.allocateDirect(vertices.get(i).length * 4);
				vbb.order(ByteOrder.nativeOrder());
				FloatBuffer vertexBuffer = vbb.asFloatBuffer();
				vertexBuffer.put(vertices.get(i));
				vertexBuffer.position(0);
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

				gl.glDrawElements(GL10.GL_TRIANGLES, triIndices.length,
						GL10.GL_UNSIGNED_SHORT, indexBuffer);

			}

			gl.glColor4f(0, 0, 0, 1);
			for (int i = 0; i < vertices.size(); i++) {
				ByteBuffer vbb = ByteBuffer
						.allocateDirect(vertices.get(i).length * 4);
				vbb.order(ByteOrder.nativeOrder());
				FloatBuffer vertexBuffer = vbb.asFloatBuffer();
				vertexBuffer.put(vertices.get(i));
				vertexBuffer.position(0);
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

				ByteBuffer ibb = ByteBuffer
						.allocateDirect(lineIndices.length * 2);
				ibb.order(ByteOrder.nativeOrder());
				ShortBuffer indexBuffer = ibb.asShortBuffer();
				indexBuffer.put(lineIndices);
				indexBuffer.position(0);

				gl.glDrawElements(GL10.GL_LINES, lineIndices.length,
						GL10.GL_UNSIGNED_SHORT, indexBuffer);
			}

			if (showLid) {
				gl.glColor4f(0.3f, 0.1f, 0.8f, 1);
				for (int i = 0; i < sheetVertices.size(); i++) {

					ByteBuffer ibb = ByteBuffer
							.allocateDirect(triIndices.length * 2);
					ibb.order(ByteOrder.nativeOrder());
					ShortBuffer indexBuffer = ibb.asShortBuffer();
					indexBuffer.put(triIndices);
					indexBuffer.position(0);

					ByteBuffer vbb = ByteBuffer.allocateDirect(sheetVertices
							.get(i).length * 4);
					vbb.order(ByteOrder.nativeOrder());
					FloatBuffer vertexBuffer = vbb.asFloatBuffer();
					vertexBuffer.put(sheetVertices.get(i));
					vertexBuffer.position(0);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

					gl.glDrawElements(GL10.GL_TRIANGLES, triIndices.length,
							GL10.GL_UNSIGNED_SHORT, indexBuffer);

				}

				gl.glColor4f(0, 0, 0, 1);
				for (int i = 0; i < sheetVertices.size(); i++) {
					ByteBuffer vbb = ByteBuffer.allocateDirect(sheetVertices
							.get(i).length * 4);
					vbb.order(ByteOrder.nativeOrder());
					FloatBuffer vertexBuffer = vbb.asFloatBuffer();
					vertexBuffer.put(sheetVertices.get(i));
					vertexBuffer.position(0);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

					ByteBuffer ibb = ByteBuffer
							.allocateDirect(lineIndices.length * 2);
					ibb.order(ByteOrder.nativeOrder());
					ShortBuffer indexBuffer = ibb.asShortBuffer();
					indexBuffer.put(lineIndices);
					indexBuffer.position(0);

					gl.glDrawElements(GL10.GL_LINES, lineIndices.length,
							GL10.GL_UNSIGNED_SHORT, indexBuffer);
				}

			}
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisable(GL10.GL_LINE_SMOOTH);
			gl.glDisable(GL10.GL_BLEND);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// Sets the current view port to the new size.
			gl.glViewport(0, 0, width, height);
			// Select the projection matrix
			gl.glMatrixMode(GL10.GL_PROJECTION);
			// Reset the projection matrix
			gl.glLoadIdentity();
			// Calculate the aspect ratio of the window
			GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
					1000.0f);
			// Select the modelview matrix
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			// Reset the modelview matrix
			gl.glLoadIdentity();

		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

			// Set the background color to black ( rgba ).
			gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

			// Enable Smooth Shading, default not really needed.
			gl.glShadeModel(GL10.GL_SMOOTH);

			// Depth buffer setup.
			gl.glClearDepthf(1.0f);
			// Enables depth testing.
			gl.glEnable(GL10.GL_DEPTH_TEST);

			// The type of depth testing to do.
			gl.glDepthFunc(GL10.GL_LEQUAL);

			// Really nice perspective calculations.
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			gl.glEnable(GL10.GL_LINE_SMOOTH);
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_MULTISAMPLE);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
			gl.glHint(GL10.GL_SMOOTH, GL10.GL_NICEST);
			gl.glLineWidth(2.0f);

		}

		public void showLid() {
			showLid = true;
		}
	}

	public void setSetPiece(Buildable setPiece) {
		mRenderer = new SetPieceRenderer(setPiece);
		scale = Math.max(setPiece.length.toFloat(), setPiece.width.toFloat())
				* -2.0f;

		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
}