package com.boutique.productcatalog.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.boutique.productcatalog.dto.CreateProductRequest;
import com.boutique.productcatalog.service.ProductService;
import tools.jackson.databind.JsonNode;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

public final class ProductLambdaHandler implements RequestStreamHandler {
    private final ProductService service = LambdaSupport.bean(ProductService.class);

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        try {
            JsonNode event = LambdaSupport.readEvent(input);
            String method = LambdaSupport.method(event);
            String path = LambdaSupport.path(event);

            if ("POST".equals(method) && "/api/v1/products".equals(path)) {
                CreateProductRequest request = LambdaSupport.validate(
                        LambdaSupport.JSON.readValue(LambdaSupport.body(event), CreateProductRequest.class));
                LambdaSupport.respond(output, 201, service.createProduct(request));
                return;
            }

            if ("GET".equals(method) && "/api/v1/products".equals(path)) {
                String query = LambdaSupport.queryParameter(event, "q");
                String category = LambdaSupport.queryParameter(event, "category");
                int page = parseInt(LambdaSupport.queryParameter(event, "page"), 0);
                int size = parseInt(LambdaSupport.queryParameter(event, "size"), ProductService.DEFAULT_PAGE_SIZE);
                LambdaSupport.respond(output, 200, service.searchProducts(query, category, page, size));
                return;
            }

            if ("GET".equals(method) && "/api/v1/products/categories".equals(path)) {
                LambdaSupport.respond(output, 200, service.getCategories());
                return;
            }

            if ("GET".equals(method) && path.startsWith("/api/v1/products/")) {
                String rawId = LambdaSupport.pathParameter(event, "productId");
                if (rawId.isBlank()) rawId = path.substring("/api/v1/products/".length());
                LambdaSupport.respond(output, 200, service.getProductById(UUID.fromString(rawId)));
                return;
            }

            LambdaSupport.respond(output, 404, Map.of("message", "Product route not found"));
        } catch (Throwable failure) {
            try { LambdaSupport.fail(output, failure, context); }
            catch (Exception responseFailure) { throw new RuntimeException(responseFailure); }
        }
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) return fallback;
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
