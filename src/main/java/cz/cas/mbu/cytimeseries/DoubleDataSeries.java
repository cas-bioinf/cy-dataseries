package cz.cas.mbu.cytimeseries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public interface DoubleDataSeries<INDEX> extends DataSeries<INDEX, Double>{
	/**
	 * First index corresponds the index. 
	 * @return
	 */
	public double[][] getDataArray();
	
	default  Class<Double> getDataClass()
	{
		return Double.class;
	}
	
	default List<Double> getData(int row)
	{
		return DoubleStream.of(getDataArray()[row]).boxed().collect(Collectors.toList()); 
	}

}
