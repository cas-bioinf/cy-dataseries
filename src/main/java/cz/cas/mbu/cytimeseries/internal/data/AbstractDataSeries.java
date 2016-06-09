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
	
	private List<String> rowNames;
	
	protected AbstractDataSeries(List<Long> rowSuids, List<String> rowNames) {
		rowMap = new HashMap<>();
		setRowSUIDs(rowSuids);		
		
		this.rowNames = new ArrayList<>(rowNames);
	}
	
	
	
	@Override
	public List<Long> getRowIDs() {
		return Collections.unmodifiableList(rowSuids);
	}

	
	
	@Override
	public Long getRowID(int row) {
		return rowSuids.get(row);
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
	public int idToRow(Long suid) {
		Integer index = rowMap.get(suid);
		if(index == null)
		{
			return -1;
		}
		return index;
	}

	
	@Override
	public List<String> getRowNames() {
		return Collections.unmodifiableList(rowNames);
	}


	@Override
	public String getRowName(int row) {
		return rowNames.get(row);
	}
	

	@Override
	public int getDependentCount() {
		return rowSuids.size();
	}

}
