
package org.sunyuyangg.service.core.handler;

@FunctionalInterface
public interface HandlerMethodMappingNamingStrategy<T> {

	String getName(HandlerMethod handlerMethod, T mapping);

}
