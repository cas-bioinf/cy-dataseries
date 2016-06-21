package cz.cas.mbu.cytimeseries.dataimport;

import java.util.List;

public class PreImportResults {
	private List<String> rowNames;
	private List<String> indexValues;
	private String[][] cellData;
	
	
	public PreImportResults(List<String> rowNames, List<String> indexValues, String[][] cellData) {
		super();
		this.rowNames = rowNames;
		this.indexValues = indexValues;
		this.cellData = cellData;
	}
	
	public List<String> getRowNames() {
		return rowNames;
	}
	public List<String> getIndexValues() {
		return indexValues;
	}
	public String[][] getCellData() {
		return cellData;
	}
	
	
}
