/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.cin.ufpe.wsn2rbd.protocols;

import br.cin.ufpe.wsn2cpn.Node;
import br.cin.ufpe.wsn2rbd.Route;
import java.util.List;

/**
 *
 * @author avld
 */
public class LeachProtocol extends Protocol
{

    public LeachProtocol()
    {
        // do nothing
    }
    
    @Override
    public Route getRoute( int fromId , int toId )
    {
        Node fromNode = getTopology().getNodeMap().get( fromId );
        String chIdStr = fromNode.getProperties().get( "chId" );
        int chId = 1;
        
        try
        {
            chId = Integer.parseInt( chIdStr );
            
            if( chId <= 0 )     //TODO: quais sao as condicoes para ele nao tem um CH?
            {
                chId = 1;
            }
        }
        catch( Exception err )
        {
            System.out.println( "Error: " + err.getMessage() );
        }
        
        Route route = convertNodeToRoute( fromNode );
        
        List<Integer> neighborList = getNeighborMap().get( fromId );
        
        
        if( chId == 1 )
        {
            if( neighborList.contains( toId ) )
            {
                return route;
            }
            else
            {
                return null;
            }
        }
        else
        {
            Node chNode = getTopology().getNodeMap().get( chId );
            Route chRoute = convertNodeToRoute( chNode );
            route.getRouteList().add( chRoute );
            
            return route;
        }
    }

    @Override
    public String getName()
    {
        return "LEACH";
    }
    
}
