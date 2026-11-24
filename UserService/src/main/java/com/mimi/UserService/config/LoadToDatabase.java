package com.mimi.UserService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mimi.UserService.model.Rol;
import com.mimi.UserService.model.User;
import com.mimi.UserService.repository.RoleRepository;
import com.mimi.UserService.repository.UserRepository;

import lombok.RequiredArgsConstructor;  

@Configuration
@RequiredArgsConstructor
public class LoadToDatabase {

        private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(RoleRepository rolRepo, UserRepository userRepo) {
        return args -> {

            if (rolRepo.count() == 0) {

                System.out.println("Cargando roles iniciales...");

                
                Rol cliente = rolRepo.save(Rol.builder().nombre("CLIENTE").build());
                Rol admin = rolRepo.save(Rol.builder().nombre("ADMIN").build());
                Rol trabajador = rolRepo.save(Rol.builder().nombre("TRABAJADOR").build());

                System.out.println("Roles creados correctamente:");
                System.out.println(" - " + admin.getNombre());
                System.out.println(" - " + cliente.getNombre());
                System.out.println(" - " + trabajador.getNombre());
            }

            if (userRepo.count() == 0) {

                System.out.println("Cargando usuario inicial...");

                Rol admin = rolRepo.findByNombre("ADMIN")
                        .orElseThrow(() -> new RuntimeException("No existe rol ADMIN"));

                Rol cliente = rolRepo.findByNombre("CLIENTE")
                        .orElseThrow(() -> new RuntimeException("No existe rol ADMIN"));

                Rol trabajador = rolRepo.findByNombre("TRABAJADOR")
                        .orElseThrow(() -> new RuntimeException("No existe rol ADMIN"));

                userRepo.save(User.builder()
                        .nombre("Admin")
                        .apellido("Principal")
                        .correo("admin@mimi.cl")
                        .phone("999999999")
                        .password(passwordEncoder.encode("1234"))
                        .rol(admin)
                        .build());

                userRepo.save(User.builder()
                        .nombre("Cliente")
                        .apellido("Principal")
                        .correo("cliente@mimi.cl")
                        .phone("999999999")
                        .password(passwordEncoder.encode("1234"))
                        .rol(cliente)
                        .build());
                

                userRepo.save(User.builder()
                        .nombre("Trabajador")
                        .apellido("Principal")
                        .correo("trabajador1@mimi.cl")
                        .phone("999999999")
                        .password(passwordEncoder.encode("1234"))
                        .rol(trabajador)
                        .build());

        
                userRepo.save(User.builder()
                        .nombre("Trabajador")
                        .apellido("Secundario")
                        .correo("trabajador2@mimi.cl")
                        .phone("999999999")
                        .password(passwordEncoder.encode("1234"))
                        .rol(trabajador)
                        .build());


                userRepo.save(User.builder()
                        .nombre("Trabajador")
                        .apellido("Tercearios")
                        .correo("trabajador3@mimi.cl")
                        .phone("999999999")
                        .password(passwordEncoder.encode("1234"))
                        .rol(trabajador)
                        .build());
                
                userRepo.save(User.builder()
                        .nombre("Trabajador")
                        .apellido("IV")
                        .correo("trabajador4@mimi.cl")
                        .phone("999999999")
                        .password(passwordEncoder.encode("1234"))
                        .rol(trabajador)
                        .build());
                

                userRepo.save(User.builder()
                        .nombre("Trabajador")
                        .apellido("V")
                        .correo("trabajador5@mimi.cl")
                        .phone("999999999")
                        .password(passwordEncoder.encode("1234"))
                        .rol(trabajador)
                        .build());


                System.out.println("Usuario por defecto creados correctamente.");
            }
        };
    }
}

