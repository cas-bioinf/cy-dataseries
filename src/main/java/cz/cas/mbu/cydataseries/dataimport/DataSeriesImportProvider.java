package cz.cas.mbu.cydataseries.dataimport;

import cz.cas.mbu.cydataseries.DataSeries;

/**
 * Any registered service of this class will be made an option for importing through the Data Series -&gt; Import dialog.
 * @author MBU
 *
 */
public interface DataSeriesImportProvider {
	DataSeries<?, ?> importDataDataSeries(String name, Long suid, PreImportResults preImportResults);
	
	String getDescription();
	
	/**
	 * Returns the class that is created when {@link #importDataDataSeries(String, Long, PreImportResults)} is called.
	 * Implementations should return interfaces rather than concrete classes. May return null, if the actual class is determined based on data.
	 * @return the most specific super interface or null. 
	 */
	Class<? extends DataSeries<?, ?>> getImportedClass();
}
