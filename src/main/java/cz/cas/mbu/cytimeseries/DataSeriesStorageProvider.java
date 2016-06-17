package cz.cas.mbu.cytimeseries;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import cz.cas.mbu.cytimeseries.internal.TimeSeriesSourceType;

/**
 * @author MBU
 *
 * @param <TARGET_CLASS>
 */
public interface DataSeriesStorageProvider {
	Class<? extends DataSeries<?,?>> getProvidedClass();

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
