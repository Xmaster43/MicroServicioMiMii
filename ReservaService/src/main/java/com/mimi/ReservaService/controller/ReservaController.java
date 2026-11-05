package com.mimi.ReservaService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mimi.ReservaService.dto.ApiResponsess;
import com.mimi.ReservaService.dto.CrearReservaRequest;
import com.mimi.ReservaService.dto.ReservaDetalleResponse;
import com.mimi.ReservaService.dto.ReservaResponse;
import com.mimi.ReservaService.service.ReservaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas para la app Mimi")
public class ReservaController {

    private final ReservaService reservaService;

    // ==========================================================
    // 1. CREAR RESERVA
    // ==========================================================
    @Operation(summary = "Crear una reserva", description = "Crea una nueva reserva asignando usuario, trabajador y servicio.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "code": 200,
                      "data": {
                        "idReserva": 15,
                        "idUsuario": 3,
                        "idServicio": 7,
                        "idTrabajador": 4,
                        "fecha": "2025-02-10",
                        "hora": "15:00",
                        "estado": "PENDIENTE"
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Error en los datos enviados", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "\"Datos inválidos para crear la reserva\"")))
    })
    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody CrearReservaRequest req) {
        try {
            ReservaResponse reserva = reservaService.crearReserva(req);
            ApiResponsess<ReservaResponse> api = new ApiResponsess<>(true, 201, reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(api);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================================
    // 2. OBTENER TODAS LAS RESERVAS CON DETALLES
    // ==========================================================
    @Operation(summary = "Listar todas las reservas con detalles", description = "Devuelve todas las reservas junto con información del usuario, trabajador y servicio.")
    @ApiResponse(responseCode = "200", description = "Lista de reservas con detalles", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservaDetalleResponse.class)), examples = @ExampleObject(value = """
            [
              {
                "idReserva": 10,
                "fecha": "2025-02-07",
                "hora": "11:00",
                "estado": "CONFIRMADA",
                "usuario": "Camila Rojas",
                "trabajador": "Daniel Pérez",
                "servicio": "Peinado Profesional"
              },
              {
                "idReserva": 11,
                "fecha": "2025-02-08",
                "hora": "14:30",
                "estado": "PENDIENTE",
                "usuario": "Bastián Gómez",
                "trabajador": "Laura Carrasco",
                "servicio": "Corte de Cabello"
              }
            ]
            """)))
    @GetMapping("/detalles")
    public ResponseEntity<?> obtenerTodas() {
        try {
            List<ReservaDetalleResponse> detalles = reservaService.obtenerTodasLasReservasConDetalles();
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener reservas");
        }
    }

    // ==========================================================
    // 3. OBTENER RESERVA POR ID
    // ==========================================================
    @Operation(summary = "Obtener una reserva por ID", description = "Devuelve toda la información principal de una reserva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponse.class), examples = @ExampleObject(value = """
                    {
                      "idReserva": 20,
                      "idUsuario": 3,
                      "idServicio": 5,
                      "idTrabajador": 2,
                      "fecha": "2025-02-09",
                      "hora": "16:00",
                      "estado": "CONFIRMADA"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "\"No existe la reserva solicitada\"")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReserva(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ==========================================================
    // 4. LISTAR TODAS LAS RESERVAS (ADMIN)
    // ==========================================================
    @Operation(summary = "Listar todas las reservas (modo admin)", description = "Devuelve todas las reservas registradas en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reservas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponse.class)), examples = @ExampleObject(value = """
                    [
                      {
                        "idReserva": 12,
                        "idUsuario": 3,
                        "idServicio": 5,
                        "idTrabajador": 2,
                        "fecha": "2025-02-06",
                        "hora": "10:00",
                        "estado": "PENDIENTE"
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "204", description = "No existen reservas")
    })
    @GetMapping("/all")
    public ResponseEntity<?> listarTodas() {
        List<ReservaResponse> lista = reservaService.listarTodas();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // ==========================================================
    // 5. RESERVAS POR USUARIO
    // ==========================================================
    @Operation(summary = "Obtener reservas por usuario", description = "Devuelve todas las reservas asociadas a un usuario específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponse.class)))),
            @ApiResponse(responseCode = "204", description = "El usuario no tiene reservas")
    })
    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long id) {
        try {
            List<ReservaResponse> r = reservaService.obtenerReservasUsuario(id);
            if (r.isEmpty())
                return ResponseEntity.noContent().build();
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener reservas del usuario");
        }
    }

    // ==========================================================
    // 6. DETALLE POR USUARIO
    // ==========================================================
    @Operation(summary = "Obtener reservas detalladas por usuario", description = "Incluye nombre del trabajador, servicio y estado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaDetalleResponse.class)))),
            @ApiResponse(responseCode = "204", description = "No hay reservas para este usuario")
    })
    @GetMapping("/usuario/detalle/{id}")
    public ResponseEntity<?> obtenerDetallePorUsuario(@PathVariable Long id) {
        try {
            List<ReservaDetalleResponse> r = reservaService.obtenerReservasDetalleUsuario(id);
            if (r.isEmpty())
                return ResponseEntity.noContent().build();
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener reservas del usuario");
        }
    }

    // ==========================================================
    // 7. RESERVAS POR TRABAJADOR
    // ==========================================================
    @Operation(summary = "Obtener reservas por trabajador", description = "Devuelve reservas asignadas a un trabajador específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas encontradas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponse.class)))),
            @ApiResponse(responseCode = "204", description = "El trabajador no tiene reservas")
    })
    @GetMapping("/trabajador/{id}")
    public ResponseEntity<?> obtenerPorTrabajador(@PathVariable Long id) {
        try {
            List<ReservaResponse> r = reservaService.obtenerReservasTrabajador(id);
            if (r.isEmpty())
                return ResponseEntity.noContent().build();
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener reservas del trabajador");
        }
    }

    // ==========================================================
    // 8. ACTUALIZAR ESTADO RESERVA
    // ==========================================================
    @Operation(summary = "Actualizar estado de la reserva", description = "Permite cambiar el estado a CONFIRMADA, COMPLETADA o CANCELADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponse.class), examples = @ExampleObject(value = """
                    {
                      "idReserva": 20,
                      "idUsuario": 3,
                      "idServicio": 5,
                      "idTrabajador": 2,
                      "fecha": "2025-02-09",
                      "hora": "16:00",
                      "estado": "CONFIRMADA"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "\"Estado no permitido\"")))
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestBody String nuevoEstado) {
        try {
            return ResponseEntity.ok(reservaService.actualizarEstado(id, nuevoEstado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================================
    // 9. DETALLE POR TRABAJADOR
    // ==========================================================
    @Operation(summary = "Obtener reservas detalladas por trabajador", description = "Incluye información completa de cada reserva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas encontradas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaDetalleResponse.class)))),
            @ApiResponse(responseCode = "204", description = "No hay reservas para este trabajador")
    })
    @GetMapping("/trabajador/detalle/{id}")
    public ResponseEntity<?> obtenerDetallePorTrabajador(@PathVariable Long id) {
        try {
            List<ReservaDetalleResponse> r = reservaService.obtenerReservasDetalleTrabajador(id);
            if (r.isEmpty())
                return ResponseEntity.noContent().build();
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener reservas del trabajador");
        }
    }

    // ==========================================================
    // 10. CANCELAR UNA RESERVA (DELETE)
    // ==========================================================
    @Operation(summary = "Cancelar una reserva", description = "Permite al usuario cancelar su reserva. No elimina la reserva, solo cambia su estado a CANCELADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponse.class), examples = @ExampleObject(value = """
                    {
                      "idReserva": 20,
                      "idUsuario": 3,
                      "idServicio": 5,
                      "idTrabajador": 2,
                      "fecha": "2025-02-09",
                      "hora": "16:00",
                      "estado": "CANCELADA"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "\"Reserva no encontrada\"")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            ReservaResponse cancelada = reservaService.actualizarEstado(id, "CANCELADA");
            return ResponseEntity.ok(cancelada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<ReservaDetalleResponse> obtenerDetallePorId(@PathVariable Long id) {
        ReservaDetalleResponse reserva = reservaService.obtenerReservaDetalleById(id);
        return ResponseEntity.ok(reserva);
    }

}
