package com.zgrannan.crewandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.zgrannan.crewandroid.Geometry.DimLine;
import com.zgrannan.crewandroid.Geometry.Line;
import com.zgrannan.crewandroid.Geometry.Vertex;
import com.zgrannan.crewandroid.Pieces.CanShowDimLine;
import com.zgrannan.crewandroid.Pieces.Piece;
import com.zgrannan.crewandroid.Pieces.RectPiece;
import com.zgrannan.crewandroid.Pieces.RightTriPiece;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This class describes a view that can draw set pieces. All graphics operations
 * are defined here.
 * 
 * @author Zack
 * @version 0.9
 */
public class CustomDrawableView extends View {

	/**
	 * This detects when the user uses the two-finger scale mechanism
	 */
	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		/**
		 * Change the scale for the graphics.
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			scale *= detector.getScaleFactor();
			if (scale < 1) {
				scale = 1; // Don't let the scale be less than one
			}
			labelPaint.setTextSize(scale * 5);
			invalidate(); // Force the view to redraw itself
			return true;
		}
	}

	private Paint piecePaint, // Paint for drawing the piece outline normally
			dimPaint, // Paint for writing the dimension and drawing dimLines
			labelPaint, // Paint for writing piece labels
			underPaint,

			/*
			 * Paint for indicating that a piece is under another (border)
			 */
			underFillPaint,

			/*
			 * Paint for indicating that a piece is under another (fill)
			 */
			selectFillPaint;

	/*
	 * Paint for indicating that a piece is selected (fill)
	 */

	private float scale, // Graphical scale
			xPad = 50, // Graphical horizontal offset
			yPad = 50, // Graphical vertical offset
			lastTouchX, lastTouchY;

	/*
	 * The places where the user last touched the screen
	 */
	private int activePointerId = -1; // For use in gesture detection
	private ScaleGestureDetector mScaleDetector;
	private CanDraw toDraw; // The set piece being built

	/**
	 * Sets the paint properties, and initializes the view
	 * 
	 * 
	 */
	public CustomDrawableView(Context context, AttributeSet attr) {
		super(context, attr);

		piecePaint = new Paint();
		piecePaint.setColor(Color.RED);

		dimPaint = new Paint();
		dimPaint.setTextSize(24);
		dimPaint.setColor(Color.WHITE);

		labelPaint = new Paint();
		labelPaint.setTextSize(18);
		labelPaint.setColor(Color.GRAY);

		underPaint = new Paint();
		underPaint.setColor(Color.MAGENTA);

		underFillPaint = new Paint();
		underFillPaint.setColor(Color.GRAY);

		selectFillPaint = new Paint();
		selectFillPaint.setARGB(127, 54, 102, 201);

		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

	}

	/**
	 * Draws a dimension line.
	 * 
	 * @param dimLine
	 *            The dimension line to be drawn.
	 * @param canvas
	 *            The canvas that it is drawn on.
	 */
	private void drawLine(DimLine dimLine, Canvas canvas) {

		final float SMALL_SPACE = 5, TEXT_SPACE = 50;
		if (dimLine == null || canvas == null || !dimLine.visible)
			return;

		/*
		 * If either the dimLine or canvas don't exist, or if the dimLine is
		 * invisible, return
		 */

		float fromX = dimLine.from.x.toFloat() * scale + xPad;
		float fromY = dimLine.from.y.toFloat() * scale + yPad;

		float toX = dimLine.to.x.toFloat() * scale + xPad;
		float toY = dimLine.to.y.toFloat() * scale + yPad;

		if (fromX > toX) {

			float temp = fromX;
			fromX = toX;
			toX = temp;

			temp = fromY;
			fromY = toY;
			toY = temp;

		}

		float dX = toX - fromX;
		float dY = toY - fromY;
		float dist = (float) Math.hypot(dX, dY);
		float angle = (float) Math.atan2(dY, dX);
		float perp = (float) (Math.PI / 2 + angle);

		Vertex start = new Vertex(fromX + SMALL_SPACE * Math.cos(perp), fromY
				+ Math.sin(perp) * SMALL_SPACE);
		Vertex stop = new Vertex(toX + SMALL_SPACE * Math.cos(perp), toY
				+ Math.sin(perp) * SMALL_SPACE);
		Vertex vertex, startEnd, stopEnd, startWrite;

		/*
		 * Depending on the relationship between the dimension line and the line
		 * it describes, Draw it
		 */
		if (dimLine.orientation == C.ABOVE || dimLine.orientation == C.LEFT) {
			perp -= Math.PI;
		} else {
			if (dimLine.orientation == C.RIGHT) {
				perp += Math.PI;
			}
		}
		vertex = drawLine(start, perp, dimLine.DIMSIZE, canvas, dimPaint);
		startEnd = drawLine(vertex, angle, (dist - TEXT_SPACE) / 2, canvas,
				dimPaint);

		vertex = drawLine(stop, perp, dimLine.DIMSIZE, canvas, dimPaint);
		stopEnd = drawLine(vertex, angle, 0 - (dist - TEXT_SPACE) / 2, canvas,
				dimPaint);

		startWrite = Geometry.getVertexAt(startEnd, angle, SMALL_SPACE);
		startWrite.y
				.setDimension((startEnd.y.toDouble() + stopEnd.y.toDouble()) / 2);

		canvas.drawText(dimLine.getDimension().toString(),
				startWrite.x.toFloat(), startWrite.y.toFloat(), this.dimPaint);
	}

