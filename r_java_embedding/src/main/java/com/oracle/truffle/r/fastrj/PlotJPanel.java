package com.oracle.truffle.r.fastrj;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public abstract class PlotJPanel extends JPanel {
	private static final long serialVersionUID = -7296367853334296393L;

    private Context context = null;
    private Value showPlot = null;
    private Value plotFunction = null;
    
    public PlotJPanel() {
    }

    public static PlotJPanel createSimplePlotPanel(FastR fastR, String plotFunctionSource, Supplier<Object> data) {
    	return createSimplePlotPanel(fastR.getContext(), fastR.asFunction(plotFunctionSource), data);
    }
    
    public static PlotJPanel createSimplePlotPanel(Context context, Value plotFunction, Supplier<Object> data) {
    	return new PlotJPanel() {
			private static final long serialVersionUID = -72418092687364509L;

			@Override
			protected Context getContext() {
				return context;
			}

			@Override
			protected Object getPlotParameters() {
				return data.get();
			}
			
			@Override
			protected Value getPlotFunction(Context context) {
				return plotFunction;
			}
		};
    }
    
	public static final String PLOT_REGRESSION = "function (fit) {\n" +
	    	"ggplot(fit$model, aes_string(x = names(fit$model)[2], y = names(fit$model)[1])) + \n" + 
	    	"  geom_point() +\n" + 
	    	"  stat_smooth(method = \"lm\", col = \"red\") +\n" + 
	    	"  labs(title = paste(\"Adj R2 = \",signif(summary(fit)$adj.r.squared, 5),\n" + 
	    	"                     \"Intercept =\",signif(fit$coef[[1]],5 ),\n" + 
	    	"                     \" Slope =\",signif(fit$coef[[2]], 5),\n" + 
	    	"                     \" P =\",signif(summary(fit)$coef[2,4], 5)))\n" + 
	    	"}\n";
			
    public static PlotJPanel createRegressionPlotPanel(Context context, Supplier<Object> data) {
    	return createSimplePlotPanel(context, context.eval("R", PLOT_REGRESSION), data);
    }
    
    protected Context getContext() {
    	return Context.newBuilder("R").allowAllAccess(true).build();
    }
    
    protected abstract Value getPlotFunction(Context context);

    protected abstract Object getPlotParameters();
    
    public BufferedImage paintHeadless() {
        // Instead of drawing to a component, we use BufferedImage for headless testing
        // Note: one can achieve this with "png" built-in directly in R,
        // this is only example of what can be done
    	int w = getWidth();
        int h = getHeight();
        BufferedImage image = new BufferedImage(w, h, TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setBackground(new Color(255, 255, 255));
        graphics.clearRect(0, 0, w, h);
    	
    	plot(graphics);
    	
    	return image;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        plot(g);
    }

	private void plot(Graphics g) {
		if (context == null) {
            initMainPlotFunction();
        }
        // The MAGIC happens HERE: we invoke R plotting code and pass it the graphics object
        showPlot.execute((Graphics2D) g, getWidth(), getHeight(), plotFunction, getPlotParameters());
	}

	private Value initMainPlotFunction() {
		context = getContext();
		plotFunction = getPlotFunction(context);
		// This R function opens FastR graphics device passing it Graphics2D object,
		// then it plots the graph and closes the device
		String src = "library(grid); library(lattice); " +
		    "function(g, w, h, plotFn, plotParams) { " +
		    "   grDevices:::awt(w, h, g);" +
		    "   print(plotFn(plotParams));" +
		    "   dev.off();" +
		    "   NULL;" +
		    "}";
		showPlot = context.eval("R", src);
		return showPlot;
	}

    public void showFrame(String title, int width, int height) {
    	JFrame frame = new JFrame(title);
    	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	frame.setPreferredSize(new Dimension(width, height));

    	Container pane = frame.getContentPane();
    	pane.add(this, BorderLayout.CENTER);

    	frame.pack();
    	frame.setVisible(true);
    } 

}
