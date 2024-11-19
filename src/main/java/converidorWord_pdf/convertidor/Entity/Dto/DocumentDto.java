package converidorWord_pdf.convertidor.Entity.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {

    private Long id;

    private String imagenNameId;
    private String imagenUrlPublic;
    private String imagenUrlprivate;
    private Long imageCreateTime;

    private String fileNameId;
    private String fileUrlPublic;
    private String fileUrlPrivate;
    private String fileDownLoadToken;
    private Long fileCreateTime;

    private boolean borradoLogico;
    private int numeroDePaginas;
    private int countLikes;
    private int countPreView;


    private String title;


    private String description;


    private String format;


    private Float price;


    private String category;


    private MultipartFile file;
}
