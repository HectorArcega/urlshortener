package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.usecases.CsvUseCase
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import jakarta.servlet.http.HttpServletRequest
import org.springframework.hateoas.server.mvc.linkTo


interface CsvController {

    /**
     * Genera y devuelve un csv en funcion del csv de entrada y del texto personalizado(aun no)
     */
    //@RequestPart("customText") customText: String = ""

    //request: HttpServletRequest
    fun generateCsv(
    @RequestPart("csvFile") csvFile: MultipartFile,
    @RequestPart("customText") customText: String?,
    request: HttpServletRequest
    
): ResponseEntity<String>
}

/**
 * Implementacion del controlador csv
 */

 //request: HttpServletRequest
@RestController
class CsvControllerImpl(
    private val csvUseCase: CsvUseCase
) : CsvController {
    @PostMapping("/api/bulk", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    override fun generateCsv(
        @RequestPart("file") csvFile: MultipartFile,
        @RequestPart("customText") customText: String?,
        request: HttpServletRequest
        
    ): ResponseEntity<String> {
        val ip = request.remoteAddr
        println("Direccion IP del cliente: $ip")

        // Convertir MultipartFile a lista de strings
        val csvContent = readUrlsFromCsv(csvFile)
        println("Primera componente del csv: ${csvContent[0]}")

        // Llamar al caso de uso para generar el CSV
        val csvContentResult = csvUseCase.createCsv(csvContent, "", ip) //customText

        // eliminar las comillas innecesarias
        var cadenaResultado = ""
        for (i in csvContentResult.indices){
            if (csvContentResult[i].toString() != "\""){
                cadenaResultado+=csvContentResult[i]
            }
        }
        // println("csvContentResult:")
        // for (item in csvContentResult) {
        //     println(item)
        // }
        // println("cadenaResultado:")
        // for (item in cadenaResultado) {
        //     println(item)
        // }
        
        //ESTE ES EL BUENO
        val csvContentWithFullUrls = cadenaResultado.lines().map { line ->
            val splitLine = line.split(",")
            if (splitLine.size >= 2) {
                val (originalUrl, processedUrl) = splitLine
                val fullUrl = linkTo<UrlShortenerControllerImpl> { redirectTo(processedUrl, request) }.toUri()
                "$originalUrl,$fullUrl"
            } else {
                // Handle the case where the line does not contain the expected format
                // You can log a warning or handle it based on your requirements
                "Invalid line format: $line"
            }
        }.joinToString("\n")
        

        // Configurar la respuesta HTTP
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("text/csv")
        headers.setContentDispositionFormData("attachment", "output.csv")

            // Devolver la respuesta HTTP con el contenido del CSV
        return ResponseEntity(csvContentWithFullUrls, headers, HttpStatus.CREATED)

         


        // Configurar la respuesta HTTP
        ///val headers = HttpHeaders()
        //headers.contentType = MediaType.parseMediaType("text/csv")
        //headers.setContentDispositionFormData("attachment", "output.csv")

        // Devolver la respuesta HTTP con el contenido del CSV
        //return ResponseEntity(csvContentResult, headers, HttpStatus.CREATED) //HTTP: 201  
    }

    // Método para convertir MultipartFile a lista de strings
    private fun readUrlsFromCsv(csvFile: MultipartFile): List<String> {
        return csvFile.inputStream.bufferedReader().readLines()
    }
}
