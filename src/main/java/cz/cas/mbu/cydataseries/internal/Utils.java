package cz.cas.mbu.cydataseries.internal;

import org.cytoscape.model.CyNetwork;

public class Utils {
	public static String getNetworkName(CyNetwork network)
	{
		return network.getRow(network).get(CyNetwork.NAME, String.class);
	}
}
