package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Contents of SOFT files as stored at gene expression omnibus
 * @author MBU
 *
 */
public class SoftFile {
	public static class SoftTable 
	{
		private String caption;
		private List<String> columnNames;
		private List<String> columnDescriptions;
		private List<List<String>> contents;
		
				
		public SoftTable(String caption, List<String> columnNames, List<String> columnDescriptions,
				List<List<String>> contents) {
			super();
			this.caption = caption;
			this.columnNames = columnNames;
			this.columnDescriptions = columnDescriptions;
			this.contents = contents;
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
		
		
	}
	
	private List<SoftTable> tables;

	public SoftFile(List<SoftTable> tables) {
		super();
		this.tables = tables;
	}

	public List<SoftTable> getTables() {
		return tables;
	}
	
	public static SoftFile fromLines(Stream<String> lines)
	{
		 
		for(Iterator<String> it = lines.iterator(); it.hasNext(); it.next())
		{
			String line = it.next();
		}
		return new SoftFile(null);
	}
}
