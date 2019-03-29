package com.oracle.truffle.r.fastrj;

import java.io.IOException;
import java.util.Map;

import org.graalvm.polyglot.Value;

public final class BrexitHandler {

	private final Value plotFn;
	private final Value plotData;
	private final Value plotHeadDataFn;
	
	public BrexitHandler(FastR fastR) {
		this.plotHeadDataFn = fastR.asFunction("head(_data_, _n_)");

		RBundle bundle = new RBundle(fastR, BrexitHandler.class);
		this.plotFn = bundle.get("svgExample1");
		this.plotData = bundle.get("brexitByConstituency");
	}
	
	public static BrexitHandler getHandler(Map<String, Object> request) {
		return FastR.getInstance().getOrCreate(BrexitHandler.class, fastR -> new BrexitHandler(fastR));
	}
	
	public String plotChart(int maxCols) throws IOException {
		Value out = plotFn.execute(plotHeadDataFn.execute(plotData, maxCols));
		return out.asString();
	}

}

