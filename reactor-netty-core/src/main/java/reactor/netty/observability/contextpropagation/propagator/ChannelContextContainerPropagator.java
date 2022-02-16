package reactor.netty.observability.contextpropagation.propagator;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import reactor.netty.ReactorNetty;
import reactor.netty.observability.contextpropagation.ContextContainer;

public class ChannelContextContainerPropagator implements ContextContainerPropagator<Channel, Channel> {

	private static AttributeKey<Object> key = ReactorNetty.CONTEXT_PROPAGATION_ATTR;

	@Override
	public Channel set(Channel ctx, ContextContainer value) {
		ctx.attr(key).compareAndSet(null, value);
		return ctx;
	}

	@Override
	public ContextContainer get(Channel ctx) {
		return (ContextContainer) ctx.attr(key).get();
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