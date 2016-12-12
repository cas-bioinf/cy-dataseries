package cz.cas.mbu.cydataseries;

import java.io.File;
import java.io.IOException;

/**
 * Implement this interface to be able to save/load specific data series with the default save/load system.
 * The save/load system queries for all service implementing this interface before save/load.
 * @author MBU
 */
public interface DataSeriesStorageProvider {
	/**
	 * The class this provider handles.
	 * @return Should return the CONCRETE class, not an interface (as two implementations of the same interface should be serialized differently). The class is checked for equality. 
	 */
	Class<? extends DataSeries<?,?>> getProvidedClass();
	
	/**
	 * A user facing name for the series class this provider handles.
	 * @return
	 */
	String getSeriesTypeCaption();


	/**
	 * 
	 * @param file
	 * @param name
	 * @param oldSuid The old suid (SUID the DS was saved with) - the loaded DS should obtain a new SUID
	 * @return
	 * @throws IOException
	 */
	DataSeries<?,?> loadDataSeries(File file, String name, long oldSuid) throws IOException;
	
	/**
	 * Saves the data series to a given file
	 * @param dataSeries
	 * @param file
	 * @throws IOException
	 */
	public void saveDataSeries(DataSeries<?, ?> dataSeries, File file) throws IOException;
}
