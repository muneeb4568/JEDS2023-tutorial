
package ens.fr.univ_montpellier.iut.sudoku;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import static org.chocosolver.solver.search.strategy.Search.minDomLBSearch;
import static org.chocosolver.util.tools.ArrayUtils.append;

public class GTSudoku {

	static int n;
	static int s;
	private static int instance;
	private static long timeout = 3600000; // one hour

	IntVar[][] rows, cols, shapes;

	Model model;

	public static void main(String[] args) throws ParseException {

		final Options options = configParameters();
		final CommandLineParser parser = new DefaultParser();
		final CommandLine line = parser.parse(options, args);

		boolean helpMode = line.hasOption("help"); // args.length == 0
		if (helpMode) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("sudoku", options, true);
			System.exit(0);
		}
		instance = 9;
		// Check arguments and options
		for (Option opt : line.getOptions()) {
			checkOption(line, opt.getLongOpt());
		}

		n = instance;
		s = (int) Math.sqrt(n);

		new GTSudoku().solve();
	}

	public void solve() {

		buildModel();
		model.getSolver().showStatistics();
		model.getSolver().solve();
		
		StringBuilder st = new StringBuilder(String.format("Sudoku -- %s\n", instance, " X ", instance));
	
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				st.append(rows[i][j]).append(" ");
			}
			st.append("\n");
		}

		System.out.println(st.toString());
	}

	public void buildModel() {
		model = new Model();

		rows = new IntVar[n][n];
		cols = new IntVar[n][n];
		shapes = new IntVar[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				rows[i][j] = model.intVar("c_" + i + "_" + j, 1, n, false);
				cols[j][i] = rows[i][j];
			}
		}

		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				for (int k = 0; k < s; k++) {
					for (int l = 0; l < s; l++) {
						shapes[j + k * s][i + (l * s)] = rows[l + k * s][i + j * s];
					}
				}
			}
		}

		for (

				int i = 0; i < n; i++) {
			System.out.println(i);
			model.allDifferent(rows[i], "AC").post();
			model.allDifferent(cols[i], "AC").post();
			model.allDifferent(shapes[i], "AC").post();
		}
		
		// --------------------------------------
		// TODO: add constraints here

		String[][] tabtmp = {{"<0",">0","00","<0","<0","00",">0","<0","00"},
							{"<>","<>","0>","<<","<<","0>","<<","<<","0<"},
							{"><","<<","0<",">>",">>","0>",">>","<<","0<"},
							{">0",">0","00","<0",">0","00","<0","<0","00"},
							{"<<",">>","0>",">>","><","0<",">>",">>","0<"},
							{"<>","<<","0>","<>",">>","0>","<<",">>","0>"},
							{">0","<0","00",">0",">0","00","<0","<0","00"},
							{">>",">>","0>",">>","><","0>","><","><","0<"},
							{"><","<<","0<","><","<<","0>",">>",">>","0>"}};

		for(int i = 0; i < n;i++){
			for(int j = 0; j < n;j++){
				if(tabtmp[i][j].substring(0) == "<"){
					model.arithm(rows[i][j], "<", rows[i][j+1]).post();
				} else if(tabtmp[i][j].substring(0) == ">"){
					model.arithm(rows[i][j], ">", rows[i][j+1]).post();
				}
				
				if(tabtmp[i][j].substring(1) == ">"){
					model.arithm(rows[i][j], ">", rows[i-1][j]).post();
				}
				else if(tabtmp[i][j].substring(1) == "<"){
					model.arithm(rows[i][j], "<", rows[i-1][j]).post();
				}
			}
			
		}
		
		// --------------------------------------


	}

	// Check all parameters values
	public static void checkOption(CommandLine line, String option) {

		switch (option) {
		case "inst":
			instance = Integer.parseInt(line.getOptionValue(option));
			break;
		case "timeout":
			timeout = Long.parseLong(line.getOptionValue(option));
			break;
		default: {
			System.err.println("Bad parameter: " + option);
			System.exit(2);
		}

		}

	}

	// Add options here
	private static Options configParameters() {

		final Option helpFileOption = Option.builder("h").longOpt("help").desc("Display help message").build();

		final Option instOption = Option.builder("i").longOpt("instance").hasArg(true).argName("sudoku instance")
				.desc("sudoku instance size").required(false).build();

		final Option limitOption = Option.builder("t").longOpt("timeout").hasArg(true).argName("timeout in ms")
				.desc("Set the timeout limit to the specified time").required(false).build();

		// Create the options list
		final Options options = new Options();
		options.addOption(instOption);
		options.addOption(limitOption);
		options.addOption(helpFileOption);

		return options;
	}

	public void configureSearch() {
		model.getSolver().setSearch(minDomLBSearch(append(rows)));

	}
	

}