package com.zgrannan.crewandroid;

import java.io.Serializable;

import com.zgrannan.crewandroid.Consumables.Material;
import com.zgrannan.crewandroid.Consumables.Sheet;
import com.zgrannan.crewandroid.Consumables.WoodStick;
import com.zgrannan.crewandroid.Geometry.Line;
import com.zgrannan.crewandroid.Geometry.Vertex;
import com.zgrannan.crewandroid.Util.Dimension;

/**
 * This class describes different types of pieces that can be initiated.
 * 
 * @author Zack Grannan
 * @version 0.9
 */
public class Pieces {

	//

	/**
	 * Any piece that can show a dimline should implement this interface
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public interface CanShowDimLine {

		public void removeDimLine();

		public void setDimLinePos(int pos);

		public void showDimLine();

		public void toggleDimLine();
	}

	/**
	 * Generic piece class,this piece should use a public constructor if it's
	 * really complex
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 */
	public static class Piece implements Comparable<Piece>, Serializable,
			CanDraw {

		private static final long serialVersionUID = 1L;

		/**
		 * Used for auto piece labeling
		 */
		private static int nameIndex = 0;

		/**
		 * Returns an automatically generated single-character piece name.
		 * 
		 * @return the next unique piece label.
		 */
		protected static String autoName() {
			if (nameIndex >= 26) {
				char firstChar = (char) ((nameIndex / 26) + 65);
				char secondChar = (char) ((nameIndex % 26) + 65);
				nameIndex++;
				return String.valueOf(firstChar) + secondChar;
			} else {
				nameIndex++;
				return String.valueOf((char) (nameIndex + 64));
			}
		}

		/**
		 * Resets the automatic labeler
		 */
		public static void resetName() {
			nameIndex = 0;
		}

		protected Material material;

		/**
		 * @return The material that this piece is made up of.
		 */
		public Material getMaterial() {
			return material;
		}

		protected Dimension length, width;

		protected Vertex[] vertex;
		protected Line[] line;
		protected boolean visible = true, selected = false, under = false;
		protected String name;

		/**
		 * , Creates a piece from another piece. Gives the piece a new name and
		 * new visibility
		 * 
		 * @param piece
		 *            The piece that parameters will be copied from.
		 */
		public Piece(Piece piece) {
			this(piece, false, false);
		}

		/**
		 * Pseudo constructor: copies parameters of other piece but does not
		 * copy vertices or lines.
		 * 
		 * @param piece
		 *            The piece that parameters will be copied from.
		 * @param copyName
		 *            Determines if the name will be copied from the other
		 *            piece.
		 * @param copyVisibility
		 *            Determines if the visibility will be copied from the other
		 *            piece.
		 */
		public Piece(Piece piece, boolean copyName, boolean copyVisibility) {

			length = new Dimension(piece.length);
			width = new Dimension(piece.width);

			material = piece.material;

			if (!copyName) {
				name = autoName();
			} else {
				name = piece.name;
			}
			if (copyVisibility) {
				visible = piece.visible;
				selected = piece.selected;
				under = piece.under;
			}
		}

		/**
		 * Constructor called from a further extended piece.
		 * 
		 * @param name
		 *            The name of the piece
		 * @param length
		 *            The address of the dimension representing the length of
		 *            this piece.
		 * @param width
		 *            The address of the dimension representing the width of
		 *            this piece.
		 * @param material
		 *            The material this piece is made of.
		 */
		protected Piece(String name, Dimension length, Dimension width,
				Material material) {
			this(name, material);
			// TODO V2 Constructors won't work like this.
			this.length = new Dimension(length);
			this.width = new Dimension(width);
		}

		/**
		 * Constructor called from a further extended piece
		 * 
		 * @param name
		 *            The name of the piece.
		 * @param material
		 *            The material the piece is made of.
		 */
		protected Piece(String name, Material material) {
			this.name = name;
			this.material = material;
		}

		protected Piece(Dimension length, Dimension width) {
			this.length = length;
			this.width = width;
		}

		/**
		 * Compares this piece to another, for use in sorting only
		 */
		@Override
		public int compareTo(Piece other) {

			if (material.id() != other.material.id()) {
				return other.material.id() - material.id();
			}
			if (!length.equals(other.length)) {
				return other.length.toDouble() - length.toDouble() > 0 ? 1 : -1;
			}
			if (!width.equals(other.width)) {
				return other.width.toDouble() - width.toDouble() > 0 ? 1 : -1;
			}
			return 0;
		}

		/**
		 * Gets the midpoint of this piece.
		 * 
		 * @return The midpoint of this piece.
		 */
		public Vertex getMidpoint() {

			float x = 0, y = 0;
			for (int i = 0; i < vertex.length; i++) {
				x += vertex[i].x.toFloat();
				y += vertex[i].y.toFloat();
			}
			return new Vertex(x / vertex.length, y / vertex.length);
		}

		public boolean isSelected() {
			return selected;
		}

		public boolean isUnder() {
			return under;
		}

		public boolean isVisible() {
			return visible;
		}

		public void makeInvisible() {
			visible = false;
		}

		public void makeVisible() {
			visible = true;
		}

		/**
		 * //Moves the piece by the dimensions.
		 * 
		 * @param x
		 *            The horizontal displacement of the piece.
		 * @param y
		 *            The vertical displacement of the piece.
		 */
		public void move(Dimension x, Dimension y) {

			for (int i = 0; i < vertex.length; i++) {
				vertex[i].move(x, y);
			}
		}

		public void select() {
			selected = true;
		}

		public void setMaterial(Material material) {
			this.material = material;
		}

		/**
		 * Sets the vertices of this piece. This should be called at the end of
		 * every extended piece at the end of it's construction. Updates the
		 * vertices for the piece and generates the lines.
		 * 
		 * @param vertex
		 *            An array of vertices.
		 */
		public void setVertices(Vertex[] vertex) {

			this.vertex = vertex;
			line = new Line[vertex.length];

			for (int i = 0; i < vertex.length - 1; i++) {
				line[i] = new Line(vertex[i], vertex[i + 1]);
			}

			line[vertex.length - 1] = new Line(vertex[vertex.length - 1],
					vertex[0]);
		}

		/**
		 * Sets the visibility of the piece from an integer Used in instructions
		 * only, if the visibility needs to be changed via an interface, adjust
		 * the settings using the actual functions
		 * 
		 * @param setting
		 *            The integer representing the visibility setting.
		 */
		public void setVisibility(int setting) {

			switch (setting) {
			case C.VISIBLE: {
				visible = true;
				selected = false;
				under = false;
				break;
			}
			case C.INVISIBLE: {
				visible = false;
				selected = false;
				under = false;
				break;
			}
			case C.UNDER: {
				visible = true;
				selected = false;
				under = true;
				break;
			}
			case C.SELECTED: {
				visible = true;
				selected = true;
				under = false;
				break;
			}
			}
		}

		public void unselect() {
			selected = false;
		}

		@Override
		public Dimension getHeight() {
			return length;
		}

		@Override
		public Dimension getWidth() {
			return width;
		}

		public float[] toVertices(float offX, float offY) {
			float[] vertices = new float[vertex.length * 6];
			for (int i = 0; i < vertex.length; i++) {
				vertices[i * 6] = vertex[i].x.toFloat() + offX;
				vertices[i * 6 + 1] = vertex[i].y.toFloat() + offY;
				vertices[i * 6 + 2] = 0.0f;
				vertices[i * 6 + 3] = vertex[i].x.toFloat() + offX;
				vertices[i * 6 + 4] = vertex[i].y.toFloat() + offY;
				if (material instanceof WoodStick) {
					vertices[i * 6 + 5] = material.getDepth().toFloat();
				} else {
					vertices[i * 6 + 5] = 0 - material.getDepth().toFloat();
				}
			}
			return vertices;
		}

	}

