package org.openremote.agent.protocol.modbus;
import jakarta.persistence.Entity;
import org.openremote.model.asset.agent.AgentDescriptor;
import org.openremote.model.value.AttributeDescriptor;
import org.openremote.model.value.ValueType;
import java.util.Optional;

@Entity
public class ModbusTcpAgent extends ModbusAgent<ModbusTcpAgent, ModbusTcpProtocol> {
    public static final AttributeDescriptor<Boolean> MODBUS_COIL =
            new AttributeDescriptor<Boolean>("modbusCoil", ValueType.BOOLEAN);

    public static final AttributeDescriptor<Double> MODBUS_HOLDING_REGISTER =
            new AttributeDescriptor<Double>("modbusHoldingRegister", ValueType.POSITIVE_NUMBER);

    public static final AttributeDescriptor<Double> MODBUS_INPUT_REGISTER =
            new AttributeDescriptor<Double>("modbusInputRegister", ValueType.POSITIVE_NUMBER);

    public static final AttributeDescriptor<Boolean> MODBUS_DISCRETE_INPUT =
            new AttributeDescriptor<Boolean>("modbusDiscreteInput", ValueType.BOOLEAN);

    // Ajout d'un nouvel attribut pour le délai de connexion
    public static final AttributeDescriptor<Double> CONNECTION_TIMEOUT =
            new AttributeDescriptor<Double>("connectionTimeout", ValueType.POSITIVE_NUMBER);

    public static final AgentDescriptor<ModbusTcpAgent, ModbusTcpProtocol, ModbusAgentLink> DESCRIPTOR =
            new AgentDescriptor<>( ModbusTcpAgent.class, ModbusTcpProtocol.class, ModbusAgentLink.class
    );


    public ModbusTcpAgent(String name) {
        super(name);
        getAttributes().getOrCreate(HOST).setValue("127.0.0.1");
        getAttributes().getOrCreate(PORT).setValue(502);
        getAttributes().getOrCreate(UNIT_ID).setValue(1.0);
        getAttributes().getOrCreate(POLLING_INTERVAL).setValue(1000.0);
        getAttributes().getOrCreate(CONNECTION_TIMEOUT).setValue(3000.0);
    }






    public Optional<Integer> getConnectionTimeout() {
        Optional<Double> timeoutOpt = getAttributes().getValue(CONNECTION_TIMEOUT);
        return timeoutOpt.map(Double::intValue);
    }





    @Override
    public ModbusTcpProtocol getProtocolInstance() {
        return new ModbusTcpProtocol(this);
    }
}

