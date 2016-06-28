package net.hawkengine.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.dto.WsContractDto;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WsEndpoint extends WebSocketAdapter {
    static final Logger LOGGER = Logger.getLogger(WsEndpoint.class.getClass());
    private Gson jsonConverter;
    private UUID id;

    public WsEndpoint() {
        this.id = UUID.randomUUID();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        System.out.println("Socket Connected: " + session);
        EndpointConnector.setWsEndpoint(this);
    }

    @Override
    public void onWebSocketText(String message) {
        WsContractDto contract = null;
        Gson serializer = new Gson();
        RemoteEndpoint remoteEndpoint = null;

        try {
            remoteEndpoint = this.getSession().getRemote();
            contract = this.resolve(message);
            if (contract == null) {
                contract = new WsContractDto();
                contract.setError(true);
                contract.setErrorMessage("Invalid Json was provided");
                remoteEndpoint.sendString(serializer.toJson(contract));
                return;
            }

//            SchemaValidator schemaValidator = new SchemaValidator();
//            for (ConversionObject conversionObject : contract.getArgs()) {
//                Class objectClass = Class.forName(conversionObject.getPackageName());
//                Object object = this.jsonConverter.fromJson(conversionObject.getObject(), objectClass);
//                String result = schemaValidator.validate(object);
//
//                if (!result.equals("OK")) {
//                    contract.setError(true);
//                    contract.setErrorMessage(result);
//                    remoteEndpoint.sendString(serializer.toJson(contract));
//                    return;
//                }
//            }

            ServiceResult result = (ServiceResult) this.call(contract);
            contract.setResult(result.getObject());
            contract.setError(result.hasError());
            contract.setErrorMessage(result.getMessage());

            String jsonResult = serializer.toJson(contract);
            remoteEndpoint.sendString(jsonResult);
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            LOGGER.error(String.format(LoggerMessages.WSENDPOINT_ERROR, e));
            e.printStackTrace();
            this.errorDetails(contract, serializer, e, remoteEndpoint);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public WsContractDto resolve(String message) {
        WsContractDto contract = null;
        try {
            contract = this.jsonConverter.fromJson(message, WsContractDto.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        return contract;
    }

    public void send(WsContractDto contract) {
        RemoteEndpoint remoteEndpoint = this.getSession().getRemote();

        String jsonResult = this.jsonConverter.toJson(contract);
        try {
            remoteEndpoint.sendString(jsonResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object call(WsContractDto contract) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String fullPackageName = String.format("%s.%s", contract.getPackageName(), contract.getClassName());
        Object service = Class.forName(fullPackageName).newInstance();
        List<Object> methodArgs = new ArrayList<>();
        int contractArgsLength = contract.getArgs().length;
        for (int i = 0; i < contractArgsLength; i++) {
            if (contract.getArgs()[i] != null) {
                Class objectClass = Class.forName(contract.getArgs()[i].getPackageName());
                Object object = this.jsonConverter.fromJson(contract.getArgs()[i].getObject(), objectClass);
                methodArgs.add(object);
            }
        }

        Command command = new Command(service, contract.getMethodName(), methodArgs);
        return command.execute();
    }

    private void errorDetails(WsContractDto contract, Gson serializer, Exception e, RemoteEndpoint endPoint) {
        contract.setError(true);
        contract.setErrorMessage(e.getMessage());
        try {
            String errDetails = serializer.toJson(contract);
            endPoint.sendString(errDetails);
        } catch (IOException | RuntimeException e1) {
            e1.printStackTrace();
        }
    }
}