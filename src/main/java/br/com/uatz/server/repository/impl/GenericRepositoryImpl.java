package br.com.uatz.server.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.uatz.server.repository.GenericRepository;
import br.com.uatz.server.util.QueryUtil;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

/**
 * Implementação dos helpers comuns aos repositórios. As implementações concretas
 * estendem esta classe e ganham, junto com o Panache, `salvar` e
 * `obterDataHoraAtual`.
 */
public class GenericRepositoryImpl<T, ID> implements GenericRepository<T, ID> {

	@Override
	public Date obterDataHoraAtual() {
		Query query = QueryUtil.createNativeQuery(this.getEntityManager(), "select current_timestamp ");
		return QueryUtil.getSingleField(query, Date.class);
	}

	@Override
	@Transactional
	public T salvar(T entity) {
		return this.getEntityManager().merge(entity);
	}

	@Override
	public List<T> salvar(List<T> entities) {
		List<T> retorno = new ArrayList<>();
		for (T entity : entities) {
			retorno.add(this.salvar(entity));
		}
		return retorno;
	}

}
