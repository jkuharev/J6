package de.mz.jk.jsix.utilities;

import java.util.*;

/**
 * permutate given value sets for given attributes.
 * 
 * <h3>{@link AttributesIterator}</h3>
 * @author kuharev
 * @version 23.07.2012 16:54:18
 */
public class AttributesIterator implements Runnable
{
	public static void main(String[] args)
	{
		String[] varNames = {"A", "B", "C"};
		String[][] varValues = {
				{"1", "2", "3"},
				{"a", "b", "c"},
				{"X", "Y", "Z"}
		};

		PermutationListener l = new PermutationListener()
		{
			int loopCount = 0;
			@Override public void attributesChanged(Map<String, String> att2val)
			{
				loopCount++;
				System.out.print(loopCount + ":\t");				
				Set<String> vars = att2val.keySet();
				for(String var : vars)
				{
					System.out.print(att2val.get(var));
				}
				System.out.println();
			}
		};
		
		AttributesIterator it = new AttributesIterator(varNames, varValues, l);
		it.run();		
	}
	
	
	public static interface PermutationListener
	{
		public void attributesChanged( Map<String, String> att2val );
	}
	
	private String[] varNames = {};
	private String[][] varValues = {};
	private int nVars = 0;
	private Stack<Integer> stack = null;
	private Set<Map<String, String>> states = null;
	private PermutationListener permutationListener = null; 

	public AttributesIterator(String[] varNames, String[][] varValues, PermutationListener permutationListener)
	{
		this.varNames = varNames;
		this.varValues = varValues;
		this.nVars  = varNames.length;
		this.permutationListener = permutationListener;
	}
	
	@Override public void run()
	{
		// clear states
		states = new HashSet<Map<String, String>>();
		
		stack = new Stack<Integer>();
		while(stack.size()<nVars) stack.push( 0 );
		
		while( !stack.isEmpty() )
		{
			process();
			
			int count = stack.pop();
			int level = stack.size();
			
			count++;
			
			if( count < varValues[ level ].length )
			{
				stack.push(count);
				while(stack.size()<nVars) 
				{ 
					stack.push(0); 
				}
			}
		}			
	}

	/**
	 * @param level
	 * @param count
	 */
	private void process()
	{
		Map<String, String> varMap = new HashMap<String, String>();
		for(int i=0; i<nVars; i++)
		{
			String var = varNames[i];
			String val = varValues[i][ ( i<stack.size() ) ? stack.get(i) : 0 ];
			varMap.put( var, val );
		}
		
		if( !states.contains( varMap) )
		{
			states.add(varMap);
			
			if(permutationListener==null)
			{
				changeAttributes( varMap );
			}
			else
			{
				permutationListener.attributesChanged( varMap );
			}
		}
	}

	int loopCount = 0;
	private void changeAttributes(Map<String, String> varMap)
	{
		loopCount++;
		Set<String> vars = varMap.keySet();
		for(String var : vars)
		{
			System.out.print(varMap.get(var));
		}
		System.out.println();
	}
}