package com.letscode.produtoseureka.repository;

import com.letscode.produtoseureka.domain.Produto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProdutoRepository extends MongoRepository<Produto, Integer> {
}
