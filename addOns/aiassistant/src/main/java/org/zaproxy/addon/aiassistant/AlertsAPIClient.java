package org.zaproxy.addon.aiassistant;

import java.util.List;

import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.core.scanner.Alert;
import org.zaproxy.zap.extension.alert.ExtensionAlert;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AlertsAPIClient {
  private static final Logger LOGGER = LogManager.getLogger(ExtensionSimpleExample.class);

  public List<Alert> getAllAlerts() {
    ExtensionAlert extAlert = Control.getSingleton().getExtensionLoader().getExtension(ExtensionAlert.class);

    List<Alert> alerts = extAlert.getAllAlerts();

    LOGGER.debug("Alerts total: " + alerts.size());

    return alerts;
  }
}
