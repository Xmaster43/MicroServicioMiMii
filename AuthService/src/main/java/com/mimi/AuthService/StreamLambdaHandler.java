package com.mimi.AuthService;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {
    
    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    // Usamos un bloque static para que AWS Lambda inicialice Spring Boot 
    // solo en el "Cold Start". Las siguientes peticiones reusarán el contenedor.
    static {
        try {
            // REVISA ESTO: Reemplaza 'AuthServiceApplication.class' con el nombre real de tu clase principal
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(AuthServiceApplication.class);
        } catch (ContainerInitializationException e) {
            // Si Spring Boot falla al arrancar, esto quedará registrado en los logs de CloudWatch
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar el contenedor de Spring Boot en Lambda", e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        // Delega el flujo de entrada y salida a la librería serverless para que lo procese Spring
        handler.proxyStream(inputStream, outputStream, context);
    }
}