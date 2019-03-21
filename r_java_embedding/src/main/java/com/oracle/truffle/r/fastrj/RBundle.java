package com.oracle.truffle.r.fastrj;

import static com.oracle.truffle.r.fastrj.FastR.named;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

import org.graalvm.polyglot.Value;

public final class RBundle {

	private final FastR fastR;
	
	/**
	 * An environment containing the bundle
	 */
	private final Value qenv; 
	
	public RBundle(FastR fastR, Class<?> bundleFileCompanionClass) {
		this.fastR = fastR;
 		URL queryFileResource = bundleFileCompanionClass.getResource(bundleFileCompanionClass.getSimpleName() + ".R");
		if (queryFileResource == null) {
			throw new RuntimeException("R bundle for class " + bundleFileCompanionClass.getName() + " not found");
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(queryFileResource.openStream()))) {
			String bundleSource = br.lines().collect(Collectors.joining(System.lineSeparator()));
			Value queries = fastR.parse(bundleSource);
			this.qenv = fastR.newEnvironment();
			fastR.call("eval", queries, named("envir", qenv));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public RBundle(FastR fastR, File bundleFilePath) {
		this.fastR = fastR;
		Value bundle = fastR.execute("parse", bundleFilePath.getAbsolutePath());
		this.qenv = fastR.newEnvironment();
		fastR.call("eval", bundle, named("envir", qenv));
	}
	
	public Set<String> getQueryNames() {
		return qenv.getMemberKeys();
	}

	public Value get(String name) {
		return fastR.execute("get", name, qenv);
	}
	
	public Value buildQuery(String queryName) {
		Value queryAST = fastR.execute("get", queryName, qenv);
		return fastR.asFunction(queryAST);
	}
}
