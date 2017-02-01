package cz.cas.mbu.cydataseries.internal;

import org.cytoscape.model.CyNetwork;

public class Utils {
	/**
	 * Get the user-facing name of a network.
	 * @param network
	 * @return
	 */
	public static String getNetworkName(CyNetwork network)
	{
		return network.getRow(network).get(CyNetwork.NAME, String.class);
	}
}
