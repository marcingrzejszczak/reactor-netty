package reactor.netty.observability.contextpropagation.propagator;

import io.micrometer.api.lang.Nullable;
import reactor.netty.observability.contextpropagation.ContextContainer;

@SuppressWarnings("rawtypes")
public class ContainerUtils {

	@SuppressWarnings("unchecked")
	public static <T> T saveContainer(T bag, @Nullable ContextContainer container) {
		if (container == null) {
			return bag;
		}
		ContextContainerPropagator contextContainerPropagator = PropagatorLoader.getPropagatorForSet(bag);
		return (T) contextContainerPropagator.set(bag, container);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> ContextContainer restoreContainer(T bag) {
		ContextContainerPropagator contextContainerPropagator = PropagatorLoader.getPropagatorForGet(bag);
		return contextContainerPropagator.get(bag);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T resetContainer(T bag) {
		ContextContainerPropagator contextContainerPropagator = PropagatorLoader.getPropagatorForGet(bag);
		return (T) contextContainerPropagator.remove(bag);
	}
}