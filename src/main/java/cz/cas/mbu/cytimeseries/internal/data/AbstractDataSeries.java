package cz.cas.mbu.cytimeseries.internal.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cas.mbu.cytimeseries.DataSeries;

public abstract class AbstractDataSeries<INDEX, DATA> implements DataSeries<INDEX, DATA>{

	private List<Long> rowSuids;
	private Map<Long, Integer> rowMap;
	
	protected AbstractDataSeries(List<Long> rowSuids) {
		rowMap = new HashMap<>();
		setRowSUIDs(rowSuids);		 
	}
	
	
	
	@Override
	public List<Long> getRowSUIDs() {
		return Collections.unmodifiableList(rowSuids);
	}



	protected void setRowSUIDs(List<Long> rowSuids)
	{
		this.rowSuids = new ArrayList<>(rowSuids);
		reconstructRowMap();
	}
	
	protected void reconstructRowMap()
	{
		rowMap.clear();
		for(int i = 0; i < rowSuids.size(); i++)
		{
			rowMap.put(rowSuids.get(i), i);
		}
	}

	//TODO: functions to call when row added/deleted/suid changed, to update the suid map 
	
	@Override
	public int suidToRow(Long suid) {
		Integer index = rowMap.get(suid);
		if(index == null)
		{
			return -1;
		}
		return index;
	}

	@Override
	public int getDependentCount() {
		return rowSuids.size();
	}

}
