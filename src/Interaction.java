import peersim.cdsim.CDProtocol;
import peersim.core.Node;

/**
 * This protocol has only two static fields to log whether after an {@link AxelrodSchelling} cycle
 * any activity (cultural changes or moving Sites) happened. It will be useful to the
 * controller responsible for ending the simulation
 */
public class Interaction implements CDProtocol
{
    //FIELDS
    private static long moveActivity = 0;
    private static long culturalChanges = 0;

    public Interaction(String prefix)
    {
        //do nothing
    }

    public Object clone()
    {
        Interaction interaction = null;

        try
        {
            interaction = (Interaction) super.clone();
        }catch (CloneNotSupportedException e) {} //never happens

        return interaction;
    }

    @Override
    public void nextCycle(Node node, int i)
    {
        Interaction.moveActivity = 0;
        Interaction.culturalChanges = 0;
    }

    public static void setMoveActivity()
    {
        Interaction.moveActivity++;
    }

    public static long getMoveActivity()
    {
        return Interaction.moveActivity;
    }

    public static void setCulturalChanges()
    {
        Interaction.culturalChanges++;
    }

    public static long getCulturalChanges()
    {
        return Interaction.culturalChanges;
    }
}
