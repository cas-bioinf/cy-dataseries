package cz.cas.mbu.cydataseries;

import java.io.File;
import java.io.IOException;

/**
 * @author MBU
 *
 * @param <TARGET_CLASS>
 */
public interface DataSeriesStorageProvider {
	Class<? extends DataSeries<?,?>> getProvidedClass();
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
	public void saveDataSeries(DataSeries<?, ?> dataSeries, File file) throws IOException;
}
