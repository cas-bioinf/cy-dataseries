package cz.cas.mbu.cytimeseries.internal.data;

import java.util.Arrays;
import java.util.List;

public class ObjectDataSeriesImpl<INDEX, DATA> extends AbstractListIndexDataSeries<INDEX, DATA> {
	
	DATA [][] data;
	Class<DATA> dataClass;
	
	
	public ObjectDataSeriesImpl(List<Long> rowSuids, List<INDEX> indexData, Class<INDEX> indexClass, DATA[][] data, Class<DATA> dataClass) {
		super(rowSuids, indexData, indexClass);
		this.data = data;
		this.dataClass = dataClass;
	}

	@Override
	public Class<DATA> getDataClass() {
		return dataClass;
	}

	@Override
	public List<DATA> getData(int row) {
		return Arrays.asList(data[row]);
	}
	
	
}
