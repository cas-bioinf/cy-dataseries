package cz.cas.mbu.cydataseries;

/**
 * Register a service of this type to listen for addition/removal of mapping of data series to network entities.
 * @author Martin
 *
 */
@FunctionalInterface
public interface DataSeriesMappingListener {
	void handleEvent(DataSeriesMappingEvent evt);
}
