package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DoubleDataSeries;

/**
 * The task lets the user pick any instance of DoubleDataSeries registered in
 * the current session and exponentiates (Math.exp) all of its values (modifying
 * the selected series).
 * 
 * @author Craig Faria
 */

/**
 * go to link http://www.programcreek.com/java-api-examples/index.php?source_dir
 * =cytoscape -api-master/work-api/src/main/java/org/cytoscape/work/Tunable.java
 * to understand how @Tunable works
 * 
 */
public class ExponentiateDataSeriesTask extends AbstractValidatedTask {

	@Tunable(description = "Series to exponentiate")
	public ListSingleSelection<DataSeries<?, ?>> dataSeries;

	public ExponentiateDataSeriesTask(DataSeriesManager dataSeriesManager) {
		dataSeries = new ListSingleSelection<>(
				dataSeriesManager.getAllDataSeries());
	}

	@ProvidesTitle
	public String getTitle() {
		return "Exponentiate data series";
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		DoubleDataSeries<?> doubleDataSeries = (DoubleDataSeries<?>) dataSeries
				.getSelectedValue();
		for (int i = 0; i < doubleDataSeries.getRowCount(); i++) {
			for (int j = 0; j < doubleDataSeries.getRowDataArray(i).length; j++) {
				doubleDataSeries.getDataArray()[i][j] = Math
						.exp(doubleDataSeries.getDataArray()[i][j]);
			}
		}
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		errMsg.append("Are you sure you want to exponentiate data series '"
				+ dataSeries.getSelectedValue().getName() + "'?");
		return ValidationState.REQUEST_CONFIRMATION;
	}

}
