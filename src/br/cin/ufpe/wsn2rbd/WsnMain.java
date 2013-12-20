package br.cin.ufpe.wsn2rbd;

import br.cin.ufpe.wsn2cpn.Topology;
import br.cin.ufpe.wsn2rbd.xml.RbdFile;
import com.local.entities.ProjectDesdac;
import com.local.gui.JFrameDesdacTool;
import java.io.File;
import java.net.URL;
import org.modcs.tools.rbd.blocks.Block;
import org.modcs.tools.rbd.blocks.BlockChain;
import org.modcs.tools.rbd.blocks.RBDModel;
import org.modcs.tools.rbd.evaluatorrbd.ApplicationRBD;
import org.modcs.tools.rbd.evaluatorrbd.EvaluationType;
import org.modcs.tools.rbd.evaluatorrbd.ExperimentParametersRBD;
import org.modcs.tools.rbd.evaluatorrbd.LabelSet;
import org.modcs.tools.rbd.evaluatorrbd.ParametersResultsRBD;
import org.modcs.tools.rbd.file.FileStream;

/**
 *
 * @author avld
 */
public class WsnMain
{
    public static void main( String args[] ) throws Exception
    {
        System.out.println( "Gerando a topologia..." );
        Topology top = RouteMain.getTopology();
        
        System.out.println( "Gerando as rotas entre A e B..." );
        Route route = RouteMain.getRoute( top , 21 , 1 );
        
        System.out.println( "Imprimindo as rotas geradas..." );
        RouteMain.imprimirRoute( route );
        
        System.out.println( "Convertendo as rotas para o modelo RBD..." );
        ConvertRouteToRBD convert = new ConvertRouteToRBD();
        RBDModel model = convert.convert( route );
        
        System.out.println( "Imprimindo o modelo RBD..." );
        imprimirNaTela( model );
        
        System.out.println( "Salvando o modelo RBD..." );
        salvar( "./rbd_dinamic.xml" , model );
        RbdFile.save( "./rbd_example.xml" , model );
        
        System.out.println( "Avaliando o modelo RBD..." );
        EvaluateRBD evaluator = new EvaluateRBD( model );
        double disponibilidade = evaluator.evaluate();
        
        System.out.println( "Disponibilidade: " + disponibilidade );
    }
    
    // --------------------------------- //
    // --------------------------------- // IMPRIMIR UM MODELO RBD NO CONSOLE
    // --------------------------------- //
    
    /**
     * Imprimi na tela um modelo RBD
     * 
     * @param model        modelo RBD
     */
    public static void imprimirNaTela( RBDModel model )
    {
        String name = model.getName() == null ? "" : model.getName().toUpperCase();
        
        System.out.println( "----------------------------- IMPRIMIR MODELO RBD: " + name );
        
        imprimirNaTela( model.getModel() , "" );
        
        System.out.println( "----------------------------------------------------" );
    }
    
    /**
     * Imprimi na tela um Block. Caso ele seja um BlockChain (ex. BlockSeries),
     * os seus filhos serão mostrados na tela também e assim sucessivamente. Ou seja,
     * essa função é recursiva. A cada filho, é acrescentado um espaço " " na tela.
     * 
     * @param block     block que será mostrado na tela
     * @param inicial   posição inicial na tela
     */
    private static void imprimirNaTela( Block block , String inicial )
    {
        String tipo = block.getClass().getSimpleName();
        
        System.out.print( inicial );
        System.out.print( "block name: " + block.getName()  );
        System.out.print( " | tipo: " + tipo );
        System.out.println();

        if( block instanceof BlockChain )
        {
            for( Block blockChild : ((BlockChain) block).getBlocks() )
            {
                imprimirNaTela( blockChild , inicial + " " );
            }
        }
    }
    
    // --------------------------------- //
    // --------------------------------- // AVALIAR UM MODELO RBD
    // --------------------------------- //
    
    /**
     * Função responsável por configurar o experimentos
     * 
     * @return 
     */
    public static ExperimentParametersRBD getParameters()
    {
        ExperimentParametersRBD param = new ExperimentParametersRBD();
        param.setEvaluationTime( 100.0 );
        param.setEvaluationType( EvaluationType.INSTANTANEOUS_AVAILABILITY_EVAL );
        
        return param;
    }
    
    /**
     * Avalia um modelo RBD e retorna os dados sobre a avaliação
     * 
     * @deprecated 
     * @return 
     */
    public static ParametersResultsRBD evaluate( RBDModel model )
    {
        // 0 = default
        // 1 = sum of disjoint points (p modelo com equipamento repetido)

        int typeRbdCalculation = 0;

        if ( model.hasRepeatedComponent() )
        {
            typeRbdCalculation=1;
        }
        else
        {
            typeRbdCalculation=0;
        }

        ProjectDesdac project = new ProjectDesdac();
        project.setAppRBD( new ApplicationRBD( model , null , null ) );

        return JFrameDesdacTool.rbdAnalysis( project , typeRbdCalculation );
    }
    
    // --------------------------------- //
    // --------------------------------- // TRABALHA COM UM ARQUIVO
    // --------------------------------- //
    
    /**
     * Abri um arquivo e retorna um projeto com todos os modelos
     * 
     * @param filename      nome do arquivo (ex. /home/avld/teste.xml ou c:\teste.xml)
     * @return              projeto
     * @throws Exception    caso haja problema ao abrir o arquivo (ex. o arquivo não existe)
     */
    private static ProjectDesdac abrirProjeto( String filename ) throws Exception
    {
//        FileInputStream fis = new FileInputStream( new File( filename ) );
//        
//        XStream xstream = new XStream();
//        ProjectDesdac project = (ProjectDesdac) xstream.fromXML( fis );
//        xstream = null;
//        
//        return project;
        return null;
    }
    
    /**
     * Abri um arquivo e retorna o modelo RBD dentro dele
     * 
     * @param filename      nome do arquivo (ex. /home/avld/teste.xml ou c:\teste.xml)
     * @return              modelo RBD
     * @throws Exception    caso haja problema ao abrir o arquivo (ex. o arquivo não existe)
     */
    public static RBDModel abrir( String filename ) throws Exception
    {
        return abrirProjeto( filename ).getAppRBD().getRBDModel();
    }
    
    /**
     * salva um modelo dentro de um arquivo
     * 
     * @param filename      nome do arquivo (ex. /home/avld/teste.xml ou c:\teste.xml)
     * @param model         modelo em RBD
     * @throws Exception    caso haja problema ao salvar o arquivo (ex. não tem permissão de escrita)
     */
    public static void salvar( String filename , RBDModel model ) throws Exception
    {
        //É necessario abrir um projeto base para evitar error quando for visualizar o modelo no Mercury
        URL filenameBase = WsnMain.class.getResource( "example.xml" );
        ProjectDesdac project = abrirProjeto( filenameBase.getFile() );
        
        //Como tem foi aberto um projeto base, é necessário editar alguns parametros desse projeto
        //tal como o arquivo
        project.setFile( new File( filename ) );
        System.out.println( "diretorio completo: " + project.getFile().getAbsolutePath() );
        
        //e o modelo RBD
        project.setAppRBD( new ApplicationRBD( model , new FileStream() , new LabelSet() ) );
        
        //Salvar o novo modelo em outro arquivo
        JFrameDesdacTool.saveProject( project );
    }
}
