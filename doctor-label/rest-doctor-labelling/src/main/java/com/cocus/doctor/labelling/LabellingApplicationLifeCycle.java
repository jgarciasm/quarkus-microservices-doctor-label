package com.cocus.doctor.labelling;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class LabellingApplicationLifeCycle {

    private static final Logger LOGGER = Logger.getLogger(LabellingApplicationLifeCycle.class);

    void onStart(@Observes StartupEvent ev) {
    	
    	LOGGER.info("   _               ____  ______ _      _      _____ _   _  _____ ");
    	LOGGER.info("  | |        /\\   |  _ \\|  ____| |    | |    |_   _| \\ | |/ ____|");
    	LOGGER.info("  | |       /  \\  | |_) | |__  | |    | |      | | |  \\| | |  __ ");
    	LOGGER.info("  | |      / /\\ \\ |  _ <|  __| | |    | |      | | | . ` | | |_ |");
    	LOGGER.info("  | |____ / ____ \\| |_) | |____| |____| |____ _| |_| |\\  | |__| |");
    	LOGGER.info("  |______/_/    \\_\\____/|______|______|______|_____|_| \\_|\\_____|");
        LOGGER.info("                                               Powered by Quarkus");
        LOGGER.info("                                               Designed by Jonad");
        LOGGER.infof("The application LABELLING is starting with profile `%s`", ProfileManager.getActiveProfile());
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application LABELLING is stopping...");
    }

}
