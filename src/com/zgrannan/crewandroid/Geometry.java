package com.zgrannan.crewandroid;

import java.io.Serializable;

import com.zgrannan.crewandroid.Util.Dimension;

/**
 * Class for lines, vertices,etc.
 * 
 * @author Zack Grannan
 * @version 0.9
 * 
 */
public class Geometry {

	/**
	 * Class for dimension lines.
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 */
	static class DimLine extends Line {

		private static final long serialVersionUID = 1L;
		protected boolean visible = false;

		/**
		 * Specifies the length of the lines making up the dimLine
		 */
		protected final float DIMSIZE = 50;
		// TODO V2 This should be a scalable thing

		/**
		 * Used when drawing the dimline, can be above,below,left, or right
		 */
		protected int orientation;

		/**
		 * Create a new {@link DimLine} from another dimline
		 * 
		 * @param the
		 *            {@link DimLine} that the properties will be copied from
		 */
		public DimLine(DimLine other) {
			super(other);
			visible = other.visible;
			orientation = other.orientation;
		}

		/**
		 * Create a dimline from vertices, set a manual orientation
		 * 
		 * @param a
		 *            The "from" {@link Vertex}.
		 * @param b
		 *            The "to" {@link Vertex}.
		 * @param orient
		 *            An integer representing the position of the DimLine in
		 *            relation to the Line it represents.
		 */
		DimLine(Vertex a, Vertex b, int orient) {
			super(a, b);
			orientation = orient;
		}

		/**
		 * Returns the value of this {@link DimLine}
		 * 
		 * @return A {@link Dimension} with the value of the distance between
		 *         the two {@link Vertex}s of this {@link DimLine}
		 */
		@Override
		public Dimension getDimension() {
			return Line.getLength(from, to);
		}

		/**
		 * Returns a String with a "pretty" graphical representation of the
		 * Dimension that this DimLine represents.
		 */
		@Override
		public String toString() {
			return Line.getLength(from, to).toString();
		}

		/**
		 * Select new vertices for dimLine, and update dimension accordingly
		 * 
		 * @param a
		 *            The new "from" vertex.
		 * @param b
		 *            The new "to" vertex.
		 */
		public void update(Vertex a, Vertex b) {
			from = a;
			to = b;
		}
	}

	/**
	 * This class describes a simple line. Note that lines reference vertices as
	 * endpoints, so they should be updated dynamically.
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class Line implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * //Returns the distance between two vertices as a dimension
		 * 
		 * @param from
		 *            The "from" vertex.
		 * @param to
		 *            The "to" vertex.
		 * @return A new dimension representing the distance between these
		 *         vertices.
		 */
		public static Dimension getLength(Vertex from, Vertex to) {

			return new Dimension(getLengthDouble(from, to));
		}

		/**
		 * Returns the distance between two vertices as a double
		 * 
		 * @param a
		 *            One vertex.
		 * @param b
		 *            Another vertex.
		 * @return A double representing the distance between these vertices.
		 */
		private static double getLengthDouble(Vertex a, Vertex b) {
			return Math.sqrt((a.x.toDouble() - b.x.toDouble())
					* (a.x.toDouble() - b.x.toDouble())
					+ (a.y.toDouble() - b.y.toDouble())
					* (a.y.toDouble() - b.y.toDouble()));
		}

		/**
		 * The dimension line that corresponds to this one. If this line
		 * shouldn't be showing a dimline, set this to null.
		 */
		protected DimLine dimLine;
		protected Vertex from, to;

		/**
		 * Creates a line from another line. If the line has a dimline
		 * associated with it, copy it.
		 * 
		 * @param other
		 *            The line to copy parameters from.
		 */

		public Line(Line other) {
			if (other.dimLine != null)
				dimLine = new DimLine(other.dimLine);
			from = new Vertex(other.from);
			to = new Vertex(other.to);
		}

		/**
		 * Create a line from a set of vertices. For use in DimLines only,
		 * unless there's a good reason for a line to be incapable of ever
		 * showing a DimLine.
		 * 
		 * @param a
		 *            The "from" vertex.
		 * @param b
		 *            The "to" vertex.
		 */
		Line(Vertex a, Vertex b) {

			from = a;
			to = b;

		}

		/**
		 * //Creates a line from a set of vertices, and associates a dimLine
		 * with this line
		 * 
		 * @param a
		 *            The "from vertex"
		 * @param b
		 *            The "to vertex"
		 * @param orient
		 *            An integer representing the positional relationship
		 *            between the DimLine and the Line.
		 */
		Line(Vertex a, Vertex b, int orient) {
			this(a, b);
			dimLine = new DimLine(a, b, orient);
		}

		/**
		 * Adds a dimension line to this line at the given orientation
		 * 
		 * @param orient
		 *            An integer representing the positional relationship
		 *            between the dimLine and this line.
		 */
		public void addDimLine(int orient) {

			dimLine = new DimLine(from, to, orient);

		}

		/**
		 * Returns the length of this line, as a double
		 * 
		 * @return the length of this line, as a double
		 */
		public double getLength() {
			return Line.getLength(from, to).toDouble();
		}

		public Vertex getMidpoint() {
			return new Vertex((from.x.toDouble() + to.x.toDouble()) / 2,
					(from.y.toDouble() + to.y.toDouble()) / 2);
		}

