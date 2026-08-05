package br.com.uatz.server.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.ws.rs.WebApplicationException;

import io.quarkus.runtime.annotations.RegisterForReflection;

public class QueryUtil {

	/**
	 * Este método deverá ser utilizado para consultas nativas, ou seja, quando o
	 * script utiliza diretamente o nome das tabelas.
	 * 
	 * @param em        - Entity Manager
	 * @param sqlNative - String do sql nativo
	 * @return - Query
	 */
	public static Query createNativeQuery(EntityManager em, String sqlNative) {

		return em.createNativeQuery(sqlNative, Tuple.class);
	}

	/**
	 * Este método deverá ser utilizado para consultas HQL, ou seja, quando o script
	 * utiliza o nome da entidade mapeada no model.
	 * 
	 * @return Tuple.class
	 */
	public static Query createQuery(EntityManager em, String sqlNative) {

		return em.createQuery(sqlNative, Tuple.class);
	}

	/**
	 * Este método deve ser utilizado para obter o resultado da consulta convertido
	 * para uma lista tipo passado por parâmetro. Geralmente este parâmetro será um
	 * DTO.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getResultList(Query query, Class<T> type) {

		RegisterForReflection annotation = type.getAnnotation(RegisterForReflection.class);

		if (annotation == null) {
			throw new WebApplicationException("A classe " + type.getName() + " precisa ser anotada com @RegisterForReflection para permitir build nativa.");
		}

		List<Tuple> tuples = query.getResultList();

		return convertTuplesToMap(type, tuples);

	}

	/**
	 * Este método deve ser utilizado para obter o resultado de uma consulta com um
	 * único atributo, este será convertido para uma lista tipo passado por
	 * parâmetro.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getResultField(Query query, Class<T> type) {

		List<Tuple> tuples = query.getResultList();

		List<T> result = new ArrayList<>();

		for (Tuple single : tuples) {

			Object value = single.get(0);
			if (value != null) {

				result.add((T) convert(type, value));
			}

		}

		return result;

	}

	/**
	 * Este método deve ser utilizado para obter o resultado de uma consulta com um
	 * único atributo, este será convertido para o tipo passado por parâmetro.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSingleField(Query query, Class<T> type) {

		try {
			Tuple tuple = null;
			try {
				tuple = (Tuple) query.getSingleResult();
			} catch (NoResultException e) {
				return null;
			}

			Object value = tuple.get(0);

			return (T) convert(type, value);

		} catch (Exception e) {

			e.printStackTrace();
			throw new WebApplicationException("NÃO FOI POSSIVEL PROCESSAR");

		}
	}

	/**
	 * Este método deve ser utilizado para obter o resultado da consulta convertido
	 * para o tipo passado por parâmetro. Geralmente este parâmetro será um DTO.
	 */
	public static <T> T getSingleResult(Query query, Class<T> type) {

		try {
			Tuple tuple = null;
			try {
				tuple = (Tuple) query.getSingleResult();
			} catch (NoResultException e) {
				return null;
			}

			return convertSingleTuple(type, tuple);

		} catch (Exception e) {

			e.printStackTrace();
			throw new WebApplicationException("NÃO FOI POSSIVEL PROCESSAR");

		}
	}

	private static <T> List<T> convertTuplesToMap(Class<T> type, List<Tuple> tuples) {

		List<T> tupleTypes = new ArrayList<>();

		for (Tuple single : tuples) {

			tupleTypes.add(convertSingleTuple(type, single));

		}

		return tupleTypes;
	}

	private static <T> T convertSingleTuple(Class<T> type, Tuple single) {
		try {
			Constructor<T> ctor = type.getConstructor();

			T item = ctor.newInstance();

			Field[] fields = type.getDeclaredFields();

			for (Field field : fields) {

				if (!Modifier.isStatic(field.getModifiers())) {

					Object value = null;

					try {
						value = single.get(field.getName().toLowerCase());
					} catch (Exception e) {
					}

					if (value != null) {

						if (Modifier.isPrivate(field.getModifiers())) {
							field.setAccessible(true);
						}

						field.set(item, convert(field.getType(), value));
					}
				}

			}

			return item;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Object convert(Class<?> tipoDestino, Object value) {

		Class<?> tipoOrigem = value.getClass();

		if (tipoDestino.equals(Long.class) && tipoOrigem.equals(BigDecimal.class)) {
			return ((BigDecimal) value).longValue();
		}

		if (tipoDestino.equals(Integer.class) && tipoOrigem.equals(BigDecimal.class)) {
			return ((BigDecimal) value).intValue();
		}

		if (tipoDestino.equals(Date.class) && tipoOrigem.equals(java.sql.Date.class)) {
			return new Date(((java.sql.Date) value).getTime());
		}

		return value;
	}

	public static Query createNativeQuery(EntityManager em, String sqlNative, Map<String, Object> parametros) {
		Query query = em.createNativeQuery(sqlNative, Tuple.class);
		atriburiParametros(query, parametros);
		return query;
	}

	public static void atriburiParametros(Query query, Map<String, Object> parametros) {
		Iterator<Entry<String, Object>> iter = parametros.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> next = iter.next();
			query.setParameter(next.getKey(), next.getValue());
		}
	}

}