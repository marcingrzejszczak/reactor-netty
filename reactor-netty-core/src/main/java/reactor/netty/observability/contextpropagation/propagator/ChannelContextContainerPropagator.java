package reactor.netty.observability.contextpropagation.propagator;

import io.micrometer.contextpropagation.ContextContainer;
import io.micrometer.contextpropagation.ContextContainerPropagator;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import reactor.netty.ReactorNetty;
import reactor.util.context.ContextView;

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
	public Class<?> getSupportedContextClassForSet() {
		return Channel.class;
	}

	@Override
	public Class<?> getSupportedContextClassForGet() {
		return Channel.class;
	}

}