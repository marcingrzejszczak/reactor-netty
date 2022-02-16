package reactor.netty.observability.contextpropagation.propagator;

import io.micrometer.api.lang.Nullable;
import reactor.netty.observability.contextpropagation.ContextContainer;

public interface ContextContainerPropagator<READ, WRITE> {

	@SuppressWarnings("rawtypes")
	ContextContainerPropagator NOOP = new ContextContainerPropagator() {
		@Override
		public Object set(Object ctx, ContextContainer value) {
			return ctx;
		}

		@Override
		public ContextContainer get(Object ctx) {
			return null;
		}

		@Override
		public Object remove(Object ctx) {
			return ctx;
		}

		@Override
		public boolean supportsContextForSet(Object context) {
			return false;
		}

		@Override
		public boolean supportsContextForGet(Object context) {
			return false;
		}
	};

	WRITE set(WRITE ctx, ContextContainer value);

	@Nullable
	ContextContainer get(READ ctx);

	@Nullable
	WRITE remove(WRITE ctx);

	boolean supportsContextForSet(Object context);

	boolean supportsContextForGet(Object context);
}





