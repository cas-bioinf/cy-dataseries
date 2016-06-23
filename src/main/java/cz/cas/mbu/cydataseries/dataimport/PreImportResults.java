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
	
	public void checkConsistentcy()
	{
		if(rowNames.size() != cellData.length)
		{
			throw new DataSeriesImportException("The size of row names (" + rowNames.size() + ") is different from the size of the data (" + cellData.length + ").");
		}
		
		for(int row = 0; row < cellData.length; row++)
		{
			if(indexValues.size() != cellData[row].length)
			{
				throw new DataSeriesImportException("The size of the index (" + indexValues.size() + ") is different from the size of row " + row + " (" + cellData[row].length + ")");
			}
		}
	}
}
