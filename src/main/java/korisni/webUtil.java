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
 * Klasa <code>webUtil</code> sadrži pomoćne metode koje se koriste u 
 * web aplikaciji, kao što su prikaz poruka, rad sa slikama i dobijanje 
 * trenutnog datuma.
 * @author Amel Džanić
 */
public class webUtil {
    /**
     * Metoda <code>poruka</code> prikazuje poruku korisniku koristeći 
     * JSF FacesMessage.
     * 
     * @param s Poruka koja će biti prikazana korisniku.
     */
    private static void poruka(String s){
        FacesMessage message = new FacesMessage(s);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("", message);
    }
    /**
     * Metoda <code>errPoruka</code> prikazuje poruku o grešci korisniku koristeći
     * JSF FacesMessage. Postoje tri verzije metode: 
     * <ul>
     * <li> jedna koja prima naslov, poruku i komponentu,</li>
     * <li> druga koja prima samo poruku i komponentu,</li>
     * <li> i treća koja prima samo poruku.</li>
     * </ul>
     * 
     * @param naslov Naslov poruke o grešci.
     * @param poruka Tekst poruke o grešci.
     * @param komponenta Komponenta kojoj je poruka vezana (može biti null).
     */
    public static void errPoruka(String naslov, String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, naslov, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    /**
     * Metoda <code>errPoruka</code> prikazuje poruku o grešci korisniku koristeći
     * JSF FacesMessage. Ova verzija metode prima samo poruku i komponentu, 
     * a naslov poruke je isti kao tekst poruke. 
     * {@link #errPoruka(String naslov, String poruka, String komponenta) }
     * 
     * @param poruka Tekst poruke o grešci.
     * @param komponenta Komponenta kojoj je poruka vezana (može biti null).
     */
    public static void errPoruka(String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, poruka, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    /**
     * Metoda <code>errPoruka</code> prikazuje poruku o grešci korisniku koristeći
     * JSF FacesMessage. Ova verzija metode prima samo poruku, 
     * a naslov poruke je isti kao tekst poruke, poziva metodu 
     * {@link #errPoruka(String poruka, String komponenta) errPoruka(String poruka, String komponenta)}
     *  sa null komponentom.
     * 
     * @param poruka Tekst poruke o grešci.
     */

    public static void errPoruka(String poruka){
        errPoruka(poruka, null);
    }
    /**
     * Metoda <code>warPoruka</code> prikazuje upozoravajuću poruku korisniku koristeći
     * JSF FacesMessage. Ova metoda prima tekst poruke i komponentu kojoj je poruka vezana,
     *  a naslov poruke je isti kao tekst poruke.
     *  
     * @param poruka Tekst upozoravajuće poruke.
     * @param komponenta Komponenta kojoj je poruka vezana (može biti null).
     */
    public static void warPoruka(String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, poruka, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    /**
     * Metoda <code>infoPoruka</code> prikazuje informacionu poruku korisniku koristeći
     * JSF FacesMessage. Ova metoda prima tekst poruke i komponentu kojoj je poruka vezana,
     *  a naslov poruke je isti kao tekst poruke.
     *  
     * @param poruka Tekst informacione poruke.
     * @param komponenta Komponenta kojoj je poruka vezana (može biti null).
     */
    public static void infoPoruka(String poruka, String komponenta){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, poruka, poruka);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(komponenta, message);
    }
    /**
     * Metoda <code>vratiPath</code> vraća fizičku putanju do direktorija 
     * "img" unutar direktorija resources/img/ web aplikacije.
     * 
     * @return Fizička putanja do direktorija "img".
     */
    public static String vratiPath(){
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String      imgPath=servletContext.getRealPath("/resources/img/");
        return imgPath;
    }
    /**
     * Metoda <code>vratiPathDB</code> vraća fizičku putanju do direktorija 
     * "DB" unutar direktorija resources/DB/ web aplikacije.
     * 
     * @return Fizička putanja do direktorija "DB".
     */
     public static String vratiPathDB(){
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String      dbPath=servletContext.getRealPath("/resources/DB/");
        return dbPath;
    }
    /**
     * Metoda <code>resize</code> prima putanju do ulazne slike, mijenja njenu veličinu na širinu od 400 piksela
     * i proporcionalnu visinu, te zatim sprema novu sliku na istu lokaciju sa istim imenom, zamjenjujući originalnu sliku.
     * 
     * @param inputImagePath Putanja do ulazne slike koja će biti promijenjena.
     * @throws IOException Ako dođe do greške prilikom čitanja ili pisanja slike.
     */
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
    /**
     * Metoda <code>getImageDimension</code> prima datoteku slike i vraća njene dimenzije (širinu i visinu) kao objekt klase Dimension.
     * Metoda koristi ImageIO da pročita dimenzije slike bez potrebe za učitavanjem cijele slike u memoriju, što je efikasnije za velike slike.
     * Ako datoteka nema ekstenziju ili nije prepoznat kao podržana slika, metoda baca IOException.
     * @param imgFile Datoteka slike za koju se traže dimenzije.
     * @return Objekt klase Dimension koji sadrži dimenzije slike.
     * @throws IOException Ako dođe do greške prilikom čitanja datoteke slike.
     */
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
    /**
     * Metoda <code>rename</code> prima dva Path objekta, jedan koji predstavlja 
     * izvorni put do datoteke i drugi koji predstavlja odredišni put.
     * Metoda koristi Files.move() da premjesti datoteku sa izvornog puta na odredišni put,
     *  zamjenjujući postojeću datoteku na odredišnom putu ako ona već postoji. 
     * Ako dođe do greške prilikom premještanja datoteke, metoda hvata IOException 
     * i prikazuje poruku o grešci korisniku koristeći metodu poruka().
     * @param source Path do izvornog fajla koji se želi premjestiti ili preimenovati.
     * @param destination Path do odredišnog fajla. Ako datoteka već postoji na ovom putu, bit će zamijenjena.
     */
    public static void rename(Path source, Path destination) {
        try{
        Files.move(source, destination, REPLACE_EXISTING);
        } catch (IOException e){
            webUtil.poruka(e.getMessage());
        }
    }
    /**
     * Metoda <code>trenutniDatum</code> vraća trenutni datum u formatu "yyyy-MM-dd" kao String.
     * Metoda koristi DateTimeFormatter za formatiranje datuma i LocalDateTime za dobivanje trenutnog datuma i vremena.
     * @return Trenutni datum u formatu "yyyy-MM-dd".
     */
    public static String trenutniDatum(){
        String datum;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();  
        datum = dtf.format(now);
        return datum;
    }
    /**
     * Metoda <code>testUsp</code> prima poruku kao argument i sprema je u Flash scope pod ključem "successMessage".
     * Ova metoda se koristi za prikazivanje uspješnih poruka korisniku nakon određenih akcija,
     *  kao što su uspješno izvršene operacije. Poruka će biti dostupna na sljedećoj stranici nakon redirekcije.    
     * @param poruka Tekst poruke koja će biti prikazana korisniku kao uspješna poruka.
     */
    public static void testUsp(String poruka){
        FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().put("successMessage", poruka);
    }
    /**
     * Metoda <code>testErr</code> prima poruku kao argument i sprema je u Flash scope pod ključem "errorMessage".
     * Ova metoda se koristi za prikazivanje poruka o grešci korisniku
     * nakon određenih akcija koje nisu uspješne. Poruka će biti dostupna na sljedećoj stranici nakon redirekcije.
     * 
     * @param poruka Tekst poruke koja će biti prikazana korisniku kao poruka o grešci.
     */
    public static void testErr(String poruka){
        FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().put("errorMessage", poruka);
    }
    
}
