package org.usfirst.frc.team2239.robot;

public class Contour {
	//All these parameters will be visible to classes within the same package as this class (see "package" above)
	final double notFoundDefault = 0;
	double x;
	double y;
	double w;
	double h;
	double area;
	
	public Contour(double[] properties) {
		if (properties.length==0) {
			this.x = notFoundDefault;
			this.y = notFoundDefault;
			this.w = notFoundDefault;
			this.h = notFoundDefault;
			this.area = notFoundDefault;
		} else if (properties.length==4) {
			this.x = properties[0];
			this.y = properties[1];
			this.w = properties[2];
			this.h = properties[3];
			this.area = notFoundDefault;
		} else if (properties.length==5) {
			this.x = properties[0];
			this.y = properties[1];
			this.w = properties[2];
			this.h = properties[3];
			this.area = properties[4];
		}
	}
	
	public Contour() {
		this.x = notFoundDefault;
		this.y = notFoundDefault;
		this.w = notFoundDefault;
		this.h = notFoundDefault;
		this.area = notFoundDefault;
	}
	
	public Contour(double x, double y, double width, double height, double area) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
		this.area = area;
	}
	
	public Contour(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
		this.area = notFoundDefault;
	}
	
	public String toString() {
		return "Countour at ("+x+", "+y+") with width: "+w+" height: "+h+" area: "+area;
	}
}
