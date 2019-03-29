package com.oracle.truffle.r.fastrj;

import static com.oracle.truffle.r.fastrj.FastR.asRows;
import static com.oracle.truffle.r.fastrj.FastR.named;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;

public class FastREmbeddingDemo {

    public static final class User {
        public final int id;
        public final String name;
        public final String favoriteLanguage;

        public User(int id, String name, String favoriteLanguage) {
            this.id = id;
            this.name = name;
            this.favoriteLanguage = favoriteLanguage;
        }
        
        public String toDisplayName(String prefix) {
        	return prefix + name;
        }
    }

    public static class NameColumn implements ProxyArray {
        private final User[] users;

        public NameColumn(User[] users) {
            this.users = users;
        }

        public Object get(long index) {
            return users[(int) index].name;
        }

        public void set(long index, Value value) {
            throw new UnsupportedOperationException();
        }

        public long getSize() {
            return users.length;
        }
    }

    public static class IdColumn implements ProxyArray {
        private final User[] users;

        public IdColumn(User[] users) {
            this.users = users;
        }

        public Object get(long index) {
            return (Object) users[(int) index].id;
        }

        public void set(long index, Value value) {
            throw new UnsupportedOperationException();
        }

        public long getSize() {
            return users.length;
        }
    }

    public static class AnyColumn implements ProxyArray {
        private final User[] users;
        private final Function<User, Object> getter;

        public AnyColumn(User[] users, Function<User, Object> getter) {
            this.users = users;
            this.getter = getter;
        }


        @Override
        public Object get(long index) {
            return getter.apply(users[(int) index]);
        }

        @Override
        public void set(long index, Value value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getSize() {
            return users.length;
        }
    }

    public static class UsersTable {
        public final IdColumn id;
        public final NameColumn name;
        public final AnyColumn language;

        public UsersTable(User[] users) {
            this.id = new IdColumn(users);
            this.name = new NameColumn(users);
            this.language = new AnyColumn(users, x -> x.favoriteLanguage);
        }
    }

    static Function<List<Integer>, Object> myAggr = x -> {
    	return x.stream().reduce((res, b) -> res + b).get();
    };

    static Function<Object, Object> myAggr2 = x -> {
    	// TODO: x can be NULL, NA, a scalar or a list
    	return 1;
    };

    //static final FastR fastR = new FastR();
    //static final Dplyr dplyr = new Dplyr(fastR);
    static {
        //dplyr.stringsAsFactors(false);
    }

    public static void main(String[] args) throws IOException {
    	
    	FastR fastR = new FastR();
    	
//    	polyglotUsage();
    	
//    	IrisHandler h = new IrisHandler(fastR);
//		Object res = h.generateTable(1);
//    	
//        basicFastR();
//
//    	
//    	usingPackages();
    	
//        usingJavaObjects();
//        
//        parseSimpleQuery(fastR);
//        
//        parseSimpleParamQuery(fastR);
//        
//        parseSimpleParamQueryFromBundle(fastR);
//
//        buildSimpleQuery(fastR);
        
//        
//        buildQueryOverJavaObject();
//
        buildQueryWithCustomFilter(fastR);
//
//        buildQueryWithCustomAggregator();
//
//		loadAndQueryCSVFile();
//
//		queryParameters();
//		
//    	javax.swing.SwingUtilities.invokeLater(() -> plotInJava1());
//
//    	javax.swing.SwingUtilities.invokeLater(() -> plotInJava2());
//    	
//    	javaHeadlessGraphics();
    }

	private static void plotInJava1() {
		FastR fastR = new FastR();
		fastR.require("ggplot2");
		
		Value iris = fastR.eval("iris");
		PlotJPanel plotPanel = PlotJPanel.createSimplePlotPanel(fastR, 
				"ggplot(_iris_, aes(x = Petal.Width, y = Sepal.Length)) + \n" + 
				"  geom_point() +\n" + 
				"  stat_smooth(method = \"lm\", col = \"red\")", 
				() -> iris);
		plotPanel.showFrame("Iris Regression", 700, 500);
	}

	
	private static void plotInJava2() {
		FastR fastR = new FastR();
		fastR.library("dplyr");
		fastR.library("ggplot2");
		
		Value sepalPredQ = fastR.asFunction("iris %>% group_by(Species) %>% do(pred = lm(Sepal.Width ~ Petal.Width, .))");
		Value sepalPred = sepalPredQ.execute();

		JFrame frame = new JFrame("Iris Regression");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(750, 500));

