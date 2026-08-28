package org.openremote.agent.protocol.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.openremote.model.asset.agent.AgentLink;

public class ModbusAgentLink extends AgentLink<ModbusAgentLink> {

    @JsonPropertyDescription("Identifiant du serveur Modbus TCP ou de l'esclave Modbus série")
    private int unitId;

    @JsonPropertyDescription("Intervalle de polling en millisecondes")
    private long refresh;

    @JsonPropertyDescription("Type de lecture: \"coil\" (FC01), \"discrete\" (FC02), \"holding\" (FC03), \"input\" (FC04)")
    private ReadType readType;

    @JsonPropertyDescription("Type de valeur de lecture: \"int64\", \"int64_swap\", \"uint64\", \"uint64_swap\", \"float32\", \"float32_swap\", \"int32\", \"int32_swap\", \"uint32\", \"uint32_swap\", \"int16\", \"uint16\", \"int8\", \"uint8\", \"bit\"")
    private ReadValueType readValueType;

    @JsonPropertyDescription("Adresse de base zéro pour la lecture des données")
    private int readAddress;

    @JsonPropertyDescription("Type d'écriture: \"coil\", \"holding\"")
    private WriteType writeType;

    @JsonPropertyDescription("Adresse de base zéro pour l'écriture des données")
    private int writeAddress;

    @JsonPropertyDescription("Type de valeur d'écriture: \"int64\", \"int64_swap\", \"float32\", \"float32_swap\", \"int32\", \"int32_swap\", \"int16\", \"bit\"")
    private WriteValueType writeValueType;

    // Getters et setters pour chaque variable
    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int id) {
        this.unitId = id;
    }

    public long getRefresh() {
        return refresh;
    }

    public void setRefresh(long refresh) {
        this.refresh = refresh;
    }

    public ReadType getReadType() {
        return readType;
    }

    public void setReadType(ReadType readType) {
        this.readType = readType;
    }

    public ReadValueType getReadValueType() {
        return readValueType;
    }

    public void setReadValueType(ReadValueType readValueType) {
        this.readValueType = readValueType;
    }

    public int getReadAddress() {
        return readAddress;
    }

    public void setReadAddress(int readAddress) {
        this.readAddress = readAddress;
    }

    public WriteType getWriteType() {
        return writeType;
    }

    public void setWriteType(WriteType writeType) {
        this.writeType = writeType;
    }

    public int getWriteAddress() {
        return writeAddress;
    }

    public void setWriteAddress(int writeAddress) {
        this.writeAddress = writeAddress;
    }

    public WriteValueType getWriteValueType() {
        return writeValueType;
    }

    public void setWriteValueType(WriteValueType writeValueType) {
        this.writeValueType = writeValueType;
    }
    public enum ReadType {
        COIL, DISCRETE, HOLDING, INPUT
    }
    public enum ReadValueType {
        INT64, INT64_SWAP, UINT64, UINT64_SWAP, FLOAT32, FLOAT32_SWAP,
        INT32, INT32_SWAP, UINT32, UINT32_SWAP,
        INT16, UINT16, INT8, UINT8, BIT
    }

    public enum WriteType {
        COIL, HOLDING
    }

    public enum WriteValueType {
        INT64, INT64_SWAP, FLOAT32, FLOAT32_SWAP,
        INT32, INT32_SWAP, INT16, BIT
    }

    public ModbusAgentLink(String id) {
        super(id);
    }

    protected ModbusAgentLink() {
    }

    @JsonCreator
    public ModbusAgentLink(@JsonProperty("id") String id,
                           @JsonProperty("unitId") int unitId,
                           @JsonProperty("refresh") long refresh,
                           @JsonProperty("readType") ReadType readType,
                           @JsonProperty("readValueType") ReadValueType readValueType,
                           @JsonProperty("readAddress") int readAddress,
                           @JsonProperty("writeType") WriteType writeType,
                           @JsonProperty("writeAddress") int writeAddress,
                           @JsonProperty("writeValueType") WriteValueType writeValueType) {
        super(id);
        this.unitId = unitId;
        this.refresh = refresh;
        this.readType = readType;
        this.readValueType = readValueType;
        this.readAddress = readAddress;
        this.writeType = writeType;
        this.writeAddress = writeAddress;
        this.writeValueType = writeValueType;
    }
}

