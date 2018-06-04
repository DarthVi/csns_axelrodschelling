import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASObserver implements Control
{

    //CONSTANTS
    private static final String PAR_ITERINTERVAL = "logInterval";
    private static final String PAR_PROT = "protocol";
    private static final String F_VALUE = "f_value";

    private static final String NODEFILENAME = "nodes";
    private static final String EDGEFILENAME = "edges";
    private static final String[] EDGE_HEADERS = {"source", "target"};

    //FIELDS
    private int iterations;
    private int interval;
    private int pid;
    private int culturalCodeSize;

    private CSVPrinter edgePrinter;

    public ASObserver(String prefix)
    {
        interval = Configuration.getInt(prefix + "." + PAR_ITERINTERVAL);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        iterations = 0;
        culturalCodeSize = Configuration.getInt(prefix + "." + F_VALUE);

        FileWriter outEdges = null;

        try
        {
            outEdges = new FileWriter("./snapshots/" + EDGEFILENAME  + ".csv");

            edgePrinter = new CSVPrinter(outEdges, CSVFormat.DEFAULT.withHeader(EDGE_HEADERS));
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute()
    {
        //serialize the edges only one time (they do not change in this model), then serialize network snapshot
        if(iterations == 0)
        {
            serializeEdges();
        }

        iterations++;
        if(iterations % interval == 0)
            serializeNetworkSnapshot(iterations);

        //if no imitation or movements occur, stop the simulation
        if((Interaction.getCulturalChanges() == false && Interaction.getMoveActivity() == false && iterations != 1) == true)
        {
            closeCSVPrinters();
            return true;
        }
        else
            return false;
    }

    /**
     * Saves a .csv snapshot of the network compatible with gephi (nodes.csv and edges.csv).
     * edges.csv contains merely source id and target id, while nodes.csv contains the id and the important attributes
     * for each site.
     *
     * @param iterAppend    used as an index to append to the filenames
     */
    private void serializeNetworkSnapshot(int iterAppend)
    {

        FileWriter outNodes = null;
        CSVPrinter nodePrinter = null;

        try
        {
            outNodes = new FileWriter("./snapshots/" + NODEFILENAME + iterAppend  + ".csv");
            String[] NODE_HEADERS;

            //node headers depends on cultural code size, cannot be defined as a static final field
            NODE_HEADERS = new String[3 + culturalCodeSize];
            NODE_HEADERS[0] = "Id";
            NODE_HEADERS[1] = "Empty";
            NODE_HEADERS[2] = "Interval";

            for(int j = 3; j < NODE_HEADERS.length; j++)
            {
                NODE_HEADERS[j] = "code" + (j-3);
            }
            nodePrinter = new CSVPrinter(outNodes, CSVFormat.DEFAULT.withHeader(NODE_HEADERS).withQuote(null));

        } catch (IOException e)
        {
            e.printStackTrace();
        }


        for(int i=0; i < Network.size(); i++)
        {
            Site site = (Site) Network.get(i);

            //collect site data
            long siteID = site.getID();
            boolean isEmpty = site.isEmpty();
            int[] culturalCode = site.getWholeSigma();


            try
            {
                nodePrinter.printRecord(siteID, isEmpty, "\"[" + iterAppend + ", " + (iterAppend + 2) + "\"]", Arrays.toString(culturalCode).replaceAll("[\\\" \\[\\]]", ""));
            }
            catch(IOException | NullPointerException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            nodePrinter.close();
        }
        catch(IOException | NullPointerException e)
        {
            e.printStackTrace();
        }



    }

    public void closeCSVPrinters()
    {
        try
        {
            edgePrinter.close();
        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public void serializeEdges()
    {
        for(int i=0; i < Network.size(); i++)
        {
            Site site = (Site) Network.get(i);
            Linkable linkable = (Linkable) site.getProtocol(FastConfig.getLinkable(pid));

            //collect site data
            long siteID = site.getID();

            //collecte neighbors IDs
            List<Long> neighborsID = new ArrayList<Long>();

            for(int j = 0; j < linkable.degree(); j++)
            {
                neighborsID.add(linkable.getNeighbor(j).getID());
            }

            for(long id : neighborsID)
            {
                try
                {
                    edgePrinter.printRecord(siteID, id);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
