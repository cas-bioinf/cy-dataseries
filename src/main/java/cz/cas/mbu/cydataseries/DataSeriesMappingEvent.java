package cz.cas.mbu.cydataseries;

import java.util.EventObject;
import java.util.List;

/**
 * Event for changes in mapping of data-series to network entities.
 * @author Martin
 *
 */
public class DataSeriesMappingEvent extends EventObject{
	public enum EventType { MAPPING_ADDED, MAPPING_REMOVED };
	
	private final EventType eventType;
	private final List<MappingDescriptor<? extends DataSeries<?, ?>>> descriptors;
	

	public DataSeriesMappingEvent(Object source, EventType eventType,
			List<MappingDescriptor<? extends DataSeries<?, ?>>> descriptors) {
		super(source);
		this.eventType = eventType;
		this.descriptors = descriptors;
	}

	public EventType getEventType() {
		return eventType;
	}

	public List<MappingDescriptor<? extends DataSeries<?, ?>>> getDescriptors() {
		return descriptors;
	}	
	
}
