/*
 *
 */
package io.github.marcomilli.wget.wsdl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utiliy class for manage files, urls and document
 * 
 * @author Milli Marco
 */
public class WsdlUtils {

    public static String parseLocationURL( URL location ) {
        if( location==null ) throw new IllegalArgumentException( "location is null!");

        String file = location.getFile();

        int index = file.lastIndexOf('/');
        if( index!=-1 ) {
            file = file.substring(++index);
        }

        file = file.replace('?', '.').replace('=', '.');

        return file;
    }
    
    public static String getExtentionFile(String fileNanme){
        String ext = fileNanme;
        int index = fileNanme.lastIndexOf('.');
        
        if( index!=-1 ) {
            ext = fileNanme.substring(++index);
        }
        
        return ext;
    }
    
    public static boolean createDirectory(String path){
        boolean ret = false;
        Path realDest = Paths.get(path);
        try {
            Files.createDirectories(realDest);
            ret = true;
        } catch (IOException ex) {
            Logger.getLogger(WsdlUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public static Document parseDocument( URL url ) throws ParserConfigurationException, IOException, SAXException {

        if( url==null ) throw new IllegalArgumentException( "url is null!");

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        System.out.printf("download url [%s]\n", url );

        InputStream is = url.openStream();

        String file = url.getFile();

        int index = file.lastIndexOf('/');
        if( index!=-1 ) {
            file = file.substring(++index);
        }

        file = file.replace('?', '.').replace('=', '.');

        Document doc = db.parse( is );

        doc.getDocumentElement().normalize();

        is.close();

        return doc;
    }
    
    public static URI resolveURI( java.net.URL from, String to )  {

        try { 
            java.net.URI locationURI = new java.net.URI( to );

            if( locationURI.isAbsolute() ) {
                return locationURI;
            }

            return from.toURI().resolve(locationURI);
        
        } catch( URISyntaxException e ) {

            System.out.printf("error parsing URI [%s]. It will be ignored! \n", to );
            e.printStackTrace( System.err );
            
        }
        return null;
    }
    
    public static String addPrefix(String filename, String prefix){
        if(null != filename && 0 < filename.length()){
            return prefix +"_"+filename;
        }
        return "";
    }
    
    public static String addPostfix(String filename, String postfix){
        if(null != filename &&  0 < filename.length()){
            String ext = getExtentionFile(filename);
            int index = filename.lastIndexOf(ext) -1;
            filename = filename.substring(0, index);
            return filename + "_" +postfix+"."+ext;
        }
        return "";
    }
    
    public static void writeXmlFile(Document doc, String filename) throws TransformerConfigurationException, TransformerException {
            File file = new File(filename);

            if( file.exists() ) {
                
                System.out.printf( "file [%s] already exists. I delete it!\n", file );
                
                file.delete();
                
//                return;
            }
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();

            System.out.printf("writing file [%s]\n", file );

            
            xformer.transform(source, result);

    }
}
