package org.openremote.agent.protocol.modbus;

import org.openremote.model.asset.agent.Agent;
import org.openremote.model.value.AttributeDescriptor;
import org.openremote.model.value.ValueType;

import jakarta.persistence.Entity;

import java.util.Optional;

@Entity
public abstract class ModbusAgent<T extends ModbusAgent<T, U>, U extends AbstractModbusProtocol<U, T>> extends Agent<T, U, ModbusAgentLink> {

    // Attributs de base conservés
    public static final AttributeDescriptor<String> HOST = Agent.HOST.withOptional(false);
    public static final AttributeDescriptor<Integer> PORT = Agent.PORT.withOptional(false);

    // Attributs renommés pour plus de clarté
    public static final AttributeDescriptor<Double> UNIT_ID =
            new AttributeDescriptor<>("modbusUnitId", ValueType.POSITIVE_NUMBER);

    public static final AttributeDescriptor<Double> POLLING_INTERVAL =
            new AttributeDescriptor<>("modbusPollingInterval", ValueType.POSITIVE_NUMBER);

    // Pour les Hydrateurs
    protected ModbusAgent() {}

    protected ModbusAgent(String name) {
        super(name);
    }

    // Méthodes d'accès aux attributs
    public Optional<String> getHost(){
        return getAttributes().getValue(HOST);
    }

    public Optional<Integer> getPort(){
        return getAttributes().getValue(PORT);
    }

    public Optional<Integer> getUnitId() {
        Optional<Double> unitIdOpt = getAttributes().getValue(UNIT_ID);
        return unitIdOpt.map(Double::intValue);
    }

    public Optional<Integer> getPollingInterval() {
        Optional<Double> intervalOpt = getAttributes().getValue(POLLING_INTERVAL);
        return intervalOpt.map(Double::intValue);
    }
}