	/**
	 * Draws a regular line.
	 * 
	 * @param line
	 *            The line that will be drawn.
	 * @param canvas
	 *            The canvas that the line will be drawn on.
	 * @param paint
	 *            The paint that describes the style of the line.
	 */
	private void drawLine(Line line, Canvas canvas, Paint paint) {

		if (line != null && canvas != null) {

			/*
			 * Provided that the line and canvas both exist
			 */
			canvas.drawLine(line.from.x.toFloat() * scale + xPad,
					line.from.y.toFloat() * scale + yPad, line.to.x.toFloat()
							* scale + xPad, line.to.y.toFloat() * scale + yPad,
					paint);
			drawLine(line.dimLine, canvas); // Also try to draw the dimline
		}
	}

	/**
	 * Draws a piece.
	 * 
	 * @param piece
	 *            The piece that will be drawn.
	 * @param canvas
	 *            The canvas that it will be drawn on.
	 */
	private void drawPiece(Piece piece, Canvas canvas) {
		// Find the right way to draw it
		if (piece instanceof RectPiece) {
			drawPiece((RectPiece) piece, canvas);
		}
		if (piece instanceof RightTriPiece) {
			drawPiece((RightTriPiece) piece, canvas);
		}
	}

	/**
	 * Draws a RightTriPiece. Labels the vertices and also shows the angles if
	 * the piece requests it.
	 * 
	 * @param piece
	 *            The piece that will be drawn.
	 * @param canvas
	 *            The canvas that it will be drawn on.
	 */
	private void drawPiece(RightTriPiece piece, Canvas canvas) {

		drawLine(piece.d, canvas, piecePaint);
		drawLine(piece.e, canvas, piecePaint);
		drawLine(piece.f, canvas, piecePaint);

		canvas.drawText("A", piece.a.x.toFloat() * scale + xPad,
				piece.a.y.toFloat() * scale + yPad, dimPaint);
		canvas.drawText("B", piece.b.x.toFloat() * scale + xPad,
				piece.b.y.toFloat() * scale + yPad, dimPaint);
		canvas.drawText("C", piece.c.x.toFloat() * scale + xPad,
				piece.c.y.toFloat() * scale + yPad, dimPaint);

		if (piece.canShowAngles()) {
			Vertex a = Geometry.getVertexAt(piece.a,
					0 - Math.toRadians(piece.getPrimaryAngle() / 2),
					piece.hypotenuse.getLength() / 10);
			Vertex b = Geometry.getVertexAt(piece.b, 5 * Math.PI / 4,
					piece.hypotenuse.getLength() / 10);
			Vertex c = Geometry
					.getVertexAt(
							piece.c,
							Math.PI
									/ 2
									+ Math.toRadians(piece.getSecondaryAngle() / 2),
							piece.hypotenuse.getLength() / 5);

			String aText = String.format("%1$.0f",
					Math.abs(piece.getPrimaryAngle()));
			String cText = String.format("%1$.0f",
					Math.abs(piece.getSecondaryAngle()));

			canvas.drawText(aText, a.x.toFloat() * scale + xPad, a.y.toFloat()
					* scale + yPad, dimPaint);
			canvas.drawText("90", b.x.toFloat() * scale + xPad, b.y.toFloat()
					* scale + yPad, dimPaint);
			canvas.drawText(cText, c.x.toFloat() * scale + xPad, c.y.toFloat()
					* scale + yPad, dimPaint);

		}
	}

