package com.letscode.produtoseureka.service;

import com.letscode.produtoseureka.config.UsuarioClient;
import com.letscode.produtoseureka.domain.Produto;
import com.letscode.produtoseureka.repository.ProdutoRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    public boolean salvarProduto(Produto p) {
        Integer userId = p.getUsuario();

        try {
            usuarioClient.getUsuario(userId);
        } catch (FeignException.FeignClientException e) {
            if (e.status() == 404) {
                return false;
            }
        }

        produtoRepository.save(p);

        return true;
    }
}
