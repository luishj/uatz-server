package br.com.uatz.server.startup.impl;

import org.jboss.logging.Logger;

import br.com.uatz.server.env.Enviroment;
import br.com.uatz.server.util.PropertyUtil;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * Ponto de entrada da aplicação.
 */
@QuarkusMain
public class StartupImpl {

	public static void main(String... args) {
		Quarkus.run(MyApp.class, args);
	}

	public static class MyApp implements QuarkusApplication {

		private static final Logger logger = Logger.getLogger(MyApp.class);

		@Inject
		Enviroment enviroment;

		// Executa DURANTE a inicialização do container
		void onStart(@Observes StartupEvent ev) {
			this.logarParametros();
		}

		// Executa DEPOIS de inicializar o container
		@Override
		public int run(String... args) throws Exception {
			Quarkus.waitForExit();
			return 0;
		}

		private void logarParametros() {
			logger.info("=========== APP: " + enviroment.getNomeApp() + " ===========");
			logger.info("=========== VERSAO: " + enviroment.getVersaoApp() + " ===========");
			logger.info("=========== PROFILE: " + PropertyUtil.getProfile().getIdentificador() + " ===========");
			logger.info("=========== DATABASE: " + enviroment.getUrlDatabase() + " ===========");
		}

	}

}
