package net.hawkengine.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Represents WebSockets end-point. All calls are channeled through this class
 */
public class WsEndpoint extends WebSocketAdapter {
	private UUID id;
	private Gson jsonConverter;

	public WsEndpoint() {
		this.id = UUID.randomUUID();
		this.jsonConverter = new GsonBuilder()
				.registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
				.create();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		System.out.println("Socket Connected: " + session);
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
				contract.setError("Contract Failed");
				contract.setErrorMessage("Invalid Json was provided");
				remoteEndpoint.sendString(serializer.toJson(contract));
				return;
			}

			Object result = this.call(contract);
			if (result.getClass() != String.class || result.toString().length() != 0) {
				contract.setResult(result);
			} else {
				contract.setError("Contract Failed");
				contract.setErrorMessage(result.toString());
			}

			String jsonResult = serializer.toJson(contract);

			remoteEndpoint.sendString(jsonResult);

		} catch (Exception e) {
			// TODO: log this
			e.printStackTrace();
			errorDetails(contract, serializer, e, remoteEndpoint);
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

	public Object call(WsContractDto contract) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String fullPackageName = String.format("%s.%s", contract.getPackageName(), contract.getClassName());
		Object service = Class.forName(fullPackageName).newInstance();
		List<Object> methodArgs = new ArrayList<>();
		for (int i = 0; i < contract.getArgs().length; i++) {
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
		contract.setError(e.getClass().getName());
		contract.setErrorMessage(e.getMessage());
		try {
			String errDetails = serializer.toJson(contract);
			endPoint.sendString(errDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}