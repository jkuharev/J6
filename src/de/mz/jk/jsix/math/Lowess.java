package de.mz.jk.jsix.math;

import de.mz.jk.jsix.plot.pt.XYPlotter;

/**
Java implementation of the original LOWESS algorithm published as indicated:
	Cleveland, W. S. (1979). Robust Locally Weighted Regression and Smoothing Scatterplots.
	Journal of the American Statistical Association, 74(368), pp. 829-836. American Statistical Association.

 * <h3>{@link Lowess}</h3>
 * @author kuharev
 * @version 27.06.2012 16:48:35
 */
public class Lowess
{
	// restrict to local initialization
	private Lowess()
	{}

	private double ycurrent = 0.0;

	/**
	 * See class doc
	 * 
	 * @param x
	 * @param y
	 * @param n
	 * @param xs
	 * @param nleft
	 * @param nright
	 * @param w
	 * @param userw
	 * @param rw
	 * @return boolean
	 */
	private boolean lowest(double[] x, double[] y, int n, double xs, int nleft, int nright, double[] w, boolean userw, double[] rw)
	{
		int nrt, j;
		double a, b, c, h, h1, h9, r, range;
		boolean ok;

		range = x[n - 1] - x[0];
		h = Math.max(xs - x[nleft], x[nright] - xs);
		h9 = 0.999 * h;
		h1 = 0.001 * h;
		/* sum of weights */
		a = 0.0;
		for (j = nleft; j < n; j++) {
			/* compute weights */
			/* (pick up all ties on right) */
			w[j] = 0.;
			r = Math.abs(x[j] - xs);
			if (r <= h9) {
				if (r <= h1) {
					w[j] = 1.;
				} else {
					w[j] = cube(1. - cube(r / h));
				}

				if (userw) {
					w[j] *= rw[j];
				}
				a += w[j];
			} else if (x[j] > xs) {
				break;
			}
		}

		/* rightmost pt (may be greater */
		/* than nright because of ties) */

		nrt = j - 1;
		// nrt = j;

		if (a <= 0.) {
			ok = false;
		} else {
			ok = true;

			/* weighted least squares */
			/* make sum of w[j] == 1 */

			for (j = nleft; j <= nrt; j++) {
				w[j] /= a;
			}
			if (h > 0.) {
				a = 0.0;

				/* use linear fit */
				/* weighted center of x values */

				for (j = nleft; j <= nrt; j++) {
					a += w[j] * x[j];
				}
				b = xs - a;
				c = 0.;
				for (j = nleft; j <= nrt; j++) {
					c += w[j] * square(x[j] - a);
				}
				if (Math.sqrt(c) > 0.001 * range) {
					b /= c;

					/* points are spread out */
					/* enough to compute slope */

					for (j = nleft; j <= nrt; j++) {
						w[j] *= (b * (x[j] - a) + 1.);
					}
				}
			}
			ycurrent = 0.0;
			for (j = nleft; j <= nrt; j++) {
				ycurrent += w[j] * y[j];
			}

		}
		return ok;
	}

