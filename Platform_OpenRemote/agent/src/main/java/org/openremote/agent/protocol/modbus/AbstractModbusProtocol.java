package org.openremote.agent.protocol.modbus;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.openremote.agent.protocol.AbstractProtocol;
import org.openremote.model.Container;
import org.openremote.model.asset.agent.ConnectionStatus;
import org.openremote.model.attribute.Attribute;
import org.openremote.model.attribute.AttributeEvent;
import org.openremote.model.attribute.AttributeRef;
import org.openremote.model.syslog.SyslogCategory;
import org.openremote.model.util.ValueUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.openremote.model.syslog.SyslogCategory.PROTOCOL;

public abstract class AbstractModbusProtocol<S extends AbstractModbusProtocol<S,T>, T extends ModbusAgent<T, S>> extends AbstractProtocol<T, ModbusAgentLink>{

    public static final Logger LOG = SyslogCategory.getLogger(PROTOCOL, AbstractModbusProtocol.class);
    public static final int DEFAULT_MODBUS_POLLING_INTERVAL = 1000;
    protected final Map<AttributeRef, ScheduledFuture<?>> attributePollingTasks = new HashMap<>();
    protected PlcConnection plc4xConnection = null;

    public AbstractModbusProtocol(T agent) {
        super(agent);
    }

    @Override
    protected void doStart(Container container) throws Exception {
        try {
            setConnectionStatus(ConnectionStatus.CONNECTING);
            plc4xConnection = createIoClient(agent);
            if (plc4xConnection.isConnected()) {
                setConnectionStatus(ConnectionStatus.CONNECTED);
            } else {
                setConnectionStatus(ConnectionStatus.DISCONNECTED);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Échec de création de la connexion PLC4X pour l'instance de protocole: " + agent, e);
            setConnectionStatus(ConnectionStatus.ERROR);
            throw e;
        }
    }

    @Override
    protected void doStop(Container container) throws Exception {
        if (plc4xConnection != null) {
            plc4xConnection.close();
        }
    }

    @Override
    protected void doLinkAttribute(String assetId, Attribute<?> attribute, ModbusAgentLink agentLink) throws RuntimeException {
        AttributeRef ref = new AttributeRef(assetId, attribute.getName());
        int unitId = agentLink.getUnitId();
        int readAddress = agentLink.getReadAddress();
        ScheduledFuture<?> pollingTask = createPollingTask(ref, attribute, ((int) agentLink.getRefresh()), unitId, agentLink.getReadType(), readAddress);
        attributePollingTasks.put(ref, pollingTask);
    }

    @Override
    protected void doUnlinkAttribute(String assetId, Attribute<?> attribute, ModbusAgentLink agentLink) {
        AttributeRef attributeRef = new AttributeRef(assetId, attribute.getName());
        ScheduledFuture<?> pollTask = attributePollingTasks.remove(attributeRef);
        if (pollTask != null) {
            pollTask.cancel(false);
        }
    }

    @Override
    protected void doLinkedAttributeWrite(ModbusAgentLink agentLink, AttributeEvent event, Object processedValue) {
        try {
            int unitId = agentLink.getUnitId();
            int writeAddress = agentLink.getWriteAddress();
            ModbusAgentLink.WriteType writeType = agentLink.getWriteType();

            if (writeType == null) {
                LOG.warning("Type d'écriture non spécifié pour l'attribut: " + event.getRef());
                return;
            }
            writeValueToModbus(unitId, writeAddress, writeType, processedValue);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Exception lors de l'écriture vers le périphérique Modbus: " + e.getMessage(), e);
        }
    }
    protected void writeValueToModbus(int unitId, int address, ModbusAgentLink.WriteType writeType, Object value) {
        try {
            if (writeType == ModbusAgentLink.WriteType.COIL) {
                boolean boolValue = false;
                if (value instanceof Boolean) {
                    boolValue = (Boolean) value;
                } else if (value instanceof Number) {
                    boolValue = ((Number) value).intValue() != 0;
                }

                LOG.info("Écriture de la valeur " + boolValue + " à la bobine à l'adresse " + address);
                // Utilisation de PLC4X pour écrire à une bobine
                plc4xConnection.writeRequestBuilder()
                        .addTagAddress("coil", "coil:" + address, boolValue)
                        .build()
                        .execute()
                        .get();
            } else if (writeType == ModbusAgentLink.WriteType.HOLDING) {
                int intValue = 0;
                if (value instanceof Number) {
                    intValue = ((Number) value).intValue();
                } else {
                    LOG.warning("Impossible d'écrire une valeur non numérique dans un registre: " + value);
                    return;
                }

                LOG.info("Écriture de la valeur " + intValue + " au registre à l'adresse " + address);
                // Utilisation de PLC4X pour écrire à un registre
                plc4xConnection.writeRequestBuilder()
                        .addTagAddress("register", "holding-register:" + address, intValue)
                        .build()
                        .execute()
                        .get();
            } else {
                LOG.warning("Type d'écriture non supporté: " + writeType);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Erreur lors de l'écriture Modbus: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProtocolName() {
        return "Client Modbus TCP";
    }

    @Override
    public String getProtocolInstanceUri() {
        return "modbus-tcp://" + agent.getHost().orElse("inconnu") + ":" + agent.getPort().orElse(0);
    }
    protected ScheduledFuture<?> createPollingTask(AttributeRef ref,
                                                   Attribute<?> attribute,
                                                   int pollingMillis,
                                                   int unitId,
                                                   ModbusAgentLink.ReadType readType,
                                                   int readAddress) {

        LOG.warning("Planification de la requête de polling à exécuter toutes les " + pollingMillis + " ms pour l'attribut: " + attribute);

        return scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                PlcReadRequest.Builder builder = plc4xConnection.readRequestBuilder();
                String tagName;

                switch (readType) {
                    case COIL:
                        tagName = "coilData";
                        builder.addTagAddress(tagName, "coil:" + readAddress + "[1]");
                        break;
                    case DISCRETE:
                        tagName = "discreteData";
                        builder.addTagAddress(tagName, "discrete-input:" + readAddress + "[1]");
                        break;
                    case HOLDING:
                        tagName = "holdingData";
                        builder.addTagAddress(tagName, "holding-register:" + readAddress + "[1]");
                        break;
                    case INPUT:
                        tagName = "inputData";
                        builder.addTagAddress(tagName, "input-register:" + readAddress + "[1]");
                        break;
                    default:
                        throw new IllegalArgumentException("Type de lecture non supporté: " + readType);
                }

                PlcReadRequest readRequest = builder.build();
                PlcReadResponse response = readRequest.execute().get();
                Object value = null;
                switch (readType) {
                    case COIL:
                        value = response.getBoolean("coilData", 0);
                        break;
                    case DISCRETE:
                        value = response.getBoolean("discreteData", 0);
                        break;
                    case HOLDING:
                        value = response.getInteger("holdingData", 0);
                        break;
                    case INPUT:
                        value = response.getInteger("inputData", 0);
                        break;
                }

                Optional<?> coercedResponse = ValueUtil.getValueCoerced(value, attribute.getTypeClass());
                LOG.fine("Valeur reçue: " + value);
                updateLinkedAttribute(ref, coercedResponse);
            } catch (Exception e) {
                LOG.log(Level.WARNING, prefixLogMessage("Exception lors du traitement de la réponse de polling: " + e.getMessage()), e);
            }
        }, 0, pollingMillis, TimeUnit.MILLISECONDS);
    }

    protected abstract PlcConnection createIoClient(T agent) throws RuntimeException;
}

