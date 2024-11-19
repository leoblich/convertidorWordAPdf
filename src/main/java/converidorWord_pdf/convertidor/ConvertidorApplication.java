package converidorWord_pdf.convertidor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebSessionIdResolverAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		TaskSchedulingAutoConfiguration.class,
		WebFluxAutoConfiguration.class,
		ThymeleafAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		WebMvcAutoConfiguration.class,  // Desactivar WebMvc si no usas vistas
//		WebSocketAutoConfiguration.class, // Desactivar WebSockets si no los necesitas
		WebSessionIdResolverAutoConfiguration.class // Si no usas sesiones reactivas
})
public class ConvertidorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConvertidorApplication.class, args);
	}

}