	/**
	 * Class for pieces with four vertices
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class QuadPiece extends Piece implements CanShowDimLine {

		private static final long serialVersionUID = 1L;
		protected Vertex a, b, c, d; // Clockwise from top left
		protected Line e, f, g, h; // Starts at the top, clockwise

		/**
		 * Where the dimline will be drawn for this piece.
		 */
		protected int dimLinePos;

		/**
		 * Whether or not the dimline will be drawn.
		 */
		protected boolean dimensionVisible;

		/**
		 * Creates this piece from another QuadPiece. Doesn't copy the name or
		 * visibility of the other piece.
		 * 
		 * @param piece
		 *            The piece that parameters will be copied from.
		 */
		public QuadPiece(QuadPiece piece) {
			this(piece, false, false);
		}

		/**
		 * Creates the piece from another QuadPiece.
		 * 
		 * @param piece
		 *            The piece that parameters will be copied from.
		 * @param copyName
		 *            Whether or not the name will be copied.
		 * @param copyVisibility
		 *            Whether or not the visibility will be copied.
		 */
		public QuadPiece(QuadPiece piece, boolean copyName,
				boolean copyVisibility) {

			super(piece, copyName, copyVisibility);

			createFromVertices(new Vertex(piece.a), new Vertex(piece.b),
					new Vertex(piece.c), new Vertex(piece.d));

			if (copyVisibility) {

				/*
				 * If this piece copies the visibility of the other piece... The
				 * dimline positioning should probably be the same.
				 */
				dimLinePos = piece.dimLinePos;
			}
		}

