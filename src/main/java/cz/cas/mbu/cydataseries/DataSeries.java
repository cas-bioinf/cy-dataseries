package cz.cas.mbu.cydataseries;

import java.util.List;

import org.cytoscape.model.CyIdentifiable;

/**
 * Main interface for all data series.
 * @author MBU
 *
 * @param <INDEX> The type of index (independent variable)
 * @param <DATA> The type of data of the series.
 */
public interface DataSeries<INDEX, DATA> extends CyIdentifiable {
		
	  String getName();
	
	  Class<INDEX> getIndexClass();
	  
	  /**
	   * The index is a unique identifier identifying the columns of the DS table (timepoints/meauserements/...)
	   * The data is read-only, writing to the list is undefined behavior. 
	   * In other words, implementations decide, whether the changes made to the list affect
	   * the underlying index or not. 
	   * @return The index
	   */
	  List<INDEX> getIndex();
	  
	  default int getIndexCount()
	  {
		  return getIndex().size();
	  }
	  
	  /**
	   * The array is read-only, writing to the array is undefined behavior (implementations decide, whether changes to the array returned affect the actual series).
	   * @return Persistent IDs corresponding to individual rows. Those IDs can be relied on to refer to specific rows in the series.
	   * */ 
	  int[] getRowIDs();
	  
	  default int getRowID(int row)
	  {
		  return getRowIDs()[row];
	  }

	  /**
	   * Human-interpretable names for rows.
	   * The data is read-only, writing to the list is undefined behavior. 
	   */
	  List<String> getRowNames();
	  
	  default String getRowName(int row)
	  {
		  return getRowNames().get(row);
	  }
	  
	  /**
	   * Returns the row number (usable for {@link #getRowData(int)}) given a unique id. 
	   * Unlike the row IDs, the row number is not guaranteed to be persistent. 
	   * @param id
	   * @return the row corresponding to the id or -1 if no data for this id
	   */
	  int idToRow(int id); 
	  
	  int getRowCount();
	  Class<DATA> getDataClass(); 
	  
	  /**
	   * The data is read-only, writing to the list is undefined behavior. 
	   * In other words, implementations decide, whether the changes made to the list affect
	   * the underlying data or not. 
	   * @param row
	   * @return
	   */
	  List<DATA> getRowData(int row);    
  }
