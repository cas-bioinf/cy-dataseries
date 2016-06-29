package cz.cas.mbu.cydataseries;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;

public interface DataSeriesMappingManager {
	static final Class<Integer> MAPPING_COLUMN_CLASS = Integer.class;
	
	void mapDataSeriesRowsToTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName, DataSeries<?, ?> ds);
	void unmapTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName);
	
	DataSeries<?,?> getMappedDataSeries(Class<? extends CyIdentifiable> targetClass, String columnName);
	
	Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>> getAllMappings();
	List<MappingDescriptor> getAllMappingDescriptors();
	
	Map<String, DataSeries<?,?>> getAllMappings(Class<? extends CyIdentifiable> targetClass);	
	<T extends DataSeries<?,?>> Map<String, T> getAllMappings(Class<? extends CyIdentifiable> targetClass, Class<T> dataSeriesClass);
	
	boolean isMappingsEmpty();
	
	/**
	 * Get all classes with at least one DS mapped.
	 * @return
	 */
	Collection<Class<? extends CyIdentifiable>> getTargetsWithMappedDataSeries();
	
	public static class MappingDescriptor 
	{
		private Class<? extends CyIdentifiable> targetClass;
		private String columnName;
		private DataSeries<?, ?> dataSeries;
		
		public MappingDescriptor(Class<? extends CyIdentifiable> targetClass, String columnName,
				DataSeries<?, ?> dataSeries) {
			super();
			this.targetClass = targetClass;
			this.columnName = columnName;
			this.dataSeries = dataSeries;
		}
		
		@Override
		public String toString()
		{
			return targetClass.getSimpleName() + ": " + columnName + " -> " + dataSeries.getName();
		}

		public Class<? extends CyIdentifiable> getTargetClass() {
			return targetClass;
		}

		public String getColumnName() {
			return columnName;
		}

		public DataSeries<?, ?> getDataSeries() {
			return dataSeries;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
			result = prime * result + ((dataSeries == null) ? 0 : dataSeries.hashCode());
			result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MappingDescriptor other = (MappingDescriptor) obj;
			if (columnName == null) {
				if (other.columnName != null)
					return false;
			} else if (!columnName.equals(other.columnName))
				return false;
			if (dataSeries == null) {
				if (other.dataSeries != null)
					return false;
			} else if (!dataSeries.equals(other.dataSeries))
				return false;
			if (targetClass == null) {
				if (other.targetClass != null)
					return false;
			} else if (!targetClass.equals(other.targetClass))
				return false;
			return true;
		}
		
		
		
	}

}
