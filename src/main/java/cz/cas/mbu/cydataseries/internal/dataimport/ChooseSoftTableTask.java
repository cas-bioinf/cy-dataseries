package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.function.Consumer;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.SoftTable;

public class ChooseSoftTableTask extends AbstractTask {
	@Tunable(description = "Choose dataset to import")
	public ListSingleSelection<SoftTable> table;
	
	private final Consumer<SoftTable> tableTarget;
	
	private SoftFile softFile;
	
	public ChooseSoftTableTask(Consumer<SoftTable> tableTarget){
		this.tableTarget = tableTarget;
	}

	public void setSoftFile(SoftFile file)
	{
		softFile = file;
		if(file.getTables().size() > 1)
		{
			table = new ListSingleSelection<>(file.getTables());
		}
		else
		{
			table = null; //makes the GUI skip the prompt
		}
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if(table == null) //skipped the prompt - means there was only a single table (see setSoftFile)
		{
			tableTarget.accept(softFile.getTables().get(0));
		}
		else
		{
			tableTarget.accept(table.getSelectedValue());
		}
	}
	
	
	
}
