package cz.cas.mbu.cydataseries.dataimport;

import cz.cas.mbu.cydataseries.DataSeries;

/**
 * Any registered service of this class will be made an option for importing through the File -&gt; Import -&gt; Data Series dialogs.
 * This abstraction covers both tabular files (.CSV,.TSV) and SOFT files.
 * @author MBU
 *
 */
public interface DataSeriesImportProvider {
	/**
	 * Perform the actual imported from preprocessed results
	 * @param name name of the DS to create
	 * @param suid SUID of the DS to create.
	 * @param preImportResults preprocessed results
	 * @return
	 */
	DataSeries<?, ?> importDataSeries(String name, Long suid, PreImportResults preImportResults);
	
	/**
	 * A user-facing description of the data series this provider handles.
	 * @return
	 */
	String getDescription();
	
	/**
	 * Returns the class that is created when {@link #importDataSeries(String, Long, PreImportResults)} is called.
	 * Implementations should return interfaces rather than concrete classes. May return null, if the actual class is determined based on data.
	 * @return the most specific super interface or null. 
	 */
	Class<? extends DataSeries<?, ?>> getImportedClass();
}
