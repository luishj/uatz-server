package br.com.uatz.server.repository;

import java.util.Date;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

/**
 * Contrato comum dos repositórios. Todo repositório do projeto estende esta
 * interface para herdar os helpers de persistência.
 */
public interface GenericRepository<T, ID> extends PanacheRepositoryBase<T, ID> {

	public abstract Date obterDataHoraAtual();

	public abstract T salvar(T entity);

	public abstract List<T> salvar(List<T> entities);

}
