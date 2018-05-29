import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.List;

public class AxelrodSchelling implements CDProtocol {

    //CONSTANTS

    private static final String E_PROB = "empty_probability";
    private static final String T_TRESHOLD = "T_threshold";

    //FIELDS

    //Site's probability of being empty
    private final double emptyProbability;

    //Threshold value for the average cultural overlap
    private final double toleranceThreshold;

    //empty sites list
    private static List<Integer> emptySites = new ArrayList<Integer>();

    public AxelrodSchelling(String prefix)
    {
        emptyProbability = Configuration.getDouble(prefix + "." + E_PROB);
        toleranceThreshold = Configuration.getDouble(prefix + "." + T_TRESHOLD);
    }

    public Object clone()
    {
        AxelrodSchelling prot = null;

        try
        {
            prot = (AxelrodSchelling) super.clone();
        }
        catch (CloneNotSupportedException e ) {} //never happens

        return prot;
    }

    @Override
    public void nextCycle(Node node, int pid)
    {

        Linkable link = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
        List<Integer> nonEmptyNeighbours = getNonEmptyNeighbours(node, pid);

        int randomPeer;

        //TODO: complete this
    }

    /*
       Returns a list of indexes of the linkable structure. The sites associated
       with these indexes are not empty.

       @param   node    the site whose neighbours we want to analyze
       @param   pid     the node's pid
       @return          list of indexes of non empty sites in the linkable structure
     */
    private List<Integer> getNonEmptyNeighbours(Node node, int pid)
    {
        Linkable link = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));

        List<Integer> nonEmptyNeighbours = new ArrayList<Integer>();

        for(int i = 0; i < link.degree(); i++)
        {
            Site site = (Site) link.getNeighbor(i);

            if(!site.isEmpty())
                nonEmptyNeighbours.add(i);
        }

        return nonEmptyNeighbours;
    }
}