	/**
	 * Draws a rectpiece
	 * 
	 * @param piece
	 *            The piece that will be drawn.
	 * @param canvas
	 *            The canvas that it will be drawn on.
	 */
	private void drawPiece(RectPiece piece, Canvas canvas) {

		if (piece.isVisible()) {

			// Only draw if visible

			if (piece.isUnder()) {

				// Draw it this way if it's underneath something

				drawLine(piece.e, canvas, underPaint);
				drawLine(piece.f, canvas, underPaint);
				drawLine(piece.g, canvas, underPaint);
				drawLine(piece.h, canvas, underPaint);
				drawUnder(piece, canvas, underFillPaint);

				/*
				 * Draw shading to indicate it's underneath something
				 */
			} else {

				// Otherwise draw it like this, including
				drawLine(piece.e, canvas, piecePaint);
				drawLine(piece.f, canvas, piecePaint);
				drawLine(piece.g, canvas, piecePaint);
				drawLine(piece.h, canvas, piecePaint);

				canvas.drawText(piece.name,
						(float) ((piece.getMidpoint().x.toFloat() - .75)
								* scale + xPad),
						(piece.getMidpoint().y.toFloat() + 1) * scale + yPad,
						labelPaint);
			}
			if (piece.isSelected()) {

				// If the piece is selected, fill it with some paint

				canvas.drawRect(piece.a.x.toFloat() * scale + xPad + 1,
						piece.a.y.toFloat() * scale + yPad + 1,
						piece.c.x.toFloat() * scale + xPad - 1,
						piece.c.y.toFloat() * scale + yPad - 1, selectFillPaint);
			}
		}

	}

	/**
	 * Draws a whole set piece to the canvas.
	 * 
	 * @param setPiece
	 *            The set piece that will be drawn.
	 * @param canvas
	 *            The canvas that it will be drawn on.
	 */
	private void drawSetPiece(Buildable setPiece, Canvas canvas) {

		for (int i = 0; i < setPiece.getPieces().length; i++) {
			// Piece by piece
			drawPiece(setPiece.getPieces()[i], canvas);
		}
	}

	/**
	 * Draw the shading for a rectangular piece that is underneath another
	 * piece.
	 * 
	 * @param piece
	 *            The piece that will be drawn.
	 * @param canvas
	 *            The canvas that it will be drawn on.
	 * @param paint
	 *            The paint that describes how the piece will be drawn.
	 * 
	 */
	private void drawUnder(RectPiece piece, Canvas canvas, Paint paint) {

		boolean orient = piece.getLongSide();

		/*
		 * Figure out the long side of the piece
		 */

		Vertex from, to; // These set the endpoints for each line drawn

		// Depending on the orientation, find the vertexes of the initial line

		if (orient == C.HORIZONTAL) {
			from = new Vertex(piece.d);
			to = new Vertex(piece.a);
			to.move(new Dimension(C.UNDER_LINE_DENSITY), new Dimension(0));
		} else {
			from = new Vertex(piece.a);
			to = new Vertex(piece.b);
			to.move(new Dimension(0), new Dimension(C.UNDER_LINE_DENSITY));
		}

		Line line = new Line(from, to); // Creates the first line

		for (int i = 0; i < piece.getLongDimension().toDouble()
				- C.UNDER_LINE_DENSITY; i += C.UNDER_LINE_DENSITY) {

			// Draw lines, depending on orientation

			drawLine(line, canvas, paint);
			if (orient == C.HORIZONTAL) {
				line.move(new Dimension(C.UNDER_LINE_DENSITY), new Dimension(0));
			} else {
				line.move(new Dimension(0), new Dimension(C.UNDER_LINE_DENSITY));
			}
		}
	}

