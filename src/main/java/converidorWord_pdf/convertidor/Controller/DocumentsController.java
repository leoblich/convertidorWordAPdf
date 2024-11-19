package converidorWord_pdf.convertidor.Controller;

import converidorWord_pdf.convertidor.Entity.Dto.DocumentDto;
import converidorWord_pdf.convertidor.Service.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api")
public class DocumentsController {

    @Value("${script.path}")
    private String scriptPath;

    @Autowired
    public DocumentsService documentsService;

    // guardado de un documento
    @PostMapping()
    public boolean postDocument(@RequestParam("file") MultipartFile file) throws GeneralSecurityException, IOException {
        if(scriptPath == null || scriptPath.isEmpty()) {
            throw new IllegalStateException("La propiedad script.path no está configurada");
        }

                documentsService.guardarDocument(file, new File(scriptPath));

        return true;
    }

    @GetMapping()
    public  String probandoConeccion() {
        return "conectado";
    }

}
