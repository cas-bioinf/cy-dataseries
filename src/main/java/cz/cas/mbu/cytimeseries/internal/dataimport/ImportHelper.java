package cz.cas.mbu.cytimeseries.internal.dataimport;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import cz.cas.mbu.cytimeseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cytimeseries.dataimport.PreImportResults;

public class ImportHelper {
	
	private static PreImportResults preImportFromArray(List<List<String>> records, ImportParameters params, boolean strict)
	{		
		if(records.isEmpty())
		{
			return new PreImportResults(Collections.EMPTY_LIST, Collections.EMPTY_LIST, new String[][]{});
		}
		
		List<String> index;
		List<String> rowNames;
		String[][] cellData;
		int dataStartIndex;
		if(params.isManualIndexData())
		{
			index = params.getManualIndexValues();
			dataStartIndex = 0;
		}
		else
		{
			
			if(params.isImportRowNames())
			{
				//skip the first column dedicated to row names
				index = new ArrayList<String>(records.get(0).size() - 1); 
				for(int column = 1; column < records.get(0).size(); column++)
				{
					index.add(records.get(0).get(column));
				}
			}
			else
			{
				index = new ArrayList<String>(records.get(0).size() - 1);
				records.get(0).forEach(x -> index.add(x));					
			}
			dataStartIndex = 1;				
		}
		
		rowNames = new ArrayList<>(records.size() - dataStartIndex);
		
		int maxColumns = 0;
		for(int row = dataStartIndex; row < records.size(); row++)
		{
			int numColumns = records.get(row).size();
			if(params.isImportRowNames())
			{
				numColumns -= 1; //skip the column for row names
			}
			if(strict && numColumns > index.size())
			{
				throw new DataSeriesImportException("Row " + row + " has more columns (" + numColumns + ") than there are index values (" + index.size() + ")"); 
			}
			maxColumns = Math.max(maxColumns, numColumns);
		}
		
		
		maxColumns = Math.max(maxColumns, index.size());
		
		cellData = new String[records.size() - dataStartIndex][maxColumns];

		for(int row = dataStartIndex; row < records.size(); row++)
		{
			List<String> currentRecord = records.get(row);
			int firstColumn = 0;
			if(params.isImportRowNames() && currentRecord.size() > 0)
			{
				rowNames.add(currentRecord.get(0));
				firstColumn = 1;
			}
			else
			{
				rowNames.add("Row" + Integer.toString(row - dataStartIndex + 1));
			}
					
			for(int column = firstColumn; column < currentRecord.size(); column ++)
			{
				cellData[row - dataStartIndex][column - firstColumn] = currentRecord.get(column);
			}
		}		
		
		return new PreImportResults(rowNames, index, cellData);		
	}
	
		
	public static PreImportResults preImport(Reader reader, ImportParameters params, boolean strict) throws IOException
	{
		CSVFormat format = CSVFormat.DEFAULT
								.withDelimiter(params.getSeparator())
								.withCommentMarker(params.getCommentCharacter());
		
		try (CSVParser parser = new CSVParser(reader, format))		
		{			
			
			
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
			
			return preImportFromArray(recordsList, params, strict);
		}
	}
}

