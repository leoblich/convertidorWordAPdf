package converidorWord_pdf.convertidor.Service;

import converidorWord_pdf.convertidor.Entity.Dto.DocumentDto;
import converidorWord_pdf.convertidor.Entity.Dto.DocumentPdfCovertidoDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class DocumentsService {


    private static final Logger logger = LoggerFactory.getLogger(DocumentsService.class);

    public byte[] guardarDocument(MultipartFile file, File scriptResource) throws GeneralSecurityException, IOException {

        String nombreArchivo = baseNameFile(file);

        // valido el archivo
//        validarArchivo(file);

        File tempFileGuargar = null;
        String outputFilePath = String.valueOf(scriptResource.getParentFile());
        int cantidadPaginas = 0;
        byte[] imagenWord = new byte[0];
        byte[] pdfData = new byte[0];

        String fileType = file.getContentType();
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String baseName = fileName != null ? fileName.substring(0, fileName.lastIndexOf(".")) : "file";
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        byte[] fileParaTrabajar = file.getBytes();

//        if (extension.equals("pdf")) {
//
//            System.out.println("cuento páginas del archivo");
//            CompletableFuture<Integer> cantidadPaginasPdf = contarPaginasPdf(fileParaTrabajar);
//            cantidadPaginas = cantidadPaginasPdf.join();
//
//            System.out.println("saco imagen pre-vista");
//            CompletableFuture<byte[]> imagenPdfAsync = sacarPrimeraPaginaPdfAImagen(fileParaTrabajar);
//            imagenWord = imagenPdfAsync.join();
//        }

        if (extension.equals("docx")) {
            try {
                String scriptPath = scriptResource.getAbsolutePath();

                System.out.println("llamo script de python");
                CompletableFuture<DocumentPdfCovertidoDto> respuestaConversionAsinc = llamadaScriptPythonConversion(fileParaTrabajar, scriptPath, outputFilePath, nombreArchivo);
                DocumentPdfCovertidoDto respuestaConversion = respuestaConversionAsinc.join();
                System.out.println("volviendo del script de python");

                if (respuestaConversion.isExitoConversionPython()) {

                    cantidadPaginas = respuestaConversion.getNumeroDePagina();

                    // buscar el archivo en el directorio indicado
                    pdfData = obtenerPdfDesdeDirectorio(outputFilePath, nombreArchivo);

//                    String home = System.getProperty("user.home");
//                    String rutaDescarga = home + File.separator + "Downloads";
//                    System.out.println();
//
//                    File archivo = new File(rutaDescarga + File.separator + nombreArchivo + ".pdf");

//                    try (FileOutputStream fos = new FileOutputStream(archivo)) {
//                        fos.write(pdfData);
//                        System.out.println("Archivo guardado en: " + archivo.getAbsolutePath());
//                    }

//                    System.out.println("saco imagen pre-vista");
//                    CompletableFuture<byte[]> imagenPdfAsync = sacarPrimeraPaginaPdfAImagen(pdfData);
//                    imagenWord = imagenPdfAsync.join();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (tempFileGuargar != null && tempFileGuargar.exists() && !tempFileGuargar.delete()) {
                    System.out.println("No se pudo eliminar el archivo temporal.");
                } else {
                    System.out.println("archivo temporal file - eliminado");
                }
            }
        }


        if (extension.equals("docx")) {
            String ruta = outputFilePath;
            String nombre = nombreArchivo;
            String pdfFilename = nombre + "_first_page.pdf";
            Path pdfPath = Paths.get(ruta, pdfFilename);
            File borrar = new File(String.valueOf(pdfPath));
            if (borrar.exists()) {

                boolean eliminado = borrar.delete();
                if (eliminado) {
                    System.out.println("El archivo fue eliminado PDF correctamente: ");

                } else {
                    System.out.println("No se pudo eliminar el archivo: ");
                }
            } else {
                System.out.println("El archivo no existe: ");
            }
        }

        return pdfData;

    }


    /* METODOS */


    // lista de MIME types permitidos
    public static final Set<String> ALLOWED_MIME_TYPES = Set.of("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/jpeg", "image/png", "image/gif");


    private String baseNameFile(MultipartFile file) {
        // saco el nombre del archivo
        String fileName = file.getOriginalFilename();

        // saco nombre del archivo
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private boolean validarArchivo(MultipartFile file) {

        // compruebo si el archivo viene vacío o no viene
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("error de recepción de archivo");
        }

        // saco el nombre del archivo
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("el nombre del archivo no tiene una extensión válida");
        }

        // saco el tipo de archivo
        String fileType = file.getContentType();

        // Usa URLConnection para detectar el tipo MIME
        String mimeType = URLConnection.guessContentTypeFromName(file.getOriginalFilename());

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        // compruebo que el contenido del archivo es de tipo pdf, word o imágen
        if (!(fileType.equals("application/pdf") || fileType.equals("application/msword") || fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/gif"))) {
            throw new IllegalArgumentException("el tipo de archivo no es permitido");
        }
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("el tipo de archivo no es permitido");
        }

        return true;
    }

    // Método para llamar al script de conversión en python
    private CompletableFuture<DocumentPdfCovertidoDto> llamadaScriptPythonConversion(byte[] file, String
            scriptPath, String outputFilePath, String baseName) throws IOException {
logger.info("entrando a llamado al script ");
        return CompletableFuture.supplyAsync(() -> {
            DocumentPdfCovertidoDto respuestaConversion = new DocumentPdfCovertidoDto();

            if (file.length == 0) {
                logger.error("El archivo Word es vacío.");
                throw new RuntimeException("El archivo Word es vacío.");
            }

            File tempFile = null;
            try {
                tempFile = File.createTempFile("temp", ".docx");
                tempFile.deleteOnExit();
                try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                    fileOutputStream.write((file));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            try {
                // Llama al script de Python para convertir el archivo
                ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, tempFile.getAbsolutePath(), outputFilePath, baseName);
                Process process = processBuilder.start();

                CompletableFuture<Void> outputFuture = CompletableFuture.runAsync(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
//                            System.out.println("Salida del script: " + line);
                            logger.info("Salida del script Python: " + line);  // Registra la salida estándar

                            // Verifica si la línea contiene solo un número (número de páginas)
                            if (line.matches("\\d+")) { // Este patrón verifica si la línea es un número
                                try {
                                    int numPages = Integer.parseInt(line.trim()); // Convertir el número
                                    respuestaConversion.setNumeroDePagina(numPages); // Asignar al DTO
                                } catch (NumberFormatException e) {
//                                    System.err.println("Error al convertir el número de páginas: " + line);
                                    logger.error("Error en el script Python: " + line);  // Registra los errores
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Error al leer la salida estándar del script. bufferedReader", e);
                        throw new RuntimeException("Error al leer la salida estándar del script.", e);
                    }
                });
                CompletableFuture<Void> errorFuture = CompletableFuture.runAsync(() -> {
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            if (line.contains("%|") || line.contains("[00:")) {
                                continue; // Ignorar barras de progreso
                            }
                            logger.error("Error en el script Python: " + line);  // Registra los errores
//                            System.err.println("Error del script: " + line);
                            // Maneja los errores según sea necesario
                        }
                    } catch (IOException e) {
                        logger.error("Error al leer la salida de error del script. bufferedError", e);
                        throw new RuntimeException("Error al leer la salida de error del script.", e);
                    }
                });

                // Espera a que el proceso termine
                int exitCode = process.waitFor();

                // Asegúrate de que se procesen ambas salidas antes de continuar
                CompletableFuture.allOf(outputFuture, errorFuture).join();

                if (exitCode != 0) {
                    logger.error("El script Python terminó con un error. Código de salida: " + exitCode);
                    throw new RuntimeException("Error al ejecutar el script de Python: ");
                }
                respuestaConversion.setExitoConversionPython(true);

            } catch (IOException | InterruptedException e) {
                logger.error("Error al ejecutar el script Python", e);
                throw new RuntimeException("Error al ejecutar el script de Python.", e);
            } finally {
                // Eliminar el archivo temporal si fue creado
                if (tempFile.exists() && tempFile != null && !tempFile.delete()) {
                    logger.error("error al borrar archivo temporal");
                    System.out.println("error al borrar archivo temporal");
                } else {
                    System.out.println("Archivo temporal eliminado exitosamente.");
                }
            }
            return respuestaConversion;
        });
    }

    // Método para obtener el archivo convertido a pdf del directorio
    public byte[] obtenerPdfDesdeDirectorio(String outputDir, String nombre) throws IOException {

        // Generar el nombre del archivo PDF
        String pdfFilename = nombre + "_first_page.pdf";
        Path pdfPath = Paths.get(outputDir, pdfFilename);

        // Comprobar si el archivo existe
        if (!Files.exists(pdfPath)) {
            throw new FileNotFoundException("El archivo PDF no se encontró: " + pdfPath);
        }

        // Leer el archivo PDF en un arreglo de bytes
        return Files.readAllBytes(pdfPath);
    }

    private CompletableFuture<byte[]> sacarPrimeraPaginaPdfAImagen(byte[] file) throws IOException {

        return CompletableFuture.supplyAsync(() -> {
            try (PDDocument document = PDDocument.load(file)) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                BufferedImage firstPageImage = pdfRenderer.renderImageWithDPI(0, 72);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(firstPageImage, "png", baos);
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar el PDF", e);
            }
        });

    }
}
