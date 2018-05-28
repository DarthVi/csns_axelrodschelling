import peersim.core.GeneralNode;
import peersim.config.Configuration;

public class Site extends GeneralNode {

    //CONSTANTS
    //
    /*parameter value that will initialize the length of the cultural code vector
     */
    private static final String F_VALUE = "f_value";

    //FIELDS
    /*
    Cultural code vector
     */
    private int[] sigma;


    public Site(String prefix)
    {
        super(prefix);

        int culturalCodeSize = Configuration.getInt(prefix + "." + F_VALUE);
        this.sigma = new int[culturalCodeSize];
    }

    public Object clone()
    {
        Site result = (Site) super.clone();
        result.sigma = this.sigma.clone();
        return result;
    }
}
