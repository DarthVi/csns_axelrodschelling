import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;

public class AxelrodSchelling implements CDProtocol {

    //CONSTANTS

    private static final String E_PROB = "empty_probability";
    private static final String T_TRESHOLD = "T_threshold";

    //FIELDS

    //Site's probability of being empty
    private final double emptyProbability;

    //Threshold value for the average cultural overlap
    private final double toleranceThreshold;

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
    public void nextCycle(Node node, int i) {
        //TODO
    }
}
