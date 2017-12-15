package cc.joymaker.utils;

public class PolygonPoint {
	double lng; // y
	double lat; // x

	public PolygonPoint(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}
	@Override
	public String toString() {
		return lng + ";" + lat;
	}
}
