package com.gimnasio.repository;

import com.gimnasio.enums.Rol;
import com.gimnasio.enums.UserStatus;
import com.gimnasio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByDocumento(String documento);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByActivo(boolean activo);
    List<Usuario> findByRolAndActivo(Rol rol, boolean activo);
    List<Usuario> findByStatus(UserStatus status);
}