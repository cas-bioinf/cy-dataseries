package cz.cas.mbu.cytimeseries.internal.data;

import java.util.HashMap;
import java.util.Map;

import cz.cas.mbu.cytimeseries.DataSeries;

public abstract class AbstractDataSeries<INDEX, DATA> implements DataSeries<INDEX, DATA>{

	private int[] rowIds;
	private Map<Integer, Integer> rowMap;
	
	private String[] rowNames;
		
	private final Long suid;
	private final String name;
	
	protected AbstractDataSeries(Long suid, String name, int[] rowIds, String[] rowNames) {
		rowMap = new HashMap<>();
		setRowIDs(rowIds);		
		
		this.rowNames = rowNames;
		this.suid = suid;
		this.name = name;
	}
	
	
	
	@Override
	public Long getSUID() {
		return suid;
	}



	@Override
	public String getName() {
		return name;
	}



	@Override
	public int[] getRowIDs() {
		return rowIds;
	}

	
	
	@Override
	public int getRowID(int row) {
		return rowIds[row];
	}



	protected void setRowIDs(int[] rowIds)
	{
		this.rowIds = rowIds;
		reconstructRowMap();
	}
	
	protected void reconstructRowMap()
	{
		rowMap.clear();
		for(int i = 0; i < rowIds.length; i++)
		{
			rowMap.put(rowIds[i], i);
		}
	}

	//TODO: functions to call when row added/deleted/suid changed, to update the suid map 
	
	@Override
	public int idToRow(int id) {
		Integer index = rowMap.get(id);
		if(index == null)
		{
			return -1;
		}
		return index;
	}

	
	@Override
	public String[] getRowNames() {
		return rowNames;
	}



	@Override
	public int getRowCount() {
		return rowIds.length;
	}

}
