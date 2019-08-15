package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {

    protected final Log logger = LogFactory.getLog(getClass());

    protected final List<HandlerMethodArgumentResolver> argumentResolvers = new LinkedList<>();

    private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache =
            new ConcurrentHashMap<>(256);

    /**
     * Add the given {@link HandlerMethodArgumentResolver}.
     */
    public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
        this.argumentResolvers.add(resolver);
        return this;
    }

    /**
     * Add the given {@link HandlerMethodArgumentResolver}s.
     * @since 4.3
     */
    public HandlerMethodArgumentResolverComposite addResolvers(@Nullable HandlerMethodArgumentResolver... resolvers) {
        if (resolvers != null) {
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                this.argumentResolvers.add(resolver);
            }
        }
        return this;
    }

    /**
     * Add the given {@link HandlerMethodArgumentResolver}s.
     */
    public HandlerMethodArgumentResolverComposite addResolvers(
            @Nullable List<? extends HandlerMethodArgumentResolver> resolvers) {

        if (resolvers != null) {
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                this.argumentResolvers.add(resolver);
            }
        }
        return this;
    }

    /**
     * Return a read-only list with the contained resolvers, or an empty list.
     */
    public List<HandlerMethodArgumentResolver> getResolvers() {
        return Collections.unmodifiableList(this.argumentResolvers);
    }

    public void clear() {
        this.argumentResolvers.clear();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        return (getArgumentResolver(parameter, parseResult) != null);
    }

    @Nullable
    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter, STAFCommandParseResult parseResult) {
        HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            for (HandlerMethodArgumentResolver methodArgumentResolver : this.argumentResolvers) {
                logger.trace("Testing if argument resolver [" + methodArgumentResolver + "] supports [" +
                        parameter.getGenericParameterType() + "]");
                if (methodArgumentResolver.supportsParameter(parameter, parseResult)) {
                    result = methodArgumentResolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception {
        HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter, parseResult);
        if (resolver == null) {
            throw new IllegalArgumentException("Unknown parameter type [" + parameter.getParameterType().getName() + "]");
        }
        return resolver.resolveArgument(parameter, parseResult);
    }
}
