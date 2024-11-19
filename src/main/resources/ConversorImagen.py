import sys
sys.stdout.reconfigure(encoding='utf-8')
sys.stderr.reconfigure(encoding='utf-8')
import os
import time
from docx2pdf import convert
import pikepdf

def convert_word_to_pdf(input_file_path, output_dir, nombre):
    try:
        # Generar un nombre único para el archivo PDF
        base_filename = nombre
        pdf_filename = f"{base_filename}_converted.pdf"
        pdf_path = os.path.join(output_dir, pdf_filename)

        # Convertir el archivo DOCX a PDF y guardarlo en el sistema de archivos
        convert(input_file_path, pdf_path)
        print(f"Conversión completada con éxito a {pdf_path}")

        try:
          # Abrir el archivo PDF
          with pikepdf.open(pdf_path) as pdf:
              num_pages = len(pdf.pages)  # Contar las páginas
          print(num_pages)
        except Exception as e:
          print(f"Error al contar las páginas: {e}")
          sys.exit(1)

        return pdf_path
    except Exception as e:
        print(f"Error durante la conversión: {e}", file=sys.stderr)
        sys.exit(1)

def extract_first_page(input_pdf, output_pdf):
    try:
        # Abrir el archivo PDF
        with pikepdf.open(input_pdf) as pdf:
            # Crear un nuevo archivo PDF para la primera página
            first_page_pdf = pikepdf.new()
            first_page_pdf.pages.append(pdf.pages[0])  # Añadir la primera página

            # Guardar la primera página en un nuevo archivo PDF
            first_page_pdf.save(output_pdf)

        print(f"Primera página extraída con éxito a {output_pdf}.")
    except Exception as e:
        print(f"Error al extraer la primera página: {e}")
        sys.exit(1)

if __name__ == "__main__":
    # Comprobar argumentos
    if len(sys.argv) < 4:
        print("Error: Se requieren tres argumentos: la ruta del archivo de entrada, el directorio de salida y el nombre del archivo.", file=sys.stderr)
        sys.exit(1)

    # Extracción de parámetros
    input_file_path = sys.argv[1]
    output_dir = sys.argv[2]
    nombre = sys.argv[3]

    # Ejecutar la conversión de Word a PDF
    pdf_file = convert_word_to_pdf(input_file_path, output_dir, nombre)

    # Generar la ruta del archivo para la primera página
    first_page_pdf_path = os.path.join(output_dir, f"{nombre}_first_page.pdf")

    # Extraer la primera página del PDF generado
    extract_first_page(pdf_file, first_page_pdf_path)


    # Añadir un retraso breve antes de intentar eliminar el archivo
    time.sleep(1)  # Espera un segundo para asegurar la liberación

    # Eliminar el archivo PDF original después de contar páginas y extraer la primera página
    try:
        os.remove(pdf_file)
        print(f"Archivo temporal {pdf_file} eliminado con éxito.")
    except Exception as e:
        print(f"Error al eliminar el archivo PDF: {e}", file=sys.stderr)
