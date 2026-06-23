package com.ipn.mx.reservaciones7cm3.features.archivo.service;

import com.ipn.mx.reservaciones7cm3.core.domain.Archivo;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public interface ArchivoService {
    //Almacenar Archivos en la BD
    Archivo guardarArchivoEnBD(MultipartFile datosArchivo) throws IOException;

    Optional<Archivo> descargarArchivo(Long id) throws FileNotFoundException;

    //agregar equivalentes para subir a sistema de archivos local y modificar la BD para vincular la ruta de donde se encuentra el recurso
}
