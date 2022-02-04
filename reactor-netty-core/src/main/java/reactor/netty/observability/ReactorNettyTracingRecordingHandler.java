/*
 * Copyright (c) 2021 VMware, Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.netty.observability;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.micrometer.api.instrument.Tag;
import io.micrometer.api.instrument.observation.Observation;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingRecordingHandler;

public class ReactorNettyTracingRecordingHandler extends DefaultTracingRecordingHandler {

	public ReactorNettyTracingRecordingHandler(Tracer tracer) {
		super(tracer);
	}

	@Override
	public void tagSpan(Observation.Context context, Span span) {
		// TODO: Duplication
		SocketAddress address = context.get(SocketAddress.class);
		if (address != null && address instanceof InetSocketAddress) {
			InetSocketAddress inet = (InetSocketAddress) address;
			span.remoteIpAndPort(inet.getHostString(), inet.getPort());
		}
		for (Tag tag : context.getHighCardinalityTags()) {
			span.tag(tag.getKey(), tag.getValue());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getSpanName(Observation.Context context) {
		String name = context.getContextualName();
		if (name != null) {
			return name;
		}
		return super.getSpanName(context);
	}

	@Override
	public boolean supportsContext(Observation.Context handlerContext) {
		return handlerContext instanceof ReactorNettyHandlerContext;
	}
}
