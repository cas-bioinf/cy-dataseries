package cz.cas.mbu.cydataseries;

/**
 * Manages to {@link DataSeriesStorageProvider} instances.
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
