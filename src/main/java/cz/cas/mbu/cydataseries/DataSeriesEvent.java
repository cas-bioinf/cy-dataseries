package cz.cas.mbu.cydataseries;

import java.util.EventObject;
import java.util.List;

public class DataSeriesEvent extends EventObject{
	public enum EventType { DS_ADDED, DS_REMOVED };
	
	private final EventType eventType;
	private final List<? extends DataSeries<?, ?>> dataSeries;
	
	public DataSeriesEvent(Object source, EventType eventType, List<? extends DataSeries<?, ?>> dataSeries) {
		super(source);
		this.eventType = eventType;
		this.dataSeries = dataSeries;
	}

	public EventType getEventType() {
		return eventType;
	}

	public List<? extends DataSeries<?, ?>> getDataSeries() {
		return dataSeries;
	}
	
	
}
