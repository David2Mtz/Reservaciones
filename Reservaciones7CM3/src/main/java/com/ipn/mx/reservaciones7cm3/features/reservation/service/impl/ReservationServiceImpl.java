package com.ipn.mx.reservaciones7cm3.features.reservation.service.impl;

import com.ipn.mx.reservaciones7cm3.core.domain.Cuarto;
import com.ipn.mx.reservaciones7cm3.core.domain.Reservation;
import com.ipn.mx.reservaciones7cm3.core.domain.Usuario;
import com.ipn.mx.reservaciones7cm3.core.exceptions.BussinesValidationException;
import com.ipn.mx.reservaciones7cm3.core.exceptions.EntityNotFoundException;
import com.ipn.mx.reservaciones7cm3.features.room.repository.CuartoRepository;
import com.ipn.mx.reservaciones7cm3.features.auth.repository.UsuarioRepository;
import com.ipn.mx.reservaciones7cm3.features.reservation.dto.CreateReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.dto.ReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.dto.UpdateReservationDTO;
import com.ipn.mx.reservaciones7cm3.features.reservation.repository.ReservationRepository;
import com.ipn.mx.reservaciones7cm3.features.reservation.service.ReservationService;
import com.ipn.mx.reservaciones7cm3.features.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CuartoRepository cuartoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MailService mailService;

    @Override
    @Transactional
    public ReservationDTO createReservation(CreateReservationDTO dto) {
        Cuarto cuarto = cuartoRepository.findById(dto.cuartoId())
                .orElseThrow(() -> new EntityNotFoundException("El cuarto con ID " + dto.cuartoId() + " no existe"));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("El usuario con ID " + dto.usuarioId() + " no existe"));

        if (dto.fechaFin().isBefore(dto.fechaInicio()) || dto.fechaFin().isEqual(dto.fechaInicio())) {
            throw new BussinesValidationException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Verificar solapamiento de reservaciones
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                dto.cuartoId(), dto.fechaInicio(), dto.fechaFin());
        if (!overlapping.isEmpty()) {
            throw new BussinesValidationException("El cuarto no está disponible para el rango de fechas seleccionado");
        }

        // Calcular precio total
        long noches = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());
        if (noches <= 0) {
            noches = 1;
        }
        BigDecimal precioTotal = cuarto.getPrecio().multiply(BigDecimal.valueOf(noches));

        Reservation reservation = Reservation.builder()
                .cuarto(cuarto)
                .usuario(usuario)
                .fechaInicio(dto.fechaInicio())
                .fechaFin(dto.fechaFin())
                .estado("CONFIRMADA")
                .precioTotal(precioTotal)
                .detalles(dto.detalles())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // Enviar correo de confirmación
        String emailBody = String.format(
                "Hola %s,\n\nTu reservación ha sido confirmada con éxito.\n\n" +
                "Detalles de la reservación:\n" +
                "- Número de Reservación: %d\n" +
                "- Tipo de Habitación: %s (Nº %d)\n" +
                "- Fecha de Entrada: %s\n" +
                "- Fecha de Salida: %s\n" +
                "- Precio Total: $%s MXN\n\n" +
                "¡Gracias por elegirnos!",
                usuario.getNombre(),
                savedReservation.getId(),
                cuarto.getTipo(),
                cuarto.getNumero(),
                savedReservation.getFechaInicio(),
                savedReservation.getFechaFin(),
                savedReservation.getPrecioTotal()
        );
        mailService.sendEmail(usuario.getEmail(), "Confirmación de Reservación #" + savedReservation.getId(), emailBody);

        return mapearDTO(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> readAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapearDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDTO readById(Long id) {
        return reservationRepository.findById(id)
                .map(this::mapearDTO)
                .orElseThrow(() -> new EntityNotFoundException("La reservación con ID " + id + " no existe"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> readByUsuarioId(Long usuarioId) {
        return reservationRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearDTO)
                .toList();
    }

    @Override
    @Transactional
    public ReservationDTO updateReservation(Long id, UpdateReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La reservación con ID " + id + " no existe"));

        if (dto.detalles() != null) {
            reservation.setDetalles(dto.detalles());
        }

        if (dto.estado() != null) {
            reservation.setEstado(dto.estado());
        }

        if (dto.fechaInicio() != null && dto.fechaFin() != null) {
            if (dto.fechaFin().isBefore(dto.fechaInicio()) || dto.fechaFin().isEqual(dto.fechaInicio())) {
                throw new BussinesValidationException("La fecha de fin debe ser posterior a la fecha de inicio");
            }
            // Verificar solapamiento de reservaciones omitiendo la reservación actual
            List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                    reservation.getCuarto().getId(), dto.fechaInicio(), dto.fechaFin());
            boolean overlapsOthers = overlapping.stream()
                    .anyMatch(r -> !r.getId().equals(id));
            if (overlapsOthers) {
                throw new BussinesValidationException("El cuarto no está disponible para las nuevas fechas seleccionadas");
            }

            reservation.setFechaInicio(dto.fechaInicio());
            reservation.setFechaFin(dto.fechaFin());

            // Recalcular precio total
            long noches = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());
            if (noches <= 0) {
                noches = 1;
            }
            BigDecimal precioTotal = reservation.getCuarto().getPrecio().multiply(BigDecimal.valueOf(noches));
            reservation.setPrecioTotal(precioTotal);
        }

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapearDTO(updatedReservation);
    }

    @Override
    @Transactional
    public ReservationDTO cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La reservación con ID " + id + " no existe"));

        reservation.setEstado("CANCELADA");
        Reservation savedReservation = reservationRepository.save(reservation);

        // Enviar correo de cancelación
        String emailBody = String.format(
                "Hola %s,\n\nTe informamos que tu reservación #%d ha sido cancelada.\n\n" +
                "Detalles de la reservación cancelada:\n" +
                "- Habitación: %s (Nº %d)\n" +
                "- Fecha de Entrada: %s\n" +
                "- Fecha de Salida: %s\n\n" +
                "Esperamos poder recibirte en otra ocasión.",
                reservation.getUsuario().getNombre(),
                reservation.getId(),
                reservation.getCuarto().getTipo(),
                reservation.getCuarto().getNumero(),
                reservation.getFechaInicio(),
                reservation.getFechaFin()
        );
        mailService.sendEmail(reservation.getUsuario().getEmail(), "Cancelación de Reservación #" + reservation.getId(), emailBody);

        return mapearDTO(savedReservation);
    }

    private ReservationDTO mapearDTO(Reservation entity) {
        return new ReservationDTO(
                entity.getId(),
                entity.getCuarto().getId(),
                entity.getCuarto().getNumero(),
                entity.getCuarto().getTipo(),
                entity.getUsuario().getId(),
                entity.getUsuario().getUsername(),
                entity.getUsuario().getEmail(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getEstado(),
                entity.getPrecioTotal(),
                entity.getDetalles()
        );
    }
}
