package com.mimi.ServicioService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mimi.ServicioService.model.Categoria;
import com.mimi.ServicioService.model.Servicio;
import com.mimi.ServicioService.repository.CategoriaRepository;
import com.mimi.ServicioService.repository.ServicioRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadToDatabase {

    @Bean
    CommandLineRunner initDatabase(
            CategoriaRepository categoriaRepo,
            ServicioRepository servicioRepo,
            @Value("${app.images.base-url:http://localhost:8083}") 
            String imagesBaseUrl
    ) {
        return args -> {

            if (servicioRepo.count() == 0) {

                // ------------------------------
                // CREAR CATEGORÍAS
                // ------------------------------

                Categoria coloracion = categoriaRepo.save(
                        Categoria.builder()
                                .nombre("Coloración")
                                .build()
                );

                Categoria cortes = categoriaRepo.save(
                        Categoria.builder()
                                .nombre("Cortes")
                                .build()
                );

                Categoria tratamiento = categoriaRepo.save(
                        Categoria.builder()
                                .nombre("Tratamientos")
                                .build()
                );

                Categoria peinados = categoriaRepo.save(
                        Categoria.builder()
                                .nombre("Peinados")
                                .build()
                );

                Categoria manicure = categoriaRepo.save(
                        Categoria.builder()
                                .nombre("Manicure")
                                .build()       
                );

                // ------------------------------
                // CREAR SERVICIOS
                // ------------------------------

                servicioRepo.save(Servicio.builder()
                    .nombre("Brushing Ondas")
                    .descripcion("Brushing profesional con ondas definidas.")
                    .precio(19990)
                    .categoria(peinados)
                    .imagenUrl(imagesBaseUrl + "/servicios/brushing%20ondas.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Brushing Plancha")
                    .descripcion("Brushing con terminación a plancha.")
                    .precio(17990)
                    .categoria(peinados)
                    .imagenUrl(imagesBaseUrl + "/servicios/brushing-plancha.webp")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Color Completo")
                    .descripcion("Coloración completa profesional.")
                    .precio(24990)
                    .categoria(coloracion)
                    .imagenUrl(imagesBaseUrl + "/servicios/color-completo.png")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Corte Flequillo")
                    .descripcion("Corte rápido de flequillo moderno.")
                    .precio(4990)
                    .categoria(cortes)
                    .imagenUrl(imagesBaseUrl + "/servicios/corte-flequillo.png")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Corte Hombre")
                    .descripcion("Corte masculino + perfilado.")
                    .precio(9990)
                    .categoria(cortes)
                    .imagenUrl(imagesBaseUrl + "/servicios/corte-hombre.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Corte Mujer")
                    .descripcion("Corte profesional con asesoría de estilo.")
                    .precio(14990)
                    .categoria(cortes)
                    .imagenUrl(imagesBaseUrl + "/servicios/corte-mujer.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Corte Niño")
                    .descripcion("Corte infantil con estilo y rapidez.")
                    .precio(6990)
                    .categoria(cortes)
                    .imagenUrl(imagesBaseUrl + "/servicios/corte-nino.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Crecimiento Tinte")
                    .descripcion("Retoque de raíces con tinte profesional.")
                    .precio(17990)
                    .categoria(coloracion)
                    .imagenUrl(imagesBaseUrl + "/servicios/crecimiento-tintura.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Laminado de Cejas")
                    .descripcion("Laminado profesional para cejas definidas.")
                    .precio(12990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/laminado-cejas.jpeg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Lavado de Cabello")
                    .descripcion("Lavado + masaje capilar hidratante.")
                    .precio(5990)
                    .categoria(tratamiento)
                    .imagenUrl(imagesBaseUrl + "/servicios/lavado-cabello.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Lifting de Pestañas")
                    .descripcion("Lifting profesional para pestañas curvas y elevadas.")
                    .precio(14990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/lifting-pestanas.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Manicure Express")
                    .descripcion("Manicure rápida + limado + esmalte.")
                    .precio(7990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/manicure-express.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Manicure Hombre")
                    .descripcion("Manicure para hombre con limpieza profunda.")
                    .precio(8990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/manicure-hombre.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Manicure Niños")
                    .descripcion("Manicure suave para niños.")
                    .precio(4990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/manicure-ninos.webp")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Manicure Pedicure")
                    .descripcion("Manicure + pedicure completo.")
                    .precio(14990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/manicure-pedicure.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Manicure Tradicional")
                    .descripcion("Limpieza + corte + esmaltado tradicional.")
                    .precio(9990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/manicure-tradicional.webp")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Ondulado Plancha")
                    .descripcion("Ondas definidas hechas con plancha.")
                    .precio(12990)
                    .categoria(peinados)
                    .imagenUrl(imagesBaseUrl + "/servicios/ondulado-plancha.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Perfilado de Cejas")
                    .descripcion("Perfilado profesional con pinza o cera.")
                    .precio(5990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/perfilado-cejas.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Reflejos")
                    .descripcion("Reflejos suaves para iluminar tu cabello.")
                    .precio(19990)
                    .categoria(coloracion)
                    .imagenUrl(imagesBaseUrl + "/servicios/reflejos.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Restauración de Rizos")
                    .descripcion("Restauración profunda de rizos naturales.")
                    .precio(19990)
                    .categoria(tratamiento)
                    .imagenUrl(imagesBaseUrl + "/servicios/restauracion-rizos.webp")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Tinte de Cejas")
                    .descripcion("Tinte especial para cejas naturales y definidas.")
                    .precio(6990)
                    .categoria(manicure)
                    .imagenUrl(imagesBaseUrl + "/servicios/tinte-cejas.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Tratamientos Capilares")
                    .descripcion("Tratamientos nutritivos para todo tipo de cabello.")
                    .precio(17990)
                    .categoria(tratamiento)
                    .imagenUrl(imagesBaseUrl + "/servicios/tratamientos-capilares.jpg")
                    .build()
            );

            servicioRepo.save(Servicio.builder()
                    .nombre("Visos con Papel")
                    .descripcion("Mechas con técnica tradicional usando papel aluminio.")
                    .precio(21990)
                    .categoria(coloracion)
                    .imagenUrl(imagesBaseUrl + "/servicios/visos-con-papel.webp")
                    .build()
            );

                System.out.println("Datos iniciales cargados correctamente (categorías y servicios)");

            } else {
                System.out.println("Datos existentes — no se insertaron datos nuevos.");
            }
        };
    }
}
