package br.cin.ufpe.wsn2rbd.protocols;

import br.cin.ufpe.wsn2cpn.Node;
import br.cin.ufpe.wsn2rbd.Estatistica;
import br.cin.ufpe.wsn2rbd.Route;

/**
 *
 * @author avld
 */
public class FloodingProtocol extends Protocol
{
    private int MAX_HOP = 5;
    private double ACCEPT_TO_SEND = 0.05;
    
    public FloodingProtocol()
    {
        // do nothing
    }
    
    @Override
    public Route getRoute( int fromID , int toID )
    {
        if( !isValidTopology() )
        {
            return null;
        }
        
        return getneighborRoute( fromID , toID , 0 );
    }

    private Route getneighborRoute( int fromId , int toID,  int hop )
    {
        Node fromNode   = getTopology().getNodeMap().get( fromId );
        Route fromRoute = convertNodeToRoute( fromNode );
        
        if( fromId == toID )
        {
            return null;        //a rota chegou no ponto final
        }
        else if( !willItRoute() && hop > 0 )
        {
            return null;        //cancelar a rota
        }
        else if( hop > MAX_HOP && hop > 0 )
        {
            return null;        //cancelar a rota
        }
        
        boolean hasLink = false;        //se tem conexão com o toID
        
        for( Integer neighborId : getNeighborMap().get( fromId ) )
        {
            Route neighborRoute = getneighborRoute( neighborId , toID , hop + 1 );
            
            if( neighborId == toID )
            {
                hasLink = true;
            }
            else if( neighborRoute != null )
            {
                fromRoute.getRouteList().add( neighborRoute );
            }
        }
        
        if( hasLink || !fromRoute.getRouteList().isEmpty() )
        {
            return fromRoute;
        }
        else
        {
            return null;
        }
    }
    
    private boolean willItRoute()
    {
        return Estatistica.uniform( 0.0 , 1.0 ) <= ACCEPT_TO_SEND;
    }
    
    @Override
    public String getName()
    {
        return "flooding_probability_based";
    }
}
