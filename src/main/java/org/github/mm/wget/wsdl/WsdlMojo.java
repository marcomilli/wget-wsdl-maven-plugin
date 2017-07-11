/*
 *
 */
package org.github.mm.wget.wsdl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Milli Marco
 */
@Mojo(name = "wget-wsdl", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class WsdlMojo extends AbstractMojo{

    /**
     * Name of root directory where file is downloaded
     */
    @Parameter(required = true)
    private String destinationDirectory;
    
    /**
     * Name of root directory where file is downloaded
     */
    @Parameter(defaultValue = "false")
    private boolean keep;
    
    /**
     * Create a sub directory for any wsdl file if it set 'true'
     */
    @Parameter(defaultValue = "false")
    private boolean createSubDir;
    
    /**
     * Set a prefix name to the import file in wsdl
     */
    @Parameter(defaultValue = "")
    private String importPrefixName;
    
    /**
     * Set a postfix name to the import file in wsdl
     */
    @Parameter(defaultValue = "")
    private String importPostfixName;
    
    @Parameter
    private List<String> wsdlUrls;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        if(!keep){
            FileSetManager fsm = new FileSetManager();
            FileSet fs = new FileSet();
            fs.setDirectory(destinationDirectory);
            try {
                fsm.delete(fs);
                WsdlUtils.createDirectory(destinationDirectory);
            } catch (IOException ex) {
                Logger.getLogger(WsdlMojo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        
        for (String urlItem : wsdlUrls) {
            try {
                String desString = destinationDirectory;
                URL url = new URL(urlItem);
                
                getLog().info("Url: "+ url.toString());
                
                String file = WsdlUtils.parseLocationURL( url );
                getLog().info("Nome file "+file);

                String ext = WsdlUtils.getExtentionFile(file);

                String nomeSubDir = file.replace(ext, "").replace(".", "");
                getLog().info("Nome dubDirectory: "+nomeSubDir);
                
                if(createSubDir){
                    desString += File.separator + nomeSubDir;
                    getLog().info("Creo la directory: "+desString);
                    WsdlUtils.createDirectory(desString);
                }
                
                String fileName = WsdlUtils.parseLocationURL(url);
                
                download(url, desString, fileName);
                
            } catch (Exception e) {
                getLog().info("Error: "+e.getCause()+" - "+e.getMessage());
            }
        }
    }
    
    
    private void download(URL url, String destination, String fileName) throws ParserConfigurationException, IOException, SAXException, TransformerException{

        Document doc = WsdlUtils.parseDocument(url);
        
        final NodeList wsdlImport = doc.getElementsByTagName("wsdl:import");
        
        for( int i=0; i < wsdlImport.getLength() ; ++i ) {

            Element e = (Element) wsdlImport.item(i);

            String location = e.getAttribute("location");

            getLog().info("wsdl:import location=" + location );
            if(null == location || "".equals(location)){
                continue;
            }

            URI uri = WsdlUtils.resolveURI( url, location );    
            if( uri != null ) {  
              
                final URL locationURL = uri.toURL();

                String newLocation = WsdlUtils.addPrefix(fileName, importPrefixName);
                newLocation = WsdlUtils.addPostfix(fileName, importPostfixName);
                e.setAttribute("location", newLocation);

                download( locationURL, destination, newLocation);
            }
        }
        
        final NodeList xsdImport = doc.getElementsByTagName("xsd:import");

        for( int i=0; i < xsdImport.getLength() ; ++i ) {

            Element e = (Element) xsdImport.item(i);

            String location = e.getAttribute("schemaLocation");
            System.out.printf("xsd:import location=[%s]\n", location );
            if(null == location){
                continue;
            }

            URI uri = WsdlUtils.resolveURI( url, location );    
            if( uri != null ) {  
                final URL locationURL = uri.toURL();

                String newLocation = WsdlUtils.addPrefix(fileName, importPrefixName);
                newLocation = WsdlUtils.addPostfix(fileName, importPostfixName);
                e.setAttribute("schemaLocation", newLocation);

                download( locationURL, destination, newLocation);
            }
        }

        final NodeList xsInclude = doc.getElementsByTagName("xs:include");

        for( int i=0; i < xsInclude.getLength() ; ++i ) {

            Element e = (Element) xsInclude.item(i);

            String location = e.getAttribute("schemaLocation");
            getLog().info("xsd:include location=[%s] "+location );
            if(null == location || "".equals(location)){
                continue;
            }

            URI uri = WsdlUtils.resolveURI( url, location );    
            if( uri != null ) {  

                final URL locationURL = uri.toURL();

                String newLocation = WsdlUtils.addPrefix(fileName, importPrefixName);
                newLocation = WsdlUtils.addPostfix(fileName, importPostfixName);
                e.setAttribute("schemaLocation", newLocation);

                download( locationURL, destination, newLocation);
            }
        }

        final NodeList xsImport = doc.getElementsByTagName("xs:import");

        for( int i=0; i < xsImport.getLength() ; ++i ) {

            Element e = (Element) xsImport.item(i);

            String location = e.getAttribute("schemaLocation");
            getLog().info("xs:import location=[%s] "+location );
            if(null == location || "".equals(location)){
                continue;
            }

            URI uri = WsdlUtils.resolveURI( url, location );    
            if( uri != null ) {  

                final URL locationURL = uri.toURL();

                String newLocation = WsdlUtils.addPrefix(fileName, importPrefixName);
                newLocation = WsdlUtils.addPostfix(fileName, importPostfixName);
                e.setAttribute("schemaLocation", newLocation);

                download( locationURL, destination, newLocation);
            }
        }
        
        final NodeList xsRedefine = doc.getElementsByTagName("xs:redefine");

        for( int i=0; i < xsRedefine.getLength() ; ++i ) {

            Element e = (Element) xsRedefine.item(i);

            String location = e.getAttribute("schemaLocation");

            getLog().info("xs:redefine location=[%s] "+location );
            if(null == location || "".equals(location)){
                continue;
            }

            URI uri = WsdlUtils.resolveURI( url, location );    
            if( uri != null ) {  

                final URL locationURL = uri.toURL();

                String newLocation = WsdlUtils.addPrefix(fileName, importPrefixName);
                newLocation = WsdlUtils.addPostfix(fileName, importPostfixName);
                e.setAttribute("schemaLocation", newLocation);

                download( locationURL, destination, newLocation);
            }
        }
        
        fileName = destination + File.separator + fileName;
        getLog().info("Path + Nome file "+fileName);
        WsdlUtils.writeXmlFile( doc, fileName );

    }
    
}
