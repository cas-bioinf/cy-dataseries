package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DoubleDataSeries;
import cz.cas.mbu.cydataseries.internal.data.DoubleDataSeriesImpl;

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

	private final DataSeriesManager dataSeriesManager;

	public ExponentiateDataSeriesTask(DataSeriesManager dataSeriesManager) {
		this.dataSeriesManager = dataSeriesManager;
		dataSeries = new ListSingleSelection<>(
				dataSeriesManager.getAllDataSeries());
	}

	@ProvidesTitle
	public String getTitle() {
		return "Exponentiate data series";
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		Double[][] mDoubleDataSeriesMatrix = new Double[dataSeries
				.getSelectedValue().getRowCount()][];
		double[][] doubleDataSeriesMatrix = new double[dataSeries
				.getSelectedValue().getRowCount()][];
		for (int i = 0; i < dataSeries.getSelectedValue().getRowCount(); i++) {
			mDoubleDataSeriesMatrix[i] = dataSeries.getSelectedValue()
					.getRowData(i).toArray(mDoubleDataSeriesMatrix[i]);
			for (int j = 0; j < doubleDataSeriesMatrix.length; j++) {
				doubleDataSeriesMatrix[i][j] = Math
						.exp(mDoubleDataSeriesMatrix[i][j]);
			}
		}
		
		dataSeriesManager.unregisterDataSeries(dataSeries.getSelectedValue());
		DoubleDataSeries<?> finalDS = new DoubleDataSeriesImpl(dataSeries.getSelectedValue().getSUID(),
				dataSeries.getSelectedValue().getName(), dataSeries
				.getSelectedValue().getRowIDs(), dataSeries
				.getSelectedValue().getRowNames(), dataSeries
				.getSelectedValue().getIndex(), dataSeries
				.getSelectedValue().getClass(), doubleDataSeriesMatrix);
		dataSeriesManager.registerDataSeries(finalDS);
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		errMsg.append("Are you sure you want to exponentiate data series '"
				+ dataSeries.getSelectedValue().getName() + "'?");
		return ValidationState.REQUEST_CONFIRMATION;
	}

}
