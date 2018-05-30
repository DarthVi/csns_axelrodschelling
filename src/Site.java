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

    //empty state
    private boolean empty;


    public Site(String prefix)
    {
        super(prefix);

        int culturalCodeSize = Configuration.getInt(prefix + "." + F_VALUE);
        this.sigma = new int[culturalCodeSize];
        this.empty = true;
    }

    public Object clone()
    {
        Site result = (Site) super.clone();
        result.sigma = this.sigma.clone();
        result.empty = this.empty;
        return result;
    }

    //SETTERS AND GETTERS

    public void setSigma(int i, int value)
    {
        this.sigma[i] = value;
    }

    public int getSigma(int i)
    {
        return this.sigma[i];
    }

    public int getSigmaSize()
    {
        return this.sigma.length;
    }

    public boolean isEmpty()
    {
        return empty;
    }

    public void setEmpty(boolean state)
    {
        this.empty = state;
    }
}
