package cz.cas.mbu.cydataseries.dataimport;

import java.util.List;

import cz.cas.mbu.cydataseries.DataSeries;

public interface DataSeriesImportManager {
	List<DataSeriesImportProvider> getAllImportProviders();
}
