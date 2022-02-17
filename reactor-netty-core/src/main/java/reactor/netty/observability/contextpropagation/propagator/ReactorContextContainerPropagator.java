package reactor.netty.observability.contextpropagation.propagator;

import io.micrometer.contextpropagation.ContextContainer;
import io.micrometer.contextpropagation.ContextContainerPropagator;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

public class ReactorContextContainerPropagator implements ContextContainerPropagator<ContextView, Context> {
	private static final Object key = ContextContainer.class.getName();

	@Override
	public Context set(Context container, ContextContainer value) {
		return container.put(key, value);
	}

	@Override
	public ContextContainer get(ContextView container) {
		return container.getOrDefault(key, ContextContainer.NOOP);
	}

	@Override
	public Context remove(Context ctx) {
		return ctx.delete(key);
	}

	@Override
	public boolean supportsContextForSet(Object context) {
		return context instanceof Context;
	}

	@Override
	public boolean supportsContextForGet(Object context) {
		return context instanceof ContextView;
	}

}