		/**
		 * Hides the dimline associated with this line. The DimLine still
		 * exists.
		 */
		public void hideDimLine() {
			dimLine.visible = false;
		}

		/**
		 * Moves the line.
		 * 
		 * @param x
		 *            Horizontal displacement.
		 * @param y
		 *            Vertical displacement.
		 */
		public void move(Dimension x, Dimension y) {
			from.move(x, y);
			to.move(x, y);
		}

		/**
		 * Changes the vertices to the given vertexes.
		 * 
		 * @param a
		 *            The address of the new "from" vertex.
		 * @param b
		 *            The address of the new "to" vertex.
		 */
		public void setVertices(Vertex a, Vertex b) {
			// Re-associates the line with new vertices
			from = a;
			to = b;
		}

		public void showDimLine() {
			dimLine.visible = true;
		}

		/**
		 * Returns the length of this line, as a dimension.
		 * 
		 * @return the length of this line, as a dimension.
		 */
		public Dimension getDimension() {
			return Line.getLength(from, to);
		}

		/**
		 * Returns the slope of the line (delta Y / delta X)
		 * 
		 * @return A double representing the slope.
		 */
		public double getSlope() {
			return (from.y.toDouble() - to.y.toDouble())
					/ (from.x.toDouble() - to.x.toDouble());
		}
	}

	/**
	 * Class for the vertex.
	 * 
	 * @author Zack Grannan
	 * @version 0.9
	 * 
	 */
	public static class Vertex implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected Dimension x, y;

		/**
		 * Creates a vertex from given dimensions.
		 * 
		 * @param x
		 *            The horizontal {@link Dimension}.
		 * @param y
		 *            The vertical {@link Dimension}.
		 */
		public Vertex(Dimension x, Dimension y) {
			// TODO V2 Constructors shouldn't call new() in this function
			this.x = new Dimension(x);
			this.y = new Dimension(y);
		}

		/**
		 * Creates a vertex from given double values
		 * 
		 * @param x
		 *            The horizontal value.
		 * @param y
		 *            The vertical calue.
		 */
		public Vertex(double x, double y) {

			this.x = new Dimension(x);
			this.y = new Dimension(y);

		}

		/**
		 * Creates a vertex from another vertex.
		 * 
		 * @param other
		 *            The vertex the other parameters are copied from.
		 */
		Vertex(Vertex other) {

			x = new Dimension(other.x);
			y = new Dimension(other.y);

		}

		/**
		 * Move this vertex by given dimensions
		 * 
		 * @param a
		 *            Horizontal displacement.
		 * @param b
		 *            Vertical displacement.
		 */
		public void move(Dimension a, Dimension b) {

			x.add(a);
			y.add(b);
		}

		/**
		 * Moves the vertex from given float values
		 * 
		 * @param x
		 *            The horizontal value.
		 * @param y
		 *            The vertical calue.
		 */
		public void setPosition(float x, float y) {
			this.x = new Dimension(x);
			this.y = new Dimension(y);
		}

		/**
		 * Pretty text output of this vertex.
		 */
		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}

	/***
	 * Used for determining how accurate screen gestures need to be
	 */
	public static final float FUDGE_FACTOR = (float) 12.5;

	/**
	 * The corner of the vertex-space
	 */
	public static final Vertex origin = new Vertex(0, 0);

	/**
	 * Cosine using degrees
	 * 
	 * @param angle
	 *            The angle in degrees.
	 * @return The cosine of the angle.
	 */
	static double cos(double angle) {
		return Math.cos(Math.toRadians(angle));
	}

	/**
	 * Sine using degrees.
	 * 
	 * @param angle
	 *            The angle in degrees.
	 * @return The sine of the angle.
	 */
	static double sin(double angle) {
		return Math.sin(Math.toRadians(angle));
	}

	/**
	 * Tangent using degrees.
	 * 
	 * @param angle
	 *            The angle in degrees.
	 * @return The tangent of the angle.
	 */
	public static double tan(double angle) {
		// TODO Auto-generated method stub
		return Math.tan(Math.toRadians(angle));
	}

	/**
	 * Arc-tangent using degrees.
	 * 
	 * @param angle
	 *            The slope (rise/run).
	 * @return The corresponding angle.
	 */
	public static double arctan(double slope) {
		return Math.toDegrees(Math.atan(slope));
	}

	/**
	 * Cotangent using degrees
	 * 
	 * @param angle
	 *            The angle in degrees.
	 * @return The cotangent of the angle.
	 */
	public static double cot(double angle) {
		return 1.0 / tan(angle);
	}

	/**
	 * Provides the location that exists by traveling for a certain distance at
	 * a certain angle from a certain vertex.
	 * 
	 * @param from
	 *            The vertex where traveling will start from.
	 * @param radians
	 *            The angle, in radians.
	 * @param distance
	 *            The distance that will be traveled.
	 * @return A new vertex witht the resulting coordinates.
	 */
	public static Vertex getVertexAt(Vertex from, double radians,
			double distance) {
		return new Vertex(from.x.toFloat() + distance * Math.cos(radians),
				from.y.toFloat() + distance * Math.sin(radians));
	}
}
