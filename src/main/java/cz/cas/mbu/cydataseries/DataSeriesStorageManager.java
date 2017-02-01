package cz.cas.mbu.cydataseries;

import cz.cas.mbu.cydataseries.internal.DataSeriesStorageManagerImpl;

/**
 * Manages to {@link DataSeriesStorageProvider} instances.
 * The implementaion {@link DataSeriesStorageManagerImpl} is directly responsible for
 * performing save/load.
 * @author Martin
 *
 */
public interface DataSeriesStorageManager {
	/**
	 * Get provider for a given class
	 * @param seriesClass
	 * @return the provider or null, if no provider for a given class is present
	 */
	DataSeriesStorageProvider getStorageProvider(Class<?> seriesClass);
}
