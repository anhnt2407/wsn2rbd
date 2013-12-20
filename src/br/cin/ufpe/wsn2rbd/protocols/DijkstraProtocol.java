package br.cin.ufpe.wsn2rbd.protocols;

import br.cin.ufpe.wsn2cpn.Node;
import br.cin.ufpe.wsn2rbd.Route;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author avld
 */
public class DijkstraProtocol extends Protocol
{
    private Map<Node,Integer> routeMap;
    private int MAX = 10;
    
    public DijkstraProtocol()
    {
        // do nothing
    }
    
    @Override
    public Route getRoute(int fromID , int toID )
    {
        System.out.println( "--------------- DIJSKTRA" );
        routeMap = new HashMap<>();
        
        Node node = getTopology().getNodeMap().get( fromID );
        Route route = selectNode( node , toID );
        
        return isLivelock() ? null : route;
    }
    
    public Route selectNode( Node nodeFrom , int toID )
    {
        if( isLivelock( nodeFrom ) )
        {
            return null;
        }
        
        Route fromRoute = convertNodeToRoute( nodeFrom );
        
        // pega o id do proximo nó
        String nodeNextIdStr = nodeFrom.getProperties().get( "nodeNext" );
        int nodeNextId = Integer.parseInt( nodeNextIdStr );
        
        System.out.print( "node: " + nodeFrom.getId() );
        System.out.println( " | nodeNext: " + nodeNextIdStr );
        
        // caso o nó selecionado seja o destinatario
        if( nodeNextId == toID )
        {
            // deve retorna nada
            return fromRoute;
        }
        
        // pega o proximo nó
        Node nodeNext = getTopology().getNodeMap().get( nodeNextId );
        if( nodeNext == null )
        {
            return fromRoute;
        }
        
//        Route route = convertNodeToRoute( nodeNext );
        Route selectedRoute = selectNode( nodeNext , toID );
        
        // caso não seja o destinatario
        if( selectedRoute != null )
        {
            // deve escolher o proximo
            fromRoute.getRouteList().add( selectedRoute );
        }

        return fromRoute;
    }
    
    private boolean isLivelock( Node node )
    {
        if( !routeMap.containsKey( node ) )
        {
            routeMap.put( node , 0 );
        }
        
        int counter = routeMap.get( node ) + 1;
        routeMap.put( node , counter );
        
        return counter >= MAX;
    }
    
    private boolean isLivelock()
    {
        for( int counter : routeMap.values() )
        {
            if( counter >= MAX )
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getName()
    {
        return "dijkstra";
    }
    
}
