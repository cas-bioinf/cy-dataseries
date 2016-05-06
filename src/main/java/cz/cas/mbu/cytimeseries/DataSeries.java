package cz.cas.mbu.cytimeseries;

import java.util.List;

import org.cytoscape.model.CyIdentifiable;

public interface DataSeries<INDEX, DATA> {
	  Class<INDEX> getIndexClass();
	  
	  /**
	   * The data is read-only, writing to the list is undefined behavior. 
	   * In other words, implementations decide, whether the changes made to the list affect
	   * the underlying index or not. 
	   * @param row
	   * @return
	   */
	  List<INDEX> getIndex();
	  
	  List<Long> getRowSUIDs();
	  int suidToRow(Long suid); //returns -1 if no data for this suid
	  
	  int getDependentCount();
	  Class<DATA> getDataClass(); 
	  
	  /**
	   * The data is read-only, writing to the list is undefined behavior. 
	   * In other words, implementations decide, whether the changes made to the list affect
	   * the underlying data or not. 
	   * @param row
	   * @return
	   */
	  List<DATA> getData(int row);    
  }
