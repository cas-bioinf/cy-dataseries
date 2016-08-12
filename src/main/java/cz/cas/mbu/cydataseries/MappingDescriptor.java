package cz.cas.mbu.cydataseries;

import java.util.Comparator;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;

public class MappingDescriptor<T extends DataSeries<?,?>> 
{
	private Class<? extends CyIdentifiable> targetClass;
	private String columnName;
	private T dataSeries;
	
	public MappingDescriptor(Class<? extends CyIdentifiable> targetClass, String columnName,
			T dataSeries) {
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

	public T getDataSeries() {
		return dataSeries;
	}

	/**
	 * Gets the index of the data series row corresponding to a given CyRow under this mapping
	 * @param row
	 * @return -1 if there is no row for this CyRow, or the index of the associated row.
	 * @throws DataSeriesException if the CyRow contains an invalid ID.
	 */
	public int getDataSeriesRow(CyRow row)
	{
		Integer rowID = row.get(columnName, DataSeriesMappingManager.MAPPING_COLUMN_CLASS);
		if(rowID == null)
		{
			return -1;
		}
		int tsRow = dataSeries.idToRow(rowID);
		if(tsRow < 0)
		{
			throw new DataSeriesException("Requesting non existent row id (" + tsRow + ") from Data Series " + dataSeries.getName());			
		}
		return tsRow;
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
		MappingDescriptor<?> other = (MappingDescriptor<?>) obj;
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
	
	public static class DefaultComparator<T extends DataSeries<?, ?>> implements Comparator<MappingDescriptor<T>>
	{

		@Override
		public int compare(MappingDescriptor<T> o1, MappingDescriptor<T> o2) {
			int nameCompare = (o1.dataSeries.getName().compareTo(o2.getDataSeries().getName()));
			if(nameCompare != 0)
			{
				return nameCompare;
			}
			
			int columnNameCompare = o1.getColumnName().compareTo(o2.getColumnName());
			if(columnNameCompare != 0)
			{
				return columnNameCompare;
			}
			
			return o1.getTargetClass().getSimpleName().compareTo(o2.getTargetClass().getSimpleName());
		}
		
	}
	
}