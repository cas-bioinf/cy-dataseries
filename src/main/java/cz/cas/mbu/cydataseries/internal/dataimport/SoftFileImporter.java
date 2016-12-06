package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.EntityType;
import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.SoftTable;

public class SoftFileImporter {
	private enum State {NoEntity,EntityAttributes,EntityTableHeader,EntityTableBody, Closed };

	private State state = State.NoEntity;
	private EntityType currentEntityType = null;
	private String currentCaption = null;
	private Map<String, String> currentColumnDescriptions = null;

	
	private SoftTable currentDatasetTable = null;

	private Map<String, SoftTable> seriesTables; // indexed by series ID
	private Map<String, Map<String, Integer>> seriesDependentVariableIndicesMaps;

	private List<SoftTable> processedTables = new ArrayList<>();
	
	/**
	 * Once called, no more calls to {@link #parseLines(Stream)} can be made.
	 * @return
	 */
	public SoftFile getResult() {
		state = State.Closed;
		return new SoftFile(processedTables);
	}

	public void parseLines(Stream<String> lines) {

		Iterator<String> it = lines.iterator();
		while(it.hasNext()) {
			String line = it.next();
			parseLine(line);
		}
		finalizeCurrentEntity();		
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

	
	protected void parseTableHeader(String line)
	{
		String[] values = line.split("\t");
		if(currentEntityType == EntityType.Dataset)
		{
			currentDatasetTable.getColumnNames().addAll(Arrays.asList(values));
			currentDatasetTable.getColumnNames().forEach(column ->
			{
				currentDatasetTable.getColumnDescriptions().add(currentColumnDescriptions.get(column));
			});
		}
		else if(currentEntityType == EntityType.Platform)
		{
			//TODO
		}
		else if(currentEntityType == EntityType.Sample){
			//TODO
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
			if(currentEntityType == EntityType.Dataset)
			{
				currentDatasetTable.getContents().add(Arrays.asList(values));
			}
			else if(currentEntityType == EntityType.Platform)
			{
				//TODO
			}
			else if(currentEntityType == EntityType.Sample){
				//TODO
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
				currentCaption = typeAndId.getValue();
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
				if(currentEntityType == EntityType.Dataset)
				{
					currentDatasetTable = new SoftTable(EntityType.Dataset, currentCaption);
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
					//TODO
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
		if(currentEntityType == EntityType.Dataset && currentDatasetTable != null)
		{
			processedTables.add(currentDatasetTable);
		}
		currentEntityType = null;
		currentCaption = null;
		currentColumnDescriptions = null;
		currentDatasetTable = null;
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
