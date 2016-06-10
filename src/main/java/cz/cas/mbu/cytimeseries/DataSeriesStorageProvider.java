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

	DataSeries<?,?> loadDataSeries(File file, String name, long suid) throws IOException;
	public void saveDataSeries(DataSeries<?, ?> dataSeries, File file) throws IOException;
}
