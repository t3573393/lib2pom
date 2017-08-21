package org.fartpig.lib2pom.archivahelper;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.http.HTTPTransportFactory;

public abstract class CxfBaseHelper {

	public CxfBaseHelper() {
		// fix http not found in jar
		Bus defaultBus = BusFactory.getDefaultBus();
		ConduitInitiatorManager extension = defaultBus.getExtension(ConduitInitiatorManager.class);
		extension.registerConduitInitiator("http://cxf.apache.org/transports/http", new HTTPTransportFactory());
	}

}
