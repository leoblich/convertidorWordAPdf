package converidorWord_pdf.convertidor.Entity.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentPdfCovertidoDto {

    private Integer numeroDePagina = 0;
    private boolean exitoConversionPython = false;
}
