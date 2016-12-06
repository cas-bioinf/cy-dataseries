package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;

/**
 * Helper functions for import.
 */
public class ImportHelper {

	public static PreImportResults preImportFromArrayAndIndex(List<String> rawIndex, List<List<String>> records, DataSeriesImportParameters params, boolean strict) {
		List<String> index;
		List<String> rowNames;
		String[][] cellData;
		
		switch(params.getIndexSource())
		{
			case ManualAdd :
				index = params.getManualIndexValues();
				break;
			case ManualOverride :
				index = params.getManualIndexValues();
				break;
			case Data :			
				int indexStart;		
				if(params.isImportRowNames()) {
					//skip the first column dedicated to row names
					indexStart = 1;
				}
				else
				{
					indexStart = 0;
				}
				
				if(params.isImportAllColumns()) {				
					index = rawIndex.subList(indexStart, rawIndex.size());
				}
				else{
					index = new ArrayList<>();
					for(int column = indexStart; column < params.getImportedColumnIndices().size(); column++)
					{
						int columnIndex = params.getImportedColumnIndices().get(column);
						if(columnIndex < rawIndex.size())
						{
							index.add(rawIndex.get(columnIndex));													
						}
					}					
				}		
				break;
			default:
				throw new IllegalStateException("Unrecognized index source: " + params.getIndexSource());
		}
					
		rowNames = new ArrayList<>(records.size());
		
		int maxColumns = 0;
		if(params.isImportAllColumns()) {
			for(int row = 0; row < records.size(); row++) {
				int numColumns = records.get(row).size();
				if(params.isImportRowNames()) {
					numColumns -= 1; //skip the column for row names
				}
				if(strict && numColumns > index.size())
				{
					throw new DataSeriesImportException("Row " + row + " has more columns (" + numColumns + ") than there are index values (" + index.size() + ")"); 
				}
				maxColumns = Math.max(maxColumns, numColumns);
			}
			maxColumns = Math.max(maxColumns, index.size());
		}
		else
		{
			if(params.isImportRowNames()) {
				maxColumns = params.getImportedColumnIndices().size() - 1;
			}
			else
			{
				maxColumns = params.getImportedColumnIndices().size();				
			}
		}
		
		maxColumns = Math.max(maxColumns, 0); //ensure non-negative
		
		cellData = new String[records.size()][maxColumns];

		for(int row = 0; row < records.size(); row++)
		{
			List<String> currentRecord = records.get(row);
			boolean rowNameImported = false;
			int firstColumn = 0;
			if(params.isImportRowNames() && !currentRecord.isEmpty())
			{
				if(params.isImportAllColumns())
				{
					rowNames.add(currentRecord.get(0));
					rowNameImported = true;
				}
				else if (!params.getImportedColumnIndices().isEmpty() && params.getImportedColumnIndices().get(0) < currentRecord.size())
				{
					rowNames.add(currentRecord.get(params.getImportedColumnIndices().get(0)));
					rowNameImported = true;
				}
				firstColumn = 1;
			}
			
			if(!rowNameImported)
			{
				rowNames.add("Row" + Integer.toString(row + 1));
			}
					
			if(params.isImportAllColumns())
			{
				for(int column = firstColumn; column < currentRecord.size(); column ++)
				{
					cellData[row][column - firstColumn] = currentRecord.get(column);
				}
			}
			else
			{
				for(int column = firstColumn; column < params.getImportedColumnIndices().size(); column++)
				{
					int columnIndex = params.getImportedColumnIndices().get(column);
					if(columnIndex < currentRecord.size())
					cellData[row][column - firstColumn] = currentRecord.get(columnIndex);
				}
				
			}
		}		
		
		return new PreImportResults(rowNames, index, cellData, rawIndex);			
	}

	
	public static PreImportResults preImportFromArray(List<List<String>> records, DataSeriesImportParameters params, boolean strict) {
		if(records.isEmpty()) {
			return new PreImportResults(Collections.EMPTY_LIST, Collections.EMPTY_LIST, new String[][]{}, Collections.EMPTY_LIST);
		}
				
		List<String> rawIndex = records.get(0);
			
		List<List<String>> dataRecords;
		
		switch(params.getIndexSource())
		{
			case ManualAdd :
				dataRecords = records;
				break;
			case ManualOverride :
				dataRecords = records.subList(1, records.size());
				break;
			case Data :
				dataRecords = records.subList(1, records.size());
				break;
			default:
				throw new IllegalStateException("Unrecognized index source: " + params.getIndexSource());
		}		
		
		return preImportFromArrayAndIndex(rawIndex, dataRecords, params, strict);
	}
	
		
	public static PreImportResults preImport(Reader reader, FileFormatImportParameters params, DataSeriesImportParameters dataSeriesParams, boolean strict) throws IOException {
		CSVFormat format = CSVFormat.DEFAULT
								.withDelimiter(params.getSeparator())
								.withCommentMarker(params.getCommentCharacter());
		
		try (CSVParser parser = new CSVParser(reader, format)) {
			List<List<String>> recordsList = new ArrayList<>();
			
			parser.forEach(record -> {
				int targetRow = 0;
				int targetColumn = 0;
				if(params.isTransposeBeforeImport())
				{
					targetColumn = (int)record.getRecordNumber() - 1; 
				}
				else
				{					
					targetRow = (int)record.getRecordNumber() - 1;
				}
				for (int column = 0; column < record.size(); column++)
				{
					if(params.isTransposeBeforeImport())
					{
						targetRow = column;
					}
					else
					{
						targetColumn = column;
					}
					
					while(targetRow >= recordsList.size())
					{
						recordsList.add(new ArrayList<>());
					}
					while(targetColumn >= recordsList.get(targetRow).size())
					{
						recordsList.get(targetRow).add(null);
					}
					recordsList.get(targetRow).set(targetColumn, record.get(column));
				}
			});
			
			return preImportFromArray(recordsList, dataSeriesParams, strict);
		}
	}
}

