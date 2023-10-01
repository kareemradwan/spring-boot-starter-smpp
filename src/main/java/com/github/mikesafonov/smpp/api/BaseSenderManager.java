package com.github.mikesafonov.smpp.api;

import com.github.mikesafonov.smpp.config.SmscConnection;
import com.github.mikesafonov.smpp.core.sender.SenderClient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of {@link SenderManager}
 *
 * @author Mike Safonov
 */
public abstract class BaseSenderManager implements SenderManager {
    protected final List<SmscConnection> smscConnections;

    protected BaseSenderManager(@NotNull List<SmscConnection> smscConnections) {
        this.smscConnections = requireNonNull(smscConnections);
    }


    @Override
    public SenderClient getByName(@NotBlank String name) {
        return smscConnections.stream()
                .filter(smscConnection -> smscConnection.getName().equals(name))
                .findFirst()
                .map(SmscConnection::getSenderClient)
                .orElseThrow(NoSenderClientException::new);
    }

    @Override
    public void create(SmscConnection connection) {
        this.smscConnections.add(connection);
    }

    public void remove(@NotBlank String name) {
        SmscConnection connection = smscConnections.stream()
                .filter(smscConnection -> smscConnection.getName().equals(name))
                .findFirst()
                .orElseThrow(NoSenderClientException::new);

        connection.closeConnection();
        smscConnections.remove(connection);
    }

    protected boolean isEmpty() {
        return smscConnections.isEmpty();
    }

    protected int size() {
        return smscConnections.size();
    }

    @Override
    public List<String> keys() {
        return smscConnections.stream()
                .map(SmscConnection::getName)
                .collect(Collectors.toList());
    }
}
