package de.mz.jk.jsix.math;
import Jama.Matrix;

/**
 * simple wrapper implementation for Principal Component Analysis methods.
 * Calculation is based on Jama Matrix Package
 * <h3>{@link XPCA}</h3>
 * @author kuharev
 * @version 18.03.2013 11:04:26
 */
public class XPCA 
{
	/**
	 * PCA by just doing Singular Value Decomposition of given data
	 * @param data row based data matrix
	 * @return eigen vectors of svd(data)
	 */
	public static double[][] getSVDEigenVectors(double[][] data)
	{
		Matrix m = new Matrix(data);
		return m.svd().getV().getArray();
	}
	
	/**
	 * PCA by generating covariance matrix of data and 
	 * passing the covariance matrix to the Eigenvalue Decomposition
	 * @param data row based data matrix
	 * @return eigen vectors of evd( cov(data) )
	 */
	public static double[][] getCovEigenVectors(double[][] data)
	{
		Matrix m = new Matrix(getCovarianceMatrix(data, null));
		return m.eig().getV().getArray();
	}
	
	/**
	 * PCA by generating covariance matrix of data and 
	 * passing the covariance matrix to the Eigenvalue Decomposition.
	 * This version works also for sparse data matrices
	 * @param data row based data matrix
	 * @return eigenvectors of eigenvector decomposition of calculated covariance matrix
	 */
	public static double[][] getCovEigenVectors(Double[][] data)
	{
		Matrix m = new Matrix(getCovarianceMatrix(data, null));
		return m.eig().getV().getArray();
	}
	
	/**
	 * calculate mean for each matrix column
	 * @param data row based matrix, that means data[0] contains first row vector
	 * @return array of column means
	 */
	public static double[] getColMeans(double[][] data)
	{
		int nrows = data.length;
		int ncols = data[0].length;
		double[] res = new double[ncols];
		// loop rows
		for(int i=0; i<nrows; i++)
		{
			// loop cols
			for(int j=0; j<ncols; j++)
			{
				// add cell to the j-th column sum
				res[j] += data[i][j];
			}
		}
		// loop sums and convert into means
		for(int j=0; j<ncols; j++) 
		{
			// mean = sum / n
			res[j] /= nrows;
		}
		return res;
	}
	
	/**
	 * Sparse mean for each matrix column.
	 * e.g.
	 * 	mean( {null, 1, 2 ,3} ) = 2; 
	 * 	mean( {null, null, null} ) = null;
	 * @param data row based matrix, that means data[0] contains first row vector
	 * @return array of column means
	 */
	public static Double[] getColMeans(Double[][] data)
	{
		int nrows = data.length;
		int ncols = data[0].length;
		
		Double[] res = new Double[ncols];
		
		for(int c=0; c<ncols; c++)
		{
			int n=0;
			double sum=0.0;
			for(int r=0; r<nrows; r++)
			{
				Double cell = data[r][c];
				if(cell!=null)
				{
					sum += cell;
					n++;
				}
			}
			if(n>0) res[c] = sum/n;
		}
		
		return res;
	}
	
	/**
	 * two-pass column based covariance as described in http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
	 * <pre>
			def two_pass_covariance(data1, data2):
				n = len(data1)
				mean1 = sum(data1) / n
				mean2 = sum(data2) / n      
				covariance = 0
				for i in range(n):
				    a = data1[i] - mean1            
				    b = data2[i] - mean2
				    covariance += a*b / n
				return covariance
		</pre>
	 * @param data the matrix (as 2d row-based array) having n columns
	 * @param colMeans vector for outputting column means
	 * @return n x n symmetric covariance matrix containing column covariances 
	 */
	public static double[][] getCovarianceMatrix( double[][] data, double[] colMeans )
	{
		int nRows = data.length;
		int nCols = data[0].length;
		double[] mean = getColMeans(data);
		double[][] cov = new double[nCols][nCols];
		
		for(int a=0; a<nCols; a++)
		{
			for(int b=a; b<nCols; b++)
			{
				for(int row=0; row<nRows; row++)
				{
					cov[a][b] += (data[row][a] - mean[a])*(data[row][b] - mean[b])/nRows;
				}
				cov[b][a] = cov[a][b]; // ensure symmetry
			}
		}
		// copy means to colMeans
		if(colMeans!=null && colMeans.length>=nCols) for(int i=0;i<nCols;i++)colMeans[i]=mean[i];
		return cov;
	}
	
	/**
	 * modified version of
	 * two-pass column based covariance 
	 * (as described in http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance).
	 * The modification ensures that the covariance for each column pair
	 * is calculated using only pairwise complete observations.
	 * ATTENTION: empty columns (having only null values) will always result in Covariance=0
	 * @param data the matrix (as 2d row-based array) having n columns
	 * @param colMeans vector for outputting column means
	 * @return n x n symmetric covariance matrix containing column covariances 
	 */
	public static double[][] getCovarianceMatrix( Double[][] data, Double[] colMeans )
	{
		int nRows = data.length;
		int nCols = data[0].length;
		Double[] mean = getColMeans(data);
		double[][] cov = new double[nCols][nCols];
		
		for(int ai=0; ai<nCols; ai++)
		{
			for(int bi=ai; bi<nCols; bi++)
			{
				int abPairs = 0;
				double abCov = 0;
				for(int row=0; row<nRows; row++)
				{
					Double a = data[row][ai];
					Double b = data[row][bi];
					if( a!=null && b!=null )
					{
						abCov += (data[row][ai] - mean[ai])*(data[row][bi] - mean[bi]);
						abPairs++;
					}					
				}
				abCov = (abPairs>0) ? abCov / abPairs : 0;
				cov[ai][bi] = abCov;
				cov[bi][ai] = abCov;
			}
		}
		// copy means to colMeans
		if(colMeans!=null && colMeans.length>=nCols) for(int i=0;i<nCols;i++)colMeans[i]=mean[i];
		return cov;
	}
}
