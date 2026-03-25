/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package korisni;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author TFB5
 */
public class webUtil {
    private static void poruka(String s){
        FacesMessage message = new FacesMessage(s);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("", message);
    }
    public static void errPoruka(String naslov, String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, naslov, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    public static void errPoruka(String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    public static void errPoruka(String poruka){
        errPoruka(poruka, null);
    }
    public static void warPoruka(String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, poruka, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    public static void infoPoruka(String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, poruka, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    public static String vratiPath(){
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String      imgPath=servletContext.getRealPath("/resources/img/");
        return imgPath;
    }
     public static String vratiPathDB(){
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String      dbPath=servletContext.getRealPath("/resources/DB/");
        return dbPath;
    }
     public static void resize(String inputImagePath) throws IOException{
        File inputFile=new File(inputImagePath);
        BufferedImage inputImage=ImageIO.read(inputFile);
        Dimension d = getImageDimension(inputFile);
        int h=(int) ((int)(400*d.getHeight())/d.getWidth());
        BufferedImage outputImage = new BufferedImage(400,h, inputImage.getType());
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, 400, h, null);
        g2d.dispose();       
        String formatName = inputImagePath.substring(inputImagePath.lastIndexOf(".") + 1);
        String outputImagePath=vratiPath()+"/temp."+formatName;     
        File outputImageFile = new File(outputImagePath);
        ImageIO.write(outputImage, formatName, outputImageFile);               
        rename(outputImageFile.toPath(),inputFile.toPath());        
    }
     public static Dimension getImageDimension(File imgFile) throws IOException {
      int pos = imgFile.getName().lastIndexOf(".");
      if (pos == -1)
        throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
      String suffix = imgFile.getName().substring(pos + 1);
      Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
      while(iter.hasNext()) {
        ImageReader reader = iter.next();
        try {
          ImageInputStream stream = new FileImageInputStream(imgFile);
          reader.setInput(stream);
          int width = reader.getWidth(reader.getMinIndex());
          int height = reader.getHeight(reader.getMinIndex());
          stream.close();
          return new Dimension(width, height);
        } catch (IOException e) {
          //log.warn("Error reading: " + imgFile.getAbsolutePath(), e);
        } finally {
          reader.dispose();
        }
      }

      throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
    }
    public static void rename(Path source, Path destination) {
        try{
        Files.move(source, destination, REPLACE_EXISTING);
        } catch (IOException e){
            webUtil.poruka(e.getMessage());
        }
    }
    public static String trenutniDatum(){
        String datum;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();  
        datum = dtf.format(now);
        return datum;
    }
    // Za Flash poruke
    public static void testUsp(String poruka){
        FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().put("successMessage", poruka);
    }
    public static void testErr(String poruka){
        FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().put("errorMessageMessage", poruka);
    }
    
}
