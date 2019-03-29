package com.oracle.truffle.r.fastrj;

import java.util.function.Function;

public final class Dplyr {
	
	private final FastR fastR;

	public Dplyr(FastR fastR) {
		this.fastR = fastR;
		this.fastR.require("dplyr");
	}

	public FastR getFastR() {
		return fastR;
	}
	
	public DplyrQueryBuilder newQueryBuilder() {
		return new DplyrQueryBuilder(this);
	}

	public DplyrQueryBuilder newQueryBuilder(String head) {
		return new DplyrQueryBuilder(this, head);
	}

	public Dplyr stringsAsFactors(boolean flag) {
		fastR.setOption("stringsAsFactors", flag);
		return this;
	}
	
	@FunctionalInterface
	public interface F2<T1, T2, R> {
		R apply(T1 t1, T2 t2);
	}
	
	@FunctionalInterface
	public interface F3<T1, T2, T3, R> {
		R apply(T1 t1, T2 t2, T3 t3);
	}

	@FunctionalInterface
	public interface F4<T1, T2, T3, T4, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4);
	}

	public <T> Function<T, Object> resultAsVector(Function<T, Object> fn) {
		return (t) -> {
			return fastR.asVector(fn.apply(t));
		};
	} 

	public <T1, T2> F2<T1, T2, Object> resultAsVector(F2<T1, T2, Object> fn) {
		return (t1, t2) -> {
			return fastR.asVector(fn.apply(t1, t2));
		};
	} 
	
	public <T1, T2, T3> F3<T1, T2, T3, Object> resultAsVector(F3<T1, T2, T3, Object> fn) {
		return (t1, t2, t3) -> {
			return fastR.asVector(fn.apply(t1, t2, t3));
		};
	} 

	public <T1, T2, T3, T4> F4<T1, T2, T3, T4, Object> resultAsVector(F4<T1, T2, T3, T4, Object> fn) {
		return (t1, t2, t3, t4) -> {
			return fastR.asVector(fn.apply(t1, t2, t3, t4));
		};
	} 
}
