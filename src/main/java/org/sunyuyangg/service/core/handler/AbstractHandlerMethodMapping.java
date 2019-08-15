package org.sunyuyangg.service.core.handler;

import com.ibm.staf.service.STAFServiceInterfaceLevel30;
import org.pmw.tinylog.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.*;
import org.sunyuyangg.service.core.method.OptionMappingInfo;
import org.sunyuyangg.service.core.method.OptionMappingServiceRequest;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {

    private boolean detectHandlerMethodsInAncestorContexts = false;

    private PathMatcher pathMatcher = new AntPathMatcher();

    private HandlerMethodMappingNamingStrategy<T> namingStrategy;

    private final MappingRegistry mappingRegistry = new MappingRegistry();

    @Override
    public void afterPropertiesSet() throws Exception {
        initHandlerMethods();
        // Total includes detected mappings + explicit registrations via registerMapping..
        int total = this.getHandlerMethods().size();
        logger.debug("Detected " + total + " mappings in " + formatMappingName());
    }

    protected void initHandlerMethods() {

        String[] beanNames = (this.detectHandlerMethodsInAncestorContexts ?
                BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class) :
                obtainApplicationContext().getBeanNamesForType(Object.class));

        for (String beanName : beanNames) {
            Class<?> beanType = null;
            try {
                beanType = obtainApplicationContext().getType(beanName);
            } catch (Throwable ex) {
                // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                if (logger.isTraceEnabled()) {
                    logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
                }
            }
            if (beanType != null && isHandler(beanType)) {
                detectHandlerMethods(beanName);
            }
        }
        handlerMethodsInitialized(getHandlerMethods());
    }

    /**
     * Invoked after all handler methods have been detected.
     *
     * @param handlerMethods a read-only map with handler methods and mappings.
     */
    protected void handlerMethodsInitialized(Map<T, HandlerMethod> handlerMethods) {
    }

    /**
     * Return a (read-only) map with all mappings and HandlerMethod's.
     */
    public Map<T, HandlerMethod> getHandlerMethods() {
        this.mappingRegistry.acquireReadLock();
        try {
            return Collections.unmodifiableMap(this.mappingRegistry.getMappings());
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    /**
     * Look for handler methods in a handler.
     *
     * @param handler the bean name of a handler or a handler instance
     */
    protected void detectHandlerMethods(final Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                obtainApplicationContext().getType((String) handler) : handler.getClass());

        if (handlerType != null) {
            final Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
                    (MethodIntrospector.MetadataLookup<T>) method -> {
                        try {
                            return getMappingForMethod(method, userType);
                        } catch (Throwable ex) {
                            throw new IllegalStateException("Invalid mapping on handler class [" +
                                    userType.getName() + "]: " + method, ex);
                        }
                    });
            Logger.info("Mapped " + methods.size() + " handler method(s) for " + userType + ": " + methods);
            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }
    }

    @Override
    protected Object getHandlerInternal(STAFServiceInterfaceLevel30.RequestInfo request) throws Exception {
        String lookupPath = getMappingPath(request);
        this.mappingRegistry.acquireReadLock();
        try {
            HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
            return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }


    private String getMappingPath(STAFServiceInterfaceLevel30.RequestInfo requestInfo) throws Exception {
        String[] commands = requestInfo.request.split(" ");
        if (commands.length < 2) {
            throw new Exception("request format is wrong!");
        }
        return Arrays.asList(commands).stream().limit(2).map(String::toUpperCase).collect(Collectors.joining("#"));
    }

    private HandlerMethod lookupHandlerMethod(String lookupPath, STAFServiceInterfaceLevel30.RequestInfo request) throws Exception {
        List<Match> matches = new ArrayList<>();
        List<T> directPathMatches = this.mappingRegistry.getMappingsByPath(lookupPath);
        if (directPathMatches != null) {
            addMatchingMappings(directPathMatches, matches, request);
        }

        if (matches.isEmpty()) {
            // No choice but to go through all mappings...
            addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, request);
        }

        if (!matches.isEmpty()) {
            Match bestMatch = matches.get(0);
            if (matches.size() > 1) {
                if (logger.isTraceEnabled()) {
                    logger.trace(matches.size() + " matching mapppings: " + matches);
                }
                Match secondBestMatch = matches.get(1);
                Method m1 = bestMatch.handlerMethod.getMethod();
                Method m2 = secondBestMatch.handlerMethod.getMethod();
                String path = request.request;
                throw new IllegalStateException(
                        "Ambiguous handler methods mapped for '" + path + "': {" + m1 + ", " + m2 + "}");
            }

            return bestMatch.handlerMethod;
        } else {
            return handleNoMatch(this.mappingRegistry.getMappings().keySet(), request);
        }
    }


    private HandlerMethod handleNoMatch(Set<T> keySet, STAFServiceInterfaceLevel30.RequestInfo request) throws Exception {
        return null;
    }

    private void addMatchingMappings(Collection<T> mappings, List<Match> matches, STAFServiceInterfaceLevel30.RequestInfo request) {
        for (T mapping : mappings) {
            T match = getMatchingMapping(mapping, request);
            if (match != null) {
                matches.add(new Match(match, this.mappingRegistry.getMappings().get(mapping)));
            }
        }
    }

    protected abstract T getMatchingMapping(T mapping, STAFServiceInterfaceLevel30.RequestInfo request);


    protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    /**
     * Provide the mapping for a handler method. A method for which no
     * mapping can be provided is not a handler method.
     *
     * @param method      the method to provide a mapping for
     * @param handlerType the handler type, possibly a sub-type of the method's
     *                    declaring class
     * @return the mapping, or {@code null} if the method is not mapped
     */
    @Nullable
    protected abstract T getMappingForMethod(Method method, Class<?> handlerType);

    /**
     * Whether the given type is a handler with handler methods.
     *
     * @param beanType the type of the bean being checked
     * @return "true" if this a handler type, "false" otherwise.
     */
    protected abstract boolean isHandler(Class<?> beanType);

    /**
     * Extract and return the URL paths contained in a mapping.
     */
    protected abstract String getMappingPath(T mapping);

    public void setDetectHandlerMethodsInAncestorContexts(boolean detectHandlerMethodsInAncestorContexts) {
        this.detectHandlerMethodsInAncestorContexts = detectHandlerMethodsInAncestorContexts;
    }

    /**
     * Create the HandlerMethod instance.
     *
     * @param handler either a bean name or an actual handler instance
     * @param method  the target method
     * @return the created HandlerMethod
     */
    protected abstract HandlerMethod createHandlerMethod(T mapping, Object handler, Method method) ;

    public void setHandlerMethodMappingNamingStrategy(HandlerMethodMappingNamingStrategy<T> namingStrategy) {
        this.namingStrategy = namingStrategy;
    }


    /**
     * Return the configured naming strategy or {@code null}.
     */
    @Nullable
    public HandlerMethodMappingNamingStrategy<T> getNamingStrategy() {
        return this.namingStrategy;
    }

    /**
     * Return the PathMatcher implementation to use for matching URL paths
     * against registered URL patterns.
     */
    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    protected abstract void registerHelp(T mapping);

    /**
     * A registry that maintains all mappings to handler methods, exposing methods
     * to perform lookups and providing concurrent access.
     *
     * <p>Package-private for testing purposes.
     */
    class MappingRegistry {

        private final Map<T, MappingRegistration<T>> registry = new HashMap<>();

        private final MultiValueMap<String, T> pathLookup = new LinkedMultiValueMap<>();

        private final Map<T, HandlerMethod> mappingLookup = new LinkedHashMap<>();

        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap<>();

        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        /**
         * Return all mappings and handler methods. Not thread-safe.
         *
         * @see #acquireReadLock()
         */
        public Map<T, HandlerMethod> getMappings() {
            return this.mappingLookup;
        }

        public List<T> getMappingsByPath(String path) {
            return this.pathLookup.get(path);
        }

        /**
         * Return handler methods by mapping name. Thread-safe for concurrent use.
         */
        public List<HandlerMethod> getHandlerMethodsByMappingName(String mappingName) {
            return this.nameLookup.get(mappingName);
        }

        /**
         * Acquire the read lock when using getMappings and getMappingsByUrl.
         */
        public void acquireReadLock() {
            this.readWriteLock.readLock().lock();
        }

        /**
         * Release the read lock after using getMappings and getMappingsByUrl.
         */
        public void releaseReadLock() {
            this.readWriteLock.readLock().unlock();
        }

        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();
            try {
                HandlerMethod handlerMethod = createHandlerMethod(mapping, handler, method);
                assertUniqueMethodMapping(handlerMethod, mapping);

                this.mappingLookup.put(mapping, handlerMethod);

                this.pathLookup.add(getMappingPath(mapping), mapping);

                String name = null;
                if (getNamingStrategy() != null) {
                    name = getNamingStrategy().getName(handlerMethod, mapping);
                    addMappingName(name, handlerMethod);
                }

                registerHelp(mapping);

                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, name));
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void assertUniqueMethodMapping(HandlerMethod newHandlerMethod, T mapping) {
            HandlerMethod handlerMethod = this.mappingLookup.get(mapping);
            if (handlerMethod != null && !handlerMethod.equals(newHandlerMethod)) {
                throw new IllegalStateException(
                        "Ambiguous mapping. Cannot map '" + newHandlerMethod.getBean() + "' method \n" +
                                newHandlerMethod + "\nto " + mapping + ": There is already '" +
                                handlerMethod.getBean() + "' bean method\n" + handlerMethod + " mapped.");
            }
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.emptyList();
            }

            for (HandlerMethod current : oldList) {
                if (handlerMethod.equals(current)) {
                    return;
                }
            }

            List<HandlerMethod> newList = new ArrayList<>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);
        }

        public void unregister(T mapping) {
            this.readWriteLock.writeLock().lock();
            try {
                MappingRegistration<T> definition = this.registry.remove(mapping);
                if (definition == null) {
                    return;
                }

                this.mappingLookup.remove(definition.getMapping());

                removeMappingName(definition);
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void removeMappingName(MappingRegistration<T> definition) {
            String name = definition.getMappingName();
            if (name == null) {
                return;
            }
            HandlerMethod handlerMethod = definition.getHandlerMethod();
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                return;
            }
            if (oldList.size() <= 1) {
                this.nameLookup.remove(name);
                return;
            }
            List<HandlerMethod> newList = new ArrayList<>(oldList.size() - 1);
            for (HandlerMethod current : oldList) {
                if (!current.equals(handlerMethod)) {
                    newList.add(current);
                }
            }
            this.nameLookup.put(name, newList);
        }
    }

    private static class MappingRegistration<T> {

        private final T mapping;

        private final HandlerMethod handlerMethod;

        @Nullable
        private final String mappingName;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod, @Nullable String mappingName) {

            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull(handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.mappingName = mappingName;
        }

        public T getMapping() {
            return this.mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        @Nullable
        public String getMappingName() {
            return this.mappingName;
        }
    }

    /**
     * A thin wrapper around a matched HandlerMethod and its mapping, for the purpose of
     * comparing the best match with a comparator in the context of the current request.
     */
    protected class Match {

        public final T mapping;

        public final HandlerMethod handlerMethod;

        public Match(T mapping, HandlerMethod handlerMethod) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
        }

        @Override
        public String toString() {
            return this.mapping.toString();
        }
    }


    private static class EmptyHandler {

        @SuppressWarnings("unused")
        public void handle() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }


}