	/**
	 * Finds the piece within FUDGE_FACTOR of the given coordinates that is
	 * capable of showing it's dimension line and is also makes up setPiece.
	 * 
	 * @param setPiece
	 *            The set piece that contains a piece that could be returned by
	 *            this function.
	 * @param x
	 *            The horizontal location of where the user tapped their finger.
	 * @param y
	 *            The vertical location of where the user tapped their finger.
	 * @return The nearest thing that can show a {@link DimLine}, or null if
	 *         nothing can be found.
	 */
	private CanShowDimLine findPieceThatCanShowDimLine(Buildable setPiece,
			float x, float y) {

		// Do some reverse scaling to find original value
		x -= xPad;
		x /= scale;
		y -= yPad;
		y /= scale;

		// Creates a vertex where the user clicked
		Vertex v = new Vertex(x, y);

		/*
		 * Creates an array of doubles to represent distances between the pieces
		 * of the object And where the screen was clicked
		 */
		double[] dist = new double[setPiece.getPieces().length];
		for (int i = 0; i < setPiece.getPieces().length; i++) {
			if (setPiece.getPieces()[i] != null)
				dist[i] = Line.getLength(v,
						setPiece.getPieces()[i].getMidpoint()).toDouble();
			else
				dist[i] = Double.MAX_VALUE;
		}

		// Stores the closes piece into piece
		Piece piece;
		piece = setPiece.getPieces()[Util.indexOfMinValue(dist)];

		// If the piece is too far away, forget it and return nothing
		if (Line.getLength(v, piece.getMidpoint()).toDouble() < Geometry.FUDGE_FACTOR) {
			return (CanShowDimLine) piece;
		} else {
			return null;
		}
	}

	/**
	 * Draws the canvas, this is called often
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		this.setBackgroundDrawable(null); // TODO V2 can this be deleted

		if (toDraw != null) {
			if (scale == 0) {
				scale = (float) Math.abs(Math.min(this.getHeight()
						/ toDraw.getHeight().toDouble(), this.getWidth()
						/ toDraw.getWidth().toDouble()) * 2 / 3);
				scale = Math.max(scale, 1);
			}
			if (toDraw instanceof Buildable) {
				drawSetPiece((Buildable) toDraw, canvas); // Draws the piece, if
															// it exists
			}
			if (toDraw instanceof Piece) {
				drawPiece((Piece) toDraw, canvas);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		// Looks like someone touched the screen

		mScaleDetector.onTouchEvent(e);

		/*
		 * Perform a scale operation if that's what the user wants
		 */

		// Get info about the event
		float x = e.getX();
		float y = e.getY();
		int action = e.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		// Handle the event
		case MotionEvent.ACTION_DOWN: {

			// If the person put's their finger down, change lastTouchs
			x = e.getX();
			y = e.getY();
			lastTouchX = x;
			lastTouchY = y;
			activePointerId = e.getPointerId(0);
			break;
		}
		case MotionEvent.ACTION_UP: {

			/*
			 * If the person put's their finger up, see if they were trying to
			 * show a dimLine
			 */
			if (toDraw instanceof Buildable) {
				CanShowDimLine piece = findPieceThatCanShowDimLine(
						(Buildable) toDraw, x, y);
				if (piece != null) {
					piece.toggleDimLine();

					/*
					 * If that's what they were trying to do, have the piece
					 * show it's dimline
					 */
				}
				break;
			}
		}
		case MotionEvent.ACTION_MOVE: {

			// If the person is trying to move the object, do it
			final int pointerIndex = e.findPointerIndex(activePointerId);
			x = e.getX(pointerIndex);
			y = e.getY(pointerIndex);

			if (!mScaleDetector.isInProgress()) {
				// Unless they are scaling the object
				xPad += x - lastTouchX;
				yPad += y - lastTouchY;
			}

			lastTouchX = x;
			lastTouchY = y;
			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {
			// Umm.. yeah
			final int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = e.getPointerId(pointerIndex);
			if (pointerId == activePointerId) {

				/*
				 * This was our active pointer going up. Choose a new active
				 * pointer and adjust accordingly.
				 */
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				lastTouchX = e.getX(newPointerIndex);
				lastTouchY = e.getY(newPointerIndex);
				activePointerId = e.getPointerId(newPointerIndex);
			}
			break;
		}
		}
		invalidate(); // Better redraw the canvas
		return true;
	}

	/**
	 * Set the item to be drawn.
	 * 
	 * @param toDraw
	 *            The item to be drawn.
	 */
	public void setDrawable(CanDraw toDraw) {
		this.toDraw = toDraw;
		scale = 0;
	}

	public Vertex drawLine(Vertex from, float angle, float distance, Canvas c,
			Paint paint) {
		c.drawLine(from.x.toFloat(), from.y.toFloat(),
				(float) (from.x.toFloat() + distance * Math.cos(angle)),
				(float) (from.y.toFloat() + distance * Math.sin(angle)), paint);
		return new Vertex(from.x.toFloat() + distance * Math.cos(angle),
				from.y.toFloat() + distance * Math.sin(angle));

	}

}
