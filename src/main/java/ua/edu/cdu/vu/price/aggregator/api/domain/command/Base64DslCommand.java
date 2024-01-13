package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import org.springframework.core.io.FileSystemResource;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.FileUtils;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class Base64DslCommand extends DslCommand<Object, Object> {

    @Override
    public Object execute(String url, Object input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("BASE64 command executed on null input");
        }

        if (input instanceof FileSystemResource resource) {
            return FileUtils.encodeAsBase64(resource.getFile());
        }

        if (input instanceof List<?> list) {
            return list.stream()
                    .map(resource -> (FileSystemResource) resource)
                    .map(FileSystemResource::getFile)
                    .map(FileUtils::encodeAsBase64)
                    .toList();
        }

        return null;
    }
}
