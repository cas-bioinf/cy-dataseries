package cz.cas.mbu.cydataseries;

/**
 * Register a service of this type to listen for series addition/removal.
 * @author Martin
 *
 */
@FunctionalInterface
public interface DataSeriesListener {
	void handleEvent(DataSeriesEvent event);
}
