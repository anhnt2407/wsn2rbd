package br.cin.ufpe.wsn2rbd.protocols;

import br.cin.ufpe.wsn2cpn.Node;
import br.cin.ufpe.wsn2rbd.Estatistica;
import br.cin.ufpe.wsn2rbd.Route;
import java.util.List;

/**
 *
 * @author avld
 */
public class GossipingProtocol extends Protocol
{
    
    public GossipingProtocol()
    {
        // do nothing
    }
    
    @Override
    public Route getRoute(int fromID , int toID )
    {
        Route selectedRoute = selectNode( fromID , toID );
        
        Node node = getTopology().getNodeMap().get( fromID );
        Route route = convertNodeToRoute( node );
        
        if( selectedRoute != null )
        {
            route.getRouteList().add( selectedRoute );
        }
        
        return route;
    }
    
    public Route selectNode( int nodeId , int toID )
    {
        List<Integer> neighborList = getNeighborMap().get( nodeId );
        int index = (int) Estatistica.uniform( 0.0 , neighborList.size() * 1.0 );
        int selectedId = neighborList.get( index );
        
        Node node = getTopology().getNodeMap().get( selectedId );
        Route route = convertNodeToRoute( node );
        
        if( selectedId == toID )    // caso o nó selecionado seja o destinatario
        {
            return null;            // deve retorna nada
        }
        
        Route selectedRoute = selectNode( selectedId , toID );
            
        if( selectedRoute != null ) // caso não seja o destinatario
        {
            route.getRouteList().add( selectedRoute );  // deve escolher o proximo
        }

        return route;
    }
    
    @Override
    public String getName()
    {
        return "gossiping";
    }
}
