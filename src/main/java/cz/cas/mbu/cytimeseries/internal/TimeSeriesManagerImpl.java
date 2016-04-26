package cz.cas.mbu.cytimeseries.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.SUIDFactory;

import cz.cas.mbu.cytimeseries.TimeSeries;
import cz.cas.mbu.cytimeseries.TimeSeriesException;
import cz.cas.mbu.cytimeseries.TimeSeriesManager;

public class TimeSeriesManagerImpl implements TimeSeriesManager{

	private final CyTableManager cyTableManager;
	private final CyTableFactory cyTableFactory;
	
	private final CyNetworkTableManager cyNetworkTableManager;
	
	 
	
	public TimeSeriesManagerImpl(CyTableManager cyTableManager, CyTableFactory cyTableFactory,
			CyNetworkTableManager cyNetworkTableManager) {
		super();
		this.cyTableManager = cyTableManager;
		this.cyTableFactory = cyTableFactory;
		this.cyNetworkTableManager = cyNetworkTableManager;
	}

	@Override
	public CyTable getTimeSeriesTable(CyNetwork network) {
		//return network.getTable(TimeSeries.class, TimeSeriesManager.TIME_SERIES_TABLE_CONTEXT);
		//TODO - a temporary work around for not being able to store custom tables
		CyTable table = network.getTable(TimeSeries.class, TimeSeriesManager.TIME_SERIES_TABLE_CONTEXT);
		if(table != null)
		{
			return table;
		}
		else
		{
			for(CyTable candidateTable : cyTableManager.getGlobalTables())
			{
				if(candidateTable.getTitle().equals("Time Series Definitions"))
				{
					return candidateTable;
				}
			}
			return null;
		}
	}

	protected CyTable createTimeSeriesTable(CyNetwork network)
	{
		CyTable timeSeriesTable = cyTableFactory.createTable("Time Series Definitions", "SUID", Long.class, true /*public*/, true/*isMutable*/);
		cyNetworkTableManager.setTable(network, TimeSeries.class, TimeSeriesManager.TIME_SERIES_TABLE_CONTEXT, timeSeriesTable);
		cyTableManager.addTable(timeSeriesTable);			
		
		//Add default columns
		timeSeriesTable.createColumn(TimeSeries.NAME_ATTRIBUTE, String.class, true /*The column is immutable*/);
		timeSeriesTable.createColumn(TimeSeries.TARGET_CLASS_ATTRIBUTE, String.class, true /*The column is immutable*/);
		timeSeriesTable.createColumn(TimeSeries.SOURCE_TYPE_ATTRIBUTE, String.class, true /*The column is immutable*/);
		timeSeriesTable.createListColumn(TimeSeries.TIME_POINTS_ATTRIBUTE, Double.class, true /*The column is immutable*/);
		timeSeriesTable.createListColumn(TimeSeries.DATA_COLUMNS_ATTRIBUTE, String.class, true /*The column is immutable*/);
		
		return timeSeriesTable;
	}
	
	@Override
	public CyTable ensureTimeSeriesTableExists(CyNetwork network) {
		CyTable timeSeriesTable = getTimeSeriesTable(network);
		if(timeSeriesTable == null)			
		{
			timeSeriesTable = createTimeSeriesTable(network);
		}
		
		return timeSeriesTable;
	}

	private String stringForClass(Class<? extends CyIdentifiable> targetClass)
	{
		if(targetClass.equals(CyNode.class))
		{
			return "Node";
		}
		else if(targetClass.equals(CyEdge.class))
		{
			return "Edge";
		}
		else
		{
			return targetClass.getName();
		}
	}
	
	private Class<? extends CyIdentifiable> classForString(String s)
	{
		if(s.equals("Node"))
		{
			return CyNode.class;
		}
		else if(s.equals("Edge"))
		{
			return CyEdge.class;
		}
		else
		{
			try {
				Class<?> retClass = Class.forName(s);
				if(CyIdentifiable.class.isAssignableFrom(retClass)){
					return (Class<? extends CyIdentifiable>)retClass;
				}
				else
				{
					throw new TimeSeriesException("Class '" + s + "' is not a valid target class - it is not identifiable.");					
				}
			}
			catch (Exception ex)
			{
				throw new TimeSeriesException("Could not load target class '" + s + "' for time series.", ex);
			}
		}
				
	}
	
	@Override
	public <TARGET_TYPE extends CyIdentifiable> TimeSeries<TARGET_TYPE> createTimeSeries(CyNetwork network, Class<TARGET_TYPE> targetClass) {
		CyTable timeSeriesTable = ensureTimeSeriesTableExists(network);
		long newSUID = SUIDFactory.getNextSUID();
		//getRow creates the row if it does not exist
		CyRow row = timeSeriesTable.getRow(newSUID);
		row.set(TimeSeries.TARGET_CLASS_ATTRIBUTE, stringForClass(targetClass));
		TimeSeriesImpl<TARGET_TYPE> timeSeries = new TimeSeriesImpl<TARGET_TYPE>(row, targetClass);		
		return timeSeries;
	}

	@Override
	public <TARGET_TYPE extends CyIdentifiable> List<TimeSeries<TARGET_TYPE>> getAllTimeSeries(CyNetwork network, Class<TARGET_TYPE> targetClass) {	
		CyTable timeSeriesTable = getTimeSeriesTable(network);
		if(timeSeriesTable == null) {
			return Collections.EMPTY_LIST;
		}
			
		Collection<CyRow> rows = timeSeriesTable.getMatchingRows(TimeSeries.TARGET_CLASS_ATTRIBUTE, stringForClass(targetClass)); 
		List<TimeSeries<TARGET_TYPE>> result = new ArrayList<>(rows.size());
		for(CyRow r : rows)	{
			result.add(new TimeSeriesImpl<TARGET_TYPE>(r, targetClass));
		}
		return result;
	}

	@Override
	public <TARGET_TYPE extends CyIdentifiable> void deleteTimeSeries(TimeSeries<TARGET_TYPE> series) {
		series.getRow().getTable().deleteRows(Collections.singletonList(series.getSUID()));		
	}

	
}
