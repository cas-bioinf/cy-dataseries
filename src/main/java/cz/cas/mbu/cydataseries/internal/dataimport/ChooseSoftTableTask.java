package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.SoftTable;

public class ChooseSoftTableTask extends AbstractTask {
	@Tunable(description = "Choose dataset to import")
	public ListSingleSelection<StringAndIndex> tableSelection; //not using SoftTable directly due to memory leaks in Task GUI
	
	private final Consumer<SoftTable> tableTarget;
	
	private SoftFile softFile;
	
	public ChooseSoftTableTask(Consumer<SoftTable> tableTarget){
		this.tableTarget = tableTarget;
	}

	@ProvidesTitle
	public String getTitle()
	{
		return "Choose table in the SOFT file to import";
	}
	
	public void setSoftFile(SoftFile file)
	{
		softFile = file;		
		if(file.getTables().size() > 1)
		{
			List<StringAndIndex> choice = new ArrayList<>();
			for(int i = 0; i < file.getTables().size(); i++)
			{
				SoftTable softTable = file.getTables().get(i);
				choice.add(new StringAndIndex(i, softTable.getType().toString() + ": " + softTable.getCaption()));
			}
			tableSelection = new ListSingleSelection<>(choice);
		}
		else
		{
			tableSelection = null; //makes the GUI skip the prompt
		}
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if(tableSelection == null) //skipped the prompt - means there was only a single table (see setSoftFile)
		{
			tableTarget.accept(softFile.getTables().get(0));
		}
		else
		{
			tableTarget.accept(softFile.getTables().get(tableSelection.getSelectedValue().id));
		}
		softFile = null; //free the softFile for GC
	}
	
	public static class StringAndIndex {
		public int id;
		public String caption;			
		
		public StringAndIndex(int id, String caption) {
			super();
			this.id = id;
			this.caption = caption;
		}



		@Override
		public String toString()
		{
			return caption;
		}
	}
	
}
