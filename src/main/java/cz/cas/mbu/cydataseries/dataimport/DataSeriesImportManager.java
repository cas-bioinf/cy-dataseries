package cz.cas.mbu.cydataseries.dataimport;

import java.util.List;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;

/**
 * Manages {@link DataSeriesImportProvider} instances.
 * @author Martin
 *
 */
public interface DataSeriesImportManager {
	List<DataSeriesImportProvider> getAllImportProviders();
}
