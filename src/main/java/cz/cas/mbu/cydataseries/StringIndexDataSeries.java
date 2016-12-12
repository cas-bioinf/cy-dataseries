package cz.cas.mbu.cydataseries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

import com.google.common.primitives.Doubles;

/**
 * A marker interface for all series whose indices (column names) are strings.
 * @author Martin
 *
 * @param <DATA>
 */
public interface StringIndexDataSeries<DATA> extends DataSeries<String, DATA> {

	@Override
	default Class<String> getIndexClass() {
		return String.class;
	}
}
