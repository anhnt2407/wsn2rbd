package br.cin.ufpe.wsn2rbd.xml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.modcs.tools.rbd.blocks.Block;
import org.modcs.tools.rbd.blocks.BlockBridge;
import org.modcs.tools.rbd.blocks.BlockChain;
import org.modcs.tools.rbd.blocks.BlockExponential;
import org.modcs.tools.rbd.blocks.BlockParallel;
import org.modcs.tools.rbd.blocks.BlockSeries;
import org.modcs.tools.rbd.blocks.RBDModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Esta classe é responsável por abrir um arquivo XML e criar um model RBD dele.
 * Esta classe só pode ser usada por outra classe dentro desse pacote. Isso foi
 * necessário para força o usuário a usar o método RbdFile.open().
 * 
 * @author avld
 */
public class RbdFileOpen
{
    
    protected RbdFileOpen()
    {
        // do nothing
    }
    
    /**
     * Abri um arquivo XML e retorna o modelo RBD dentro dele
     * 
     * @param filename          arquivo o qual deseja abrir
     * @return                  modelo RBD dentro do arquivo
     * @throws Exception        caso o arquivo não seja um XML
     * @throws IOException      problema causado por tentar abrir um arquivo
     */
    protected RBDModel open( String filename ) throws Exception, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document doc = docBuilder.parse( new File( filename ) );
        Element rootElement = doc.getDocumentElement();
        
        // --------------
        
        RBDModel model = new RBDModel();
        
        for( int i = 0; i < rootElement.getChildNodes().getLength(); i++ )
        {
            Node node = rootElement.getChildNodes().item( i );
            
            if( node.getNodeName().isEmpty() 
                    || "#text".equalsIgnoreCase( node.getNodeName() ) )
            {
                continue ;
            }
            if( "name".equalsIgnoreCase( node.getNodeName() ) )
            {
                model.setName( getValue( node ) );
            }
            else if( "label_list".equalsIgnoreCase( node.getNodeName() ) )
            {
                //TODO: como tratar isso aqui?
            }
            else if( isBlock( node ) )
            {
                model.setModel( getBlock( node ) );
            }
        }
        
        return model;
    }
    
    // ---------------------------- //
    // ---------------------------- // BLOCK LIST
    // ---------------------------- //
    
    /**
     * Criar um bloco qualquer (ex. exponencial ou paralelo) de uma parte do arquivo XML.
     * 
     * @param node  parte do XML que deseja converter em um bloco
     * @return      bloco convertido
     */
    private Block getBlock( Node node )
    {
        if( "block".equalsIgnoreCase( node.getNodeName() ) )
        {
            return getBlockExponential( node );
        }
        else if( "series".equalsIgnoreCase( node.getNodeName() ) )
        {
            return getBlockChain( new BlockSeries( "series" ) , node );
        }
        else if( "parallel".equalsIgnoreCase( node.getNodeName() ) )
        {
            return getBlockChain( new BlockParallel( "parallel" ) , node );
        }
        else if( "brigde".equalsIgnoreCase( node.getNodeName() ) )
        {
            BlockChain chain = getBlockChain( new BlockSeries( "series" ) , node );
            
            BlockBridge bridge = new BlockBridge( "bridge" , 0 , null , null );
            bridge.setName( chain.getName() );
            bridge.setBlocks( chain.getBlocks() );
            
            return bridge;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Criar um bloco exponencial de uma parte do arquivo XML.
     * 
     * @param node  parte do XML que deseja converter em um bloco
     * @return      bloco convertido
     */
    private BlockExponential getBlockExponential( Node node )
    {
        BlockExponential block = new BlockExponential( "block" , null , null );
        
        NodeList list = node.getChildNodes();
        for( int i = 0 ; i < list.getLength() ; i++ )
        {
            Node n = list.item( i );
            
            if( n.getNodeName().isEmpty() 
                    || "#text".equalsIgnoreCase( n.getNodeName() ) )
            {
                continue ;
            }
            else if( "name".equalsIgnoreCase( n.getNodeName() ) )
            {
                block.setName( getValue( n ) );
            }
            else if( "type".equalsIgnoreCase( n.getNodeName() ) )
            {
                //TODO: What is the field in the BlockExponential.class?
            }
            else if( "status".equalsIgnoreCase( n.getNodeName() ) )
            {
                String value = getValue( n );
                
                if ( "broken".equalsIgnoreCase( value ) )
                {
                    block.setStateBroken();
                }
                else if ( "default".equalsIgnoreCase( value ) )
                {
                    block.setStateDefault();
                }
                else if ( "working".equalsIgnoreCase( value ) )
                {
                    block.setStateWorking();
                }
            }
            else if( "availability".equalsIgnoreCase( n.getNodeName() ) )
            {
                //TODO: What is the field in the BlockExponential.class?
            }
            else if( "mttf".equalsIgnoreCase( n.getNodeName() ) )
            {
                String value = getValue( n );
                block.setFailureRate( Double.parseDouble( value ) );
            }
            else if( "mttr".equalsIgnoreCase( n.getNodeName() ) )
            {
                String value = getValue( n );
                block.setRepairRate( Double.parseDouble( value ) );
            }
            else if( "cost".equalsIgnoreCase( n.getNodeName() ) )
            {
                String value = getValue( n );
                block.setCost( Double.parseDouble( value ) );
            }
        }
        
        return block;
    }
    
    /**
     * Insere dados, capturados do arquivo XML, em um bloco do tipo Chain.
     * 
     * @param chain Block Seriado, Paralelo ou Ponte que deseja inserir os dados
     * @param node  parte do XML que deseja converter em um bloco
     * @return      bloco com os dados inserido
     */
    private BlockChain getBlockChain( BlockChain chain , Node node )
    {
        NodeList list = node.getChildNodes();
        
        for( int i = 0 ; i < list.getLength(); i++ )
        {
            Node n = list.item( i );
            
            if( n.getNodeName().isEmpty() 
                    || "#text".equalsIgnoreCase( n.getNodeName() ) )
            {
                continue ;
            }
            else if( "name".equalsIgnoreCase( n.getNodeName() ) )
            {
                chain.setName( getValue( n ) );
            }
            else
            {
                Block block = getBlock( n );

                if( block != null )
                {
                    chain.addBlock( block );
                }
            }
        }
        
        return chain;
    }
    
    // ---------------------------- //
    // ---------------------------- // Others
    // ---------------------------- //
    
    /**
     * Verifica se a parte do XML é um bloco (ex. seriado ou paralelo)
     * 
     * @param node      parte do XML
     * @return          true, caso seja um bloco valido; false, caso não.
     */
    private static boolean isBlock( Node node )
    {
        String[] blockNames = new String[]{ "block" , "series" , "parallel" , "bridge" };
        String name = node.getNodeName();
        
        for( String bn : blockNames )
        {
            if ( bn.equalsIgnoreCase( name ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Retorna um elemento texto dentro de um Node
     * 
     * @param node  node o qual deseja retirar um texto
     * @return      elemento texto retirado
     */
    private String getValue( Node node )
    {
        return node.getChildNodes().item( 0 ).getNodeValue();
    }
}
