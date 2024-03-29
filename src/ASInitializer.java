import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Initializer for the Axelrod-Schelling model. Nodes are empty with a probability defined in the configuration file.
 * Non empty nodes are handled by generating a random cultural code with code length and trait maximum value both
 * defined in the configuration file.
 */
public class ASInitializer implements Control
{

    //CONSTANTS
    private static final String E_PROB = "empty_probability";
    private static final String F_VALUE = "f_value";
    private static final String Q_VALUE = "q_value";
    private static final String PAR_PROT = "protocol";

    //FIELDS
    private double emptyProbability;
    private int culturalCodeSize;
    private int qvalue;
    private int protocolPid;

    public ASInitializer(String prefix)
    {
        emptyProbability = Configuration.getDouble(prefix + "." + E_PROB);
        culturalCodeSize = Configuration.getInt(prefix + "." + F_VALUE);
        qvalue = Configuration.getInt(prefix + "." + Q_VALUE);
        protocolPid = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    @Override
    public boolean execute()
    {
        for(int i = 0; i < Network.size(); i++)
        {
            Site site = (Site) Network.get(i);
            site.allocSigma(new int[culturalCodeSize]);

            //il nodo è vuoto con probabilità emptyProbability, at least one node empty
            if(CommonState.r.nextDouble() <= emptyProbability || i==0)
            {
                site.setEmpty(true);
                AxelrodSchelling axs = (AxelrodSchelling) site.getProtocol(protocolPid);
                axs.addEmptyNode(site);
            }
            else
            {
                site.setEmpty(false);

                for(int j = 0; j < culturalCodeSize; j++)
                {
                    site.setSigma(j, CommonState.r.nextInt(qvalue));
                }
            }
        }
        return false;
    }
}
