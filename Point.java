import java.io.Serializable;

public class Point implements Serializable {

	public double x;
	public double y;

	Point() {
		x = 0;
		y = 0;
	}

	Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}

	public boolean equal(Object p) {
		Point point = null;
		if (p instanceof Point) {
			point = (Point) p;
		}
		return point.x == this.x && point.y == this.y;
	}
}
