package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.List;

public class DataSeriesImportParameters {
	public enum IndexSource { Data, ManualAdd, ManualOverride }
	
	
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
