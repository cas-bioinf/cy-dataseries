package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.List;

/**
 * A collection of all import parameters that are common to all import methods.
 * @author MBU
 *
 */
public class DataSeriesImportParameters {
	public enum IndexSource {
		/**
		 * Index is first row of the data
		 */
		Data, 
		/**
		 * Index is given in {@link DataSeriesImportParameters#getManualIndexValues()} and data contain no index.
		 */
		ManualAdd, 
		/**
		 * Index is given in {@link DataSeriesImportParameters#getManualIndexValues()} and data also contains index - the first row of data should be ignored..
		 */
		ManualOverride 
		}
	
	
	private IndexSource indexSource;
	private List<String> manualIndexValues;
	private boolean importRowNames;
	
	private boolean importAllColumns = true;
	private List<Integer> importedColumnIndices;
	
	public IndexSource getIndexSource() {
		return indexSource;
	}
	public void setIndexSource(IndexSource indexSource) {
		this.indexSource = indexSource;
	}
	public List<String> getManualIndexValues() {
		return manualIndexValues;
	}
	public void setManualIndexValues(List<String> manualIndexValues) {
		this.manualIndexValues = manualIndexValues;
	}
	public boolean isImportRowNames() {
		return importRowNames;
	}
	public void setImportRowNames(boolean importRowNames) {
		this.importRowNames = importRowNames;
	}
	public boolean isImportAllColumns() {
		return importAllColumns;
	}
	public void setImportAllColumns(boolean importAllColumns) {
		this.importAllColumns = importAllColumns;
	}
	public List<Integer> getImportedColumnIndices() {
		return importedColumnIndices;
	}
	public void setImportedColumnIndices(List<Integer> importedColumnIndices) {
		this.importedColumnIndices = importedColumnIndices;
	}
	
	
	
}
