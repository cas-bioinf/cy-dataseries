# cy-timeseries

As of now (2016-04-26) a first prototype of a Cytoscape plugin for handling time series.

What can you do now (not much):
* Explore the test session (see testData/test.cys) which has a time series associated with each node. 
* View the series data in the "Time Series Visual" panel in the Table panel shows the time series. However, I did not implement a selection listener so far and thus you have to click on the chart in the panel to make it refresh to display the series for currently selected node.
* Add a time series with Apps -> Time series. Currently, only a series where there is a column in the node table for each time point are supported. The time point specification supports Matlab-style syntax (e.g. 1:100).
