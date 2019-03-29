package com.oracle.truffle.r.fastrj;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.graalvm.polyglot.Value;

public final class GraalVM {

    private static class ValueIterable implements Iterable<Value> {

    	private final Value arrayValue; 
		private final long sz;
    	
    	private ValueIterable(Value arrayValue) {
    		assert arrayValue.hasArrayElements();
    		this.arrayValue = arrayValue;
    		this.sz = arrayValue.getArraySize();
    	}
    	
		@Override
		public Iterator<Value> iterator() {
			return new Iterator<Value>() {

				long i = 0;
				
				@Override
				public boolean hasNext() {
					return i < sz;
				}

				@Override
				public Value next() {
					return arrayValue.getArrayElement(i++);
				}
				
			};
		}
	};

	public static Iterable<Value> asIterable(Value value) {
		if (value.hasArrayElements()) {
			return new ValueIterable(value);
		} else {
			return Collections.singleton(value);
		}
	}

	public static Stream<Value> asStream(Value value) {
		if (value.hasArrayElements()) {
			return StreamSupport.stream(new ValueIterable(value).spliterator(), false);
		} else {
			return Stream.of(value);
		}
	}
	
	public static Value[] asArray(Value val) {
 		int sz = (int)val.getArraySize();
 		Value[] arr = new Value[sz];
 		for (int i = 0; i < sz; i++) {
 			arr[i] = val.getArrayElement(i);
 		}
 		return arr;
	}
	
	public static Object[] asArray(Value val, Function<Value, Object> mapper) {
 		int sz = (int)val.getArraySize();
 		Object[] arr = new Object[sz];
 		for (int i = 0; i < sz; i++) {
 			arr[i] = mapper.apply(val.getArrayElement(i));
 		}
 		return arr;
 	}
	
 	public static Map<String, Value> asMap(Value val) {
 		return asMap(val, x -> x);
 	}
 	
 	public static <R> Map<String, R> asMap(Value val, Function<Value, R> mapper) {
 		Map<String, R> map = new HashMap<>();
 		Set<String> keys = val.getMemberKeys(); 		
 		for (String key : keys) {
 			map.put(key, mapper.apply(val.getMember(key)));
 		}
 		return map;
 	}
}
