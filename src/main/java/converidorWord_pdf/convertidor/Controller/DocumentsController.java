package converidorWord_pdf.convertidor.Controller;

import converidorWord_pdf.convertidor.Entity.Dto.DocumentDto;
import converidorWord_pdf.convertidor.Service.DocumentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api")
public class DocumentsController {

    @Value("${script.path}")
    private String scriptPath;

    @Autowired
    public DocumentsService documentsService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace(); // Esto registrará la pila completa del error
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }

    private static final Logger logger = LoggerFactory.getLogger(DocumentsController.class);

    // guardado de un documento
    @PostMapping("/document")
    public ResponseEntity<Object> postDocument(@RequestParam("file") MultipartFile file) throws GeneralSecurityException, IOException {
        if (scriptPath == null || scriptPath.isEmpty()) {
            throw new IllegalStateException("La propiedad 'script.path' no está configurada.");
        }

        // Verificar que el archivo no esté vacío
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al convertir el archivo a PDF.");
        }
// "/src/main/resources/conversorLibreOffice"
        try {
            // Llamar al servicio para guardar el documento
//            documentsService.guardarDocument(file, new File("C:\\PROYECTO_PERCY\\pruebaparadeployar\\convertidor\\src\\main\\resources\\conversorLibreOffice.py"));
            byte[] respuesta = documentsService.guardarDocument(file, new File(scriptPath));

            String fileName = file.getOriginalFilename();
            System.out.println("********" + fileName);

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_PDF);
            // Construir la cabecera Content-Disposition
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
//            ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                    .filename(fileName)
                    .build();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

            // Crear la respuesta
            return ResponseEntity.ok()
                    .headers(headers) // Usa el objeto HttpHeaders completo
                    .body(respuesta); // Envía el contenido del archivo como cuerpo

        } catch (Exception e) {
            e.printStackTrace(); // Registrar cualquier excepción en los logs
            return ResponseEntity.status(500).body("Ocurrió un error al guardar el documento: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<String> probandoConeccion() {
        logger.info("Esto es un log de nivel INFO.");
        logger.debug("Esto es un log de nivel DEBUG.");
        logger.error("Esto es un log de nivel ERROR.");
        logger.info("Esto es un log de prueba.");
        return ResponseEntity.ok("Conexión exitosa");
    }

}
