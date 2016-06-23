package cz.cas.mbu.cydataseries.internal;

public enum TimeSeriesSourceType {
	SeparateColumns("Separate columns"), ListInAColumn("A single column of list type.");
	
	private String displayName;

	private TimeSeriesSourceType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	
}
