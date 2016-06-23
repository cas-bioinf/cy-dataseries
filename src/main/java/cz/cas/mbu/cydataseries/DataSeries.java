package cz.cas.mbu.cydataseries;

import java.util.List;

import org.cytoscape.model.CyIdentifiable;

public interface DataSeries<INDEX, DATA> extends CyIdentifiable {
		
	  String getName();
	
	  Class<INDEX> getIndexClass();
	  
	  /**
	   * The index is a unique identifier identifying the columns of the DS table (timepoints/meauserements/...)
	   * The data is read-only, writing to the list is undefined behavior. 
	   * In other words, implementations decide, whether the changes made to the list affect
	   * the underlying index or not. 
	   * @param row
	   * @return
	   */
	  List<INDEX> getIndex();
	  
	  default int getIndexCount()
	  {
		  return getIndex().size();
	  }
	  
	  /**
	   * The list is read-only, writing to the list is undefined behavior.
	   * */ 
	  int[] getRowIDs();
	  
	  default int getRowID(int row)
	  {
		  return getRowIDs()[row];
	  }

	  /**
	   * Human-interpretable names for rows
	   * @return
	   */
	  List<String> getRowNames();
	  
	  default String getRowName(int row)
	  {
		  return getRowNames().get(row);
	  }
	  
	  /**
	   *  
	   * @param suid
	   * @return the row corresponding to the SUID or -1 if no data for this suid
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
