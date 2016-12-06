package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.EntityType;
import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.SoftTable;

public class SoftFileImporter {
	
	private final Logger logger = Logger.getLogger(SoftFileImporter.class);
	
	private enum State {NoEntity,EntityAttributes,EntityTableHeader,EntityTableBody, Closed };

	private State state = State.NoEntity;
	private EntityType currentEntityType = null;
	private String currentID = null;
	private String currentCaption = null;
	private Map<String, String> currentColumnDescriptions = null;

	
	private SoftTable currentDatasetTable = null;

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

	private void finalizeAll()
	{
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
		} catch(Throwable t)
		{
			logger.error("Error parsing SOFT file.", t);
			throw t;
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
			currentDatasetTable.getColumnNames().addAll(valuesList);
			currentDatasetTable.getColumnNames().forEach(column ->
			{
				currentDatasetTable.getColumnDescriptions().add(currentColumnDescriptions.get(column));
			});
		}
		else if(currentEntityType == EntityType.Sample && currentSampleSeriesID != null){
			currentSampleIDColumnIndex = valuesList.indexOf("ID_REF");
			currentSampleValueColumnIndex = valuesList.indexOf("VALUE");
			
			if(!seriesDependentVariableIndicesMaps.containsKey(currentSampleSeriesID))
			{
				seriesDependentVariableIndicesMaps.put(currentSampleSeriesID, new HashMap<>());
			}

			SoftTable currentTable = seriesTables.get(currentSampleSeriesID);
			currentTable.getColumnNames().add(currentID);
			currentTable.getColumnDescriptions().add(currentSampleTitle);
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
				currentDatasetTable.getContents().add(Arrays.asList(values));
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
				currentCaption = typeAndId.getValue(); //seed the caption with ID, more info will be added, if encountered
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
					currentDatasetTable = new SoftTable(EntityType.Dataset, currentCaption);
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
				if (nameValue.getName().endsWith("_title") || (nameValue.getName().endsWith("description")))
				{
					currentCaption = currentCaption + " (" + nameValue.getValue() + ")"; 
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
		if(importTableDirectly(currentEntityType) && currentDatasetTable != null)
		{
			processedTables.add(currentDatasetTable);
			directlyImportedTablesById.put(currentID, currentDatasetTable);
		}
		
		currentEntityType = null;
		currentID = null;
		currentCaption = null;
		currentColumnDescriptions = null;
		currentDatasetTable = null;
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
