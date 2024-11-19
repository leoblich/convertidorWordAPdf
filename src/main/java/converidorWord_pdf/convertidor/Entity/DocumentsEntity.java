package converidorWord_pdf.convertidor.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentsEntity {

    private Long id;
    private String title;
    private String description;
    private String format;
    private Float price;
    private int numeroDePaginas = 0;
    private String fileNameId;
    private String fileUrlPublic;
    private String fileUrlPrivate;
    private String fileDownLoadToken;
    private Long fileCreateTime = 0L;

    private String category;

    private Boolean borradoLogico = false;

    private LocalDate createdAt = LocalDate.now();
    private String imagenNameId;
    private String imagenUrlPublic;
    private String imagenUrlprivate;
    private String imageDownLoadToken;
    private Long imageCreateTime = 0L;

    private int countLikes = 0;

    private Integer countPreView = 0;

}
