package com.oracle.truffle.r.fastrj;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class FastRJavaUI {
	public static final class PlotParams {
		public final int clustersCount;
		public final String xVar;
		public final String yVar;

		PlotParams(int clustersCount, String xVar, String yVar) {
			this.clustersCount = clustersCount;
			this.xVar = xVar;
			this.yVar = yVar;
		}
	}

	static final class MyPlotJPanel extends PlotJPanel {
		private static final long serialVersionUID = -6022725592051006171L;

		private static final String PLOT_FN = "require(grid); require(lattice);" + "function(plotParams) { "
				+ "   print(plotParams); clustersCount <- plotParams$clustersCount; x <- plotParams$xVar; y <- plotParams$yVar;"
				+ "   iris$cluster <- factor(kmeans(iris[, c(y, x)], clustersCount)$cluster);"
				+ "   print(xyplot(as.formula(paste0(y,'~',x)), data=iris, groups=cluster, pch=20, cex=3));" + "}";
		private final Supplier<PlotParams> paramsSupplier;

		MyPlotJPanel(Supplier<PlotParams> paramsSupplier) {
			this.paramsSupplier = paramsSupplier;
		}

		@Override
		protected Value getPlotFunction(Context context) {
			return context.eval("R", PLOT_FN);
		}

		@Override
		protected Object getPlotParameters() {
			return paramsSupplier.get();
		}

	}

	// Create and set up the window -- this is standard Java/Swing
	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Hello World to R from Java");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(750, 500));

		JComboBox<Integer> clustersCombo = new JComboBox<>(new Integer[] { 2, 3, 4, 5, 6, 7 });
		clustersCombo.setSelectedIndex(2);
		String[] variables = { "Sepal.Length", "Sepal.Width", "Petal.Length", "Petal.Width" };
		JComboBox<String> xAxisCombo = new JComboBox<>(variables);
		JComboBox<String> yAxisCombo = new JComboBox<>(variables);
		yAxisCombo.setSelectedIndex(1);

		JPanel plot = new MyPlotJPanel(() -> new PlotParams((Integer) clustersCombo.getSelectedItem(),
				(String) xAxisCombo.getSelectedItem(), (String) yAxisCombo.getSelectedItem()));
		ActionListener updatePlot = e -> plot.repaint();
		clustersCombo.addActionListener(updatePlot);
		xAxisCombo.addActionListener(updatePlot);
		yAxisCombo.addActionListener(updatePlot);

		JPanel options = new JPanel();
		options.setLayout(new FlowLayout(FlowLayout.CENTER));
		options.add(new JLabel("X Variable: "));
		options.add(xAxisCombo);
		options.add(new JLabel("Y Variable: "));
		options.add(yAxisCombo);
		options.add(new JLabel("Clusters count: "));
		options.add(clustersCombo);
		xAxisCombo.setBorder(new EmptyBorder(0, 0, 0, 40));
		yAxisCombo.setBorder(new EmptyBorder(0, 0, 0, 40));
		clustersCombo.setBorder(new EmptyBorder(0, 0, 0, 40));

		frame.setLayout(new BorderLayout(20, 20));
		Container pane = frame.getContentPane();
		pane.add(options, BorderLayout.PAGE_START);
		pane.add(plot, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
	}
}