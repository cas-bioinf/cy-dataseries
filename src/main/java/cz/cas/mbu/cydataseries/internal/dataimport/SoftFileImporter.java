package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.EntityType;
import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.SoftTable;

public class SoftFileImporter {
	
	private static final String SAMPLE_ID_REF_COLUMN_NAME = "ID_REF";
	private static final String SAMPLE_VALUE_COLUMN_NAME = "VALUE";
	private final Logger logger = Logger.getLogger(SoftFileImporter.class);
	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME);
	
	private enum State {NoEntity,EntityAttributes,EntityTableHeader,EntityTableBody, Closed };

	private State state = State.NoEntity;
	private EntityType currentEntityType = null;
	private String currentID = null;
	private String currentTitle = null;
	private Map<String, String> currentColumnDescriptions = null;

	
	private SoftTable currentDirectlyImportedTable = null;

	private Map<String, SoftTable> seriesTables = new HashMap<>(); // indexed by series ID
	private Map<String, Map<String, Integer>> seriesDependentVariableIndicesMaps = new HashMap<>();

	private Map<String, String> seriesPlatforms = new HashMap<>();
	private Map<String, SoftTable> directlyImportedTablesById = new HashMap<>();
	
	private String currentSampleSeriesID;
	private String currentSampleTitle;
	private int currentSampleIDColumnIndex;
	private int currentSampleValueColumnIndex;
	
	private List<SoftTable> processedTables = new ArrayList<>();
	
	/**
	 * Once called, no more calls to {@link #parseLines(Stream)} can be made.
	 * @return
	 */
	public SoftFile getResult() {
		if(state != State.Closed)
		{
			state = State.Closed;
			finalizeAll();
		}
		return new SoftFile(processedTables);
	}

	private void tryMergePlatformTable(String seriesId, SoftTable seriesTable)
	{
		String platformId = seriesPlatforms.get(seriesId); 
		if(platformId != null)
		{
			SoftTable platformTable = directlyImportedTablesById.get(platformId);
			if(platformTable != null)
			{
				int platformIdIndex = platformTable.getColumnNames().indexOf("ID");
				if(platformIdIndex >= 0)
				{
					List<List<String>> mergedContents = new ArrayList<>();
					List<String> seriesRowIds = seriesTable.getContents().stream().map(row -> row.get(0)).collect(Collectors.toList()); 
					platformTable.getContents().forEach(platformRow ->
					{
						String rowId = platformRow.get(platformIdIndex);
						int seriesRowIndex = seriesRowIds.indexOf(rowId);
						if(seriesRowIndex > 0)
						{
							List<String> seriesRow = seriesTable.getContents().get(seriesRowIndex);
							List<String> seriesData = seriesRow.subList(1, seriesRow.size()); //omit the ID column
							mergedContents.add(new ListConcatenation<>(platformRow, seriesData));
						}						
					});
					List<String> mergedColumnNames = new ListConcatenation<>(platformTable.getColumnNames(), seriesTable.getColumnNames().subList(1, seriesTable.getColumnNames().size()));
					List<String> mergedColumnDescriptions = new ListConcatenation<>(platformTable.getColumnDescriptions(), seriesTable.getColumnDescriptions().subList(1, seriesTable.getColumnDescriptions().size()));
					SoftTable mergedTable = new SoftTable(EntityType.Series, seriesTable.getCaption() + " + platform", mergedColumnNames, mergedColumnDescriptions, mergedContents);
					processedTables.add(mergedTable);
				}
				else
				{
					userLogger.info("Platform '" + platformId + "' for series '" + seriesId + "' does not have an ID column.");					
				}
			}
			else
			{
				userLogger.info("Platform '" + platformId + "' for series '" + seriesId + "' not found.");					
			}
		}
		else 
		{
			userLogger.info("Series '" + seriesId + "' does not have any associated platform.");
		}
	}
	
	private void finalizeAll()	
	{
		//Combine series tables with corresponding platform tables
		seriesTables.forEach(this::tryMergePlatformTable);
		processedTables.addAll(seriesTables.values());
	}
	
	public void parseLines(Stream<String> lines) {

		try {
			Iterator<String> it = lines.iterator();
			while(it.hasNext()) {
				String line = it.next();
				parseLine(line);
			}
			finalizeCurrentEntity();
		}
		catch(OutOfMemoryError e)
		{
			//free the memory
			seriesTables = null;
			directlyImportedTablesById = null;
			currentDirectlyImportedTable = null;
			processedTables = null;
			throw new DataSeriesImportException("Out of memory while importing SOFT file");
		}
		catch(Throwable t)
		{
			logger.error("Error parsing SOFT file.", t);
			if(t instanceof Exception) //working around issue http://code.cytoscape.org/redmine/issues/3623
			{
				throw t;
			}
			else
			{
				throw new DataSeriesImportException(t.getMessage(), t);
			}
		}
	}
	
	/**
	 * 
	 * @param line the line (including the first character)
	 * @return
	 */
	protected NameValuePair processNameValuePair(String line)
	{
		String[] tokens = line.substring(1).split("=",2);
		if(tokens.length == 0)
		{
			return new NameValuePair(null, null);
		}
		else if (tokens.length == 1)
		{
			return new NameValuePair(tokens[0].trim(), null);
		}
		else 
		{
			return new NameValuePair(tokens[0].trim(), tokens[1].trim());
		}
			
	}

	
	protected boolean importTableDirectly(EntityType type)
	{
		return type == EntityType.Dataset || type == EntityType.Platform;
	}
	
	protected void parseTableHeader(String line)
	{
		String[] values = line.split("\t");
		List<String> valuesList = Arrays.asList(values);
		if(importTableDirectly(currentEntityType))
		{
			currentDirectlyImportedTable.getColumnNames().addAll(valuesList);
			currentDirectlyImportedTable.getColumnNames().forEach(column ->
			{
				currentDirectlyImportedTable.getColumnDescriptions().add(currentColumnDescriptions.get(column));
			});
		}
		else if(currentEntityType == EntityType.Sample && currentSampleSeriesID != null){
			currentSampleIDColumnIndex = valuesList.indexOf(SAMPLE_ID_REF_COLUMN_NAME);
			currentSampleValueColumnIndex = valuesList.indexOf(SAMPLE_VALUE_COLUMN_NAME);
			
			if(!seriesDependentVariableIndicesMaps.containsKey(currentSampleSeriesID))
			{
				seriesDependentVariableIndicesMaps.put(currentSampleSeriesID, new HashMap<>());
			}

			SoftTable currentTable = seriesTables.get(currentSampleSeriesID);
			currentTable.getColumnNames().add(currentID);
			
			StringBuilder columnDescription = new StringBuilder();
			if(currentSampleTitle != null)
			{
				columnDescription.append(currentSampleTitle);
			}
			if(currentColumnDescriptions.containsKey(SAMPLE_VALUE_COLUMN_NAME))
			{
				columnDescription.append(currentColumnDescriptions.get(SAMPLE_VALUE_COLUMN_NAME));
			}
			currentTable.getColumnDescriptions().add(columnDescription.toString());
			//Adding empty values for all known genes for the current sample
			currentTable.getContents().forEach(x -> x.add(null));
		}
		state = State.EntityTableBody;
	}
	
	protected void parseTableLine(String line)
	{
		if (line.startsWith("!") && line.trim().endsWith("table_end"))
		{
			state = State.EntityAttributes;
		}
		else
		{
			String[] values = line.split("\t");
			if(importTableDirectly(currentEntityType))
			{
				currentDirectlyImportedTable.getContents().add(Arrays.asList(values));
			}
			else if(currentEntityType == EntityType.Sample && currentSampleSeriesID != null){
				String id = values[currentSampleIDColumnIndex];
				String value = values[currentSampleValueColumnIndex];
				
				SoftTable currentTable = seriesTables.get(currentSampleSeriesID);
				Map<String,Integer> indexMap = seriesDependentVariableIndicesMaps.get(currentSampleSeriesID);
				
				if(!indexMap.containsKey(id))
				{
					//Create a new row, the size of the number of columns 
					indexMap.put(id, currentTable.getContents().size());
					List<String> newRow = new ArrayList<>(currentTable.getColumnNames().size());
					newRow.add(id);
					currentTable.getColumnNames().forEach(x -> newRow.add(null)); 
					currentTable.getContents().add(newRow);
				}
				
				int row = indexMap.get(id);
				
				//Set the last column to the correct value
				currentTable.getContents().get(row).set(currentTable.getColumnNames().size() - 1, value);
			}
		}
	}
	
	protected void parseMetaDataLine(String line)
	{
		if (line.startsWith("^")) {
			if (state != State.NoEntity) {
				finalizeCurrentEntity();
			}
			NameValuePair typeAndId = processNameValuePair(line);
			if(typeAndId.getName() != null)
			{
				switch(typeAndId.getName())
				{
					case "DATASET":
						currentEntityType = EntityType.Dataset;
						break;
					case "SERIES" :
						currentEntityType = EntityType.Series;
						break;
					case "SAMPLE" :
						currentEntityType = EntityType.Sample;
						break;
					case "PLATFORM":
						currentEntityType = EntityType.Platform;
						break;
					default :
						currentEntityType = null;						
				}
			}
			
			if(currentEntityType != null)
			{
				state = State.EntityAttributes;
				currentID = typeAndId.getValue();
				currentColumnDescriptions = new HashMap<>();
			}
			else
			{
				state = State.NoEntity;
			}
		}
		else if (line.startsWith("!") && state == State.EntityAttributes)
		{
			if (line.endsWith("_table_begin"))
			{
				state = State.EntityTableHeader;
				if(importTableDirectly(currentEntityType))
				{
					String caption = currentID;
					if (currentTitle != null)
					{
						caption += " (" + currentTitle + ")";
					}
					currentDirectlyImportedTable = new SoftTable(currentEntityType, caption);
				}
				else if(currentEntityType == EntityType.Sample)				
				{
					//Make sure table for the sample series is present
					if(currentSampleSeriesID != null && !seriesTables.containsKey(currentSampleSeriesID))
					{
						SoftTable newTable = new SoftTable(EntityType.Series, currentSampleSeriesID);
						newTable.getColumnNames().add("ID");
						newTable.getColumnDescriptions().add("");
						seriesTables.put(currentSampleSeriesID, newTable);
					}
				}
			}
			else {
				NameValuePair nameValue = processNameValuePair(line);
				if (nameValue.getName().endsWith("_title"))
				{
					currentTitle = nameValue.getValue(); 
				}
				else if (currentEntityType == EntityType.Sample && nameValue.getName().equals("Sample_series_id"))
				{
					currentSampleSeriesID = nameValue.getValue();
				}
				else if (currentEntityType == EntityType.Sample && nameValue.getName().equals("Sample_title"))
				{
					currentSampleTitle = nameValue.getValue();
				}
				else if (currentEntityType == EntityType.Series && nameValue.getName().equals("Series_platform_id")) 
				{
					seriesPlatforms.put(currentID, nameValue.getValue());
				}
			}						
		}
		else if (line.startsWith("#") && state == State.EntityAttributes) {
			NameValuePair nameValue = processNameValuePair(line);
			String value = nameValue.getValue();
			//A special hack for the very common naming scheme repeating the name in the value
			if(value.startsWith("Value for " + nameValue.getName() + ":"))
			{
				value = value.substring(value.indexOf(':') + 1);
			}
			currentColumnDescriptions.put(nameValue.getName(), value); 
		}
	}
	
	protected void parseLine(String line) {
		switch(state)
		{
			case Closed:
				throw new IllegalStateException("Cannot parse more lines with a closed SoftFileImporter");
			case NoEntity:
				parseMetaDataLine(line);
				break;
			case EntityAttributes:
				parseMetaDataLine(line);
				break;
			case EntityTableHeader:
				parseTableHeader(line);
				break;
			case EntityTableBody:
				parseTableLine(line);
				break;
			default:
				throw new IllegalStateException("Unrecognized state value:" + state);
		}
	}
	
	protected void finalizeCurrentEntity()
	{
		if(importTableDirectly(currentEntityType) && currentDirectlyImportedTable != null)
		{
			processedTables.add(currentDirectlyImportedTable);
			directlyImportedTablesById.put(currentID, currentDirectlyImportedTable);
		}
		
		currentEntityType = null;
		currentID = null;
		currentTitle = null;
		currentColumnDescriptions = null;
		currentDirectlyImportedTable = null;
		currentSampleSeriesID = null;
		currentSampleTitle = null;
		state = State.NoEntity;
	}
	
	private static class NameValuePair {
		private final String name;
		private final String value;
		
		public NameValuePair(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
		
		
	}
}