		/**
		 * Pseudo-construcutor, sets name,length,width,material,dimlinePos
		 * 
		 * @param name
		 *            The name of this piece.
		 * @param length
		 *            Reference to a dimension with the length of this piece.
		 * @param width
		 *            Reference to a dimension with the width of this piece.
		 * @param material
		 *            What the piece will be made of.
		 * @param dimLinePos
		 *            The position of the dimLine with relation to this piece.
		 */
		public QuadPiece(String name, Dimension length, Dimension width,
				Material material, int dimLinePos) {

			super(name, length, width, material);

			/*
			 * Also satisfies super Pseudo-Constructor
			 */
			this.dimLinePos = dimLinePos;

			/*
			 * Set the position for where the dimline should be
			 */

		}

		/**
		 * Creates a new piece from vertices
		 * 
		 * @param name
		 *            The name of the piece.
		 * @param i
		 *            Top-left vertex.
		 * @param j
		 *            Top-right vertex.
		 * @param k
		 *            Bottom-right vertex.
		 * @param l
		 *            Bottom-left vertex.
		 * @param material
		 *            The material the piece is made of.
		 */
		QuadPiece(String name, Vertex i, Vertex j, Vertex k, Vertex l,
				Material material) {
			super(name, material); // Satisfy super pseudo constructor
			createFromVertices(i, j, k, l);
		}

		/**
		 * Loads the vertexes into the piece, and associates objects
		 * 
		 * @param a
		 *            Top-left vertex.
		 * @param b
		 *            Top-right vertex.
		 * @param c
		 *            Bottom-right vertex.
		 * @param d
		 *            Bottom-left vertex.
		 */
		private void createFromVertices(Vertex a, Vertex b, Vertex c, Vertex d) {

			super.setVertices(new Vertex[] { a, b, c, d });

			/*
			 * Load vertexes into super
			 */

			// Associate quadlines and quadvertices
			this.a = super.vertex[0];
			this.b = super.vertex[1];
			this.c = super.vertex[2];
			this.d = super.vertex[3];
			e = super.line[0];
			f = super.line[1];
			g = super.line[2];
			h = super.line[3];

			// adds dimLines to lines
			e.addDimLine(C.ABOVE);
			f.addDimLine(C.RIGHT);
			g.addDimLine(C.BELOW);
			h.addDimLine(C.LEFT);
		}

		@Override
		public void removeDimLine() {
			dimensionVisible = false;
			switch (dimLinePos) {
			case C.ABOVE:
				e.hideDimLine();
				break;
			case C.RIGHT:
				f.hideDimLine();
				break;
			case C.BELOW:
				g.hideDimLine();
				break;
			case C.LEFT:
				h.hideDimLine();
				break;
			}
		}

		@Override
		public void setDimLinePos(int pos) {
			// Sets the position of the dimline to be created for this piece
			dimLinePos = pos;
		}

		@Override
		public void showDimLine() {
			dimensionVisible = true;
			switch (dimLinePos) {
			case C.ABOVE:
				e.showDimLine();
				break;
			case C.RIGHT:
				f.showDimLine();
				break;
			case C.BELOW:
				g.showDimLine();
				break;
			case C.LEFT:
				h.showDimLine();
				break;
			}
		}

