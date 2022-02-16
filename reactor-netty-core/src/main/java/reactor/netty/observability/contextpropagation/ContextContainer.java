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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import io.micrometer.api.lang.Nullable;

/**
 * Holds context values to be propagated different context environments along
 * with the accessors required to propagate to and from those environments.
 */
public final class ContextContainer {

	private final Map<String, Object> values = new ConcurrentHashMap<>();

	private final List<ThreadLocalAccessor> threadLocalAccessors;

	private final Map<String, List<?>> accessors = new ConcurrentHashMap<>(1);

	private ContextContainer(List<ThreadLocalAccessor> accessors) {
		this.threadLocalAccessors = new ArrayList<>(accessors);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) this.values.get(key);
	}

	public boolean containsKey(String key) {
		return this.values.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T put(String key, T value) {
		return (T) this.values.put(key, (T) value);
	}

	public Object remove(String key) {
		return this.values.remove(key);
	}

	public <A> void setAccessors(String key, List<A> accessors) {
		this.accessors.put(key, accessors);
	}

	@SuppressWarnings("unchecked")
	public <A> List<A> getAccessors(String key) {
		return (List<A>) this.accessors.getOrDefault(key, Collections.emptyList());
	}

	public ContextContainer captureThreadLocalValues() {
		this.threadLocalAccessors.forEach(accessor -> accessor.captureValues(this));
		return this;
	}

	public Scope restoreThreadLocalValues() {
		this.threadLocalAccessors.forEach(accessor -> accessor.restoreValues(this));
		return () -> this.threadLocalAccessors.forEach(accessor -> accessor.resetValues(this));
	}

	/**
	 * Tries to run the action against an Observation. If the
	 * Observation is null, we just run the action, otherwise
	 * we run the action in scope.
	 *
	 * @param parent observation, potentially {@code null}
	 * @param action action to run
	 */
	public static void tryScoped(@Nullable ContextContainer parent, Runnable action) {
		if (parent != null) {
			try (Scope scope = parent.restoreThreadLocalValues()) {
				action.run();
			}
		}
		else {
			action.run();
		}
	}

	/**
	 * Tries to run the action against an Observation. If the
	 * Observation is null, we just run the action, otherwise
	 * we run the action in scope.
	 *
	 * @param parent observation, potentially {@code null}
	 * @param action action to run
	 * @return result of the action
	 */
	public static <T> T tryScoped(@Nullable ContextContainer parent, Supplier<T> action) {
		if (parent != null) {
			try (Scope scope = parent.restoreThreadLocalValues()) {
				return action.get();
			}
		}
		return action.get();
	}

	/**
	 * Create an instance with the given ThreadLocalAccessors to use.
	 */
	public static ContextContainer create() {
		return new ContextContainer(AccessorLoader.getThreadLocalAccessors());
	}

	/**
	 * Demarcates the scope of restored ThreadLocal values.
	 */
	public interface Scope extends AutoCloseable {

		@Override
		void close();

	}
}
