package cz.cas.mbu.cytimeseries;

import java.io.File;
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
public interface DataSeriesStorageProvider<T extends DataSeries<?,?>> {
	Class<T> getProvidedClass();

	T loadDataSeries(File file, String name, long suid);
	void saveDataSeries(T dataSeries, File file);
}
