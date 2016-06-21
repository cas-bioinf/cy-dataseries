package cz.cas.mbu.cytimeseries.dataimport;

import java.util.List;

public interface DataSeriesImportManager {
	List<DataSeriesImportProvider> getAllImportProviders();
}
