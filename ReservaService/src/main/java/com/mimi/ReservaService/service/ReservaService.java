package com.mimi.ReservaService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mimi.ReservaService.dto.CrearReservaRequest;
import com.mimi.ReservaService.dto.ReservaDetalleResponse;
import com.mimi.ReservaService.dto.ReservaResponse;
import com.mimi.ReservaService.dto.ServicioDTO;
import com.mimi.ReservaService.dto.UsuarioDTO;
import com.mimi.ReservaService.model.Reserva;
import com.mimi.ReservaService.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepo;
    private final RestTemplate rest;

    @Value("${userservice.url:http://localhost:8084}")
    private String userServiceUrl;

    @Value("${servicioservice.url:http://localhost:8083}")
    private String servicioServiceUrl;

    private String getUserUrl() {
        return userServiceUrl + "/api/users/";
    }

    private String getServicioUrl() {
        return servicioServiceUrl + "/api/servicios/";
    }

    public ReservaResponse crearReserva(CrearReservaRequest req) {
        // *validaciones (las de siempre)*
        UsuarioDTO usuario = rest.getForObject(getUserUrl() + req.getIdUsuario(), UsuarioDTO.class);
        if (usuario == null)
            throw new RuntimeException("Usuario no encontrado");

        UsuarioDTO trabajador = rest.getForObject(getUserUrl() + req.getIdTrabajador(), UsuarioDTO.class);
        if (trabajador == null)
            throw new RuntimeException("Trabajador no encontrado");
        if (trabajador.getRolId() != 3)
            throw new RuntimeException("El usuario no es trabajador válido");

        ServicioDTO servicio = rest.getForObject(getServicioUrl() + req.getIdServicio(), ServicioDTO.class);
        if (servicio == null)
            throw new RuntimeException("Servicio no encontrado");

        Optional<Reserva> existente = reservaRepo.findByFechaAndHoraAndIdTrabajador(
                req.getFecha(), req.getHora(), req.getIdTrabajador());
        if (existente.isPresent())
            throw new RuntimeException("Ese trabajador ya tiene reservado ese horario");

        Reserva nueva = Reserva.builder()
                .idUsuario(req.getIdUsuario())
                .idServicio(req.getIdServicio())
                .idTrabajador(req.getIdTrabajador())
                .fecha(req.getFecha())
                .hora(req.getHora())
                .estado("Pendiente")
                .build();

        reservaRepo.save(nueva);

        return toDTO(nueva);
    }

    public ReservaResponse obtenerPorId(Long id) {
        Reserva r = reservaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        return toDTO(r);
    }

    public List<ReservaResponse> listarTodas() {
        return reservaRepo.findAll()
                .stream().map(this::toDTO).toList();
    }

    public List<ReservaResponse> obtenerReservasUsuario(Long idUsuario) {
        return reservaRepo.findByIdUsuario(idUsuario)
                .stream().map(this::toDTO).toList();
    }

    public List<ReservaResponse> obtenerReservasTrabajador(Long idTrabajador) {
        return reservaRepo.findByIdTrabajador(idTrabajador)
                .stream().map(this::toDTO).toList();
    }

    public ReservaResponse actualizarEstado(Long idReserva, String nuevoEstado) {
        Reserva r = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        r.setEstado(nuevoEstado);
        reservaRepo.save(r);

        return toDTO(r);
    }

    private ReservaResponse toDTO(Reserva r) {
        return new ReservaResponse(
                r.getIdReserva(),
                r.getIdUsuario(),
                r.getIdServicio(),
                r.getIdTrabajador(),
                r.getFecha(),
                r.getHora(),
                r.getEstado());
    }

    public ReservaDetalleResponse mapToDetalle(Reserva r) {
        // obtener datos externos
        UsuarioDTO usuario = rest.getForObject(getUserUrl() + r.getIdUsuario(), UsuarioDTO.class);
        UsuarioDTO trabajador = rest.getForObject(getUserUrl() + r.getIdTrabajador(), UsuarioDTO.class);
        ServicioDTO servicio = rest.getForObject(getServicioUrl() + r.getIdServicio(), ServicioDTO.class);

        return new ReservaDetalleResponse(
                r.getIdReserva(),
                r.getFecha(),
                r.getHora(),
                r.getEstado(),
                usuario.getNombre(),
                trabajador.getNombre(),
                servicio.getNombre());
    }

    public List<ReservaDetalleResponse> obtenerTodasLasReservasConDetalles() {

        return reservaRepo.findAll()
                .stream()
                .map(this::mapToDetalle) // <- método que arma el DTO completo
                .toList();
    }

    public List<ReservaDetalleResponse> obtenerReservasDetalleTrabajador(Long idTrabajador) {
        return reservaRepo.findByIdTrabajador(idTrabajador)
                .stream().map(this::mapToDetalle).toList();
    }

    public List<ReservaDetalleResponse> obtenerReservasDetalleUsuario(Long idUsuario) {
        return reservaRepo.findByIdUsuario(idUsuario)
                .stream().map(this::mapToDetalle).toList();
    }

    public ReservaDetalleResponse obtenerReservaDetalleById(Long idReserva) {
        Optional<Reserva> reserva = reservaRepo.findById(idReserva);

        return mapToDetalle(reserva.get());
    }
}
