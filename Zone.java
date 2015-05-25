import java.io.Serializable;

public class Zone implements Serializable {

	Point bottomLeft;
	Point bottomRight;
	Point topRight;
	Point topLeft;
	Point centre;

	public Zone(Point bottomLeft, Point topRight) {

		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
		calcOthers();

	}

	public void calcOthers() {
		bottomRight = new Point(topRight.x, bottomLeft.y);
		topLeft = new Point(bottomLeft.x, topRight.y);
		double centrex = bottomLeft.x + ((topRight.x - bottomLeft.x) / 2);
		double centrey = bottomLeft.y + ((topRight.y - bottomLeft.y) / 2);
		centre = new Point(centrex, centrey);
	}

	@Override
	public String toString() {
		return "bottomLeft= " + bottomLeft + ", topRight= " + topRight;
				
	}

	public Zone split() {

		Zone newZone = null;
		if (height() != width()) {
			newZone = new Zone(new Point(bottomLeft.x, centre.y), new Point(
					topRight.x, topRight.y));
			bottomLeft = new Point(bottomLeft.x, bottomLeft.y);
			topRight = new Point(topRight.x, centre.y);
			calcOthers();
			return newZone;

		} else {
			newZone = new Zone(new Point(centre.x, bottomLeft.y), new Point(
					topRight.x, topRight.y));
			bottomLeft = new Point(bottomLeft.x, bottomLeft.y);
			topRight = new Point(centre.x, topRight.y);
			calcOthers();
			return newZone;

		}
	}

	public void merge(Zone zone) {
		Zone first = this;
		Zone second = zone;
		if (!isSquare() && !zone.isSquare()) {
			if (bottomLeft.x > zone.bottomLeft.x) {
				Zone temp = first;
				first = second;
				second = temp;
			}

		} else if (isSquare() && zone.isSquare()) {
			if (bottomLeft.y > zone.bottomLeft.y) {
				Zone temp = first;
				first = second;
				second = temp;
			}
		} else {

			System.out.println("Bad merge");
			return;
		}

		bottomLeft = first.bottomLeft;
		topRight = second.topRight;
		calcOthers();
	}

	public boolean contains(Point p) {
		return bottomLeft.x <= p.x && p.x <= topRight.x && bottomLeft.y <= p.y
				&& p.y <= topRight.y;
	}

	public double height() {
		return topRight.y - bottomLeft.y;
	}

	public double width() {
		return topRight.x - bottomLeft.x;
	}

	public boolean isSquare() {
		return height() == width();
	}

	public boolean surfaceEqual(Zone zone) {
		return height() == zone.height() && width() == zone.width();
	}

	public double getSurfaceArea() {
		return height() * width();
	}

}