		Value[] speciesModel = GraalVM.asArray(sepalPred.getMember("Species"));
		
		JComboBox<Value> speciesCombo = new JComboBox<>(speciesModel);
		speciesCombo.setSelectedIndex(0);

		PlotJPanel plotPanel = PlotJPanel.createRegressionPlotPanel(fastR.getContext(), () -> {
			Value selectedItem = speciesCombo.getItemAt(speciesCombo.getSelectedIndex());
			return sepalPred.getMember("pred").getArrayElement(selectedItem.asInt() - 1);
		});

		ActionListener updatePlot = e -> plotPanel.repaint();
		speciesCombo.addActionListener(updatePlot);

		JPanel options = new JPanel();
		options.setLayout(new FlowLayout(FlowLayout.CENTER));
		options.add(new JLabel("Species: "));
		options.add(speciesCombo);
		speciesCombo.setBorder(new EmptyBorder(0, 0, 0, 40));

		frame.setLayout(new BorderLayout(20, 20));
		Container pane = frame.getContentPane();
		pane.add(options, BorderLayout.PAGE_START);
		pane.add(plotPanel, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}

    static void javaHeadlessGraphics() {
		FastR fastR = new FastR();
		fastR.library("dplyr");
		fastR.library("ggplot2");
		
		Value sepalPredQ = fastR.asFunction("iris %>% group_by(Species) %>% do(pred = lm(Sepal.Width ~ Petal.Width, .))");
		Value sepalPred = sepalPredQ.execute();
		AtomicInteger currentModelIndex = new AtomicInteger();
		PlotJPanel plotPanel = PlotJPanel.createRegressionPlotPanel(fastR.getContext(), () -> {
			return sepalPred.getMember("pred").getArrayElement(currentModelIndex.get());
		});
		plotPanel.setSize(700, 500);
		
		int nrow = fastR.nrow(sepalPred);
		for (int i = 0; i < nrow; i++) {
			currentModelIndex.set(i);
			BufferedImage image = plotPanel.paintHeadless();
			
	        // Save the image to file
	        String dest = "test-" + i + ".png";
	        try {
	            ImageIO.write(image, "png", new File(dest));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
    }
    
	private static void queryParameters(FastR fastR) {
		Value msleepDF = fastR.readCSV("src/main/resources/msleep_ggplot2.csv");
        String msleepQ2 = "_msleep_ %>%" + 
        "    group_by(order) %>%" + 
        "    summarise(avg_sleep = _weight1_ * mean(sleep_total)," + 
        "              min_sleep = _weight2_ * min(sleep_total)," + 
        "              max_sleep = _weight3_ * max(sleep_total)," + 
        "              total = n())";
        
        Value dplyrQuery2 = fastR.asFunction(msleepQ2);
        Value res = dplyrQuery2.execute(msleepDF, 1, 2, 3);

        for (List<Value> row : asRows(res)) {
            System.out.println(row);
        }
	}

	private static void loadAndQueryCSVFile(FastR fastR) {
		Value msleepDF = fastR.readCSV("src/main/resources/msleep_ggplot2.csv");
        fastR.assign("msleep", msleepDF);

        String msleepQ1 = "msleep %>%" + 
        "    group_by(order) %>%" + 
        "    summarise(avg_sleep = mean(sleep_total)," + 
        "              min_sleep = min(sleep_total)," + 
        "              max_sleep = max(sleep_total)," + 
        "              total = n())";

        Value dplyrQuery1 = fastR.asFunction(msleepQ1);
        Value res = dplyrQuery1.execute();

        for (List<Value> row : asRows(res)) {
            System.out.println(row);
        }
	}

	private static void buildQueryOverJavaObject(FastR fastR) {
		Dplyr dplyr = new Dplyr(fastR);
        DplyrQueryBuilder dplyrBuilder = dplyr.newQueryBuilder();
        dplyrBuilder.select("id", "name", "language").
        	group_by("language").
        	summarise("count = n()");
        
        Value dplyrQuery0 = dplyrBuilder.build();

        User[] data = getUsers();
        Value df = fastR.asDataFrame(new UsersTable(data));
		Value res = dplyrQuery0.execute(df);

        for (List<Value> row : asRows(res)) {
            System.out.println(row);
        }
	}

	private static boolean isAllowedMPG(double mpg) {
		return mpg > 17;
	}
	
	private static void buildQueryWithCustomFilter(FastR fastR) {

		Dplyr dplyr = new Dplyr(fastR);
		dplyr.stringsAsFactors(false);
        
		DplyrQueryBuilder allowedMPGBuilder = dplyr.newQueryBuilder("mtcars");
		allowedMPGBuilder.
        	<Double>assignFilter("myFilter", (mpg) -> isAllowedMPG(mpg + 2.2));

		allowedMPGBuilder.filter("myFilter(mpg)").summarise("total = n()");
        
        Value dplyrQuery0 = allowedMPGBuilder.build();
		Value res = dplyrQuery0.execute();

		System.out.println(res);
	}

	private static void buildQueryWithCustomAggregator(FastR fastR) {
		Dplyr dplyr = new Dplyr(fastR);
        DplyrQueryBuilder dplyrBuilder = dplyr.newQueryBuilder();
        dplyrBuilder.select("id", "name", "language").
        	assignAggregator("myAggr", myAggr2).
        	group_by("language").
        	summarise("myaggr = myAggr(name)");
        
        Value dplyrQuery0 = dplyrBuilder.build();

        User[] data = getUsers();
        Value df = fastR.asDataFrame(new UsersTable(data));
		Value res = dplyrQuery0.execute(df);

        for (List<Value> row : asRows(res)) {
            System.out.println(row);
        }
	}

	private static void buildSimpleQuery(FastR fastR) {
		
		Dplyr dplyr = new Dplyr(fastR);
		
		Value mtcarsCylQ = dplyr.newQueryBuilder("mtcars").
        		group_by("cyl").
        		summarise("total = n()").
        		build();
        
		Value mtcarsCyl = mtcarsCylQ.execute();
        
        System.out.println(mtcarsCyl);
	}

	private static void parseSimpleParamQueryFromBundle(FastR fastR) {
		
		RBundle bundle = new RBundle(fastR, new File("src/main/resources/sampleBundle.R"));
		Value mtcarsCylQ = bundle.buildQuery("mtcarsCylQ");
		Value mtcarsCyl = mtcarsCylQ.execute(18.5);
				
		System.out.println("cyl\ttotal");
        for (List<Value> row : asRows(mtcarsCyl)) {
            System.out.println(row.get(0).asInt() + "\t" + row.get(1).asInt());
        }
        
	}

	private static void parseSimpleParamQuery(FastR fastR) {
		
		fastR.require("dplyr");
		
		Value mtcarsCylQ = fastR.
				asFunction("mtcars %>% filter(mpg > _mpg_) %>% group_by(cyl) %>% summarise(total = n())");
		Value mtcarsCyl = mtcarsCylQ.execute(18.5);
				
		System.out.println("cyl\ttotal");
        for (List<Value> row : asRows(mtcarsCyl)) {
            System.out.println(row.get(0).asInt() + "\t" + row.get(1).asInt());
        }
        
	}

	private static void parseSimpleQuery(FastR fastR) {
		
		fastR.require("dplyr");
		
		Value mtcarsCyl = fastR.eval("mtcars %>% group_by(cyl) %>% summarise(total = n())");

        System.out.println("cyl\ttotal");
        for (List<Value> row : asRows(mtcarsCyl)) {
            System.out.println(row.get(0).asInt() + "\t" + row.get(1).asInt());
        }
        
	}

	private static void usingPackages() {
		FastR fastR = new FastR();
		
		fastR.require("jsonlite");

		Value rndJson = fastR.execute("toJSON", fastR.execute("runif", 10));
		System.out.println(rndJson.asString());

		Value intArrayJson = fastR.asFunction("toJSON(as.integer(_x_))").
				execute(new int[] {1, 2, 3});
		System.out.println(intArrayJson.asString());

		Value userJson = fastR.asFunction("toJSON(as.list(_x_))").
				execute(new User(100, "Pepa", "R"));
		System.out.println(userJson.asString());
		
    	fastR.installPackage("jsonlite");

    	if (!fastR.require("jsonlite")) {
        	fastR.installPackage("jsonlite");
        }

	}
	
	private static void usingJavaObjects() {
		@SuppressWarnings("resource")

		FastR fastR = new FastR();
		
		fastR.execute("print", new User(100, "Pepa", "R"));

		fastR.execute("(function (user) print(user$name))", 
				new User(100, "Pepa", "R"));

		Value displ = fastR.execute("(function (user) user$toDisplayName('***'))", 
				new User(100, "Pepa", "R"));
				
		displ = fastR.asFunction("_user_$toDisplayName('***')").
				execute(new User(100, "Pepa", "R"));
		
		
		System.out.println(displ);
	}

	private static void basicFastR() {
		@SuppressWarnings("resource")

		FastR fastR = new FastR();			
		Value rnd = fastR.execute("runif", 12);
        Value mat = fastR.call("matrix", rnd, named("nrow", 3), named("ncol", 4));
		fastR.execute("print", mat);
		System.out.println(mat);

	}
	
	public interface MyStruct { int id(); String text(); List<Integer> arr(); }
	
	private static void polyglotUsage2() {

		Context context = Context.newBuilder().allowAllAccess(true).build();
		Value result = context.eval("R", "list(" +
		                    "id     = 42, "      +
		                    "text   = '42', "    +
		                    "arr    = c(1,42,3)" +
		                ")");
		assert result.hasMembers();

		int id = result.getMember("id").asInt();
		assert id == 42;

		String text = result.getMember("text").asString();
		assert text.equals("42");

		Value array = result.getMember("arr");
		assert array.hasArrayElements();
		assert array.getArraySize() == 3;
		assert array.getArrayElement(1).asInt() == 42;

		MyStruct myStruct = result.as(MyStruct.class);
		assert myStruct.arr().size() == 3;
	}
	
	private static void polyglotUsage() {
		
		Context polyglot = Context.newBuilder("R").
				allowAllAccess(true).
				build();
		
		Value rnd = polyglot.eval("R", "runif(10)");
		for (int i = 0; i < rnd.getArraySize(); i++) {
			System.out.println(rnd.getArrayElement(i).asDouble());
		}
		
	}
	
    private static User[] getUsers() {
        return new User[] {
                new User(1, "Florian", "Python"),
                new User(2, "Lukas", "R"),
                new User(3, "Mila", "Java"),
                new User(4, "Paley", "Coq"),
                new User(5, "Stepan", "C#"),
                new User(6, "Tomas", "Java"),
                new User(7, "Zbynek", "Scala"),
        };
    }

    public static class MyClass {
        public int               id    = 42;
        public String            text  = "42";
        public int[]             arr   = new int[]{1, 42, 3};
        public Callable<Integer> ret42 = () -> 42;
    }

    public static void main2(String[] args) {
        Context context = Context.newBuilder().allowAllAccess(true).build();
        context.getPolyglotBindings().putMember("javaObj", new MyClass());
        boolean valid = context.eval("R",
               "javaObj <- import('javaObj');"  +
               "    javaObj$id         == 42"   +
               " && javaObj$text       == '42'" +
               " && javaObj$arr[[2]]   == 42"   +
               " && javaObj$ret42()    == 42")
           .asBoolean();
        assert valid == true;
    }

}
