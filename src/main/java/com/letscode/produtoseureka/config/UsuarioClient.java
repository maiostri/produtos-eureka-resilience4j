package com.letscode.produtoseureka.config;

import com.letscode.produtoseureka.domain.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("usuarios-service")
public interface UsuarioClient {

    @RequestMapping(method = RequestMethod.GET, value = "/usuarios/{id}")
    Usuario getUsuario(@PathVariable Integer id);
}
