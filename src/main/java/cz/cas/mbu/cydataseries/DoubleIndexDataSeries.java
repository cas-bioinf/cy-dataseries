package cz.cas.mbu.cydataseries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

import com.google.common.primitives.Doubles;

/**
 * Interface for all data series whose indices are floating-point numerics.
 * @author Martin
 *
 * @param <DATA>
 */
public interface DoubleIndexDataSeries<DATA> extends DataSeries<Double, DATA> {
	public double[] getIndexArray();
	
	@Override
	default List<Double> getIndex() {
		return Doubles.asList(getIndexArray());		
	}
	
	@Override
	default Class<Double> getIndexClass() {
		return Double.class;
	}
}
