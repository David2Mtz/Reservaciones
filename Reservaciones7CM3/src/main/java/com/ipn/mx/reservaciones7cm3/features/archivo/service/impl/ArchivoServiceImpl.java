package com.ipn.mx.reservaciones7cm3.features.archivo.service.impl;

import com.ipn.mx.reservaciones7cm3.core.domain.Archivo;
import com.ipn.mx.reservaciones7cm3.features.archivo.repository.ArchivoRepository;
import com.ipn.mx.reservaciones7cm3.features.archivo.service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Service
public class ArchivoServiceImpl implements ArchivoService {
    @Autowired
    private ArchivoRepository archivoRepository;

    @Override
    public Archivo guardarArchivoEnBD(MultipartFile datosArchivo) throws IOException {
        String nombreArchivo =
                StringUtils.cleanPath(datosArchivo.getOriginalFilename());
        Archivo archivo = Archivo.builder().
                nombreArchivo(nombreArchivo).
                tipoArchivo(datosArchivo.getContentType()).
                datosArchivo(datosArchivo.getBytes()).build();
        return archivoRepository.save(archivo);
    }

    @Override
    public Optional<Archivo> descargarArchivo(Long id) throws FileNotFoundException {
        Optional<Archivo> archivo = archivoRepository.findById(id);
        if (archivo.isPresent()) {
            return archivo;
        }
        throw new FileNotFoundException("Archivo no encontrado");
    }
}
