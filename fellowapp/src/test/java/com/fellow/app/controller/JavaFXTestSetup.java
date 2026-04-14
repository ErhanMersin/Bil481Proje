package com.fellow.app.controller;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JavaFXTestSetup implements BeforeAllCallback {
    private static boolean initialized = false;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!initialized) {
            try {
                Platform.startup(() -> {
                });
                initialized = true;
            } catch (IllegalStateException e) {
                // Already initialized, which is fine
            }
        }
    }
}
