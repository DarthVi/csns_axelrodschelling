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
    private static List<Site> emptySites = new ArrayList<Site>();

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

        if(!nonEmptyNeighbours.isEmpty())
        {
            int neighbourIndex = nonEmptyNeighbours.get(CommonState.r.nextInt(nonEmptyNeighbours.size()));
            Site peer = (Site) link.getNeighbor(neighbourIndex);

            double culturalOverlap = computeCulturalOverlap((Site) node, peer);

            //TODO: complete here
        }
        else
        {
            moveToEmptySite((Site) node);
        }

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

    /*
        Moves current site to another empty site and makes the current site empty

        @param  currentSite     The current site/node to be moved
     */
    private void moveToEmptySite(Site currentSite)
    {
        int emptySiteIndex = CommonState.r.nextInt(emptySites.size());
        Site newSite = emptySites.get(emptySiteIndex);

        newSite = (Site) currentSite.clone();
        currentSite.setEmpty(true);
        emptySites.remove(emptySiteIndex);
        emptySites.add(currentSite);
    }

    /*
        computes the cultural overlap between two nodes

        @param  site    node requiring the omega/cultural overlap
        @param  peer    neighbor selected to compare the cultural code

        @return         cultural overlap between two nodes
     */
    private double computeCulturalOverlap(Site site, Site peer)
    {
        double culturalOverlap = 0;
        int sigmaLength = site.getSigmaSize();

        for(int i = 0; i < sigmaLength; i++)
        {
            culturalOverlap += kroneckerDelta(site.getSigma(i), peer.getSigma(i));
        }

        return culturalOverlap/sigmaLength;

    }

    /*
        Computes the kronecker's delta between i and j

        @param  i
        @param  j

        @return         1 if i==j, 0 otherwise
     */
    private int kroneckerDelta(int i, int j)
    {
        return (i == j) ? 1 : 0;
    }
}