		@Override
		public void toggleDimLine() {
			// Changes the visibility of this piece's dimline
			if (dimensionVisible)
				removeDimLine();
			else
				showDimLine();
		}
	}

	/**
	 * Class for rectangular pieces.
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class RectPiece extends QuadPiece {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// TODO V2 Nine constructors, really?

		/**
		 * Create a rectangle piece from another rectangle piece.
		 * 
		 * @param piece
		 *            The piece parameters are copied from.
		 */
		public RectPiece(RectPiece piece) {
			super(piece);
		}

		/**
		 * Creates a rectangle piece from parameters.
		 * 
		 * @param name
		 *            The name of the piece.
		 * @param origin
		 *            The upper-left corner of the piece.
		 * @param width
		 *            The width of the piece.
		 * @param length
		 *            The length of the piece.
		 * @param material
		 *            The material this piece is made of.
		 * @param orient
		 *            The orientation of the piece. Determines if the piece is
		 *            placed vertically or horizontally.
		 * @param dimLinePos
		 *            The positional relationship between the DimLine and this
		 *            piece.
		 */
		RectPiece(String name, Vertex origin, Dimension width,
				Dimension length, Material material, boolean orient,
				int dimLinePos) {

			super(name, length, width, material, dimLinePos);

			/*
			 * Satisfy super pseudo-constructor
			 */

			a = new Vertex(origin);
			if (orient == C.HORIZONTAL) {
				b = new Vertex(a);
				b.move(length, new Dimension(0));
				c = new Vertex(b);
				c.move(new Dimension(0), width);
				d = new Vertex(c);
				d.move(length.negative(), new Dimension(0));
			} else {
				b = new Vertex(a);
				b.move(width, new Dimension(0));
				c = new Vertex(b);
				c.move(new Dimension(0), length);
				d = new Vertex(c);
				d.move(width.negative(), new Dimension(0));
			}

			super.createFromVertices(a, b, c, d);

			/*
			 * Associates these vertices with this piece
			 */
		}

		/**
		 * Creates a rectangle piece from parameters.
		 * 
		 * @param name
		 *            The name of the piece.
		 * @param origin
		 *            The upper-left corner of the piece.
		 * @param length
		 *            The length of the piece.
		 * @param woodstick
		 *            The woodstick this piece is made of.
		 * @param orient
		 *            The orientation of the piece. Determines if the piece is
		 *            placed vertically or horizontally.
		 */
		RectPiece(String name, Vertex origin, Dimension length,
				WoodStick woodstick, boolean orient) {
			this(name, origin, length, woodstick, orient,
					orient == C.VERTICAL ? C.RIGHT : C.BELOW);
		}

		/**
		 * Creates a rectangle piece from parameters.
		 * 
		 * @param name
		 *            The name of the piece.
		 * @param origin
		 *            The upper-left corner of the piece.
		 * @param length
		 *            The length of the piece.
		 * @param woodstick
		 *            The woodstick this piece is made of.
		 * @param orient
		 *            The orientation of the piece. Determines if the piece is
		 *            placed vertically or horizontally.
		 * @param dimLinePos
		 *            The positional relationship between the DimLine and this
		 *            piece.
		 */
		RectPiece(String name, Vertex origin, Dimension length,
				WoodStick woodstick, boolean orient, int dimLinePos) {

			this(name, origin, woodstick.getWidth(), length, woodstick, orient,
					dimLinePos);
		}

		/**
		 * Creates a RectPiece from parameters.
		 * 
		 * @param origin
		 *            The upper-left corner of the piece.
		 * @param width
		 *            The width of the piece.
		 * @param length
		 *            The length of the piece.
		 * @param sheet
		 *            The sheet this piece is made of.
		 * @param visibility
		 *            An integer representing the visibility of the piece.
		 */
		RectPiece(Vertex origin, Dimension width, Dimension length,
				Sheet sheet, int visibility) {
			this(autoName(), origin, width, length, sheet, C.VERTICAL, C.LEFT);
			setVisibility(visibility);
		}

		/**
		 * Creates a RectPiece from parameters.
		 * 
		 * @param origin
		 *            The upper-left corner of the piece.
		 * @param length
		 *            The length of the piece.
		 * @param woodstick
		 *            The woodstick this piece is made of.
		 * @param orient
		 *            The orientation of the piece. Determines if the piece is
		 *            placed vertically or horizontally.
		 */
		RectPiece(Vertex origin, Dimension length, WoodStick woodstick,
				boolean orient) {

			this(autoName(), origin, length, woodstick, orient,
					orient == C.VERTICAL ? C.RIGHT : C.BELOW);
		}

		RectPiece(Vertex origin, Dimension length, WoodStick woodstick,
				boolean orient, int dimLinePos) {

			/*
			 * Generates a rectpiece from
			 * origin,length,woodstick,orient,dimLinepos Go autoLabel
			 */
			this(autoName(), origin, length, woodstick, orient, dimLinePos);
		}

		public RectPiece(Vertex origin, double length, WoodStick woodstick,
				boolean orientation) {

			/*
			 * Generates a rectpiece from origin,length,woodstick,orient Go
			 * autoLabel
			 */
			this(autoName(), origin, new Dimension(length), woodstick,
					orientation);
		}

		public RectPiece(Vertex origin, double length, WoodStick woodstick,
				boolean orientation, int dimLinePos) {

			/*
			 * Generates a rectpiece from
			 * origin,length,woodstick,orient,dimLinepos Go autoLabel
			 */

			this(autoName(), origin, new Dimension(length), woodstick,
					orientation, dimLinePos);
		}

		/**
		 * Finds the length of the longest side of the rectPiece.
		 * 
		 * @return The dimension of the longest side.
		 */
		public Dimension getLongDimension() {

			if (getLongSide() == C.HORIZONTAL)
				return Line.getLength(a, b);
			else
				return Line.getLength(b, c);
		}

		/**
		 * Returns the longer orientation of the rectPiece.
		 * 
		 * @return {@link C.HORIZONTAL} (false) if it is longer horizontally,
		 *         {@link C.VERTICAL} (true) if it is longer vertically.
		 */
		public boolean getLongSide() {
			return b.x.toDouble() - a.x.toDouble() > c.y.toDouble()
					- a.y.toDouble() ? C.HORIZONTAL : C.VERTICAL;
		}

		@Override
		public String toString() {
			String value = "";
			value += material + " @ ";
			value += length;
			if (material instanceof Sheet) {
				value += " * " + width;
			}
			return value;
		}
	}

	/**
	 * Class for a triangular piece
	 * 
	 * @author Zack Grannan
	 * @version 0.93
	 * 
	 */
	public static class TriPiece extends Piece {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected TriPiece(Dimension length, Dimension width) {
			super(length, width);
		}

		protected Vertex a, b, c;
		protected Line d, e, f;
		protected boolean showAngles;

		/**
		 * Allows the triangle to show it's angles.
		 */
		public void showAngles() {
			showAngles = true;
		}

		/**
		 * Determines if this can show angles
		 * 
		 * @return True if the piece can show angles.
		 */
		public boolean canShowAngles() {
			return showAngles;
		}

	}

	/**
	 * Class for a right-triangular piece
	 * 
	 * @author Zack Grannan
	 * @version 0.93
	 * 
	 */
	public static class RightTriPiece extends TriPiece {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * Creates a right triangular piece from dimensions, this constructor is
		 * used for the trigonometry calculator
		 * 
		 * @param height
		 *            The height of the triangle ( "opposite side" )
		 * @param width
		 *            The width of the triangle ( "adjacent side" )
		 */
		protected Line opposite, adjacent, hypotenuse;

		public RightTriPiece(Dimension height, Dimension width) {
			super(height, width);
			a = new Vertex(new Dimension(0), height);
			b = new Vertex(width, height);
			c = new Vertex(width, new Dimension(0));
			super.setVertices(new Vertex[] { a, b, c });
			d = super.line[0];
			e = super.line[1];
			f = super.line[2];
			adjacent = d;
			opposite = e;
			hypotenuse = f;
			adjacent.addDimLine(C.BELOW);
			opposite.addDimLine(C.BELOW); // TODO: Eeek.
			hypotenuse.addDimLine(C.ABOVE);
		}

		/**
		 * Returns the primary angle of the triangle, assuming that the triangle
		 * is a right-triangle
		 * 
		 * @return The slope of the angle between the hypotenuse and the
		 *         "adjacent side"
		 */
		public double getPrimaryAngle() {
			return Geometry.arctan(length.toDouble() / width.toDouble());
		}

		/**
		 * Returns the secondary angle of the triangle, assuming that the
		 * triangle is a right-triangle. This angle is acquired by subtracting
		 * the primary angle from 90.
		 * 
		 * @return The slope of the angle between the hypotenuse and the
		 *         "opposite side"
		 */
		public double getSecondaryAngle() {
			return 90 - getPrimaryAngle();
		}

		/**
		 * Returns the length of the opposite side of the triangle.
		 * 
		 * @return A dimension representing the length
		 */
		public Dimension getOppositeLength() {
			return opposite.getDimension();
		}

		/**
		 * Returns the length of the adjacent side of the triangle.
		 * 
		 * @return A dimension representing the length
		 */
		public Dimension getAdjacentLength() {
			return adjacent.getDimension();
		}

		/**
		 * Returns the length of the hypotenuse of the triangle.
		 * 
		 * @return A dimension representing the length
		 */
		public Dimension getHypotenuseLength() {
			return hypotenuse.getDimension();
		}

	}
}
