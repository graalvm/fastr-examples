package com.oracle.truffle.r.fastrj;

import java.io.IOException;
import java.util.Map;

import org.graalvm.polyglot.Value;

public final class IrisHandler {

	private final FastR fastR;
	private final Value irisData;
	private final Value kableQuery;
	
	public IrisHandler(FastR fastR) {
		this.fastR = fastR;
		this.irisData = fastR.eval("iris");		
		
		RBundle bundle = new RBundle(fastR, IrisHandler.class);
		this.kableQuery = bundle.buildQuery("kableQuerySample1");
	}
	
	public static IrisHandler getHandler(Map<String, Object> request) {
		return FastR.getInstance().getOrCreate(IrisHandler.class, fastR -> new IrisHandler(fastR));
	}
	
	public String generateTable(int maxRows) throws IOException {
		Value data = fastR.execute("head", irisData, maxRows);
		return kableQuery.execute(data, maxRows).asString();
	}
	
}
