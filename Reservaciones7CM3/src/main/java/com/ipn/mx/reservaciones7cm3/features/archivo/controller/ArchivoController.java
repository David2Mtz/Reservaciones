package com.ipn.mx.reservaciones7cm3.features.archivo.controller;

import com.ipn.mx.reservaciones7cm3.core.domain.Archivo;
import com.ipn.mx.reservaciones7cm3.features.archivo.dto.RespuestaDTO;
import com.ipn.mx.reservaciones7cm3.features.archivo.service.ArchivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/apiArchivos/v1/archivos")
@Tag(name = "Módulo de Archivos", description = "Endpoints para subir y descargar archivos/documentos en la base de datos")
public class ArchivoController {
    @Autowired
    private ArchivoService archivoService;

    @PostMapping(value = "/subirArchivo", consumes = "multipart/form-data")
    @Operation(summary = "Subir un archivo a la base de datos", description = "Recibe un archivo multimedia o documento y lo persiste en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo guardado con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar el archivo")
    })
    public ResponseEntity<RespuestaDTO> subirArchivo
            (@RequestParam MultipartFile archivo) throws IOException {
        archivoService.guardarArchivoEnBD(archivo);
        RespuestaDTO respuestaDTO = new RespuestaDTO();
        respuestaDTO.setMensaje("Archivo guardado correctamente");
        return ResponseEntity.ok(respuestaDTO);
    }

    @GetMapping("/descargarArchivo/{id}")
    @Operation(summary = "Descargar un archivo por ID", description = "Descarga los datos binarios de un archivo previamente cargado por su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Descarga de archivo exitosa"),
            @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    })
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) throws FileNotFoundException {
        Archivo archivo = archivoService.descargarArchivo(id).get();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, archivo.getTipoArchivo())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getNombreArchivo() + "\"").body(archivo.getDatosArchivo());
    }
}
