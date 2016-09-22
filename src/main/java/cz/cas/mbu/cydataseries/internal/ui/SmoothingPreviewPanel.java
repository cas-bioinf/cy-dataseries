package cz.cas.mbu.cydataseries.internal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.Tunable;
import org.jfree.chart.ChartPanel;

import com.google.common.primitives.Doubles;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import cern.colt.Arrays;
import cz.cas.mbu.cydataseries.SmoothingService;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.KernelSmoothing;
import cz.cas.mbu.cydataseries.internal.dataimport.MatlabSyntaxNumberList;
import cz.cas.mbu.cydataseries.internal.tasks.SmoothInteractivePerformTask;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class SmoothingPreviewPanel extends JPanel implements CytoPanelComponent {

	
	JComboBox<DisplayFormat> displayGridComboBox;
	private JPanel mainDisplayPanel;
	
	private final CyServiceRegistrar registrar;
	
	private final TimeSeries sourceTimeSeries;
	
	private double[] estimateX = null;
	
	private Map<String,List<Integer>> currentlyShownRows;
	private double currentBandwidth;
		
	
	private final Color errorTextFieldBackground = new Color(255, 125, 128);
	private final Color defaultTextFieldBackground;
	
	private String caption;
	private JTextField bandwidthTextField;
	private JTextField timePointsTextField;
	private JCheckBox keepSourceTimePointsCheckBox;
	
	/**
	 * Create the panel.
	 */
	public SmoothingPreviewPanel(CyServiceRegistrar registrar, TimeSeries timeSeries) {
		setLayout(new BorderLayout(0, 0));
		
		mainDisplayPanel = new JPanel();
		add(mainDisplayPanel, BorderLayout.CENTER);
		
		JPanel controlPanel = new JPanel();
		add(controlPanel, BorderLayout.SOUTH);
		controlPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.UNRELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblDisplay = new JLabel("Display:");
		controlPanel.add(lblDisplay, "2, 2, right, default");
		
		displayGridComboBox = new JComboBox<>(new DefaultComboBoxModel<DisplayFormat>(new DisplayFormat[] {
				new DisplayFormat(1, 1),
				new DisplayFormat(1, 5),
				new DisplayFormat(2, 2),				
				new DisplayFormat(3, 2),				
				new DisplayFormat(5, 1),				
				}));
		displayGridComboBox.setSelectedIndex(3);//TODO - use RememberValueService
		displayGridComboBox.addItemListener(evt -> {
			if(evt.getStateChange() == ItemEvent.SELECTED)
			{
				updateDisplayGrid();
			}
		});
		controlPanel.add(displayGridComboBox, "4, 2, fill, default");
		
		JSeparator separator = new JSeparator();
		controlPanel.add(separator, "1, 1, 12, 1");
		
		JButton btnResampleRows = new JButton("See different examples");
		controlPanel.add(btnResampleRows, "12, 2");
		btnResampleRows.addActionListener(evt -> showDifferentExamples());
		
		JButton btnPerformSmoothing = new JButton("Perform smoothing");
		btnPerformSmoothing.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnPerformSmoothing.addActionListener(evt -> performSmoothing());
		
		keepSourceTimePointsCheckBox = new JCheckBox("Estimate in the same time points as the original series");
		keepSourceTimePointsCheckBox.setSelected(true);
		controlPanel.add(keepSourceTimePointsCheckBox, "2, 4, 3, 1");
		keepSourceTimePointsCheckBox.addItemListener(evt -> estimateInputChanged());
		
		JLabel lblNewLabel = new JLabel("Time points to estimate the data:");
		controlPanel.add(lblNewLabel, "6, 4, right, default");
		
		timePointsTextField = new JTextField();
		controlPanel.add(timePointsTextField, "8, 4, 3, 1, fill, default");
		timePointsTextField.setColumns(10);
		controlPanel.add(btnPerformSmoothing, "12, 4");
		timePointsTextField.getDocument().addDocumentListener(UIUtils.listenForAllDocumentChanges(this::estimateInputChanged));
		
		JLabel lblSmoothingBandwidth = new JLabel("Smoothing bandwidth:");
		controlPanel.add(lblSmoothingBandwidth, "2, 6, right, default");
		
		bandwidthTextField = new JTextField();
		controlPanel.add(bandwidthTextField, "4, 6, fill, default");
		bandwidthTextField.setColumns(10);
				
		JLabel lblCommaSeparatedSupports = new JLabel("Comma separated, supports Matlab notation (e.g. 1,2,3:5,10:2:20)");
		lblCommaSeparatedSupports.setFont(new Font("Tahoma", Font.ITALIC, 11));
		controlPanel.add(lblCommaSeparatedSupports, "6, 6, 5, 1");
		
		JButton btnClose = new JButton("Close");
		controlPanel.add(btnClose, "12, 6");
		btnClose.addActionListener(evt -> closePanel());

		
		//Add listener only after initial assignment to the bandwidth text field
		bandwidthTextField.getDocument().addDocumentListener(UIUtils.listenForAllDocumentChanges(this::bandwidthTextChanged));
		
		this.defaultTextFieldBackground = bandwidthTextField.getBackground();
		
		this.registrar = registrar;
		this.sourceTimeSeries = timeSeries;
		this.estimateX = timeSeries.getIndexArray();	
		
		currentlyShownRows = new HashMap<>();
		
		if(!java.beans.Beans.isDesignTime())
		{
			currentBandwidth = guessBandwidth(sourceTimeSeries);
			bandwidthTextField.setText(Double.toString(currentBandwidth));
			
			sampleShownRows();
			updateDisplayGrid();
			
			this.caption = "Smoothing: " + timeSeries.getName();
		}
		
	}

	private double guessBandwidth(TimeSeries ts)
	{
		return 10;
	}
	
	private void sampleShownRows()
	{
		//sample row names
		currentlyShownRows.clear();
		for(int i = 0; i < Math.min(getMaxDisplayed(), sourceTimeSeries.getRowCount()); i++)
		{
			currentlyShownRows.put(sourceTimeSeries.getRowName(i), Collections.singletonList(i));
		}
	}
	
	private void updateEstimateX()
	{
		boolean showTextError = false;
		if(!keepSourceTimePointsCheckBox.isSelected())
		{
			try {
				List<Double> timePointsList = MatlabSyntaxNumberList.listFromString(timePointsTextField.getText());
				if(!timePointsList.isEmpty())
				{
					estimateX = Doubles.toArray(timePointsList);
				}
				else
				{
					showTextError = true;
				}
			}
			catch (NumberFormatException ex)
			{
				showTextError = true;
			}
		}
		else 
		{
			estimateX = sourceTimeSeries.getIndexArray();
		}
		
		if(showTextError)
		{
			timePointsTextField.setBackground(errorTextFieldBackground);			
		}
		else
		{
			timePointsTextField.setBackground(defaultTextFieldBackground);
		}
	}
	
	private void estimateInputChanged()
	{
		double[] oldEstimateX = estimateX;
		updateEstimateX();
		timePointsTextField.setEnabled(!keepSourceTimePointsCheckBox.isSelected());
		if(oldEstimateX != estimateX) //intentional reference equality - just to bypass the most obvious unnecessary redrawings
		{
			updateDisplayGrid();
		}
	}
	
	@Override
	public Component getComponent() {		
		return this;
	}



	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}



	@Override
	public String getTitle() {
		return caption;
	}



	@Override
	public Icon getIcon() {
		return null;
	}


	private void showDifferentExamples()
	{
		sampleShownRows();
		updateDisplayGrid();
	}

	private void updateDisplayGrid()
	{
		DisplayFormat fmt = getSelectedDisplayFormat();
		mainDisplayPanel.removeAll();

		int maxDisplayed = getMaxDisplayed();		

		if(maxDisplayed != currentlyShownRows.size())
		{
			sampleShownRows();
		}
		
		ColumnSpec[] layoutColumns = new ColumnSpec[fmt.width * 2 - 1];
		RowSpec[] layoutRows = new RowSpec[(fmt.height) * 2 - 1]; 		
		
		for(int i = 0; i < fmt.width; i++)
		{
			if(i != 0)
			{
				layoutColumns[i * 2 - 1] = FormSpecs.RELATED_GAP_COLSPEC;
			}
			layoutColumns[i * 2] = FormSpecs.DEFAULT_COLSPEC;
		}
		
		for(int i = 0; i < fmt.height; i++) 
		{
			if(i != 0)
			{
				layoutRows[i * 2 - 1] = FormSpecs.RELATED_GAP_ROWSPEC;
			}
			layoutRows[i * 2] = FormSpecs.DEFAULT_ROWSPEC;
			
		}
		
		mainDisplayPanel.setLayout(new FormLayout(layoutColumns, layoutRows));
		
		List<JPanel> panelsToShow = currentlyShownRows.entrySet().stream()
				.map(entry -> {
					List<Integer> rows = entry.getValue();
					double[] allRowsConcat = new double[rows.size() * sourceTimeSeries.getIndexCount()];
					double[] repeatedIndex = new double[rows.size() * sourceTimeSeries.getIndexCount()];
					int rowLength = sourceTimeSeries.getIndexCount(); 
					for(int i = 0; i < rows.size(); i++ )
					{
						int row = rows.get(i);
						System.arraycopy(sourceTimeSeries.getRowDataArray(row), 0, allRowsConcat, i * rowLength, rowLength);
						System.arraycopy(sourceTimeSeries.getIndexArray(), 0, repeatedIndex, i * rowLength, rowLength);
					}
				
					//Do the smoothing
					double[] smoothedY = KernelSmoothing.linearKernalEstimator(repeatedIndex, allRowsConcat, estimateX, currentBandwidth);
					
					SmoothingChartContainer chartContainer = new SmoothingChartContainer();
					chartContainer.setSmoothingData(repeatedIndex, allRowsConcat, estimateX, smoothedY, entry.getKey());
					return new ChartPanel(chartContainer.getChart());
				})
				.collect(Collectors.toList());
		
		CellConstraints cc = new CellConstraints();
		for(int x = 0; x < fmt.getWidth(); x++)
		{
			for(int y = 0; y < fmt.getHeight(); y++)
			{
				int index = (y * fmt.getWidth()) + x;
				JPanel panelToAdd;
				if(index >= panelsToShow.size())
				{
					panelToAdd = new JPanel();
				}
				else
				{
					panelToAdd = panelsToShow.get(index);
				}
				
				int layoutX = x * 2 + 1;
				int layoutY = y * 2 + 1;				
				mainDisplayPanel.add(panelToAdd, cc.xy(layoutX, layoutY));
			}
		}
		

		mainDisplayPanel.revalidate();
		mainDisplayPanel.repaint();
	}

	private int getMaxDisplayed() {
		DisplayFormat fmt = getSelectedDisplayFormat();
		return fmt.getWidth() * fmt.getHeight();
	}

	private DisplayFormat getSelectedDisplayFormat() {
		return displayGridComboBox.getItemAt(displayGridComboBox.getSelectedIndex());
	}
	
	private void bandwidthTextChanged()
	{
		boolean showError = false;
		try 
		{
			double bandwidth = Double.parseDouble(bandwidthTextField.getText());
			if (bandwidth > 0 && Double.isFinite(bandwidth))
			{
				currentBandwidth = bandwidth;
				updateDisplayGrid();
			}
			else
			{
				showError = true;
			}
		} catch (NumberFormatException ex)
		{
			showError = true;
		}
		
		if(showError)
		{
			bandwidthTextField.setBackground(errorTextFieldBackground);
		}
		else
		{
			bandwidthTextField.setBackground(defaultTextFieldBackground);
		}
	}
	
	private void performSmoothing()
	{
		registrar.getService(TaskManager.class).execute(new TaskIterator(new SmoothInteractivePerformTask(registrar, sourceTimeSeries, estimateX, currentBandwidth)));
	}
	
	public void closePanel()
	{
		registrar.unregisterAllServices(this);
	}
	
	private static class DisplayFormat
	{
		private final int width;
		private final int height;
		
		public DisplayFormat(int width, int height) {
			super();
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		@Override
		public String toString() {
			return width + "x" + height;
		}
		
		
	}
			

}
