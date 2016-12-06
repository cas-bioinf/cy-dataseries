package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Contents of SOFT files as stored at gene expression omnibus
 * @author MBU
 *
 */
public class SoftFile {
	public enum EntityType {Platform,Series,Sample,Dataset}; 
	
	public static class SoftTable 
	{
		private final EntityType type;
		private final String caption;
		private final List<String> columnNames;
		private final List<String> columnDescriptions;
		private final List<List<String>> contents;
		
		public SoftTable(EntityType type, String caption, List<String> columnNames, List<String> columnDescriptions,
				List<List<String>> contents) {
			super();
			this.type = type;
			this.caption = caption;
			this.columnNames = columnNames;
			this.columnDescriptions = columnDescriptions;
			this.contents = contents;
		}
		
		public SoftTable(EntityType type, String caption) {
			super();
			this.type = type;
			this.caption = caption;
			this.columnNames = new ArrayList<>();
			this.columnDescriptions = new ArrayList<>();
			this.contents = new ArrayList<>();
		}
		
		
		public EntityType getType() {
			return type;
		}



		public String getCaption() {
			return caption;
		}
		public List<String> getColumnNames() {
			return columnNames;
		}
		public List<String> getColumnDescriptions() {
			return columnDescriptions;
		}
		public List<List<String>> getContents() {
			return contents;
		}
		
		@Override
		public String toString() {
			return type.toString() + ": " + getCaption();
		}
		
	}
	
	private List<SoftTable> tables;

	public SoftFile(List<SoftTable> tables) {
		super();
		this.tables = tables;
	}

	public List<SoftTable> getTables() {
		return tables;
	}
	
	
}
