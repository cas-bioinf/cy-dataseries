package cz.cas.mbu.cytimeseries.internal.data;

import java.util.List;

public abstract class AbstractListIndexDataSeries<INDEX,DATA> extends AbstractDataSeries<INDEX, DATA> {

	private List<INDEX> indexData;
	private Class<INDEX> indexClass;
	
	
	public AbstractListIndexDataSeries(List<Long> rowSuids, List<String> rowNames, List<INDEX> indexData, Class<INDEX> indexClass) {
		super(rowSuids, rowNames);
		this.indexData = indexData;
		this.indexClass = indexClass;
	}

	@Override
	public Class<INDEX> getIndexClass() {
		return indexClass;
	}

	@Override
	public List<INDEX> getIndex() {
		return indexData;
	}
	
	
}
