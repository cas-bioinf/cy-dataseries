package cz.cas.mbu.cytimeseries.dataimport;

import cz.cas.mbu.cytimeseries.DataSeries;

public interface DataSeriesImportProvider {
	DataSeries<?, ?> importDataDataSeries(String name, Long suid, PreImportResults preImportResults);
	
	String getDescription();
}
