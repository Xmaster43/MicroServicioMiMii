package com.mimi.ProductService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCreatedEvent {
    private Long reservaId;
    private Long usuarioId;
    private Long servicioId;
    private String fecha;
    private String hora;
}