package cz.cas.mbu.cydataseries.dataimport;

import java.util.List;

public class PreImportResults {
	private List<String> rowNames;
	private List<String> indexValues;
	private String[][] cellData;

	private List<String> originalIndexValues;

	public PreImportResults(List<String> rowNames, List<String> indexValues, String[][] cellData,
			List<String> originalIndexValues) {
		super();
		this.rowNames = rowNames;
		this.indexValues = indexValues;
		this.cellData = cellData;
		this.originalIndexValues = originalIndexValues;
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

	public List<String> getOriginalIndexValues() {
		return originalIndexValues;
	}

	/**
	 * Checks that the pre-import data are OK.
	 * @throws DataSeriesImportException when the lengths of row names and/or index do not match the cell data.
	 */
	public void checkConsistentcy() {
		if (rowNames.size() != cellData.length) {
			throw new DataSeriesImportException("The size of row names (" + rowNames.size()
					+ ") is different from the size of the data (" + cellData.length + ").");
		}

		for (int row = 0; row < cellData.length; row++) {
			if (indexValues.size() != cellData[row].length) {
				throw new DataSeriesImportException("The size of the index (" + indexValues.size()
						+ ") is different from the size of row " + row + " (" + cellData[row].length + ")");
			}
		}
	}
}
