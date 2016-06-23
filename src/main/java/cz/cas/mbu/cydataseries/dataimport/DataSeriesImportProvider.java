package cz.cas.mbu.cydataseries.dataimport;

import cz.cas.mbu.cydataseries.DataSeries;

public interface DataSeriesImportProvider {
	DataSeries<?, ?> importDataDataSeries(String name, Long suid, PreImportResults preImportResults);
	
	String getDescription();
}
