package cz.cas.mbu.cytimeseries.dataimport;

import cz.cas.mbu.cytimeseries.DataSeries;

public interface DataSeriesImportProvider {
	DataSeries<?, ?> importDataDataSeries(PreImportResults preImportResults);
}
