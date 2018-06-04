import peersim.core.GeneralNode;
import peersim.config.Configuration;

public class Site extends GeneralNode {

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
        this.sigma = null;
        this.empty = true;
    }

    public Object clone()
    {
        Site result = (Site) super.clone();
        result.sigma = this.sigma;
        result.empty = this.empty;
        return result;
    }

    //SETTERS AND GETTERS

    protected void allocSigma(int[] ref)
    {
        this.sigma = ref;
    }

    public void setSigma(int i, int value)
    {
        this.sigma[i] = value;
    }

    public int getSigma(int i)
    {
        return this.sigma[i];
    }

    public int[] getWholeSigma() {return this.sigma.clone();}

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
