package com.oracle.truffle.r.fastrj;

import java.io.Closeable;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public final class FastR implements Closeable {
	
	private static final String SET_OPTION = "function(name, value) { " +
			"args <- list(); " +
			"args[name] <- value; " +
			"options(args)" +
			"}";

	private static final String AS_VECTOR = "function(x) sapply(x, identity)";

	private static final String SET_NAMES = "function(x, nm) { names(x) <- as.character(nm); x }";
	
	private static final String PARSE = "function(src) parse(text = src)";
	
	private static final String AS_FUNCTION = "function(fml, bd, envir) {\n" + 
	"	nm <- as.character(fml)\n" + 
	"	fml <- vector('list', length(nm))\n" + 
	"	names(fml) <- nm\n" + 
	"	fml[[length(fml) + 1]] <- bd\n" + 
	"	as.function(fml, envir = envir)\n" + 
	"}\n"; 
	
	final Context plgCtx;
	private final Value setOption;
	private final Value asVector;
	private final Value parse;
	private final Value asFunction;
	private final Value setNames;

	private final Map<String, Value> functions = new HashMap<String, Value>(); 
	
	public FastR() {
        this(Context.newBuilder("R").allowAllAccess(true).build());
	}
	
	private FastR(Context plgCtx) {
		this.plgCtx = plgCtx;
        this.setOption = eval(SET_OPTION);
	    this.asVector = eval(AS_VECTOR);
        this.parse = eval(PARSE);
        this.asFunction = eval(AS_FUNCTION);
        this.setNames = eval(SET_NAMES);
	}

	public static FastR getInstance() {
		return getInstance(Context.getCurrent());
	}

	public static FastR getInstance(Context plgCtx) {
		Value fastrVal = plgCtx.eval("R", "mget('__FastR__', envir= .GlobalEnv, ifnotfound=list('__FastR__' = NULL))").getMember("__FastR__");
        FastR fastR = fastrVal.isNull() ? null : (FastR) fastrVal.asHostObject();
        if (fastR == null) {
        	fastR = new FastR(plgCtx);
    		fastR.assign("__FastR__", fastR, fastR.globalEnvironment());
        }
        return fastR;
	}
	
	public <T> T getOrCreate(Class<T> valCls, Function<FastR, T> supplier) {
		return getOrCreate(valCls.getName(), supplier);
	}
	
	public <T> T getOrCreate(String name, Function<FastR, T> supplier) {
		Value tVal = call("mget", name, named("envir", globalEnvironment()), named("ifnotfound", list(named(name, null)))).getMember(name);
        @SuppressWarnings("unchecked")
		T t = tVal.isNull() ? null : (T) tVal.asHostObject();
        if (t == null) {
        	t = supplier.apply(this);
    		assign(name, t, globalEnvironment());
        }
		return t;
	}

	public Value library(String packageName) {
		return execute("library", packageName);
	}

	public boolean require(String packageName) {
		return call("require", packageName, named("character.only", true)).asBoolean();
	}

	public void installPackage(String packageName) {
		execute("install.packages", packageName);
	}
	
	public Value environment() {
		return execute("environment");
	}
	
	public Value newEnvironment() {
		return execute("new.env");
	}

	public Value newEnvironment(Map<String, Object> entries) {
		Value env = newEnvironment();
		for (Map.Entry<String, Object> entry : entries.entrySet()) {
			assign(entry.getKey(), entry.getValue(), env);
		}
		return env;
	}

	
	public Value globalEnvironment() {
		return eval(".GlobalEnv");
	}

	public Value execute(String function, Object... args) {
		Value fn = functions.get(function);
		if (fn == null) {
			fn = eval(function);
			functions.put(function, fn);
		}
		
		return fn.execute(args);
	}

	public Value call(String function, Object... args) {
		Value fn = functions.get(function);
		if (fn == null) {
			fn = eval(function);
			functions.put(function, fn);
		}
		
		List<Object> actualArgs = new ArrayList<Object>();
		List<String> argNames = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof NamedArg) {
				actualArgs.add(((NamedArg)args[i]).value);
				argNames.add(((NamedArg)args[i]).name);
			} else {
				actualArgs.add(args[i]);
				argNames.add("");
			}
		}
		
		Value argList = execute("as.list", actualArgs);
		argList = setNames.execute(argList, argNames);
		
		return execute("do.call", function, argList);
	}
	
	public static NamedArg named(String name, Object value) {
		return new NamedArg(name, value);
	}
	
	public Context getContext() {
		return this.plgCtx;
	}
	
	public Value eval(CharSequence source) {
		return this.plgCtx.eval("R", source);
	}
	
	public Value parse(CharSequence source) {
		return parse.execute(source);
	}

	public static <R> R treewalk(Value tree, R input, BiFunction<R, Value, R> visitor) {
		R result = visitor.apply(input, tree);
		if (tree.hasArrayElements()) {
			long childrenSize = tree.getArraySize();
			for (int i = 0; i < childrenSize; i++) {
				Value child = tree.getArrayElement(i);
				result = treewalk(child, result, visitor);
			}
		}
		return result;
	}

	public Value get(String name) {
		return execute("get", name);
	}	

	public Value getOrElse(String name, Object ifnotfound) {
		return execute("mget", name, named("ifnotfound", list(named(name, ifnotfound)))).getMember(name);
	}	

	public Value get(String name, Value envir) {
		return call("get", name, named("envir", envir));
	}	

	public Value getOrElse(String name, Value envir, Object ifnotfound) {
		return call("mget", name, named("envir", envir), named("ifnotfound", list(named(name, ifnotfound)))).getMember(name);
	}	

	public FastR assign(String name, Object value) {
		assign(name, value, environment());
		return this;
	}

	public FastR assign(String name, Object value, Value envir) {
		call("assign", name, value, named("envir", envir));
		return this;
	}
	
	public FastR setOption(String name, Object value) {
		setOption.execute(name, value);
		return this;
	}

	public Value getOption(String name) {
		return execute("getOption", name);
	}

	public Value asVector(Object x) {
		return asVector.execute(x);
	} 
	
	public Value list(Object... elements) {
		return call("list", elements);
	}
	
	public Value asDataFrame(Object x) {
		return execute("as.data.frame", x);
	} 
	
	public String asString(Object x) {
		return execute("as.character", x).asString();
	} 
	
	public Value asFunction(CharSequence expression) {
		return asFunction(expression, Collections.emptyMap());
	}
	
	public Value asFunction(CharSequence expression, Map<String, Object> environment) {
		Value bodyAST = parse(expression);
		return asFunction(bodyAST.getArrayElement(0), environment);
	}

	public Value asFunction(Value bodyAST) {
		return asFunction(bodyAST, Collections.emptyMap());
	}
	
	public Value asFunction(Value bodyAST, Map<String, Object> environment) {
		Value enclosingEnv = newEnvironment(environment);
		List<String> formals = retrieveFormals(bodyAST);
		return asFunction(formals, bodyAST, enclosingEnv);
	}
	
	public Value asFunction(List<String> formals, Value bodyAST, Value enclosingEnv) {
		return asFunction.execute(formals, bodyAST, enclosingEnv);
	} 

	public int nrow(Value table) {
		return execute("nrow", table).asInt();
	}
	
	public int ncol(Value table) {
		return execute("ncol", table).asInt();
	}

	public Value readCSV(String filePath) {
		return execute("read.csv", filePath);
	}
	
	public static List<List<Value>> asRows(final Value dataFrame) {
		if (!dataFrame.hasArrayElements()) {
			return Collections.singletonList(Collections.singletonList(dataFrame));
		}
		
		final int ncol = (int) dataFrame.getArraySize();
		if (ncol == 0) {
			return Collections.emptyList();
		}
		
		final int nrow = (int) dataFrame.getArrayElement(0).getArraySize();
		if (nrow == 0) {
			return Collections.emptyList();
		}
		
		return new AbstractList<List<Value>>() {

			@Override
			public List<Value> get(int index) {
				return new AbstractList<Value>() {

					@Override
					public Value get(int colIndex) {
						return dataFrame.getArrayElement(colIndex).getArrayElement(index);
					}

					@Override
					public int size() {
						return ncol;
					}
				};
			}

			@Override
			public int size() {
				return nrow;
			}
			
		};
	}
	
	public boolean isSymbol(Value x) {
		return execute("is.symbol", x).asBoolean();
	}
	
    @FunctionalInterface
    public interface VarArgFunction<T> {
    	T execute(Object... args);
    }
    
	private List<String> retrieveFormals(Value queryAST) {
		Set<String> seenFormals = new HashSet<String>();
        List<String> queryFormals = new ArrayList<String>();
        FastR.treewalk(queryAST, null, (res, node) -> {
        	if (isSymbol(node)) {
        		String sym = asString(node);
        		if (sym.startsWith("_") && sym.endsWith("_")) {
        			if (!seenFormals.contains(sym)) {
            			seenFormals.add(sym);
            			queryFormals.add(sym);
        			}
        		}
        	}
        	return res;
        });
        return queryFormals;
	}

    public final static class NamedArg {
    	private final String name;
    	private final Object value;
    	
    	public NamedArg(String name, Object value) {
    		this.name = name;
    		this.value = value;
    	}
    	
    }

	@Override
	public void close() throws IOException {
		plgCtx.close();
	}
}
