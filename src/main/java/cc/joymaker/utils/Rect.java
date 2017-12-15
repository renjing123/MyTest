package cc.joymaker.utils;

import cc.joymaker.weiop.open.base.utils.PolygonPoint;

public class Rect {
	private PolygonPoint a;
	private PolygonPoint b;

	public Rect(PolygonPoint x1, PolygonPoint y1) {
		this.a = x1;
		this.b = y1;
	}

	public PolygonPoint getA() {
		return a;
	}

	public PolygonPoint getB() {
		return b;
	}

	public boolean contains(PolygonPoint p) {
		return p.getLat() > Math.min(a.lat, b.lat) && p.getLng() > Math.min(a.lng, b.lng)
				&& p.getLat() < Math.max(a.lat, b.lat) && p.getLng() < Math.max(a.lng, b.lng);
	}
	
	@Override
	public String toString() {
		return a.lat + "," + a.lng +";" + b.lat + "," + b.lng ;
	}
}
