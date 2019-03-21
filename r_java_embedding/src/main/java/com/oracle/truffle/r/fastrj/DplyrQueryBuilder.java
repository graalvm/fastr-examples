package com.oracle.truffle.r.fastrj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.graalvm.polyglot.Value;

import com.oracle.truffle.r.fastrj.Dplyr.F2;
import com.oracle.truffle.r.fastrj.Dplyr.F3;
import com.oracle.truffle.r.fastrj.Dplyr.F4;

public final class DplyrQueryBuilder {

	private final Dplyr dplyr;
	private final String head;
	private final List<String> steps = new ArrayList<String>();
	private final Map<String, Object> queryEnvEntries = new HashMap<String, Object>();

	DplyrQueryBuilder(Dplyr dplyr) {
		this(dplyr, null);
	}	

	DplyrQueryBuilder(Dplyr dplyr, String head) {
		this.dplyr = dplyr;
		this.head = head;
	}	

	public DplyrQueryBuilder putEnvironmentEntries(Map<String, Object> queryEnvEntries) {
		this.queryEnvEntries.putAll(queryEnvEntries);
		return this;
	} 
	
	private void buildStep(String stepName, String... args) {
		steps.add(new StringBuilder().append(stepName).append("(").append(String.join(",", args)).append(")").toString());
	}
	
	public DplyrQueryBuilder select(String... columns) {
		buildStep("select", columns);
		return this;
	}
	
	public DplyrQueryBuilder filter(String... predicates) {
		buildStep("filter", predicates);
		return this;
	}

	public DplyrQueryBuilder arrange(String... columns) {
		buildStep("arrange", columns);
		return this;
	}

	public DplyrQueryBuilder group_by(String... columns) {
		buildStep("group_by", columns);
		return this;
	}

	public DplyrQueryBuilder summarise(String... columns) {
		buildStep("summarise", columns);
		return this;
	}

	public DplyrQueryBuilder do_(String... computations) {
		buildStep("do", computations);
		return this;
	}

	public DplyrQueryBuilder mutate(String... columnMutations) {
		buildStep("mutate", columnMutations);
		return this;
	}

	public DplyrQueryBuilder mutate_if(String condition, String columnMutation) {
		buildStep("mutate_if", condition, columnMutation);
		return this;
	}

	public <T> DplyrQueryBuilder assignFilter(String name, Function<T, Boolean> pred) {
		queryEnvEntries.put(name, dplyr.resultAsVector(new Function<List<T>, Object>() {

			@Override
			public List<Boolean> apply(List<T> t) {
        		// Each element indicates whether the row is retained  
        		List<Boolean> retained = new ArrayList<>();
        		for (int i = 0; i < t.size(); i++) {
        			boolean retainIt = pred.apply(t.get(i));
        			retained.add(retainIt);
        		}
				return retained;
			}
			
		}));
		return this;
	}

	public <T1, T2> DplyrQueryBuilder assignFilter(String name, F2<T1, T2, Boolean> pred) {
		queryEnvEntries.put(name, dplyr.resultAsVector(new F2<List<T1>, List<T2>, Object>() {

			@Override
			public List<Boolean> apply(List<T1> t1, List<T2> t2) {
        		// Each element indicates whether the row is retained  
        		List<Boolean> retained = new ArrayList<>();
        		for (int i = 0; i < t1.size(); i++) {
        			boolean retainIt = pred.apply(t1.get(i), t2.get(i));
        			retained.add(retainIt);
        		}
				return retained;
			}
			
		}));
		return this;
	}
	
	public <T1, T2, T3> DplyrQueryBuilder assignFilter(String name, F3<T1, T2, T3, Boolean> pred) {
		queryEnvEntries.put(name, dplyr.resultAsVector(new F3<List<T1>, List<T2>, List<T3>, Object>() {

			@Override
			public List<Boolean> apply(List<T1> t1, List<T2> t2, List<T3> t3) {
        		// Each element indicates whether the row is retained  
        		List<Boolean> retained = new ArrayList<>();
        		for (int i = 0; i < t1.size(); i++) {
        			boolean retainIt = pred.apply(t1.get(i), t2.get(i), t3.get(i));
        			retained.add(i, retainIt);
        		}
				return retained;
			}
			
		}));
		return this;
	}

	public <T1, T2, T3, T4> DplyrQueryBuilder assignFilter(String name, F4<T1, T2, T3, T4, Boolean> pred) {
		queryEnvEntries.put(name, dplyr.resultAsVector(new F4<List<T1>, List<T2>, List<T3>, List<T4>, Object>() {

			@Override
			public List<Boolean> apply(List<T1> t1, List<T2> t2, List<T3> t3, List<T4> t4) {
        		// Each element indicates whether the row is retained  
        		List<Boolean> retained = new ArrayList<>();
        		for (int i = 0; i < t1.size(); i++) {
        			boolean retainIt = pred.apply(t1.get(i), t2.get(i), t3.get(i), t4.get(i));
        			retained.add(i, retainIt);
        		}
				return retained;
			}
			
		}));
		return this;
	}

	public DplyrQueryBuilder assignAggregator(String name, Object aggr) {
		assign(name, aggr);
		return this;
	}

	public DplyrQueryBuilder assign(String name, Object value) {
		queryEnvEntries.put(name, value);
		return this;
	}

	private String getQuerySource() {
		return (head == null ? "_table_" : head)+ " %>% " + String.join(" %>% ", steps);
	}

	public Value build() {
		return dplyr.getFastR().asFunction(getQuerySource(), queryEnvEntries);
	}
	
}
