package com.erikandreas.customer;

import java.util.Map;

public record ErrorResponse(
    Map<String, String> errors
) {

}
