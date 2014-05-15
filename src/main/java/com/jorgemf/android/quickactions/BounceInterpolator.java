package com.jorgemf.android.quickactions;

import android.view.animation.Interpolator;

public class BounceInterpolator implements Interpolator {

	private static final double PI_4 = 4 * Math.PI;

	private static final double PI_3 = 3 * Math.PI;

	private static final double PI_2 = 2 * Math.PI;

	@Override
	public float getInterpolation(float input) {
		// sin(x^1.6*3*pi)/(x^1.6*3*pi)*-1+1
//		double x = Math.pow(input,1.2) * PI_3;
//		return (float)(Math.sin(x)/x)*-1+1;

		//sin((cos((x+1)*pi)/2+0.5)*3*pi)/((cos((x+1)*pi)/2+0.5)*3*pi)*-1+1
//		double a = (input + 1) * Math.PI;
//		double b = Math.cos(a) / 2 + 0.5;
//		double c = b * PI_4;
//		return (float) (Math.sin(c) / c) * -1 + 1;

//		sin((sin(x*pi/2) )*3*pi)/((sin(x*pi/2) )*3*pi)*-1+1 from 0 to 1
//		double a = input * Math.PI / 2;
//		double b = Math.sin(a);
//		double c = b * PI_3;
//		return (float) (Math.sin(c) / c) * -1 + 1;

		// (sin(x*3*pi)/(x*3*pi))*(-x+1)*-1+1 from 0 to 1
		double a = input * PI_3;
		double b = Math.sin(a) / a;
		double c = b * (input * -1 + 1);
		return (float) c * -1 + 1;

	}
}
