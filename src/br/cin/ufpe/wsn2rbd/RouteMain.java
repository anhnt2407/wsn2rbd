package br.cin.ufpe.wsn2rbd;

import br.cin.ufpe.wsn2cpn.Node;
import br.cin.ufpe.wsn2cpn.Topology;
import br.cin.ufpe.wsn2cpn.deploy.NodeDeploy;
import br.cin.ufpe.wsn2cpn.deploy.NodeDeployFactory;
import br.cin.ufpe.wsn2rbd.protocols.*;

/**
 *
 * @author avld
 */
public class RouteMain
{

    public static void main( String arg[] ) throws Exception
    {
        System.out.println( "Gerando a topologia..." );
        Topology top = getTopology();
        
        System.out.println( "Gerando as rotas entre A e B..." );
        Route route = getRoute( top , 21 , 1 );
        
        System.out.println( "Imprimindo as rotas geradas..." );
        imprimirRoute( route );
    }
    
    /**
     * Cria uma topologia qualquer
     * 
     * @return
     * @throws Exception 
     */
    public static Topology getTopology() throws Exception
    {
        NodeDeploy deploy = NodeDeployFactory.getNodeDeploy( "Random Uniform" );
        
        deploy.setLocation( 0 , 0 );
        deploy.setSize( 100 , 100 );
        deploy.setNodeSize( 21 );
        
        deploy.setNodeDefault( new Node() );
        
        return deploy.create( new Topology() );
    }
    
    /**
     * 
     * 
     * @param topology
     * @return
     * @throws Exception 
     */
    public static Route getRoute( Topology topology , int fromID , int toID ) throws Exception
    {
        Protocol protocol = new FloodingProtocol();
        protocol.setTopology( topology );
        return protocol.getRoute( fromID , toID );
    }
    
    // ------------------------------- //
    // ------------------------------- //  IMPRIMIR NA TELA
    // ------------------------------- //
    
    public static void imprimirRoute( Route route )
    {
        if( route == null )
        {
            System.out.println( "SEM ROTA PARA IMPRIMIR!" );
        }
        else
        {
            System.out.println( "------------------ ROTA DE " + route.getName() );
            imprimirNaTela( route , "" );
        }
    }
    
    private static void imprimirNaTela( Route route , String inicial )
    { 
        System.out.print( inicial );
        System.out.print( "node ID: " + route.getName()  );
        System.out.println();
        
        for( Route routeChild : route.getRouteList() )
        {
            imprimirNaTela( routeChild , inicial + " " );
        }
    }
}
