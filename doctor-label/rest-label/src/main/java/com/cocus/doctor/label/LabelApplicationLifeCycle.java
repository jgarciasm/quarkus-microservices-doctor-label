package com.cocus.doctor.label;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
class LabelApplicationLifeCycle {

    private static final Logger LOGGER = Logger.getLogger(LabelApplicationLifeCycle.class);

    void onStart(@Observes StartupEvent ev) {

    	LOGGER.info("   _               ____  ______ _      ");
    	LOGGER.info("  | |        /\\   |  _ \\|  ____| |    ");
    	LOGGER.info("  | |       /  \\  | |_) | |__  | |    ");
    	LOGGER.info("  | |      / /\\ \\ |  _ <|  __| | |    ");
    	LOGGER.info("  | |____ / ____ \\| |_) | |____| |____");
    	LOGGER.info("  |______/_/    \\_\\____/|______|______|");
        LOGGER.info("                     Powered by Quarkus");
        LOGGER.info("                     Designed by Jonad");
        LOGGER.infof("The application LABEL is starting with profile `%s`", ProfileManager.getActiveProfile());
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application LABEL is stopping...");
    }
}
