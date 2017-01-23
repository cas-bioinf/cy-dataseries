package cz.cas.mbu.cydataseries.dataimport;

import java.util.List;

/**
 * Preprocessed results for import operations. Provides the data to be imported as an array of Strings.
 * The preprocessed results have already been split into header (index values), row names and the actual data.
 * The data has also been already filtered to contain only those columns/rows the user really wants to import. 
 * How these strings are turned into the DS values is up to the specific {@link DataSeriesImportProvider} implementation.
 * @author MBU
 *
 */
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

	/**
	 * The names of the individual rows
	 */
	public List<String> getRowNames() {
		return rowNames;
	}

	/**
	 * The individual index values
	 */
	public List<String> getIndexValues() {
		return indexValues;
	}

	/**
	 * A single entry for each rowName - index combination. Use as getCellData()[row][index]
	 */
	public String[][] getCellData() {
		return cellData;
	}

	/**
	 * The original index values for the imported columns as present in the file.
	 * In user-facing messages, these are preferred to {@link #getIndexValues()}.
	 * @return
	 */
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
