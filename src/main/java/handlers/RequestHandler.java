package handlers;

import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import controllers.TableController;

import java.util.ArrayList;
import java.util.List;

public class RequestHandler implements ProxyRequestHandler {

    private final TableController tableController;


    public RequestHandler(TableController tableController) {
        this.tableController = tableController;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {

        List<ParsedHttpParameter> removed = parseParameters(interceptedRequest);

        if (!removed.isEmpty()) {
            HttpRequest modifiedRequest = interceptedRequest.withRemovedParameters(removed);
            return ProxyRequestReceivedAction.continueWith(modifiedRequest);
        } else {
            return ProxyRequestReceivedAction.continueWith(interceptedRequest);
        }

    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    private List<ParsedHttpParameter> parseParameters(InterceptedRequest interceptedRequest) {
        List<ParsedHttpParameter> parameters = interceptedRequest.parameters();
        List<ParsedHttpParameter> removed = new ArrayList<>();

        for (ParsedHttpParameter parameter : parameters) {
            if (parameter.type() == HttpParameterType.COOKIE) {
                // see if the cookie is found in the filter list
                boolean exists = tableController.check(parameter.name());
                if (exists) {
                    removed.add(parameter);
                }
            }
        }

        return removed;
    }
}
