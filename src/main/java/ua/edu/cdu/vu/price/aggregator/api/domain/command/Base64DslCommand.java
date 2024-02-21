package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import org.springframework.core.io.FileSystemResource;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.FileUtils;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class Base64DslCommand extends DslCommand<Object, Object> {

    @Override
    public Object executeInternal(String url, Object input, Map<String, Object> context) {
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

        throw new DslExecutionException("BASE64 command executed on not supported input");
    }
}
