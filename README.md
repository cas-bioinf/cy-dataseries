[![Build Status](https://travis-ci.org/martincerny/cy-dataseries.svg?branch=master)](https://travis-ci.org/martincerny/cy-dataseries)
# cy-dataseries

As of now (2016-06-23) a working prototype of a Cytoscape plugin for handling data series (time series, repeated measurements etc.).

Licensed under GPLv3.

See [Wiki](https://github.com/martincerny/cy-dataseries/wiki) for more info

## Usage
Import data from either SOFT files or tabular files (csv, tsv and the like) through
```
File -> Import -> Data Series
```

Modifying and other functionality of cy-dataseries is available from the Cytoscape menu bar
```
Apps -> Data Series
```
This includes the management of data series and mapping of data series to networks, i.e.
* `remove data series`
* `map column series`
* `remove column mapping`
* `manage column mapping`

Finally, to export data series use
```
File -> Export -> Data Series
```