	/**
	 * See class doc
	 * 
	 * @param x
	 * @param y
	 * @param bandWidth degree of smooothing (range 0.0 - 1.0 ... use default 0.5)
	 * @param robustnessIter no of iterations (recommended range 0-5)
	 * @param delta step size (recommended range 0.0-3.0)
	 * @param ys
	 * @param rw
	 * @param res
	 */
	private void lowess(double[] x, double[] y, double bandWidth, int robustnessIter, double delta, double[] ys, double[] rw, double[] res)
	{
		int i, iter, j, last, m1, m2, nleft, nright, ns, n;
		boolean ok;
		double alpha, c1, c9, cmad, cut, d1, d2, denom, r, sc;
		n = y.length;
		if (n < 2) {
			ys[0] = y[0];
			return;
		}

		/**
		 * 
		 * convert the relative bandWidth into concrete number of points 
		 */
		int f = (int) ((double) n * bandWidth); 
		
		/* nleft, nright, last, etc. must all be shifted to get rid of these: */

		/* at least two, at most n points */
		ns = Math.max(2, Math.min(n, f));

		/* robustness iterations */

		for (iter = 0; iter <= robustnessIter; iter++) {
			nleft = 0;
			nright = ns - 1;
			last = -1; /* index of prev estimated point */
			i = 0; /* index of current point */

			for (;;) {
				if (nright < n - 1) {
					/* move nleft, nright to right */
					/* if radius decreases */
					d1 = x[i] - x[nleft];
					d2 = x[nright + 1] - x[i];
					/* if d1 <= d2 with */
					/* x[nright+1] == x[nright], */
					/* lowest fixes */

					if (d1 > d2) 
					{
						/* radius will not */
						/* decrease by */
						/* move right */
						nleft++;
						nright++;
						continue;
					}
				}

				/* fitted value at x[i] */

				ok = lowest(x, y, n, x[i], nleft, nright, res, iter > 0, rw);
				ys[i] = ycurrent;

				if (!ok) {
					ys[i] = y[i];
				}
				/* all weights zero */
				/* copy over value (all rw==0) */

				if (last < i - 1) {
					denom = x[i] - x[last];

					/* skipped points -- interpolate */
					/* non-zero - proof? */

					for (j = last + 1; j < i; j++) {
						alpha = (x[j] - x[last]) / denom;
						ys[j] = alpha * ys[i] + (1. - alpha) * ys[last];
					}
				}

				/* last point actually estimated */
				last = i;

				/* x coord of close points */
				cut = x[last] + delta;
				for (i = last + 1; i < n; i++) {
					if (x[i] > cut)
						break;
					if (x[i] == x[last]) {
						ys[i] = ys[last];
						last = i;
					}
				}
				i = Math.max(last + 1, i - 1);
				if (last >= n - 1)
					break;
			}
			/* residuals */
			for (i = 0; i < n; i++) {
				res[i] = y[i] - ys[i];
			}

			/* overall scale estimate */
			sc = 0.;
			for (i = 0; i < n; i++) {
				sc += Math.abs(res[i]);
			}
			sc /= n;

			/* compute robustness weights */
			/*
			 * Note: The following code, biweight_{6 MAD|Ri|} is also used in stl(), loess and several other places. -->
			 * should provide API here (MM)
			 */
			for (i = 0; i < n; i++) {
				rw[i] = Math.abs(res[i]);
			}
			quicksort(rw);
			/* Compute cmad := 6 * median(rw[], n) ---- */
			m1 = n / 2;
			m2 = n - m1 - 1;

			if (n % 2 == 0) {
				cmad = 3. * (rw[m1] + rw[m2]);
			} else { /* n odd */
				cmad = 6. * rw[m1];
			}
			/* effectively zero */
			if (cmad < 1.0E-7 * sc) {
				break;
			}
			c9 = 0.999 * cmad;
			c1 = 0.001 * cmad;
			for (i = 0; i < n; i++) {
				r = Math.abs(res[i]);
				if (r <= c1) {
					rw[i] = 1.;
				} else if (r <= c9) {
					rw[i] = square(1. - square(r / cmad));
				} else {
					rw[i] = 0.;
				}
			}
		}
	}

	private double cube(double x)
	{
		return x * x * x;
	}

	private double square(double x)
	{
		return x * x;
	}

	private void quicksort(double[] g)
	{
		quicksort( g, 0, g.length - 1 );
	}

	private void quicksort(double[] g, int lo, int hi)
	{
		// lo is the lower index, hi is the upper index
		// of the region of array a that is to be sorted
		int i = lo, j = hi;
		double h;
		double x = g[(lo + hi) / 2];

		do
		{
			while (g[i] < x) i++;
			while (g[j] > x) j--;
			if (i <= j) 
			{
				h = g[i];
				g[i] = g[j];
				g[j] = h;
				i++;
				j--;
			}
		}
		while (i <= j);

		if (lo < j) quicksort(g, lo, j);
		if (i < hi)	quicksort(g, i, hi);
	}
	
	/**
	 * wrapping original implementation
	 * @param x
	 * @param y
	 * @param bandWidth
	 * @param robustnessIter
	 * @return smoothed y values
	 */
	private double[] lowessXYWI(double[] x, double[] y, double bandWidth, int robustnessIter)
	{
		double[] ys = new double[x.length];
		double[] rw = new double[x.length];
		double[] res = new double[x.length];
		lowess(x, y, bandWidth, robustnessIter, 0.0, ys, rw, res);
		return ys;
	}
	
	private double[] lowessXY(double[] x, double[] y)
	{
		double[] ys = new double[x.length];
		double[] rw = new double[x.length];
		double[] res = new double[x.length];
		lowess( x, y, 0.3, 0, 0.0, ys, rw, res );
		return ys;
	}

	/**
	 * Locally Weighted Scatterplot Smoothing Regression
	 * with default values of bandWidth=0.3 and robustnessIter=2
	 * @param x
	 * @param y
	 * @return smoothed y values
	 */
	public static double[] lowess(double[] x, double[] y)
	{
		return new Lowess().lowessXY( x, y );
	}	
	
	/**
	 * Locally Weighted Scatterplot Smoothing Regression
	 * @param x
	 * @param y
	 * @param bandWidth
	 * @param robustnessIter
	 * @return smoothed y values
	 */
	public static double[] lowess(double[] x, double[] y, double bandWidth, int robustnessIter)
	{
		return new Lowess().lowessXYWI( x, y, bandWidth, robustnessIter );
	}

	public static void main(String[] args)
	{
		double[] x = new double[] { 1, 2, 3, 4, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 10, 12, 14, 50 };
		double[] y = new double[] { 18, 2, 15, 6, 10, 4, 16, 11, 7, 3, 14, 17, 20, 12, 9, 13, 1, 8, 5, 19 };
		XYPlotter p = new XYPlotter(1024, 768);
		p.setPointStyle("bigdots");
		p.plotXY(x, y, "wolke", false);
		p.plotXY( x, Lowess.lowess( x, y, 0.7, 2 ), "lowess", true );
		p.plotXY( x, Lowess.lowess( x, y ), "lowess 2", true );
	}
}
