package org.zaproxy.addon.aiassistant;

import java.util.ArrayList;
import java.util.List;

import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.core.scanner.Alert;
import org.zaproxy.zap.extension.alert.ExtensionAlert;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AlertsAPIClient {
  private static final Logger LOGGER = LogManager.getLogger(ExtensionSimpleExample.class);

  private ArrayList<Alert> existingAlerts = new ArrayList<Alert>();

  public List<Alert> getAllAlerts() {
    ExtensionAlert extAlert = Control.getSingleton().getExtensionLoader().getExtension(ExtensionAlert.class);

    List<Alert> alerts = extAlert.getAllAlerts();

    LOGGER.debug("Alerts total: " + alerts.size());

    return alerts;
  }

  public List<Alert> getNewAlerts() {
    List<Alert> allAlerts = getAllAlerts();

    // diff of allAlerts and existingAlerts
    List<Alert> newAlerts = new ArrayList<Alert>(allAlerts);
    newAlerts.removeAll(existingAlerts);

    existingAlerts = new ArrayList<Alert>(allAlerts);

    LOGGER.debug("New alerts: " + newAlerts.size());

    return newAlerts;
  }
}
