package ens.fr.univ_montpellier.iut.sudoku;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import static org.chocosolver.solver.search.strategy.Search.minDomLBSearch;
import static org.chocosolver.util.tools.ArrayUtils.append;


public class SudokuDomain {

    Data data = Data.grid9;

    private final int n = data.grid.length;
    private final int s = (int) Math.sqrt(n);
    final static int A = 10, B = 11, C = 12, D = 13, E = 14, F = 15, G = 16;    

    IntVar[][] rows, cols, squares;

	private Model model;


    public void buildModel() {
		model = new Model();

		rows = new IntVar[n][n];
		cols = new IntVar[n][n];
		squares = new IntVar[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
		        if (data.grid(i, j) > 0) {
                    rows[i][j] = model.intVar("c_" + i + "_" + j, data.grid(i, j));
                } else {
                    rows[i][j] = model.intVar("c_" + i + "_" + j, 1, n, false);
                }
				cols[j][i] = rows[i][j];
			}
		}

		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				for (int k = 0; k < s; k++) {
					for (int l = 0; l < s; l++) {
						squares[j + k * s][i + (l * s)] = rows[l + k * s][i + j * s];
					}
				}
			}
		}

		for (int i = 0; i < n; i++) {
			System.out.println(i);
			model.allDifferent(rows[i]).post();
			model.allDifferent(cols[i]).post();
			model.allDifferent(squares[i]).post();

		}

	}

    
    public void configureSearch() {
        model.getSolver().setSearch(minDomLBSearch(append(rows)));

    }

    
    public void solve() {
    	this.buildModel();
        model.getSolver().showStatistics();
        model.getSolver().solve();

        StringBuilder st = new StringBuilder(String.format("Sudoku -- %s\n", data.name()));
        st.append("\t");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                st.append(rows[i][j]).append("\t\t\t");
            }
            st.append("\n\t");
        }

        System.out.println(st.toString());
    }

    public static void main(String[] args) {
        new SudokuDomain().solve();
    }

    /////////////////////////////////// DATA //////////////////////////////////////////////////
    enum Data {
        grid9(
                new int[][]{
                        {8, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 3, 6, 0, 0, 0, 0, 0},
                        {0, 7, 0, 0, 9, 0, 2, 0, 0},
                        {0, 5, 0, 0, 0, 7, 0, 0, 0},
                        {0, 0, 0, 0, 4, 5, 7, 0, 0},
                        {0, 0, 0, 1, 0, 0, 0, 3, 0},
                        {0, 0, 1, 0, 0, 0, 0, 6, 8},
                        {0, 0, 8, 5, 0, 0, 0, 1, 0},
                        {0, 9, 0, 0, 0, 0, 4, 0, 0}
                }
        ),
        grid16(
                new int[][]{
                        {0, G, 0, 0, F, 8, 9, 6, 4, B, D, 5, 0, 0, 3, 0},
                        {6, C, 0, 0, 0, 0, 4, E, 2, 7, 0, 0, 0, 0, 5, 9},
                        {0, 0, 0, D, 0, 0, G, 7, F, E, 0, 0, 6, 0, 0, 0},
                        {0, 0, 4, 3, A, 0, 0, 0, 0, 0, 0, 6, 1, B, 0, 0},
                        {7, 0, 0, 5, 8, F, 0, 0, 0, 0, B, E, 9, 0, 0, G},
                        {8, 0, 0, 0, 9, 0, 0, 4, D, 0, 0, 3, 0, 0, 0, 2},
                        {C, 1, 3, 0, 0, 0, 6, 0, 0, G, 0, 0, 0, F, 4, 5},
                        {9, D, B, 0, 0, G, 0, 0, 0, 0, F, 0, 0, 7, A, 6},
                        {G, B, A, 0, 0, 2, 0, 0, 0, 0, 7, 0, 0, 5, 6, D},
                        {5, 6, F, 0, 0, 0, A, 0, 0, 2, 0, 0, 0, 8, 7, 4},
                        {D, 0, 0, 0, 6, 0, 0, 9, 5, 0, 0, G, 0, 0, 0, F},
                        {3, 0, 0, C, B, 5, 0, 0, 0, 0, A, 4, G, 0, 0, 1},
                        {0, 0, 9, 6, G, 0, 0, 0, 0, 0, 0, 7, 2, C, 0, 0},
                        {0, 0, 0, G, 0, 0, B, D, C, 5, 0, 0, F, 0, 0, 0},
                        {4, 3, 0, 0, 0, 0, 8, 2, G, F, 0, 0, 0, 0, 1, 7},
                        {0, 8, 0, 0, 5, 9, E, A, 1, 3, 2, D, 0, 0, G, 0}
                        }
        ),;
    	
        final int[][] grid;

        Data(int[][] grid) {
            this.grid = grid;
        }

        int grid(int i, int j) {
            return grid[i][j];
        }
    }
}
