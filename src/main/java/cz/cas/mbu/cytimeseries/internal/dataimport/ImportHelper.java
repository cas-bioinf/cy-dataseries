package cz.cas.mbu.cytimeseries.internal.dataimport;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import cz.cas.mbu.cytimeseries.dataimport.PreImportResults;

public class ImportHelper {
	public static PreImportResults preImport(Reader reader, ImportParameters params) throws IOException
	{
		CSVFormat format = CSVFormat.DEFAULT
								.withRecordSeparator(params.getSeparator())
								.withCommentMarker(params.getCommentCharacter());
		
		try (CSVParser parser = new CSVParser(reader, format))		
		{
			List<String> index;
			List<String> rowNames;
			String[][] cellData;
			
			if(params.isTransposeBeforeImport())
			{
				throw new UnsupportedOperationException("Transpose not supported so far");
			}
			else
			{
				List<CSVRecord> records = parser.getRecords();
				
				
				int dataStartIndex;
				if(params.isManualIndexData())
				{
					index = params.getManualIndexValues();
					dataStartIndex = 0;
				}
				else
				{
					if(records.isEmpty())
					{
						return new PreImportResults(Collections.EMPTY_LIST, Collections.EMPTY_LIST, new String[][]{});
					}
					
					if(params.isImportRowNames())
					{
						//skip the first column dedicated to row names
						index = new ArrayList<String>(records.get(0).size() - 1); 
						for(int column = 1; column < records.get(0).size(); column++)
						{
							index.set(column - 1,  records.get(0).get(column));
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
				
				int maxColumns = index.size();
				for(int row = dataStartIndex; row < records.size(); row++)
				{
					int numColumns = records.get(row).size();
					if(params.isImportRowNames())
					{
						numColumns -= 1; //skip the column for row names
					}
					maxColumns = Math.max(maxColumns, numColumns);
				}
				
				cellData = new String[records.size() - dataStartIndex][maxColumns];
	
				for(int row = dataStartIndex; row < records.size(); row++)
				{
					CSVRecord currentRecord = records.get(row);
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
						cellData[row][column - firstColumn] = currentRecord.get(column);
					}
				}
			}
			return new PreImportResults(rowNames, index, cellData);
		}
	}
}

