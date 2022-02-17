package reactor.netty.observability.contextpropagation.propagator;

import io.micrometer.contextpropagation.ContextContainer;
import io.micrometer.contextpropagation.ContextContainerPropagator;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import reactor.netty.ReactorNetty;

public class ChannelContextContainerPropagator implements ContextContainerPropagator<Channel, Channel> {

	private static AttributeKey<Object> key = ReactorNetty.CONTEXT_PROPAGATION_ATTR;

	@Override
	public Channel set(Channel ctx, ContextContainer value) {
		ctx.attr(key).compareAndSet(null, value);
		return ctx;
	}

	@Override
	public ContextContainer get(Channel ctx) {
		ContextContainer container = (ContextContainer) ctx.attr(key).get();
		if (container != null) {
			return container;
		}
		return ContextContainer.NOOP;
	}

	@Override
	public Channel remove(Channel ctx) {
		ctx.attr(key).set(null);
		return ctx;
	}

	@Override
	public boolean supportsContextForSet(Object context) {
		return context instanceof Channel;
	}

	@Override
	public boolean supportsContextForGet(Object context) {
		return supportsContextForSet(context);
	}
}