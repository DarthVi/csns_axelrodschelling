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

    private static final String NODEFILENAME = "nodes";
    private static final String EDGEFILENAME = "edges";
    private static final String[] EDGE_HEADERS = {"source", "target"};

    //FIELDS
    private int iterations;
    private int interval;
    private int pid;

    public ASObserver(String prefix)
    {
        interval = Configuration.getInt(prefix + "." + PAR_ITERINTERVAL);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        iterations = 0;
    }

    @Override
    public boolean execute()
    {
        //serialize a snapshot of the network current state
        iterations++;
        if(iterations % interval == 0)
            serializeNetworkSnapshot(iterations);

        //if no imitation or movements occur, stop the simulation
        return (Interaction.getCulturalChanges() == false && Interaction.getMoveActivity() == false && iterations != 1);
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
        String[] NODE_HEADERS = null;
        FileWriter outNodes = null;
        FileWriter outEdges = null;
        CSVPrinter nodePrinter = null;
        CSVPrinter edgePrinter = null;


        for(int i=0; i < Network.size(); i++)
        {
            Site site = (Site) Network.get(i);
            Linkable linkable = (Linkable) site.getProtocol(FastConfig.getLinkable(pid));

            //collect site data
            long siteID = site.getID();
            boolean isEmpty = site.isEmpty();
            int[] culturalCode = site.getWholeSigma();

            //collecte neighbors IDs
            List<Long> neighborsID = new ArrayList<Long>();

            for(int j = 0; j < linkable.degree(); j++)
            {
                neighborsID.add(linkable.getNeighbor(j).getID());
            }

            //node headers depends on cultural code size, cannot be defined as a static final field
            NODE_HEADERS = new String[3 + culturalCode.length];
            NODE_HEADERS[0] = "Id";
            NODE_HEADERS[1] = "Empty";
            NODE_HEADERS[2] = "Interval";

            for(int j = 3; j < NODE_HEADERS.length; j++)
            {
                NODE_HEADERS[j] = "code" + (j-3);
            }

            if(nodePrinter == null || edgePrinter == null)
            {
                try
                {
                    outNodes = new FileWriter("./snapshots/" + NODEFILENAME + iterAppend + ".csv");
                    outEdges = new FileWriter("./snapshots/" + EDGEFILENAME + iterAppend + ".csv");

                    nodePrinter = new CSVPrinter(outNodes, CSVFormat.DEFAULT.withHeader(NODE_HEADERS).withQuote(null));
                    edgePrinter = new CSVPrinter(outEdges, CSVFormat.DEFAULT.withHeader(EDGE_HEADERS));
                }
                catch(IOException e)
                {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

            try
            {

                nodePrinter.printRecord(siteID, isEmpty, "\"[" + iterAppend + ", " + (iterAppend+interval) + "]\"", Arrays.toString(culturalCode).replaceAll("[\\\" \\[\\]]", ""));


                for(long id : neighborsID)
                {
                    edgePrinter.printRecord(siteID, id);
                }



            }
            catch(IOException | NullPointerException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            nodePrinter.close();
            edgePrinter.close();
        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }

    }
}
