package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.List;

public class MatlabSyntaxNumberList {
	public static double DEFAULT_CONCATENATION_EPSILON = 0.0001;
	
	public static List<Double> listFromString(String str) throws NumberFormatException
	{
		return listFromString(str, DEFAULT_CONCATENATION_EPSILON);
	}

	public static List<Double> listFromString(String str, double concatenationEpsilon) throws NumberFormatException
	{
		List<Double> result = new ArrayList<>();
		String[] elements = str.split(",");
		for (String element : elements) {
			if(element.contains(":"))
			{
				String[] concatenationParts = element.split(":");
				if(concatenationParts.length > 3)
				{
					throw new NumberFormatException("Sequnce format has too many ':'s - " + element);
				}
				if(concatenationParts.length < 1)
				{
					throw new NumberFormatException("Invalid sequnce format - " + element);
				}
				double first = Double.parseDouble(concatenationParts[0]);
				double step;
				double last;
				if(concatenationParts.length == 2)
				{
					step = 1;
					last = Double.parseDouble(concatenationParts[1]);
				}
				else
				{
					step = Double.parseDouble(concatenationParts[1]);
					last = Double.parseDouble(concatenationParts[2]);					
				}
				double current = first;
				while(current < last + concatenationEpsilon)
				{
					result.add(current);
					current += step;
				}
			}
			else
			{
				result.add(Double.parseDouble(element));
			}
		}
		
		return result;
	}
	
	public static String stringFromList(List<Double> list)
	{
		return stringFromList(list, DEFAULT_CONCATENATION_EPSILON);
	}
	
	public static String stringFromList(List<Double> list, double concatenationEpsilon)
	{
		StringBuilder output = new StringBuilder();
		int currentIndex = 0;
		boolean first = true;
		while(currentIndex < list.size())
		{		
			if(!first)
			{
				output.append(",");
			}
			first = false;

			output.append(list.get(currentIndex));
			
			//If this number starts an arthmetic sequence of at least three numbers, concatenate it
			if(currentIndex + 3 < list.size())
			{
				double firstDifference = list.get(currentIndex + 1) - list.get(currentIndex);
				double secondDifference = list.get(currentIndex + 2) - list.get(currentIndex + 1);
				double thirdDifference = list.get(currentIndex + 3) - list.get(currentIndex + 2);
				if(firstDifference > 10 * concatenationEpsilon 
						&&  Math.abs(firstDifference - secondDifference) < concatenationEpsilon 
						&& Math.abs(firstDifference - thirdDifference) < concatenationEpsilon)
				{
					int numConcatenationSteps = 3;
					while(currentIndex + numConcatenationSteps + 1 < list.size() 
							&& Math.abs(firstDifference - (list.get(currentIndex + numConcatenationSteps + 1) - list.get(currentIndex + numConcatenationSteps))) < concatenationEpsilon)
					{
						numConcatenationSteps++;
					}
			
					//first element of the seqeunce (currentIndex) already added at the beginning of the loop.
					
					//Check if I need to specify step (step size 1 is implicit)
					if(Math.abs(firstDifference - 1) >= concatenationEpsilon)
					{
						output.append(":").append(firstDifference);
					}
					output.append(":").append(list.get(currentIndex + numConcatenationSteps));
					
					currentIndex += numConcatenationSteps; //move to the last added element, the last step is made outside the if
				}
			}
						
			currentIndex++;
		}
		return output.toString();
	}
}
