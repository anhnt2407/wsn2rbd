package br.cin.ufpe.wsn2rbd.protocols;

import br.cin.ufpe.wsn2cpn.Node;
import br.cin.ufpe.wsn2rbd.Route;
import java.util.List;

/**
 *
 * @author avld
 */
public class DirectProtocol extends Protocol
{

    public DirectProtocol()
    {
        // do nothing
    }
    
    @Override
    public Route getRoute( int fromId , int toId )
    {
        List<Integer> neighborList = getNeighborMap().get( fromId );
        
        if( neighborList.contains( toId ) )
        {
            Node node = getTopology().getNodeMap().get( fromId );
            return convertNodeToRoute( node );
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public String getName()
    {
        return "direct";
    }
    
}
