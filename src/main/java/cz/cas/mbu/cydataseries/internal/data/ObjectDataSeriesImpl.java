package cz.cas.mbu.cytimeseries.internal.data;

import java.util.Arrays;
import java.util.List;

public class ObjectDataSeriesImpl<INDEX, DATA> extends AbstractListIndexDataSeries<INDEX, DATA> {
	
	DATA [][] data;
	Class<DATA> dataClass;
	
	public ObjectDataSeriesImpl(Long suid, String name, int[] rowIds, List<String> rowNames, List<INDEX> indexData,
			Class<INDEX> indexClass, DATA[][] data, Class<DATA> dataClass) {
		super(suid, name, rowIds, rowNames, indexData, indexClass);
		this.data = data;
		this.dataClass = dataClass;
	}

	@Override
	public Class<DATA> getDataClass() {
		return dataClass;
	}

	@Override
	public List<DATA> getRowData(int row) {
		return Arrays.asList(data[row]);
	}
	
	
}
