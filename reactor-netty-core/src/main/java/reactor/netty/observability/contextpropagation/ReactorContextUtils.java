/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.netty.observability.contextpropagation;

import java.util.List;

import io.micrometer.contextpropagation.ContextContainer;
import reactor.util.context.Context;

/**
 * Utility methods to apply {@link ReactorContextAccessor} to a
 * {@link ContextContainer} without introducing a dependency on Reactor in
 * {@link ContextContainer}.
 */
public final class ReactorContextUtils {

	private static final String ACCESSORS_KEY = "REACTOR";

	/**
	 * Create a {@link ContextContainer} with the given ThreadLocal and Reactor
	 * Context accessors. A shortcut for creating the container first with
	 * {@link ContextContainer#create()} and then calling
	 */
	public static ContextContainer create() {
		ContextContainer container = ContextContainer.create();
		container.setAccessors(ACCESSORS_KEY, ReactorAccessorLoader.getReactorContextAccessor());
		return container;
	}

	/**
	 * Capture Reactor context values and save them in the given
	 * {@link ContextContainer}.
	 */
	public static void captureReactorContext(Context context, ContextContainer container) {
		List<ReactorContextAccessor> accessors = container.getAccessors(ACCESSORS_KEY);
		accessors.forEach(accessor -> accessor.captureValues(context, container));
	}

	/**
	 * Restore Reactor context values previously saved in the given
	 * {@link ContextContainer}.
	 */
	public static Context restoreReactorContext(Context context, ContextContainer container) {
		if (container.isNoOp()) {
			return context;
		}
		List<ReactorContextAccessor> accessors = container.getAccessors(ACCESSORS_KEY);
		for (ReactorContextAccessor accessor : accessors) {
			context = accessor.restoreValues(context, container);
		}
		return context;
	}

}
