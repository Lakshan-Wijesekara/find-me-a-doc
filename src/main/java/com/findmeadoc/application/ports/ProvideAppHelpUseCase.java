package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.AppHelpRequest;
import com.findmeadoc.application.dto.AppHelpResponse;

public interface ProvideAppHelpUseCase {
    AppHelpResponse getHelpResponse(AppHelpRequest request);
}
