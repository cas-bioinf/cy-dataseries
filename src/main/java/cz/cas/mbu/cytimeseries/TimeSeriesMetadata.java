package cz.cas.mbu.cytimeseries;

import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import cz.cas.mbu.cytimeseries.internal.TimeSeriesSourceType;

/**
 * Currently work in progress, awaiting the final DataSeriesTable design
 * @author MBU
 *
 * @param <TARGET_CLASS>
 */
public interface TimeSeriesMetadata<TARGET_CLASS extends CyIdentifiable> extends CyIdentifiable {
	public static final String NAME_ATTRIBUTE = "name";
	public static final String TARGET_CLASS_ATTRIBUTE = "targetClass";
	public static final String DATA_COLUMNS_ATTRIBUTE = "dataColumns";
	public static final String SOURCE_TYPE_ATTRIBUTE = "sourceType";
	public static final String TIME_POINTS_ATTRIBUTE = "timePoints";
	
	CyRow getRow();

	Class<TARGET_CLASS> getTargetClass();
	
	String getName();	
	void setName(String name);
	
	List<Double> getTimePoints();
	void setTimePoints(List<Double> timePoints);

	TimeSeriesSourceType getSourceType();
	void setSourceType(TimeSeriesSourceType sourceType);

	List<String> getDataColumns();
	void setDataColumns(List<String> dataColumns);


	double[] getData(CyRow row);


}
