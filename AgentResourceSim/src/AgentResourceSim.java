// ** Imports **************************************************************************************
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

// ** Class AgentResourceSim ***********************************************************************
public class AgentResourceSim {
	
	double[] testTopographyArray = { 4 };//0, 1, 2, 3, 4, 5, 6, 7, 8 };//, 1, 2, 3, 4, 5, 6, 7 };

	Scanner keyboard = new Scanner(System.in); // use for user input
	// use 100, 100, 250, 0, 1.3, 0, false, 1, 0, 0, 5, 1, 1, X, {0,6,11}, {0, 4, 4} for testing
	int topographyOverride = 4;
	//				4k	12k	36k
	//	bal			0	1	2
	//	unbal		3	4	5
	//	2D local	6	7	8
	// 0 = 2.5KedgesBalanced, 1 = 5KedgesBalanced, 2 = 10KedgesBalanced, 3 = 20KedgesBalanced, 4 = 40KedgesBalanced
	// 5 = 2.5KedgesBalanced, 6 = 5KedgesUnbalanced, 7 = 10KedgesUnbalanced, 8 = 20KedgesUnbalanced, 9 = 40KedgesUnbalanced
	
	
	double startingNetworkFI = 0.5;
	int numrows = 4;					// set number of rows of agents
	int numcols = 4;					// set number of columns of agents
	int N = 28;						// set number of resources
	int numEdges = 0;//2500;					// set to zero if do not want to use numEdges
	int numAgentsFlops = 0;//(int) (numEdges * 3.2);
	int numResourcesFlops = 0;
	int CEILING = 0;
	boolean ceilingLimit = false;
	double CONNECTIVITY_RANGE = 1.28;//1.28;	// set agent-resource connectivity range
	double DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
	double PERIMETER = 0;				// <=0 for agent boarder, >0 for resource boarder
	double TOP_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
	double BOTTOM_PERIMETER = PERIMETER;// <=0 for agent boarder, >0 for resource boarder
	double LEFT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
	double RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
	boolean SQUARE_RANGE = false;		// set to false if want circular range
	double densityVariance = 0;			// set to 0 if determinate resource quota
	double agentBalanceFactor = 0;		// set to 0 or infinity for fair connection distribution
	int locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
	int assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
	double RADIUS = 0.65;				// radius of de Moivre's circle (only applies if locationAlgorithm==4)
	double pGeometric = 1;				// probability of successfully stop creating more resources (only applies if locationAlgorithm==5)
	boolean shuffle = true;				// if shuffle is off, then balancing algorithms will always look at agent 0 first (good for minMax, bad for offerAccept)
	String visualsOutputLocation = "C:/AgentResourceSimVisuals/";
	int[] algorithmBidWhoOrderAndExecution = { 7, 5 }; // use 0,5 for default; use 7,5 for any resource lower
	// 0 is based on price; 1 is based on number of connections to resource; 2 is number of connections to resource's assigned agent
	// 3 is average price of nearby resources of the resource's assigned agent; 4 is total price of nearby resources of the resource's assigned agent;
	// 5 is random; 6 is scramble; 7 is any lower price at random; 10 is the ranking of winning; 11 is the probability of winning
	int[] algorithmWhoWinsOrderAndExecution = { 5, 4 }; // use 0,4 for default, use 5, 4 for any higher bidder
	// 0 is based on highest bid; 1 is based on lower number of connections; 2 is based on agent with more neighboring agents;
	// 3 is overtime; 4 is random; 5 is random as long as it is not the original bidder
	String[] algorithms = {"Min-Max", "1Offer-1Accept", "1Apply-1Give", "MultipleOffer-MultipleAccept", "Bidding", "RichDonateToPoor", "PoorRobFromRich", "Centralized-Algorithm", "Worst"};

	
	//String[] algorithms = {"1o1a", "1oMa", "Mo1aLar", "Mo1aRic", "MoMaRes", "1a1g", "1aMg", "Ma1g", "MaMgRes", "DonateF", "RobF", "BCIP", "worst"};
//	String[] algorithms = {"ip2500", "ip1875", "MOA", "RmOmA", "AmOmA", "TT", "DF1101", "RF1110", "BCIP", "worst"};
//	String[] algorithms = {"RmOmAAv", "RmOmALo", "RmOmAHi", "AmOmAAv", "AmOmALo", "AmOmAHi", "BCIP", "worst"};
//	String[] algorithms = {"OA", "anyLowr", "SM_2", "SM_inf", "OmA", "mOA", "mOmA", "BCIP", "worst"};
//	String[] algorithms = {"dfAvg", "df25p", "df50p", "df75p", "BCIP", "worst"};
//	String[] algorithms = {"rf1000", "rf1001", "rf1010", "rf1011", "rf1100", "rf1101", "rf1110", "rf1111", "BCIP", "worst"};
//	String[] algorithms = {"OfAnyLw", "SM_2R", "ofMAcpt", "ofAcptM", "BidHigh", "BidMid", "Rob1110", "Police", "DFavgR", "ipws1", "ipws2", "ipws8", "ipws32", "ipws128", "BNIP", "BCIP", "worst"};
//{ "mM_WS", "OA_WS", "SM_2R", "BidHigh", "BidMid", "RobHghF", "RobMidF", "Rob000", "Rob001", "Rob010", "Rob011", "Rob100", "Rob101", "Rob110", "Rob111", "TkeTrn", "RobDnte", "Police", "DFpoor", "DFavg", "DFpoorR", "DFavgR", "BNIP", "BCIP", "worst"};

	// data structures
	int[] agentsWeight;
	private Resource[] resources;		// used to keep track of resources
	private Agent[][] agents;			// used to keep track of agents
	private Stack<Integer> deadPool;// = new Stack<Integer>(); // used to keep track of dead resources
	int[][] connectionMatrix;			// [numrows*numcols][maxID+1] used to store connection matrix data
	int[][] assignmentMatrix;			// [numrows*numcols][maxID+1] used to store assignment matrix data
	int[] assignmentVector;				// * [maxID+1] used to store the assignment of each resource for tiebreakers
	int[] numberOfConnectedAgentsArray; // * [maxID+1] used to store the number of connections of each resource for DOTLangauge and tiebreakers
	// int[] resourcePrice; // * [maxID+1] used to store the current price to beat before placing a bid
	int[] totalPriceOfAllResources;		// * [numrows*numcols] used to store the price of all resources connected to agent for tiebreakers
	int[][] bidMatrix;					// [numrows*numcols][maxID+1] used to store the bid from each agent on each resource
	int[] agentsBiddingPrices;			// [numrows*numcols] used to store the bid that the agent is placing
	int[][] highestBidArray;			// [maxID+1][2] used to store the value of the highest bid and the bidder
	// double[][] assignmentMatrixV2; // used for continuous resource transfer
	int[][] differenceMatrix;			// used to store xor matrix data and compute path matrix
	int[][] pathMatrix;					// used to store path matrix data (advanced reach matrix)
	int[][] reachMatrix;				// used to store reach matrix and compute improvement paths
	int[][] adjacencyMatrix;			// used to store agent adjacency for small world computations
	int[][] agentPathList;				// used for improvement path
	ArrayList<int[][]> reachMatrixList; // = new ArrayList<Integer[][]>();
	ArrayList<int[][]> agentPathMatrixList;// = new ArrayList<Integer[][]>();
	// ArrayList<Integer> resourceMarker; // used to not move resource twice
	ArrayList<double[][]> testResults = new ArrayList<double[][]>();
	double[][] testResultsElement;// = new double[1][3]; = {algorithmStep, numberOfExecutions, fairnessIndex}
	Random randomNumberGenerator = new Random(1);	// Random number generator
	Random gaussianNumberGenerator = new Random();	// used for Guassian to not interefere with bidding tiebreakers
	Random disturbanceNumberGenerator = new Random(1); // used for disturbance
	double[][] resourceLocationBeforeDisturbance;
	double[][] resourceLocationDuringDisturbance;
	double[] cutoffs;
	int numberOfPhases = 0;
	int numberOfCommunications = 0;
	int biddingAggressiveness = 0;
	double standardDeviation = 0;
	int cascadedDecentralizedIsDone = 0;
	String algorithm = "";
	int results = 0;
	int numberOfDifferences = 0;
	int numberOfBidPeakDeclinations = 0;
	String[] colors = { "006837", "1a9850", "66bd63", "a6d96a", "d9ef8b", "ffffbf", "fee08b", "fdae61", "f46d43", "d73027", "a50026" };
	boolean isAgentsTurnOrderSet = false;
	int[] agentsTurnOrder = new int[0];
	ArrayList<String> agentsTakeTurnsColor = new ArrayList<String>();
	int takeTurnPhases = 0;
	int takeTurnPhaseCounter = 0;
	int lastTakeTurnPhaseWithModification = -1;
	double[][] bidMatrixD;					// [numrows*numcols][maxID+1] used to store the bid from each agent on each resource
	double[] resourcePrice;
	double[] agentWealth;
	boolean resourcePriceIsSet = false;
	int numberOfCalls = 0;
	int numberOfOffers = 0;
	int numberOfApplies = 0;
	double globalMaxNumberOfConnections = 0;
	double globalMaxNumberOfAssignments = 0;
	
	// file names
	private String BipartiteOutputFileName = "BipartiteOutput";
	private String GraphOutputFileName = "GraphOutput";
	private String AgentsColorOutputFileName = "AgentsColorGraph";
	private String AgentsGrayScaleOutputFileName = "AgentsGrayScaleGraph";
	private String AgentLevelGraphOutputFileName = "AgentLevelGraphOutput";
	private String AllocationSpaceGraphOutputFileName = "AllocationSpaceGraphOutput";
	private String AssignmentSpaceGraphOutputFileName = "AssignmentSpaceGraphOutput";
	private String connectionInputFileName = "IConnectionInput.txt";
	private String assignmentInputFileName = "IAssignmentInput.txt";
	private String gridAndResourceLocationInputFileName = "IGridAndResourceLocationInput.txt";
	private String IOConnectionMatrixFileName = "IOConnectionMatrix.txt";			// used for testing disturbances
	private String IOAssignmentMatrixFileName = "IOAssignmentMatrix.txt"; 			// used for reset 2
	private String IOGridAndResourceLocationFileName = "IOGridAndResourceLocation.txt";
	private String connectionOutputFileName = "OConnectionOutput.txt";
	private String assignmentOutputFileName = "AssignmentOutput.txt";				// O + algorithmStep will be appended
	private String gridAndResourceLocationOutputFileName = "OGridAndResourceLocationOutput.txt";
	private String IOTempConnectionMatrixFileName1 = "IOTempConnectionMatrix1.txt"; // used for disturbance of random edges
	private String IOTempConnectionMatrixFileName2 = "IOTempConnectionMatrix2.txt"; // used for manual connection matrix patterns
	private String IOTempAssignmentMatrixFileName1 = "IOTempAssignmentMatrix1.txt"; // used for agent space current
	private String IOTempAssignmentMatrixFileName2 = "IOTempAssignmentMatrix2.txt"; // used for agent space temp to compare
	private String IOTempAssignmentMatrixFileName3 = "IOTempAssignmentMatrix3.txt"; // used for network min and max FI
	private String IOTempAssignmentMatrixFileName4 = "IOTempAssignmentMatrix4.txt"; // used temp for non safe algorithms such as bidPeak
	// private String debugAssignmentMatrix = "debugAssignmentMatrix.txt"; // used for network min and max FI
	private String adjacencyOutputFileName = "OAdjacencyMatrixOutput.txt";
	private String differenceMatrixOutputFileName = "DifferenceMatrixOutput.txt";
	private String reachMatrixOutputFileName = "ReachMatrixOutput.txt";
	private String pathMatrixOutputFileName = "PathMatrixOutput.txt";
	private String IOSeededConnectionFileName = "IOSeededConnectionOutput.txt";
	private String IOAgentNEdgeConnectionFileName = "IOAgentNEdgeConnectionOutput.txt";
	private String testResultsFileName = "OTestResults.txt";
	private String testFIResultsFileName = "OTestFIResults.txt";
	private String testNetworkFIResultsFileName = "OTestNetworkFIResults.txt";
	private String testExecutionsResultsFileName = "OTestExecutionsResults.txt";
	private String testIterationsResults0950FileName = "OTestIterationsResults0950.txt";
	private String testIterationsResults0975FileName = "OTestIterationsResults0975.txt";
	private String testIterationsResults1000FileName = "OTestIterationsResults1000.txt";
	private String testPhasesResults0950FileName = "OTestPhasesResults0950.txt";
	private String testPhasesResults0975FileName = "OTestPhasesResults0975.txt";
	private String testPhasesResults1000FileName = "OTestPhasesResults1000.txt";
	private String testPhasesResultsFileName = "OTestPhasesResults.txt";
//	private String testTotalPhasesResultsFileName = "OTestTotalPhasesResults.txt";
	private String testCommunicationsResultsFileName = "OTestCommunicationsResults.txt";
	private String testCommunicationsResults0950FileName = "OTestCommunicationsResults0950.txt";
	private String testCommunicationsResults0975FileName = "OTestCommunicationsResults0975.txt";
	private String testCommunicationsResults1000FileName = "OTestCommunicationsResults1000.txt";
//	private String testTotalCommunicationsResultsFileName = "OTestTotalCommunicationsResults.txt";
	private String testFinalFIResultsFileName = "OTestFinalFIResults.txt";
	private String testNetworkFinalFIResultsFileName = "OTestNetworkFinalFIResults.txt";
	private String testTotalExecutionsResultsFileName = "OTestTotalExecutionResults.txt";
	private String testFairnessIndexResults02FileName = "OTestFairnessIndexResults02.txt";
	private String testFairnessIndexResults06FileName = "OTestFairnessIndexResults06.txt";
	private String testFairnessIndexResults10FileName = "OTestFairnessIndexResults10.txt";
	private String testOffersResultsFileName = "OTestOffersResults.txt";
	private String testDistributionFunctionFileName = "OTestDistributionFunctions.txt";

	// global variables
	int maxID = -1;						// compute maxID for creating new resources
	boolean vocal = false;				// used to display outputs such as creating, moving, or removing resources
	boolean showAlgorithmVisual = false;// used to print algorithms visuals to help visualize algorithms
	boolean showDebug = false;			// used to print debugging information
	boolean showDebugLevel2 = true;			// used to print debugging information
	boolean printASGLabels = false;		// used to display ASG edge labels
	int DOT_LAYOUT = 2;					// 1 = actual location; 2 = neato; 3 = sfdp; 4 = used for paper;
	// 10 = updated prices; 11 = bids; 12 = update highest bid of the contested and remove original owners;
	// 13 = move units to highest bidders and update prices and clear old bids that did not win
	double DOT_SCALE = 200;				// visual distance between each agent
	int numberOfAgentsWithSameRank = printASGLabels ? 40 : 1000; // lower number will reduce the probability of GraphVIZ to crash
	int batchScale = 100;				// pick a number less than stepScale for different prefix after each batch
	int stepScale = 1000;				// adjust relative to the batch scale
	int algorithmStep = 5 * stepScale;	// used to determine file output destination
	double minScaledFI, maxScaledFI, minNetworkFI, maxNetworkFI;

	// ** Main **************************************************************************************
	public static void main(String[] args) {
		AgentResourceSim instanceOfAgentResourceSim = new AgentResourceSim();
		instanceOfAgentResourceSim.getConfigurations();
		instanceOfAgentResourceSim.simulate();
	}

	public void getConfigurations() {
		String config = "";
		try {
			Scanner inputFile = new Scanner(new File("config.txt"));
			while (inputFile.hasNextLine()) {
				config += inputFile.nextLine() + "\n";
			}
			inputFile.close();
			topographyOverride = parseInteger(config, "topographyOverride") == null ? topographyOverride : parseInteger(config, "topographyOverride");
			numrows = parseInteger(config, "numrows") == null ? numrows : parseInteger(config, "numrows");
			numcols = parseInteger(config, "numcols") == null ? numcols : parseInteger(config, "numcols");
			N = parseInteger(config, "N") == null ? N : parseInteger(config, "N");
			numEdges = parseInteger(config, "numEdges") == null ? numEdges : parseInteger(config, "numEdges");
			CONNECTIVITY_RANGE = parseDouble(config, "CONNECTIVITY_RANGE") == null ? CONNECTIVITY_RANGE : parseDouble(config, "CONNECTIVITY_RANGE");
			DENSITY = parseDouble(config, "DENSITY") == null ? DENSITY : parseDouble(config, "DENSITY");
			PERIMETER = parseDouble(config, "PERIMETER") == null ? PERIMETER : parseDouble(config, "PERIMETER");
			TOP_PERIMETER = parseDouble(config, "TOP_PERIMETER") == null ? TOP_PERIMETER : parseDouble(config, "TOP_PERIMETER");
			BOTTOM_PERIMETER = parseDouble(config, "BOTTOM_PERIMETER") == null ? BOTTOM_PERIMETER : parseDouble(config, "BOTTOM_PERIMETER");
			LEFT_PERIMETER = parseDouble(config, "LEFT_PERIMETER") == null ? LEFT_PERIMETER : parseDouble(config, "LEFT_PERIMETER");
			RIGHT_PERIMETER = parseDouble(config, "RIGHT_PERIMETER") == null ? RIGHT_PERIMETER : parseDouble(config, "RIGHT_PERIMETER");
			SQUARE_RANGE = parseBoolean(config, "SQUARE_RANGE") == null ? SQUARE_RANGE : parseBoolean(config, "SQUARE_RANGE");
			densityVariance = parseDouble(config, "densityVariance") == null ? densityVariance : parseDouble(config, "densityVariance");
			agentBalanceFactor = parseDouble(config, "agentBalanceFactor") == null ? agentBalanceFactor : parseDouble(config, "agentBalanceFactor");
			locationAlgorithm = parseInteger(config, "locationAlgorithm") == null ? locationAlgorithm : parseInteger(config, "locationAlgorithm");
			assignmentAlgorithm = parseInteger(config, "assignmentAlgorithm") == null ? assignmentAlgorithm : parseInteger(config, "assignmentAlgorithm");
			RADIUS = parseDouble(config, "RADIUS") == null ? RADIUS : parseDouble(config, "RADIUS");
			pGeometric = parseDouble(config, "pGeometric") == null ? pGeometric : parseDouble(config, "pGeometric");
			shuffle = parseBoolean(config, "shuffle") == null ? shuffle : parseBoolean(config, "shuffle");
			visualsOutputLocation = parseString(config, "visualsOutputLocation") == null? visualsOutputLocation : parseString(config, "visualsOutputLocation");
			algorithmBidWhoOrderAndExecution = parseIntegerArray(config, "algorithmBidWhoOrderAndExecution") == null ? algorithmBidWhoOrderAndExecution : parseIntegerArray(config, "algorithmBidWhoOrderAndExecution");
			algorithmWhoWinsOrderAndExecution = parseIntegerArray(config, "algorithmWhoWinsOrderAndExecution") == null ? algorithmBidWhoOrderAndExecution : parseIntegerArray(config, "algorithmWhoWinsOrderAndExecution");
			toString(testTopographyArray);
			testTopographyArray = parseDoubleArray(config, "testTopographyArray") == null ? testTopographyArray : parseDoubleArray(config, "testTopographyArray");
			toString(testTopographyArray);
			vocal = parseBoolean(config, "vocal") == null? vocal : parseBoolean(config, "vocal");
			showAlgorithmVisual = parseBoolean(config, "showAlgorithmVisual") == null? showAlgorithmVisual : parseBoolean(config, "showAlgorithmVisual");
			showDebug = parseBoolean(config, "showDebug") == null? showDebug : parseBoolean(config, "showDebug");
			printASGLabels = parseBoolean(config, "printASGLabels") == null? printASGLabels : parseBoolean(config, "printASGLabels");
			DOT_LAYOUT = parseInteger(config, "DOT_LAYOUT") == null ? DOT_LAYOUT : parseInteger(config, "DOT_LAYOUT");
			DOT_SCALE = parseInteger(config, "DOT_SCALE") == null ? DOT_SCALE : parseInteger(config, "DOT_SCALE");
			batchScale = parseInteger(config, "batchScale") == null ? batchScale : parseInteger(config, "batchScale");
			stepScale = parseInteger(config, "stepScale") == null ? stepScale : parseInteger(config, "stepScale");
		} catch (IOException e) {
			System.out.println("\tno config file found. config: '"+config+"'");
			return;
			// e.printStackTrace();
		}
		System.out.println("\tconfig file was used\n"+config);
	}
	
	private Integer parseInteger(String config, String variable) {
		if (config.indexOf("int "+variable+" =") > 0) {
			return Integer.parseInt(config.substring(config.indexOf("int "+variable+" =") + ("int  "+variable+" =").length(), config.indexOf(";", config.indexOf("int "+variable+" ="))).trim());
		}
		if (config.indexOf("Integer "+variable+" =") > 0) {
			return Integer.parseInt(config.substring(config.indexOf("Integer "+variable+" =") + ("Integer  "+variable+" =").length(), config.indexOf(";", config.indexOf("Integer "+variable+" ="))).trim());
		}
		return null;
	}
	
	private Double parseDouble(String config, String variable) {
		if (config.indexOf("double "+variable+" =") > 0) {
			return Double.parseDouble(config.substring(config.indexOf("double "+variable+" =") + ("double "+variable+" =").length(), config.indexOf(";", config.indexOf("double "+variable+" ="))).trim());
		}
		if (config.indexOf("Double "+variable+" =") > 0) {
			return Double.parseDouble(config.substring(config.indexOf("Double "+variable+" =") + ("Double "+variable+" =").length(), config.indexOf(";", config.indexOf("Double "+variable+" ="))).trim());
		}
		return null;
	}
	
	private Boolean parseBoolean(String config, String variable) {
		if (config.indexOf("boolean "+variable+" =") > 0) {
			return Boolean.parseBoolean(config.substring(config.indexOf("boolean "+variable+" =") + ("boolean "+variable+" =").length(), config.indexOf(";", config.indexOf("boolean "+variable+" ="))).trim());
		}
		if (config.indexOf("Boolean "+variable+" =") > 0) {
			return Boolean.parseBoolean(config.substring(config.indexOf("Boolean "+variable+" =") + ("Boolean "+variable+" =").length(), config.indexOf(";", config.indexOf("Boolean "+variable+" ="))).trim());
		}
		return null;
	}
	
	private String parseString(String config, String variable) {
		if (config.indexOf("String "+variable+" =") > 0) {
			String returnValue = config.substring(config.indexOf("String "+variable+" =") + ("String "+variable+" =").length(), config.indexOf(";", config.indexOf("String "+variable+" ="))).trim();
			if (returnValue.length() > 1 && returnValue.charAt(0) == '"' && returnValue.charAt(returnValue.length() - 1) == '"') {
				return returnValue.substring(1, returnValue.length() - 1);
			} else {
				return returnValue;
			}
		}
		return null;
	}
	
	private int[] parseIntegerArray(String config, String variable) {
		boolean intArray = config.contains("int[] "+variable+" =");
		boolean IntegerAray = config.contains("Integer[] "+variable+" =");
		if (intArray || IntegerAray) {
			String textValue = intArray ? config.substring(config.indexOf("int[] "+variable+" =")+("int[] "+variable+" =").length(), config.indexOf(";", config.indexOf("int[] "+variable+" ="))).trim() : config.substring(config.indexOf("Integer[] "+variable+" =")+("Integer"+variable+" =").length(), config.indexOf(";", config.indexOf("Integer[] "+variable+" ="))).trim();
			if (textValue.length() > 1 && textValue.charAt(0) == '{' && textValue.charAt(textValue.length() - 1) == '}') {
				textValue = textValue.substring(1, textValue.length() - 1);
			}
			String[] returnValueString = textValue.split(",");
			ArrayList<Integer> returnValueArrayList = new ArrayList<Integer>();
			for (int i = 0; i < returnValueString.length; i++) {
				if (returnValueString[i].trim().length() > 0) {
					returnValueArrayList.add(Integer.parseInt(returnValueString[i].trim()));
				}
			}	// convert ArrayList<Integer> to int[]
			int[] returnValue = new int[returnValueArrayList.size()];
			for (int i = 0; i < returnValueArrayList.size(); i++) {
				returnValue[i] = returnValueArrayList.get(i);
			}
			return returnValue;
		}
		return null;
	}
	
	private double[] parseDoubleArray(String config, String variable) {
		boolean doubleArray = config.contains("double[] "+variable+" =");
		boolean DoubleAray = config.contains("Double[] "+variable+" =");
		if (doubleArray || DoubleAray) {
			String textValue = doubleArray ? config.substring(config.indexOf("double[] "+variable+" =")+("double[] "+variable+" =").length(), config.indexOf(";", config.indexOf("double[] "+variable+" ="))).trim() : config.substring(config.indexOf("Double[] "+variable+" =")+("Double"+variable+" =").length(), config.indexOf(";", config.indexOf("Double[] "+variable+" ="))).trim();
			if (textValue.length() > 1 && textValue.charAt(0) == '{' && textValue.charAt(textValue.length() - 1) == '}') {
				textValue = textValue.substring(1, textValue.length() - 1);
			}
			String[] returnValueString = textValue.split(",");
			ArrayList<Double> returnValueArrayList = new ArrayList<Double>();
			for (int i = 0; i < returnValueString.length; i++) {
				if (returnValueString[i].trim().length() > 0) {
					returnValueArrayList.add(Double.parseDouble(returnValueString[i].trim()));
				}
			}	// convert ArrayList<Integer> to int[]
			double[] returnValue = new double[returnValueArrayList.size()];
			for (int i = 0; i < returnValueArrayList.size(); i++) {
				returnValue[i] = returnValueArrayList.get(i);
			}
			return returnValue;
		}
		return null;
	}

	private void testEclipseGraphVIZ() {
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		gv.addln("A -> B;");
		gv.addln("A -> C;");
		gv.addln(gv.end_graph());
		System.out.println(gv.getDotSource());

		String type = "gif";
		// String type = "png";
		File out = new File("C:/out." + type);    // Windows
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, "neato"), out);
	}

	void test() {
		int numberOfBands = 4;
		int startingFill = -20;
		int endingFill = numEdges / (numrows * numcols) - 20;
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < N; j++) {
				if ((j >= startingFill - 4000 && j < endingFill - 4000) || (j >= startingFill - 3000 && j < endingFill - 3000)
						|| (j >= startingFill - 2000 && j < endingFill - 2000) || (j >= startingFill - 1000 && j < endingFill - 1000)
						|| (j >= startingFill && j < endingFill) || (j >= startingFill + 1000 && j < endingFill + 1000))
					System.out.print("1");
				else {
					System.out.print("0");
				}
			}
			startingFill += 10 * numberOfBands;
			endingFill += 10 * numberOfBands;
			System.out.println();
		}
	}

	// ** Simulate Using Interactive Loop to Support Commands ***************************************
	void doCommand(String commandWord, double ID, double row, double column, boolean outOfAgents, String[] commandArray) {
		if (commandWord.equalsIgnoreCase("test")) {
			test();
		}
		if (!outOfAgents && (commandWord.equalsIgnoreCase("members") || commandWord.equalsIgnoreCase("connection") || commandWord.equalsIgnoreCase("connections")) && commandArray.length > 2) {
			// if members, then print out members connected to agent
			// if connection, then print out the number of connections to the agent
			System.out.println("\t" + agents[(int) row][(int) column].numberOfResourcesConnectedToAgent + " resources");
			System.out.print("\tresources: ");
			for (int IDtoPrint : members((int) row, (int) column))
				System.out.print(IDtoPrint + " ");
			System.out.println();
		}
		if (outOfAgents && (commandWord.equalsIgnoreCase("members") || commandWord.equalsIgnoreCase("connection") || commandWord.equalsIgnoreCase("connections")) && commandArray.length > 2)
			System.out.println("\terror: agent out of range for command i j for this specific command");
		if (!outOfAgents && (commandWord.equalsIgnoreCase("members") || commandWord.equalsIgnoreCase("connection") || commandWord.equalsIgnoreCase("connections")) && commandArray.length == 2) {
			// if members, then print out members connected to agent
			// if connection, then print out the number of connections to the agent
			System.out.println("\t" + agents[(int) ID / numcols][(int) ID % numcols].numberOfResourcesConnectedToAgent + " resources");
			System.out.print("\tresources: ");
			for (int IDtoPrint : members((int) ID / numcols, (int) ID % numcols))
				System.out.print(IDtoPrint + " ");
			System.out.println();
		}
		if (commandWord.equalsIgnoreCase("population") && commandArray.length < 3)
			// if grid population, then print out population of grid
			System.out.println("\ttotal resource population: " + N);
		if (commandWord.equalsIgnoreCase("whereIs") && commandArray.length > 1)
				whereIs((int) ID);		// if query, then return location of person
		if ((commandWord.equalsIgnoreCase("checkConnection") || commandWord.equalsIgnoreCase("checkContains")) && commandArray.length > 3)
			printCheckContains((int) row, (int) column, (int) ID);
		if (commandWord.equalsIgnoreCase("kill") && commandArray.length > 1)
			kill((int) ID, true);		// if kill, then kill
		if (commandWord.equalsIgnoreCase("create") && commandArray.length > 2)
			create(row, column, true);	// if create, then create and update
		if (commandWord.equalsIgnoreCase("create") && commandArray.length < 2)
			create(Math.random() * (numrows - 1 + TOP_PERIMETER + BOTTOM_PERIMETER) - BOTTOM_PERIMETER, Math.random() * (numcols - 1 + LEFT_PERIMETER + RIGHT_PERIMETER) - RIGHT_PERIMETER, true);
		if (commandWord.equalsIgnoreCase("move") && commandArray.length > 3)
			move((int) ID, row, column); // if move, then move person
		if ((commandWord.equalsIgnoreCase("disturbance") || commandWord.equalsIgnoreCase("disturb") || commandWord.equalsIgnoreCase("DR")) && commandArray.length > 1)
			disturbResources(ID);
		if ((commandWord.equalsIgnoreCase("agentDisturbance") || commandWord.equalsIgnoreCase("disturbAgent") || commandWord.equalsIgnoreCase("DA")) && commandArray.length > 1)
			disturbAgents(ID);
		if ((commandWord.equalsIgnoreCase("randomEdge") || commandWord.equalsIgnoreCase("smallWorld") || commandWord.equalsIgnoreCase("SW")) && commandArray.length > 1)
			smallWorld(ID);
		if (commandWord.equalsIgnoreCase("reset") || commandWord.equalsIgnoreCase("reset1") || commandWord.equalsIgnoreCase("r") || commandWord.equalsIgnoreCase("r1")) {	
			reset1(); 					// call reset method
		}
		if (commandWord.equalsIgnoreCase("reset2") || commandWord.equalsIgnoreCase("r2")) {								// call reset method
			reset2();
		}
		if (commandWord.equalsIgnoreCase("print") || commandWord.equalsIgnoreCase("printAll") || commandWord.equalsIgnoreCase("p")) {
			printDOT2DVisualization(2); // print agent resource graph in 1 = bipartite, 2 = manual location
			printAgentsGraph(2, "_" + algorithm + "_" +  algorithmStep); // print manual location graph in 1 = grayScale, 2 = color
			// too slow for testing networks // printAgentLevelGraph(1, null, null); // print agent level graph in 0 = edge label, 1 = no edge label
			// print (0) OConnection, (1) IOConnection, (2) adjacency, (3) assignment, (4) difference, (5) reach, and (6) path
			for (int i = 0; i < 6; i++)
				printMatrices(i);
			// printASG(2, -1); // print all distributions
			printGridAndResourceLocation(gridAndResourceLocationOutputFileName);
			//printStats(null);
		}
		if (commandWord.equalsIgnoreCase("printStats") || commandWord.equalsIgnoreCase("ps")) {
			println("topographyOverride = "+ topographyOverride);
			PrintWriter outputStream1 = null;
			String fileName = "April3Stats"+numEdges+"_"+(numAgentsFlops/(double)numEdges)+".txt";
			try {								// try to open the file of the computed file name
				outputStream1 = new PrintWriter(new FileOutputStream(fileName));
			} catch (FileNotFoundException e) {
				System.out.println("\terror opening the file: " + fileName);
				System.exit(0);
			}
			outputStream1.println(printPDFandCDF("April3Geni.txt", true, 0));
			//outputStream1.println(getCDFconnection());
			outputStream1.println(printPDFandCDF("April3Reg.txt", false, 0));
			printStats(outputStream1);
//			printPDFandCDF("test.txt", true, true);
			println("\twrote to file: " + fileName);
		}
		if ((commandWord.equalsIgnoreCase("controlSystemSignal") || commandWord.equalsIgnoreCase("css"))) {
			controlSystemSignalAuto();
		}
		if ((commandWord.equalsIgnoreCase("productivityAndOvershoot") || commandWord.equalsIgnoreCase("pos"))) {
			productivityAndOvershoot();
		}
		if (commandWord.equalsIgnoreCase("pALG")) {
			printAgentLevelGraph(0, null, null); // print agent level graph in 0 = edge label, 1 = no edge label
		}
		if (commandWord.equalsIgnoreCase("testEclipseGraphVIZ")) {
			testEclipseGraphVIZ();
		}
		if (commandWord.equalsIgnoreCase("printVisuals") || commandWord.equalsIgnoreCase("printV")) {
			printVisuals();
		}
		if (commandWord.equalsIgnoreCase("printASG") || commandWord.equalsIgnoreCase("pASG")) {
			printASG();
		}
		if (commandWord.equalsIgnoreCase("ideal") || commandWord.equalsIgnoreCase("iuse")) {
			printSeededConnectionMatrix(true); // call method
			useConnectionInput(IOSeededConnectionFileName); // use ideal connection matrix input
		}
		if (commandWord.equalsIgnoreCase("useIConnectionMatrix") || commandWord.equalsIgnoreCase("useConnectionInput")) {
			useConnectionInput(connectionInputFileName); // use input connection matrix
			printMatrices(10);
		}
		if (commandWord.equalsIgnoreCase("useIOConnectionMatrix") || commandWord.equalsIgnoreCase("reconnect")) {
			useConnectionInput(IOConnectionMatrixFileName); // use input output connection matrix
			printMatrices(10);
		}
		if (commandWord.equalsIgnoreCase("useOConnectionMatrix")) {
			useConnectionInput(connectionOutputFileName);
			printMatrices(10);
		}
		if (commandWord.equalsIgnoreCase("useIAssignmentMatrix") || commandWord.equalsIgnoreCase("useAssignmentInput")) {
			useAssignmentInput(assignmentInputFileName); // use input connection matrix
			printMatrices(11);
		}
		if (commandWord.equalsIgnoreCase("useIOAssignmentMatrix") || commandWord.equalsIgnoreCase("reassign")) {
			useAssignmentInput(IOAssignmentMatrixFileName); // use default assignment matrix
			printMatrices(11);
		}
		if (commandWord.equalsIgnoreCase("useAll") || commandWord.equalsIgnoreCase("useBoth")) {
			useConnectionInput(IOConnectionMatrixFileName); // use connection matrix first
			useAssignmentInput(IOAssignmentMatrixFileName); // then use assignment matrix
			useGridAndResourceLocation(IOGridAndResourceLocationFileName); // use grid properties
		}
		if (commandWord.equalsIgnoreCase("useIGrid") || commandWord.equalsIgnoreCase("useGridInput")) {
			useGridAndResourceLocation(gridAndResourceLocationInputFileName); // use grid properties
			printGridAndResourceLocation(IOGridAndResourceLocationFileName);
		}
		if (commandWord.equalsIgnoreCase("useInputs") || commandWord.equalsIgnoreCase("useI")) {
			useConnectionInput(connectionInputFileName); // use connection matrix first
			useAssignmentInput(assignmentInputFileName); // then use assignment matrix
			useGridAndResourceLocation(gridAndResourceLocationInputFileName); // then use grid properties
			printMatrices(10);
			printMatrices(11);
			printGridAndResourceLocation(IOGridAndResourceLocationFileName);
			System.out.println("\tuseInputs of IConnection, IAssignment, and IGrid then printing to IOConnection, IOAssignment, IOGrid was successful");
		}
		if ((commandWord.equalsIgnoreCase("algorithmCaller") || commandWord.equalsIgnoreCase("AC")) && commandArray.length > 1) {								// try to execute a difference of 2
			results = algorithmCaller((int) ID);
			System.out.println("\tOK, algorithmCaller" + (int) ID + ": " + algorithm + " made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("takeTurn") || commandWord.equalsIgnoreCase("TT") || commandWord.equalsIgnoreCase("takeTurn0") || commandWord.equalsIgnoreCase("TT0")) {								// try to execute a difference of 2
			results = takeTurns(0);
			System.out.println("\tOK, takeTurn0 made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("takeTurn1") || commandWord.equalsIgnoreCase("TT1")) {								// try to execute a difference of 2
			results = takeTurns(1);
			System.out.println("\tOK, takeTurn1 made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("takeTurn2") || commandWord.equalsIgnoreCase("TT2")) {								// try to execute a difference of 2
			results = takeTurns(2);
			System.out.println("\tOK, takeTurn2 made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("takeTurn3") || commandWord.equalsIgnoreCase("TT3")) {								// try to execute a difference of 2
			results = takeTurns(3);
			System.out.println("\tOK, takeTurn3 made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("takeTurn4") || commandWord.equalsIgnoreCase("TT4")) {								// try to execute a difference of 2
			results = takeTurns(4);
			System.out.println("\tOK, takeTurn4 made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("takeTurn5") || commandWord.equalsIgnoreCase("TT5")) {								// try to execute a difference of 2
			results = takeTurns(5);
			System.out.println("\tOK, takeTurn5 made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("takeTurn6") || commandWord.equalsIgnoreCase("TT6")) {								// try to execute a difference of 2
			results = takeTurns(6);
			System.out.println("\tOK, takeTurn6 made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("worst") || commandWord.equalsIgnoreCase("w")) {								// try to execute a difference of 2
			results = worst();
			System.out.println("\tOK, worst made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("improvementPath") || commandWord.equalsIgnoreCase("IP")) {								// try to execute a difference of 2
			results = improvementPath(true);
			System.out.println("\tOK, the improvement path made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("improvementPaths") || commandWord.equalsIgnoreCase("IPs")) {
			int numberOfExecutes = 0;	// reset execution counter
			while (improvementPath(true) > 0) {
				numberOfExecutes++; 	// count while execution is successful
				if (algorithmStep + 1 < 7 * stepScale) {
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = ++algorithmStep;
					testResultsElement[0][1] = 1;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, improvement paths had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			if (algorithmStep < 7 * stepScale)
				results = 0; // output a 0 step execution on test results
			algorithmStep = Math.max(algorithmStep, 7 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("declinationPath") || commandWord.equalsIgnoreCase("DP")) {								// try to execute a difference of 2
			results = improvementPath(false);
			System.out.println("\tOK, the declination path made " + results + " declination" + (results == 1 ? "" : "s"));
			if (algorithmStep <= 3 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep - 1 > 3 * stepScale)
				algorithmStep--; 		// only decrement if it is not hitting the glass ceiling
			if (results == 0) 				// if execution was false, then all improvements were already done
				algorithmStep = Math.min(algorithmStep, 3 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("declinationPaths") || commandWord.equalsIgnoreCase("DPs")) {								// try to execute a difference of 2

			int numberOfExecutes = 0;	// reset execution counter
			while (improvementPath(false) > 0) {
				numberOfExecutes++; 	// count while execution is successful
				if (algorithmStep - 1 > 3 * stepScale) {
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = --algorithmStep;
					testResultsElement[0][1] = 1;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, declination paths had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "")); // print number of executes
			if (algorithmStep <= 3 * stepScale)
				return; // return to prevent adding elements to array list
			if (algorithmStep > 3 * stepScale)
				results = 0; // output a 0 step execution on test results
			algorithmStep = Math.min(algorithmStep, 3 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("batchImprovementPath") || commandWord.equalsIgnoreCase("BIP")
				|| commandWord.equalsIgnoreCase("C")) {
			results = batchImprovementPath(true);
			System.out.println("\tOK, the batch improvement path made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("batchImprovementPaths") || commandWord.equalsIgnoreCase("BIPs")
				|| commandWord.equalsIgnoreCase("d2")) {
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = batchImprovementPath(true);
				numberOfImprovements += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 7 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, batch improvement paths had occured " + numberOfExecutes + " time"
					+ (numberOfExecutes != 1 ? "s" : "") + " for " + numberOfImprovements + " improvement"
					+ (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.max(algorithmStep, 7 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("donateWtihFlooding") || commandWord.equalsIgnoreCase("DF") || commandWord.equalsIgnoreCase("DF3")) {	// try to execute donate with flooding
			int total = 0;
			for (Agent[] a1 : agents) {
				for (Agent a2 : a1) {
					total += a2.numberOfResourcesAssignedToAgent;
				}
			}
			System.out.println("before = " + total);
			results = donateWithFlooding(1, 1, 0, 1, .5, 0);
			System.out.println("\tOK, donate with flooding made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
			total = 0;
			for (Agent[] a1 : agents) {
				for (Agent a2 : a1) {
					total += a2.numberOfResourcesAssignedToAgent;
				}
			}
			System.out.println("after = " + total);
		}
		if (commandWord.equalsIgnoreCase("IPsWithoutSimulation") || commandWord.equalsIgnoreCase("IPWS")) {	// try to execute donate with flooding
			if (commandArray.length == 0) {
				ID = 1;
			}
			results = IPsWithoutSimulation(ID);
			System.out.println("\tOK, IPsWithoutSimulation made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
//		if (commandWord.equalsIgnoreCase("donateWtihFlooding1") || commandWord.equalsIgnoreCase("DF1")) {	// try to execute a difference of 2
//			results = donateWithFlooding(1);
//			System.out.println("\tOK, donateWtihFlooding1 made " + results + " improvement" + (results == 1 ? "" : "s"));
//			updateNeighboringAlgorithmStep(results);
//		}
//		if (commandWord.equalsIgnoreCase("donateWtihFlooding2") || commandWord.equalsIgnoreCase("DF2")) {	// try to execute a difference of 2
//			results = donateWithFlooding(2);
//			System.out.println("\tOK, donateWtihFlooding2 made " + results + " improvement" + (results == 1 ? "" : "s"));
//			updateNeighboringAlgorithmStep(results);
//		}
//		if (commandWord.equalsIgnoreCase("donateWtihFlooding3") || commandWord.equalsIgnoreCase("DF3")) {	// try to execute a difference of 2
//			results = donateWithFlooding(3);
//			System.out.println("\tOK, donateWtihFlooding3 made " + results + " improvement" + (results == 1 ? "" : "s"));
//			updateNeighboringAlgorithmStep(results);
//		}
		if (commandWord.equalsIgnoreCase("donateWtihFloodings") || commandWord.equalsIgnoreCase("DFs")) {
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = donateWithFlooding(1, 1, 0, 1, .5, 0);
				numberOfImprovements += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 7 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, donate with flooding had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "")
					+ " for " + numberOfImprovements + " improvement" + (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.max(algorithmStep, 7 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("robWtihFlooding") || commandWord.equalsIgnoreCase("RF") || commandWord.equalsIgnoreCase("RF0")) {	// try to execute rob with flooding
			results = robWithFlooding(1, 1, 1, 0);
			System.out.println("\tOK, rob with flooding made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("bid0") || commandWord.equalsIgnoreCase("bidding0") || commandWord.equalsIgnoreCase("B0")) {	// try to execute a bidding
			results = bidding(true, 0);
			System.out.println("\tOK, the bidding made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("bid1") || commandWord.equalsIgnoreCase("bidding1") || commandWord.equalsIgnoreCase("B1")  || commandWord.equalsIgnoreCase("B")) {
			results = bidding(true, 1);
			System.out.println("\tOK, the bidding made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("bid2") || commandWord.equalsIgnoreCase("bidding2") || commandWord.equalsIgnoreCase("B2")) {	// try to execute a bidding
			results = bidding(true, 2);
			System.out.println("\tOK, the bidding made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("bid3") || commandWord.equalsIgnoreCase("bidding3") || commandWord.equalsIgnoreCase("B3")) {	// try to execute a bidding
			results = bidding(true, 3);
			System.out.println("\tOK, the bidding made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("bid4") || commandWord.equalsIgnoreCase("bidding4") || commandWord.equalsIgnoreCase("B4")) {	// try to execute a bidding
			results = bidding(true, 4);
			System.out.println("\tOK, the bidding made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("bids") || commandWord.equalsIgnoreCase("biddings") || commandWord.equalsIgnoreCase("Bs")) {	// try to execute a bidding
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = bidding(true, 1);
				numberOfImprovements += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 7 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, bidding had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "") + " for "
					+ numberOfImprovements + " improvement" + (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.max(algorithmStep, 7 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("stableMatch") || commandWord.equalsIgnoreCase("stableMatching")
				|| commandWord.equalsIgnoreCase("SM")) {	// try to execute a bidding
			if (commandArray.length < 2)
				results = stableMatching(true, 0);
			else
				results = stableMatching(true, (int) ID);
			System.out.println("\tOK, the stable matching made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("stableMatches") || commandWord.equalsIgnoreCase("stableMatchings")
				|| commandWord.equalsIgnoreCase("SMs")) {
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = stableMatching(true, 0);
				numberOfImprovements += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 7 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, stable matching had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "")
					+ " for " + numberOfImprovements + " improvement" + (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.max(algorithmStep, 7 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("offerAccept") || commandWord.equalsIgnoreCase("OA") ) {	// try to execute a offer and accept
			results = offerAccept(true, 0);
			System.out.println("\tOK, offer and accept made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("oneOfferOneAccept") || commandWord.equalsIgnoreCase("1O1A")) {	// try to execute a offer and accept
			results = oneOfferOneAccept();
			System.out.println("\tOK, one-offer and one-accept made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("oneOfferMultiAccept") || commandWord.equalsIgnoreCase("OMA") || commandWord.equalsIgnoreCase("1OMA")) {	// try to execute a offer and accept
			results = oneOfferMultiAccept();
			System.out.println("\tOK, one-offer and multi-accept made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("multiOfferOneAccept") || commandWord.equalsIgnoreCase("MOA") || commandWord.equalsIgnoreCase("MO1A")) {	// try to execute a offer and accept
			results = multiOfferOneAccept(true);
			System.out.println("\tOK, multi-offer and one-accept made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("multiOfferMultiAccept") || commandWord.equalsIgnoreCase("MOMA")) {	// try to execute a offer and accept
			results = multiOfferMultiAccept(true, 0);
			System.out.println("\tOK, multi-offer and multi-accept made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("oneApplyOneGive") || commandWord.equalsIgnoreCase("AG") || commandWord.equalsIgnoreCase("1A1G")) {	// try to execute a offer and accept
			results = oneApplyOneGive();
			System.out.println("\tOK, one-apply and one-give made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("oneApplyMultiGive") || commandWord.equalsIgnoreCase("AMG") || commandWord.equalsIgnoreCase("1AMG")) {	// try to execute a offer and accept
//			results = oneApplyMultiGive(true);
			System.out.println("\tOK, one-apply and multi-give made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("multiApplyOneGive") || commandWord.equalsIgnoreCase("MAG") || commandWord.equalsIgnoreCase("MA1G")) {	// try to execute a offer and accept
			results = multiApplyOneGive();
			System.out.println("\tOK, multi-apply and one-give made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("multiApplyMultiGive") || commandWord.equalsIgnoreCase("MAMG")) {	// try to execute a offer and accept
			results = multiApplyMultiGive(false);
			System.out.println("\tOK, multi-apply and multi-give made " + results + " improvement" + (results == 1 ? "" : "s"));
			updateNeighboringAlgorithmStep(results);
		}
		if (commandWord.equalsIgnoreCase("offerAccepts") || commandWord.equalsIgnoreCase("OAs")) {
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = offerAccept(true, 0);
				numberOfImprovements += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 7 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, offer and accept had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "")
					+ " for " + numberOfImprovements + " improvement" + (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.max(algorithmStep, 7 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("cascadedOfferAccept") || commandWord.equalsIgnoreCase("COA")) {	// try to execute cascaded offer and accept
			results = offerAccept(true, 1);
			System.out.println("\tOK, the cascaded offer and accept made " + results + " executions" + (results == 1 ? "" : "s"));
			if (algorithmStep >= 9 * stepScale)
				return;
			if (algorithmStep + batchScale < 9 * stepScale)
				algorithmStep += batchScale; // only increment if it is not hitting the glass ceiling
			if (cascadedDecentralizedIsDone >= 2) 					// if execution was false, then all improvements were already done
				algorithmStep = Math.max(algorithmStep, 9 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("cascadedOfferAccepts") || commandWord.equalsIgnoreCase("COAs")) {	// try to execute cascaded offer and accepts
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = offerAccept(true, 1);
				numberOfImprovements += Math.max(results, 0);
				if (cascadedDecentralizedIsDone >= 2)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 9 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = Math.max(results, 0);
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, cascaded offer accept had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "")
					+ " for " + numberOfImprovements + " improvement" + (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 9 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.max(algorithmStep, 9 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("acceptOffer") || commandWord.equalsIgnoreCase("AO")) {
			results = offerAccept(false, 0);
			System.out.println("\tOK, the accept and offer made " + results + " declination" + (results == 1 ? "" : "s"));
			if (algorithmStep <= 3 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep + batchScale > 3 * stepScale)
				algorithmStep -= batchScale; // only increment if it is not hitting the glass ceiling
			if (results == 0) 				// if execution was false, then all improvements were already done
				algorithmStep = Math.min(algorithmStep, 3 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("acceptOffers") || commandWord.equalsIgnoreCase("AOs")) {
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = offerAccept(false, 0);
				numberOfImprovements += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep - batchScale > 3 * stepScale) {
					algorithmStep -= batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, accept and offer had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "")
					+ " for " + numberOfImprovements + " declination" + (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep <= 3 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.min(algorithmStep, 3 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("minMax") || commandWord.equals("mM") || commandWord.equals("mm")) {	// try to execute a min max
			results = minMax(0);
			System.out.println("\tOK, the min and max made " + Math.max(results, 0) + " improvement" + (results == 1 ? "" : "s"));
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep + batchScale < 7 * stepScale)
				algorithmStep += batchScale; // only increment if it is not hitting the glass ceiling
			if (results == 0) 				// if execution was false, then all improvements were already done
				algorithmStep = Math.max(algorithmStep, 7 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("minMaxes") || commandWord.equals("mMs") || commandWord.equals("mms")) {
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = minMax(0);
				numberOfImprovements += Math.max(results, 0);
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 7 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = Math.max(results, 0);
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, min and max had occured " + numberOfExecutes + " time" + (numberOfExecutes != 1 ? "s" : "") + " for "
					+ numberOfImprovements + " improvement" + (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 7 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.max(algorithmStep, 7 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("cascadedImprovementPath") || commandWord.equalsIgnoreCase("CIP")) {								// try to execute a cascaded improvement path
			results = cascadedImprovementPath(reachMatrix, 0, true);
			System.out.println("\tOK, the cascaded improvement path made " + results + " improvement" + (results == 1 ? "" : "s"));
			if (algorithmStep >= 9 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep + 1 < 9 * stepScale)
				algorithmStep++; 		// only increment if it is not hitting the glass ceiling
			if (results == 0) 				// if execution was false, then all cascaded improvements were already done
				algorithmStep = Math.max(algorithmStep, 9 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("cascadedImprovementPaths") || commandWord.equalsIgnoreCase("CIPs")) {
			int numberOfExecutes = 0;	// reset execution counter
			while (cascadedImprovementPath(reachMatrix, 0, true) > 0) {
				numberOfExecutes++; 	// count while execution is successful
				if (algorithmStep + 1 < 9 * stepScale) {
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = ++algorithmStep;
					testResultsElement[0][1] = 1;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, cascaded improvement paths had occured " + numberOfExecutes + " time"
					+ (numberOfExecutes != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 9 * stepScale)
				return; // return to prevent adding elements to array list
			if (algorithmStep < 9 * stepScale)
				results = 0; // output a 0 step execution on test results
			algorithmStep = Math.max(algorithmStep, 9 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("cascadedDeclinationPath") || commandWord.equalsIgnoreCase("CDP")) {								// try to execute a cascaded improvement path
			results = cascadedImprovementPath(reachMatrix, 0, false);
			System.out.println("\tOK, the cascaded declination path made " + results + " declination" + (results == 1 ? "" : "s"));
			if (algorithmStep <= 3 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep - 1 > 1 * stepScale)
				algorithmStep--; 		// only increment if it is not hitting the glass ceiling
			if (results == 0) 				// if execution was false, then all cascaded improvements were already done
				algorithmStep = Math.min(algorithmStep, 1 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("cascadedDeclinationPaths") || commandWord.equalsIgnoreCase("CDPs")) {
			int numberOfExecutes = 0;	// reset execution counter
			while (cascadedImprovementPath(reachMatrix, 0, false) > 0) {
				numberOfExecutes++; 	// count while execution is successful
				if (algorithmStep + 1 < 9 * stepScale) {
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = --algorithmStep;
					testResultsElement[0][1] = 1;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, cascaded declination paths had occured " + numberOfExecutes + " time"
					+ (numberOfExecutes != 1 ? "s" : "")); // print number of executes
			if (algorithmStep <= 1 * stepScale)
				return; // return to prevent adding elements to array list
			if (algorithmStep > 1 * stepScale)
				results = 0; // output a 0 step execution on test results
			algorithmStep = Math.min(algorithmStep, 1 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("batchCascadedImprovementPath") || commandWord.equalsIgnoreCase("BCIP")) {
			results = batchCascadedImprovementPath(true);
			System.out.println("\tOK, the batch cascaded improvement path made " + results + " improvement" + (results == 1 ? "" : "s"));
			if (algorithmStep >= 9 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep + batchScale < 9 * stepScale)
				algorithmStep += batchScale; // only increment if it is not hitting the glass ceiling
			if (results == 0) 			// if execution was false, then all improvements were already done
				algorithmStep = Math.max(algorithmStep, 9 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("batchCascadedImprovementPaths") || commandWord.equalsIgnoreCase("BCIPs")
				|| commandWord.equalsIgnoreCase("i2")) {
			results = 0;			// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfImprovements = 0;
			while (true) {
				results = batchCascadedImprovementPath(true);
				numberOfImprovements += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep + batchScale < 9 * stepScale) {
					algorithmStep += batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, batch cascaded improvement paths had occured " + numberOfExecutes + " time"
					+ (numberOfExecutes != 1 ? "s" : "") + " for " + numberOfImprovements + " improvement"
					+ (numberOfImprovements != 1 ? "s" : "")); // print number of executes
			if (algorithmStep >= 9 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = 9 * stepScale; // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("batchDeclinationPath") || commandWord.equalsIgnoreCase("BDP")) {
			results = batchImprovementPath(false);
			System.out.println("\tOK, the batch declination path made " + results + " deterioration" + (results == 1 ? "" : "s"));
			if (algorithmStep <= 3 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep - batchScale > 3 * stepScale)
				algorithmStep -= batchScale; // only increment if it is not hitting the glass ceiling
			if (results == 0) 			// if execution was false, then all improvements were already done
				algorithmStep = Math.min(algorithmStep, 3 * stepScale);
		}
		if (commandWord.equalsIgnoreCase("batchDeclinationPaths") || commandWord.equalsIgnoreCase("BDPs")
				|| commandWord.equalsIgnoreCase("d0")) {
			results = 0;				// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfDeteriorations = 0;
			while (true) {
				results = batchImprovementPath(false);
				numberOfDeteriorations += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep - batchScale > 3 * stepScale) {
					algorithmStep -= batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, batch declination paths had occured " + numberOfExecutes + " time"
					+ (numberOfExecutes != 1 ? "s" : "") + " for " + numberOfDeteriorations + " deterioration"
					+ (numberOfDeteriorations != 1 ? "s" : "")); // print number of executes
			if (algorithmStep <= 3 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = Math.min(algorithmStep, 3 * stepScale); // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("batchCascadedDeclinationPath") || commandWord.equalsIgnoreCase("BCDP")) {
			results = batchCascadedImprovementPath(false);
			System.out.println("\tOK, the batch cascaded declination path made " + results + " deterioration" + (results == 1 ? "" : "s"));
			if (algorithmStep <= 1 * stepScale)
				return; // return to prevent adding elements to array list
			if (results > 0 && algorithmStep - batchScale > 1 * stepScale)
				algorithmStep -= batchScale; // only increment if it is not hitting the glass ceiling
			if (results == 0) 				// if execution was false, then all improvements were already done
				algorithmStep = 1 * stepScale;
		}
		if (commandWord.equalsIgnoreCase("batchCascadedDeclinationPaths") || commandWord.equalsIgnoreCase("BCDPs")
				|| commandWord.equalsIgnoreCase("i0")) {
			results = 0;			// keep a copy to results to compare and add
			int numberOfExecutes = 0;	// reset execution counter
			int numberOfDeteriorations = 0;
			while (true) {
				results = batchCascadedImprovementPath(false);
				numberOfDeteriorations += results;
				if (results == 0)
					break;	// break if unsuccessful
				numberOfExecutes++;		// count while execution is successful
				if (algorithmStep - batchScale > 1 * stepScale) {
					algorithmStep -= batchScale;
					testResultsElement = new double[1][4];
					testResultsElement[0][0] = algorithmStep;
					testResultsElement[0][1] = results;
					testResultsElement[0][2] = fairnessIndex(1, null, true);
					testResultsElement[0][3] = fairnessIndex(2, null, true);
					testResults.add(testResultsElement);
				}
			}
			System.out.println("\tOK, batch cascaded declination paths had occured " + numberOfExecutes + " time"
					+ (numberOfExecutes != 1 ? "s" : "") + " for " + numberOfDeteriorations + " deterioration"
					+ (numberOfDeteriorations != 1 ? "s" : "")); // print number of executes
			if (algorithmStep <= 1 * stepScale)
				return; // return to prevent adding elements to array list
			algorithmStep = 1 * stepScale; // adjust algorithm step accordingly
		}
		if (commandWord.equalsIgnoreCase("worst") || commandWord.equalsIgnoreCase("W")) {
			results = worst();
			System.out.println("\tOK, the worst allocation of this network is now implemented");
			algorithmStep = 1 * stepScale; // adjust algorithm step accordingly
		}
		if (results >= 0) {
			testResultsElement = new double[1][4];
			testResultsElement[0][0] = algorithmStep;
			testResultsElement[0][1] = results;
			testResultsElement[0][2] = fairnessIndex(1, null, true);
			testResultsElement[0][3] = fairnessIndex(2, null, true);
			testResults.add(testResultsElement);
		}
		if (commandWord.equalsIgnoreCase("numrows") && commandArray.length > 1) {
			numrows = (int) ID;			// adjust variable and let user know
			Agent[][] newAgents = new Agent[numrows][numcols];
			for (int i = 0; i < Math.min(agents.length, numrows); i++)
				// use minimum to not go out of bounds of agents
				for (int j = 0; j < Math.min(agents[0].length, numcols); j++)
					newAgents[i][j] = agents[i][j]; // copy min of rows
			for (int i = Math.min(agents.length, numrows); i < numrows; i++)
				for (int j = 0; j < Math.min(agents[0].length, numcols); j++)
					newAgents[i][j] = new Agent(); // use new agents if numrows increase
			agents = newAgents;
			updateMatricesFromData();	// update matrices after adjusting numrows
			System.out.println("\tnumrows adjusted to " + numrows);
		}
		if (commandWord.equalsIgnoreCase("numcols") && commandArray.length > 1) {
			numcols = (int) ID;			// adjust variable and let user know
			Agent[][] newAgents = new Agent[numrows][numcols];
			for (int i = 0; i < Math.min(agents.length, numrows); i++)
				for (int j = 0; j < Math.min(agents[0].length, numcols); j++)
					newAgents[i][j] = agents[i][j]; // copy min of columns
			for (int i = 0; i < Math.min(agents.length, numrows); i++)
				for (int j = Math.min(agents[0].length, numcols); j < numcols; j++)
					newAgents[i][j] = new Agent(); // use new agents if numcols increased
			agents = newAgents;
			updateMatricesFromData(); 	// update matrices after adjusting numcols
			System.out.println("\tnumcols adjusted to " + numcols);
		}
		if (commandWord.equalsIgnoreCase("PARAMETER") && commandArray.length > 1) {
			PERIMETER = ID;				// adjust variable and let user know
			System.out.println("\tPARAMETER adjusted to " + PERIMETER);
		}
		if (commandWord.equalsIgnoreCase("N") && commandArray.length > 1) {
			if (ID < 0)					// check if N is negative
			{							// if so, then return and take next input
				System.out.println("\tN cannot be negative");
				return;					// return to take next command
			}
			boolean tempVocal = vocal;		// mute resource commands
			vocal = true;
			int IDtoKill = maxID;		// start killing IDs starting from the maxID
			for (IDtoKill = maxID; N > (int) ID; IDtoKill--) {							// kill until N resources
				if (resources[IDtoKill] != null && resources[IDtoKill].ID == IDtoKill)
					kill(IDtoKill, false);		// use if statement to not decrease N if already dead
			}
			updateMatricesFromData();
			while (N < (int) ID)
				// create until N resources
				create(Math.random() * (numrows - 1 + TOP_PERIMETER + BOTTOM_PERIMETER) - BOTTOM_PERIMETER, Math.random()
						* (numcols - 1 + LEFT_PERIMETER + RIGHT_PERIMETER) - RIGHT_PERIMETER, true);
			// create(Math.random() * (numrows - 1 + 2 * PERIMETER) - PERIMETER,
			// Math.random() * (numcols - 1 + 2 * PERIMETER) - PERIMETER, false);
			updateMatricesFromData();
			vocal = tempVocal;		// unmute resource commands
			System.out.println("\tN adjusted to " + N); // update users
		}
		if (commandWord.equalsIgnoreCase("CONNECTIVITY_RANGE") && commandArray.length > 1) {
			CONNECTIVITY_RANGE = ID;	// adjust variable and let user know
			System.out.println("\tCONNECTIVITY_RANGE adjusted to " + CONNECTIVITY_RANGE + " for future resources");
		}
		if (commandWord.equalsIgnoreCase("SQUARE_RANGE") && commandArray.length > 1) {
			SQUARE_RANGE = ID != 0 ? true : false; // adjust variable and let user know
			System.out.println("\tSQUARE_RANGE adjusted to " + SQUARE_RANGE + " for future resources");
		}
		if (commandWord.equalsIgnoreCase("DENSITY") && commandArray.length > 1) {
			DENSITY = ID;				// adjust variable and let user know
			System.out.println("\tDENSITY adjusted to " + DENSITY + " for future resources");
		}
		if (commandWord.equalsIgnoreCase("locationAlgorithm") && commandArray.length > 1) {
			locationAlgorithm = (int) ID; // adjust variable and let user know
			System.out.println("\tlocationAlgorithm adjusted to " + locationAlgorithm);
		}
		if (commandWord.equalsIgnoreCase("assignmentAlgorithm") && commandArray.length > 1) {
			assignmentAlgorithm = (int) ID; // adjust variable and let user know
			System.out.println("\tassignmentAlgorithm adjusted to " + assignmentAlgorithm);
		}
		if (commandWord.equalsIgnoreCase("RADIUS") && commandArray.length > 1) {
			RADIUS = ID;				// adjust variable and let user know
			System.out.println("\tRADIUS adjusted to " + RADIUS);
		}
		if (commandWord.equalsIgnoreCase("DOT_LAYOUT") && commandArray.length > 1) {
			DOT_LAYOUT = (int) ID;		// adjust variable and let user know
			System.out.println("\tDOT_LAYOUT adjusted to " + DOT_LAYOUT);
		}
		if (commandWord.equalsIgnoreCase("DOT_SCALE") && commandArray.length > 1) {
			DOT_SCALE = ID;				// adjust variable and let user know
			System.out.println("\tDOT_SCALE adjusted to " + DOT_SCALE);
		}
		if (commandWord.equalsIgnoreCase("numberOfAgentsWithSameRank") && commandArray.length > 1) {
			numberOfAgentsWithSameRank = (int) ID; // adjust variable and let user know
			System.out.println("\tnumberOfAgentsWithSameRank adjusted to " + numberOfAgentsWithSameRank);
		}
		if (commandWord.equalsIgnoreCase("vocal") && commandArray.length > 1) {
			vocal = ID != 0 ? true : false; // adjust variable and let user know
			System.out.println("\tmoveIsOccuring adjusted to " + vocal);
		}
		if (commandWord.equalsIgnoreCase("showDebug") && commandArray.length > 1) {
			showDebug = ID != 0 ? true : false; // adjust variable and let user know
			System.out.println("\tshowDebug adjusted to " + showDebug);
		}
		if (commandWord.equalsIgnoreCase("printDebug") && commandArray.length > 1) {
			showAlgorithmVisual = ID != 0 ? true : false; // adjust variable and let user know
			System.out.println("\tprintDebug adjusted to " + showAlgorithmVisual);
		}
		if (commandWord.equalsIgnoreCase("shuffle") && commandArray.length > 1) {
			shuffle = ID != 0 ? true : false; // adjust variable and let user know
			System.out.println("\tshuffle adjusted to " + shuffle);
		}
		if (commandWord.equalsIgnoreCase("connum") || commandWord.equalsIgnoreCase("numcon")
				|| commandWord.equalsIgnoreCase("numberOfConnections"))
			printNumberOfConnections();	// print the number of connections and the predicted number of connections
		if (commandWord.equalsIgnoreCase("help"))
			help();						// print out help menu
		if (commandWord.equalsIgnoreCase("testI") || commandWord.equalsIgnoreCase("tI"))
			testImprovementDeclination(true);			// print out test results
		if (commandWord.equalsIgnoreCase("testD") || commandWord.equalsIgnoreCase("tD"))
			testImprovementDeclination(false);			// print out test results
		if (commandWord.equalsIgnoreCase("ta") && commandArray.length > 1)
			testAlgorithms((int) ID);			// print out test results
		if (commandWord.equalsIgnoreCase("manualManipulate") || commandWord.equalsIgnoreCase("m1")) {
			algorithmWhoWinsOrderAndExecution[1] = 2;
		}
		if (commandWord.equalsIgnoreCase("manualManipulate") || commandWord.equalsIgnoreCase("m2")) {
			algorithmWhoWinsOrderAndExecution[1] = 1;
		}
		if (commandWord.equalsIgnoreCase("d") || commandWord.equalsIgnoreCase("debug")) {
			debug();					// print variables to attempt to debug
		}
		if (commandWord.equalsIgnoreCase("con")) {
			int actualNumberOfConnectionsSum = 0;
			int numberOfTrials = 10;
			boolean tempVocal = vocal;		// used to mute outputs
			vocal = false; 
			boolean tempShowDebug = showDebug;		// used to mute outputs
			showDebug = false; 
			for (int trialCounter = 0; trialCounter < numberOfTrials; trialCounter++) {
				print(trialCounter + " ");
				maxID = -1;					// compute maxID for creating new resources
				if (numEdges >= N && numEdges <= numcols * numrows * N)
					init3();	// initialize grid with agents and resources
				else
					init(numrows, numcols, N);
				algorithmStep = 5 * stepScale;		// used to determine file output destination
				randomNumberGenerator = new Random(1);	// Random number generator
				updateMinMaxFairnessIndexes();
				testResults = new ArrayList<double[][]>();
				testResultsElement = new double[1][4];
				testResultsElement[0][0] = algorithmStep;
				testResultsElement[0][2] = fairnessIndex(1, null, true);
				testResultsElement[0][3] = fairnessIndex(2, null, true);
				testResults.add(testResultsElement);
				printMatrices(10);
				printMatrices(11);
				int actualNumberOfConnections = 0;
				for (int i = 0; i < numrows * numcols; i++) {// go through the connection matrix and add up the number of connections
					for (int j = 0; j < maxID + 1; j++)
						actualNumberOfConnections += connectionMatrix[i][j];
				}
				actualNumberOfConnectionsSum += actualNumberOfConnections;
			}
			System.out.println("\n\t" + actualNumberOfConnectionsSum + " connections in " + numberOfTrials + " trials = "
					+ actualNumberOfConnectionsSum / numberOfTrials);
			vocal = tempVocal;
			showDebug = tempShowDebug;
		}
	}

	// ** Simulate Using Interactive Loop to Support Commands ***************************************
	void simulate() {
//		agentsWeight = new int[numrows * numcols]; TODO
//		for (int i = 0; i < numrows * numcols; i++) {
//			agentsWeight[i] = (int) (1 + (Math.random() * 7.0));
//			println(agentsWeight[i]);
//		}
		topographySetup(topographyOverride); // setupTopography(topographyOverride);
		if (numrows < 1 || numcols < 1 || N < 0) {								// also change move is occuring to false
			System.out.println("\terror: invalid numagents or N. Exiting...");
			System.exit(0);				// if problem with init, then end program
		}								// numEdges has to be >0 for init2
		boolean tempVocal = vocal;
		boolean showDebugTemp = showDebug;
		showDebug = false;
		vocal = false;
		if (numEdges >= N && numEdges <= numcols * numrows * N)
			init3();	// initialize grid with agents and resources
		else
			init(numrows, numcols, N);
		updateMinMaxFairnessIndexes();
		if (assignmentAlgorithm == 4) {
			algorithmStep = batchScale;
			bidding(true, 1);
			algorithmStep = 5 * stepScale;
			randomNumberGenerator = new Random(1);	// Random number generator
		}
		printMatrices(10);				// print IO matrices for reset2
		printMatrices(11);
		printGridAndResourceLocation(IOGridAndResourceLocationFileName);
		testResultsElement = new double[1][4];
		testResultsElement[0][0] = algorithmStep;
		testResultsElement[0][2] = fairnessIndex(1, null, true);
		testResultsElement[0][3] = fairnessIndex(2, null, true);
		testResults.add(testResultsElement);
		setupStartingFI();
		vocal = tempVocal;
		showDebug = showDebugTemp;
		System.out.print("% java AgentResourceSim\n");	// print start of program
		String s;						// string to grab user input
		while (true)					// repeat till quit
		{
			System.out.print("> ");		// print out prompt
			s = keyboard.nextLine();	// take a line of input
			if (s.equalsIgnoreCase("quit") || s.equalsIgnoreCase("q")) // check if user wants to quit
				break;
			String[] commandArray;		// make a array of strings
			String commandWord;			// declare variables to store each index of the string
			double ID = 0, row = 0, column = 0; // create variables to store input
			boolean outOfAgents = false; // keep track whether agent is out of bounds for members command
			commandArray = s.split(" "); // split the user input into parts
			for (int i = 1; i < commandArray.length; i++) {							// check every word after the first
				if (commandArray[i].charAt(0) == 'R' || commandArray[i].charAt(0) == 'r' || commandArray[i].charAt(0) == 'A'
						|| commandArray[i].charAt(0) == 'a')
					commandArray[i] = commandArray[i].substring(1); // omit the extra characters
			}
			if (commandArray.length == 1)
				; // check nothing if the command is only 1 word
			else if (commandArray.length == 2) {							// if 2 parts, then error check command ID
				if (!checkDouble(commandArray[1]))
					continue;
				ID = Double.parseDouble(commandArray[1]);
			} else if (commandArray.length == 3) {							// if 3 parts, then error check command i j
				if (!checkDouble(commandArray[1]) || !checkDouble(commandArray[2]))
					continue;			// take next input if
				row = Double.parseDouble(commandArray[1]);
				column = Double.parseDouble(commandArray[2]);
				if (row < -PERIMETER || row >= numrows + PERIMETER || column < -PERIMETER || column >= numcols + PERIMETER)	// include boarder due to create
				{						// check if district valid
					System.out.println("\terror: agent out of range for command i j");
					continue;			// if agent is out of range for all commands, continue to ask for next input
				}
			} else {							// if 4 parts, then command ID i j
				if (!checkDouble(commandArray[1]) || !checkDouble(commandArray[2]) || !checkDouble(commandArray[3]))
					continue;
				ID = Double.parseDouble(commandArray[1]); // set variables
				row = Double.parseDouble(commandArray[2]);
				column = Double.parseDouble(commandArray[3]);
				if (row < -PERIMETER || row >= numrows + PERIMETER || column < -PERIMETER || column >= numcols + PERIMETER)	// include boarder due to create
				{						// check if district is valid
					System.out.println("\terror: agent out of range for command ID i j");
					continue;			// if agent is out of range for all commands, continue to ask for next input
				}
			}
			commandWord = commandArray[0]; // grab command
			if (row < 0 || row >= numrows || column < 0 || column >= numcols)	// include boarder due to create
				outOfAgents = true;
			doCommand(commandWord, ID, row, column, outOfAgents, commandArray); // execute user command
		}
		System.out.println("\tgoodbye...");	// if quit then prompt exit
		System.exit(0);					// exit
	} // end simulate

	// ** Check if String can be Converted to Integer or Double *************************************
	boolean checkDouble(String checkString) {
		boolean decimalUsed = false;	// a double is allowed 1 decimal
		for (int i = 0; i < checkString.length(); i++) {
			if (i == 0 && checkString.charAt(0) == '-')
				;
			// it is okay for only the first char to be the negative sign
			else if (checkString.charAt(i) == '.') {							// if a decimal has occurred,
				if (decimalUsed) {						// complement the boolean or print error message
					System.out.println("\terror: cannont convert from String to double");
					return false;
				} else
					decimalUsed = true;
			} else if (checkString.charAt(i) < '0' || checkString.charAt(i) > '9') {							// check every char to see if it is a digit
				System.out.println("\terror: cannont convert from String to double");
				return false;
			}
		}								// if every character passes, then return true
		return true;
	} // end check double

	// ** Initialize Agents *************************************************************************
	// initializes the data structures of agents with the given arguments.
	public boolean init(int nrows, int ncols, int numberOfResources) {
		deadPool = new Stack<Integer>();
		resources = new Resource[2 * numberOfResources];
		if (numberOfResources == 0)		// if initially no people
			resources = new Resource[10]; // give a postive finite array size
		agents = new Agent[nrows][ncols]; // set data structure of 2D array of agents
		for (int i = 0; i < nrows; i++)
			// for every row
			for (int j = 0; j < ncols; j++)
				// for every column
				agents[i][j] = new Agent(); // create a new district for every row and column
		if (locationAlgorithm == 0)		// check if algorithm is equal to 0
			for (N = 0; N < numberOfResources;)
				create(-PERIMETER, -PERIMETER, false); // if so, create at parameter
		if (locationAlgorithm == 1)		// random location placement
			// store randomly generated rows and columns
				// double randomRow = Math.random() * (numrows - 1 + 2 * PERIMETER) - PERIMETER;
				// double randomColumn = Math.random() * (numcols - 1 + 2 * PERIMETER) - PERIMETER;
				// for (int i = 0; i < magnification; i++)
				// create(randomRow, randomColumn, false); // create at those rows and columns
				create(Math.random() * (numrows - 1 + TOP_PERIMETER + BOTTOM_PERIMETER) - BOTTOM_PERIMETER, Math.random()
						* (numcols - 1 + LEFT_PERIMETER + RIGHT_PERIMETER) - RIGHT_PERIMETER, true);
		if (locationAlgorithm == 2) {								// uniform placement
			// The way the number of rows are compute is to take a ratio of the powers. The formula
			// is resourcesPerRow = N^(numrows/(numrows+numcols)) and
			// resourcesPerColumn = N^(numcols/(numrows+numcols)) because the product of the two
			// will result in N.
			// For example, if 100 resources has to be divided into 3 rows and 7 columns, then
			// resourcesPerRow = 100^(3/(3+7)) = 3.981, which rounds to 4 and
			// resourcesPerColumn = 100^(7/(3+7)) = 25.119, which rounds to 25. The product of
			// 4*25 will result to 100. Other examples will get close to N as well.
			double numberOfRowsOfResources = (int) Math.round(Math.pow(numberOfResources, (double) (numrows + 2 * PERIMETER)
					/ (numrows + numcols + 4 * PERIMETER)));
			double numberOfColumnsOfResources = (int) Math.round(Math.pow(numberOfResources, (double) (numcols + 2 * PERIMETER)
					/ (numrows + numcols + 4 * PERIMETER)));
			double rowIncrement = (double) (numrows - 1 + 2 * PERIMETER) / numberOfRowsOfResources;
			double columnIncrement = (double) (numcols - 1 + 2 * PERIMETER) / numberOfColumnsOfResources;
			double rowWalker = rowIncrement / 2 - PERIMETER; // start walker halfway of increment and NOT at parameter
			double columnWalker = columnIncrement / 2 - PERIMETER;
			for (N = 0; N < numberOfResources;) {
				create(rowWalker, columnWalker, false); // create at location
				columnWalker += columnIncrement; // increment for new resource
				if (columnWalker > numcols - 1 + PERIMETER) {						// adjust location walkers accordingly when out of bounds
					columnWalker = columnIncrement / 2 - PERIMETER;
					rowWalker += rowIncrement;
					if (rowWalker > numrows - 1 + PERIMETER)
						rowWalker = 2 * rowIncrement / 4 - PERIMETER; // use 2*rowIncrement/4 to be able to easily change to 3/4
				}
			}
		}
		if (locationAlgorithm == 3) {								// inbetween agents location placement
			double rowWalker = .5 - Math.round(PERIMETER); // start off between agents
			double columnWalker = .5 - Math.round(PERIMETER);
			for (N = 0; N < numberOfResources;) {
				create(rowWalker, columnWalker, false); // create at walker's location
				columnWalker++;			// increment walker
				if (columnWalker > numcols - 1 + PERIMETER) {						// adjust walker positions accordingly if out of bounds
					columnWalker = .5 - Math.round(PERIMETER);
					rowWalker++;
					if (rowWalker > numrows - 1 + PERIMETER)
						rowWalker = .5 - Math.round(PERIMETER);
				}
			}
		}
		if (locationAlgorithm == 4) {								// circular placements
			double rowWalker = .5 - Math.round(PERIMETER); // center the circles between agents
			double columnWalker = .5 - Math.round(PERIMETER);
			int circleWalker = 0;
			int circleCounter = 0;		// count how many should have a difference of 1
			for (N = 0; N < numberOfResources; N++) {
				columnWalker++;			// increment walker and counter
				circleWalker++;
				if (columnWalker > numcols - 1 + PERIMETER) {						// adjust walker accordingly if out of bounds
					columnWalker = .5 - Math.round(PERIMETER);
					rowWalker++;
					if (rowWalker > numrows - 1 + PERIMETER) {
						circleCounter++;
						rowWalker = .5 - Math.round(PERIMETER);
					}
				}
			}
			double endedRow = rowWalker; // keep track of the ending location
			double endedColumn = columnWalker;
			circleWalker = 0;
			rowWalker = columnWalker = .5 - Math.round(PERIMETER); // reset walkers
			for (N = 0; N < numberOfResources;) {
				if (rowWalker * numcols + columnWalker >= endedRow * numcols + endedColumn)
					create(rowWalker + RADIUS * Math.sin(circleWalker * 2 * Math.PI / circleCounter), columnWalker + RADIUS
							* Math.cos(circleWalker * 2 * Math.PI / circleCounter), false);
				else
					// create the resource using the correct number of resources per a circle
					create(rowWalker + RADIUS * Math.sin(circleWalker * 2 * Math.PI / (circleCounter + 1)), columnWalker + RADIUS
							* Math.cos(circleWalker * 2 * Math.PI / (circleCounter + 1)), false);
				columnWalker++;			// increment walkers and adjust accordingly if out of bounds
				if (columnWalker > numcols - 1 + PERIMETER) {
					columnWalker = .5 - Math.round(PERIMETER);
					rowWalker++;
					if (rowWalker > numrows - 1 + PERIMETER) {
						rowWalker = .5 - Math.round(PERIMETER);
						circleWalker++;
					}
				}
			}
		}
		if (locationAlgorithm == 5) {								// geometric skew
			for (N = 0; N < numberOfResources;) {							// store randomly generated rows and columns
			// double randomRow = Math.random() * (numrows - 1 + 2 * PERIMETER) - PERIMETER;
			// double randomColumn = Math.random() * (numcols - 1 + 2 * PERIMETER) - PERIMETER;
				double randomRow = Math.random() * (numrows - 1 + TOP_PERIMETER + BOTTOM_PERIMETER) - BOTTOM_PERIMETER;
				double randomColumn = Math.random() * (numcols - 1 + LEFT_PERIMETER + RIGHT_PERIMETER) - RIGHT_PERIMETER;
				double closestRow = randomRow;
				double closestColumn = randomColumn;
				double shortestDistance = numrows + numcols + 4 * PERIMETER;
				while (Math.random() > pGeometric) {
					// randomRow = Math.random() * (numrows - 1 + 2 * PERIMETER) - PERIMETER;
					// randomColumn = Math.random() * (numcols - 1 + 2 * PERIMETER) - PERIMETER;
					randomRow = Math.random() * (numrows - 1 + TOP_PERIMETER + BOTTOM_PERIMETER) - BOTTOM_PERIMETER;
					randomColumn = Math.random() * (numcols - 1 + LEFT_PERIMETER + RIGHT_PERIMETER) - RIGHT_PERIMETER;
					for (int Nprevious = 0; Nprevious < N; Nprevious++) {
						double comparingDistance = Math.sqrt((randomRow - resources[Nprevious].row)
								* (randomRow - resources[Nprevious].row) + (randomColumn - resources[Nprevious].column)
								* (randomColumn - resources[Nprevious].column));
						if (comparingDistance < shortestDistance) {
							shortestDistance = comparingDistance;
							closestRow = randomRow;
							closestColumn = randomColumn;
						}
					}
				}
				create(closestRow, closestColumn, false); // create at those rows and columns
			}
		}
		resourceLocationBeforeDisturbance = new double[maxID + 1][2];
		for (int i = 0; i < maxID + 1; i++) {
			resourceLocationBeforeDisturbance[i][0] = resources[i].row;
			resourceLocationBeforeDisturbance[i][1] = resources[i].column;
		}
		updateMatricesFromData();
		return true;
	}	// end init

	// ** init2 with specified numagents, N, and numedges *******************************************
	public boolean init2Slow() {
		int numEdgesBackup = numEdges;
		PrintWriter outputStream = null;
		try {								// try to open up an output stream in file
			outputStream = new PrintWriter(new FileOutputStream(IOAgentNEdgeConnectionFileName));
		} catch (FileNotFoundException e) {
			System.out.println("\terror opening the file " + IOAgentNEdgeConnectionFileName);
			System.exit(0);
		}
		int[][] connectionMatrix = new int[numrows * numcols][N];
		for (int i = 0; i < N; i++) {								// make connection of every resource to at least 1 agent
			int randomInt = (int) Math.floor(Math.random() * numrows * numcols);
			int randomWalker = 0;
			while (randomInt > 0) {
				randomInt--;
				randomWalker++;
			}
			connectionMatrix[randomWalker][i] = 1;
		}
		int randomRange = numrows * numcols * N - N; // range is the connection matrix minus what is already filled
		// System.out.println("first randomrange is "+randomRange+" while numEdeges to be filled is "+(numEdges-N));
		for (numEdges -= N; numEdges > 0; numEdges--) {								// randomly add connections
			int randomInt = (int) Math.floor(Math.random() * (randomRange--));
			// System.out.print("the "+numEdges+"th edge randomInt is "+randomInt);
			int rowWalker = 0, columnWalker = 0;
			while (randomInt > 0 || connectionMatrix[rowWalker][columnWalker] == 1) {
				if (connectionMatrix[rowWalker][columnWalker] == 0)
					randomInt--;
				columnWalker++;
				if (columnWalker == N) {
					columnWalker = 0;
					rowWalker++;
				}
			}
			connectionMatrix[rowWalker][columnWalker] = 1;
			// System.out.println("; the "+numEdges+"th edge is on ("+rowWalker+","+columnWalker+")");//randomInt);
		}
		for (int i = 0; i < numrows * numcols; i++) {								// print the connection matrix
			for (int j = 0; j < N; j++)
				outputStream.print(connectionMatrix[i][j]);
			outputStream.println();
		}
		outputStream.close();
		// if (!mute) // maybe update user
		// System.out.println("\twrote to file: "+IOAgentNEdgeConnectionFileName);
		useConnectionInput(IOAgentNEdgeConnectionFileName); // use the connection matrix
		numEdges = numEdgesBackup;
		return true;
	}	// end init2 with specified numagents, N, and numedges
	
	// ** init3 with specified numagents, N, and numedges *******************************************
	public boolean init3Old() {
		PrintWriter outputStream = null;
		try {								// try to open up an output stream in file
			outputStream = new PrintWriter(new FileOutputStream(IOAgentNEdgeConnectionFileName));
		} catch (FileNotFoundException e) {
			System.out.println("\terror opening the file " + IOAgentNEdgeConnectionFileName);
			System.exit(0);
		}
		int[][] connectionMatrix = new int[numrows * numcols][N];
		for (int i = 0; i < N; i++) {								// make connection of every resource to at least 1 agent
			connectionMatrix[(int) (Math.random() * numrows * numcols)][i] = 1;
		}
		ArrayList<Integer> randomArray = new ArrayList<Integer>();
		for (int i = 0; i < numrows * numcols * N; i++) {
			randomArray.add(i);
		}
		Collections.shuffle(randomArray);
		int counter = numEdges - N;
		for (int i = 0; counter > 0; i++) {
			if (connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] == 0) {
				connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] = 1;
				counter--;
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {								// print the connection matrix
			for (int j = 0; j < N; j++)
				outputStream.print(connectionMatrix[i][j]);
			outputStream.println();
		}
		outputStream.close();
		// if (!mute) // maybe update user
		// System.out.println("\twrote to file: "+IOAgentNEdgeConnectionFileName);
		useConnectionInput(IOAgentNEdgeConnectionFileName); // use the connection matrix
		return true;
	}	// end init3 with specified numagents, N, and numedges
	
	// ** init3 with specified numagents, N, and numedges *******************************************
		public boolean init3Agents() {
			PrintWriter outputStream = null;
			try {								// try to open up an output stream in file
				outputStream = new PrintWriter(new FileOutputStream(IOAgentNEdgeConnectionFileName));
			} catch (FileNotFoundException e) {
				System.out.println("\terror opening the file " + IOAgentNEdgeConnectionFileName);
				System.exit(0);
			}
			int[][] connectionMatrix = new int[numrows * numcols][N];
			for (int i = 0; i < N; i++) {								// make connection of every resource to at least 1 agent
				connectionMatrix[(int) (Math.random() * numrows * numcols)][i] = 1;
			}
			ArrayList<Integer> randomArray = new ArrayList<Integer>();
			for (int i = 0; i < numrows * numcols * N; i++) {
				randomArray.add(i);
			}
			Collections.shuffle(randomArray);
			int counter = numEdges - N;
			for (int i = 0; counter > 0; i++) {
				if (connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] == 0) {
					connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] = 1;
					counter--;
				}
			}
			counter = 0;
//			ArrayList<Integer> a1List = new ArrayList<Integer>();
//			ArrayList<Integer> a2List = new ArrayList<Integer>();
//			for (int i = 0; i < numrows * numcols; i++) {
//				a1List.add(i);
//				a2List.add(i);
//			}
//			Collections.shuffle(a1List);
//			Collections.shuffle(a2List);
			int counter2 = 0;
			int counter3 = 0;
			while (counter < numAgentsFlops) {
				int a1 = (int) (Math.random() * numrows * numcols);
				int a2 = (int) (Math.random() * numrows * numcols);
				ArrayList<Integer> a1Connections = new ArrayList<Integer>();
				ArrayList<Integer> a2Connections = new ArrayList<Integer>();
				for (int j = 0; j < N; j++) {
					if (connectionMatrix[a1][j] > 0) {
						a1Connections.add(j);
					}
					if (connectionMatrix[a2][j] > 0) {
						a2Connections.add(j);
					}
				}
				if (a1Connections.size() > a2Connections.size()) {
					Collections.shuffle(a2Connections);
					for (int j = 0; j < a2Connections.size(); j++) {
						if (!a1Connections.contains(a2Connections.get(j))) {
							connectionMatrix[a1][a2Connections.get(j)] = 1;
							connectionMatrix[a2][a2Connections.get(j)] = 0;
							counter++;
							counter2 = 0;
							break;
						}
					}
				} else if (a2Connections.size() > a1Connections.size()) {
					Collections.shuffle(a1Connections);
					for (int j = 0; j < a1Connections.size(); j++) {
						if (!a2Connections.contains(a1Connections.get(j))) {
							connectionMatrix[a2][a1Connections.get(j)] = 1;
							connectionMatrix[a1][a1Connections.get(j)] = 0;
							counter++;
							counter2 = 0;
							break;
						}
					}
				} else {
					counter2++;
					if (counter2 >= 10) {
						counter3++;
						counter2 = 0;
						counter++;
					}
				}
			}
			System.out.println("counter3 = " + counter3);
			for (int i = 0; i < numrows * numcols; i++) {								// print the connection matrix
				for (int j = 0; j < N; j++)
					outputStream.print(connectionMatrix[i][j]);
				outputStream.println();
			}
			outputStream.close();
			// if (!mute) // maybe update user
			// System.out.println("\twrote to file: "+IOAgentNEdgeConnectionFileName);
			useConnectionInput(IOAgentNEdgeConnectionFileName); // use the connection matrix
			return true;
		}	// end init3 with specified numagents, N, and numedges
		
		// ** init3 with specified numagents, N, and numedges *******************************************
		public boolean init3Resources() {
			PrintWriter outputStream = null;
			try {								// try to open up an output stream in file
				outputStream = new PrintWriter(new FileOutputStream(IOAgentNEdgeConnectionFileName));
			} catch (FileNotFoundException e) {
				System.out.println("\terror opening the file " + IOAgentNEdgeConnectionFileName);
				System.exit(0);
			}
			int[][] connectionMatrix = new int[numrows * numcols][N];
			for (int i = 0; i < N; i++) {								// make connection of every resource to at least 1 agent
				connectionMatrix[(int) (Math.random() * numrows * numcols)][i] = 1;
			}
			ArrayList<Integer> randomArray = new ArrayList<Integer>();
			for (int i = 0; i < numrows * numcols * N; i++) {
				randomArray.add(i);
			}
			Collections.shuffle(randomArray);
			int counter = numEdges - N;
			for (int i = 0; counter > 0; i++) {
				if (connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] == 0) {
					connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] = 1;
					counter--;
				}
			}
			counter = 0;
			int counter2 = 0;
			int counter3 = 0;
			while (counter < numResourcesFlops) {
				int r1 = (int) (Math.random() * N);
				int r2 = (int) (Math.random() * N);
				ArrayList<Integer> r1Connections = new ArrayList<Integer>();
				ArrayList<Integer> r2Connections = new ArrayList<Integer>();
				for (int j = 0; j <numrows * numcols; j++) {
					if (connectionMatrix[j][r1] > 0) {
						r1Connections.add(j);
					}
					if (connectionMatrix[j][r2] > 0) {
						r2Connections.add(j);
					}
				}
				if (r1Connections.size() > r2Connections.size() && r2Connections.size() >= 2) {
					Collections.shuffle(r2Connections);
					for (int j = 0; j < r2Connections.size(); j++) {
						if (!r1Connections.contains(r2Connections.get(j))) {
							connectionMatrix[r2Connections.get(j)][r1] = 1;
							connectionMatrix[r2Connections.get(j)][r2] = 0;
							counter++;
							counter2 = -1;
							break;
						}
					}
					counter2++;
				} else if (r2Connections.size() > r1Connections.size() && r1Connections.size() >= 2) {
					Collections.shuffle(r1Connections);
					for (int j = 0; j < r1Connections.size(); j++) {
						if (!r2Connections.contains(r1Connections.get(j))) {
							connectionMatrix[r1Connections.get(j)][r2] = 1;
							connectionMatrix[r1Connections.get(j)][r1] = 0;
							counter++;
							counter2 = -1;
							break;
						}
					}
					counter2++;
				} else {
					counter2++;
					if (counter2 >= 10) {
						counter3++;
						counter2 = 0;
						counter++;
					}
				}
			}
			System.out.println("counter3 = " + counter3);
			for (int i = 0; i < numrows * numcols; i++) {								// print the connection matrix
				for (int j = 0; j < N; j++)
					outputStream.print(connectionMatrix[i][j]);
				outputStream.println();
			}
			outputStream.close();
			// if (!mute) // maybe update user
			// System.out.println("\twrote to file: "+IOAgentNEdgeConnectionFileName);
			useConnectionInput(IOAgentNEdgeConnectionFileName); // use the connection matrix
			return true;
		}	// end init3 with specified numagents, N, and numedges
	
		// ** init3 with specified numagents, N, and numedges *******************************************
		public boolean init3() {
			PrintWriter outputStream = null;
			try {								// try to open up an output stream in file
				outputStream = new PrintWriter(new FileOutputStream(IOAgentNEdgeConnectionFileName));
			} catch (FileNotFoundException e) {
				System.out.println("\terror opening the file " + IOAgentNEdgeConnectionFileName);
				System.exit(0);
			}
			int[][] connectionMatrix = new int[numrows * numcols][N];
			for (int i = 0; i < N; i++) {								// make connection of every resource to at least 1 agent
				connectionMatrix[(int) (Math.random() * numrows * numcols)][i] = 1;
			}
			ArrayList<Integer> randomArray = new ArrayList<Integer>();
			for (int i = 0; i < numrows * numcols * N; i++) {
				randomArray.add(i);
			}
			Collections.shuffle(randomArray);
			int counter = numEdges - N;
			for (int i = 0; counter > 0; i++) {
				if (connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] == 0) {
					connectionMatrix[randomArray.get(i) / N][randomArray.get(i) %  N] = 1;
					counter--;
				}
			}
			counter = 0;
			int counter2 = 0;
			while (counter < numAgentsFlops) {
				int a1 = (int) (Math.random() * numrows * numcols);
				int a2 = (int) (Math.random() * numrows * numcols);
				ArrayList<Integer> a1Connections = new ArrayList<Integer>();
				ArrayList<Integer> a2Connections = new ArrayList<Integer>();
				for (int j = 0; j < N; j++) {
					if (connectionMatrix[a1][j] > 0) {
						a1Connections.add(j);
					}
					if (connectionMatrix[a2][j] > 0) {
						a2Connections.add(j);
					}
				}
				if (a1Connections.size() > a2Connections.size()) {
					Collections.shuffle(a2Connections);
					for (int j = 0; j < a2Connections.size(); j++) {
						if (!a1Connections.contains(a2Connections.get(j))) {
							connectionMatrix[a1][a2Connections.get(j)] = 1;
							connectionMatrix[a2][a2Connections.get(j)] = 0;
							counter++;
							counter2 = 0;
							break;
						}
					}
				} else if (a2Connections.size() > a1Connections.size()) {
					Collections.shuffle(a1Connections);
					for (int j = 0; j < a1Connections.size(); j++) {
						if (!a2Connections.contains(a1Connections.get(j))) {
							connectionMatrix[a2][a1Connections.get(j)] = 1;
							connectionMatrix[a1][a1Connections.get(j)] = 0;
							counter++;
							counter2 = 0;
							break;
						}
					}
				} else {
					counter2++;
					if (counter2 >= 10) {
						counter2 = 0;
						counter++;
					}
				}
			}
			counter = 0;
			counter2 = 0;
			while (counter < numResourcesFlops) {
				int r1 = (int) (Math.random() * N);
				int r2 = (int) (Math.random() * N);
				ArrayList<Integer> r1Connections = new ArrayList<Integer>();
				ArrayList<Integer> r2Connections = new ArrayList<Integer>();
				for (int j = 0; j <numrows * numcols; j++) {
					if (connectionMatrix[j][r1] > 0) {
						r1Connections.add(j);
					}
					if (connectionMatrix[j][r2] > 0) {
						r2Connections.add(j);
					}
				}
				if (r1Connections.size() > r2Connections.size() && r2Connections.size() >= 2) {
					Collections.shuffle(r2Connections);
					for (int j = 0; j < r2Connections.size(); j++) {
						if (!r1Connections.contains(r2Connections.get(j))) {
							connectionMatrix[r2Connections.get(j)][r1] = 1;
							connectionMatrix[r2Connections.get(j)][r2] = 0;
							counter++;
							counter2 = -1;
							break;
						}
					}
					counter2++;
				} else if (r2Connections.size() > r1Connections.size() && r1Connections.size() >= 2) {
					Collections.shuffle(r1Connections);
					for (int j = 0; j < r1Connections.size(); j++) {
						if (!r2Connections.contains(r1Connections.get(j))) {
							connectionMatrix[r1Connections.get(j)][r2] = 1;
							connectionMatrix[r1Connections.get(j)][r1] = 0;
							counter++;
							counter2 = -1;
							break;
						}
					}
					counter2++;
				} else {
					counter2++;
					if (counter2 >= 10) {
						counter2 = 0;
						counter++;
					}
				}
			}
			for (int i = 0; i < numrows * numcols; i++) {								// print the connection matrix
				for (int j = 0; j < N; j++)
					outputStream.print(connectionMatrix[i][j]);
				outputStream.println();
			}
			outputStream.close();
			// if (!mute) // maybe update user
			// System.out.println("\twrote to file: "+IOAgentNEdgeConnectionFileName);
			useConnectionInput(IOAgentNEdgeConnectionFileName); // use the connection matrix
			return true;
		}	// end init3 with specified numagents, N, and numedges
		
		
	// ** Reset1 ***********************************************************************************
	public void reset1(){
		maxID = -1;					// compute maxID for creating new resources
		boolean tempVocal = vocal;				// used to mute reset
		vocal = false;
		if (numEdges >= N && numEdges <= numcols * numrows * N)
			init3();	// initialize grid with agents and resources
		else
			init(numrows, numcols, N);
		vocal = tempVocal;
		algorithmStep = 5 * stepScale;		// used to determine file output destination
		randomNumberGenerator = new Random(1);	// Random number generator
		updateMinMaxFairnessIndexes();
		testResults = new ArrayList<double[][]>();
		testResultsElement = new double[1][4];
		testResultsElement[0][0] = algorithmStep;
		testResultsElement[0][2] = fairnessIndex(1, null, true);
		testResultsElement[0][3] = fairnessIndex(2, null, true);
		testResults.add(testResultsElement);
		if (assignmentAlgorithm == 4) {
			algorithmStep = batchScale;
			bidding(true, 1);
			algorithmStep = 5 * stepScale;
			randomNumberGenerator = new Random(1);	// Random number generator
		}
		System.out.println("\treset1 (redo allocation algorithm) was successful"); // let user know about the reset
		printMatrices(10);
		printMatrices(11);
		printGridAndResourceLocation(IOGridAndResourceLocationFileName);
		vocal = tempVocal;
	} 	// end reset1
	
	// ** Reset2 ***********************************************************************************
	public void reset2() {
		useConnectionInput(IOConnectionMatrixFileName); // use connection matrix first
		useAssignmentInput(IOAssignmentMatrixFileName); // then use assignment matrix
		if (vocal)
			System.out.println("\treset2 (revert time back to " + 5 * stepScale + ") was successful"); // let user know about the reset
	}	// end reset2

	// ** Return Array List of Resources Connected to Agents to Print ******************************
	public ArrayList<Integer> members(int row, int col) {
		ArrayList<Integer> returnList = new ArrayList<Integer>(); 	// create the array to store IDs
		Resource resourceWalker = agents[row][col].firstResource; 	// grab the first resource in list
		while (resourceWalker != null) {						  	// keep walking and adding to the list of IDs
			returnList.add(resourceWalker.ID);	
			resourceWalker = resourceWalker.nextResource;
		}
		return returnList;											// return list
	}	// end resources to print
	
	public boolean printCheckContains(int row, int col, int resourceID) {
		if (resourceID >= resources.length || resources[resourceID] == null || resources[resourceID].ID != resourceID) {
			System.out.println("\tno such resource"); // check to make sure ID is valid
			return false;
		}
		System.out.println("\tit is '" + members(row, col).contains(resourceID) + "' that A" + (row * numcols + col) + " contains R" + resourceID);
		return true;
	}

	public boolean checkContains(int resourceID, int row, int col) {
		return members(row, col).contains(resourceID);
	}	// end check contains

	// ** Return Row and Column of Resource *********************************************************
	public boolean whereIs(int resourceID) {// print out the row, column, and then the agents connected to the resource
		if (resourceID >= resources.length || resources[(int) resourceID] == null || resources[(int) resourceID].ID != (int) resourceID) {
			System.out.println("\tno such resource"); // check to make sure ID is valid
			return false;
		}
		System.out.print("\tlocation (" + resources[resourceID].row + ", " + resources[resourceID].column + ")");
		for (int row = 0; row < numrows; row++)
			for (int column = 0; column < numcols; column++)
				if (checkContains(resourceID, row, column))
					System.out.print(", A" + (row * numcols + column));
		System.out.println();
		return true;					// return for next user input
	}	// end return Row and Column of Resource

	// ** Kill Person and Push ID onto Stack ********************************************************
	public boolean kill(int resourceID, boolean doUpdate) {
		if (resourceID >= resources.length || resources[resourceID] == null || resources[resourceID].ID != resourceID) {
			System.out.println("\tno such resource"); // check to make sure ID is valid
			return false;
		}
		for (int i = 0; i < numrows * numcols; i++) // check every agent
		{
			Resource resourceWalker = agents[i / numcols][i % numcols].firstResource; // take head of the agent
			while (resourceWalker != null && resourceWalker.ID != resourceID)
				// keep walking until ID found or until end
				resourceWalker = resourceWalker.nextResource;
			if (resourceWalker == null)
				continue;				// if resource not found then check next agent
			if (resourceWalker.nextResource != null) // adjust the next resource's previous resource
				resourceWalker.nextResource.previousResource = resourceWalker.previousResource;
			if (resourceWalker.previousResource != null) // adjust the previous resource's next resource
				resourceWalker.previousResource.nextResource = resourceWalker.nextResource;
			else
				// if (resourceWalker.previousResource==null), then resourceWalker was head
				agents[i / numcols][i % numcols].firstResource = resourceWalker.nextResource; // for the kill, redeclare the head
			agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent--;// = agents[i/numcols][i%numcols].numberOfResourcesConnectedToAgent-1;
		}								// adjust the numberOfResourcesConnectedToAgent
		deadPool.push(resourceID);		// push ID tag into a stack
		if (vocal)			// print if mute is off
			System.out.println("\t" + resourceID + " is dead");
		agents[assignmentVector[resourceID] / numcols][assignmentVector[resourceID] % numcols].numberOfResourcesAssignedToAgent--;
		resources[resourceID].ID = -1;	// set tag to signify death
		N--;							// subtract global population after kill
		if (doUpdate)
			updateMatricesFromData();	// update the matrices
		return true;
	}	// end kill

	// ** Create Person *****************************************************************************
	// creates a new resource on agent (row,col); returns that person's ID; if one or more
	// previously used IDs are available (because of previous kill operations), one of those must
	// be used
	public int create(double doubleRow, double doubleColumn, boolean doUpdate) {
		int ID;
		if (!deadPool.empty())
			ID = deadPool.pop();									// grab ID from stack if any
		else {														// else grab new ID
			ID = ++maxID;
			if (ID >= resources.length) {							// if array is not large enough, double its size
				Resource[] newResources = new Resource[2 * ID];
				for (int i = 0; i < resources.length; i++)
					newResources[i] = resources[i];
				resources = newResources; 							// adjust the resources
			}
		}
		int row, column, beginingRow, endingRow, beginingColumn, endingColumn; // declare variables once in case random
		// initialize rows and columns based on range
		beginingRow = Math.max(0, (int) Math.ceil(doubleRow - CONNECTIVITY_RANGE));
		endingRow = Math.min(numrows - 1, (int) Math.floor(doubleRow + CONNECTIVITY_RANGE));
		beginingColumn = Math.max(0, (int) Math.ceil(doubleColumn - CONNECTIVITY_RANGE));
		endingColumn = Math.min(numcols - 1, (int) Math.floor(doubleColumn + CONNECTIVITY_RANGE));
		int numberOfConnectedAgents = 0;
		// first compute the estimated number of connections and all connections
		int possibleConnectionCounter = 0;
		double estimatedNumberOfConnections = 0;
		double sumOfAllConnections = 0;
		ArrayList<Integer> listOfSpecialCaseAgents = new ArrayList<Integer>(); // ArrayList(int[][])<>;
		for (row = beginingRow; row <= endingRow; row++) {								// check every agent
			for (column = beginingColumn; column <= endingColumn; column++) {
				if (!SQUARE_RANGE && Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2)) > CONNECTIVITY_RANGE)
					continue;			// check if it as at least within range for SQUARE_RANGE
				possibleConnectionCounter++;
				sumOfAllConnections += agents[row][column].numberOfResourcesConnectedToAgent + agentBalanceFactor;
			}
		}
		estimatedNumberOfConnections = possibleConnectionCounter * (DENSITY + gaussianNumberGenerator.nextGaussian() * densityVariance);
		if (estimatedNumberOfConnections <= 0)
			estimatedNumberOfConnections = 1;
		// do
		// {
		// estimatedNumberOfConnections = possibleConnectionCounter*(DENSITY+gaussianNumberGenerator.nextGaussian()*densityVariance);
		// // System.out.println(estimatedNumberOfConnections);
		// } while (estimatedNumberOfConnections<0||estimatedNumberOfConnections>possibleConnectionCounter);
		// if (densityVariance>0)
		// estimatedNumberOfConnections = possibleConnectionCounter*Math.random();
		if (showDebug)
			System.out.println("\t\tr" + ID + ": estimatedNumOfConnections = " + estimatedNumberOfConnections
					+ " and sumOfAllConnections = " + sumOfAllConnections);
		// second do the special cases where the probability of connection is greater than 1 (for the agent
		int numberOfConnectionsMade = 0;
		double numberOfConnectionsToSubtractFromAll = 0;
		for (row = beginingRow; row <= endingRow; row++) {								// check every agent
			for (column = beginingColumn; column <= endingColumn; column++) {
				if (!SQUARE_RANGE && Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2)) > CONNECTIVITY_RANGE)
					continue;			// check if it as at least within range for SQUARE_RANGE
				// System.out.println("\t\t"+sumOfAllConnections+"_"+(double)agents[row][column].numberOfResourcesConnectedToAgent/sumOfAllConnections);
				if (agentBalanceFactor > 0
						&& (double) (agents[row][column].numberOfResourcesConnectedToAgent + agentBalanceFactor) / sumOfAllConnections
								* estimatedNumberOfConnections >= 1) {
					listOfSpecialCaseAgents.add(row * numcols + column);
					numberOfConnectionsMade++;
					numberOfConnectionsToSubtractFromAll += agents[row][column].numberOfResourcesConnectedToAgent + agentBalanceFactor;
					numberOfConnectedAgents++;
					resources[ID] = new Resource(ID, doubleRow, doubleColumn);
					if (agents[row][column].firstResource != null) // modify agent head if a head exists
						agents[row][column].firstResource.previousResource = resources[ID]; // declare the head's previous
					resources[ID].nextResource = agents[row][column].firstResource; // declare the new resource's next
					agents[row][column].firstResource = resources[ID]; // make the resource the new head
					agents[row][column].numberOfResourcesConnectedToAgent++; // increase agent recourse count
					// numberOfConnectedAgentsArray[ID]++; // omit since have to consider init2
				}
			}
		}
		// last use a probability function to determine if the rest of the connections should be made
		estimatedNumberOfConnections -= numberOfConnectionsMade;
		sumOfAllConnections -= numberOfConnectionsToSubtractFromAll;
		for (row = beginingRow; row <= endingRow; row++) {								// check every agent
			for (column = beginingColumn; column <= endingColumn; column++) {
				if (!SQUARE_RANGE && Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2)) > CONNECTIVITY_RANGE)
					continue;			// check if it as at least within range for SQUARE_RANGE
				if (((agentBalanceFactor > 0 && !listOfSpecialCaseAgents.contains(row * numcols + column) && (double) (agents[row][column].numberOfResourcesConnectedToAgent + agentBalanceFactor)
						/ sumOfAllConnections * estimatedNumberOfConnections > Math.random()))
						|| (agentBalanceFactor <= 0 && densityVariance == 0 && Math.random() < DENSITY)
						|| (agentBalanceFactor <= 0 && densityVariance > 0 && Math.random() < (double) estimatedNumberOfConnections
								/ possibleConnectionCounter)) {
					numberOfConnectedAgents++;
					resources[ID] = new Resource(ID, doubleRow, doubleColumn);
					if (agents[row][column].firstResource != null) // modify agent head if a head exists
						agents[row][column].firstResource.previousResource = resources[ID]; // declare the head's previous
					resources[ID].nextResource = agents[row][column].firstResource; // declare the new resource's next
					agents[row][column].firstResource = resources[ID]; // make the resource the new head
					agents[row][column].numberOfResourcesConnectedToAgent++; // increase agent recourse count
					// numberOfConnectedAgentsArray[ID]++; // omit since have to consider init2
				}
			}
		}

		// System.out.println("second algorithm did "+(numberOfConnectedAgents-t1)+" connections");
		resources[ID] = new Resource(ID, doubleRow, doubleColumn, -1, -1);
		// redeclare resource with no assigned column or row
		// as to NOT use the last declare instance of the agent
		long rowToAssign = -1, columnToAssign = -1;
		boolean alreadyAssigned = false;
		if (numberOfConnectedAgents <= 0 || assignmentAlgorithm <= 0 || assignmentAlgorithm == 4) {								
			// do not cast an assignment if it is connected to less than 1 agent
			// or if less than assignment algorithm of 1
			// these assignment algorithms only compute the assigned row and column
			// therefore, if the assignment algorithm <=0, then no computations required
			// System.out.println("A" + ID + " is not connected");
		}
		if (assignmentAlgorithm == 1) {								// random assignment
			if (ceilingLimit) {
				ArrayList<Integer> possibleResources = new ArrayList<Integer>();
				for (int i = 0; i < numrows * numcols; i++) {
					if (checkContains(ID, i / numcols, i % numcols)) {
						possibleResources.add(i);
					}
				}
				if (possibleResources.size() > 0) {
					Collections.shuffle(possibleResources);
					while (possibleResources.size() > 1 && agents[possibleResources.get(0) / numcols][possibleResources.get(0) % numcols].numberOfResourcesAssignedToAgent + 1 > CEILING) {
						possibleResources.remove(0);
					}
					rowToAssign = possibleResources.get(0) / numcols;
					columnToAssign = possibleResources.get(0) % numcols;
				}
			} else {
				int counterForAssignment = (int) Math.floor(Math.random() * (numberOfConnectedAgents)); // random int
				for (row = 0; row < numrows; row++) {
					if (alreadyAssigned)
						break;
					for (column = 0; column < numcols; column++) {
						if (alreadyAssigned)
							break;
						if (checkContains(ID, row, column)) {
							if (counterForAssignment == 0) {
								rowToAssign = row;
								columnToAssign = column;
								alreadyAssigned = true;
								break;
							}
							if (!alreadyAssigned)
								counterForAssignment--;
						}
					}
				}
			}
		}
		if (assignmentAlgorithm == 2) {								// shortest distance
			double shortestDistance = Math.pow(numrows + PERIMETER, 2) + Math.pow(numcols + PERIMETER, 2) + 1;
			// compute something greater than the maximum distance so that the first compare will be the shortest distance
			for (row = 0; row < numrows; row++) {
				if (alreadyAssigned)
					break;
				for (column = 0; column < numcols; column++) {
					if (alreadyAssigned)
						break;
					if (checkContains(ID, row, column)
							&& Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2)) < shortestDistance) {					// if agent is connected and is the shortest
						// distance, then change row and column to
						// assign
						shortestDistance = Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2));
						rowToAssign = row;
						columnToAssign = column;
					}
				}
			}
		}
		if (assignmentAlgorithm == 3) {								// pmf assignment
			double totalDistance = 0;	// reset total distance to 0, and then add as going through agents
			for (row = 0; row < numrows; row++) {
				for (column = 0; column < numcols; column++) {
					if (checkContains(ID, row, column) && Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2)) != 0) {					// add up the inverses of the
						// distances so long as the distance
						// is not zero
						// because of the dividing by zero
						// error
						totalDistance += 1 / Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2));
					}
				}
			}
			double counterForAssignment = Math.random() * totalDistance; // generate a random distance
			for (row = 0; row < numrows; row++) {
				if (alreadyAssigned)
					break;
				for (column = 0; column < numcols; column++) {
					if (alreadyAssigned)
						break;
					if (checkContains(ID, row, column)) {
						if (Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2)) == 0) {				// if the distance is 0, then assign to that agent
							rowToAssign = row;
							columnToAssign = column;
							alreadyAssigned = true;
							break;
						} else {				// else subtract the inverse of the distance
							counterForAssignment -= 1 / Math.sqrt(Math.pow(row - doubleRow, 2) + Math.pow(column - doubleColumn, 2));
							if (counterForAssignment <= 0) {			// assign if the difference is less than 0
								rowToAssign = row;
								columnToAssign = column;
								alreadyAssigned = true;
								break;
							}
						}
					}
				}
			}
		}
		resources[ID].assignedRow = rowToAssign; // do the assignment and increase numberOfResourcesAssignedToAgent
		resources[ID].assignedColumn = columnToAssign;
		if (rowToAssign >= 0 && rowToAssign < numrows && columnToAssign >= 0 && columnToAssign < numcols)
			// check to make sure the agent in within range
			agents[(int) rowToAssign][(int) columnToAssign].numberOfResourcesAssignedToAgent++;
		N++;							// increase district and grid population
		if (vocal)			// let user know
			System.out.println("\tnew resource " + ID + " created on (" + doubleRow + ", " + doubleColumn + ")");
		if (doUpdate) {
			updateMatricesFromData();		// update matrices since new resource just created
		}
		return ID;
	}	// end create

	// ** Move Agent from Anywhere to Agent (i, j) *************************************************
	public boolean move(int resourceID, double row, double column) {
		if (resourceID >= resources.length || resources[resourceID] == null || resources[resourceID].ID != resourceID) {
			System.out.println("\tno such resource"); // check to make sure ID is valid
			return false;
		}
		else
		kill(resourceID, true);			// first kill that resource (ID is pushed into stack)
		create(row, column, true);		// immediately pop that ID to create
		if (vocal)
			System.out.println("\tok");		// let user know if the operation was successful
		return true;					// return for next user input
	}	// end move

	// ** Disturb an Percentage of Resources ********************************************************
	public boolean disturbResources(double percentageOfResourcesToDisturb) {
		int numberOfResourcesToDisturb = (int) (((double) percentageOfResourcesToDisturb / 100) * N);
		// System.out.println("disturbing "+percentageOfAgentsToDisturb+" means to disturb "+numberOfAgentsToDisturb);
		int[] IDsToDisturb = new int[N];
		for (int i = 0; i < numberOfResourcesToDisturb; i++) {
			IDsToDisturb[i] = i;
		}
		for (int i = 0; i < N; i++) {
			int r = i + (int) (disturbanceNumberGenerator.nextDouble() * (N - i));
			int swap = IDsToDisturb[r];
			IDsToDisturb[r] = IDsToDisturb[i];
			IDsToDisturb[i] = swap;
		}
		if (numEdges >= N && numEdges <= numcols * numrows * N) {
			for (int i = 0; i < numberOfResourcesToDisturb; i++) {
				int numberOfConnectionsToAddBack = 0;
				for (int j = 0; j < numrows * numcols; j++) {
					if (connectionMatrix[j][IDsToDisturb[i]] > 0) {
						connectionMatrix[j][IDsToDisturb[i]] = 0;
						assignmentMatrix[j][IDsToDisturb[i]] = 0;
						numberOfConnectionsToAddBack++;
					}
				}
				int[] agentsToReconnect = new int[numrows * numcols];
				for (int j = 0; j < numrows * numcols; j++) {
					agentsToReconnect[j] = j;
				}
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (disturbanceNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = agentsToReconnect[r];
					agentsToReconnect[r] = agentsToReconnect[j];
					agentsToReconnect[j] = swap;
				}
				for (int j = 0; j < numberOfConnectionsToAddBack; j++) {
					connectionMatrix[agentsToReconnect[j]][IDsToDisturb[i]] = 1;
					if (j == 0)
						assignmentMatrix[agentsToReconnect[j]][IDsToDisturb[i]] = 1;
				}
			}
			printMatrices(13);
			printMatrices(14);
			useConnectionInput(IOTempConnectionMatrixFileName1);
			useAssignmentInput(IOTempAssignmentMatrixFileName2);
		} else {
			for (int i = 0; i < numberOfResourcesToDisturb; i++) {
				kill(IDsToDisturb[i], false);
			}
			for (int i = 0; i < numberOfResourcesToDisturb - 1; i++) {
				create(disturbanceNumberGenerator.nextDouble() * (numrows - 1 + 2 * PERIMETER) - PERIMETER, disturbanceNumberGenerator
						.nextDouble()
						* (numcols - 1 + 2 * PERIMETER) - PERIMETER, false);
			}
			create(disturbanceNumberGenerator.nextDouble() * (numrows - 1 + 2 * PERIMETER) - PERIMETER, disturbanceNumberGenerator
					.nextDouble()
					* (numcols - 1 + 2 * PERIMETER) - PERIMETER, true);
		}
		if (showDebug)
			System.out.println("\tok");		// let user know if the operation was successful
		return true;
	}	// end disturbing an amount of resources

	// ** Disturb an Percentage of Agents ********************************************************
	public boolean disturbAgents(double percentageOfAgentsToDisturb) {
		int numberOfAgentsToDisturb = (int) (((double) percentageOfAgentsToDisturb / 100) * numrows * numcols);
		// System.out.println("disturbing "+percentageOfAgentsToDisturb+" means to disturb "+numberOfAgentsToDisturb);
		int[] IDsToDisturb = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			IDsToDisturb[i] = i;
		}
		for (int i = 0; i < numberOfAgentsToDisturb; i++) {
			int r = i + (int) (disturbanceNumberGenerator.nextDouble() * (numrows * numcols - i));
			int swap = IDsToDisturb[r];
			IDsToDisturb[r] = IDsToDisturb[i];
			IDsToDisturb[i] = swap;
		}
		if (showDebug) {
			System.out.print("\trenewing ");
			for (int i = 0; i < numberOfAgentsToDisturb; i++) {
				System.out.print("a" + IDsToDisturb[i] + ", ");
			}
			System.out.println();
		}
		if (numEdges >= N && numEdges <= numcols * numrows * N) // if random edge disturbances
		{
			int numberOfConnectionsToAddBack = 0;
			// kill resources first
			for (int i = 0; i < numberOfAgentsToDisturb; i++) {
				for (int j = 0; j < N; j++) {
					if (connectionMatrix[IDsToDisturb[i]][j] > 0) {
						connectionMatrix[IDsToDisturb[i]][j] = 0;
						numberOfConnectionsToAddBack++;
					}
				}
			}
			int[] connectionsToAddBack = new int[numberOfAgentsToDisturb * N];
			for (int i = 0; i < numberOfAgentsToDisturb * N; i++) {
				connectionsToAddBack[i] = i;
			}
			// check for orphaned resources
			for (int i = 0; i < N; i++) {
				boolean ithResourceIsOrphaned = true;
				for (int j = 0; j < numrows * numcols; j++) {
					if (connectionMatrix[j][i] > 0) {
						ithResourceIsOrphaned = false;
						break;
					}
				}
				if (ithResourceIsOrphaned) {
					int agentToAddBack = (int) (disturbanceNumberGenerator.nextDouble() * numberOfAgentsToDisturb);
					connectionMatrix[IDsToDisturb[agentToAddBack]][i] = 1;
					if (showDebug)
						System.out.println("\t\tresource " + i + " is no longer orphaned by adding a connection to a"
								+ IDsToDisturb[agentToAddBack]);
					numberOfConnectionsToAddBack--;
					// boolean agentWasRenewed = false;
					connectionsToAddBack[agentToAddBack * N + i] = -1;
					// for (int j = 0; j<numberOfAgentsToDisturb; j++)
					// {
					// if (IDsToDisturb[j]==agentToAddBack)
					// {
					// connectionsToAddBack[j*N+i] = -1;
					// break;
					// }
					// }
					// connectionsToAddBack[agentToAddBack*N+i] = -1;
				}
			}
			for (int i = 0; i < numberOfAgentsToDisturb * N; i++) {
				int r = i + (int) (disturbanceNumberGenerator.nextDouble() * (numberOfAgentsToDisturb * N - i));
				int swap = connectionsToAddBack[r];
				connectionsToAddBack[r] = connectionsToAddBack[i];
				connectionsToAddBack[i] = swap;
			}
			// ArrayList<Integer> connectionsToMake = new ArrayList<Integer>();
			int walker = 0;
			for (int i = 0; i < numberOfConnectionsToAddBack; i++) {
				while (connectionsToAddBack[walker++] < 0)
					;
				connectionMatrix[IDsToDisturb[connectionsToAddBack[walker - 1] / N]][connectionsToAddBack[walker - 1] % N] = 1;
				// connectionsToMake.add(connectionsToAddBack[walker-1]);
			}
			// for (int i = 0; i<numberOfConnectionsToAddBack; i++)
			// {
			// int agentToAddBack = connectionsToAddBack[i]/N;
			// int resourceToAddBack = connectionsToAddBack[i]%N;
			// connectionMatrix
			// }
			for (int i = 0; i < N; i++) {
				boolean needToAdjustAssignment = false;
				ArrayList<Integer> agentsToConsider = new ArrayList<Integer>();
				for (int j = 0; j < numrows * numcols; j++) {
					if (connectionMatrix[j][i] == 1) {
						agentsToConsider.add(j);
					}
					if (assignmentMatrix[j][i] == 1 && connectionMatrix[j][i] == 0) {
						assignmentMatrix[j][i] = 0;
						needToAdjustAssignment = true;
					}
				}
				if (needToAdjustAssignment) {
					assignmentMatrix[agentsToConsider.get((int) (disturbanceNumberGenerator.nextDouble() * agentsToConsider.size()))][i] = 1;
				}
			}
			printMatrices(13);
			printMatrices(14);
			useConnectionInput(IOTempConnectionMatrixFileName1);
			useAssignmentInput(IOTempAssignmentMatrixFileName2);
		} else // if local disturbance
		{
			// THIS IS ELLIPSE DISTURBANCE
			for (int i = 0; i < numberOfAgentsToDisturb; i++) {
				agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].firstResource = null;
				agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].numberOfResourcesAssignedToAgent = 0;
				agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].numberOfResourcesConnectedToAgent = 0;
				// double degreeOfOtherLoci = disturbanceNumberGenerator.nextDouble()*Math.PI*2;
				// double degreeOfOtherLoci = Math.PI/2; // 90
				double degreeOfOtherLoci = 0; // 0
				// double degreeOfOtherLoci = Math.PI/4; //45
				double a = Math.sqrt(2) * CONNECTIVITY_RANGE;
				double b = CONNECTIVITY_RANGE / Math.sqrt(2);
				double F = Math.sqrt(Math.pow(a, 2) - Math.pow(b, 2));
				System.out.println("degreeOfLoci = " + degreeOfOtherLoci);
				double rowOfOtherLoci = IDsToDisturb[i] / numcols + Math.sin(degreeOfOtherLoci) * 2 * F;
				double columnOfOtherLoci = IDsToDisturb[i] % numcols + Math.cos(degreeOfOtherLoci) * 2 * F;
				System.out.println("this is (" + IDsToDisturb[i] / numcols + ", " + IDsToDisturb[i] % numcols + ") while other loci is ("
						+ rowOfOtherLoci + ", " + columnOfOtherLoci + ")");
				for (int j = 0; j < N; j++) {
					double distance1 = Math.sqrt(Math.pow(resources[j].row - IDsToDisturb[i] / numcols, 2)
							+ Math.pow(resources[j].column - IDsToDisturb[i] % numcols, 2)); 	// the distance from agent to resource
					double distance2 = Math.sqrt(Math.pow(resources[j].row - rowOfOtherLoci, 2)
							+ Math.pow(resources[j].column - columnOfOtherLoci, 2));					// the distance from other loci to resource
					if (distance1 + distance2 <= 2 * a) {
						resources[j] = new Resource(j, resources[j].row, resources[j].column);
						if (agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].firstResource != null) // modify agent head if a head exists
							agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].firstResource.previousResource = resources[j]; // declare the head's previous
						resources[j].nextResource = agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].firstResource; // declare the new resource's next
						agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].firstResource = resources[j]; // make the resource the new head
						agents[IDsToDisturb[i] / numcols][IDsToDisturb[i] % numcols].numberOfResourcesConnectedToAgent++; // increase agent recourse count
					}
				}
			}

			// THIS IS DENSITY DISTURBANCE
			// for (int i = 0; i<numberOfAgentsToDisturb; i++)
			// {
			// agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].firstResource = null;
			// agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].numberOfResourcesAssignedToAgent = 0;
			// agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].numberOfResourcesConnectedToAgent = 0;
			// double degreeOfOtherLoci = Math.PI/4;
			// double a = Math.sqrt(2)*CONNECTIVITY_RANGE;
			// double b = CONNECTIVITY_RANGE/Math.sqrt(2);
			// double F = Math.sqrt(Math.pow(a, 2)-Math.pow(b, 2));
			// System.out.println("degreeOfLoci = "+degreeOfOtherLoci);
			// double rowOfOtherLoci = IDsToDisturb[i]/numcols+Math.sin(degreeOfOtherLoci)*2*F;
			// double columnOfOtherLoci = IDsToDisturb[i]%numcols+Math.cos(degreeOfOtherLoci)*2*F;
			// System.out.println("this is ("+IDsToDisturb[i]/numcols+", "+IDsToDisturb[i]%numcols+") while other loci is ("+rowOfOtherLoci+", "+columnOfOtherLoci+")");
			// for (int j = 0; j<N; j++)
			// {
			// double distance1 = Math.sqrt(Math.pow(resources[j].row-IDsToDisturb[i]/numcols, 2)+Math.pow(resources[j].column-IDsToDisturb[i]%numcols, 2)); // the distance from
			// agent to resource
			// //double distance2 = Math.sqrt(Math.pow(resources[j].row-rowOfOtherLoci, 2)+Math.pow(resources[j].column-columnOfOtherLoci, 2)); // the distance from other loci to
			// resource
			// if (distance1<=CONNECTIVITY_RANGE&&Math.random()<DENSITY)
			// {
			// resources[j] = new Resource(j, resources[j].row, resources[j].column);
			// if (agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].firstResource!=null) // modify agent head if a head exists
			// agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].firstResource.previousResource = resources[j]; // declare the head's previous
			// resources[j].nextResource = agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].firstResource; // declare the new resource's next
			// agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].firstResource = resources[j]; // make the resource the new head
			// agents[IDsToDisturb[i]/numcols][IDsToDisturb[i]%numcols].numberOfResourcesConnectedToAgent++; // increase agent recourse count
			// }
			// }
			// }

			// NOT SURE WHAT THIS IS
			// for (int i = 0; i<numrows*numcols; i++) // check every agent
			// {
			// Resource resourceWalker = agents[i/numcols][i%numcols].firstResource; // take head of the agent
			// while (resourceWalker!=null&&resourceWalker.ID!=resourceID) // keep walking until ID found or until end
			// resourceWalker = resourceWalker.nextResource;
			// if (resourceWalker==null)
			// continue; // if resource not found then check next agent
			// if (resourceWalker.nextResource!=null) // adjust the next resource's previous resource
			// resourceWalker.nextResource.previousResource = resourceWalker.previousResource;
			// if (resourceWalker.previousResource!=null) // adjust the previous resource's next resource
			// resourceWalker.previousResource.nextResource = resourceWalker.nextResource;
			// else // if (resourceWalker.previousResource==null), then resourceWalker was head
			// agents[i/numcols][i%numcols].firstResource = resourceWalker.nextResource; // for the kill, redeclare the head
			// agents[i/numcols][i%numcols].numberOfResourcesConnectedToAgent--;// = agents[i/numcols][i%numcols].numberOfResourcesConnectedToAgent-1;
			// } // adjust the numberOfResourcesConnectedToAgent
			// deadPool.push(resourceID); // push ID tag into a stack
			// if (!mute) // print if mute is off
			// System.out.println("\t"+resourceID+" is dead");
			// agents[assignmentVector[resourceID]/numcols][assignmentVector[resourceID]%numcols].numberOfResourcesAssignedToAgent--;
			// resources[resourceID].ID = -1; // set tag to signify death
			// N--; // subtract global population after kill
			// if (doUpdate)
			// updateMatricesFromData(); // update the matrices
			// return true;
		}
		updateMatricesFromData();			// update the matrices
		if (vocal)
			System.out.println("\tok");		// let user know if the operation was successful
		return true;
	}	// end disturbing an amount of resources

	// ** Small World *******************************************************************************
	public boolean smallWorld(double percentOfEdges) {
		int actualNumberOfConnections = 0; // initialize number of connections to zero
		int[] connectionsToAddBack = new int[N * numrows * numcols];
		for (int i = 0; i < connectionsToAddBack.length; i++) {
			connectionsToAddBack[i] = i;
		}
		for (int i = 0; i < numrows * numcols; i++) {								// go through the connection matrix and add up the number of connections
			for (int j = 0; j < maxID + 1; j++)
				actualNumberOfConnections += connectionMatrix[i][j];
		}
		int numberOfEdgesToDisturb = (int) (((double) percentOfEdges / 100) * actualNumberOfConnections);
		int[] edgesToDisturb = new int[actualNumberOfConnections];
		for (int i = 0; i < edgesToDisturb.length; i++) {
			edgesToDisturb[i] = i;
		}
		ArrayList<Integer> edgesToModify = new ArrayList<Integer>();
		for (int i = 0; i < numberOfEdgesToDisturb; i++) {
			int r = i + (int) (disturbanceNumberGenerator.nextDouble() * (actualNumberOfConnections - i));
			int swap = edgesToDisturb[r];
			edgesToDisturb[r] = edgesToDisturb[i];
			edgesToDisturb[i] = swap;
			edgesToModify.add(swap);
		}
		int edgeCounter = 0;
		for (int i = 0; i < numrows * numcols; i++) {								// go through the connection matrix and add up the number of connections
			for (int j = 0; j < maxID + 1; j++) {
				if (connectionMatrix[i][j] > 0) {
					if (edgesToModify.contains(edgeCounter)) {
						connectionMatrix[i][j] = 0;
						if (showDebug)
							System.out.println("\tdeleting edge at a" + i + ", r" + j);
					}
					edgeCounter++;
				}
			}
		}
		for (int i = 0; i < N; i++) {
			boolean ithResourceIsOrphaned = true;
			for (int j = 0; j < numrows * numcols; j++) {
				if (connectionMatrix[j][i] > 0) {
					ithResourceIsOrphaned = false;
					break;
				}
			}
			if (ithResourceIsOrphaned) {
				int agentToAddBack = (int) (disturbanceNumberGenerator.nextDouble() * numrows * numcols);
				connectionMatrix[agentToAddBack][i] = 1;
				if (showDebug)
					System.out.println("\t\tresource " + i + " is no longer orphaned by adding a connection to a" + agentToAddBack);
				numberOfEdgesToDisturb--;
			}
		}
		for (int i = 0; i < N * numrows * numcols; i++) {
			if (connectionMatrix[i / N][i % N] > 0)
				connectionsToAddBack[i] = -1;
		}
		for (int i = 0; i < connectionsToAddBack.length; i++) {
			int r = i + (int) (disturbanceNumberGenerator.nextDouble() * (N * numrows * numcols - i));
			int swap = connectionsToAddBack[r];
			connectionsToAddBack[r] = connectionsToAddBack[i];
			connectionsToAddBack[i] = swap;
		}
		int walker = 0;
		for (int i = 0; i < numberOfEdgesToDisturb; i++) {
			while (connectionsToAddBack[walker++] < 0)
				;
			connectionMatrix[connectionsToAddBack[walker - 1] / N][connectionsToAddBack[walker - 1] % N] = 1;
			if (showDebug)
				System.out.println("\tadding edge at a" + connectionsToAddBack[walker - 1] / N + ", r" + connectionsToAddBack[walker - 1]
						% N);
		}
		for (int i = 0; i < N; i++) {
			boolean needToAdjustAssignment = false;
			ArrayList<Integer> agentsToConsider = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (connectionMatrix[j][i] == 1) {
					agentsToConsider.add(j);
				}
				if (assignmentMatrix[j][i] == 1 && connectionMatrix[j][i] == 0) {
					assignmentMatrix[j][i] = 0;
					needToAdjustAssignment = true;
				}
			}
			if (needToAdjustAssignment) {
				assignmentMatrix[agentsToConsider.get((int) (disturbanceNumberGenerator.nextDouble() * agentsToConsider.size()))][i] = 1;
			}
		}
		reallocate();
		printMatrices(13);
		printMatrices(14);
		useConnectionInput(IOTempConnectionMatrixFileName1);
		useAssignmentInput(IOTempAssignmentMatrixFileName2);
		if (vocal)
			System.out.println("\tok");		// let user know if the operation was successful
		return true;
	}	// end smallWolrd

	// ** Reallocate ********************************************************************************
	public void reallocate() {
		assignmentMatrix = new int[numrows * numcols][maxID + 1];
		for (int i = 0; i < maxID + 1; i++) {
			ArrayList<Integer> connectedAgents = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (connectionMatrix[j][i] > 0) {
					connectedAgents.add(j);
				}
			}
			if (connectedAgents.size() == 0)
				assignmentMatrix[(int) (disturbanceNumberGenerator.nextDouble() * maxID + 1)][i] = 1;
			else
				assignmentMatrix[connectedAgents.get((int) (disturbanceNumberGenerator.nextDouble() * connectedAgents.size()))][i] = 1;
		}
	}	// end reallocate

	// ** Algorithm Caller **************************************************************************
	public int algorithmCaller(int algorithmKey) {// "" "MM_RT", "MM_WS", "OA_RT", "OA_WS", "OA_Casc", "SM_2P", "bidding", "CenBIP", "CenBCIP"};
		algorithm = algorithms[algorithmKey];
//		numberOfOffers = 0;
//		numberOfApplies = 0;
		switch (algorithmKey) {
		// algorithms = {"Min-Max", "1Offer-1Accept", "1Apply-1Give", "Stable-Matching", "MultipleOffer-MultipleAccept", "Bidding", "RichDonateToPoor", "PoorRobFromRich", "Centralized Algorithm", "Worst"};
		case 0: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 1: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 2: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 3: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 4: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 5: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 6: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 7: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		case 8: return donateWithFlooding(1, 1, 0, 1, .5, 0);
		
		
//		case 0: return minMax(0);
//		case 1: return oneOfferOneAccept();
//		case 2: return oneApplyOneGive();
//		case 3: return multiOfferMultiAccept(true, 0);
//		case 4: return bidding(true, 1);
//		case 5: return donateWithFlooding(1, 1, 0, 1, 0);
//		case 6: return robWithFlooding(1, 1, 1, 0);
//		case 7: return batchCascadedImprovementPath(true);
//		case 8: return worst();
		
		
//		algorithms = {"1o1a", "1oMa", "Mo1aLar", "Mo1aRic", "MoMaAge", "MoMaRes", "1a1g", "1aMg", "Ma1g", "MaMgAge", "MaMgRes", "BCIP", "worst"};
//		case 0: return oneOfferOneAccept();
//		case 1: return oneOfferMultiAccept();
//		case 2: return multiOfferOneAccept(true);
//		case 3: return multiOfferOneAccept(false);
//		case 4: return multiOfferMultiAccept(false, 0);
//		case 5: return oneApplyOneGive();
//		case 6: return oneApplyMultiGive();
//		case 7: return multiApplyOneGive();
//		case 8: return multiApplyMultiGive(false);
//		case 9: return donateWithFlooding(1, 1, 0, 1, 0);
//		case 10: return robWithFlooding(1, 1, 1, 0);
//		case 11: return batchCascadedImprovementPath(true);
//		case 12: return worst();
		
//		String[] algorithms = {"RmOmAAv", "RmOmA25", "RmOmA50", "RmOmA75", "AmOmAAv", "AmOmA25", "AmOmA50", "AmOmA75", "BCIP", "worst"};

		//		case 0: return multiOfferMultiAccept(true, 0);
//		case 1: return multiOfferMultiAccept(true, 1);
//		case 2: return multiOfferMultiAccept(true, 2);
//		case 3: return multiOfferMultiAccept(false, 0);
//		case 4: return multiOfferMultiAccept(false, 1);
//		case 5: return multiOfferMultiAccept(false, 2);
//		case 6: return batchCascadedImprovementPath(true);
//		case 7: return worst();
		
		
//		case 0: return IPsWithoutSimulation(.25);
//		case 1: return IPsWithoutSimulation(.1875);
//		case 2: return multiOfferOneAccept(true);
//		case 3: return multiOfferMultiAccept(true, 0);
//		case 4: return multiOfferMultiAccept(false, 0);
//		case 5: return takeTurns(5);
//		case 6: return donateWithFlooding(1, 1, 0, 1, 0);
//		case 7: return robWithFlooding(1, 1, 1, 0);
//		case 8: return batchCascadedImprovementPath(true);
//		case 9: return worst();
		
		
//		case 0: return offerAccept(true, 0);
//		case 1: return offerAccept(true, 2);
//		case 2: return stableMatching(true, 2);
//		case 3: return stableMatching(true, 0);
//		case 4: return offerMultiAccept();
//		case 5: return multiOfferAccept();
//		case 6: return multiOfferMultiAccept();
//		case 7: return batchCascadedImprovementPath(true);
//		case 8: return worst();
		
//		case 0: return donateWithFlooding(1, 1, 0, 1, 0);
//		case 1: return donateWithFlooding(1, 1, 0, 1, 0.25);
//		case 2: return donateWithFlooding(1, 1, 0, 1, 0.5);
//		case 3: return donateWithFlooding(1, 1, 0, 1, 0.75);
//		case 4: return batchCascadedImprovementPath(true);
//		case 5: return worst();
		
//		case 0: return robWithFlooding(1, 0, 0, 0);
//		case 1: return robWithFlooding(1, 0, 0, 1);
//		case 2: return robWithFlooding(1, 0, 1, 0);
//		case 3: return robWithFlooding(1, 0, 1, 1);
//		case 4: return robWithFlooding(1, 1, 0, 0);
//		case 5: return robWithFlooding(1, 1, 0, 1);
//		case 6: return robWithFlooding(1, 1, 1, 0);
//		case 7: return robWithFlooding(1, 1, 1, 1);
//		case 8: return batchCascadedImprovementPath(true);
//		case 9: return worst();
		
//		case 0: return IPsWithoutSimulation(1);
//		case 1: return IPsWithoutSimulation(.75);
//		case 2: return IPsWithoutSimulation(.5);
//		case 3: return IPsWithoutSimulation(.4375);
//		case 4: return IPsWithoutSimulation(.375);
//		case 5: return IPsWithoutSimulation(.3125);
//		case 6: return IPsWithoutSimulation(.25);
//		case 7: return IPsWithoutSimulation(.1875);
//		case 8: return IPsWithoutSimulation(.125);
//		case 9: return IPsWithoutSimulation(.03125);
//		case 10: return batchCascadedImprovementPath(true);
//		case 11: return worst();
		
//		case 0: return offerAccept(true, 2);
//		case 1: return stableMatching(true, 2);
//		case 2: return offerMultiAccept();
//		case 3: return offerAcceptMulti();
//		case 4: algorithmBidWhoOrderAndExecution[0] = 0; algorithmBidWhoOrderAndExecution[1] = 5; algorithmWhoWinsOrderAndExecution[0] = 0; algorithmWhoWinsOrderAndExecution[1] = 4; return bidding(true, 1);
//		case 5: algorithmBidWhoOrderAndExecution[0] = 0; algorithmBidWhoOrderAndExecution[1] = 5; algorithmWhoWinsOrderAndExecution[0] = 0; algorithmWhoWinsOrderAndExecution[1] = 4; return bidding(true, 2);
//		case 6: return robWithFlooding(1, 1, 1, 0);
//		case 7: return takeTurns(5);
//		case 8: return donateWithFlooding(3);
//		case 9: return IPsWithoutSimulation(1);
//		case 10: return IPsWithoutSimulation(.5);
//		case 11: return IPsWithoutSimulation(.125);
//		case 12: return IPsWithoutSimulation(.03125);
//		case 13: return IPsWithoutSimulation(.0078125);
//		case 14: return batchImprovementPath(true);
//		case 15: return batchCascadedImprovementPath(true);
//		case 16: return worst();
		default: System.out.println("\terror: no such algorithm found. actual: " + algorithmKey);
		return -1;
		}
	}	// end algorithm caller
	
	// ** Algorithm Caller **************************************************************************
	public int algorithmCallerOld(int algorithmKey) {// "" "MM_RT", "MM_WS", "OA_RT", "OA_WS", "OA_Casc", "SM_2P", "bidding", "CenBIP", "CenBCIP"};
		String[] algorithms = {"lH_WS", "mM_RT", "mM_WS", "OA_RT", "OA_WS", "OA_Casc", "SM_2P", "BidPeak", "BidHigh", "BidMid", "BidLow", "BidRand", "BNIP", "BCIP", "worst"};
		algorithm = algorithms[algorithmKey];
		switch (algorithmKey) {
		case 0: return minMax(2);
		case 1: return minMax(1);
		case 2: return minMax(0);
		case 3: return offerAccept(true, 1);
		case 4: return offerAccept(true, 0);
		case 5: return offerAccept(true, 2);
		case 6: return stableMatching(true, 2);
		case 7: return bidding(true, 0);
		case 8: return bidding(true, 1);
		case 9: return bidding(true, 2);
		case 10: return bidding(true, 3);
		case 11: return bidding(true, 4);
		case 12: return batchImprovementPath(true);
		case 13: return batchCascadedImprovementPath(true);
		case 14: return worst();
		default: System.out.println("\terror: no such algorithm found. actual: " + algorithmKey);
		return -1;
		}
	}	// end algorithm caller old
	
	// ** Update Neighboring Algorithm Step ********************************************************
	public void updateNeighboringAlgorithmStep(int results) {
		if (algorithmStep >= 7 * stepScale)
			return;							// return to prevent adding elements to array list
		if (results > 0 && algorithmStep + batchScale < 7 * stepScale)
			algorithmStep += batchScale; 	// only increment if it is not hitting the glass ceiling
		if (results == 0) 					// if execution was false, then all improvements were already done
			algorithmStep = Math.max(algorithmStep, 7 * stepScale);
	}
	
	// ** Update Cascaded Algorithm Step ***********************************************************
	public void updateCascadedAlgorithmStep(int results) {
		if (algorithmStep >= 9 * stepScale)
			return;							// return to prevent adding elements to array list
		if (algorithmStep + batchScale < 9 * stepScale)
			algorithmStep += batchScale;	// only increment if it is not hitting the glass ceiling
		if (cascadedDecentralizedIsDone >= 2) 	// if execution was false, thesn all improvements were already done
			algorithmStep = Math.max(algorithmStep, 9 * stepScale); 
	}

	// ** Is Neighboring Improvement Paths Present *************************************************
	public boolean isNeighboringImprovementPathsPresent() {
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				if (reachMatrix[i][j] > 0
						&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
								- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2)
					return true;
			}
		}
		return false;
	} 	// end Is Neighboring Improvement Paths Present

	// ** Is Cascaded Improvement Paths Present ****************************************************
	public boolean isCascadedImprovementPathsPresent(int[][] reachMatrixStack, int recursionCounter, boolean improvement) { // catch the current stack and initially 0
		if (recursionCounter > numrows * numcols) // if got to this point, then check if recursion should end
			return false;
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		if (shuffle) {
			for (int i = 0; i < numrows * numcols; i++) {
				int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
				int swap = randomAgentsArray[r];
				randomAgentsArray[r] = randomAgentsArray[i];
				randomAgentsArray[i] = swap;
			}
		}
		for (int i = 0; i < numrows * numcols; i++) // if got to this point, try to transfer resources using the stack
		{
			int[] randomAgentsArray2 = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray2[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray2[r];
					randomAgentsArray2[r] = randomAgentsArray2[j];
					randomAgentsArray2[j] = swap;
				}
			}
			for (int j = 0; j < numrows * numcols; j++) {
				boolean checkDifference1;
				if (improvement)
					checkDifference1 = (reachMatrixStack[randomAgentsArray[i]][randomAgentsArray2[j]] > 0 && agents[randomAgentsArray[i]
							/ numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
							- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 2);
				else
					checkDifference1 = (reachMatrixStack[randomAgentsArray[i]][randomAgentsArray2[j]] > 0 && agents[randomAgentsArray[i]
							/ numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
							- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent <= 0);
				if (checkDifference1) { // if there is a path and a difference of 2
					if (showDebug)
						System.out
								.println("\ta resource can be transferred from A"
										+ randomAgentsArray[i]
										+ ":"
										+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
										+ "/"
										+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent
										+ " to A"
										+ randomAgentsArray2[j]
										+ ":"
										+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent
										+ "/"
										+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesConnectedToAgent
										+ " in " + (recursionCounter + 1) + " moves via A"
										+ agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]);
					return true;
				}
			}
		} // if got to this point, begin the matrix multiplication
		int[][] reachMatrixProduct = new int[numrows * numcols][numrows * numcols];
		boolean reachMatrixStackChanged = false;
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				agentPathList[i][j] = -1; // set -1 to not be confused with resource 0
				ArrayList<Integer> possibleIntermediateAgents = new ArrayList<Integer>(); // used to not move resource twice
				for (int k = 0; k < numrows * numcols; k++) { // just use the max of 1
					if (reachMatrixStack[i][k] * reachMatrix[k][j] == 1 && reachMatrixProduct[i][j] == 0)
						reachMatrixStackChanged = true;
					reachMatrixProduct[i][j] = Math.max(reachMatrixProduct[i][j], reachMatrixStack[i][k] * reachMatrix[k][j]);
					if (reachMatrixProduct[i][j] == 1) {
						possibleIntermediateAgents.add(k); // move onto the next element if the already set
					} // and if an agent can be the intermediate
				}
				if (possibleIntermediateAgents.size() > 0)
					agentPathList[i][j] = possibleIntermediateAgents.get((int) (possibleIntermediateAgents.size() * randomNumberGenerator
							.nextDouble()));
			}
		}
		if (reachMatrixStackChanged)
			return isCascadedImprovementPathsPresent(reachMatrixProduct, recursionCounter + 1, improvement);
		else
			return false;
	} 	// end Is Cascaded Improvement Paths Present
	
	// ** takeTurns ********************************************************************************
	public int takeTurns(int version) { // 0 = recursive IP, 1 = single IP, 2 = whole_sale, 3 = improved phase based on independent sets,
		// 4 = rob & donate with imp phase; 5 = police others
		int returnValue = 0;
		numberOfPhases = 0;
		int[] resourcesMarkedToDonate = new int[N];	// new destination of each resource
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		if (shuffle) {
			for (int i = 0; i < numrows * numcols; i++) {
				int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
				int swap = randomAgentsArray[r];
				randomAgentsArray[r] = randomAgentsArray[i];
				randomAgentsArray[i] = swap;
			}
		}
		int[] agentsPhase = new int[numrows * numcols];
		for (int i = 0; i < agentsPhase.length; i++) {
			agentsPhase[i] = -1;
		}
		if (version >= 3) {
			if (!isAgentsTurnOrderSet) {
				takeTurnPhases = 0;
				agentsTakeTurnsColor = new ArrayList<String>();
				while (true) {
					int[] agentsRemainingToConsider = new int[numrows * numcols];
					for (int i = 0; i < agentsRemainingToConsider.length; i++) {
						agentsRemainingToConsider[i] = 1;
					}
					boolean isDone = true;
					if (version == 3 || version == 4 || version == 5) {
						for (int i = 0; i < numrows * numcols; i++) {
							if (agentsPhase[randomAgentsArray[i]] < 0 && agentsRemainingToConsider[randomAgentsArray[i]] == 1) {
								agentsPhase[randomAgentsArray[i]] = takeTurnPhases;
								isDone = false;
								for (int j = 0; j < agentsRemainingToConsider.length; j++) {
									if (adjacencyMatrix[randomAgentsArray[i]][randomAgentsArray[j]] > 0) {
										agentsRemainingToConsider[randomAgentsArray[j]] = 0;
									}
								}
							}
						}
					} else if (true) {
						// compute number of neighboring agents
						int[] agentsNumberOfNeighboringAgents = new int[numrows * numcols];
						for (int i = 0; i < numrows * numcols; i++) {
							agentsNumberOfNeighboringAgents[i] = numberOfNeighboringAgents(i);
						}
						while (true) {
							ArrayList<Integer> agentsWithMostNeighboringAgents = new ArrayList<Integer>();
							int mostNumberOfNeighbors = -1;
							for (int i = 0; i < numrows * numcols; i++) { // if agents not already assigne a phase, if still should be consider if not neighboring
								if (agentsPhase[randomAgentsArray[i]] < 0 && agentsRemainingToConsider[randomAgentsArray[i]] == 1 && agentsNumberOfNeighboringAgents[randomAgentsArray[i]] >= mostNumberOfNeighbors) {
									if (agentsNumberOfNeighboringAgents[randomAgentsArray[i]] > mostNumberOfNeighbors) {
										mostNumberOfNeighbors = agentsNumberOfNeighboringAgents[randomAgentsArray[i]];
										agentsWithMostNeighboringAgents = new ArrayList<Integer>();
									}
									agentsWithMostNeighboringAgents.add(randomAgentsArray[i]);
								}
							}
							if (agentsWithMostNeighboringAgents.size() == 0) {
								break;
							}
							// select a random agent to go on this phase
							int agentWithMostNeighboringAgents = agentsWithMostNeighboringAgents.get((int) (randomNumberGenerator.nextDouble() * agentsWithMostNeighboringAgents.size()));
							agentsPhase[agentWithMostNeighboringAgents] = takeTurnPhases;
							isDone = false;
							for (int j = 0; j < agentsRemainingToConsider.length; j++) {
								if (adjacencyMatrix[agentWithMostNeighboringAgents][randomAgentsArray[j]] > 0) {
									agentsRemainingToConsider[j] = 0;
								}
							}
						}
					}
					if (isDone) {
						break;
					}
					// after all the agents that can go on this phase are selected, then move to the next phase
					takeTurnPhases++;
					agentsTakeTurnsColor.add("\"#" + Long.toHexString((long) (Math.random() * 16777216)) + "\"");
					takeTurnPhaseCounter = 0;
				}
				isAgentsTurnOrderSet = true;
				agentsTurnOrder = agentsPhase;
				if (showDebug) {
					println("debugging taking turns with " + takeTurnPhases + " phases");
					for (int i = 0; i<numrows*numcols; i++) {
						println("A" + i + ": phase " + agentsPhase[i]);
					}
				}
			}
			numberOfPhases = 2;
			int[] unrandomAgentArray = array(numrows * numcols);
			for (int i = 0; i < N; i++) {
				resourcesMarkedToDonate[i] = -1;
			}
//			for (int i = 0; i < takeTurnPhases; i++) {
			if (showDebug) {
				println("debugging taking turns, takeTurnPhases = " + takeTurnPhases + "; takeTurnPhaseCounter = " + takeTurnPhaseCounter + "; lastTakeTurnPhaseWithModification = " + lastTakeTurnPhaseWithModification);
			}
				for (int j = 0; j < agentsTurnOrder.length; j++) {
					if (agentsTurnOrder[j] == takeTurnPhaseCounter) {
						if (showDebug) {
							System.out.println("A" + j + " is taking its turn on phase " + takeTurnPhaseCounter);
						}
						returnValue += version <= 3? takeFromNeighbor(j, unrandomAgentArray, version) : robAndDonateFromNeighbor(j, unrandomAgentArray, resourcesMarkedToDonate, version);
					}
				}
//				System.out.println("returnValue = " + returnValue + ", numberOfDifferences = " + numberOfDifferences);
//			}
		} else {	// version is 0 through 2
			for (int numTakes = numrows * numcols; numTakes > 0; ) {
				numberOfPhases++;
				for (int i = 0; i < randomAgentsArray.length; i++) {
					if (agentsPhase[randomAgentsArray[i]] >= 0) {
						continue;
					}
					boolean isAgentIsTurn = true;
					for (int j = 0; j < randomAgentsArray.length; j++) {
						if (adjacencyMatrix[randomAgentsArray[i]][randomAgentsArray[j]] > 0 && j < i && (agentsPhase[randomAgentsArray[j]] < 0 || agentsPhase[randomAgentsArray[j]] == numberOfPhases)) {
							isAgentIsTurn = false;
							break;
						}
					}
					if (isAgentIsTurn) {
						if (showDebug) {
							System.out.println("A" + randomAgentsArray[i] + " is taking its turn on phase " + numberOfPhases);
						}
						returnValue += version <= 4? takeFromNeighbor(i, randomAgentsArray, version) : robAndDonateFromNeighbor(i, randomAgentsArray, resourcesMarkedToDonate, version);
						agentsPhase[randomAgentsArray[i]] = numberOfPhases;
						numTakes--;
					}
				}
				if (numTakes <= 0) {
					break;
				}
				numberOfPhases++;
			}
		}
//		System.out.println("phase = " + numberOfPhases);
//		for (boolean isDone = false; !isDone; ) {
//			isDone = true;
//			numberOfPhases++;
//			numberOfCommunications += numberOfOnes(connectionMatrix);
//			for (int i = 0; i < numrows * numcols; i++) {
//				if (!takeFromNeighbor(i, randomAgentsArray, version)) {
//					isDone = false;
//				}
//			}
//		}
		useAssignmentMatrix();
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2 * numberOfPhases;
		if (returnValue > 0) {
			lastTakeTurnPhaseWithModification = takeTurnPhaseCounter;
		}
		if (showDebug) {
			println("taking turns finished at " + numberOfPhases + " phases");
		}
		takeTurnPhaseCounter++;
		if (takeTurnPhaseCounter == takeTurnPhases) {
			takeTurnPhaseCounter = 0;
		}
		if (returnValue == 0) {
			if (lastTakeTurnPhaseWithModification == -1 && takeTurnPhaseCounter == takeTurnPhases - 1) { // no improvement
				isAgentsTurnOrderSet = false;
				return 0;
			}
			if (takeTurnPhaseCounter == lastTakeTurnPhaseWithModification) {
				isAgentsTurnOrderSet = false;
				lastTakeTurnPhaseWithModification = -1;
				return 0;
			}
			return -1;
		}
		return returnValue;
	}
	
	public int takeFromNeighbor(int i, int[] randomAgentsArray, int version) { // 0 = recursive IP, 1 = single IP, 2 = whole_sale, 3 = improved phase
		int returnValue = 0;
		Resource resourceWalker = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].firstResource;
		ArrayList<Integer> possibleResources = new ArrayList<Integer>();
		boolean isFreeResourcesAvailable = false;
		while (resourceWalker != null)   // from i to j
		{					// keep walking until valid resource
			int k = resourceWalker.ID;
			if (assignmentVector[k] != randomAgentsArray[i] && connectionMatrix[randomAgentsArray[i]][k] > 0)// checkContains(k,i/numcols,i%numcols)&&checkContains(k,j/numcols,j%numcols))
			{
				if (!isFreeResourcesAvailable && assignmentVector[k] < 0) {
					isFreeResourcesAvailable = true;
					possibleResources = new ArrayList<Integer>();
				}
				if (!isFreeResourcesAvailable || assignmentVector[k] < 0) {
					possibleResources.add(k);
				}
			}
			resourceWalker = resourceWalker.nextResource;
		}
		int[] randomResourcesArray = new int[possibleResources.size()];
		for (int k = 0; k < randomResourcesArray.length; k++) {
			randomResourcesArray[k] = possibleResources.get(k);
		}
		if (shuffle) {
			for (int k = 0; k < randomResourcesArray.length; k++) {
				int r = k + (int) (randomNumberGenerator.nextDouble() * (randomResourcesArray.length - k));
				int swap = randomResourcesArray[r];
				randomResourcesArray[r] = randomResourcesArray[k];
				randomResourcesArray[k] = swap;
			}
		}
		for (int k = 0; k < randomResourcesArray.length; k++) {
			boolean isFreeResource = assignmentVector[randomResourcesArray[k]] < 0;
			boolean isImprovementPath = false;
			if (!isFreeResource) {
				isImprovementPath = agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesAssignedToAgent - agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent >= 2;
				if (showDebug) {
					System.out.println("comparing A"+assignmentVector[randomResourcesArray[k]] + " with " + agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesAssignedToAgent + " agents against A" + randomAgentsArray[i] + " with " + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent + " agents is " + isImprovementPath);
			
				}
			}
			if (isFreeResource || isImprovementPath) {
				if (showDebug) {
					if (isFreeResource) {
						System.out.println("\tA" + randomAgentsArray[i] + ":" + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent +" grabbed R" + randomResourcesArray[k]);
					} else {
						System.out.println("\tA" + randomAgentsArray[i] + ":" + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent +" grabbed R" + randomResourcesArray[k] + " from A" + assignmentVector[randomResourcesArray[k]] + ":" + agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesConnectedToAgent);
					}
				}
				returnValue++;
				agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent++;
				assignmentMatrix[randomAgentsArray[i]][randomResourcesArray[k]] = 1;
				int previousOwner = assignmentVector[randomResourcesArray[k]];
				assignmentVector[randomResourcesArray[k]] = randomAgentsArray[i];
				if (isImprovementPath) {
					agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent--;
					assignmentMatrix[previousOwner][randomResourcesArray[k]] = 0;
					if (version == 0) {
						for (int l = 0; l < randomAgentsArray.length; l++) {
							if (randomAgentsArray[l] == previousOwner) {
								returnValue += takeFromNeighbor(l, randomAgentsArray, version);
								break;
							}
						}
					}
				}
			}
		}
		return returnValue;
	}
	
	public int robAndDonateFromNeighbor(int i, int[] randomAgentsArray, int[] resourcesMarkedToDonate, int version) { // 0 = recursive IP, 1 = single IP, 2 = whole_sale, 3 = improved phase
		int returnValue = 0;
		Resource resourceWalker = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].firstResource;
		ArrayList<Integer> possibleResources = new ArrayList<Integer>();
		while (resourceWalker != null)   // from i to j
		{					// keep walking until valid resource
			int k = resourceWalker.ID;
			if (connectionMatrix[randomAgentsArray[i]][k] > 0) {//assignmentVector[k] != randomAgentsArray[i] &&// checkContains(k,i/numcols,i%numcols)&&checkContains(k,j/numcols,j%numcols))
				possibleResources.add(k);
			}
			resourceWalker = resourceWalker.nextResource;
		}
		int[] randomResourcesArray = new int[possibleResources.size()];
		for (int k = 0; k < randomResourcesArray.length; k++) {
			randomResourcesArray[k] = possibleResources.get(k);
		}
		if (showDebug) {
			print("R ");
			for (int k = 0; k < randomResourcesArray.length; k++) {
				print(randomResourcesArray[k] + " ");
			}
			println();
		}
		if (shuffle) {
			for (int k = 0; k < randomResourcesArray.length; k++) {
				int r = k + (int) (randomNumberGenerator.nextDouble() * (randomResourcesArray.length - k));
				int swap = randomResourcesArray[r];
				randomResourcesArray[r] = randomResourcesArray[k];
				randomResourcesArray[k] = swap;
			}
		}
		int targetedNumberOfResources = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent;
		ArrayList<int[]> agentsToDonateTo = new ArrayList<int[]>();
		for (int j = 0; j < numrows * numcols; j++) {
			if (adjacencyMatrix[i][j] > 0 && (version >=5 || i != j)) {
				for (int k = 0; k < randomResourcesArray.length; k++) {
					if (connectionMatrix[j][randomResourcesArray[k]] > 0) {
						int[] intArrayToAdd = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
						agentsToDonateTo.add(intArrayToAdd);
						break;
					}
				}
			}
		}
		if (showDebug) {
			print("A");
			for (int j = 0; j < agentsToDonateTo.size(); j++) {
				print(agentsToDonateTo.get(j)[0] + " ");
			}
			println();
		}
		for (boolean anImprovementHasoccured = true; anImprovementHasoccured; ) {
			anImprovementHasoccured = false;
			Collections.shuffle(agentsToDonateTo, randomNumberGenerator);
			Collections.sort(agentsToDonateTo, new Comparator<int[]>() {
				@Override
				public int compare(int[] arg0, int[] arg1) {
					return arg0[1] - arg1[1];
				}
			});
			for (int j = 0; j < agentsToDonateTo.size(); j++) {
				boolean breakOutOfOutterLoop = false;
				for (int k = 0; k < randomResourcesArray.length; k++) {
					// without policing
					if (version == 4) {
					if (targetedNumberOfResources - agentsToDonateTo.get(j)[1] >= 2 && connectionMatrix[agentsToDonateTo.get(j)[0]][randomResourcesArray[k]] > 0 && assignmentMatrix[randomAgentsArray[i]][randomResourcesArray[k]] > 0) {
						if (showDebug) {
							System.out.println("\tA" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent +" will donate R" + randomResourcesArray[k] + " to A" + agentsToDonateTo.get(j)[0] + ":" + agentsToDonateTo.get(j)[1] + "/" + agents[agentsToDonateTo.get(j)[0] / numcols][agentsToDonateTo.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
						}
						targetedNumberOfResources--;
						int[] intArrayToReplace = {agentsToDonateTo.get(j)[0], agentsToDonateTo.get(j)[1] + 1};
						agentsToDonateTo.set(j, intArrayToReplace);
						assignmentMatrix[randomAgentsArray[i]][randomResourcesArray[k]] = 0;
						assignmentMatrix[agentsToDonateTo.get(j)[0]][randomResourcesArray[k]] = 1;
						anImprovementHasoccured = true;
						breakOutOfOutterLoop = true;
						break;
					}
					if (agentsToDonateTo.get(j)[1] - targetedNumberOfResources >= 2 && connectionMatrix[randomAgentsArray[i]][randomResourcesArray[k]] > 0 && assignmentMatrix[agentsToDonateTo.get(j)[0]][randomResourcesArray[k]] > 0) {
						if (showDebug) {
							System.out.println("\tA" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent +" will rob R" + randomResourcesArray[k] + " from A" + agentsToDonateTo.get(j)[0] + ":" + agentsToDonateTo.get(j)[1] + "/" + agents[agentsToDonateTo.get(j)[0] / numcols][agentsToDonateTo.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
						}
						targetedNumberOfResources++;
						int[] intArrayToReplace = {agentsToDonateTo.get(j)[0], agentsToDonateTo.get(j)[1] - 1};
						agentsToDonateTo.set(j, intArrayToReplace);
						assignmentMatrix[randomAgentsArray[i]][randomResourcesArray[k]] = 1;
						assignmentMatrix[agentsToDonateTo.get(j)[0]][randomResourcesArray[k]] = 0;
						anImprovementHasoccured = true;
						breakOutOfOutterLoop = true;
						break;
					}
					} else { // if version == 5 with policing, have to find the resource owner, find what this agent thinks its original own have, and see if it should transfer
						
						int indexOfAgentOfCurrentOwner = -1;
//						boolean isDonori = false;
						for (int l = 0; l < agentsToDonateTo.size(); l++) {
							if (agentsToDonateTo.get(l)[0] == assignmentVector[randomResourcesArray[k]]) {
								indexOfAgentOfCurrentOwner = l;
							}
						}
						if (indexOfAgentOfCurrentOwner < 0) {
//							indexOfAgentOfCurrentOwner = agentsToDonateTo.size();
//							int[] intArrayToReplace = {i, targetedNumberOfResources};
//							agentsToDonateTo.add(intArrayToReplace);
//							isDonori = true;
							println("something is wrong. the agent should be able to detect the current owner"); 
							println("the list of agents is ");
							print("A");
							for (int l = 0; l < agentsToDonateTo.size(); l++) {
								print(agentsToDonateTo.get(l)[0] + " ");
							}
							println();
							println("while the actual owner is " + assignmentVector[randomResourcesArray[k]]);
						}
						if (agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[1] - agentsToDonateTo.get(j)[1] >= 2 && connectionMatrix[agentsToDonateTo.get(j)[0]][randomResourcesArray[k]] > 0 && assignmentMatrix[agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[0]][randomResourcesArray[k]] > 0) {
							if (showDebug) { 
								System.out.println("\tA" + agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[0] + ":" + agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[1] + "/" + agents[agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[0] / numcols][agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[0] % numcols].numberOfResourcesConnectedToAgent +" will donate R" + randomResourcesArray[k] + " to A" + agentsToDonateTo.get(j)[0] + ":" + agentsToDonateTo.get(j)[1] + "/" + agents[agentsToDonateTo.get(j)[0] / numcols][agentsToDonateTo.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
							}
							int[] intArrayToReplace = {agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[0], agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[1] - 1};
							agentsToDonateTo.set(indexOfAgentOfCurrentOwner, intArrayToReplace);
							int[] intArrayToReplace2 = {agentsToDonateTo.get(j)[0], agentsToDonateTo.get(j)[1] + 1};
							agentsToDonateTo.set(j, intArrayToReplace2);
							assignmentMatrix[agentsToDonateTo.get(indexOfAgentOfCurrentOwner)[0]][randomResourcesArray[k]] = 0;
							assignmentMatrix[agentsToDonateTo.get(j)[0]][randomResourcesArray[k]] = 1;
							assignmentVector[randomResourcesArray[k]] = agentsToDonateTo.get(j)[0];
							anImprovementHasoccured = true;
							breakOutOfOutterLoop = true;
//							if (isDonori) {
//								targetedNumberOfResources--;
//								agentsToDonateTo.remove(agentsToDonateTo.size() - 1);
//							}
							break;
						}
					}
				}
				if (breakOutOfOutterLoop) {
					returnValue++;
					break;
				}
			}
		}
		return returnValue;
//			
//			
//			for (int k = 0; k < randomResourcesArray.length; k++) {
//				boolean isFreeResource = assignmentVector[randomResourcesArray[k]] < 0;
//				boolean isImprovementPath = false;
//				boolean isAntiImprovementPath = false;
//				int poorestNeighboringAgentIfAntiImprovementPath = -1;
//				if (!isFreeResource) {
//					isImprovementPath = agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesAssignedToAgent - targetedNumberOfResources >= 2;
//					if (showDebug) {
//						System.out.println("comparing A" + assignmentVector[randomResourcesArray[k]] + " with " + agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesAssignedToAgent + " agents against A" + randomAgentsArray[i] + " with " + targetedNumberOfResources + " agents is " + isImprovementPath);
//					}
//				}
//				if (!isFreeResource && !isImprovementPath && assignmentVector[randomResourcesArray[k]] == randomAgentsArray[i]) {
//					int lowestAmountOfAssignedResources = N + 1;
//					ArrayList<Integer> agentsWithLowestAmountOfAssignedResources = new ArrayList<Integer>();
//					for (int j = 0; j < numrows * numcols; j++) {
//						if (connectionMatrix[j][randomResourcesArray[k]] > 0 && randomAgentsArray[i] != j) {
//							if (agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent < lowestAmountOfAssignedResources) {
//								agentsWithLowestAmountOfAssignedResources = new ArrayList<Integer>();
//								lowestAmountOfAssignedResources = agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent;
//							}
//							if (agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent == lowestAmountOfAssignedResources) {
//								agentsWithLowestAmountOfAssignedResources.add(j);
//							}
//						}
//					}
//					if (agentsWithLowestAmountOfAssignedResources.size() > 0) {
//						poorestNeighboringAgentIfAntiImprovementPath = agentsWithLowestAmountOfAssignedResources.get((int) (randomNumberGenerator.nextDouble() * agentsWithLowestAmountOfAssignedResources.size()));
//						isAntiImprovementPath = targetedNumberOfResources - agents[poorestNeighboringAgentIfAntiImprovementPath / numcols][poorestNeighboringAgentIfAntiImprovementPath % numcols].numberOfResourcesAssignedToAgent >= 2;
//					}
//				}
//				if (isFreeResource || isImprovementPath || isAntiImprovementPath) {
//					anImprovementHasoccured = true;
//					if (showDebug) {
//						if (isFreeResource) {
//							System.out.println("\tA" + randomAgentsArray[i] + ":" + targetedNumberOfResources + "/" + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent +" grabbed R" + randomResourcesArray[k]);
//						} else if (isImprovementPath) {
//							System.out.println("\tA" + randomAgentsArray[i] + ":" + targetedNumberOfResources + "/" + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent +" robbed R" + randomResourcesArray[k] + " from A" + assignmentVector[randomResourcesArray[k]] + ":" + agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[assignmentVector[randomResourcesArray[k]] / numcols][assignmentVector[randomResourcesArray[k]] % numcols].numberOfResourcesConnectedToAgent);
//						} else {
//							System.out.println("\tA" + randomAgentsArray[i] + ":" + targetedNumberOfResources + "/" + agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent +" donated R" + randomResourcesArray[k] + " to A" + poorestNeighboringAgentIfAntiImprovementPath + ":" + agents[poorestNeighboringAgentIfAntiImprovementPath / numcols][poorestNeighboringAgentIfAntiImprovementPath % numcols].numberOfResourcesAssignedToAgent + "/" + agents[poorestNeighboringAgentIfAntiImprovementPath / numcols][poorestNeighboringAgentIfAntiImprovementPath % numcols].numberOfResourcesConnectedToAgent);
//						}
//					}
//					returnValue++;
//					int previousOwner = isImprovementPath ? assignmentVector[randomResourcesArray[k]] : randomAgentsArray[i];
//					int newOwner = isImprovementPath ? randomAgentsArray[i] : poorestNeighboringAgentIfAntiImprovementPath;
//
//					agents[newOwner / numcols][newOwner % numcols].numberOfResourcesAssignedToAgent++;
//					assignmentMatrix[newOwner][randomResourcesArray[k]] = 1;
//					assignmentVector[randomResourcesArray[k]] = newOwner;
//					if (isImprovementPath || isAntiImprovementPath) {
//						agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent--;
//						assignmentMatrix[previousOwner][randomResourcesArray[k]] = 0;
//					}
//				}
//			}
//		}
	}
	
	public int worst() {
		numberOfPhases = 0;
		numberOfCommunications = 0;
		assignmentMatrix = new int[numrows * numcols][N];
		boolean[] isResourceConnected = new boolean[N];
		for (int i = 0; i < N; i++) {
			isResourceConnected[i] = false;
		}
		int maxNumberOfConnections = 0;
		do {
			numberOfPhases++;
			maxNumberOfConnections = 0;
			ArrayList<Integer> agentsThatCanMakeMostConnections = new ArrayList<Integer>();
			for (int i = 0; i < numrows * numcols; i++) {
				int agentNumberOfConnections = 0;
				for (int j = 0; j < N; j++) {
					if (!isResourceConnected[j] && connectionMatrix[i][j] > 0) {
						agentNumberOfConnections++;
					}
				}
				numberOfCommunications += agentNumberOfConnections;
				if (agentNumberOfConnections == maxNumberOfConnections) {
					agentsThatCanMakeMostConnections.add(i);
				}
				if (agentNumberOfConnections > maxNumberOfConnections) {
					maxNumberOfConnections = agentNumberOfConnections;
					agentsThatCanMakeMostConnections = new ArrayList<Integer>();
					agentsThatCanMakeMostConnections.add(i);
				}
			}
			int agentPicked = agentsThatCanMakeMostConnections.get((int) (Math.random() * agentsThatCanMakeMostConnections.size()));
			for (int i = 0; i < N; i++) {
				if (!isResourceConnected[i] && connectionMatrix[agentPicked][i] > 0) {
					isResourceConnected[i] = true;
					assignmentMatrix[agentPicked][i] = 1;
				}
			}
		} while (maxNumberOfConnections != 0);
//		for (int i = 0; i<numrows*numcols; i++) {
//			for (int j = 0; j<N; j++) {
//				System.out.print(assignmentMatrix[i][j]);
//			}
//			System.out.println();
//		}
		useAssignmentMatrix();
//		for (int i = 0; i<numrows*numcols; i++) {
//			System.out.print(agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent + ", ");
//		}
		return 0;
	}

	// ** Improvement Path (Slow) *******************************************************************
	public int improvementPath(boolean improvement) {
		numberOfPhases = 1;
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		if (shuffle) {
			for (int i = 0; i < numrows * numcols; i++) {
				int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
				int swap = randomAgentsArray[r];
				randomAgentsArray[r] = randomAgentsArray[i];
				randomAgentsArray[i] = swap;
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				if (i == j) {
					continue;
				}
				boolean checkDifference1; // check if not the same agent and if a resource should be transfered
				if (improvement)
					checkDifference1 = reachMatrix[randomAgentsArray[i]][randomAgentsArray[j]] > 0
							&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
									- agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent >= 2;
				else
					checkDifference1 = reachMatrix[randomAgentsArray[i]][randomAgentsArray[j]] > 0
							&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
									- agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent <= 0;
				if (checkDifference1)// reachMatrix[i][j]>0&&agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent-agents[j/numcols][j%numcols].numberOfResourcesAssignedToAgent>=2)
				{						// if there is a path and a difference of 2
					Resource resourceWalker = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)   // from i to j
					{					// keep walking until valid resource
						int k = resourceWalker.ID;
						if (assignmentMatrix[randomAgentsArray[i]][k] > 0 && connectionMatrix[randomAgentsArray[j]][k] > 0)// checkContains(k,i/numcols,i%numcols)&&checkContains(k,j/numcols,j%numcols))
						{				// if a resource can be passed
							// from agents[i/numcols][i%numcols] to agents[j/numcols][j%numcols]
							// through resource[k]
							possibleResources.add(k);
						}
						resourceWalker = resourceWalker.nextResource;
					}
					int k = possibleResources.get((int) (possibleResources.size() * randomNumberGenerator.nextDouble()));
					assignmentMatrix[randomAgentsArray[j]][k] = 1;
					assignmentMatrix[randomAgentsArray[i]][k] = 0;
					if (showDebug)
						System.out.println("\tR" + k + " reassigned from A" + randomAgentsArray[i] + ":"
								+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
								+ "/"
								+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent
								+ " to A" + randomAgentsArray[j] + ":"
								+ agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent
								+ "/"
								+ agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesConnectedToAgent);
					useAssignmentMatrix(); // use assignment matrix to later update all matrices
					return 1;
				}
			}
		}								// if not difference of 2 are found
		return 0; 					// then return false
	} 	// end improvement path
	
	public int fastIncompleteDeclinationPath(int CEILING) {
		numberOfPhases = 1;
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
			int swap = randomAgentsArray[r];
			randomAgentsArray[r] = randomAgentsArray[i];
			randomAgentsArray[i] = swap;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				if (i == j || agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent + 1 > CEILING || agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent - agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent >= 1) {
					continue;
				}
				//System.out.println(agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent + 1);
				ArrayList<Integer> possibleResourcesToTransfer = new ArrayList<Integer>();
				for (int k = 0; k < N; k++) {
					if (assignmentMatrix[randomAgentsArray[i]][k] > 0 && connectionMatrix[randomAgentsArray[j]][k] > 0) {
						possibleResourcesToTransfer.add(k);
					}
				}
				if (possibleResourcesToTransfer.size() == 0) {
					continue;
				}
				int k = possibleResourcesToTransfer.get((int) (possibleResourcesToTransfer.size() * randomNumberGenerator.nextDouble()));
				assignmentMatrix[randomAgentsArray[i]][k] = 0;
				assignmentMatrix[randomAgentsArray[j]][k] = 1;
				agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent--;
				agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent++;
				return 1;
			}
		}								// if not difference of 2 are found
		System.out.println("\tno more declination paths within ceiling");
		return fastIncompleteImprovementPath(false); 					// then return false
	}
	
	// ** Improvement Path (Slow) *******************************************************************
	public int fastIncompleteImprovementPath(boolean improvement) {
		numberOfPhases = 1;
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
			int swap = randomAgentsArray[r];
			randomAgentsArray[r] = randomAgentsArray[i];
			randomAgentsArray[i] = swap;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				if (i == j) {
					continue;
				}
				boolean checkDifference1; // check if not the same agent and if a resource should be transfered
				if (improvement)
					checkDifference1 = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent - agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent >= 2;
				else
					checkDifference1 = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent - agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent <= 0;
				if (!checkDifference1) {
					continue;
				}
				ArrayList<Integer> possibleResourcesToTransfer = new ArrayList<Integer>();
				for (int k = 0; k < N; k++) {
					if (assignmentMatrix[randomAgentsArray[i]][k] > 0 && connectionMatrix[randomAgentsArray[j]][k] > 0) {
						possibleResourcesToTransfer.add(k);
					}
				}
				if (possibleResourcesToTransfer.size() == 0) {
					continue;
				}
				int k = possibleResourcesToTransfer.get((int) (possibleResourcesToTransfer.size() * randomNumberGenerator.nextDouble()));
				assignmentMatrix[randomAgentsArray[i]][k] = 0;
				assignmentMatrix[randomAgentsArray[j]][k] = 1;
				if (showDebug) 
					System.out.println("\tR" + k + " reassigned from A" + randomAgentsArray[i] + ":"
							+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
							+ "/"
							+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent
							+ " to A" + randomAgentsArray[j] + ":"
							+ agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent
							+ "/"
							+ agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesConnectedToAgent);
				agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent--;
				agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent++;
				return 1;
			}
		}								// if not difference of 2 are found
		return 0; 					// then return false
	} 	// end fast incomplete improvement path

	// ** Batch Improvement Path *********************************************************************
	public int batchImprovementPath(boolean improvement) {
		numberOfPhases = 1;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		printMatrices(12);
		int[][] sinkMatrix = new int[numrows * numcols][numrows * numcols];
		int[][] sourceMatrix = new int[numrows * numcols][numrows * numcols];
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		if (shuffle) {
			for (int i = 0; i < numrows * numcols; i++) {
				int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
				int swap = randomAgentsArray[r];
				randomAgentsArray[r] = randomAgentsArray[i];
				randomAgentsArray[i] = swap;
			}
		}
		int returnValue = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			int[] randomAgentsArray2 = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray2[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray2[r];
					randomAgentsArray2[r] = randomAgentsArray2[j];
					randomAgentsArray2[j] = swap;
				}
			}
			for (int j = 0; j < numrows * numcols; j++) {
				boolean checkDifference;
				if (improvement)
					checkDifference = reachMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] > 0
							&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
									- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 2;
				else
					checkDifference = reachMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] > 0
							&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
									- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent <= 0;
				if (checkDifference) {						// if there is a path and a difference of 2
					Resource resourceWalker = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)   // from i to j
					{					// keep walking until valid resource
						int k = resourceWalker.ID;
						// check lvl here again and delete break statement
						if (assignmentMatrix[randomAgentsArray[i]][k] > 0 && connectionMatrix[randomAgentsArray2[j]][k] > 0)// &&agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent-agents[j/numcols][j%numcols].numberOfResourcesAssignedToAgent>=2)
						{				// if a resource can be passed
							// from agents[i/numcols][i%numcols] to agents[j/numcols][j%numcols]
							// through resource[k]
							possibleResources.add(k);
						}
						resourceWalker = resourceWalker.nextResource;
					}
					if (possibleResources.size() > 0) {
						int k = possibleResources.get((int) (possibleResources.size() * randomNumberGenerator.nextDouble()));
						assignmentMatrix[randomAgentsArray2[j]][k] = 1;
						assignmentMatrix[randomAgentsArray[i]][k] = 0;
						if (showDebug)
							System.out
									.println("\tR"
											+ k
											+ " reassigned from A"
											+ randomAgentsArray[i]
											+ ":"
											+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent
											+ " to A"
											+ randomAgentsArray2[j]
											+ ":"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesConnectedToAgent);
						agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent--;
						agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent++;
						returnValue++;
						sinkMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] = 1;
						sourceMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] = 1;
					}
				}
			}
		}								// if not difference of 2 are found
		printMatrices(13);
		useAssignmentInput(IOTempAssignmentMatrixFileName1);
		if (showAlgorithmVisual)
			printAgentLevelGraph(3, sinkMatrix, sourceMatrix);
		useAssignmentInput(IOTempAssignmentMatrixFileName2);
		if (returnValue > 0)
			useAssignmentMatrix(); 		// use assignment matrix to later update all matrices
		return returnValue;
	} 	// end fast improvement path

	// ** Execute Only 1 Cascaded Improvement Path (Slow) *******************************************
	public int cascadedImprovementPath(int[][] reachMatrixStack, int recursionCounter, boolean improvement) {									// catch the current stack and initially 0
		int returnValue = improvementPath(improvement);
		if (returnValue > 0)
			return returnValue;
		if (recursionCounter > numrows * numcols) // if got to this point, then check if recursion should end
			return 0;
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		if (shuffle) {
			for (int i = 0; i < numrows * numcols; i++) {
				int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
				int swap = randomAgentsArray[r];
				randomAgentsArray[r] = randomAgentsArray[i];
				randomAgentsArray[i] = swap;
			}
		}
		for (int i = 0; i < numrows * numcols; i++) // if got to this point, try to transfer resources using the stack
		{
			int[] randomAgentsArray2 = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray2[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray2[r];
					randomAgentsArray2[r] = randomAgentsArray2[j];
					randomAgentsArray2[j] = swap;
				}
			}
			for (int j = 0; j < numrows * numcols; j++) {
				boolean checkDifference1;
				if (improvement)
					checkDifference1 = (reachMatrixStack[randomAgentsArray[i]][randomAgentsArray2[j]] > 0 && agents[randomAgentsArray[i]
							/ numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
							- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 2);
				else
					checkDifference1 = (reachMatrixStack[randomAgentsArray[i]][randomAgentsArray2[j]] > 0 && agents[randomAgentsArray[i]
							/ numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
							- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent <= 0);
				if (checkDifference1) {						// if there is a path and a difference of 2
					if (showDebug)
						System.out
								.println("\ta resource can be transferred from A"
										+ randomAgentsArray[i]
										+ ":"
										+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
										+ "/"
										+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent
										+ " to A"
										+ randomAgentsArray2[j]
										+ ":"
										+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent
										+ "/"
										+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesConnectedToAgent
										+ " in " + (recursionCounter + 1) + " moves via A"
										+ agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]);
					Resource resourceWalker = agents[agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]] / numcols][agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]
							% numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)  // this is from i to j
					{					// keep walking through the resource
						int k = resourceWalker.ID;
						if (agents[agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]] / numcols][agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]
								% numcols].numberOfResourcesAssignedToAgent
								- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 1)
							if (assignmentMatrix[agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]][k] > 0
									&& connectionMatrix[randomAgentsArray2[j]][k] > 0)// &&checkContains(k,agentPathList[i][j]/numcols,agentPathList[i][j]%numcols)&&checkContains(k,j/numcols,j%numcols))
								possibleResources.add(k);// if a resource can be passed from agentPathList[i][j] to j through resource k
						resourceWalker = resourceWalker.nextResource; // try a different resource if the current one does not work
					}
					if (possibleResources.size() > 0) {
						int k = possibleResources.get((int) (possibleResources.size() * randomNumberGenerator.nextDouble()));
						assignmentMatrix[randomAgentsArray2[j]][k] = 1; // adjust the assignment matrix
						assignmentMatrix[agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]][k] = 0;
						if (showDebug) // let user know
							System.out
									.println("\tR"
											+ k
											+ " reassigned from A"
											+ agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]
											+ ":"
											+ agents[agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]] / numcols][agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]
													% numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]] / numcols][agentPathList[randomAgentsArray[i]][randomAgentsArray2[j]]
													% numcols].numberOfResourcesConnectedToAgent
											+ " to A"
											+ randomAgentsArray2[j]
											+ ":"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesConnectedToAgent
											+ " for cascaded " + (improvement ? "improvement" : "declination") + " path");
						useAssignmentMatrix(); // update assignment matrix
						if (improvementPath(improvement) > 0) // now try to do a difference of 2
							return 1;
						else
							// else try the improvement path recursion again
							return cascadedImprovementPath(reachMatrix, 0, improvement);
					}
					// omit next line to remove false positive of cascaded improvement paths
					// return 1; // return to execute only 1 cascaded improvement path at a time]
				}
			}
		}								// if got to this point, begin the matrix multiplication
		int[][] reachMatrixProduct = new int[numrows * numcols][numrows * numcols];
		boolean reachMatrixStackChanged = false;
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				agentPathList[i][j] = -1; // set -1 to not be confused with resource 0
				ArrayList<Integer> possibleIntermediateAgents = new ArrayList<Integer>(); // used to not move resource twice
				for (int k = 0; k < numrows * numcols; k++) {						// just use the max of 1
					if (reachMatrixStack[i][k] * reachMatrix[k][j] == 1 && reachMatrixProduct[i][j] == 0)
						reachMatrixStackChanged = true;
					reachMatrixProduct[i][j] = Math.max(reachMatrixProduct[i][j], reachMatrixStack[i][k] * reachMatrix[k][j]);
					if (reachMatrixProduct[i][j] == 1) {
						possibleIntermediateAgents.add(k); // move onto the next element if the already set
					}					// and if an agent can be the intermediate
				}
				if (possibleIntermediateAgents.size() > 0)
					agentPathList[i][j] = possibleIntermediateAgents.get((int) (possibleIntermediateAgents.size() * randomNumberGenerator
							.nextDouble()));
			}
		}
		if (reachMatrixStackChanged)
			return cascadedImprovementPath(reachMatrixProduct, recursionCounter + 1, improvement);
		else
			return 0;
	} // end execute improvement path

	// ** Fast Execute Improvement Path *************************************************************
	public int batchCascadedImprovementPath(boolean improvement) {
		// first check if a difference of 2 can occur,
		// if not, then continue to do matrix multiplication and then check if an improvement path can occur,
		// if so, then for loop back until recursionCounter is back to 0 to pop the paths
		// int returnValue = batchImprovementPath(improvement);
		int returnValue = 0;
		if (returnValue > 0)
			return returnValue;
		int[][] reachMatrixElement = new int[numrows * numcols][numrows * numcols];
		int[][] agentPathMatrix = new int[numrows * numcols][numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				agentPathMatrix[i][j] = -1;
				if (reachMatrix[i][j] == 1) {
					agentPathMatrix[i][j] = i;
				}
			}
		}
		reachMatrixList.add(reachMatrix); // = reachMatrix;
		agentPathMatrixList.add(agentPathMatrix);
		// int[] randomAgentsArray = new int[numrows*numcols];
		// for (int i = 0; i<numrows*numcols; i++)
		// {
		// randomAgentsArray[i] = i;
		// }
		// if (shuffle)
		// {
		// for (int i = 0; i<numrows*numcols; i++)
		// {
		// int r = i+(int)(randomNumberGenerator.nextDouble()*(numrows*numcols-i));
		// int swap = randomAgentsArray[r];
		// randomAgentsArray[r] = randomAgentsArray[i];
		// randomAgentsArray[i] = swap;
		// }
		// }
		for (int stackCounter = 1; stackCounter < numrows * numcols; stackCounter++) {								// for every step of the cascaded improvement path
			// int reachMatrixCount = 0;
			// for (int i = 0; i<numrows*numcols; i++)
			// for (int j = 0; j<numrows*numcols; j++)
			// if (reachMatrixList.get(stackCounter-1)[i][j]==1)
			// reachMatrixCount++;
			// if (reachMatrixCount==numrows*numrows*numcols*numcols)
			// {
			// numberOfPhases = stackCounter;
			// return popImprovementPaths(stackCounter, improvement);
			// }
			boolean reachMatrixStackChanged = false;
			agentPathMatrix = new int[numrows * numcols][numrows * numcols];
			reachMatrixElement = new int[numrows * numcols][numrows * numcols];
			for (int i = 0; i < numrows * numcols; i++) {							// check every agent
				for (int j = 0; j < numrows * numcols; j++) {						// against every other
					agentPathMatrix[i][j] = -1;
					int[] randomAgentsArray = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
					for (int k = 0; k < numrows * numcols; k++) {
						randomAgentsArray[k] = k;
					}
					if (shuffle) {
						for (int k = 0; k < numrows * numcols; k++) {
							int r = k + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - k));
							int swap = randomAgentsArray[r];
							randomAgentsArray[r] = randomAgentsArray[k];
							randomAgentsArray[k] = swap;
						}
					}
					for (int k = 0; k < numrows * numcols; k++) {					// just use the max of 1 from i to j via k
						if (reachMatrixList.get(stackCounter - 1)[i][j] == 0
								&& reachMatrixList.get(stackCounter - 1)[i][randomAgentsArray[k]] * reachMatrix[randomAgentsArray[k]][j] == 1) {
							reachMatrixStackChanged = true;
							// System.out.println(i+" is able to reach "+j+" in "+(stackCounter+1)+" steps");
						}
						reachMatrixElement[i][j] = Math.max(reachMatrixList.get(stackCounter - 1)[i][j], reachMatrixList
								.get(stackCounter - 1)[i][randomAgentsArray[k]]
								* reachMatrix[randomAgentsArray[k]][j]);
						if (reachMatrixElement[i][j] == 1) {				// set the intermediate agents to be popped later
							agentPathMatrix[i][j] = randomAgentsArray[k];
							break;		// move onto the next element if the already set
						}				// and if an agent can be the intermediate
					}
				}
			}
			reachMatrixList.add(reachMatrixElement);
			agentPathMatrixList.add(agentPathMatrix);
			if (!reachMatrixStackChanged) {
				// System.out.println("popping at "+stackCounter);
				numberOfPhases = stackCounter;
				numberOfCommunications = ((numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2) * stackCounter;
				return popImprovementPaths(stackCounter, improvement);
			}
		}
		numberOfPhases = numrows * numcols - 1;
		numberOfCommunications = ((numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2) * numberOfPhases;
		return popImprovementPaths(numrows * numcols - 1, improvement);
	} // end fast execute improvement path

	// ** Pop Improvement Paths, a method for Fast Improvement Paths ********************************
	public int popImprovementPaths(int popCounter, boolean improvement) {
		printMatrices(12);
		int[][] sinkMatrix = new int[numrows * numcols][numrows * numcols];
		int[][] sourceMatrix = new int[numrows * numcols][numrows * numcols];
		int numberOfImprovementPaths = 0;
		int numberOfNeutralPaths = 0;
		int[] randomAgentsArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			randomAgentsArray[i] = i;
		}
		if (shuffle) {
			for (int i = 0; i < numrows * numcols; i++) {
				int r = i + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - i));
				int swap = randomAgentsArray[r];
				randomAgentsArray[r] = randomAgentsArray[i];
				randomAgentsArray[i] = swap;
			}
		}
		// cascaded improvement path pre-transfers for ideal
		for (; popCounter >= 0; popCounter--) {								// pop the length of the recursion counter
			for (int i = 0; i < numrows * numcols; i++) // if got to this point, try to transfer resources using the stack
			{
				int[] randomAgentsArray2 = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
				for (int j = 0; j < numrows * numcols; j++) {
					randomAgentsArray2[j] = j;
				}
				if (shuffle) {
					for (int j = 0; j < numrows * numcols; j++) {
						int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
						int swap = randomAgentsArray2[r];
						randomAgentsArray2[r] = randomAgentsArray2[j];
						randomAgentsArray2[j] = swap;
					}
				}
				for (int j = 0; j < numrows * numcols; j++) {
					boolean checkDifference;
					if (improvement)
						checkDifference = reachMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]] > 0
								&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
										- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 2;
					else
						checkDifference = reachMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]] > 0
								&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
										- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent <= 0;
					if (checkDifference) {
						if (showDebug) // the destination is from Ai to Aj via AagentPathListStack[recursionCounter][i][j]
							System.out
									.println("\ta resource can be transferred from A"
											+ randomAgentsArray[i]
											+ ":"
											+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent
											+ " to A"
											+ randomAgentsArray2[j]
											+ ":"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesConnectedToAgent
											+ " in at most "
											+ (popCounter + 1)
											+ " moves via A"
											+ agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
											+ ":"
											+ agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
													/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
													% numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
													/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
													% numcols].numberOfResourcesConnectedToAgent);
						Resource resourceWalker = agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
								/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]] % numcols].firstResource;
						ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
						while (resourceWalker != null)  // this is from i to j
						{					// keep walking through the resource
							int k = resourceWalker.ID;
							// if (!resourceMarker.contains(k))
							// {
							// System.out.print("resourcemarker does not contain k because it only has ");
							// for (int m = 0; m<resourceMarker.size(); m++)
							// {
							// System.out.print(resourceMarker.get(m)+" ");
							// }
							// System.out.println();
							boolean checkDifference2;
							if (improvement)
								checkDifference2 = (agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]] != randomAgentsArray2[j]
										&& assignmentMatrix[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]][k] == 1
										&& connectionMatrix[randomAgentsArray2[j]][k] == 1 && agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										% numcols].numberOfResourcesAssignedToAgent
										- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 1); // this needs to be 1 or
							// else we'll run in
							// R197 error
							else
								checkDifference2 = (agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]] != randomAgentsArray2[j]
										&& assignmentMatrix[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]][k] == 1
										&& connectionMatrix[randomAgentsArray2[j]][k] == 1 && agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										% numcols].numberOfResourcesAssignedToAgent
										- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent <= 0);
							if (checkDifference2) {				// if a resource can be passed from agentPathList[popCounter][i][j] to j through resource k
								possibleResources.add(k);
							}
							resourceWalker = resourceWalker.nextResource; // try a different resource if the current one does not work
						}
						if (possibleResources.size() > 0) {
							int k = possibleResources.get((int) (possibleResources.size() * randomNumberGenerator.nextDouble()));
							if (showDebug) // let user know
								System.out
										.println("\tR"
												+ k
												+ " reassigned from A"
												+ agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
												+ ":"
												+ agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
														/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
														% numcols].numberOfResourcesAssignedToAgent
												+ "/"
												+ agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
														/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
														% numcols].numberOfResourcesConnectedToAgent
												+ " to A"
												+ randomAgentsArray2[j]
												+ ":"
												+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent
												+ "/"
												+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesConnectedToAgent
												+ " for batch cascaded " + (improvement ? "improvement" : "deterioration")
												+ " path during popCounter = " + popCounter);
							// resourceMarker.add(k); // add to keep track of resourced moved to not more a resource more than once
							boolean checkDifference3;
							if (improvement)
								checkDifference3 = agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										% numcols].numberOfResourcesAssignedToAgent
										- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 2;
							else
								checkDifference3 = agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										/ numcols][agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
										% numcols].numberOfResourcesAssignedToAgent
										- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent <= 0;
							if (checkDifference3)
								numberOfImprovementPaths++;
							else
								numberOfNeutralPaths++;
							assignmentMatrix[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]][k] = 0;
							assignmentMatrix[randomAgentsArray2[j]][k] = 1; // adjust the assignment matrix
							agents[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]] / numcols][agentPathMatrixList
									.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]
									% numcols].numberOfResourcesAssignedToAgent--;
							agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent++;
							sinkMatrix[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]][randomAgentsArray2[j]] = 1;
							sourceMatrix[agentPathMatrixList.get(popCounter)[randomAgentsArray[i]][randomAgentsArray2[j]]][randomAgentsArray2[j]] = 1;
						}
							}
				}
			}
		}
		// neighboring improvement path
		for (int i = 0; i < numrows * numcols; i++) {
			int[] randomAgentsArray2 = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray2[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray2[r];
					randomAgentsArray2[r] = randomAgentsArray2[j];
					randomAgentsArray2[j] = swap;
				}
			}
			for (int j = 0; j < numrows * numcols; j++) {
				boolean checkDifference;
				if (improvement)
					checkDifference = reachMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] > 0
							&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
									- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent >= 2;
				else
					checkDifference = reachMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] > 0
							&& agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
									- agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent <= 0;
				if (checkDifference) {						// if there is a path and a difference of 2
					Resource resourceWalker = agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)   // from i to j
					{					// keep walking until valid resource
						int k = resourceWalker.ID;
						// check lvl here again and delete break statement
						if (assignmentMatrix[randomAgentsArray[i]][k] > 0 && connectionMatrix[randomAgentsArray2[j]][k] > 0)// &&agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent-agents[j/numcols][j%numcols].numberOfResourcesAssignedToAgent>=2)
						{				// if a resource can be passed
							// from agents[i/numcols][i%numcols] to agents[j/numcols][j%numcols]
							// through resource[k]
							possibleResources.add(k);
						}
						resourceWalker = resourceWalker.nextResource;
					}
					if (possibleResources.size() > 0) {
						int k = possibleResources.get((int) (possibleResources.size() * randomNumberGenerator.nextDouble()));
						assignmentMatrix[randomAgentsArray2[j]][k] = 1;
						assignmentMatrix[randomAgentsArray[i]][k] = 0;
						if (showDebug)
							System.out
									.println("\tR"
											+ k
											+ " reassigned from A"
											+ randomAgentsArray[i]
											+ ":"
											+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesConnectedToAgent
											+ " to A"
											+ randomAgentsArray2[j]
											+ ":"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent
											+ "/"
											+ agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesConnectedToAgent);
						agents[randomAgentsArray[i] / numcols][randomAgentsArray[i] % numcols].numberOfResourcesAssignedToAgent--;
						agents[randomAgentsArray2[j] / numcols][randomAgentsArray2[j] % numcols].numberOfResourcesAssignedToAgent++;
						numberOfImprovementPaths++;
						sinkMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] = 1;
						sourceMatrix[randomAgentsArray[i]][randomAgentsArray2[j]] = 1;
					}
				}
			}
		}								// if not difference of 2 are found

		useAssignmentMatrix(); // update assignment matrix
		// the first step was already a multiple of the reach matrix,
		// therefore, the program cannot pop back down to a difference of 2
		// so the difference of 2 has to be manually called
		printMatrices(13);
		useAssignmentInput(IOTempAssignmentMatrixFileName1);
		if (showAlgorithmVisual)
			printAgentLevelGraph(2, sinkMatrix, sourceMatrix);
		useAssignmentInput(IOTempAssignmentMatrixFileName2);
		if (showDebug)
			System.out.println("\t" + numberOfImprovementPaths + (improvement ? " improvement" : " deterioration") + " paths and "
					+ numberOfNeutralPaths + " neutral paths were executed");
		return numberOfNeutralPaths + numberOfImprovementPaths;
	} // end pop Improvement Paths, a method for fast execute improvement path

	// ** Donate with Flooding ************************************************************************
	public int donateWithFlooding(int donateTo, int donateTill, int whoIsConsideredInAverage, int donateWhile, double percentile, int acceptance) { // 0 = donate until richer than poorest, 1 = donate until average, 2 = doante until poorest by donating randomly, 3 = donate until avg by donating randomly
		// 1, 1, 0, 1, 0
		// 0 = donate to poorest; 1 = donate to poorer
		// 0 = donate while richer than poorest; 1 = donate while richer than average
		// 0 = diff of 1 is poorer ; 1 = diff of 2 is poorer
		// 0 = donate while not surpassing target; 1 = donate while 1 away from target
		// optimal is 1, 1, 0, 1, x
		// acceptance: 0 = accept all, 1 = accept from richest until average, 2 = accept from
		int returnValue = 0;
		numberOfPhases = 2;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		//int[][] donationMatrix = new int[numrows * numcols][numrows * numcols]; // the amount of resources that agent i will donate to j
		int[] resourcesMarkedToDonate = new int[N];	// new destination of each resource
		for (int i = 0; i < N; i++) {
			resourcesMarkedToDonate[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList<Integer> resourcesToDonate = new ArrayList<Integer>();
			Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
			while (resourceWalker != null) {
				if (assignmentMatrix[i][resourceWalker.ID] > 0) {
					resourcesToDonate.add(resourceWalker.ID);
				}
				resourceWalker = resourceWalker.nextResource;
			}
			ArrayList<int[]> agentsToDonateTo = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (adjacencyMatrix[i][j] > 0 && ((whoIsConsideredInAverage == 1 && targetedNumberOfResources - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2) || (whoIsConsideredInAverage == 0 && targetedNumberOfResources > agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent))) {
					for (int k = 0; k < resourcesToDonate.size(); k++) {
						if (connectionMatrix[j][resourcesToDonate.get(k)] > 0) {
							int[] intArrayToAdd = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
							agentsToDonateTo.add(intArrayToAdd);
							break;
						}
					}
				}
			}
			Collections.shuffle(resourcesToDonate, randomNumberGenerator);
			double averageNumberOfResources = targetedNumberOfResources;
			if (percentile > 0 && percentile < 1) {
				ArrayList <Integer> resourcesOwnForPercentileCalculation = new ArrayList<Integer>();
				resourcesOwnForPercentileCalculation.add(targetedNumberOfResources);
				for (int j = 0; j < agentsToDonateTo.size(); j++) {
					resourcesOwnForPercentileCalculation.add(agentsToDonateTo.get(j)[1]);
				}
				averageNumberOfResources = percentile(resourcesOwnForPercentileCalculation, percentile);
			} else {
				for (int j = 0; j < agentsToDonateTo.size(); j++) {
					averageNumberOfResources += agentsToDonateTo.get(j)[1];
				}
				averageNumberOfResources /= (agentsToDonateTo.size() + 1);
			}
			if (showDebug) {
				println("a" + i + "'s averageNumberOfResoures = " + averageNumberOfResources);
			}
			for (boolean anImprovementHasoccured = true; anImprovementHasoccured; )
			{
				anImprovementHasoccured = false;
				if (showDebug) {
					println("before shuffle");
					for (int j = 0; j < agentsToDonateTo.size(); j++) {
						print("a" + agentsToDonateTo.get(j)[0] + " " + agentsToDonateTo.get(j)[1] + ", ");
					}
					println();
				}
				Collections.shuffle(agentsToDonateTo, randomNumberGenerator);
				if (showDebug) {
					println("after shuffle");
					for (int j = 0; j < agentsToDonateTo.size(); j++) {
						print("a" + agentsToDonateTo.get(j)[0] + " " + agentsToDonateTo.get(j)[1] + ", ");
					}
					println();
				}
				if (donateTo == 0) {
					Collections.sort(agentsToDonateTo, new Comparator<int[]>() {
						@Override
						public int compare(int[] arg0, int[] arg1) {
							return arg0[1] - arg1[1]; // sort from low to high
						}
					});
				}
				if (showDebug) {
					println("after sorting by price if version <= 1");
					for (int j = 0; j < agentsToDonateTo.size(); j++) {
						print("a" + agentsToDonateTo.get(j)[0] + " " + agentsToDonateTo.get(j)[1] + ", ");
					}
					println();
				}
				for (int j = 0; j < agentsToDonateTo.size(); ) {
					if (targetedNumberOfResources - agentsToDonateTo.get(j)[1] < 2 || agentsToDonateTo.get(j)[1] >= averageNumberOfResources) { // recently changed. was originall  >=, but unfinished; then + 1 >, but decline; now back to >=>
						// if my self-simulation is less than 2 away from that other guys or if that other guys is >= avg
						if (showDebug) {
							print("\t\t\t");
							if (targetedNumberOfResources - agentsToDonateTo.get(j)[1] < 2) {
								print("< 2 = true; ");
							}
							if (agentsToDonateTo.get(j)[1] >= averageNumberOfResources) { // very recently changed on may 1st from + 1 > to >=
								print("> average = true; ");
							}
							println(" removing A" + agentsToDonateTo.get(j)[0] + " from list");
						}
						agentsToDonateTo.remove(j); // remove agents too rich to accept your donations
						continue;
					}
					if (donateTill == 1 && ((donateWhile == 0 && targetedNumberOfResources  <= averageNumberOfResources) || (donateWhile == 1 && targetedNumberOfResources - averageNumberOfResources < 1))) {//targetedNumberOfResources - averageNumberOfResources < 1) { targetedNumberOfResources  <= averageNumberOfResources
						if (showDebug) {
							println("\t\t\tmoving to next agent because accomplished donateTill and donateWhile");
						}
						break;
					}
					boolean breakOutOfOutterLoop = false;
					for (int k = 0; k < resourcesToDonate.size(); k++) {
						if (connectionMatrix[agentsToDonateTo.get(j)[0]][resourcesToDonate.get(k)] > 0) {
							if (showDebug) {
								System.out.println("\tA" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent +" will donate R" + resourcesToDonate.get(k) + " to A" + agentsToDonateTo.get(j)[0] + ":" + agentsToDonateTo.get(j)[1] + "/" + agents[agentsToDonateTo.get(j)[0] / numcols][agentsToDonateTo.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[agentsToDonateTo.get(j)[0] / numcols][agentsToDonateTo.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
							}
							resourcesMarkedToDonate[resourcesToDonate.get(k)] = agentsToDonateTo.get(j)[0];
							resourcesToDonate.remove(k);
							targetedNumberOfResources--;
							int[] intArrayToReplace = {agentsToDonateTo.get(j)[0], agentsToDonateTo.get(j)[1] + 1};
							agentsToDonateTo.set(j, intArrayToReplace);
							anImprovementHasoccured = true;
							breakOutOfOutterLoop = true;
							break;
						}
					}
					if (breakOutOfOutterLoop) {
						break;
					}
					if (showDebug)
						println("\t\t\tremoving A" + agentsToDonateTo.get(j)[0] + " from list since no resources to transfer");
					agentsToDonateTo.remove(j);
				}
			}
		}
		if (ceilingLimit) {
			ArrayList<Integer> shuffledResources = new ArrayList<Integer>();
			for (int i = 0; i < N; i++) {
				shuffledResources.add(i);
			}
			Collections.shuffle(shuffledResources);
			for (int i = 0; i < N; i++) {
				if (resourcesMarkedToDonate[shuffledResources.get(i)] >= 0 && agents[resourcesMarkedToDonate[shuffledResources.get(i)] / numcols][resourcesMarkedToDonate[shuffledResources.get(i)] % numcols].numberOfResourcesAssignedToAgent + 1 <= CEILING) {
					returnValue++;
					int previousOwner = assignmentVector[shuffledResources.get(i)];
					int newOwner = resourcesMarkedToDonate[shuffledResources.get(i)];
					if (showDebug) {
						System.out.println("\tA" + previousOwner + ":" + agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent + "/" + agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesConnectedToAgent +" donated R" + i + " to A" + newOwner + ":" + agents[newOwner / numcols][newOwner % numcols].numberOfResourcesAssignedToAgent + "/" + agents[newOwner / numcols][newOwner % numcols].numberOfResourcesConnectedToAgent);
					}
					agents[newOwner / numcols][newOwner % numcols].numberOfResourcesAssignedToAgent++;
					assignmentMatrix[newOwner][shuffledResources.get(i)] = 1;
					assignmentVector[shuffledResources.get(i)] = newOwner;
					agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent--;
					assignmentMatrix[previousOwner][shuffledResources.get(i)] = 0;
				}
			}
		} else {
			for (int i = 0; i < N; i++) {
				if (resourcesMarkedToDonate[i] >= 0) {
					returnValue++;
					int previousOwner = assignmentVector[i];
					int newOwner = resourcesMarkedToDonate[i];
					if (showDebug) {
						System.out.println("\tA" + previousOwner + ":" + agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent + "/" + agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesConnectedToAgent +" donated R" + i + " to A" + newOwner + ":" + agents[newOwner / numcols][newOwner % numcols].numberOfResourcesAssignedToAgent + "/" + agents[newOwner / numcols][newOwner % numcols].numberOfResourcesConnectedToAgent);
					}
					agents[newOwner / numcols][newOwner % numcols].numberOfResourcesAssignedToAgent++;
					assignmentMatrix[newOwner][i] = 1;
					assignmentVector[i] = newOwner;
					agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent--;
					assignmentMatrix[previousOwner][i] = 0;
				}
			}
		}
		if (returnValue == 0) {
			numberOfPhases = 1;
		}
		return returnValue;
	}	// end donate with flooding
	
	// ** Rob with Flooding Revised ****************************************************************
	public int robWithFlooding(int resourcePiroirty, int robFrom, int robTill, int tieWinner) {  // 1, 1, 1, 0
		// 0 = rob resource with fewest edges, 1 = rob random
		// 0 = rob from richest, 1 = rob from richer at random
		// 0 = rob until richest, 1 = rob until average
		// 0 = poorest wins, 1 = winner is randomized
		double avgBidsPlaced = 0;
		int m1 = 0;
		int m2 = 0;
		double avgBidders = 0;
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		bidMatrix = new int[numrows * numcols][maxID + 1];
		int[] agentsBiddingPrice = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList<int[]> resourcesToRob = new ArrayList<int[]>(); // ArrayList of resources to rob for resourceID and numberOfResourcesOwnerOwns
			Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
			while (resourceWalker != null) {
				if (assignmentVector[resourceWalker.ID] == i) { // OR assignmentMatrix[i][resourceWalker.ID] > 0
					bidMatrix[i][resourceWalker.ID] = 2;
				} else {
					int resourceNumberOfConnections = 0;
					if (resourcePiroirty == 0) {
						for (int j = 0; j < numrows * numcols; j++) {
							if (connectionMatrix[j][resourceWalker.ID] > 0) {
								resourceNumberOfConnections++;
							}
						}
					}
					int[] resourceToRob = {resourceWalker.ID, resourceNumberOfConnections};
					resourcesToRob.add(resourceToRob);
				}
				resourceWalker = resourceWalker.nextResource;
			}
			Collections.shuffle(resourcesToRob, randomNumberGenerator);
			if (resourcePiroirty == 0) {
				Collections.sort(resourcesToRob, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg0[1] - arg1[1]; // sort from low to high
					}
				});
			}
			double averageAmongRicherNeighbors = targetedNumberOfResources; // its own resources
			ArrayList<int[]> agentsToRob = new ArrayList<int[]>(); // will not include itself
			for (int j = 0; j < numrows*numcols; j++) {
				if (adjacencyMatrix[i][j] > 0 && agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent > targetedNumberOfResources) {
					int[] agentToRob = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					averageAmongRicherNeighbors += agentToRob[1];
					agentsToRob.add(agentToRob);
				}
			}
			averageAmongRicherNeighbors /= agentsToRob.size() + 1;
			for (boolean anImprovementHasoccured = true; anImprovementHasoccured; )
			{
				anImprovementHasoccured = false;
				if (robTill > 0 && targetedNumberOfResources >= averageAmongRicherNeighbors) {
					break;
				}
				Collections.shuffle(agentsToRob, randomNumberGenerator);
				if (robFrom == 0) {
					Collections.sort(agentsToRob, new Comparator<int[]>() {
						@Override
						public int compare(int[] arg0, int[] arg1) {
							return arg1[1] - arg0[1]; // sort from high to low
						}
					});
				}
				for (int j = 0; j < agentsToRob.size(); ) {
					if (agentsToRob.get(j)[1] - targetedNumberOfResources < 2 || agentsToRob.get(j)[1] <= averageAmongRicherNeighbors) { // remove agents you will be richer than
						agentsToRob.remove(j);			// remove agents too poor to rob
						continue;
					}
					boolean breakOutOfOutterLoop = false;
					for (int k = 0; k < resourcesToRob.size(); k++) {
						if (assignmentMatrix[agentsToRob.get(j)[0]][resourcesToRob.get(k)[0]] > 0) {
							if (showDebug) {
								System.out.println("\tA" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent +" will rob R" + resourcesToRob.get(k)[0] + " from A" + agentsToRob.get(j)[0] + ":" + agentsToRob.get(j)[1] + "/" + agents[agentsToRob.get(j)[0] / numcols][agentsToRob.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[agentsToRob.get(j)[0] / numcols][agentsToRob.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
							}
							bidMatrix[i][resourcesToRob.get(k)[0]] = 1;
//							bidMatrix[agentsToRob.get(j)[0]][resourcesToRob.get(k)[0]] = -1;
							if (showDebug) {
								println("bidMatrix["+i+"]["+resourcesToRob.get(k)[0]+"] = 1");
								println("bidMatrix["+agentsToRob.get(j)[0]+"]["+resourcesToRob.get(k)[0]+"] = -1");
							}
							resourcesToRob.remove(k);
							targetedNumberOfResources++;
							int[] intArrayToReplace = {agentsToRob.get(j)[0], agentsToRob.get(j)[1] - 1};
							agentsToRob.set(j, intArrayToReplace);
							anImprovementHasoccured = true;
							breakOutOfOutterLoop = true;
							break;
						}
					}
					if (breakOutOfOutterLoop) {
						break;
					}
					agentsToRob.remove(j); // remove agent j after searching all the resources and none can be passed
				}
			}
			agentsBiddingPrice[i] = targetedNumberOfResources;
			avgBidsPlaced += targetedNumberOfResources;
			if (targetedNumberOfResources > m1)
				m1 = targetedNumberOfResources;
		}
		int[] originalNumberOfResources = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			originalNumberOfResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		if (showDebug) {
			System.out.printf("%4d", 0);
			for (int i = 0; i < N; i++) {
				System.out.printf("%4d", i);
			}
			println();
			for (int i = 0; i < numrows * numcols; i++) {
				System.out.printf("%4d", i);
				for (int j = 0; j < N; j++) {
					System.out.printf("%4d", bidMatrix[i][j]);
				}
				println();
			}
		}
		for (int i = 0; i < N; i++) {
			boolean isOriginalOwnersBidAccepted = true;
			ArrayList<int[]> bidders = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (bidMatrix[j][i] == 1 && isOriginalOwnersBidAccepted) {
					isOriginalOwnersBidAccepted = false;
					bidders = new ArrayList<int[]>();
				}
				if (bidMatrix[j][i] == 1 || (bidMatrix[j][i] == 2 && isOriginalOwnersBidAccepted)) {
					int[] bidderToAdd = {j, agentsBiddingPrice[j]};
					bidders.add(bidderToAdd);
				}
			}
			if (bidders.size() == 0) {
				// println("R" + i + " has no bidders");
				continue;
			}
			if (showDebug) {
				print("before shuffling out of ");
				for(int[] agentToRob : bidders) {
					print("A" + agentToRob[0] + " B" + agentToRob[1] +", ");
				}
				println();
			}
			Collections.shuffle(bidders, randomNumberGenerator);
			if (showDebug) {
				print("after shuffling out of ");
				for(int[] agentToRob : bidders) {
					print("A" + agentToRob[0] + " B" + agentToRob[1] +", ");
				}
				System.out.println("; A" + bidders.get(0)[0] + " should win at price " + bidders.get(0)[1] + " original owner?= " + (bidders.get(0)[0] == assignmentVector[i]));
			}
			if (tieWinner == 0) { // if bidding
				Collections.sort(bidders, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg0[1] - arg1[1]; // sort from low to high becuase those bidding on the least should get it
					}
				});
			}
			if (showDebug) {
				print("after sorting: ");
				for(int[] agentToRob : bidders) {
					print("A" + agentToRob[0] + " B" + agentToRob[1] +", ");
				}
				println();
			}
			avgBidders += bidders.size();
			if (bidders.size() > m2)
				m2 = bidders.size();
			if (bidders.get(0)[0] != assignmentVector[i]) { // if they did not win their own resource
				returnValue++;
				int previousOwner = assignmentVector[i];
				int newOwner = bidders.get(0)[0];
				if (showDebug) {
					System.out.println("\tA" + newOwner + ":" + agents[newOwner / numcols][newOwner % numcols].numberOfResourcesAssignedToAgent + "/" + agents[newOwner / numcols][newOwner % numcols].numberOfResourcesConnectedToAgent +" robbed R" + i + " from A" + previousOwner + ":" + agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent + "/" + agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesConnectedToAgent + " at price " + agentsBiddingPrice[newOwner]);
				}
				agents[newOwner / numcols][newOwner % numcols].numberOfResourcesAssignedToAgent++;
				assignmentMatrix[newOwner][i] = 1;
				assignmentVector[i] = newOwner;
				agents[previousOwner / numcols][previousOwner % numcols].numberOfResourcesAssignedToAgent--;
				assignmentMatrix[previousOwner][i] = 0;
			}
		}
		if (returnValue == 0) {
			numberOfPhases = 1;
		}
		if (showDebug) {
			println("avg numberOfBidders = " + (double) (avgBidders / N) + "(" + m2 + ")");
			println("avg avgBidsPlaced = " + (double) (avgBidsPlaced / (numrows * numcols)) + "(" + m1 + ")");
		}
		return returnValue;
	}
	
	// ** IPs without Simulation *******************************************************************
	public int IPsWithoutSimulation(double percentage) {
		if (showDebug) {
			println("connection");
			for (int i = 0; i < connectionMatrix.length; i++) {
				for (int j = 0; j < connectionMatrix[0].length; j++) {
					print(connectionMatrix[i][j]);
				}
				println();
			}
			println();
			println("assignment before");
			for (int i = 0; i < assignmentMatrix.length; i++) {
				for (int j = 0; j < assignmentMatrix[0].length; j++) {
					print(assignmentMatrix[i][j]);
				}
				println();
			}
			println();
		}
		numberOfPhases = 2;
		numberOfCommunications = numberOfOnes(connectionMatrix);
		int returnValue = 0;
		for (int i = 0; i < N; i++) {
			ArrayList<Integer> possibleResourceRecipiants = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2 && connectionMatrix[j][i] > 0) {
					possibleResourceRecipiants.add(j);
				}
			}
			if (randomNumberGenerator.nextDouble() <= percentage && possibleResourceRecipiants.size() > 0) {
				returnValue++;
				int resourceWinner = possibleResourceRecipiants.get(randomNumberGenerator.nextInt(possibleResourceRecipiants.size()));
				if (showDebug) {
					println("\tr" + i + " will be transferred from A" + assignmentVector[i] + ":" + agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesConnectedToAgent + " to A" + resourceWinner + ":" + agents[resourceWinner / numcols][resourceWinner % numcols].numberOfResourcesAssignedToAgent + "/" + agents[resourceWinner / numcols][resourceWinner % numcols].numberOfResourcesConnectedToAgent);
				}
				assignmentMatrix[assignmentVector[i]][i] = 0;
				assignmentMatrix[resourceWinner][i] = 1;
			}
		}
		if (showDebug) {
			println("assignment after");
			for (int i = 0; i < assignmentMatrix.length; i++) {
				for (int j = 0; j < assignmentMatrix[0].length; j++) {
					print(assignmentMatrix[i][j]);
				}
				println();
			}
		}
		useAssignmentMatrix();
		numberOfCalls++;
		if (numberOfCalls >= 20 || returnValue == 0) {
			numberOfCalls = 0;
			return 0;
		}
		return returnValue;
	} 	// ipws
	
	public int oneOfferOneAccept() {
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfCommunications = numberOfOnes(connectionMatrix);
		int[][] offerMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of offers
		// if declination, then keep track of applications
		for (int i = 0; i < numrows * numcols; i++) {
			int agentThatGetsOffer = -1;
			ArrayList <Integer> possibleOfferRecipiants = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {							// i will offer to j
				if (reachMatrix[i][j] > 0 && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2) {	
					possibleOfferRecipiants.add(j);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			if (possibleOfferRecipiants.size() > 0) {
				agentThatGetsOffer = possibleOfferRecipiants.get(randomNumberGenerator.nextInt(possibleOfferRecipiants.size()));
			}
			if (agentThatGetsOffer >= 0) {// update offer matrix
				offerMatrix[i][agentThatGetsOffer] = 1;
			}
		}
		int[] resourcesNewOwner = new int[N];
		for (int i = 0; i < resourcesNewOwner.length; i++) {
			resourcesNewOwner[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList<int[]> listOfOfferees = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {		// j offered to i in the offer matrix, so now use i to find j
				if ((offerMatrix[j][i] > 0)) {					// find the offer from the richest person
					int[] offeree = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					listOfOfferees.add(offeree);
				}
				// or i applied for resources from j, so j should send to i; i should now find the poorest j that offered to transfer with
			}
			if (listOfOfferees.size() == 0) {
				continue;
			}
				// accept as many resources as possible from the richest
				Collections.shuffle(listOfOfferees, randomNumberGenerator);
				Collections.sort(listOfOfferees, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg1[1] - arg0[1]; // sort from high to low
					}
				});
				Resource resourceWalker = agents[listOfOfferees.get(0)[0] / numcols][listOfOfferees.get(0)[0] % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					if (assignmentMatrix[listOfOfferees.get(0)[0]][k] > 0 && connectionMatrix[i][k] > 0 && resourcesNewOwner[k] == -1) {
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				while (possibleResources.size() > 0 && listOfOfferees.get(0)[1] - targetedNumberOfResources >= 2) {
					int resourceToTransfer = possibleResources.get(randomNumberGenerator.nextInt(possibleResources.size()));
					listOfOfferees.get(0)[1] = listOfOfferees.get(0)[1] - 1;
					targetedNumberOfResources++;
					assignmentMatrix[listOfOfferees.get(0)[0]][resourceToTransfer] = 0;
					resourcesNewOwner[resourceToTransfer] = i;
					possibleResources.remove(possibleResources.indexOf(resourceToTransfer));
				}
		}
		for (int i = 0; i < resourcesNewOwner.length; i++) {
			if (resourcesNewOwner[i] >= 0) {
				agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent--;
				agents[resourcesNewOwner[i] / numcols][resourcesNewOwner[i] % numcols].numberOfResourcesAssignedToAgent++;
				if (assignmentMatrix[assignmentVector[i]][i] != 0) {
					println("SOMETHING IS WRONG. SHOULD HAVE ALREADY BEEN SET TO 0");
				}
				assignmentMatrix[assignmentVector[i]][i] = 0;
				assignmentMatrix[resourcesNewOwner[i]][i] = 1;
				assignmentVector[i] = resourcesNewOwner[i];
				returnValue++;
			}
		}
		useAssignmentMatrix();
		
		return returnValue;
	}
	
	// ** MultiOfferMultiAccept *******************************************************************
	public int multiOfferMultiAccept(boolean isOneAgentAtATime, int boundary) { 
		// true isOfferOneResourceAtATime. false isOfferOneAgentAtATime 
		// 0 is average, 1 is avg-low, 2 is avg-high
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfOffers = 0;
		numberOfCommunications = numberOfOnes(connectionMatrix);
		int[][] thresholdMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of offers
		int[][] offerMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of offers
		// if declination, then keep track of applications
		int[] resourcesOffered = new int[N];
		for (int i = 0; i < resourcesOffered.length; i++) {
			resourcesOffered[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			double averageAmongPoorerNeighbors = targetedNumberOfResources; // its own resources
			ArrayList <int[]> possibleOfferRecipiants = new ArrayList<int[]>();
			ArrayList <Integer> resourcesOwnForPercentileCalculation = new ArrayList<Integer>();
			resourcesOwnForPercentileCalculation.add(targetedNumberOfResources);
			for (int j = 0; j < numrows * numcols; j++) {							// i will offer to j
				if (reachMatrix[i][j] > 0 && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 1) {	
					int[] possibleOfferRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					averageAmongPoorerNeighbors += possibleOfferRecipiant[1];
					resourcesOwnForPercentileCalculation.add(agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent);
					possibleOfferRecipiants.add(possibleOfferRecipiant);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			averageAmongPoorerNeighbors /= possibleOfferRecipiants.size() + 1;
//			double avg = averageAmongPoorerNeighbors;
//			if (true) {
//				println("before averageAmongPoorerNeighbors = " + averageAmongPoorerNeighbors);
//			}
			if (boundary == 1) {
				averageAmongPoorerNeighbors = (averageAmongPoorerNeighbors + min(resourcesOwnForPercentileCalculation)) / 2.0;
			}
			if (boundary == 2) {
				averageAmongPoorerNeighbors = (averageAmongPoorerNeighbors + max(resourcesOwnForPercentileCalculation)) / 2.0;
			}
//			if (true) {
//				println("after boundary = " + boundary + ", averageAmongPoorerNeighbors = " + averageAmongPoorerNeighbors);
//			}
//			if (percentile > 0 && percentile < 1) {
//				averageAmongPoorerNeighbors = percentile(resourcesOwnForPercentileCalculation, percentile);
//			}
//			if (showDebug) {
//				if (percentile > 0 && percentile < 1) {
//					println("\ta" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + " is offer until " + percentile(resourcesOwnForPercentileCalculation, percentile) + " based on percentile");
//				}
//				{
//					println("\ta" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + " is offer until average of " + avg);
//				}
//			}
			Collections.shuffle(possibleOfferRecipiants, randomNumberGenerator);
			if (!isOneAgentAtATime) { // if isOneResourceAtATime
				Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					if (assignmentMatrix[i][k] > 0) {
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				Collections.shuffle(possibleResources, randomNumberGenerator);
				for (int k = 0; k < possibleResources.size() && targetedNumberOfResources - averageAmongPoorerNeighbors >= 1; k++) {
					Collections.shuffle(possibleOfferRecipiants, randomNumberGenerator);
					for (int j = 0; j < possibleOfferRecipiants.size(); j++) {
						if (possibleOfferRecipiants.get(j)[1] < averageAmongPoorerNeighbors && targetedNumberOfResources - possibleOfferRecipiants.get(j)[1] >= 2 && connectionMatrix[possibleOfferRecipiants.get(j)[0]][possibleResources.get(k)] > 0) {
							int resourceToOffer = possibleResources.get(k);
							if (showDebug) {
								println("\t\ta" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " offers r" + resourceToOffer + " to a" + possibleOfferRecipiants.get(j)[0] + ":" + possibleOfferRecipiants.get(j)[1] + "/" + agents[possibleOfferRecipiants.get(j)[0] / numcols][possibleOfferRecipiants.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleOfferRecipiants.get(j)[0] / numcols][possibleOfferRecipiants.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
							}
							resourcesOffered[resourceToOffer] = possibleOfferRecipiants.get(j)[0];
							targetedNumberOfResources--;
							possibleOfferRecipiants.get(j)[1] = possibleOfferRecipiants.get(j)[1] + 1;
							offerMatrix[i][possibleOfferRecipiants.get(j)[0]]++;
							numberOfOffers++;
							break;
						}
					}
				}
			} else { // if not isOfferOneResourceAtATime
				for (int j = 0; j < possibleOfferRecipiants.size(); j++) {

					Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)   // from i to j
					{					// keep walking until valid resource
						int k = resourceWalker.ID;
						//					if (assignmentMatrix[i][k] > 0 && assignmentVector[k] != i) {
						//						println("something is wrong with i = " + i + " j = " + j + " k = " +  k);
						//					}
						if (assignmentMatrix[i][k] > 0 && connectionMatrix[possibleOfferRecipiants.get(j)[0]][k] > 0 && resourcesOffered[k] == -1) {
							possibleResources.add(k);
						}
						resourceWalker = resourceWalker.nextResource;
					}
					//				println(possibleResources.size() + " " + targetedNumberOfResources + " " + possibleOfferRecipiants.get(j)[1]);
					int numberOfOfferedResources = 0;
					while (possibleOfferRecipiants.get(j)[1] < averageAmongPoorerNeighbors && targetedNumberOfResources - possibleOfferRecipiants.get(j)[1] >= 2 && possibleResources.size() > 0 && targetedNumberOfResources - averageAmongPoorerNeighbors >= 1) {
						int resourceToOffer = possibleResources.get(randomNumberGenerator.nextInt(possibleResources.size()));
						if (showDebug) {
							println("\t\ta" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " offers r" + resourceToOffer + " to a" + possibleOfferRecipiants.get(j)[0] + ":" + possibleOfferRecipiants.get(j)[1] + "/" + agents[possibleOfferRecipiants.get(j)[0] / numcols][possibleOfferRecipiants.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleOfferRecipiants.get(j)[0] / numcols][possibleOfferRecipiants.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
						}
						resourcesOffered[resourceToOffer] = possibleOfferRecipiants.get(j)[0];
						//					println("before removing " + resourceToOffer + ": " + possibleResources);
						possibleResources.remove(possibleResources.indexOf(resourceToOffer));
						//					println("after removing " + resourceToOffer + ": " + possibleResources);
						targetedNumberOfResources--;
						possibleOfferRecipiants.get(j)[1] = possibleOfferRecipiants.get(j)[1] + 1;
						numberOfOfferedResources++;
						//					println("\t\t\t\t" + (possibleOfferRecipiants.get(j)[1] < averageAmongPoorerNeighbors) + " " + (targetedNumberOfResources - possibleOfferRecipiants.get(j)[1] >= 2) + " " + (possibleResources.size() > 0) + " " + (targetedNumberOfResources - averageAmongPoorerNeighbors >= 1));
					}
					offerMatrix[i][possibleOfferRecipiants.get(j)[0]] = numberOfOfferedResources;
					numberOfOffers += numberOfOfferedResources;
				}
			}
			for (int j = 0; j < possibleOfferRecipiants.size(); j++) {
				thresholdMatrix[i][possibleOfferRecipiants.get(j)[0]] = targetedNumberOfResources;
			}
		}
		if (showDebug) {
			println("agents offered to vector:");
			for (int i = 0; i < resourcesOffered.length; i++) {
				print("r" + i + " is offered to " + resourcesOffered[i] + " from a" + assignmentVector[i] + "; ");
			}
			println();
		}
		// END of offer step
		int[] tempAgentsNumberOfAssignedResources = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			tempAgentsNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		// go through the list of agents. accept from the highest threshold first. if current > highest threshold, then break
		for (int i = 0; i < numrows * numcols; i++) {
			int tempNumberOfAssignedResourcesToThisAgent = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList<int[]> receivedOffers = new ArrayList<int[]>();
			if (showDebug) {
				print("\ta" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " is considering offers from ");
			}
			for (int j = 0; j < numrows * numcols; j++) {
				if (offerMatrix[j][i] > 0) {
					int[] receivedOffer = {j, thresholdMatrix[j][i]};
					receivedOffers.add(receivedOffer);
					if (showDebug) {
						print("a" + j + " v" + offerMatrix[j][i] + " t" + thresholdMatrix[j][i] + ", ");
					}
				}
			}
			if (showDebug)
				println();
			while (receivedOffers.size() > 0) {
				Collections.shuffle(receivedOffers, randomNumberGenerator);
				Collections.sort(receivedOffers, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg1[1] - arg0[1]; // sort from high to low
					}
				});
//				if (showDebug) {
//					println("\t\ta" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " is looking at a" + receivedOffers.get(0)[0]);
//				}
				if (tempNumberOfAssignedResourcesToThisAgent >=  receivedOffers.get(0)[1]) {
					break;
				}
				ArrayList<Integer> resourcesToAccept = new ArrayList<Integer>();
				for (int j = 0; j < N; j++) {
//					if (showDebug) {
//						print("j = " + j + " assignment = " + assignmentVector[j] + " resourcesOffered[j] = " + resourcesOffered[j] + "; ");
//					}
					if (assignmentVector[j] == receivedOffers.get(0)[0] && resourcesOffered[j] == i) { // assignmentVector[j] is the donor, i is the acceptor, j is the resource
						resourcesToAccept.add(j);
					}
				}
//				if (showDebug) {
//					println();
//				}
				if (resourcesToAccept.size() == 0) {
					if (showDebug) {
						println("\t\t\ta" + receivedOffers.get(0)[0] + " has no more resources to offer");
					}
					receivedOffers.remove(0);
				} else {
					int resourceToAccept = resourcesToAccept.get(randomNumberGenerator.nextInt(resourcesToAccept.size()));
					if (showDebug) {
						println("\t\t\ta" + i + ":" + tempAgentsNumberOfAssignedResources[i] + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " accepts r" + resourceToAccept + " from a" + assignmentVector[resourceToAccept] + ":" + tempAgentsNumberOfAssignedResources[assignmentVector[resourceToAccept]] + "/" + agents[assignmentVector[resourceToAccept] / numcols][assignmentVector[resourceToAccept] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[assignmentVector[resourceToAccept] / numcols][assignmentVector[resourceToAccept] % numcols].numberOfResourcesConnectedToAgent +" w/ threshold " + thresholdMatrix[assignmentVector[resourceToAccept]][i]);
					}
					tempNumberOfAssignedResourcesToThisAgent++;
					tempAgentsNumberOfAssignedResources[assignmentVector[resourceToAccept]]--;
					tempAgentsNumberOfAssignedResources[i]++;
//					agents[assignmentVector[resourceToAccept] / numcols][assignmentVector[resourceToAccept] % numcols].numberOfResourcesAssignedToAgent--;
//					agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent++;
					assignmentMatrix[assignmentVector[resourceToAccept]][resourceToAccept] = 0;
					assignmentMatrix[i][resourceToAccept] = 1;
					assignmentVector[resourceToAccept] = i;
					returnValue++;
				}
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent = tempAgentsNumberOfAssignedResources[i];
		}
		useAssignmentMatrix();
		numberOfOffers -= returnValue;
		return returnValue;
	}
	
	// ** offerMultiAccept *******************************************************************
	public int oneOfferMultiAccept() {
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfCommunications = numberOfOnes(connectionMatrix);
		int[][] offerMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of offers
		int[][] acceptMatrix = new int[numrows * numcols][numrows * numcols];
		// if declination, then keep track of applications
		for (int i = 0; i < numrows * numcols; i++) {
			int agentThatGetsOffer = -1;
			ArrayList <Integer> possibleOfferRecipiants = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {							// i will offer to j
				if (reachMatrix[i][j] > 0 && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2) {	
					possibleOfferRecipiants.add(j);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			if (possibleOfferRecipiants.size() > 0) {
				agentThatGetsOffer = possibleOfferRecipiants.get(randomNumberGenerator.nextInt(possibleOfferRecipiants.size()));
			}
			if (agentThatGetsOffer >= 0) {// update offer matrix
				offerMatrix[i][agentThatGetsOffer] = 1;
			}
		}
		if (showDebug) {
			println("offerMatrix:");
			for (int i = 0; i < numrows * numcols; i++) {
				for (int j = 0; j < numrows * numcols; j++) {
					print("" + offerMatrix[i][j]);
				}
				println();
			}
		}
		int[] resourcesNewOwner = new int[N];
		for (int i = 0; i < resourcesNewOwner.length; i++) {
			resourcesNewOwner[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList<int[]> listOfOfferees = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {		// j offered to i in the offer matrix, so now use i to find j
				if ((offerMatrix[j][i] > 0)) {					// find the offer from the richest person
					int[] offeree = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					listOfOfferees.add(offeree);
				}
				// or i applied for resources from j, so j should send to i; i should now find the poorest j that offered to transfer with
			}
			if (listOfOfferees.size() == 0) {
				continue;
			}
			for (int j = 0; j < listOfOfferees.size(); ) {
				// accept 1 resource at a time from the richest
				Collections.shuffle(listOfOfferees, randomNumberGenerator);
				Collections.sort(listOfOfferees, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg1[1] - arg0[1]; // sort from high to low
					}
				});
				if (listOfOfferees.get(j)[1] - targetedNumberOfResources <= 1) {
					break;
				}
				Resource resourceWalker = agents[listOfOfferees.get(j)[0] / numcols][listOfOfferees.get(j)[0] % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					if (assignmentMatrix[listOfOfferees.get(j)[0]][k] > 0 && connectionMatrix[i][k] > 0 && resourcesNewOwner[k] == -1) {
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				if (possibleResources.size() > 0) {
					int resourceToTransfer = possibleResources.get(randomNumberGenerator.nextInt(possibleResources.size()));
					if (showDebug) {
						acceptMatrix[listOfOfferees.get(j)[0]][i] = 1;
						System.out.println("\tA" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent +" will accept R" + resourceToTransfer + " from A" + listOfOfferees.get(j)[0] + ":" + listOfOfferees.get(j)[1] + "/" + agents[listOfOfferees.get(j)[0] / numcols][listOfOfferees.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[listOfOfferees.get(j)[0] / numcols][listOfOfferees.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
					}
					listOfOfferees.get(j)[1] = listOfOfferees.get(j)[1] - 1;
					targetedNumberOfResources++;
					assignmentMatrix[listOfOfferees.get(j)[0]][resourceToTransfer] = 0;
					resourcesNewOwner[resourceToTransfer] = i;
				} else {
					listOfOfferees.remove(j);
				}
			}
		}
		if (showDebug) {
			println("acceptMatrix:");
			for (int i = 0; i < numrows * numcols; i++) {
				for (int j = 0; j < numrows * numcols; j++) {
					print("" + acceptMatrix[i][j]);
				}
				println();
			}
		}
		for (int i = 0; i < resourcesNewOwner.length; i++) {
			if (resourcesNewOwner[i] >= 0) {
				agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent--;
				agents[resourcesNewOwner[i] / numcols][resourcesNewOwner[i] % numcols].numberOfResourcesAssignedToAgent++;
				if (assignmentMatrix[assignmentVector[i]][i] != 0) {
					println("SOMETHING IS WRONG. SHOULD HAVE ALREADY BEEN SET TO 0");
				}
				assignmentMatrix[assignmentVector[i]][i] = 0;
				assignmentMatrix[resourcesNewOwner[i]][i] = 1;
				assignmentVector[i] = resourcesNewOwner[i];
				returnValue++;
			}
		}
		useAssignmentMatrix();
		return returnValue;
	}	// offer accept multi
	
	// ** multiOfferAccept *******************************************************************
	public int multiOfferOneAccept(boolean isAcceptLargestOffer) {
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfCommunications = numberOfOnes(connectionMatrix);
		int[][] offerMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of offers
		// if declination, then keep track of applications
		int[] resourcesOffered = new int[N];
		for (int i = 0; i < resourcesOffered.length; i++) {
			resourcesOffered[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) { // for every offering agent
			double averageAmongPoorerNeighbors = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; // its own resources
			double numberOfPoorerNeighbors = 1; // itself
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList <int[]> possibleOfferRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {							// i will offer to j
				if (reachMatrix[i][j] > 0 && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2) {	
					int[] possibleOfferRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					possibleOfferRecipiants.add(possibleOfferRecipiant);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
				if (reachMatrix[i][j] > 0 && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 1) {	
					averageAmongPoorerNeighbors += agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent;
					numberOfPoorerNeighbors++;
				} 	// switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			averageAmongPoorerNeighbors /= numberOfPoorerNeighbors;
			Collections.shuffle(possibleOfferRecipiants, randomNumberGenerator);
			for (int j = 0; j < possibleOfferRecipiants.size(); j++) { // look at an agent and offer all resources you can
				if (targetedNumberOfResources - 1 < averageAmongPoorerNeighbors) {
					break;
				}
				Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					if (assignmentMatrix[i][k] > 0 && connectionMatrix[possibleOfferRecipiants.get(j)[0]][k] > 0 && resourcesOffered[k] == -1) {
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				//					println(possibleResources.size() + " " + targetedNumberOfResources + " " + possibleOfferRecipiants.get(j)[1]);
				int numberOfOfferedResources = 0;
				while (targetedNumberOfResources - 1 >= averageAmongPoorerNeighbors && possibleOfferRecipiants.get(j)[1] + 1 <= averageAmongPoorerNeighbors && targetedNumberOfResources - possibleOfferRecipiants.get(j)[1] >= 2 && possibleResources.size() > 0) {
					int resourceToOffer = possibleResources.get(randomNumberGenerator.nextInt(possibleResources.size()));
					//						println("before removing " + resourceToOffer + ": " + possibleResources);
					possibleResources.remove(possibleResources.indexOf(resourceToOffer));
					//						println("after removing " + resourceToOffer + ": " + possibleResources);
					resourcesOffered[resourceToOffer] = possibleOfferRecipiants.get(j)[0];
					targetedNumberOfResources--;
					possibleOfferRecipiants.get(j)[1] = possibleOfferRecipiants.get(j)[1] + 1;
					numberOfOfferedResources++;
				}
				offerMatrix[i][possibleOfferRecipiants.get(j)[0]] = numberOfOfferedResources;
			}
		}
		if (showDebug) {
			println("agents offered to vector:");
			for (int i = 0; i < resourcesOffered.length; i++) {
				print("" + resourcesOffered[i] + " ");
			}
			println();
		}
		for (int i = 0; i < numrows * numcols; i++) {
			ArrayList<int[]> receivedOffers = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (offerMatrix[j][i] > 0) {
					int[] receivedOffer = {j, isAcceptLargestOffer ? offerMatrix[j][i] : agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					receivedOffers.add(receivedOffer);
				}
			}
			if (receivedOffers.size() == 0) {
				continue;
			}
			// gather all the offers, sort by who offered the most, and accept all from that person, receivedOffers.get(0)
			Collections.shuffle(receivedOffers, randomNumberGenerator);
			Collections.sort(receivedOffers, new Comparator<int[]>() {
				@Override
				public int compare(int[] arg0, int[] arg1) {
					return arg1[1] - arg0[1]; // sort from high to low
				}
			});
			if (showDebug)
				print("\tA" + i + " accepts");
			for (int j = 0; j < N; j++) {
				if (assignmentVector[j] == receivedOffers.get(0)[0] && resourcesOffered[j] == i) { // assignmentVector[j] is the donor, i is the acceptor, j is the resource
					if (isAcceptLargestOffer) { // dont do this if accept from richest to keep the data clean. it gets updated in useAssignmentMatrix(); anyway
					agents[assignmentVector[j] / numcols][assignmentVector[j] % numcols].numberOfResourcesAssignedToAgent--;
					agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent++;
					}
					assignmentMatrix[assignmentVector[j]][j] = 0;
					assignmentMatrix[i][j] = 1;
					assignmentVector[j] = i;
					returnValue++;
					if (showDebug)
						print(" r" + j);
				}
			}
			if (showDebug)
				println(" from A" + receivedOffers.get(0)[0]);
		}
		useAssignmentMatrix();
		return returnValue;
	} 	// offer multiaccept

	// ** OneApplyOneGive **************************************************************************
	public int oneApplyOneGive() {
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfApplies = 0;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		int[] applyMatrix = new int[numrows * numcols]; // keep track of applications
		int[] giveMatrix = new int[numrows * numcols]; // keep track of gives
		for (int i = 0; i < numrows * numcols; i++) {
			applyMatrix[i] = -1;
			giveMatrix[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			ArrayList <int[]> possibleApplicationRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {						
				if (reachMatrix[j][i] > 0 && agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent - agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= 2) {	
					int[] possibleApplicationRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					possibleApplicationRecipiants.add(possibleApplicationRecipiant);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			if (possibleApplicationRecipiants.size() < 1) {
				continue;
			}
			Collections.shuffle(possibleApplicationRecipiants, randomNumberGenerator);
			applyMatrix[i] = possibleApplicationRecipiants.get(0)[0];
			if (showDebug) {
				println("\ta" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " applies to a" + possibleApplicationRecipiants.get(0)[0] + ":" + agents[possibleApplicationRecipiants.get(0)[0] / numcols][possibleApplicationRecipiants.get(0)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleApplicationRecipiants.get(0)[0] / numcols][possibleApplicationRecipiants.get(0)[0] % numcols].numberOfResourcesConnectedToAgent);
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			ArrayList <int[]> possibleGiveRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (applyMatrix[j] == i) {
					int[] possibleGiveRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					possibleGiveRecipiants.add(possibleGiveRecipiant);
				}
			}
			if (possibleGiveRecipiants.size() < 1) {
				continue;
			}
			Collections.shuffle(possibleGiveRecipiants, randomNumberGenerator);
			Collections.sort(possibleGiveRecipiants, new Comparator<int[]>() {
				@Override
				public int compare(int[] arg0, int[] arg1) {
					return arg0[1] - arg1[1]; // sort from low to high
				}
			});
			giveMatrix[i] = possibleGiveRecipiants.get(0)[0];
			if (showDebug) {
				println("\ta" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " gives to a" + possibleGiveRecipiants.get(0)[0] + ":" + agents[possibleGiveRecipiants.get(0)[0] / numcols][possibleGiveRecipiants.get(0)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleGiveRecipiants.get(0)[0] / numcols][possibleGiveRecipiants.get(0)[0] % numcols].numberOfResourcesConnectedToAgent);
			}
		}
		int[] agentsInitialNumberOfResources = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			agentsInitialNumberOfResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		int[] resourcesNewOwner = new int[N];
		for (int i = 0; i < N; i++) {
			resourcesNewOwner[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) { // for every applyer
			if (applyMatrix[i] >= 0 && giveMatrix[applyMatrix[i]] == i) {
				int applyerInitNumberOfResources = agentsInitialNumberOfResources[i];
				int giverInitNumberOfResources = agentsInitialNumberOfResources[applyMatrix[i]];
				
				Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					if (assignmentMatrix[applyMatrix[i]][k] > 0 && connectionMatrix[i][k] > 0) {
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				Collections.shuffle(possibleResources, randomNumberGenerator);
				for (int k = 0; k < possibleResources.size() && giverInitNumberOfResources - applyerInitNumberOfResources >= 2; k++) {
					resourcesNewOwner[possibleResources.get(k)] = i;
					giverInitNumberOfResources--;
					applyerInitNumberOfResources++;
					if (showDebug) {
						println("\t\ta" + applyMatrix[i] + " gives r" + possibleResources.get(k) + " to a" + i);
					}
				}
			}
		}
		for (int i = 0; i < N; i++) {
			if (resourcesNewOwner[i] >= 0) {
				agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent--;
				agents[resourcesNewOwner[i] / numcols][resourcesNewOwner[i] % numcols].numberOfResourcesAssignedToAgent++;
				assignmentMatrix[assignmentVector[i]][i] = 0;
				assignmentMatrix[resourcesNewOwner[i]][i] = 1;
				assignmentVector[i] = resourcesNewOwner[i];
				returnValue++;
			}
		}
		useAssignmentMatrix();
		return returnValue;
	}	// End OneApplyOneGive
	// *********************************************************************************************

	// ** OneApplyMultiGive **************************************************************************
	public int oneApplyMultiGive() {
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		int[] applyMatrix = new int[numrows * numcols]; // keep track of applications
		int[] giveMatrix = new int[numrows * numcols]; // keep track of gives
		for (int i = 0; i < numrows * numcols; i++) {
			applyMatrix[i] = -1;
			giveMatrix[i] = -1;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			ArrayList <int[]> possibleApplicationRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {						
				if (reachMatrix[j][i] > 0 && agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent - agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= 2) {	
					int[] possibleApplicationRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					possibleApplicationRecipiants.add(possibleApplicationRecipiant);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			if (possibleApplicationRecipiants.size() < 1) {
				continue;
			}
			Collections.shuffle(possibleApplicationRecipiants, randomNumberGenerator);
			applyMatrix[i] = possibleApplicationRecipiants.get(0)[0];
			if (showDebug) {
				println("\ta" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " applies to a" + possibleApplicationRecipiants.get(0)[0] + ":" + agents[possibleApplicationRecipiants.get(0)[0] / numcols][possibleApplicationRecipiants.get(0)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleApplicationRecipiants.get(0)[0] / numcols][possibleApplicationRecipiants.get(0)[0] % numcols].numberOfResourcesConnectedToAgent);
			}
		}
		int[] tempAgentsNumberOfAssignedResources = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			tempAgentsNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		int[] isResourceAlreadyGiven = new int[N];
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList <int[]> possibleGiveRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (applyMatrix[j] == i) {
					int[] possibleGiveRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					possibleGiveRecipiants.add(possibleGiveRecipiant);
				}
			}
			if (possibleGiveRecipiants.size() < 1) {
				continue;
			}
			for (int j = 0; j < possibleGiveRecipiants.size(); ) {
				Collections.shuffle(possibleGiveRecipiants, randomNumberGenerator);
				Collections.sort(possibleGiveRecipiants, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg0[1] - arg1[1]; // sort from low to high
					}
				});
				if (targetedNumberOfResources - possibleGiveRecipiants.get(j)[1] <= 1) {
					break;
				}
				Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					if (assignmentVector[k] == i && applyMatrix[possibleGiveRecipiants.get(j)[0]] == i && connectionMatrix[possibleGiveRecipiants.get(j)[0]][k] > 0 && isResourceAlreadyGiven[k] == 0) {
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				if (possibleResources.size() < 1) {
					possibleGiveRecipiants.remove(j);
					continue;
				}
				int resourceToGive = possibleResources.get(randomNumberGenerator.nextInt(possibleResources.size()));
				targetedNumberOfResources--;
				tempAgentsNumberOfAssignedResources[assignmentVector[resourceToGive]]++;
				tempAgentsNumberOfAssignedResources[i]--;
				assignmentMatrix[assignmentVector[resourceToGive]][resourceToGive] = 0;
				assignmentMatrix[possibleGiveRecipiants.get(0)[0]][resourceToGive] = 1;
				assignmentVector[resourceToGive] = possibleGiveRecipiants.get(0)[0];
				returnValue++;
				isResourceAlreadyGiven[resourceToGive] = 1; // used to make sure that it is not regifted
				// the winner is random because the owner decides who to give it to
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent = tempAgentsNumberOfAssignedResources[i];
		}
		useAssignmentMatrix();
		return returnValue;
	}	// End OneApplyMultiGive
	// *********************************************************************************************

	// ** MultiApplyOneGive ************************************************************************
	public int multiApplyOneGive() {
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfApplies = 0;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		int[][] applyMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of applications
		int[][] resourcesApplied = new int[numrows * numcols][N];
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			double averageAmongRicherNeighbors = targetedNumberOfResources;
			ArrayList <int[]> possibleApplicationRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {						
				if (reachMatrix[j][i] > 0 && agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent - agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= 1) {	
					int[] possibleApplicationRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					averageAmongRicherNeighbors += possibleApplicationRecipiant[1];
					possibleApplicationRecipiants.add(possibleApplicationRecipiant);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			averageAmongRicherNeighbors /= possibleApplicationRecipiants.size() + 1.0;
			if (possibleApplicationRecipiants.size() < 1) {
				continue;
			}
			Collections.shuffle(possibleApplicationRecipiants, randomNumberGenerator);
			for (int j = 0; j < possibleApplicationRecipiants.size(); j++) {
				Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					if (assignmentMatrix[possibleApplicationRecipiants.get(j)[0]][k] > 0 && connectionMatrix[i][k] > 0) {
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				//				println(possibleResources.size() + " " + targetedNumberOfResources + " " + possibleOfferRecipiants.get(j)[1]);
				int numberOfAppliedResources = 0;
				while (possibleApplicationRecipiants.get(j)[1] > averageAmongRicherNeighbors && possibleApplicationRecipiants.get(j)[1] - targetedNumberOfResources >= 2 && possibleResources.size() > 0 && averageAmongRicherNeighbors - targetedNumberOfResources >= 1) {
					int resourceToApply = possibleResources.get(randomNumberGenerator.nextInt(possibleResources.size()));
					if (showDebug) {
						println("\t\ta" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " applies for r" + resourceToApply + " from a" + possibleApplicationRecipiants.get(j)[0] + ":" + possibleApplicationRecipiants.get(j)[1] + "/" + agents[possibleApplicationRecipiants.get(j)[0] / numcols][possibleApplicationRecipiants.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleApplicationRecipiants.get(j)[0] / numcols][possibleApplicationRecipiants.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
					}
					resourcesApplied[i][resourceToApply] = 1;
					possibleResources.remove(possibleResources.indexOf(resourceToApply));
					//					println("after removing " + resourceToOffer + ": " + possibleResources);
					targetedNumberOfResources++;
					possibleApplicationRecipiants.get(j)[1] = possibleApplicationRecipiants.get(j)[1] - 1;
					numberOfAppliedResources++;
					//					println("\t\t\t\t" + (possibleOfferRecipiants.get(j)[1] < averageAmongPoorerNeighbors) + " " + (targetedNumberOfResources - possibleOfferRecipiants.get(j)[1] >= 2) + " " + (possibleResources.size() > 0) + " " + (targetedNumberOfResources - averageAmongPoorerNeighbors >= 1));
				}
				applyMatrix[i][possibleApplicationRecipiants.get(j)[0]] = numberOfAppliedResources;
				numberOfApplies += numberOfAppliedResources;
			}
		}
		// END of apply step
		if (showDebug) {
			for (int i = 0; i < resourcesApplied.length; i++) {
				for (int j = 0; j < resourcesApplied[0].length; j++) {
					print(resourcesApplied[i][j]);
				}
				println();
			}
			println();
			for (int i = 0; i < applyMatrix.length; i++) {
				for (int j = 0; j < applyMatrix[0].length; j++) {
					print(applyMatrix[i][j]);
				}
				println();
			}
		}
		int[] tempAgentsNumberOfAssignedResources = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			tempAgentsNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		int[] isResourceAlreadyGiven = new int[N];
		for (int i = 0; i < numrows * numcols; i++) {
			ArrayList <int[]> possibleGiveRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (applyMatrix[j][i] > 0) {
					int[] possibleGiveRecipiant = {j, applyMatrix[j][i]};
					possibleGiveRecipiants.add(possibleGiveRecipiant);
				}
			}
			if (possibleGiveRecipiants.size() > 0) {
				Collections.shuffle(possibleGiveRecipiants, randomNumberGenerator);
				Collections.sort(possibleGiveRecipiants, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg1[1] - arg0[1]; // sort from high to low
					}
				});
				ArrayList<Integer> resourcesToGive = new ArrayList<Integer>();
				for (int j = 0; j < N; j++) {
					if (assignmentVector[j] == i && resourcesApplied[possibleGiveRecipiants.get(0)[0]][j] > 0 && isResourceAlreadyGiven[j] == 0) { // assignmentVector[j] is the donor, i is the acceptor, j is the resource
						resourcesToGive.add(j);
					}
				}
				if (resourcesToGive.size() == 0) {
					possibleGiveRecipiants.remove(0);
				} else {
					for (int k = 0; k < resourcesToGive.size(); k++) {
						int resourceToGive = resourcesToGive.get(k);
						tempAgentsNumberOfAssignedResources[assignmentVector[resourceToGive]]++;
						tempAgentsNumberOfAssignedResources[i]--;
						assignmentMatrix[assignmentVector[resourceToGive]][resourceToGive] = 0;
						assignmentMatrix[possibleGiveRecipiants.get(0)[0]][resourceToGive] = 1;
						assignmentVector[resourceToGive] = possibleGiveRecipiants.get(0)[0];
						returnValue++;
						if (showDebug) {
							println("\t\ta" + i + " gave r" + resourceToGive + " to a" + possibleGiveRecipiants.get(0)[0]);
						}
						isResourceAlreadyGiven[resourceToGive] = 1;  // used to make sure that it is not regifted
						// the winner is random because the owner decides who to give it to
					}
				}
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent = tempAgentsNumberOfAssignedResources[i];
		}
		useAssignmentMatrix();
		return returnValue;
	}	// End MultiApplyOneGive
	// *********************************************************************************************

	// ** MultipleApplyMultipleGive ****************************************************************
	public int multiApplyMultiGive(boolean isOneAgentAtATime) {
		int returnValue = 0;
		numberOfPhases = 3;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		int[][] thresholdMatrix = new int[numrows * numcols][numrows * numcols];
		int[][] applyMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of applications
		int[][] resourcesApplied = new int[numrows * numcols][N];
		for (int i = 0; i < numrows * numcols; i++) {
			int targetedNumberOfResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			double averageAmongRicherNeighbors = targetedNumberOfResources;
			ArrayList <int[]> possibleApplicationRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {						
				if (reachMatrix[j][i] > 0 && agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent - agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= 1) {	
					int[] possibleApplicationRecipiant = {j, agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent};
					averageAmongRicherNeighbors += possibleApplicationRecipiant[1];
					possibleApplicationRecipiants.add(possibleApplicationRecipiant);
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			averageAmongRicherNeighbors /= possibleApplicationRecipiants.size() + 1;
			if (possibleApplicationRecipiants.size() < 1) {
				continue;
			}
			Collections.shuffle(possibleApplicationRecipiants, randomNumberGenerator);
			if (!isOneAgentAtATime) { // if isOneResourceAtATime
				Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					possibleResources.add(k);
					resourceWalker = resourceWalker.nextResource;
				}
				Collections.shuffle(possibleResources, randomNumberGenerator);
				for (int k = 0; k < possibleResources.size() && averageAmongRicherNeighbors - targetedNumberOfResources >= 1; k++) { // checks that you do not go above boundary
					Collections.shuffle(possibleApplicationRecipiants, randomNumberGenerator);
					for (int j = 0; j < possibleApplicationRecipiants.size(); j++) {
						if (possibleApplicationRecipiants.get(j)[1] > averageAmongRicherNeighbors && possibleApplicationRecipiants.get(j)[1] - targetedNumberOfResources >= 2 && assignmentMatrix[possibleApplicationRecipiants.get(j)[0]][possibleResources.get(k)] > 0) {
							// checks that they do not go below boundary
							int resourceToOffer = possibleResources.get(k);
							if (showDebug) {
								println("\t\ta" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " applies for r" + resourceToOffer + " from a" + possibleApplicationRecipiants.get(j)[0] + ":" + possibleApplicationRecipiants.get(j)[1] + "/" + agents[possibleApplicationRecipiants.get(j)[0] / numcols][possibleApplicationRecipiants.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleApplicationRecipiants.get(j)[0] / numcols][possibleApplicationRecipiants.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
							}
							resourcesApplied[i][resourceToOffer] = 1;
							targetedNumberOfResources++;
							possibleApplicationRecipiants.get(j)[1] = possibleApplicationRecipiants.get(j)[1] - 1;
							applyMatrix[i][possibleApplicationRecipiants.get(j)[0]]++;
							numberOfApplies++;
							break;
						}
					}
				}			
			} else { // if !isApplyOneResourceAtATime || isApplyOneAgentAtATime
				for (int j = 0; j < possibleApplicationRecipiants.size(); j++) {
					Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)   // from i to j
					{					// keep walking until valid resource
						int k = resourceWalker.ID;
						if (assignmentMatrix[possibleApplicationRecipiants.get(j)[0]][k] > 0 && connectionMatrix[i][k] > 0) {
							possibleResources.add(k);
						}
						resourceWalker = resourceWalker.nextResource;
					}
					//				println(possibleResources.size() + " " + targetedNumberOfResources + " " + possibleOfferRecipiants.get(j)[1]);
					int numberOfAppliedResources = 0;
					while (possibleApplicationRecipiants.get(j)[1] > averageAmongRicherNeighbors && possibleApplicationRecipiants.get(j)[1] - targetedNumberOfResources >= 2 && possibleResources.size() > 0 && averageAmongRicherNeighbors - targetedNumberOfResources >= 1) {
						int resourceToApply = possibleResources.get(randomNumberGenerator.nextInt(possibleResources.size()));
						if (showDebug) {
							println("\t\ta" + i + ":" + targetedNumberOfResources + "/" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/" + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " applies for r" + resourceToApply + " from a" + possibleApplicationRecipiants.get(j)[0] + ":" + possibleApplicationRecipiants.get(j)[1] + "/" + agents[possibleApplicationRecipiants.get(j)[0] / numcols][possibleApplicationRecipiants.get(j)[0] % numcols].numberOfResourcesAssignedToAgent + "/" + agents[possibleApplicationRecipiants.get(j)[0] / numcols][possibleApplicationRecipiants.get(j)[0] % numcols].numberOfResourcesConnectedToAgent);
						}
						resourcesApplied[i][resourceToApply] = 1;
						possibleResources.remove(possibleResources.indexOf(resourceToApply));
						//					println("after removing " + resourceToOffer + ": " + possibleResources);
						targetedNumberOfResources++;
						possibleApplicationRecipiants.get(j)[1] = possibleApplicationRecipiants.get(j)[1] - 1;
						numberOfAppliedResources++;
						//					println("\t\t\t\t" + (possibleOfferRecipiants.get(j)[1] < averageAmongPoorerNeighbors) + " " + (targetedNumberOfResources - possibleOfferRecipiants.get(j)[1] >= 2) + " " + (possibleResources.size() > 0) + " " + (targetedNumberOfResources - averageAmongPoorerNeighbors >= 1));
					}
					applyMatrix[i][possibleApplicationRecipiants.get(j)[0]] = numberOfAppliedResources;
					numberOfApplies += numberOfAppliedResources;
				}
			} 
			for (int j = 0; j < possibleApplicationRecipiants.size(); j++) {
				thresholdMatrix[i][possibleApplicationRecipiants.get(j)[0]] = targetedNumberOfResources;
			}
		}
		// END of apply step
		int[] tempAgentsNumberOfAssignedResources = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			tempAgentsNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		int[] isResourceAlreadyGiven = new int[N];
		for (int i = 0; i < numrows * numcols; i++) {
			int tempNumberOfAssignedResourcesToThisAgent = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			ArrayList <int[]> possibleGiveRecipiants = new ArrayList<int[]>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (applyMatrix[j][i] > 0) {
					int[] possibleGiveRecipiant = {j, thresholdMatrix[j][i]};
					possibleGiveRecipiants.add(possibleGiveRecipiant);
				}
			}
			while (possibleGiveRecipiants.size() > 0) {
				Collections.shuffle(possibleGiveRecipiants, randomNumberGenerator);
				Collections.sort(possibleGiveRecipiants, new Comparator<int[]>() {
					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg0[1] - arg1[1]; // sort from low to high
					}
				});
				if (tempNumberOfAssignedResourcesToThisAgent <= possibleGiveRecipiants.get(0)[1]) { // break if less than equal highest threshold
					break;
				}
				ArrayList<Integer> resourcesToGive = new ArrayList<Integer>();
				for (int j = 0; j < N; j++) {
					if (assignmentVector[j] == i && resourcesApplied[possibleGiveRecipiants.get(0)[0]][j] > 0 && isResourceAlreadyGiven[j] == 0) { // assignmentVector[j] is the donor, i is the acceptor, j is the resource
						resourcesToGive.add(j);
					}
				}
				if (resourcesToGive.size() == 0) {
					possibleGiveRecipiants.remove(0);
				} else {
					int resourceToGive = resourcesToGive.get(randomNumberGenerator.nextInt(resourcesToGive.size()));

					tempNumberOfAssignedResourcesToThisAgent--;
					tempAgentsNumberOfAssignedResources[assignmentVector[resourceToGive]]++;
					tempAgentsNumberOfAssignedResources[i]--;
					assignmentMatrix[assignmentVector[resourceToGive]][resourceToGive] = 0;
					assignmentMatrix[possibleGiveRecipiants.get(0)[0]][resourceToGive] = 1;
					assignmentVector[resourceToGive] = possibleGiveRecipiants.get(0)[0];
					returnValue++;
					isResourceAlreadyGiven[resourceToGive] = 1; // used to make sure that it is not regifted
					// the winner is random because the owner decides who to give it to
				}
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent = tempAgentsNumberOfAssignedResources[i];
		}
		useAssignmentMatrix();
		return returnValue;
	}	// End MultiApplyMultiGive
	// *********************************************************************************************

	// ** Bidding **********************************************************************************
	public int bidding(boolean improvement, int aggressiveness)// 0 = peak; 1 = high; 2 = mid; 3 = low; 4 = random; 5 = growing; 6 = random winner;
	{
		int[] originalNumberOfResources = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			originalNumberOfResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		numberOfPhases = 3;
		numberOfCommunications = numberOfOnes(connectionMatrix);
		printMatrices(12);
		if (numberOfBidPeakDeclinations == 4 && aggressiveness == 0) {
			printMatrices(16);
		}
		double initialFI = scaledAgentFairnessIndex();
		int[][] sinkMatrix = new int[numrows * numcols][numrows * numcols];
		int[][] sourceMatrix = new int[numrows * numcols][numrows * numcols];
		if (showAlgorithmVisual)
			printDOT2DVisualization(10);		// print out the current DOT Language with resource prices
		int returnValue = 0;
		bidMatrix = new int[numrows * numcols][maxID + 1]; // used to keep track of each bid on each resource
		agentsBiddingPrices = new int[numrows * numcols]; // used to keep track of each agents bid that they place
		highestBidArray = new int[maxID + 1][2]; // used to store the agent of the winning bid; it's price; and number of contenders remainining
		int algorithmBidOverride = 0; // used to determine if there should be a prebidding matrix
		for (int i = 0; i < algorithmBidWhoOrderAndExecution.length; i++) // check for a 10 (bid based on the rank of winning) or 11 (bid based on the approx probability of
		// winning) or 12 (exact prob of winning)
		{
			if (algorithmBidWhoOrderAndExecution[i] >= 10) {
				if (algorithmBidWhoOrderAndExecution[i] == 10) // ranking
					algorithmBidOverride = 1;
				if (algorithmBidWhoOrderAndExecution[i] == 11) // approx prob of winning
					algorithmBidOverride = 2;
				if (algorithmBidWhoOrderAndExecution[i] == 12) // exact prob of winning
					algorithmBidOverride = 3;
				break;
			}
		}
		if (algorithmBidOverride > 0) {
			int[][][] preBiddingMatrix = new int[numrows * numcols][maxID + 1][2]; 
			// for every agent for every resource there is 1) bid value and 2) rank of winning
			double[][][] doublePreBiddingMatrix = new double[numrows * numcols][maxID + 1][2]; 
			// for every agent for every resource there is probability of bidding and probability of winning
			for (int i = 0; i < numrows * numcols; i++) {
				int[][] possibleResourcesToBidOn = new int[agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent][2]; // keep tack of resources bids were placed on and
				// if a tie breaker is set
				int[] tieBreakerCode = new int[10]; // used to keep track if certain tie breakers have been executed
				Resource connectedResourceWalker = agents[i / numcols][i % numcols].firstResource;
				// place all the assigned resourced into the array of resources to bid on
				for (int j = 0; j < agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;) {
					if (assignmentMatrix[i][connectedResourceWalker.ID] > 0) {
						possibleResourcesToBidOn[j][0] = connectedResourceWalker.ID;
						possibleResourcesToBidOn[j][1] = 1;
						j++;
					}
					connectedResourceWalker = connectedResourceWalker.nextResource;
				}
				connectedResourceWalker = agents[i / numcols][i % numcols].firstResource;
				// put the rest of the connected resources (difference resources) into the array of resources to bid on
				for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;) {
					if (assignmentMatrix[i][connectedResourceWalker.ID] < 1) {
						possibleResourcesToBidOn[j][0] = connectedResourceWalker.ID;
						j++;
					}
					connectedResourceWalker = connectedResourceWalker.nextResource;
				}
				if (showDebug) {
					System.out.print("\t a" + i + " possible resources to bid on befre sorting: ");
					for (int j = 0; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++)
						System.out
								.print("r"
										+ possibleResourcesToBidOn[j][0]
										+ " priced at "
										+ agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
												% numcols].numberOfResourcesAssignedToAgent + ", ");
					System.out.println();
				}
				sortResourcesToBid(0, i, possibleResourcesToBidOn, tieBreakerCode);
				if (showDebug) {
					System.out.print("\t a" + i + " possible resources to bid on after sorting: ");
					for (int j = 0; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++)
						System.out
								.print("r"
										+ possibleResourcesToBidOn[j][0]
										+ " priced at "
										+ agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
												% numcols].numberOfResourcesAssignedToAgent + ", ");
					System.out.println();
				}
				int biddingPrice;
				if (showDebug)
					System.out.print("\ta" + i + " is checking out ");
				for (biddingPrice = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; biddingPrice < possibleResourcesToBidOn.length
						&& biddingPrice + 1 < agents[assignmentVector[possibleResourcesToBidOn[biddingPrice][0]] / numcols][assignmentVector[possibleResourcesToBidOn[biddingPrice][0]]
								% numcols].numberOfResourcesAssignedToAgent; biddingPrice++) {
					if (showDebug)
						System.out.print("r" + possibleResourcesToBidOn[biddingPrice][0] + " priced at "
										+ (agents[assignmentVector[possibleResourcesToBidOn[biddingPrice][0]] / numcols][assignmentVector[possibleResourcesToBidOn[biddingPrice][0]]
												% numcols].numberOfResourcesAssignedToAgent) + " ,");
				}
				if (showDebug)
					System.out.println();
				if (biddingPrice == 0
						&& 0 < possibleResourcesToBidOn.length
						&& agents[assignmentVector[possibleResourcesToBidOn[biddingPrice][0]] / numcols][assignmentVector[possibleResourcesToBidOn[biddingPrice][0]]
								% numcols].numberOfResourcesAssignedToAgent == 0)
					biddingPrice = agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;
				agentsBiddingPrices[i] = biddingPrice;
				if (showDebug)
					System.out.println("agent " + i + "'s bidding price is " + biddingPrice);
				// bluff bid on every connected resource
				for (int j = 0; j < maxID + 1; j++) {
					if (connectionMatrix[i][j] > 0) {
						preBiddingMatrix[i][j][0] = biddingPrice;
						preBiddingMatrix[i][j][1] = 1;
						if (assignmentMatrix[i][j] > 0)
							doublePreBiddingMatrix[i][j][0] = 1;
						else
							// there should be no problem with the denominator because it can only be 0 if you are only bidding on only those that are assigned to the agent
							doublePreBiddingMatrix[i][j][0] = (double) ((biddingPrice - agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent) / (double) (possibleResourcesToBidOn.length - agents[i
									/ numcols][i % numcols].numberOfResourcesAssignedToAgent));
						if (showDebug)
							System.out
									.println("\tthe probability of a" + i + " to bid on r" + j + " is " + doublePreBiddingMatrix[i][j][0]);
					}
				}
			}

			// update the rank of each agent according to the bids of other agents
			for (int i = 0; i < maxID + 1; i++) {
				ArrayList<Integer> agentsThatBiddedOnResourcei = new ArrayList<Integer>();
				for (int j = 0; j < numrows * numcols; j++) {
					if (doublePreBiddingMatrix[j][i][0] > 0) {
						agentsThatBiddedOnResourcei.add(j);
						// RANKING ONLY (NOT Probability)
						// having someone in the competition will affect the rank of everyone else bidding for the same resource
						// for (int k = 0; k<numrows*numcols; k++)
						// {
						// if (preBiddingMatrix[k][i][0]>0&&preBiddingMatrix[k][i]==preBiddingMatrix[j][i])
						// {
						// preBiddingMatrix[k][i][1]++;
						// }
						// if (preBiddingMatrix[k][i][0]>0&&preBiddingMatrix[k][i][0]>preBiddingMatrix[j][i][0])
						// { // if k has a lower bid, then k chances of winning the bid will be lowered
						// preBiddingMatrix[k][i][1] += 2;
						// }
						// }
					}
				}
				// show agents that placed a bid on the resource before sorting
				if (showDebug) {
					System.out.print("\tthe order for winning r" + i + " bfore sorting is ");
					for (int j = 0; j < agentsThatBiddedOnResourcei.size(); j++) {
						System.out.print("a" + agentsThatBiddedOnResourcei.get(j) + " bidding "
								+ preBiddingMatrix[agentsThatBiddedOnResourcei.get(j)][i][0] + " w/ p "
								+ doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j)][i][0] + ", ");
					}
					System.out.println();
				}
				// bubble sort based on bidding price
				for (int j = 0; j < agentsThatBiddedOnResourcei.size(); j++) {
					for (int k = j + 1; k < agentsThatBiddedOnResourcei.size(); k++) {
						if (preBiddingMatrix[agentsThatBiddedOnResourcei.get(j)][i][0] > preBiddingMatrix[agentsThatBiddedOnResourcei
								.get(k)][i][0]) {
							if (showDebug)
								System.out.println("position " + j + " will be switched with position " + k);
							int temp = agentsThatBiddedOnResourcei.get(j);
							agentsThatBiddedOnResourcei.set(j, agentsThatBiddedOnResourcei.get(k));
							agentsThatBiddedOnResourcei.set(k, temp);
						}
					}
				}
				// show agents that placed a bid on the resource after sorting
				if (showDebug) {
					System.out.print("\tthe order for winning r" + i + " after sorting is ");
					for (int j = 0; j < agentsThatBiddedOnResourcei.size(); j++) {
						System.out.print("a" + agentsThatBiddedOnResourcei.get(j) + " bidding "
								+ preBiddingMatrix[agentsThatBiddedOnResourcei.get(j)][i][0] + " w/ p "
								+ doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j)][i][0] + ", ");
					}
					System.out.println();
				}
				// determine the rank and probability of all the agents
				double probabilityOfPassingCarry = 1;
				for (int j = 0; j < agentsThatBiddedOnResourcei.size();) // j will increment by tieVariable
				{
					// System.out.println("\t\tprobabilityOfPassingCarry is currently "+probabilityOfPassingCarry);
					int tieVariable = j; // used for beginning tie, tie walker, and tieDifference
					while (tieVariable < agentsThatBiddedOnResourcei.size()
							&& preBiddingMatrix[agentsThatBiddedOnResourcei.get(j)][i][0] == preBiddingMatrix[agentsThatBiddedOnResourcei
									.get(tieVariable)][i][0])
						tieVariable++;
					tieVariable = tieVariable - j; // now used as the number of ties;
					if (showDebug)
						System.out.println("\tj is currently " + j + " while tieVariable is " + tieVariable);
					double probabilityOfPassingCurrent = 1;
					double totalTieProbability = 0;
					for (int k = 0; k < tieVariable; k++) {
						probabilityOfPassingCurrent *= 1 - doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][0];
						totalTieProbability += doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][0];
					}
					for (int k = 0; k < tieVariable; k++) {
						doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1] = (double) probabilityOfPassingCarry
								* (1 - probabilityOfPassingCurrent) * doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][0]
								/ totalTieProbability;
						// doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j+k)][i][1] =
						// (double)probabilityOfPassingCarry*(1-probabilityOfPassingCurrent)/(double)tieVariable;
						preBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1] += tieVariable;
						if (showDebug) {				// display the approximate probabilities of winning
							System.out.println("\t\tapprx r" + i + " gives a" + agentsThatBiddedOnResourcei.get(j + k) + " has a "
									+ doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1] + " prob of winning and ranked "
									+ preBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1]);
						}
					}
					if (algorithmBidOverride == 3) {
						for (int k = 0; k < tieVariable; k++) {
							doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1] = 0;
						}
						int[][] binaryCount = new int[(int) Math.pow(2, tieVariable)][tieVariable];
						if (showDebug)
							System.out.println("\t\t(int)Math.pow(2, tieVariable) = " + (int) Math.pow(2, tieVariable));
						for (int counter = 0; counter < Math.pow(2, tieVariable); counter++) {
							int remainder = counter;
							int onesCounter = 0;
							double totalShowUpProbability = 1;
							for (int k = 0; k < tieVariable; k++) {
								if (remainder >= Math.pow(2, tieVariable - k - 1)) {
									binaryCount[counter][k] = 1;
									remainder -= Math.pow(2, tieVariable - k - 1);
									onesCounter++;
									totalShowUpProbability *= doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][0];
								} else
									totalShowUpProbability *= 1 - doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][0];
							}
							for (int k = 0; k < tieVariable; k++) // update for all agents in tie
							{
								if (binaryCount[counter][k] > 0)
									doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1] += probabilityOfPassingCarry
											* totalShowUpProbability / onesCounter;
							}
						}
						if (showDebug) {				// display the exact probabilities of winning
							for (int k = 0; k < tieVariable; k++) {
								System.out.println("\t\texact r" + i + " gives a" + agentsThatBiddedOnResourcei.get(j + k) + " has a "
										+ doublePreBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1]
										+ " prob of winning and ranked " + preBiddingMatrix[agentsThatBiddedOnResourcei.get(j + k)][i][1]);
							}
						}
						if (showDebug) {				// display the binary table for this set of ties
							System.out.println("Binary Table:");
							for (int k = 0; k < binaryCount.length; k++) {
								for (int l = 0; l < binaryCount[0].length; l++) {
									System.out.print(binaryCount[k][l]);
								}
								System.out.println();
							}
						}
					}
					probabilityOfPassingCarry *= probabilityOfPassingCurrent;
					j += tieVariable;
					if (showDebug)		// just debug counter to check if every tied agent is checked
						System.out.println("\t\t\tj is now " + j + " after update");
					for (int k = j; k < agentsThatBiddedOnResourcei.size(); k++)
						preBiddingMatrix[agentsThatBiddedOnResourcei.get(k)][i][1] += 2;
				}
			}

			// debug ranking information of every agent on every resource
			if (showDebug) {
				System.out.println("agentsRanking:");
				for (int j = 0; j < maxID + 1; j++) {
					System.out.print(j % 10 + ",");
				}
				System.out.println();
				for (int i = 0; i < numrows * numcols; i++) {
					for (int j = 0; j < maxID + 1; j++) {
						System.out.print(preBiddingMatrix[i][j][1] + ",");
					}
					System.out.println();
				}
			}

			// let resources rank/sort the agents
			for (int i = 0; i < numrows * numcols; i++) {
				int remainingBidsToPlace = agentsBiddingPrices[i];
				ArrayList<Integer> resourcesToDecideBetween = new ArrayList<Integer>();
				for (int j = 0; j < maxID + 1; j++) {
					if (connectionMatrix[i][j] > 0) {
						if (assignmentMatrix[i][j] > 0) {
							bidMatrix[i][j] = agentsBiddingPrices[i];
							remainingBidsToPlace--;
						} else
							resourcesToDecideBetween.add(j);
					}
				}
				if (remainingBidsToPlace > 0) {
					if (showDebug) {
						System.out.print("before scramble of agent" + i + ":");
						for (int j = 0; j < resourcesToDecideBetween.size(); j++)
							System.out.print(resourcesToDecideBetween.get(j) + ",");
						System.out.println();
					}
					// scramble
					int FisherYatesLength = resourcesToDecideBetween.size();
					int[][] scrambledArray = new int[FisherYatesLength][3]; // 2D array required scrambled ints, scramble checker, and temp array
					int randomRange = FisherYatesLength;
					for (int k = 0; k < FisherYatesLength; k++) {
						int roll = randomNumberGenerator.nextInt(randomRange--);// (int)Math.floor(Math.random()*randomRange--); // take a new random int
						int arrayWalker = 0;
						while (roll > 0 || scrambledArray[arrayWalker][1] == 1) {
							if (scrambledArray[arrayWalker++][1] == 0)
								roll--;		// only count the integer walker if the checkList not already counted
						}
						scrambledArray[arrayWalker][1] = 1;
						scrambledArray[k][0] = arrayWalker;
					}
					// separate for loop to copy to temp array
					for (int k = 0; k < FisherYatesLength; k++)
						scrambledArray[k][2] = resourcesToDecideBetween.get(scrambledArray[k][0]);
					if (showDebug) {
						System.out.print(" after scramble of agent" + i + ":");
						for (int j = 0; j < resourcesToDecideBetween.size(); j++)
							System.out.print(scrambledArray[j][2] + ",");
						System.out.println();
					}
					// then bubble sort by probability of winning
					for (int j = 0; j < resourcesToDecideBetween.size(); j++) {
						for (int k = j + 1; k < resourcesToDecideBetween.size(); k++) {
							if ((algorithmBidOverride == 1 && preBiddingMatrix[i][scrambledArray[j][2]][1] > preBiddingMatrix[i][scrambledArray[k][2]][1])
									|| (algorithmBidOverride > 1 && doublePreBiddingMatrix[i][scrambledArray[j][2]][1] < doublePreBiddingMatrix[i][scrambledArray[k][2]][1])) {
								int tempInt = scrambledArray[j][2];
								scrambledArray[j][2] = scrambledArray[k][2];
								scrambledArray[k][2] = tempInt;
							}
						}
					}

					if (showDebug) {
						System.out.print("after bubb sorting agent" + i + ":");
						for (int j = 0; j < FisherYatesLength; j++)
							System.out.print(scrambledArray[j][2] + ",");
						System.out.println();
					}
					for (int j = 0; j < remainingBidsToPlace; j++) {
						bidMatrix[i][scrambledArray[j][2]] = agentsBiddingPrices[i];
					}
				}	// end if bidsRemainingToPlace>0 loop

			}
		}	// end if the algorithm contains 1X
		else// if using locked-in multiple tie breakers
		{
			// reset the data structures already used
			// agentsBiddingPrices = new int[numrows*numcols]; // used to keep track of each agents bid that they place
			for (int i = 0; i < numrows * numcols; i++) {
				int[][] possibleResourcesToBidOn = new int[agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent][2]; 
				// keep tack of resources bids were placed on and
				// if a tie breaker is set
				int[] tieBreakerCode = new int[10]; // used to keep track if certain tie breakers have been executed
				Resource connectedResourceWalker = agents[i / numcols][i % numcols].firstResource;
				// place all the assigned resourced into the array of resources to bid on
				for (int j = 0; j < agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;) {
					// if (connectedResourceWalker==null)
					// continue; // check for null resource because agents may have no resources
					if (assignmentMatrix[i][connectedResourceWalker.ID] > 0) {
						possibleResourcesToBidOn[j][0] = connectedResourceWalker.ID;
						possibleResourcesToBidOn[j][1] = 1;
						j++;
					}
					connectedResourceWalker = connectedResourceWalker.nextResource;
				}
				connectedResourceWalker = agents[i / numcols][i % numcols].firstResource;
				// put the rest of the connected resources (difference resources) into the array of resources to bid on
				for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;) {
					if (assignmentMatrix[i][connectedResourceWalker.ID] < 1) {
						possibleResourcesToBidOn[j][0] = connectedResourceWalker.ID;
						j++;
					}
					connectedResourceWalker = connectedResourceWalker.nextResource;
				}
				// print out the debug information
				for (int currentAlgorithm = -1; currentAlgorithm < algorithmBidWhoOrderAndExecution.length; currentAlgorithm++) {
					boolean booleanToDisplay = false;
					if (currentAlgorithm > -1 && algorithmBidWhoOrderAndExecution[currentAlgorithm] < 10)
						// bubble sort the difference resources
						booleanToDisplay = sortResourcesToBid(algorithmBidWhoOrderAndExecution[currentAlgorithm], i, possibleResourcesToBidOn, tieBreakerCode);
					if (showDebug)// &&assignmentVector[possibleResourcesToBidOn[agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent][0]]>=0)
					{
						if (currentAlgorithm == -1)
							System.out.print("\t\ta" + i + " be4 srt: ");
						else
							System.out.print("\t\tafter sort" + algorithmBidWhoOrderAndExecution[currentAlgorithm] + ":");
						for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
							System.out.print(" " + possibleResourcesToBidOn[j][0]);
						}
						System.out.print("; priced:");
						for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
							System.out
									.print(" "
											+ agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
													% numcols].numberOfResourcesAssignedToAgent);// resourcePrice[possibleResourcesToBidOn[j][0]]+" ");
						}
						System.out.print("; numOfConToResource:");
						for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
							System.out.print(" " + numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]]);
						}
						System.out.print("; numOfConResrcesToAssgnedAgnt:");
						for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
							System.out
									.print(" "
											+ agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
													% numcols].numberOfResourcesConnectedToAgent);
						}
						System.out.print("; totalPriceOfAllConResrcesToAssgnedAgnt:");
						for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
							System.out.print(" " + totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]]);
						}
						System.out.print("; CompltnCde:");
						for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
							System.out.print(" " + possibleResourcesToBidOn[j][1]);
						}
						System.out.println("; doneSorting: " + booleanToDisplay);
					}
				}
				int agentsNumberOfAssignedResources = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
				int agentsBid = agentsNumberOfAssignedResources;
				while (agentsBid < possibleResourcesToBidOn.length
						&& (agentsBid + 1 < agents[assignmentVector[possibleResourcesToBidOn[agentsBid][0]] / numcols][assignmentVector[possibleResourcesToBidOn[agentsBid][0]]
								% numcols].numberOfResourcesAssignedToAgent || agents[assignmentVector[possibleResourcesToBidOn[agentsBid][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[agentsBid][0]] % numcols].numberOfResourcesAssignedToAgent == 0)) {
					agentsBid++;			// find the amount that the agent should bid;
				}
				if (aggressiveness == 0) {
					if (agentsBid < possibleResourcesToBidOn.length
						&& (agentsBid + 1 <= agents[assignmentVector[possibleResourcesToBidOn[agentsBid][0]] / numcols][assignmentVector[possibleResourcesToBidOn[agentsBid][0]]
								% numcols].numberOfResourcesAssignedToAgent || agents[assignmentVector[possibleResourcesToBidOn[agentsBid][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[agentsBid][0]] % numcols].numberOfResourcesAssignedToAgent == 0)) {
					agentsBid++;			// find the amount that the agent should bid;
					}
				}
				if (aggressiveness == 1) {
				}
				if (aggressiveness == 2) {
					// System.out.print("before bid = " + agentsBid + " agents owned = " + agentsNumberOfAssignedResources);
					if (agentsBid > agentsNumberOfAssignedResources)			// if sum is odd and bid is greater than number of agents
					{
						agentsBid = (agentsBid+agentsNumberOfAssignedResources+1)/2;
//						if (randomNumberGenerator.nextDouble() < 0.5)
//							agentsBid++;
					}
					else {
						agentsBid = (agentsBid+agentsNumberOfAssignedResources)/2;
					}
					// System.out.println(" after bid =" + agentsBid);
				}
				if (aggressiveness==3) {
					// System.out.print("before bid = " + agentsBid + " agents owned = " + agentsNumberOfAssignedResources);
					if (agentsBid>agentsNumberOfAssignedResources)
						agentsBid = agentsNumberOfAssignedResources + 1;
					else
						agentsBid = agentsNumberOfAssignedResources;
					// System.out.println(" after bid =" + agentsBid);
				}
				if (aggressiveness == 4) {
					// System.out.print("before bid = " + agentsBid + " agents owned = " + agentsNumberOfAssignedResources);
					if (agentsBid > agentsNumberOfAssignedResources)
						agentsBid = agentsNumberOfAssignedResources + 1
								+ (int) (randomNumberGenerator.nextDouble() * (agentsBid - agentsNumberOfAssignedResources));
					else
						agentsBid = agentsNumberOfAssignedResources;
					// System.out.println(" after bid =" + agentsBid);
				}
				if (aggressiveness == 5) {
					// System.out.print("before bid = " + agentsBid + " agents owned = " + agentsNumberOfAssignedResources
					// + " and biddingAggressiveness = " + biddingAggressiveness);
					// if (biddingAggressiveness == 0) {
					// if (agentsBid > agentsNumberOfAssignedResources)
					// agentsBid = agentsNumberOfAssignedResources + 1;
					// else
					// agentsBid = agentsNumberOfAssignedResources;
					// }
					if (biddingAggressiveness <= 1) {
						if (agentsBid > agentsNumberOfAssignedResources)			// if sum is odd and bid is greater than number of agents
						{
							agentsBid = (agentsBid+agentsNumberOfAssignedResources+1)/2;
						}
						else {
							agentsBid = (agentsBid+agentsNumberOfAssignedResources)/2;
						}
					}
					// System.out.println(" after bid = " + agentsBid + " and biddingAggressiveness = " + biddingAggressiveness);
				}
				agentsBiddingPrices[i] = agentsBid;
				numberOfCommunications += agentsBid;
				for (int j = 0; j < agentsBid; j++) {							// place the bids on the bid matrix
					bidMatrix[i][possibleResourcesToBidOn[j][0]] = agentsBid;
				}
			}
			biddingAggressiveness++;
		}

		if (showDebug) {								// print out the bidding matrix, which shows the bid of every agent on every resource
			System.out.println("\n\t\tbidMatrix:");
			for (int i = 0; i < bidMatrix.length; i++) {
				System.out.print("\t\t");
				for (int j = 0; j < bidMatrix[0].length; j++) {
					System.out.print(bidMatrix[i][j] + ",");
				}
				System.out.println();
			}
		}
		if (showAlgorithmVisual)
			printDOT2DVisualization(11);		// show which agent is bidding on which resource

		ArrayList<int[]> overtimeBidTracker = new ArrayList<int[]>();
		boolean overtime = false;		// keep track of overtime bidding
		// 0 is by highest bid;
		int[] algorithmWhoWinsOrderAndExecutionClone = new int[algorithmWhoWinsOrderAndExecution.length]; // do NOT change
		for (int i = 0; i < algorithmWhoWinsOrderAndExecution.length; i++) {
			algorithmWhoWinsOrderAndExecutionClone[i] = algorithmWhoWinsOrderAndExecution[i];
		}
		for (int i = 0; i < maxID + 1; i++) {
			highestBidArray[i][0] = -1;
			highestBidArray[i][1] = maxID + 2;
			ArrayList<Integer> contestingBidders = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {
				if (bidMatrix[j][i] > 0) {
					contestingBidders.add(j);
				}
			}
			for (int currentAlgorithm = -1; currentAlgorithm < algorithmWhoWinsOrderAndExecution.length; currentAlgorithm++) {
				// System.out.print("current algorithm = "+currentAlgorithm);
				// if (contestingBidders.size()==0)
				// {
				// overtime = true;
				// break;
				// }
				if (currentAlgorithm > -1) // first is initial and second is overtime
				{
					contestingBidders = determineBidWinner(algorithmWhoWinsOrderAndExecutionClone[currentAlgorithm], i, contestingBidders,
							overtimeBidTracker, algorithmWhoWinsOrderAndExecution);
					if (contestingBidders.size() == 0) {
						if (showDebug)
							System.out.print("\t\tafter sort" + algorithmWhoWinsOrderAndExecutionClone[currentAlgorithm] + ": overtime\n");
						overtime = true;
						break;
					}
				}
				if (showDebug) {
					if (currentAlgorithm == -1)
						System.out.print("\t\tr" + i + " be4 srt: ");
					else
						System.out.print("\t\tafter sort" + algorithmWhoWinsOrderAndExecutionClone[currentAlgorithm] + ": ");
					// if (currentAlgorithm==-1)
					{
						for (int j = 0; j < contestingBidders.size(); j++)
							System.out.print(contestingBidders.get(j) + ",");
						System.out.print(" biddingPrices: ");
						for (int j = 0; j < contestingBidders.size(); j++)
							System.out.print(bidMatrix[contestingBidders.get(j)][i] + ",");
						System.out.print(" numConnections: ");
						for (int j = 0; j < contestingBidders.size(); j++)
							System.out
									.print(agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].numberOfResourcesConnectedToAgent
											+ ",");
						System.out.print(" numNeighborAgents: ");
						for (int j = 0; j < contestingBidders.size(); j++) {
							ArrayList<Integer> neighboringAgents = new ArrayList<Integer>();
							Resource resourceWalker = agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].firstResource;
							while (resourceWalker != null) {
								for (int k = 0; k < numrows * numcols; k++) {
									if (connectionMatrix[k][resourceWalker.ID] > 0 && !neighboringAgents.contains(k)) {
										neighboringAgents.add(k);
									}
								}
								resourceWalker = resourceWalker.nextResource;
							}
							System.out.print(neighboringAgents.size() + ",");
						}
						System.out.println();
					}
				}
				if (contestingBidders.size() == 1) {
					highestBidArray[i][0] = contestingBidders.get(0);
					highestBidArray[i][1] = agentsBiddingPrices[contestingBidders.get(0)];
					// System.out.println("\t\t\tbreaking out of r"+i+" because a"+highestBidArray[i][0]+" already won bid");
					break;				// break out of the rest of the algorithms
				}
			}
		}
		// reassign
		for (int i = 0; i < maxID + 1; i++) {
			if (assignmentVector[i] != highestBidArray[i][0] && highestBidArray[i][0] > -1) {
				if (showDebug)
					System.out.println("\tR" + i + " reassigned from A" + assignmentVector[i] + ":"
							+ agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent + "/"
							+ agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesConnectedToAgent
							+ " to A" + highestBidArray[i][0] + ":"
							+ agents[highestBidArray[i][0] / numcols][highestBidArray[i][0] % numcols].numberOfResourcesAssignedToAgent
							+ "/"
							+ agents[highestBidArray[i][0] / numcols][highestBidArray[i][0] % numcols].numberOfResourcesConnectedToAgent);
				agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent--;
				agents[highestBidArray[i][0] / numcols][highestBidArray[i][0] % numcols].numberOfResourcesAssignedToAgent++;
				sinkMatrix[assignmentVector[i]][highestBidArray[i][0]] = 1;
				sourceMatrix[assignmentVector[i]][highestBidArray[i][0]] = 1;
			}
		}

		if (overtime) {
			for (int overtimeWalker = 0; overtimeWalker < overtimeBidTracker.size(); overtimeWalker++) {
				ArrayList<Integer> contestingBidders = new ArrayList<Integer>();
				int i = overtimeBidTracker.get(overtimeWalker)[0];
				for (int j = 1; j < overtimeBidTracker.get(overtimeWalker).length; j++) {
					contestingBidders.add(overtimeBidTracker.get(overtimeWalker)[j]);
					bidMatrix[overtimeBidTracker.get(overtimeWalker)[j]][i] = agents[overtimeBidTracker.get(overtimeWalker)[j] / numcols][overtimeBidTracker
							.get(overtimeWalker)[j]
							% numcols].numberOfResourcesAssignedToAgent + 1;
				}
				for (int currentAlgorithm = -1; currentAlgorithm < algorithmWhoWinsOrderAndExecution.length; currentAlgorithm++) {
					if (currentAlgorithm > -1 && algorithmWhoWinsOrderAndExecution[currentAlgorithm] > -1) // first is initial and second is overtime
					{
						contestingBidders = determineBidWinner(algorithmWhoWinsOrderAndExecution[currentAlgorithm], i, contestingBidders,
								overtimeBidTracker, algorithmWhoWinsOrderAndExecution);
					}
					if (showDebug) {
						if (currentAlgorithm == -1)
							System.out.print("\t\tr" + i + " be4 sort: ");
						else if (algorithmWhoWinsOrderAndExecution[currentAlgorithm] != -1)
							System.out.print("\t\tafter sort" + algorithmWhoWinsOrderAndExecution[currentAlgorithm] + ": ");
						if (currentAlgorithm == -1 || algorithmWhoWinsOrderAndExecution[currentAlgorithm] != -1) {
							for (int j = 0; j < contestingBidders.size(); j++)
								System.out.print(contestingBidders.get(j) + ",");
							System.out.print(" biddingPrices: ");
							for (int j = 0; j < contestingBidders.size(); j++)
								System.out.print(bidMatrix[contestingBidders.get(j)][i] + ",");
							System.out.print(" numConnections: ");
							for (int j = 0; j < contestingBidders.size(); j++)
								System.out
										.print(agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].numberOfResourcesConnectedToAgent
												+ ",");
							System.out.print(" numNeighborAgents: ");
							for (int j = 0; j < contestingBidders.size(); j++) {
								ArrayList<Integer> neighboringAgents = new ArrayList<Integer>();
								Resource resourceWalker = agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].firstResource;
								while (resourceWalker != null) {
									for (int k = 0; k < numrows * numcols; k++) {
										if (connectionMatrix[k][resourceWalker.ID] > 0 && !neighboringAgents.contains(k)) {
											neighboringAgents.add(k);
										}
									}
									resourceWalker = resourceWalker.nextResource;
								}
								System.out.print(neighboringAgents.size() + ",");
							}
							System.out.println();
						}
					}
					if (contestingBidders.size() == 1) {
						highestBidArray[i][0] = contestingBidders.get(0);
						highestBidArray[i][1] = agentsBiddingPrices[contestingBidders.get(0)];
						if (assignmentVector[i] != highestBidArray[i][0] && highestBidArray[i][0] > -1) {
							if (showDebug)
								System.out
										.println("\tR"
												+ i
												+ " reassigned from A"
												+ assignmentVector[i]
												+ ":"
												+ agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent
												+ "/"
												+ agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesConnectedToAgent
												+ " to A"
												+ highestBidArray[i][0]
												+ ":"
												+ agents[highestBidArray[i][0] / numcols][highestBidArray[i][0] % numcols].numberOfResourcesAssignedToAgent
												+ "/"
												+ agents[highestBidArray[i][0] / numcols][highestBidArray[i][0] % numcols].numberOfResourcesConnectedToAgent);
							agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent--;
							agents[highestBidArray[i][0] / numcols][highestBidArray[i][0] % numcols].numberOfResourcesAssignedToAgent++;
							sinkMatrix[assignmentVector[i]][highestBidArray[i][0]] = 1;
							sourceMatrix[assignmentVector[i]][highestBidArray[i][0]] = 1;
						}
						break;			// break out of the algorithms
					}
				}
			}
		}
		// actually reassign
		for (int i = 0; i < maxID + 1; i++) {
			if (assignmentVector[i] != highestBidArray[i][0] && highestBidArray[i][0] > -1) {
				returnValue++;
				assignmentMatrix[assignmentVector[i]][i] = 0;
				assignmentMatrix[highestBidArray[i][0]][i] = 1;
			}
		}
		boolean anUnassignedHadOccured = false;
		// fix due to default assignment vector being all 0's
		for (int i = 0; i < maxID + 1; i++) {
			boolean unassigned = true;
			for (int j = 0; j < numrows * numcols; j++) {
				if (assignmentMatrix[j][i] > 0)
					unassigned = false;
			}
			if (unassigned && connectionMatrix[0][i] > 0) {
				assignmentMatrix[0][i] = 1;
				anUnassignedHadOccured = true;
			}
		}
		if (anUnassignedHadOccured)
			agents[0][0].numberOfResourcesAssignedToAgent += maxID + 1;
		if (showAlgorithmVisual)
			printDOT2DVisualization(12);
		printMatrices(13);
		boolean tempVocal = vocal;
		vocal = false;
		useAssignmentInput(IOTempAssignmentMatrixFileName1);
		if (showAlgorithmVisual)
			printAgentLevelGraph(4, sinkMatrix, sourceMatrix);
		useAssignmentInput(IOTempAssignmentMatrixFileName2);
		vocal = tempVocal;
		if (returnValue > 0)
			useAssignmentMatrix(); 		// use assignment matrix to later update all matrices
		else
			numberOfPhases = 1;
		numberOfDifferences = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			numberOfDifferences += Math.abs(agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
					- originalNumberOfResources[i]);
		}
		if (returnValue == 0 && isNeighboringImprovementPathsPresent())
			return -1;
		if (aggressiveness == 0 && initialFI > scaledAgentFairnessIndex()) {
			if (++numberOfBidPeakDeclinations == 5) {
				numberOfBidPeakDeclinations = 0;
				useAssignmentInput(IOTempAssignmentMatrixFileName4);
				return 0;
			}
		}
		return returnValue;
	} // end main bidding method

	// ** Determine Which Resources to Bid On ********************************************************
	public boolean sortResourcesToBid(int algorithm, int i, int[][] possibleResourcesToBidOn, int[] tieBreakerCode) {
		// algorithm, return true if it is done sorting
		// 0 is based on price;
		// 1 is based on number of connections to resource;
		// 2 is number of connections to resource's assigned agent
		// 3 is total price of nearby resources of the resource's assigned agent;
		// 4 is random
		// 10 is the ranking of winning
		// 11 is the probability of winning
		if (algorithm == 7) {
			// first, sort the array to find the bidding price
			ArrayList<Integer> listOfResourcesNotOwned = new ArrayList<Integer>();
			for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
				listOfResourcesNotOwned.add(possibleResourcesToBidOn[j][0]);
			}
			// bubble sort by price because possibleResourcesToBidOn is a non-final variable
			for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < possibleResourcesToBidOn.length; j++) {
				for (int k = j + 1; k < possibleResourcesToBidOn.length; k++) {
					if (agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]] % numcols].numberOfResourcesAssignedToAgent - agents[assignmentVector[possibleResourcesToBidOn[k][0]] / numcols][assignmentVector[possibleResourcesToBidOn[k][0]] % numcols].numberOfResourcesAssignedToAgent < 0) {
						int temp = possibleResourcesToBidOn[j][0];
						possibleResourcesToBidOn[j][0] = possibleResourcesToBidOn[k][0];
						possibleResourcesToBidOn[k][0] = temp;
					}
				}
			}
			int biddingPrice;
			for (biddingPrice = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; biddingPrice < possibleResourcesToBidOn.length
					&& biddingPrice + 1 < agents[assignmentVector[possibleResourcesToBidOn[biddingPrice][0]] / numcols][assignmentVector[possibleResourcesToBidOn[biddingPrice][0]]
							% numcols].numberOfResourcesAssignedToAgent; biddingPrice++) {
			}			
			for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
				ArrayList<Integer[]> listOfLessExpensiveResources = new ArrayList<Integer[]>();
				// find all the resources that are less expensive than current price
				for (int k = j; k < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; k++) {
					if (biddingPrice < agents[assignmentVector[possibleResourcesToBidOn[k][0]] / numcols][assignmentVector[possibleResourcesToBidOn[k][0]] % numcols].numberOfResourcesAssignedToAgent) {
						Integer[] expensiveResources = {possibleResourcesToBidOn[k][0], k};
						listOfLessExpensiveResources.add(expensiveResources);
					}
				}
				if (listOfLessExpensiveResources.size() > 0) {
					int locationOfResourceToBidOn = (int) (randomNumberGenerator.nextDouble() * listOfLessExpensiveResources.size());
					int jthResource = possibleResourcesToBidOn[j][0];
					possibleResourcesToBidOn[j][0] = listOfLessExpensiveResources.get(locationOfResourceToBidOn)[0];
					possibleResourcesToBidOn[j][1] = 1;
					possibleResourcesToBidOn[listOfLessExpensiveResources.get(locationOfResourceToBidOn)[1]][0] = jthResource;
				} else {
					possibleResourcesToBidOn[j][1] = 0;
				}
			}
			return true;
		}
		boolean returnValue = true;
		for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
			if (algorithm == 5 || algorithm == 6) // if random
			{
				int FisherYatesLength = 0;
				while (j + FisherYatesLength < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent
						&& possibleResourcesToBidOn[j + FisherYatesLength][1] <= 0
						&& (tieBreakerCode[0] == 0 || agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
								% numcols].numberOfResourcesAssignedToAgent <= agents[assignmentVector[possibleResourcesToBidOn[j
								+ FisherYatesLength][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j + FisherYatesLength][0]] % numcols].numberOfResourcesAssignedToAgent)
						&& (tieBreakerCode[1] == 0 || numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]] >= numberOfConnectedAgentsArray[possibleResourcesToBidOn[j
								+ FisherYatesLength][0]])
						&& (tieBreakerCode[2] == 0 || agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
								% numcols].numberOfResourcesConnectedToAgent <= agents[assignmentVector[possibleResourcesToBidOn[j
								+ FisherYatesLength][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j + FisherYatesLength][0]] % numcols].numberOfResourcesConnectedToAgent)
						&& (tieBreakerCode[3] == 0 || (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j][0]] % numcols].numberOfResourcesConnectedToAgent)) <= (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j
								+ FisherYatesLength][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j + FisherYatesLength][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j + FisherYatesLength][0]] % numcols].numberOfResourcesConnectedToAgent)))
						&& (tieBreakerCode[4] == 0 || totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]] <= totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j
								+ FisherYatesLength][0]]]))
					FisherYatesLength++;
				int[][] scrambledArray = new int[FisherYatesLength][3]; // 2D so that keep track of the new random ints; keep track if the number have been crossed off; make a copy
				int randomRange = FisherYatesLength;
				for (int k = 0; k < FisherYatesLength; k++) {
					int roll = randomNumberGenerator.nextInt(randomRange--);// (int)Math.floor(Math.random()*randomRange--); // take a new random int
					int arrayWalker = 0;
					while (roll > 0 || scrambledArray[arrayWalker][1] == 1) {
						if (scrambledArray[arrayWalker++][1] == 0)
							roll--;		// only count the integer walker if the checkList not already counted
					}
					scrambledArray[arrayWalker][1] = 1;
					scrambledArray[k][0] = arrayWalker;
				}
				// separate for loop to copy to temp array
				for (int k = 0; k < FisherYatesLength; k++)
					for (int l = 0; l < FisherYatesLength; l++)
						if (k == scrambledArray[l][0])
							scrambledArray[k][2] = possibleResourcesToBidOn[j + l][0];
				for (int k = 0; k < FisherYatesLength; k++)
					// since the ending condition wont end recursively
					possibleResourcesToBidOn[j + k][0] = scrambledArray[k][2];
				j += FisherYatesLength;
			}
			for (int k = j + 1; k < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; k++) {
				if (possibleResourcesToBidOn[j][1] <= 0
						&& possibleResourcesToBidOn[k][1] <= 0 // if to check if sort is possible
						&& (tieBreakerCode[0] == 0 || agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
								% numcols].numberOfResourcesAssignedToAgent <= agents[assignmentVector[possibleResourcesToBidOn[k][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[k][0]] % numcols].numberOfResourcesAssignedToAgent)
						&& (tieBreakerCode[1] == 0 || numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]] >= numberOfConnectedAgentsArray[possibleResourcesToBidOn[k][0]])
						&& (tieBreakerCode[2] == 0 || agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
								% numcols].numberOfResourcesConnectedToAgent <= agents[assignmentVector[possibleResourcesToBidOn[k][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[k][0]] % numcols].numberOfResourcesConnectedToAgent)
						&& (tieBreakerCode[3] == 0 || (double) (totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]])
								/ (agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
										% numcols].numberOfResourcesConnectedToAgent) <= (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[k][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[k][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[k][0]] % numcols].numberOfResourcesConnectedToAgent)))
						&& (tieBreakerCode[4] == 0 || totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]] <= totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[k][0]]])

				) {
					if ((algorithm == 0 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
							% numcols].numberOfResourcesAssignedToAgent < agents[assignmentVector[possibleResourcesToBidOn[k][0]] / numcols][assignmentVector[possibleResourcesToBidOn[k][0]]
							% numcols].numberOfResourcesAssignedToAgent)
							|| (algorithm == 1 && numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]] > numberOfConnectedAgentsArray[possibleResourcesToBidOn[k][0]])
							|| (algorithm == 2 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
									% numcols].numberOfResourcesConnectedToAgent < agents[assignmentVector[possibleResourcesToBidOn[k][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[k][0]] % numcols].numberOfResourcesConnectedToAgent)
							|| (algorithm == 3 && (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j][0]] % numcols].numberOfResourcesConnectedToAgent)) < (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[k][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[k][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[k][0]] % numcols].numberOfResourcesConnectedToAgent)))
							|| (algorithm == 4 && totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]] < totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[k][0]]])) 				// if
					// to check if a change should actually occur
					{					// bubble sort switch
						int temp = possibleResourcesToBidOn[j][0];
						possibleResourcesToBidOn[j][0] = possibleResourcesToBidOn[k][0];
						possibleResourcesToBidOn[k][0] = temp;
					}
				}
			}
		}
		tieBreakerCode[algorithm] = 1;
		for (int j = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent; j < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent; j++) {
			// if not future tie-breakers
			if (algorithm == 10 || algorithm == 5)
				possibleResourcesToBidOn[j][1] = 1;
			// if not first element (if not last element) (if last element); if first element (if more than 1 elment) (if only 1 element)
			if (j > agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent) {
				if (j + 1 < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent) {
					if ((possibleResourcesToBidOn[j - 1][1] == 1
							|| (tieBreakerCode[0] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
									% numcols].numberOfResourcesAssignedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j - 1][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j - 1][0]] % numcols].numberOfResourcesAssignedToAgent)
							|| (tieBreakerCode[1] == 1 && numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]] != numberOfConnectedAgentsArray[possibleResourcesToBidOn[j - 1][0]])
							|| (tieBreakerCode[2] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
									% numcols].numberOfResourcesConnectedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j - 1][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j - 1][0]] % numcols].numberOfResourcesConnectedToAgent)
							|| (tieBreakerCode[3] == 1 && (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j][0]] % numcols].numberOfResourcesConnectedToAgent)) != (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j - 1][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j - 1][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j - 1][0]] % numcols].numberOfResourcesConnectedToAgent))) || (tieBreakerCode[4] == 1 && totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]] != totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j - 1][0]]]))
							&& (possibleResourcesToBidOn[j + 1][1] == 1
									|| (tieBreakerCode[0] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
											% numcols].numberOfResourcesAssignedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j + 1][0]]
											/ numcols][assignmentVector[possibleResourcesToBidOn[j + 1][0]] % numcols].numberOfResourcesAssignedToAgent)
									|| (tieBreakerCode[1] == 1 && numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]] != numberOfConnectedAgentsArray[possibleResourcesToBidOn[j + 1][0]])
									|| (tieBreakerCode[2] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
											% numcols].numberOfResourcesConnectedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j + 1][0]]
											/ numcols][assignmentVector[possibleResourcesToBidOn[j + 1][0]] % numcols].numberOfResourcesConnectedToAgent)
									|| (tieBreakerCode[3] == 1 && (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j][0]]
											/ numcols][assignmentVector[possibleResourcesToBidOn[j][0]] % numcols].numberOfResourcesConnectedToAgent)) != (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j + 1][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j + 1][0]]
											/ numcols][assignmentVector[possibleResourcesToBidOn[j + 1][0]] % numcols].numberOfResourcesConnectedToAgent))) || (tieBreakerCode[4] == 1 && totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]] != totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j + 1][0]]])))
						possibleResourcesToBidOn[j][1] = 1;
				} else if (possibleResourcesToBidOn[j - 1][1] == 1
						|| (tieBreakerCode[0] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
								% numcols].numberOfResourcesAssignedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j - 1][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j - 1][0]] % numcols].numberOfResourcesAssignedToAgent)
						|| (tieBreakerCode[1] == 1 && numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]] != numberOfConnectedAgentsArray[possibleResourcesToBidOn[j - 1][0]])
						|| (tieBreakerCode[2] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
								% numcols].numberOfResourcesConnectedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j - 1][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j - 1][0]] % numcols].numberOfResourcesConnectedToAgent)
						|| (tieBreakerCode[3] == 1 && (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j][0]] % numcols].numberOfResourcesConnectedToAgent)) != (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j - 1][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j - 1][0]]
								/ numcols][assignmentVector[possibleResourcesToBidOn[j - 1][0]] % numcols].numberOfResourcesConnectedToAgent)))
						|| (tieBreakerCode[4] == 1 && totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]] != totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j - 1][0]]]))
					possibleResourcesToBidOn[j][1] = 1;
			} else {
				if (j + 1 < agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent) {
					if (possibleResourcesToBidOn[j + 1][1] == 1
							|| (tieBreakerCode[0] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
									% numcols].numberOfResourcesAssignedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j + 1][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j + 1][0]] % numcols].numberOfResourcesAssignedToAgent)
							|| (tieBreakerCode[1] == 1 && numberOfConnectedAgentsArray[possibleResourcesToBidOn[j][0]] != numberOfConnectedAgentsArray[possibleResourcesToBidOn[j + 1][0]])
							|| (tieBreakerCode[2] == 1 && agents[assignmentVector[possibleResourcesToBidOn[j][0]] / numcols][assignmentVector[possibleResourcesToBidOn[j][0]]
									% numcols].numberOfResourcesConnectedToAgent != agents[assignmentVector[possibleResourcesToBidOn[j + 1][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j + 1][0]] % numcols].numberOfResourcesConnectedToAgent)
							|| (tieBreakerCode[3] == 1 && (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j][0]] % numcols].numberOfResourcesConnectedToAgent)) != (double) ((totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j + 1][0]]]) / (agents[assignmentVector[possibleResourcesToBidOn[j + 1][0]]
									/ numcols][assignmentVector[possibleResourcesToBidOn[j + 1][0]] % numcols].numberOfResourcesConnectedToAgent)))
							|| (tieBreakerCode[4] == 1 && totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j][0]]] != totalPriceOfAllResources[assignmentVector[possibleResourcesToBidOn[j + 1][0]]]))
						possibleResourcesToBidOn[j][1] = 1;
				} else
					possibleResourcesToBidOn[j][1] = 1;
			}
			if (possibleResourcesToBidOn[j][1] == 0)
				returnValue = false;
		}
		return returnValue;
	}	// end determining which resource to bid on

	// ** Determine Bid Winner **********************************************************************
	public ArrayList<Integer> determineBidWinner(int algorithm, int i, ArrayList<Integer> contestingBidders,
			ArrayList<int[]> overtimeBidTracker, int[] algorithmOrderAndExecution) {
		ArrayList<Integer> newContestingBidders = new ArrayList<Integer>();
		if (algorithm == 0 || algorithm == 10) {
			int highestBid = maxID + 2;
			for (int j = 0; j < contestingBidders.size(); j++) {
				if (bidMatrix[contestingBidders.get(j)][i] == highestBid) {
					newContestingBidders.add(contestingBidders.get(j));
				}
				if (bidMatrix[contestingBidders.get(j)][i] > 0 && bidMatrix[contestingBidders.get(j)][i] < highestBid) // need to check 0 since it was defaulted
				{
					newContestingBidders = new ArrayList<Integer>();
					newContestingBidders.add(contestingBidders.get(j));
					highestBid = bidMatrix[contestingBidders.get(j)][i];
				}
			}
		} else if (algorithm == 1 || algorithm == 11) {
			int lowestNumberOfAgentConnections = maxID + 2;
			for (int j = 0; j < contestingBidders.size(); j++) {
				if (agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].numberOfResourcesConnectedToAgent == lowestNumberOfAgentConnections) {
					newContestingBidders.add(contestingBidders.get(j));
				}
				if (agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].numberOfResourcesConnectedToAgent < lowestNumberOfAgentConnections) {
					newContestingBidders = new ArrayList<Integer>();
					newContestingBidders.add(contestingBidders.get(j));
					lowestNumberOfAgentConnections = agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].numberOfResourcesConnectedToAgent;
				}
			}
		} else if (algorithm == 2 || algorithm == 12) {
			int highestNumberOfNeighboringAgents = -1;
			for (int j = 0; j < contestingBidders.size(); j++) {
				ArrayList<Integer> neighboringAgents = new ArrayList<Integer>();
				Resource resourceWalker = agents[contestingBidders.get(j) / numcols][contestingBidders.get(j) % numcols].firstResource;
				while (resourceWalker != null) {
					for (int k = 0; k < numrows * numcols; k++) {
						if (connectionMatrix[k][resourceWalker.ID] > 0 && !neighboringAgents.contains(k)) {
							neighboringAgents.add(k);
						}
					}
					resourceWalker = resourceWalker.nextResource;
				}
				if (neighboringAgents.size() == highestNumberOfNeighboringAgents) {
					newContestingBidders.add(contestingBidders.get(j));
				}
				if (neighboringAgents.size() > highestNumberOfNeighboringAgents) {
					newContestingBidders = new ArrayList<Integer>();
					newContestingBidders.add(contestingBidders.get(j));
					highestNumberOfNeighboringAgents = neighboringAgents.size();
				}
			}
		} else if (algorithm == 3) {
			int[] overtimeBidTrackerElement = new int[contestingBidders.size() + 1];
			overtimeBidTrackerElement[0] = i;
			for (int j = 1; j < contestingBidders.size() + 1; j++) {
				overtimeBidTrackerElement[j] = contestingBidders.get(j - 1);
			}
			overtimeBidTracker.add(overtimeBidTrackerElement);
			for (int j = 0; j < algorithmOrderAndExecution.length; j++) {
				if (algorithmOrderAndExecution[j] == 3) {
					algorithmOrderAndExecution[j] = -1;
				} else if (algorithmOrderAndExecution[j] >= 0 && algorithmOrderAndExecution[j] < 10)
					algorithmOrderAndExecution[j] += 10;
			}
		} else if (algorithm == 4) {
			if (contestingBidders.size() == 0)
				System.out.println("error: algorithm = " + algorithm + " i = " + i);
			newContestingBidders.add(contestingBidders.get(randomNumberGenerator.nextInt(contestingBidders.size())));
			// the following line is code is without a seed for the random number generator
			// newContestingBidders.add(contestingBidders.get((int)Math.floor(Math.random()*contestingBidders.size())));
		} else { // (algorithm == 5) {
			int lowestBid = 0; // need to find the greatest to find the agent with the lowest bid
			for (int j = 0; j < contestingBidders.size(); j++) {
				if (bidMatrix[contestingBidders.get(j)][i] > lowestBid) // need to check 0 since it was defaulted
				{
					lowestBid = bidMatrix[contestingBidders.get(j)][i];
				}
			}
			ArrayList<Integer> newContestingBidders2 = new ArrayList<Integer>();
			for (int j = 0; j < contestingBidders.size(); j++) {
				if (bidMatrix[contestingBidders.get(j)][i] != lowestBid) {
					newContestingBidders2.add(contestingBidders.get(j));
				}
			}
			if (newContestingBidders2.size() == 0) {
				if (newContestingBidders.size() > 0) {
					newContestingBidders.add(contestingBidders.get(randomNumberGenerator.nextInt(contestingBidders.size())));
				}
			} else {
				newContestingBidders.add(newContestingBidders2.get(randomNumberGenerator.nextInt(newContestingBidders2.size())));
			}
		}
		return newContestingBidders;
	}	// end determining bid winner

	// ** (Cascaded) Offer and Accept *****************************************************************
	public int offerAccept(boolean improvement, int version)// boolean improvement; version 0 = wholesale, 1 = retail, 2 = any lower wholesale, 3 = cascaded wholesale
	{									// if not improvement, then apply for resources from the agent with the lowest number of resources
		if (version >= 3)
			cascadedDecentralizedIsDone++;
		numberOfPhases = 3;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		int returnValue = 0;			// then give to the richest applicant
		int numberOfImprovements = 0;
		int[][] offerMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of offers
		// if declination, then keep track of applications
		int[][] acceptMatrix = new int[numrows * numcols][numrows * numcols]; // keep track of offers
		int[] acceptArray = new int[numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			int lowestNumberOfResources = N;
			int agentWithLowestNumberOfResources = -1;
			int[] randomAgentsArray = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray[r];
					randomAgentsArray[r] = randomAgentsArray[j];
					randomAgentsArray[j] = swap;
				}
			}
			ArrayList <Integer> possibleOfferRecipiants = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {							// i will offer to j
				if ((improvement && reachMatrix[i][randomAgentsArray[j]] > 0 && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent >= (version >= 3 ? 1 : 2))
						|| (!improvement && reachMatrix[randomAgentsArray[j]][i] > 0 && agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent - agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent <= 0)) {	
					if (version != 2 && agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent < lowestNumberOfResources) {
						lowestNumberOfResources = agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent;
						agentWithLowestNumberOfResources = randomAgentsArray[j];
					}
					if (version == 2) { // find poorest agent to offer to
						possibleOfferRecipiants.add(randomAgentsArray[j]);
					}
				} // switch j and i since j (poor) is now offering to i // find poorest agent to apply to
			}
			if (possibleOfferRecipiants.size() > 0) {
				agentWithLowestNumberOfResources = possibleOfferRecipiants.get(randomNumberGenerator.nextInt(possibleOfferRecipiants.size()));
			}
			if (agentWithLowestNumberOfResources >= 0) {// update offer matrix
				offerMatrix[i][agentWithLowestNumberOfResources] = 1;
				numberOfCommunications++;
			}
		}
		for (int i = 0; i < numrows * numcols; i++)
			acceptArray[i] = -1;
		for (int i = 0; i < numrows * numcols; i++) {
			int highestNumberOfResources = 0;
			if (!improvement)
				highestNumberOfResources = N;
			int agentWithHighestNumberOfResources = -1;
			int[] randomAgentsArray = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray[r];
					randomAgentsArray[r] = randomAgentsArray[j];
					randomAgentsArray[j] = swap;
				}
			}
			for (int j = 0; j < numrows * numcols; j++) {							// j offered to i in the offer matrix, so now use i to find j
				if ((improvement && offerMatrix[randomAgentsArray[j]][i] > 0 && agents[randomAgentsArray[j] / numcols][randomAgentsArray[j]
						% numcols].numberOfResourcesAssignedToAgent > highestNumberOfResources)
						|| (!improvement && offerMatrix[i][randomAgentsArray[j]] > 0 && agents[randomAgentsArray[j] / numcols][randomAgentsArray[j]
								% numcols].numberOfResourcesAssignedToAgent < highestNumberOfResources)) {						// find the offer from the richest person
					highestNumberOfResources = agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent;
					agentWithHighestNumberOfResources = randomAgentsArray[j]; // j should send to i
				}
				// or i applied for resources from j, so j should send to i; i should now find the poorest j that offered to transfer with
			}
			acceptArray[i] = agentWithHighestNumberOfResources;
			if (agentWithHighestNumberOfResources >= 0) {
				acceptMatrix[agentWithHighestNumberOfResources][i] = 1;
			}
		}
		if (showAlgorithmVisual)
			printAgentLevelGraph(5, offerMatrix, acceptMatrix);
		for (int i = 0; i < numrows * numcols; i++) {
			int agentWithHighestNumberOfResources = acceptArray[i];
			if (agentWithHighestNumberOfResources >= 0) {
				Resource resourceWalker = agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources % numcols].firstResource;
				ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
				while (resourceWalker != null)   // from i to j
				{					// keep walking until valid resource
					int k = resourceWalker.ID;
					boolean checkDifference1 = agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources
							% numcols].numberOfResourcesAssignedToAgent
							- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= (version >= 3 ? 1 : 2);
					if (!improvement)
						checkDifference1 = agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources % numcols].numberOfResourcesAssignedToAgent
								- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent <= 0;
					if (assignmentMatrix[agentWithHighestNumberOfResources][k] > 0 && connectionMatrix[i][k] > 0 && checkDifference1) {				// if a resource can be passed
						// from agents[agentWithHighestNumberOfResources/numcols][agentWithHighestNumberOfResources%numcols] to agents[i/numcols][i%numcols]
						// through resource[k]
						possibleResources.add(k);
					}
					resourceWalker = resourceWalker.nextResource;
				}
				while (true) {
					if (possibleResources.size() <= 0)
						break;
					int possibleResourcesIndex = (int) (possibleResources.size() * randomNumberGenerator.nextDouble());
					int k = possibleResources.get(possibleResourcesIndex);
					possibleResources.remove(possibleResourcesIndex);
					boolean checkDifference1 = agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources
							% numcols].numberOfResourcesAssignedToAgent
							- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= (version >= 3 ? 1 : 2);
					if (!improvement)
						checkDifference1 = agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources % numcols].numberOfResourcesAssignedToAgent
								- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent <= 0;
					if (!checkDifference1)
						break;
					boolean checkDifference2 = agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources
							% numcols].numberOfResourcesAssignedToAgent
							- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= 2;
					if (!improvement)
						checkDifference2 = agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources % numcols].numberOfResourcesAssignedToAgent
								- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent <= 0;
					if (checkDifference2)
						numberOfImprovements++;
					if (showDebug)
						System.out
								.println("\tR"
										+ k
										+ " reassigned from A"
										+ agentWithHighestNumberOfResources
										+ ":"
										+ agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources % numcols].numberOfResourcesAssignedToAgent
										+ "/"
										+ agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources % numcols].numberOfResourcesConnectedToAgent
										+ " to A" + i + ":" + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/"
										+ agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent);
					assignmentMatrix[agentWithHighestNumberOfResources][k] = 0;
					assignmentMatrix[i][k] = 1;
					agents[agentWithHighestNumberOfResources / numcols][agentWithHighestNumberOfResources % numcols].numberOfResourcesAssignedToAgent--;
					agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent++;
					returnValue++;
					if (version == 1)
						break;
					// transfer more than 1 resource at a time if case a difference of 2 remains
				}
			}
		}
		if (returnValue > 0)
			useAssignmentMatrix(); 		// use assignment matrix to later update all matrices
		else
			numberOfPhases = 1;			// no phase required for no execution
		// if (version>=3&&(numberOfImprovements>0||(numberOfImprovements==0&&cascadedImprovementPathExists(reachMatrix, 0, true))))
		if (version >= 3 && numberOfImprovements > 0) {
			cascadedDecentralizedIsDone = 0;
		}
		if (version >= 3 && cascadedDecentralizedIsDone >= 2 && numberOfImprovements <= 0)
			return 0;
		return returnValue;
	} // end (cascaded) offer and accept

	// ** Min and Max *******************************************************************************
	public int minMax(int version) {			// version 0 = wholesale, 1 = retail, 2 = higherLower wholesale
		numberOfPhases = 3;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		boolean stillNeighboringImprovementPaths = false;
		int returnValue = 0;
		int[][] minMatrix = new int[numrows * numcols][numrows * numcols];
		int[][] maxMatrix = new int[numrows * numcols][numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			int lowestNumberOfResources = N+1;
			ArrayList<Integer> agentsWithLowestNumberOfResources = new ArrayList<Integer>();
			int highestNumberOfResources = 0;
			ArrayList<Integer> agentsWithHighestNumberOfResources = new ArrayList<Integer>();
			int[] randomAgentsArray = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray[r];
					randomAgentsArray[r] = randomAgentsArray[j];
					randomAgentsArray[j] = swap;
				}
			}
			for (int j = 0; j < numrows * numcols; j++) {
				// i will see j as its min so i offer to j
				if (reachMatrix[i][randomAgentsArray[j]] > 0
						&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
						- agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent >= 2) {
					if (version >= 2) {
						agentsWithLowestNumberOfResources.add(randomAgentsArray[j]);
					} else if (agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent < lowestNumberOfResources) {
						lowestNumberOfResources = agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent;
						agentsWithLowestNumberOfResources = new ArrayList<Integer>();
						agentsWithLowestNumberOfResources.add(randomAgentsArray[j]);
					}
				}						// i will see j as its max, so want a path from j to i
				if (reachMatrix[randomAgentsArray[j]][i] > 0
						&& agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent
						- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= 2) {
					if (version >= 2) {
						agentsWithHighestNumberOfResources.add(randomAgentsArray[j]);
					} else if (agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent > highestNumberOfResources) {
						highestNumberOfResources = agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent;
						agentsWithHighestNumberOfResources = new ArrayList<Integer>();
						agentsWithHighestNumberOfResources.add(randomAgentsArray[j]);
					}
				}
			}
			if (agentsWithLowestNumberOfResources.size() > 0) {	// update min matrix
				minMatrix[i][agentsWithLowestNumberOfResources.get(0)] = 1;
				stillNeighboringImprovementPaths = true;
				numberOfCommunications++;
			}
			if (agentsWithHighestNumberOfResources.size() > 0) {// update max matrix
				maxMatrix[agentsWithHighestNumberOfResources.get(0)][i] = 1;
				numberOfCommunications++;
			}
		}
		if (showAlgorithmVisual)
			printAgentLevelGraph(6, minMatrix, maxMatrix);
		for (int i = 0; i < numcols * numrows; i++) {
			for (int j = 0; j < numcols * numrows; j++) {
				if (minMatrix[i][j] == 1 && maxMatrix[i][j] == 1) {						// if they see each other as min and max, then transfer from i to j
					// System.out.println("\tA"+j+":"+agents[j/numcols][j%numcols].numberOfResourcesAssignedToAgent+"/"+agents[j/numcols][j%numcols].numberOfResourcesConnectedToAgent+" and A"+i+":"+agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent+"/"+agents[i/numcols][i%numcols].numberOfResourcesConnectedToAgent+" sees each other");
					Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)   // from i to j
					{					// keep walking until valid resource
						int k = resourceWalker.ID;
						if (assignmentMatrix[i][k] > 0
								&& connectionMatrix[j][k] > 0
								&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
										- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2) {
							// if a resource can be passed from agents[i/numcols][i%numcols] to agents[j/numcols][j%numcols] through resource[k]
							possibleResources.add(k);
						}
						resourceWalker = resourceWalker.nextResource;
					}
					while (true) {
						if (possibleResources.size() <= 0)
							break;
						int possibleResourcesIndex = (int) (possibleResources.size() * randomNumberGenerator.nextDouble());
						int k = possibleResources.get(possibleResourcesIndex);
						possibleResources.remove(possibleResourcesIndex);
						if (!(assignmentMatrix[i][k] > 0
								&& connectionMatrix[j][k] > 0
								&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
										- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2))
							break;
						if (showDebug)
							System.out.println("\tR" + k + " reassigned from A" + i + ":"
									+ agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/"
									+ agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " to A" + j + ":"
									+ agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent + "/"
									+ agents[j / numcols][j % numcols].numberOfResourcesConnectedToAgent);
						assignmentMatrix[i][k] = 0;
						assignmentMatrix[j][k] = 1;
						agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent--;
						agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent++;
						returnValue++;
						// transfer more than 1 resource at a time if case a difference of 2 remains
						if (version > 0)
							break;
					}
				}
			}
		}
		if (returnValue > 0)
			useAssignmentMatrix(); 		// use assignment matrix to later update all matrices
		if (returnValue == 0 && stillNeighboringImprovementPaths)
			return -1;
		if (returnValue == 0 && !stillNeighboringImprovementPaths) {
			numberOfPhases = 1;
		}
		return returnValue;
	} // end offer and accept

	// ** Stable Matching **************************************************************************
	public int stableMatching(boolean improvement, int numberOfRounds)// boolean improvement)
	{									// if not improvement, then apply for resources from the agent with the lowest number of resources
		int returnValue = 0;
		numberOfCommunications = (numberOfOnes(adjacencyMatrix) - (numrows * numcols)) / 2;
		int[][] acceptorTable = new int[numrows * numcols][numrows * numcols];		// this table shows the agent that each agent prefers to accept from
		int[] oldAcceptorTableLength = new int[numrows * numcols]; 				// used to determine how many to consider proposals from
		int[] acceptorTableLength = new int[numrows * numcols]; 					// used to determine how many to consider proposals from
		boolean[] currentlyHasAcceptorPartner = new boolean[numrows * numcols];	// used to determine which proposer has an offer placed on hold
		boolean[] currentlyHasOfferPartner = new boolean[numrows * numcols];		// used to determine which acceptor has an offer placed on hold
		int[][] proposerTable = new int[numrows * numcols][numrows * numcols];		// table shows the agent that each agent prefers to propose to
		int[][] minMatrix = new int[numrows * numcols][numrows * numcols];
		int[][] maxMatrix = new int[numrows * numcols][numrows * numcols];
		int[][] proposesReceived = new int[numrows * numcols][numrows * numcols];	// a subset of the acceptor table that only include proposes
		int[] proposesReceivedCounter = new int[numrows * numcols];				// let the acceptors know who they currently hold
		int[] proposesHoldOn = new int[numrows * numcols];					// let the acceptors know who they currently hold
		int[] proposesMadeCounter = new int[numrows * numcols];					// let the proposers know where they are at in terms of proposing
		// initiate the acceptor and proposer table and currently has a partner
		for (int i = 0; i < numrows * numcols; i++) {
			currentlyHasAcceptorPartner[i] = false;
			currentlyHasOfferPartner[i] = false;
			for (int j = 0; j < numrows * numcols; j++) {
				acceptorTable[i][j] = -1;
				proposerTable[i][j] = -1;
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int[] randomAgentsArray = new int[numrows * numcols]; // for mm, the min applicant and the max should be random
			for (int j = 0; j < numrows * numcols; j++) {
				randomAgentsArray[j] = j;
			}
			if (shuffle) {
				for (int j = 0; j < numrows * numcols; j++) {
					int r = j + (int) (randomNumberGenerator.nextDouble() * (numrows * numcols - j));
					int swap = randomAgentsArray[r];
					randomAgentsArray[r] = randomAgentsArray[j];
					randomAgentsArray[j] = swap;
				}
			}
			// find the list if agents to propose/accept
			ArrayList<Integer> acceptorPool = new ArrayList<Integer>();
			ArrayList<Integer> proposerPool = new ArrayList<Integer>();
			for (int j = 0; j < numrows * numcols; j++) {
				// i will see j as its min so i offer to j
				if (reachMatrix[i][randomAgentsArray[j]] > 0
						&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
								- agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent >= 2) {
					acceptorPool.add(randomAgentsArray[j]);
				}						// i will see j as its max, so want a path from j to i
				if (reachMatrix[randomAgentsArray[j]][i] > 0
						&& agents[randomAgentsArray[j] / numcols][randomAgentsArray[j] % numcols].numberOfResourcesAssignedToAgent
								- agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent >= 2) {
					proposerPool.add(randomAgentsArray[j]);
				}
			}
			// sort the acceptor pool to acccept from the richest and then place it into the table
			for (int j = 0; j < acceptorPool.size(); j++) {
				for (int k = j + 1; k < acceptorPool.size(); k++) {
					if (agents[acceptorPool.get(j) / numcols][acceptorPool.get(j) % numcols].numberOfResourcesAssignedToAgent > agents[acceptorPool
							.get(k)
							/ numcols][acceptorPool.get(k) % numcols].numberOfResourcesAssignedToAgent) {
						int tempInt = acceptorPool.get(j);
						acceptorPool.set(j, acceptorPool.get(k));
						acceptorPool.set(k, tempInt);
					}
				}
			}
			for (int j = 0; j < acceptorPool.size(); j++) {
				acceptorTable[i][j] = acceptorPool.get(j);
			}
			// sort the proposer pool to propose from the poorest to the richest and then place it into the table
			for (int j = 0; j < proposerPool.size(); j++) {
				for (int k = j + 1; k < proposerPool.size(); k++) {
					if (agents[proposerPool.get(j) / numcols][proposerPool.get(j) % numcols].numberOfResourcesAssignedToAgent < agents[proposerPool
							.get(k)
							/ numcols][proposerPool.get(k) % numcols].numberOfResourcesAssignedToAgent) {
						int tempInt = proposerPool.get(j);
						proposerPool.set(j, proposerPool.get(k));
						proposerPool.set(k, tempInt);
					}
				}
			}
			for (int j = 0; j < proposerPool.size(); j++) {
				proposerTable[i][j] = proposerPool.get(j);
			}
		}
		if (showDebug) {
			System.out.println("Acceptor Table:");
			for (int i = 0; i < proposerTable.length; i++) {
				System.out.printf("A%3s: ", i);
				for (int j = 0; j < proposerTable[0].length; j++) {
					if (proposerTable[i][j] >= 0)
						System.out.printf("%3s,", proposerTable[i][j]);
					else
						System.out.print("  X,");
				}
				System.out.println();
			}
			System.out.println("\n\n\nOfferer Table:");
			for (int i = 0; i < acceptorTable.length; i++) {
				System.out.printf("A%3s: ", i);
				for (int j = 0; j < acceptorTable[0].length; j++) {
					if (acceptorTable[i][j] >= 0)
						System.out.printf("%3s,", acceptorTable[i][j]);
					else
						System.out.print("  X,");
				}
				System.out.println();
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			proposesReceivedCounter[i] = 0;
			proposesHoldOn[i] = -1;
			proposesMadeCounter[i] = 0;
			oldAcceptorTableLength[i] = 0;
			acceptorTableLength[i] = 0;
			for (int j = 0; j < numrows * numcols; j++) {
				proposesReceived[i][j] = -1;
			}
		}
		numberOfPhases = 1;
		while (true) {
			if (numberOfRounds > 0 && numberOfPhases >= 2 * numberOfRounds + 1)
				break;
			boolean isDone = true;
			for (int i = 0; i < acceptorTable.length; i++) {
				if (!currentlyHasAcceptorPartner[i] && acceptorTable[i][proposesMadeCounter[i]] >= 0) {
					if (showDebug)
						System.out.printf("\tagent %3s is offering to agent %3s\n", i, acceptorTable[i][proposesMadeCounter[i]]);
					acceptorTableLength[acceptorTable[i][proposesMadeCounter[i]]]++;			// increment the acceptor's list of agent to consider
					// System.out.println("proposerTable[i]: "+proposerTable[i]);
					// System.out.println("proposesMadeCounter[i]
					proposesReceived[acceptorTable[i][proposesMadeCounter[i]]][proposesReceivedCounter[acceptorTable[i][proposesMadeCounter[i]]]] = i;
					proposesReceivedCounter[acceptorTable[i][proposesMadeCounter[i]]]++;
					proposesMadeCounter[i]++;
					isDone = false;
				}
			}
			if (isDone)
				break;
			numberOfPhases+=2;
			for (int i = 0; i < numrows * numcols; i++) {
				if (acceptorTableLength[i] == 0)
					continue;
				ArrayList<Integer> proposesToConsider = new ArrayList<Integer>();
				for (int j = oldAcceptorTableLength[i]; j < acceptorTableLength[i]; j++) {
					proposesToConsider.add(proposesReceived[i][j]);
				}
				if (currentlyHasOfferPartner[i])
					proposesToConsider.add(proposesHoldOn[i]);
				for (int j = 0; j < proposerTable[i].length && proposerTable[i][j] >= 0; j++) {
					if (proposesToConsider.contains(proposerTable[i][j])) {
						if (proposesHoldOn[i] != proposerTable[i][j] && proposesHoldOn[i] >= 0)			// if better offer came along
						{
							if (agents[proposesHoldOn[i] / numcols][proposesHoldOn[i] % numcols].numberOfResourcesAssignedToAgent == agents[proposerTable[i][j]
									/ numcols][proposerTable[i][j] % numcols].numberOfResourcesAssignedToAgent)
								break;
							if (showDebug)
								System.out.printf("\tagent %3s released agent %3s's offer for agent %3s's offer\n", i, proposesHoldOn[i],
										proposerTable[i][j]);
							currentlyHasAcceptorPartner[proposesHoldOn[i]] = false;	// let go of the previous hold
						}
						if (showDebug && proposesHoldOn[i] < 0)
							System.out.printf("\tagent %3s is holding onto agent %3s's offer\n", i, proposerTable[i][j]);
						proposesHoldOn[i] = proposerTable[i][j]; // currently hold onto jth best possible offer
						currentlyHasOfferPartner[i] = true;
						currentlyHasAcceptorPartner[proposerTable[i][j]] = true;
						break;
					}
				}
			}
			for (int i = 0; i < numrows * numcols; i++) {
				oldAcceptorTableLength[i] = acceptorTableLength[i];
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {
			numberOfCommunications += proposesMadeCounter[i];
			if (proposesHoldOn[i] >= 0) {
				minMatrix[proposesHoldOn[i]][i] = 1;
				maxMatrix[proposesHoldOn[i]][i] = 1;
			}
		}
		if (showAlgorithmVisual)
			printAgentLevelGraph(7, minMatrix, maxMatrix);
		for (int i = 0; i < numcols * numrows; i++) {
			for (int j = 0; j < numcols * numrows; j++) {
				if (minMatrix[i][j] == 1 && maxMatrix[i][j] == 1) {						// if they see each other as min and max, then transfer from i to j
					// System.out.println("\tA"+j+":"+agents[j/numcols][j%numcols].numberOfResourcesAssignedToAgent+"/"+agents[j/numcols][j%numcols].numberOfResourcesConnectedToAgent+" and A"+i+":"+agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent+"/"+agents[i/numcols][i%numcols].numberOfResourcesConnectedToAgent+" sees each other");
					Resource resourceWalker = agents[i / numcols][i % numcols].firstResource;
					ArrayList<Integer> possibleResources = new ArrayList<Integer>(); // used to not move resource twice
					while (resourceWalker != null)   // from i to j
					{					// keep walking until valid resource
						int k = resourceWalker.ID;
						boolean checkDifference1 = assignmentMatrix[i][k] > 0
								&& connectionMatrix[j][k] > 0
								&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
										- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2;
						if (!improvement)
							checkDifference1 = assignmentMatrix[i][k] > 0
									&& connectionMatrix[j][k] > 0
									&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
											- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent <= 0;
						if (checkDifference1) {				// if a resource can be passed
							// from agents[i/numcols][i%numcols] to agents[j/numcols][j%numcols]
							// through resource[k]
							possibleResources.add(k);
						}
						resourceWalker = resourceWalker.nextResource;
					}
					while (true) {
						if (possibleResources.size() <= 0)
							break;
						int possibleResourcesIndex = (int) (possibleResources.size() * randomNumberGenerator.nextDouble());
						int k = possibleResources.get(possibleResourcesIndex);
						possibleResources.remove(possibleResourcesIndex);
						boolean checkDifference1 = assignmentMatrix[i][k] > 0
								&& connectionMatrix[j][k] > 0
								&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
										- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent >= 2;
						if (!improvement)
							checkDifference1 = assignmentMatrix[i][k] > 0
									&& connectionMatrix[j][k] > 0
									&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
											- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent <= 0;
						if (!checkDifference1)
							break;
						if (showDebug)
							System.out.println("\tR" + k + " reassigned from A" + i + ":"
									+ agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + "/"
									+ agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + " to A" + j + ":"
									+ agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent + "/"
									+ agents[j / numcols][j % numcols].numberOfResourcesConnectedToAgent);
						assignmentMatrix[i][k] = 0;
						assignmentMatrix[j][k] = 1;
						agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent--;
						agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent++;
						returnValue++;
						// transfer more than 1 resource at a time if case a difference of 2 remains
					}
				}
			}
		}
		if (returnValue > 0)
			useAssignmentMatrix(); 		// use assignment matrix to later update all matrices
		return returnValue;
	} // end stable matching

	// ** Update Matrices From Data *****************************************************************
	public void updateMatricesFromData() {	// recreate all data structures
		connectionMatrix = new int[numrows * numcols][maxID + 1]; // resize and initialize matrices to zero
		assignmentMatrix = new int[numrows * numcols][maxID + 1];
		assignmentVector = new int[maxID + 1];
		numberOfConnectedAgentsArray = new int[maxID + 1];
		// resourcePrice = new int[maxID+1]; // keep track of prices
		// assignmentMatrixV2 = new double[numrows*numcols][maxID+1];
		totalPriceOfAllResources = new int[numrows * numcols];
		differenceMatrix = new int[numrows * numcols][maxID + 1];
		reachMatrix = new int[numrows * numcols][numrows * numcols];
		pathMatrix = new int[numrows * numcols][numrows * numcols];
		agentPathList = new int[numrows * numcols][numrows * numcols]; // resize for improvement path calculations
		// resourceMarker = new ArrayList<Integer>();
		reachMatrixList = new ArrayList<int[][]>();
		agentPathMatrixList = new ArrayList<int[][]>();
		// for (int i = 0; i<maxID+1; i++)
		// {
		// assignmentVector[i] = -1;
		// }
		for (int i = 0; i < numrows * numcols; i++) // for every agent
		{
			for (int j = 0; j < maxID + 1; j++) // for every resource
			{
				if (resources[j].assignedRow == i / numcols && resources[j].assignedColumn == i % numcols) {
					assignmentMatrix[i][j] = 1; // update assignment matrix
					assignmentVector[j] = i;
				}
				if (checkContains(j, i / numcols, i % numcols)) {
					connectionMatrix[i][j] = 1; // update connection matrix
				}
				differenceMatrix[i][j] = connectionMatrix[i][j] - assignmentMatrix[i][j]; // update XOR matrix
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {									// use a difference set of for loop parameters for reach and path
			for (int j = 0; j < numrows * numcols; j++) {
				int sum = 0;				// initialize the matrix element to be 0
				for (int k = 0; k < maxID + 1; k++)
					// update matrix element
					sum += assignmentMatrix[i][k] * differenceMatrix[j][k];
				if (sum > 0 || i == j)
					reachMatrix[i][j] = 1; // assign matrix element
				pathMatrix[i][j] = sum;
			}
			for (int j = 0; j < maxID + 1; j++) {								// the total price array has to be filled in after the assignmentVector is completely filled
				if (connectionMatrix[i][j] > 0 && assignmentVector[j] >= 0) {
					totalPriceOfAllResources[i] += agents[assignmentVector[j] / numcols][assignmentVector[j] % numcols].numberOfResourcesAssignedToAgent;
				}
			}
		}
		for (int i = 0; i < maxID + 1; i++) {
			int sum = 0;
			for (int j = 0; j < numrows * numcols; j++)
				sum += connectionMatrix[j][i];
			numberOfConnectedAgentsArray[i] = sum;
		}
		// update the adjacencyMatrix from the connection Matrix
		adjacencyMatrix = matrixMultiply(connectionMatrix, connectionMatrix);
		for (int i = 0; i < numrows * numcols; i++)
			adjacencyMatrix[i][i] = 1;
	} // end updating matrices from data

	// ** Print (Bipartite) DOT Language ************************************************************
	public void printDOT2DVisualization(int DOT_OPTION) // 1 for bipartite, 2 for n2, 3 doe sfdp, >=10 for bidding
	{
		GraphViz gv = new GraphViz();
		String prefix = "O" + algorithm; // declare and initialize string for prefix
		String postfix = DOT_OPTION == 2 || DOT_OPTION >= 2 ? GraphOutputFileName : BipartiteOutputFileName;
		String graphName = prefix + "_" + algorithmStep + (DOT_OPTION < 10 ? "" : "_" + DOT_OPTION) + postfix;
		gv.addln("graph " + graphName);
		gv.addln("{");
		if (DOT_OPTION == 1)				// if bipartite graph
			gv.addln("\tlayout = dot;\n\trankdir = LR;");
		else if (DOT_LAYOUT == 2 || DOT_OPTION >= 10)
			// outputStream.println("\tlayout = neato;\n\toverlap = false;");
			; // this is modified after adding in location tracker
		else {								// if not manual
			if (DOT_LAYOUT == 3)
				gv.addln("\tlayout = sfdp;");
		}
		gv.addln("\toutputorder = edgesfirst;");
		// print out every agent
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {
				String agentLabel = "" + (i * numcols + j);
				// uncomment the next 2 lines for the leading 0s
				while (agentLabel.length() <= Math.log10(numrows * numcols - 1))
					agentLabel = "0" + agentLabel;
				String ownLabel = "" + agents[i][j].numberOfResourcesAssignedToAgent;
				while (ownLabel.length() <= Math.log10(maxID))
					ownLabel = "0" + ownLabel;
				String ownLabel2 = "" + agents[i][j].numberOfResourcesConnectedToAgent;
				while (ownLabel2.length() <= Math.log10(maxID))
					ownLabel2 = "0" + ownLabel2;
				// outputStream.print("\tA"+intWalker+" [label = \"a"+intWalker+":"+agents[i][j].numberOfResourcesAssignedToAgent+"/"+agents[i][j].numberOfResourcesConnectedToAgent+"\" shape = box pos = \""+j*DOT_SCALE+", "+i*DOT_SCALE+"\"];\n");
				String color = agentsTakeTurnsColor.size() > 0 ? agentsTakeTurnsColor.get(agentsTurnOrder[i * numcols + j]) : "white";
				if (DOT_OPTION >= 10) {
					String bidLabel = "";
					if (DOT_OPTION == 10) {
						while (bidLabel.length() <= Math.log10(maxID))
							bidLabel = "0" + bidLabel;
					}
					if (DOT_OPTION == 11 || DOT_OPTION == 12) {
						bidLabel = "" + agentsBiddingPrices[i * numcols + j];
						while (bidLabel.length() <= Math.log10(maxID))
							bidLabel = "0" + bidLabel;
					}
					gv.add("\tA" + (i * numcols + j) + " [label = \"a" + agentLabel + "\\n" + ownLabel + "/" + ownLabel2 + "\\nb 1/" + bidLabel
									+ "\" shape = box pos = \"" + j * DOT_SCALE + ", " + i * DOT_SCALE
									+ "\" style = filled fillcolor = white];\n");
				} else {
					gv.add("\tA" + (i * numcols + j) + " [label = \"a" + agentLabel + "\\na " + ownLabel + "\\nc " + ownLabel2
							+ "\" shape = box pos = \"" + j * DOT_SCALE + ", " + i * DOT_SCALE + "\" style = filled fillcolor = " + color + "];\n");
				}
			}
		}
		for (int i = 0; i <= maxID; i++)	// print out every resource
		{
			String resourceLabel = "" + i;
			// uncomment the next 2 lines for the leading 0s
			while (resourceLabel.length() <= Math.log10(maxID))
				resourceLabel = "0" + resourceLabel;
			if (resources[i].ID != -1)	// check to make sure that the resource is alive
			{
				if (DOT_OPTION >= 10 && DOT_OPTION <=12) {
					String resourceLabel2 = "" + (DOT_OPTION == 12 ? highestBidArray[i][1] : agents[assignmentVector[i] / numcols][assignmentVector[i] % numcols].numberOfResourcesAssignedToAgent);
					while (resourceLabel2.length() <= Math.log10(maxID)) {
						resourceLabel2 = "0" + resourceLabel2;
					}
					gv.addln("\tr" + i + " [label = \"r" + resourceLabel + "\\np 1/" + resourceLabel2 + "\" pos = \""
							+ resources[i].column * DOT_SCALE + ", " + resources[i].row * DOT_SCALE
							+ "\" style = filled fillcolor = cyan2];");
				} else
					gv.addln("\tr" + i + " [label = \"r" + resourceLabel + "\\nc " + numberOfConnectedAgentsArray[i]
							+ "\" pos = \"" + resources[i].column * DOT_SCALE + ", " + resources[i].row * DOT_SCALE
							+ "\" style = filled fillcolor = cyan2];");
			}
		}
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {
				Resource resourceWalker = agents[i][j].firstResource;
				while (resourceWalker != null) {
					gv.add("\tA" + (i * numcols + j)); // print bold red if assigned, else print dotted if connected
					// bold red = assignment, bold blue = winning bid, regular blue = bidding, dashed blue = losing bid, regular green = not involved in bidding
					if (resources[resourceWalker.ID].assignedRow == i && resources[resourceWalker.ID].assignedColumn == j) {
						if (DOT_OPTION == 12 && highestBidArray[resourceWalker.ID][0] != i * numcols + j)
							gv.addln(" -- r" + resourceWalker.ID + " [style = dashed] [color = orange];");
						else
							gv.addln(" -- r" + resourceWalker.ID + " [style = bold] [color = cyan2];");
					} else if (checkContains(resourceWalker.ID, i, j)) {
						if (DOT_OPTION == 11 && bidMatrix[i * numcols + j][resourceWalker.ID] > 0)
							gv.addln(" -- r" + resourceWalker.ID + " [color = orange];");
						else if (DOT_OPTION == 12 && highestBidArray[resourceWalker.ID][0] == i * numcols + j)
							gv.addln(" -- r" + resourceWalker.ID + " [style = bold] [color = orange];");
						else if (DOT_OPTION == 12 && bidMatrix[i * numcols + j][resourceWalker.ID] > 0)
							gv.addln(" -- r" + resourceWalker.ID + " [color = orange];");
						else
							gv.addln(" -- r" + resourceWalker.ID + ";");
					}
					resourceWalker = resourceWalker.nextResource;
				}
			}
		}
		String fairnessIndexString = "" + scaledAgentFairnessIndexFromScratch();
		while (fairnessIndexString.length() <= 6) {
			fairnessIndexString += "0";
		}
		gv.addln("\t" + graphName + "[label = \"" + graphName + "_FI="
				+ fairnessIndexString.substring(0, 6) + "\" pos = \"" + ((numcols - 1) / 2.0 * DOT_SCALE) + ", " + (-0.75 * DOT_SCALE)
				+ "!\" color = invis fontsize = "
				+ (8 + 2.5 * 16) + "]"); 
		gv.addln("}");
		// System.out.println(gv.getDotSource()); // PRINT GRAPHVIZ CODE
		String type = "pdf";
		byte[] byteGraph = gv.getGraph(gv.getDotSource(), type, "neato");
		new File(visualsOutputLocation).mkdirs();
		String outputLocation = visualsOutputLocation + graphName + "." + type;
		File out = new File(outputLocation);    // Windows
		gv.writeGraphToFile(byteGraph, out);
		if (vocal)
			System.out.println("\twrote graph: " + graphName + " to " + outputLocation);
	}

	// ** Print Agent Level Graph ********************************************************************
	public void printAgentLevelGraph(int agentLevelOption, int[][] offerMinMatrix, int[][] acceptMaxMatrix) // 0 with path labels, 1 default without path labels, 2 BCIP, 3 BIP, 4 Bid, 5 OA,
																											// 6 MM, 7 SM
		{
		GraphViz gv = new GraphViz();
		String graphName = "O" + algorithmStep; // compute the file name
		if (agentLevelOption == 0)
			graphName += "_None";
		else if (agentLevelOption == 2)
			graphName += "_BCIP";
		else if (agentLevelOption == 3)
			graphName += "_BIP";
		else if (agentLevelOption == 4)
			graphName += "_Bid";
		else if (agentLevelOption == 5)
			graphName += "_OA";
		else if (agentLevelOption == 6)
			graphName += "_mM";
		else if (agentLevelOption == 7)
			graphName += "_SM";
		graphName += AgentLevelGraphOutputFileName;
		// start printing the GraphVIZ code
		// The following is a comment for the unique connection matrix
		// gv.add("//ALG");
		// for (int i = 0; i < numrows * numcols; i++) {
		// gv.add(" ");
		// for (int j = 0; j < maxID + 1; j++) {
		// gv.add("" + connectionMatrix[i][j]);
		// }
		// }
		// gv.addln();
		gv.addln("digraph " + graphName + "\n{");
		gv.addln("\toverlap = false;");
		gv.addln("\tsplines = false;"); // turn off splines to be able to have more edges
		gv.addln("\toutputorder = edgesfirst;");
		double maxNumberOfAssignedResources = 0;
		for (int i = 0; i < numrows; i++)
			for (int j = 0; j < numcols; j++) {							// print out every agent and their debug information
				gv.add("\tA" + (i * numcols + j) + " [label = \"A" + (i * numcols + j) + "\\na "
						+ agents[i][j].numberOfResourcesAssignedToAgent + "\\nc " + agents[i][j].numberOfResourcesConnectedToAgent
						+ "\" shape = box style = filled fillcolor = white];\n");
				if (agents[i][j].numberOfResourcesAssignedToAgent > maxNumberOfAssignedResources)
					maxNumberOfAssignedResources = agents[i][j].numberOfResourcesAssignedToAgent;
			}
		// count down to zero to manually set the rank of agents
		for (int numberOfResources = (int) maxNumberOfAssignedResources; numberOfResources >= 0; numberOfResources--)
			gv.addln("\t" + numberOfResources + " [label = \"" + numberOfResources + "\" color=invis];");
		gv.add("\t");
		for (int numberOfResources = (int) maxNumberOfAssignedResources; numberOfResources > 0; numberOfResources--)
			gv.add(numberOfResources + " -> "); // connect the rank determining agents together
		gv.add("0 [color=invis];\n");
		for (int numberOfResources = 0; numberOfResources <= maxNumberOfAssignedResources; numberOfResources++) { // for every rank of agents
			if (numberOfResources == 0) // classify the ranks
				gv.add("\t{rank = max; " + numberOfResources + " ");
			else if (numberOfResources == maxNumberOfAssignedResources)
				gv.add("\t{rank = min; " + numberOfResources + " ");
			else
				gv.add("\t{rank = same; " + numberOfResources + " ");
			int rankCounter = 1; // start off with rank counter as 1
			// so that it will print a new line
			// after certain amount of agents of the same rank
			for (int i = 0; i < numrows; i++) {
				for (int j = 0; j < numcols; j++) {
					if (agents[i][j].numberOfResourcesAssignedToAgent == numberOfResources) { // print out agents
						if (rankCounter % numberOfAgentsWithSameRank == 0) { // tab twice for next line of same agents
							gv.add("};\n\t\t//");
						}
						gv.add("A" + (i * numcols + j) + " ");
						rankCounter++;
					}
				}
			}
			gv.add("};\n");
		}
		if (agentLevelOption == 0 || agentLevelOption == 1) {
			String label = "";
			int numberOfPaths = 0;
			for (int i = 0; i < numrows * numcols; i++) {								// now compute which agent should point to which
				for (int j = 0; j < numrows * numcols; j++) {							// do computation using the reach matrix
					if (reachMatrix[i][j] > 0 && i != j) {						// there exist a path from 1 agent to another
						label = "";
						numberOfPaths = 0;
						for (int k = 0; k < maxID + 1; k++) {
							if (assignmentMatrix[i][k] == 1 && checkContains(k, i / numcols, i % numcols)
									&& checkContains(k, j / numcols, j % numcols)) {				// if there is a path from i to j through resource k
								label += k + ","; // no not add an extra space so that more edges can fit
								numberOfPaths++; // increase the number of paths to increase the thickness
							}
						}
						gv.add("\tA"
								+ i
								+ " -> A"
								+ j
								+ " ["
								+ (agentLevelOption == 0 ? "label = \""
										+ (label.length() > 0 ? label.subSequence(0, label.length() - 1) : label) + "\" " : "")
								+ "penwidth = "
								+ numberOfPaths
								+ " color = "
								+ (agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent > agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent ?
								// if agent i has more resources than agent j, then either green or blue, else red and false constraint
								((agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
										- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent == 1) ? "yellow" : "green")
										: "red constraint=false") + "];\n");
					}
				}
			}
		} else // if (not vanilla ALG) // if (ALG for algorithm)
		{
			String sink = "";
			String source = "";
			switch (agentLevelOption) {
			case 2:
				sink = "deeppink";
				source = "maroon";
				break;
			case 3:
				sink = "orchid";
				source = "darkorchid3";
				break;
			case 4:
				sink = "slategray";
				source = "darkslategray4";
				break;
			case 5:
				sink = "orange";
				source = "darkorange3";
				break;
			case 6:
				sink = "skyblue";
				source = "darkslateblue";
				break;
			case 7:
				sink = "paleturquoise";
				source = "darkturquoise";
				break;
			default:
				break;
			}
			for (int i = 0; i < numrows * numcols; i++) {								// now compute which agent should point to which
				for (int j = 0; j < numrows * numcols; j++) {							// do computation using the reach matrix
					if (reachMatrix[i][j] > 0
							&& i != j
							&& agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent
									- agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent > 1) {	// there exist a path from 1 agent to another
						gv.addln("\tA" + i + " -> A" + j + " [constraint = false];");
					}
				}
			}
			for (int i = 0; i < numrows * numcols; i++) {								// now compute which agent should point to which
				for (int j = 0; j < numrows * numcols; j++) {							// do computation using the reach matrix
					if (offerMinMatrix[i][j] > 0 && i != j) {						// there exist a path from 1 agent to another
						gv.addln("\tA" + i + " -> A" + j + " [color = " + sink + " style = dashed];");
					}
					if (acceptMaxMatrix[j][i] > 0 && i != j) {						// there exist a path from 1 agent to another
						gv.addln("\tA" + i + " -> A" + j + " [color = " + source + " style = dotted constraint = false];");
					}
				}
			}
		}
		gv.addln("}");
		// System.out.println(gv.getDotSource()); // PRINT GRAPHVIZ CODE
		String type = "png";
		byte[] byteGraph = gv.getGraph(gv.getDotSource(), type, "dot");
		new File(visualsOutputLocation).mkdirs();
		String outputLocation = visualsOutputLocation + graphName + "." + type;
		File out = new File(outputLocation);    // Windows
		gv.writeGraphToFile(byteGraph, out);
		if (vocal)
			System.out.println("\twrote graph: " + graphName + " to " + outputLocation);
	}	// end print agent level graph

	// ** Print Agents Graph ********************************************************************
	public void printAgentsGraph(int colorScale, String coreGaphName) // 1 for greyScale, 2 for color
	{
		String graphName = "";
		if (colorScale == 1)
			graphName = "O" + AgentsGrayScaleOutputFileName + coreGaphName;
		if (colorScale == 2)
			graphName = "O" + AgentsColorOutputFileName + coreGaphName;
		// start printing the GraphVIZ code
		GraphViz gv = new GraphViz();
		String color = "yellow";
		gv.addln("digraph " + graphName);
		gv.addln("{");
		gv.addln("\toverlap = false;");
		gv.addln("\tsplines = false;"); // turn off splines to be able to have more edges
		gv.addln("\toutputorder = edgesfirst;");
		int maxNumberOfResourcesConnectedToAgent = 0;
		ArrayList<Integer> uniqueNumberOfResourcesAssignedToAgent = new ArrayList<>();
		for (int i = 0; i < numrows; i++)
			for (int j = 0; j < numcols; j++) {
				if (agents[i][j].numberOfResourcesConnectedToAgent > maxNumberOfResourcesConnectedToAgent)
					maxNumberOfResourcesConnectedToAgent = agents[i][j].numberOfResourcesConnectedToAgent;
				if (!uniqueNumberOfResourcesAssignedToAgent.contains(agents[i][j].numberOfResourcesAssignedToAgent))
					uniqueNumberOfResourcesAssignedToAgent.add(agents[i][j].numberOfResourcesAssignedToAgent);
			}
		Collections.sort(uniqueNumberOfResourcesAssignedToAgent);
		double[] percentTiles = { 0.05, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95, 0.99 };
		if (colorScale == 1) {
			double[] dummyPercentTiles = { 0.11, 0.22, 0.33, 0.44, 0.56, 0.67, 0.78, 0.89, 0.99 };
			percentTiles = new double[9];
			for (int i = 0; i < dummyPercentTiles.length; i++)
				percentTiles[i] = dummyPercentTiles[i];
		}
		int[] percentTilesRank = new int[percentTiles.length];
		for (int i = 0; i<percentTiles.length; i++)
			percentTilesRank[i] = (int) Math.round(percentTiles[i] * uniqueNumberOfResourcesAssignedToAgent.size() - 0.5);
		double mean = (double) (N) / (numrows * numcols);
		double[] agentsNumberOfAssignedResources = new double[numrows*numcols];
		for (int i = 0; i < agentsNumberOfAssignedResources.length; i++)
			agentsNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		if (algorithmStep == 5 * stepScale) {
			standardDeviation = standardDeviationCalculate(agentsNumberOfAssignedResources);
			if (colorScale == 2) {									// if color
				double[] cutoffsTemp = { 2.45, 1.91, 1.36, 0.82, 0.27, -0.27, -0.82, -1.36, -1.91, -2.45, -2.45 };
				cutoffs = cutoffsTemp;
				//	System.out.println("mean = " + mean + " std = " + standardDeviation + " lower bound = " + (mean + cutoffs[cutoffs.length - 1] * standardDeviation));
				for (int i = 0; i <= cutoffs.length / 2; i++) {
					cutoffs[i] = mean + cutoffs[i] * standardDeviation;
				}
				if (mean + cutoffs[cutoffs.length - 1] * standardDeviation < 0) {
					double averageWidthOfBelowAverage = cutoffs[cutoffs.length / 2] / (cutoffs.length / 2);
					// System.out.println("averageWidthOfBelowAverage = " + averageWidthOfBelowAverage);
					for (int i = cutoffs.length / 2 + 1; i < cutoffs.length; i++) {
						cutoffs[i] = (cutoffs.length - 1 - i) * averageWidthOfBelowAverage;
					}
				} else {
					for (int i = cutoffs.length / 2 + 1; i < cutoffs.length; i++) {
						cutoffs[i] = mean + cutoffs[i] * standardDeviation;
					}
				}
			}
		}
		if (cutoffs == null) {
			System.out.println("\tagents graph NOT printed because color was not determined");
			return;
		}
		double size = cutoffs.length > 0 ? 2.0 * numrows / (double) cutoffs.length : 0;
		for (int i = 0; i < cutoffs.length; i++) {
			String label = (i < cutoffs.length - 1 || cutoffs[i] == 0 ? ">=" : "<") + cutoffs[i];
			while (label.length() < 6) {
				label += "0";
			}
			gv.addln("\tc" + i + " [label = \"" + label.substring(0, 6) + "\" shape = square pos = \"" + -0.9 * DOT_SCALE + ", " + (numrows  - 1) / 10.0 * (10.0 - i) * DOT_SCALE + "!\" style = filled fillcolor = " + "\"#" + colors[i] + "\" width = " + size + " height = " + size + " fontsize = " + (8 + size * 16) + "];");
		}
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {							// print out every agent and their debug information
				int agentsNumberOfResourcesAssignedToAgent = agents[i][j].numberOfResourcesAssignedToAgent;
				if (colorScale == 1) {									// if grayScale
					if (agentsNumberOfResourcesAssignedToAgent >= mean + 2.33 * standardDeviation)
						color = "\"#000000\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= mean + 1.67 * standardDeviation)
						color = "\"#252525\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= mean + 1.00 * standardDeviation)
						color = "\"#525252\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= mean + 0.33 * standardDeviation)
						color = "\"#737373\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= mean - 0.33 * standardDeviation)
						color = "\"#969696\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= mean - 1.00 * standardDeviation)
						color = "\"#bdbdbd\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= mean - 1.67 * standardDeviation)
						color = "\"#d9d9d9\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= mean - 2.33 * standardDeviation)
						color = "\"#f0f0f0\"";
					else
						color = "\"#ffffff\"";
					//						 System.out.println("actual = " + agentsNumberOfResourcesAssignedToAgent + " mean = " + mean + " sd = "
					//						 + standardDeviation);
				}
				if (colorScale == 2) {
					if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[0])
						color = "\"#006837\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[1])
						color = "\"#1a9850\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[2])
						color = "\"#66bd63\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[3])
						color = "\"#a6d96a\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[4])
						color = "\"#d9ef8b\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[5])
						color = "\"#ffffbf\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[6])
						color = "\"#fee08b\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[7])
						color = "\"#fdae61\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[8])
						color = "\"#f46d43\"";
					else if (agentsNumberOfResourcesAssignedToAgent >= cutoffs[9])
						color = "\"#d73027\"";
					else
						color = "\"#a50026\"";
				}
				size = (double) agents[i][j].numberOfResourcesConnectedToAgent / maxNumberOfResourcesConnectedToAgent * 2.5;
				String agentLabel = "" + (i * numcols + j);
				while (agentLabel.length() < 1 + (int) Math.log10(numrows * numcols - 1)) {
					agentLabel = "0" + agentLabel;
				}
				String resourceAssignedLabel = "" + agents[i][j].numberOfResourcesAssignedToAgent;
				while (resourceAssignedLabel.length() < 1 + (int) Math.log10(maxNumberOfResourcesConnectedToAgent - 1)) {
					resourceAssignedLabel = "0" + resourceAssignedLabel;
				}
				// System.out.println("math.log10 =" + Math.log10(maxNumberOfResourcesConnectedToAgent - 1) + "while max = "
				// + maxNumberOfResourcesConnectedToAgent);
				String resourceConnectedLabel = "" + agents[i][j].numberOfResourcesConnectedToAgent;
				while (resourceConnectedLabel.length() < 1 + (int) Math.log10(maxNumberOfResourcesConnectedToAgent - 1)) {
					resourceConnectedLabel = "0" + resourceConnectedLabel;
				}
				gv.addln("\tA" + (i * numcols + j) + " [label = \"A" + agentLabel + "\\na " + resourceAssignedLabel
						+ "\\nc " + resourceConnectedLabel + "\" shape = square pos = \"" + j * DOT_SCALE	+ ", "
						+ i	* DOT_SCALE	+ "!\" style = filled fillcolor = "	+ color	+ " width = " + size
						+ " height = " + size + " fontsize = " + (8 + size * 16)
						+ (color.equals("\"#000000\"") || color.equals("\"#252525\"") || color.equals("\"#525252\"")
								|| color.equals("\"#737373\"") || color.equals("\"#006837\"") || color.equals("\"#a50026\"") ? " fontcolor = white"
										: "") + "];");
			}
		}
		int numberOfPaths = 0;
		for (int i = 0; i < numrows * numcols; i++) {								// now compute which agent should point to which
			for (int j = 0; j < numrows * numcols; j++) {							// do computation using the reach matrix
				if (reachMatrix[i][j] > 0 && i != j) {								// there exist a path from 1 agent to another
					numberOfPaths = 0;
					for (int k = 0; k < maxID + 1; k++) {
						if (assignmentMatrix[i][k] == 1 && checkContains(k, i / numcols, i % numcols)
								&& checkContains(k, j / numcols, j % numcols)) {				// if there is a path from i to j through resource k
							numberOfPaths++; // increase the number of paths to increase the thickness
						}
					}
					gv.add("\tA" + i + " -> A" + j + " [penwidth = " + numberOfPaths + " color = "
							+ (agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent > agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent ?
									// if agent i has more resources than agent j, then either green or blue, else red and false constraint
									((agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - agents[j / numcols][j % numcols].numberOfResourcesAssignedToAgent == 1) ? 
											"blue style = dashed" : "green") : "red style = dotted") + "];\n");
				}
			}
		}
		String fairnessIndexString = "" + scaledAgentFairnessIndexFromScratch();
		while (fairnessIndexString.length() <= 6) {
			fairnessIndexString += "0";
		}
		gv.addln("\t" + graphName + "[label = \"" + graphName + "_Ex=" + results + "_Diff=" + numberOfDifferences + "_FI="
				+ fairnessIndexString.substring(0, 6) + "\" pos = \"" + ((double) ((numcols - 1) / 2) * DOT_SCALE) + ", " + (-0.75 * DOT_SCALE)
				+ "!\" color = invis fontsize = "
				+ (8 + 2.5 * 16) + "]");
		gv.addln("}");
		// System.out.println(gv.getDotSource());
		String type = "pdf";
		byte[] byteGraph = gv.getGraph(gv.getDotSource(), type, "neato");
		new File(visualsOutputLocation).mkdirs();
		String outputLocation = visualsOutputLocation + graphName + "." + type;
		File out = new File(outputLocation);    // Windows
		gv.writeGraphToFile(byteGraph, out);
		if (vocal)
			System.out.println("\twrote graph: " + graphName + " to " + outputLocation);
//		System.out.println(gv.getDotSource());
	}	// end print agents graph

	// ** Print Agent Space Graph (shows all possible distributions) *******************************
	public void printASG(int format, int uniqueFileName) {									
		// format 1 displays old format with A labels and what each agent gets
		// format 2 displays connection matrix and assignment matrices
		long numberOfPermutations = 1;
		int maxPermutations = 128;
		int[] numberOfResourcePermutation = new int[maxID + 1];
		for (int i = 0; i < maxID + 1; i++) {
			int resourcePermutationCounter = 0;
			for (int j = 0; j < numrows * numcols; j++) {
				if (connectionMatrix[j][i] > 0) {
					resourcePermutationCounter++;
				}
			}
			numberOfResourcePermutation[i] = resourcePermutationCounter;
			numberOfPermutations *= resourcePermutationCounter == 0 ? 1 : resourcePermutationCounter;
			if (resourcePermutationCounter == 0) {
				System.out.println("\terror: r" + i + " is disconnected. There are no permutations for the agent space graph. Agent space graph NOT printed.");
				return; // TODO
			}
		}
		if ((int) numberOfPermutations <= 0) {
			System.out.println("\terror: there are too many permutations to be printed. Agent space graph NOT printed.");
			return; 
		}

		GraphViz gv = new GraphViz();
		String graphName = "O" + (uniqueFileName >= 0 ? "_" + uniqueFileName : "")
				+ (format != 1 ? AssignmentSpaceGraphOutputFileName : AllocationSpaceGraphOutputFileName); // compute the file name
		gv.add("//2D");
		for (int i = 0; i < numrows * numcols; i++) {
			gv.add(" ");
			for (int j = 0; j < maxID + 1; j++) {
				gv.add("" + connectionMatrix[i][j]);
			}
		}
		gv.addln();
		gv.addln("digraph " + graphName);
		gv.addln("{");
		gv.addln("\trankdir = BT;");
		if (printASGLabels)
			gv.addln("\tsplines = false;"); // turn off splines to be able to have more edges
		gv.addln("\toutputorder = edgesfirst;");
		int[][] agentSpaces = new int[(int) numberOfPermutations][maxID + 1]; // every permutation's assignment
		int numberOfResourcePermutationWalker = 1;
		long numberOfSameRepeatedAgentID = numberOfPermutations;
		for (int i = 0; i < maxID + 1; i++) {
			if (numberOfResourcePermutation[i] == 0)
				continue;
			numberOfSameRepeatedAgentID /= numberOfResourcePermutation[i];
			for (int j = 0; j < numberOfResourcePermutationWalker; j++) {
				int agentWalker = 0;
				for (int k = 0; k < numrows * numcols; k++) {
					if (connectionMatrix[k][i] > 0) {
						for (long l = 0; l < numberOfSameRepeatedAgentID; l++) {
							agentSpaces[(int) (j * numberOfSameRepeatedAgentID * numberOfResourcePermutation[i] + agentWalker
									* numberOfSameRepeatedAgentID + l)][i] = k;
						}
						agentWalker++;
					}
				}
			}
			numberOfResourcePermutationWalker *= numberOfResourcePermutation[i];
		}
		for (int i = 0; i < agentSpaces.length; i++) {
			gv.add("\t//a" + i + ": ");
			for (int j = 0; j < agentSpaces[0].length; j++) {
				gv.add(agentSpaces[i][j] + " ");
			}
			gv.addln();
		}
		printMatrices(12);
		ArrayList<Double> uniqueFIs = new ArrayList<Double>(); 			// used to do ranking
		double[] permutationFI = new double[(int) numberOfPermutations];	// used to keep track of the FI for each permutation
		ArrayList<String> allocations = new ArrayList<String>();		// used to keep track of all allocations
		ArrayList<Double> allocationFIs = new ArrayList<Double>(); 		// need a seperate array since there are different allocations for same FI
		ArrayList<Integer> allocationFICount = new ArrayList<Integer>();
		boolean tempVocal = vocal;
		vocal = false;
		for (int i = 0; i < numberOfPermutations; i++) {
			assignmentMatrix = new int[numrows * numcols][maxID + 1];
			int[] numberOfResourcesAssignedToAgentCounter = new int[numrows * numcols];
			for (int j = 0; j < maxID + 1; j++) {
				assignmentMatrix[agentSpaces[i][j]][j] = 1;
				numberOfResourcesAssignedToAgentCounter[agentSpaces[i][j]]++;			// count the resources of each agent
			}
			// Arrays.sort(numberOfResourcesAssignedToAgentCounter);						// sort those resources TODO
			printMatrices(13);
			useAssignmentInput(IOTempAssignmentMatrixFileName2);
			permutationFI[i] = fairnessIndexWithWeightedAgents2(2, null, false);
//			permutationFI[i] = fairnessIndex(2, null, false); TODO
			if (!uniqueFIs.contains(permutationFI[i]))
				uniqueFIs.add(permutationFI[i]);
			String allocationString = "}";
			for (int j = 0; j < numberOfResourcesAssignedToAgentCounter.length; j++)
				allocationString = (j == numberOfResourcesAssignedToAgentCounter.length - 1 ? "{" : ", ") + numberOfResourcesAssignedToAgentCounter[j] + allocationString;
			if (!allocations.contains(allocationString)) {
				allocations.add(allocationString);
				allocationFIs.add(permutationFI[i]);
				allocationFICount.add(1);
				if (showDebug) {
					System.out.print("\t\tallocation added: ");
					System.out.println(allocationString);
				}
			} else {
				allocationFICount.set(allocations.indexOf(allocationString), allocationFICount.get(allocations.indexOf(allocationString)) + 1);
			}
		}

		Collections.sort(uniqueFIs);

		if (format != 1) {
			gv.add("\tCM [label = \"C Matrix:\\n");
			for (int i = 0; i < numrows * numcols; i++) {
				for (int j = 0; j < maxID + 1; j++) {
					gv.add("" + connectionMatrix[i][j]);
				}
				gv.add("\\n");
			}
			gv.add("\" shape = box]\n");
		}
		
		gv.add("\tWeight [label = \"Weight:\\n");
		for (int i = 0; i < numrows * numcols; i++) {
			gv.add(agentsWeight[i] + "\\n");
		}
		gv.add("\" shape = box]\n");

		if (numberOfPermutations <= maxPermutations)
			for (int i = 0; i < numberOfPermutations; i++) {
				if (format == 1) {
					gv.add("\tA" + i + " [label = \"A" + i + "\\n");
					for (int j = 0; j < numrows * numcols; j++) {
						String stringToPrintOnLine = "";
						for (int k = 0; k < maxID + 1; k++) {
							if (agentSpaces[i][k] == j) {
								stringToPrintOnLine += k + ", ";
							}
						}
						if (stringToPrintOnLine.length() == 0) {
							gv.add("X, \\n");
						} else
							gv.add(stringToPrintOnLine + "\\n");
					}
					gv.add("\" shape = box style = filled fillcolor = pink];\n");
				} else {
					gv.add("\tA" + i + " [label = \"");
					for (int j = 0; j < numrows * numcols; j++) {
						for (int k = 0; k < maxID + 1; k++) {
							gv.add(agentSpaces[i][k] == j ? "1" : "0");
						}
						gv.add("\\n");
					}
					gv.add("\" shape = box style = filled fillcolor = pink];\n");
				}
			}

		if (showDebug) {
			System.out.print("\t\tallocationFIs: ");
			for (int i = 0; i < allocationFIs.size(); i++)
				System.out.print(allocationFIs.get(i) + ", ");
			System.out.println();
			System.out.print("uniqueFIs: ");
			for (int i = 0; i < uniqueFIs.size(); i++)
				System.out.print(uniqueFIs.get(i) + ", ");
			System.out.println();
		}

		for (int i = 0; i < uniqueFIs.size(); i++) {
			// gv.addf("\t" + uniqueFIs.get(i) + " [label = \"%1.4f", uniqueFIs.get(i));
			String uniqueFIString = "" + uniqueFIs.get(i);  // might cause problems if length is less than 6
			while (uniqueFIString.length()<6)
			{
				if (uniqueFIString.contains("."))
					uniqueFIString += "0";
				else {
					uniqueFIString += ".";
				}
			}
			gv.add("\t" + uniqueFIs.get(i) + " [label = \"" + uniqueFIString.substring(0, 6));
			for (int j = 0; j < allocationFIs.size(); j++) {
				if (showDebug)
					System.out.println("uniqueFI =" + uniqueFIs.get(i) + " while allocationFIs = " + allocationFIs.get(j));
				if (uniqueFIs.get(i).equals(allocationFIs.get(j))) {
					if (showDebug)
						System.out.println("adding " + allocations.get(j) + " to " + uniqueFIs.get(i) + " for i = " + i + " and j = " + j);
					gv.add("\\n");
					gv.add(allocations.get(j));
					gv.add("x" + allocationFICount.get(j));
				}
			}
			gv.add("\" color=invis];\n");
		}

		gv.add("\t");
		for (int i = 0; i < uniqueFIs.size() - 1; i++)
			// (int)maxNumberOfAssignedResources; numberOfResources>0; numberOfResources--)
			gv.add(uniqueFIs.get(i) + " -> "); // connect the rank determining agents together
		gv.add(uniqueFIs.get(uniqueFIs.size() - 1) + ";\n");
		if (format != 1 && uniqueFIs.size() > 0)
			gv.add("\t" + uniqueFIs.get(uniqueFIs.size() - 1) + " -> CM [color=invis];\n");
		// print out agents with the same rank
		for (int i = 0; i < uniqueFIs.size(); i++) {								// for every rank of agents
			if (i == 0)	// classify the ranks
				gv.add("\t{rank = min; " + uniqueFIs.get(i) + " ");
			else if (i == uniqueFIs.size() - 1 && format == 1)
				gv.add("\t{rank = max; " + uniqueFIs.get(i) + " ");
			else
				gv.add("\t{rank = same; " + uniqueFIs.get(i) + " ");
			int rankCounter = 1;		// start off with rank counter as 1
			// so that it will print a new line
			// after certain amount of agents of the same rank

			if (numberOfPermutations <= maxPermutations)
				for (int j = 0; j < numberOfPermutations; j++) {
					if (uniqueFIs.get(i) == permutationFI[j]) {
						if (rankCounter % numberOfAgentsWithSameRank == 0) {				// tab twice for next line of same agents
							gv.add("};\n\t\t//");
						}
						gv.add("A" + j + " ");
						rankCounter++;
					}
				}
			gv.add("};\n");
		}
		if (format != 1)
			gv.addln("\t{rank = max; CM};");

		String[] graphVIZColor = { "blueviolet", "brown", "orange", "olivedrab", "tan", "magenta", "cadetblue", "seagreen", "maroon" };
		String[] graphVIZColorTie = { "steelblue", "dodgerblue", "navyblue", "skyblue", "royalblue", "slateblue" };
		int colorIndex = 0;
		String color = "";
		for (int i = 0; i < numberOfPermutations; i++) {
			colorIndex = 0;
			for (int j = 0; j < numberOfPermutations; j++) {
				int numberOfDifferences = 0;
				String label = "";
				for (int k = 0; k < maxID + 1; k++) {
					if (agentSpaces[i][k] != agentSpaces[j][k]) {
						numberOfDifferences++;
						label = "" + k;
						if (numberOfDifferences > 1)
							break;
					}
				}
				if (numberOfDifferences == 1 && numberOfPermutations <= maxPermutations) {
					if (permutationFI[i] < permutationFI[j])
						color = graphVIZColor[colorIndex++ % graphVIZColor.length];
					else
						color = graphVIZColorTie[(int) (Math.random() * graphVIZColorTie.length)];
					if (printASGLabels) {
						if (permutationFI[i] < permutationFI[j])
							gv.add("\tA" + i + " -> A" + j + " [label = \"" + label + "\" color = " + color + " fontcolor = "
									+ color + "];\n");
						if (permutationFI[i] == permutationFI[j] && i > j)
							gv.add("\tA" + i + " -> A" + j + " [label = \"" + label + "\" constraint = false color = " + color
									+ " fontcolor = " + color + "];\n");
					} else {
						if (permutationFI[i] < permutationFI[j])
							gv.add("\tA" + i + " -> A" + j + " [color = " + color + " fontcolor = " + color + "];\n");
						if (permutationFI[i] == permutationFI[j] && i > j)
							gv.add("\tA" + i + " -> A" + j + " [constraint = false color = " + color + " fontcolor = " + color
									+ "];\n");
					}
				}
			}
		}
		useAssignmentInput(IOTempAssignmentMatrixFileName1);
		vocal = tempVocal;
		gv.addln("}");
		println(gv.getDotSource());
		String type = "png";
		byte[] byteGraph = gv.getGraph(gv.getDotSource(), type, "dot");
		new File(visualsOutputLocation).mkdirs();
		String outputLocation = visualsOutputLocation + graphName + "." + type;
		File out = new File(outputLocation);    // Windows
		gv.writeGraphToFile(byteGraph, out);
		if (vocal)
			System.out.println("\twrote graph: " + graphName + " to " + outputLocation);
//		System.out.println(gv.getDotSource());
	}	// end print Agent Space Graph

	
	public String getCDFconnection() {
		String returnValue = "";
		String returnValue2 = "";
		int maxNumberOfConnections = 0;
		
		for (int i = 0; i < numrows * numcols; i++) {
			if (agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent > maxNumberOfConnections) {
				maxNumberOfConnections = agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;
			}
		}
		int[] numConnectionsBins = new int[maxNumberOfConnections + 1];
		for (int i = 0; i < numrows * numcols; i++) {
			numConnectionsBins[agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent]++;
		}
		int runningSum = 0;
		for (int i = 0; i <= maxNumberOfConnections; i++) {
			returnValue += i + " ";
			runningSum += numConnectionsBins[i];
			returnValue2 += runningSum / ((double) numrows * numcols)  + " ";
		}
		return returnValue + "\n" + returnValue2;
	}
	// ** Output pdf and cdf Text ******************************************************************
	public String printPDFandCDF(String fileName, boolean geni, int isConnectionOrAssignment) { 
		// 0 is geni, 1 is same numAssigns, 2 is same n in each bracket
		// 0 is connection, 1 is assignment, else is both
//		PrintWriter outputStream = null;
		int maxNumberOfAssignments = 0;
		int maxNumberOfConnections = 0;
		int totalNumberOfAssignments = 0;
		int totalNumberOfConnections = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			if (agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent > maxNumberOfAssignments) {
				maxNumberOfAssignments = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			}
			if (agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent > maxNumberOfConnections) {
				maxNumberOfConnections = agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;
			}
			
		}
		if (maxNumberOfAssignments > globalMaxNumberOfAssignments) {
			globalMaxNumberOfAssignments = maxNumberOfAssignments;
		}
		if (maxNumberOfConnections > globalMaxNumberOfConnections) {
			globalMaxNumberOfConnections = maxNumberOfConnections;
		}
		println("globalMaxNumberOfConnections="+globalMaxNumberOfConnections);
		int[] numAssignments = new int[(int) globalMaxNumberOfAssignments + 1];
		int[] numConnections = new int[(int) globalMaxNumberOfConnections + 1];
		for (int i = 0; i < numrows * numcols; i++) {
			totalNumberOfAssignments += agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			totalNumberOfConnections += agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;
			numAssignments[agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent]++;
			numConnections[agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent]++;
		}
		String assignmentPDFx = "n ";
		String assignmentPDFy = "pdf ";
		String assignmentCDFy = "cdf ";
		String connectionPDFx = "n ";
		String connectionPDFy = "pdf ";
		String connectionCDFy = "cdf ";
		double previousAssignmentCDF = 0;
		double previousConnectionCDF = 0;
//		for (int i = 0; i < numAssignments.length; i++) {
//			System.out.println(i + ": " + numAssignments[i]);
//		}
//		String returnValue = "";
		if (geni) {
			assignmentPDFx = "n ";
			assignmentPDFy = "geniAssignment ";
			assignmentCDFy = "geniAssignment ";
			connectionPDFx = "n ";
			connectionPDFy = "geniConnection ";
			connectionCDFy = "geniConnection ";
			int totalWealth = 0;
			int[] runningWealth = new int[numrows * numcols];
			int counter = 0;
//			for (int j = 0; j < numConnections.length; j++) {
//				//println(numConnections[j]);
//			}
			for (int j = 0; j < numConnections.length; j++) {
				for (double k = 0; k < numConnections[j]; k++) {
					totalWealth += j;
					runningWealth[counter++] = totalWealth;
					connectionPDFx += counter + " ";
				}
			}
			for (int i = 0; i < runningWealth.length; i++) {
				connectionCDFy += runningWealth[i] / (double) totalWealth + " ";
				// System.out.println(runningWealth[i] / (double) totalWealth);
			}
			totalWealth = 0;
			runningWealth = new int[numrows * numcols];
			counter = 0;
			for (int j = 0; j < numAssignments.length; j++) {
				for (double k = 0; k < numAssignments[j]; k++) {
					totalWealth += j;
					runningWealth[counter++] = totalWealth;
					assignmentPDFx += counter + " ";
				}
			}
			for (int i = 0; i < runningWealth.length; i++) {
				assignmentCDFy += runningWealth[i] / (double) totalWealth + " ";
				// System.out.println(runningWealth[i] / (double) totalWealth);
			}
			if (isConnectionOrAssignment == 0) {
				return fileName + "\nconnections\n" + connectionPDFx + "\n" + connectionCDFy;
//			returnValue = fileName + "\nassignments\n" + assignmentPDFx + "\n" + assignmentCDFy + "\nconnections\n" + connectionPDFx + "\n" + connectionCDFy;
			} else if (isConnectionOrAssignment == 1) {
				return fileName + "\nassignments\n" + assignmentPDFx + "\n" + assignmentCDFy;
			} else {
				return fileName + "\nassignments\n" + assignmentPDFx + "\n" + assignmentCDFy + "\nconnections\n" + connectionPDFx + "\n" + connectionCDFy;
			}
//			int numberOfDivisions = 10;
//			double [] breakPointsAssignments = new double[numberOfDivisions + 1];
//			double [] breakPointsConnections = new double[numberOfDivisions + 1];
//			int minNumberOfAssignments = numrows * numcols + 2;
//			int minNumberOfConnections = N + 2;
//			for (int i = 0; i < numrows * numcols; i++) {
//				if (agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent < minNumberOfAssignments) {
//					minNumberOfAssignments = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
//				}
//				if (agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent < minNumberOfConnections) {
//					minNumberOfConnections = agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;
//				}
//			}
//			breakPointsAssignments[0] = minNumberOfAssignments;
//			breakPointsConnections[0] = minNumberOfConnections;
//			int counter = 0;
//			double counter2 = 1.0;
//			System.out.println("totalNumberOfConnections = " + totalNumberOfConnections);
//			for (int j = 0; j < numConnections.length; j++) {
//				for (double k = 0; k < numConnections[j]; k++) {
//					counter += j;
//					System.out.println("(counter2 * totalNumberOfConnections) / numberOfDivisions) = " + (counter2 * totalNumberOfConnections) / numberOfDivisions);
//					if (counter >= (counter2 * totalNumberOfConnections) / numberOfDivisions) {
//						//System.out.println("(counter2 * totalNumberOfConnections) / numberOfDivisions) = " + (counter2 * totalNumberOfConnections) / numberOfDivisions);
//						breakPointsConnections[(int) counter2] = j + (k + 1) / numConnections[j];
//						System.out.println("counter2 = " + ((int) counter2) + ": "+ breakPointsConnections[(int) counter2]);
//						counter2++;
//					}
//				}
//			}
//			String returnValue = "";
//			for (int i = 0; i < breakPointsConnections.length-1; i++) {
//				System.out.println(breakPointsConnections[i]);
//				returnValue += breakPointsConnections[i]+"-"+breakPointsConnections[i+1] + " ";
//			}
//			println("test23");
//			for (int i = 0; i < breakPointsConnections.length-1; i++) {
//				println(breakPointsConnections[i]+"-"+breakPointsConnections[i+1] + " ");
//			}
//			return returnValue;
		} else {
			DecimalFormat df=new DecimalFormat("#.00"); 
			for (double i = 0; i < globalMaxNumberOfConnections; i += globalMaxNumberOfConnections / 10.0) {
				connectionPDFx += df.format(i) + "-" + df.format(i + globalMaxNumberOfConnections / 10.0) + " ";
				double currentYaxis = 0;
				for (int j = (int) Math.ceil(i); j < Math.ceil(i + globalMaxNumberOfConnections / 10.0); j++) {
					if (j < numConnections.length) {
						currentYaxis += ((double) numConnections[j]) / (numcols * numrows);
					}
				}
				connectionPDFy += currentYaxis + " ";
				previousConnectionCDF += currentYaxis;
				connectionCDFy += previousConnectionCDF + " ";
			}
			for (double i = 0; i < globalMaxNumberOfAssignments || previousAssignmentCDF == 0; i += globalMaxNumberOfAssignments / 10.0) { // 2nd condition in case perfectly bal
				assignmentPDFx += df.format(i) + "-" + df.format(i + globalMaxNumberOfAssignments / 10.0) + " ";
				double currentYaxis = 0;
				//			System.out.println("currentYaxis = 0;");
				for (int j = (int) Math.ceil(i); j < Math.ceil(i + globalMaxNumberOfAssignments / 10.0); j++) {
//					println("j = " + j);
					if (j < numAssignments.length) {
						currentYaxis += ((double) numAssignments[j]) / (numcols * numrows);
//											System.out.println("currentYaxis += " + ((double) numAssignments[j]) / (numcols * numrows));
					}
				}
				assignmentPDFy += currentYaxis + " ";
				previousAssignmentCDF += currentYaxis;
				//			assignmentPDFy += ((double) numAssignments[j]) / (numcols * numrows) + " ";
				//			previousAssignmentCDF += ((double) numAssignments[i]) / (numcols * numrows);
				assignmentCDFy += previousAssignmentCDF + " ";
			}
			if (isConnectionOrAssignment == 0) {
				return fileName + "\nconnections\n" + connectionPDFx + "\n" + connectionPDFy + "\n" + connectionCDFy;
			} else if (isConnectionOrAssignment == 1) {
				return fileName + "\nassignments\n" + assignmentPDFx + "\n" + assignmentPDFy + "\n" + assignmentCDFy;
			} else {
				return fileName + "\nassignments\n" + assignmentPDFx + "\n" + assignmentPDFy + "\n" + assignmentCDFy + "\nconnections\n" + connectionPDFx + "\n" + connectionPDFy + "\n" + connectionCDFy;
			}
		}
//		if (printToFile) {
//			try {								// try to open the file of the computed file name
//				outputStream = new PrintWriter(new FileOutputStream(fileName));
//			} catch (FileNotFoundException e) {
//				System.out.println("\terror opening the file " + fileName);
//				System.exit(0);
//			}
//			outputStream.print(returnValue);
//			outputStream.close();
//		}
//		return returnValue;
	} // End Print PDF and CDF
	
	public void controlSystemSignal(int n) {
		//println("test");
		for (int i = 0; i < numrows * numcols; i++) {
			print(n + " ");
		}
		println();
		for (int i = 0; i < numrows * numcols; i++) {
			print(agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + " ");
		}
		println();
	}
	
	public void controlSystemSignalAuto() {
		int maxIterations = 20;
		print(". ");
		for (int j = 0; j < maxIterations; j++) {
			for (int i = 0; i < numrows * numcols; i++) {
				print(j + " ");
			}
		}
		println();
		for (int k = 0; k < algorithms.length; k++) {
			print(algorithms[k] + " ");
			reset2();
			for (int j = 0; j < maxIterations; j++) {
				for (int i = 0; i < numrows * numcols; i++) {
					print(agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent + " ");
				}
				algorithmCaller(k);
			}
			println();
		}
		reset2();
	}
	
	public void productivityAndOvershoot() {
		boolean tempVocal = vocal;
		vocal = false;
		int maxIterations = 20;
		int[] currentNumberOfAssignedResources = new int[numrows * numcols];
		double average = N / (numrows * numcols);
		double productivity = 0;
		double tempProductivity = 0;
		double PO = 0;
		double tempPO = 0;
		
		PrintWriter[] writer = new PrintWriter[5];
		String fileName = "showProductivityAndOvershoot";
		try {
			for (int w = 0; w < writer.length; w++) {
				writer[w] = new PrintWriter(fileName + w + ".txt");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer[0].println("FI");
		writer[1].println("productivity per iteration");
		writer[2].println("total productivity");
		writer[3].println("percent overshoot per iteration");
		writer[4].println("total overshoot");

		reset2();
		setupStartingFI();
		printMatrices(10);
		printMatrices(11);
			
			
		//updateMinMaxFairnessIndexes();
		
		for (int w = 0; w < writer.length; w++) {
			writer[w].print(". ");
			for (int j = 0; j < maxIterations; j++) {
				writer[w].print(j + " ");
			}
			writer[w].println();
		}
		double initFI = scaledAgentFairnessIndex();
		for (int k = 0; k < algorithms.length; k++) {
			writer[0].print(algorithms[k] + " " + initFI + " ");
			for (int w = 1; w < writer.length; w++)
				writer[w].print(algorithms[k] + " 0 ");
			useConnectionInput(IOConnectionMatrixFileName); // use connection matrix
			useAssignmentInput(IOAssignmentMatrixFileName); // then use assignment matrix
			productivity = 0;
			PO = 0;
			for (int j = 0; j < maxIterations - 1; j++) {
				for (int i = 0; i < numrows * numcols; i++) {
					currentNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
				}
				algorithmCaller(k);
				tempProductivity = 0;
				tempPO = 0;
				for (int i = 0; i < numrows * numcols; i++) {
					tempProductivity += ((currentNumberOfAssignedResources[i] - average)*(currentNumberOfAssignedResources[i] - average) - (agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - average)*(agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - average))/2;
					if ((currentNumberOfAssignedResources[i] > average && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent < average) || (currentNumberOfAssignedResources[i] < average && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent > average))
						tempPO += Math.abs((agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - average)/(currentNumberOfAssignedResources[i] - average));
				}
				productivity += tempProductivity;
				PO += tempPO;
				writer[0].print(scaledAgentFairnessIndex() + " ");
				writer[1].print(tempProductivity / (numrows * numcols) + " ");
				writer[2].print(productivity / (numrows * numcols) + " ");
				writer[3].print(tempPO / (numrows * numcols) + " ");
				writer[4].print(PO / (numrows * numcols) + " ");
			}
			for (int w = 0; w < writer.length; w++)
				writer[w].println();
		}
//		
//		
//		System.out.println("\ntotal productivity");
//		print(". ");
//		for (int j = 0; j < maxIterations; j++) {
//			print(j + " ");
//		}
//		println();
//		for (int k = 0; k < algorithms.length; k++) {
//			productivity = 0;
//			print(algorithms[k] + " 0 ");
//			reset2();
//			for (int j = 0; j < maxIterations - 1; j++) {
//				for (int i = 0; i < numrows * numcols; i++) {
//					currentNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
//				}
//				algorithmCaller(k);
//				for (int i = 0; i < numrows * numcols; i++) {
//					productivity += ((currentNumberOfAssignedResources[i] - average)*(currentNumberOfAssignedResources[i] - average) - (agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - average)*(agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - average))/2;
//				}
//				print(productivity / (numrows * numcols) + " ");
//			}
//			println();
//		}
//		
//		System.out.println("\npercent overshoot per iteration");
//		print(". ");
//		for (int j = 0; j < maxIterations; j++) {
//			print(j + " ");
//		}
//		println();
//		for (int k = 0; k < algorithms.length; k++) {
//			print(algorithms[k] + " 0 ");
//			reset2();
//			for (int j = 0; j < maxIterations - 1; j++) {
//				for (int i = 0; i < numrows * numcols; i++) {
//					currentNumberOfAssignedResources[i] = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
//				}
//				algorithmCaller(k);
//				PO = 0;
//				for (int i = 0; i < numrows * numcols; i++) {
//					if ((currentNumberOfAssignedResources[i] > average && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent < average) || (currentNumberOfAssignedResources[i] < average && agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent > average))
//						PO += Math.abs((agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent - average)/(currentNumberOfAssignedResources[i] - average));
//				}
//				print(PO / (numrows * numcols) + " ");
//			}
//			println();
//		}
//		
//		
		for (int w = 0; w < writer.length; w++) {
			writer[w].close();
		}

		try {
			PrintWriter printWriter = new PrintWriter("output.txt");
			for (int w = 0; w < writer.length; w++) {
				Scanner inputFile = new Scanner(new File(fileName + w + ".txt"));
				while (inputFile.hasNextLine()) {
					String s = inputFile.nextLine();
					printWriter.println(s);
					println(s);
				}
				inputFile.close();
				printWriter.println("\n\n\n\n");
				println("\n\n\n\n");
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done");
		vocal = tempVocal;
	}
	
	public String printStatsAssignments(String fileName, boolean printToFile) {
		PrintWriter outputStream = null;
		int maxNumberOfAssignments = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			if (agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent > maxNumberOfAssignments) {
				maxNumberOfAssignments = agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			}
		}
		if (maxNumberOfAssignments > globalMaxNumberOfAssignments) {
			globalMaxNumberOfAssignments = maxNumberOfAssignments;
		}
		int[] numAssignments = new int[(int) globalMaxNumberOfAssignments + 1];
		for (int i = 0; i < numrows * numcols; i++) {
			numAssignments[agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent]++;
		}
		String assignmentPDFx = "n ";
		String assignmentPDFy = "pdf ";
		String assignmentCDFy = "cdf ";
		double previousAssignmentCDF = 0;
		for (double i = 0; i < globalMaxNumberOfAssignments; i += globalMaxNumberOfAssignments / 10.0) {
			assignmentPDFx += i + "-" + (i + globalMaxNumberOfAssignments / 10.0) + " ";
			double currentYaxis = 0;
//			System.out.println("currentYaxis = 0;");
			for (int j = (int) Math.ceil(i); j < Math.ceil(i + globalMaxNumberOfAssignments / 10.0); j++) {
				if (j < numAssignments.length) {
					currentYaxis += ((double) numAssignments[j]) / (numcols * numrows);
//					System.out.println("currentYaxis += " + ((double) numAssignments[j]) / (numcols * numrows));
				}
			}
			assignmentPDFy += currentYaxis + " ";
			previousAssignmentCDF += currentYaxis;
			assignmentCDFy += previousAssignmentCDF + " ";
		}
		if (printToFile) {
			try {								// try to open the file of the computed file name
				outputStream = new PrintWriter(new FileOutputStream(fileName));
			} catch (FileNotFoundException e) {
				System.out.println("\terror opening the file " + fileName);
				System.exit(0);
			}
			outputStream.println(fileName);
			outputStream.println("assignments");
			outputStream.println(assignmentPDFx);
			outputStream.println(assignmentPDFy);
			outputStream.println(assignmentCDFy);
			outputStream.close();
		}
		return fileName + "\n" + assignmentPDFx + "\n" + assignmentPDFy + "\n" + assignmentCDFy + "\n";
	} // End Print PDF
	
	public String printStatsConnections(String fileName, boolean printToFile) {
		PrintWriter outputStream = null;
		int maxNumberOfConnections = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			if (agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent > maxNumberOfConnections) {
				maxNumberOfConnections = agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;
			}
		}
		if (maxNumberOfConnections > globalMaxNumberOfConnections) {
			globalMaxNumberOfConnections = maxNumberOfConnections;
		}
		int[] numConnections = new int[(int) globalMaxNumberOfConnections + 1];
		for (int i = 0; i < numrows * numcols; i++) {
			numConnections[agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent]++;
		}
		String connectionPDFx = "n ";
		String connectionPDFy = "pdf ";
		String connectionCDFy = "cdf ";
		double previousConnectionCDF = 0;
		for (double i = 0; i < globalMaxNumberOfConnections; i += globalMaxNumberOfConnections / 10.0) {
			connectionPDFx += i + "-" + (i + globalMaxNumberOfConnections / 10.0) + " ";
			double currentYaxis = 0;
			for (int j = (int) Math.ceil(i); j < Math.ceil(i + globalMaxNumberOfConnections / 10.0); j++) {
				if (j < numConnections.length) {
					currentYaxis += ((double) numConnections[j]) / (numcols * numrows);
				}
			}
			connectionPDFy += currentYaxis + " ";
			previousConnectionCDF += currentYaxis;
			connectionCDFy += previousConnectionCDF + " ";
		}
		if (printToFile) {
			try {								// try to open the file of the computed file name
				outputStream = new PrintWriter(new FileOutputStream(fileName));
			} catch (FileNotFoundException e) {
				System.out.println("\terror opening the file " + fileName);
				System.exit(0);
			}
			outputStream.println(fileName);
			outputStream.println("connections");
			outputStream.println(connectionPDFx);
			outputStream.println(connectionPDFy);
			outputStream.println(connectionCDFy);
			outputStream.close();
		}
		return fileName + "\n" + connectionPDFx + "\n" + connectionPDFy + "\n" + connectionCDFy + "\n";
	} // End Print CDF
	
	// ** Print Matrices ***************************************************************************
	public void printMatrices(int matrixCounter) {									// printAll will send from 0 to 6
		PrintWriter outputStream = null;
		String fileName = ""; // , prefix = "O"+(matrixCounter!=0&&matrixCounter!=5?algorithmStep:"");
		int[][] currentMatrix;			// create reference
		switch (matrixCounter) {								// compute the postfix
		default:						// use the connection matrix as default
		case 0:
			fileName = connectionOutputFileName;
			currentMatrix = connectionMatrix;
			break;
		case 1:
			fileName = adjacencyOutputFileName;
			currentMatrix = adjacencyMatrix;
			break;
		case 2:
			fileName = "O" + algorithmStep + assignmentOutputFileName;
			currentMatrix = assignmentMatrix;
			break;
		case 3:
			fileName = "O" + algorithmStep + differenceMatrixOutputFileName;
			currentMatrix = differenceMatrix;
			break;
		case 4:
			fileName = "O" + algorithmStep + reachMatrixOutputFileName;
			currentMatrix = reachMatrix;
			break;
		case 5:
			fileName = "O" + algorithmStep + pathMatrixOutputFileName;
			currentMatrix = pathMatrix;
			break;
		case 10:
			fileName = IOConnectionMatrixFileName;
			currentMatrix = connectionMatrix;
			break;
		case 11:
			fileName = IOAssignmentMatrixFileName;
			currentMatrix = assignmentMatrix;
			break;
		case 12:
			fileName = IOTempAssignmentMatrixFileName1;
			currentMatrix = assignmentMatrix;
			break;
		case 13:
			fileName = IOTempAssignmentMatrixFileName2;
			currentMatrix = assignmentMatrix;
			break;
		case 14:
			fileName = IOTempConnectionMatrixFileName1;
			currentMatrix = connectionMatrix;
			break;
		case 15:
			fileName = IOTempAssignmentMatrixFileName3;
			currentMatrix = assignmentMatrix;
			break;
		case 16:
			fileName = IOTempAssignmentMatrixFileName4;
			currentMatrix = assignmentMatrix;
			break;
		}
		try {								// try to open the file of the computed file name
			outputStream = new PrintWriter(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("\terror opening the file " + fileName);
			System.exit(0);
		}
		for (int i = 0; i < numrows * numcols; i++) {
			if (currentMatrix == pathMatrix) {
				if (i == 0) {						// the first row of the path matrix is special
					outputStream.print("    |");
					for (int j = 0; j < numrows * numcols; j++)
						outputStream.printf("A%3d|", j);
					outputStream.println();
				}						// print out the rest of the path matrix with '|' in-between
				outputStream.printf("A%3d|", i);
				for (int j = 0; j < numrows * numcols; j++)
					outputStream.printf("%4d|", currentMatrix[i][j]);
			} else if (currentMatrix[0].length == maxID + 1)// matrixCounter==0||matrixCounter==2||matrixCounter==3||matrixCounter==10)
			{							// the matrices of 0, 1, and 3 are numrows*numcols by maxID+1
				for (int j = 0; j < maxID + 1; j++)
					outputStream.print(currentMatrix[i][j]);//todo if u want to add spaces
			} else if (currentMatrix[0].length == numrows * numcols)// matrixCounter==2||matrixCounter==5) // else simply print out the reach matrix
				for (int j = 0; j < numrows * numcols; j++)
					outputStream.print(currentMatrix[i][j]);
			if (matrixCounter == 0 || matrixCounter == 10)
				outputStream.print(" " + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent);
			if (matrixCounter == 2 || matrixCounter == 11)
				outputStream.print(" " + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent);
			outputStream.println();
		}
		outputStream.close();			// close file and update user
		if (vocal && matrixCounter < 10)
			System.out.println("\twrote to file: " + fileName);
	} // end print matrices
	
	public void printStats(PrintWriter outputStream) {
//		PrintWriter outputStream = null;
		String fileName = "April3Stats.txt";
//		try {								// try to open the file of the computed file name
//			outputStream = new PrintWriter(new FileOutputStream(fileName));
//		} catch (FileNotFoundException e) {
//			System.out.println("\terror opening the file " + fileName);
//			System.exit(0);
//		}
//		int maxAgentConnection = 0;
//		ArrayList<Integer> agentConnections = new ArrayList<Integer>();
//		for (int i = 0; i < numrows * numcols; i++) {
//			if (agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent > maxAgentConnection) {
//				maxAgentConnection = agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent;
//			}
//			agentConnections.add(agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent);
//		}
//		Collections.sort(agentConnections);
//		for (int i = 0; i <= maxAgentConnection; i++) {
//			outputStream.printf("%5d\t%5d\n", i, Collections.frequency(agentConnections, i));
//		}
		outputStream.println();
		outputStream.println();
		outputStream.println();
		int maxResourceConnection = 0;
		ArrayList<Integer> resourceConnections = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			int numResourceConnection = 0;
			for (int j = 0; j < numrows * numcols; j++) {
				numResourceConnection += connectionMatrix[j][i];
			}
			if (numResourceConnection > maxResourceConnection) {
				maxResourceConnection = numResourceConnection;
			}
			resourceConnections.add(numResourceConnection);
		}
		Collections.sort(resourceConnections);
		for (int i = 0; i <= maxResourceConnection; i++) {
			outputStream.printf("%5d\t%5d\n", i, Collections.frequency(resourceConnections, i));
		}
		outputStream.close();
//		System.out.println("\twrote to file: " + fileName);
	}

	// ** Print Grid and Resource Location *********************************************************
	public void printGridAndResourceLocation(String fileName) {
		PrintWriter outputStream = null;
		try {								// try to open the file of the computed file name
			outputStream = new PrintWriter(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("\terror opening the file " + fileName);
			System.exit(0);
		}
		outputStream.println("numagents: " + (numrows * numcols) + " numrows: " + numrows + " numcols: " + numcols + " N: " + N);
		for (int i = 0; i < N; i++) {
			outputStream.println(resources[i].row + " " + resources[i].column);
		}
		outputStream.close();			// close file and update user
		if (vocal && !fileName.equals(IOGridAndResourceLocationFileName))
			System.out.println("\twrote to file: " + fileName);
	} 	// end print Grid and Resource Location

	// ** Print a Connection Matrix with the Best Allocation Seeded ********************************
	public void printSeededConnectionMatrix(boolean improvement) {
		PrintWriter outputStream = null;
		try {								// try to open up an output stream in file
			outputStream = new PrintWriter(new FileOutputStream(IOSeededConnectionFileName));
		} catch (FileNotFoundException e) {
			System.out.println("\terror opening the file " + IOSeededConnectionFileName);
			System.exit(0);
		}
		int[][] idealConnectionMatrix = new int[numrows * numcols][N];
		// the ideal matrix is generated using N in case some resources has been killed earlier
		if (improvement) {
			int minNumberOfConnections = N / (numrows * numcols); // this division rounds down
			int numberOfAgentsThatNeed1More = N % (numrows * numcols);
			int intWalker = 0;
			for (int i = 0; i < numberOfAgentsThatNeed1More; i++) {
				for (int j = 0; j < minNumberOfConnections + 1; j++) {							// allot the connection matrix to those that uses an extra one
					idealConnectionMatrix[i][intWalker] = 1;
					intWalker++;
				}
			}
			for (int i = numberOfAgentsThatNeed1More; i < numrows * numcols; i++) {
				for (int j = 0; j < minNumberOfConnections; j++) {							// allot the connection matrix to those that does not use an extra one
					idealConnectionMatrix[i][intWalker] = 1;
					intWalker++;
				}
			}
		} else {
			for (int i = 0; i < 1; i++) {
				for (int j = 0; j < maxID + 1; j++) {						// allot connections by giving one connection to everything
					idealConnectionMatrix[i][j] = 1;
				}
			}
		}
		for (int i = 0; i < numrows * numcols; i++) {								// go through the ideal connection matrix
			for (int j = 0; j < N; j++) {
				if (idealConnectionMatrix[i][j] == 0) {						// add conections based on DENSITY
					if (Math.random() < DENSITY)
						idealConnectionMatrix[i][j] = 1;
				}
			}
		}
		idealConnectionMatrix = scramble(idealConnectionMatrix, true); // scramble the rows
		idealConnectionMatrix = scramble(idealConnectionMatrix, false); // scramble the cols
		for (int i = 0; i < numrows * numcols; i++) {								// print the ideal connection matrix
			for (int j = 0; j < N; j++)
				outputStream.print(idealConnectionMatrix[i][j]);
			outputStream.println();
		}
		outputStream.close();
		if (vocal)			// maybe update user
			System.out.println("\twrote to file: " + IOSeededConnectionFileName);
	} // end print ideal matrix

	// ** Scramble using the Fisher-Yates Shuffle ***************************************************
	public int[][] scramble(int[][] original, boolean rowScramble) {
		int[][] scramble = new int[original.length][original[0].length];
		// compute size to determine how the 2D array should be scrambled
		int size = rowScramble ? original.length : original[0].length;
		int[] random = new int[size]; 	// make a new array to store the scrambled ints
		// int[] checkList = new int[size]; // create a array of ints of size of scramble
		// int randomRange = size; // initialize the range to size
		// int randomInt, arrayWalker;
		for (int i = 0; i < numrows * numcols; i++) {
			random[i] = i;
		}
		for (int i = 0; i < numrows * numcols; i++) {
			int r = i + (int) (Math.random() * (numrows * numcols - i));
			int swap = random[r];
			random[r] = random[i];
			random[i] = swap;
		}
		// for (int i=0; i<size; i++) // for every size of scramble
		// {
		// randomInt = (int)Math.floor(Math.random()*randomRange--); // take a new random int
		// arrayWalker = 0; // arrayWalker is use to walk through checkList
		// while (randomInt>0||checkList[arrayWalker]==1) // stop if intWalker==randomInt
		// if (checkList[arrayWalker++]==0)
		// randomInt--; // only count the integer walker if the checkList not already counted
		// checkList[arrayWalker] = 1;
		// random[i] = arrayWalker; // set random array
		// }
		for (int i = 0; i < size; i++)
			// flip elements of size
			for (int j = 0; j < size; j++)
				if (i == random[j]) {
					if (rowScramble)	// seems inverse because this is the amount of element to flip
						// for every move
						for (int k = 0; k < original[0].length; k++)
							scramble[i][k] = original[j][k];
					else
						for (int k = 0; k < original.length; k++)
							scramble[k][i] = original[k][j];
				}
		return scramble;				// return scrambled int[][]
	} // end matrix scramble

	// ** Use Connection Input **********************************************************************
	public void useConnectionInput(String fileName) {
		try {
			Scanner inputFile = new Scanner(new File(fileName));
			int numberOfAgents = 0;
			String stringWalker = "";
			while (inputFile.hasNextLine()) {							// keep reading lines to compute the number of agents
				numberOfAgents++;
				stringWalker = inputFile.nextLine();
			}							// since a matrix with more rows is preferred,
			// (paper is portrait as opposed to landscape)
			// (easier to scroll down that it is to scroll across due to page down button)
			// take the ceiling of the number of agents
			// and then keep increasing the number of rows until it is divisible
			// HOWEVER, landscape looks better on a computer screen
			inputFile.close();			// close file to prevent corruption
			if ((int) Math.ceil(Math.sqrt(numberOfAgents)) == 0 || stringWalker.length() == 0) {							// check if the number of rows is valid
				System.out.println("\terror: invalid connection matrix due to number of agents. Returning...");
				return;			// return since the number of columns not yet modified
			}							// --> errors later down the road
			numcols = (int) Math.ceil(Math.sqrt(numberOfAgents));
			while (numberOfAgents % numcols != 0)
				numcols++;
			numrows = numberOfAgents / numcols; // compute numrows
			N = 0;
			while (N < stringWalker.length() && stringWalker.charAt(N) != ' ')
				N++;
			maxID = N - 1;

			inputFile = new Scanner(new File(fileName));
			connectionMatrix = new int[numberOfAgents][N]; // reset the connection and assignment matrix
			assignmentMatrix = new int[numberOfAgents][N];
			for (int i = 0; i < numberOfAgents; i++) {
				stringWalker = inputFile.nextLine();
				for (int j = 0; j < maxID + 1; j++)
					// update the connection matrix
					connectionMatrix[i][j] = Integer.parseInt(stringWalker.substring(j, j + 1));
			}
			inputFile.close();
			agents = new Agent[numrows][numcols];
			resources = new Resource[2 * N];
			if (N == 0)				// if initially no people
				resources = new Resource[10]; // give a finite array size
			for (int i = 0; i < numrows * numcols; i++) {
				agents[i / numcols][i % numcols] = new Agent(); // create a new district for every row and column
				for (int j = 0; j < maxID + 1; j++) {
					if (connectionMatrix[i][j] == 1) {
						resources[j] = new Resource(j, i * numcols, i % numcols);
						if (agents[i / numcols][i % numcols].firstResource != null) // modify district head if no head
							agents[i / numcols][i % numcols].firstResource.previousResource = resources[j];
						resources[j].nextResource = agents[i / numcols][i % numcols].firstResource; // make connection from new resource to head
						agents[i / numcols][i % numcols].firstResource = resources[j]; // declare a new head
						agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent = agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent + 1;
					}					// increase number of resource counter
				}
			}
			for (int i = 0; i < maxID + 1; i++) {
				double doubleRow = 0, doubleColumn = 0;
				int numberOfAgentConnectedToResource = 0;
				for (int j = 0; j < numrows * numcols; j++) {
					if (connectionMatrix[j][i] == 1) {					// use the average row and column of connected agents
						doubleRow += j / numcols;
						doubleColumn += j % numcols;
						numberOfAgentConnectedToResource++;
					}
				}						// then add some noise
				doubleRow = doubleRow / numberOfAgentConnectedToResource + Math.random() - 0.5;
				doubleColumn = doubleColumn / numberOfAgentConnectedToResource + Math.random() - 0.5;
				if (resourceLocationBeforeDisturbance != null && resourceLocationBeforeDisturbance.length == maxID)
					resources[i] = new Resource(i, resourceLocationBeforeDisturbance[i][0], resourceLocationBeforeDisturbance[i][1]);
				else if (numberOfAgentConnectedToResource > 0)
					resources[i] = new Resource(i, doubleRow, doubleColumn);
				else
					// create a new resource in case it is connected to nothing
					resources[i] = new Resource(i, -PERIMETER, -PERIMETER);
			}
			if (fileName.equals(IOAgentNEdgeConnectionFileName))
				useGridAndResourceLocation(gridAndResourceLocationInputFileName);
			else
				useGridAndResourceLocation(fileName.replaceFirst("ConnectionMatrix", "GridAndResourceLocation").replaceFirst("Connection",
						"GridAndResourceLocation"));
			N = maxID + 1;
			deadPool = new Stack<Integer>(); // reset the stack of dead resources
			if (vocal)
				System.out.println("\tconnections adjusted using " + fileName);
			// DOT_LAYOUT = 2; // have the option to use neato after adjusting layout

			// adjust assignment matrix to use
			int numberOfConnections;
			int counterForAssignment;	// if buggy then change variable name
			for (int i = 0; i < maxID + 1; i++) // for loop to assign every resource
			{
				numberOfConnections = 0;
				for (int j = 0; j < numcols * numrows; j++)
					// for loop to count agents
					if (connectionMatrix[j][i] == 1)
						numberOfConnections++;
				counterForAssignment = (int) Math.floor(Math.random() * (numberOfConnections));
				for (int j = 0; j < numcols * numrows; j++)
					// for loop to count down and assign resource
					if (connectionMatrix[j][i] == 1) {
						if (counterForAssignment == 0) {
							assignmentMatrix[j][i] = 1;
							counterForAssignment--;
							break;
						} else
							counterForAssignment--;
					}
			}
			useAssignmentMatrix();		// use assignment matrix
			// commented below because not sure what is is used for
			// if (testing) // if this method is not a submethod of another
			// {
				algorithmStep = 5 * stepScale; // reset algorithm step to print in the proper file name
				randomNumberGenerator = new Random(1);	// Random number generator
			// updateMinMaxFairnessIndexes();
				testResults = new ArrayList<double[][]>();
				testResultsElement = new double[1][4];
				testResultsElement[0][0] = algorithmStep;
				testResultsElement[0][2] = fairnessIndex(1, null, true);
				testResultsElement[0][3] = fairnessIndex(2, null, true);
				testResults.add(testResultsElement);
			// }
		}								// which will update matrix from data,
		catch (IOException e)			// which will update the XOR matrix
		{								// if cannot open file
			System.out.println("\tuseConnectionInput error using the file " + fileName);
			System.exit(0);
		}
	} // end use connection input

	// ** Use Assignment Input **********************************************************************
	public void useAssignmentInput(String fileName) {
		try {
			Scanner inputFile = new Scanner(new File(fileName));
			int numberOfAgents = 0;		// integer to count the number of agents
			String stringWalker = "";	// dummy string to grab lines of input file
			while (inputFile.hasNextLine()) {
				numberOfAgents++;
				stringWalker = inputFile.nextLine();
			}
			inputFile.close();			// close file since finished using
			int NassignmentMatrix = 0;
			while (NassignmentMatrix < stringWalker.length() && stringWalker.charAt(NassignmentMatrix) != ' ')
				NassignmentMatrix++;
			if (numberOfAgents != numrows * numcols || N != NassignmentMatrix) {							// check if the number of rows is valid
				System.out.println("\terror: the assignment matrix input (" + numberOfAgents + ", " + NassignmentMatrix
						+ ") does not match the dimension of the current connection matrix (" + connectionMatrix.length + ", "
						+ connectionMatrix[0].length + "). Returning...");
				return; 				// return since the number of rows has not been modified
			}							// --> errors later down the road
			inputFile = new Scanner(new File(fileName));
			int[][] newAssignmentMatrix = new int[numberOfAgents][N];
			for (int i = 0; i < newAssignmentMatrix.length; i++) {
				for (int j = 0; j < newAssignmentMatrix[0].length; j++) {
					newAssignmentMatrix[i][j] = -1;
				}
			}
			for (int i = 0; i < numberOfAgents; i++) {
				stringWalker = inputFile.nextLine();
				for (int j = 0; j < N; j++) { // update the assignment matrix and check if it is valid
					newAssignmentMatrix[i][j] = Integer.parseInt(stringWalker.substring(j, j + 1));
					if (connectionMatrix[i][j] == 0 && newAssignmentMatrix[i][j] == 1) {
						System.out.println("\tthis input assignment matrix " + fileName
								+ " is not suitable for the current connection matrix");
						System.out.println("\tCheck (" + i + ", " + j + ")");
						System.out.println("Con matrix:");
						for (int k = 0; k < numrows * numcols; k++) {
							for (int l = 0; l < N; l++) {
								System.out.print(connectionMatrix[k][l]);
							}
							System.out.println();
						}
						System.out.println("\n\n current assign matrix:");
						for (int k = 0; k < numrows * numcols; k++) {
							for (int l = 0; l < N; l++) {
								System.out.print(newAssignmentMatrix[k][l]);
							}
							System.out.println();
						}
						inputFile.close();
						return;
					}
				}
			}
			inputFile.close();
			assignmentMatrix = newAssignmentMatrix;
			if (vocal)
				System.out.println("\tassignments adjusted using " + fileName);
			useAssignmentMatrix();		// use assignment matrix
		}								// which will update matrix from data,
		catch (IOException e)			// which will update the XOR matrix
		{								// if cannot open file
			System.out.println("\terror using the file " + fileName);
			System.exit(0);
		}
	}	// end use assignment input

	// ** Use Assignment Matrix *********************************************************************
	public void useAssignmentMatrix()			// update data structure from matrix
	{
		for (int i = 0; i < numrows * numcols; i++) {									// for every agent and resource
			agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent = 0;
			for (int j = 0; j < N; j++) {
				if (assignmentMatrix[i][j] > 0) {						// update the resource's assigned agent
					resources[j].assignedRow = i / numcols;
					resources[j].assignedColumn = i % numcols;
					agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent++;
				}						// calculate and set numberOfResourcesAssignedToAgent
			}
			// System.out.println("\t\t\ta" + i + "'s number of assignedResources = "
			// + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent);
		}
		updateMatricesFromData();		// update XOR path, and reach matrix after updating assignment matrix
	} // end use assignment matrix

	public boolean useGridAndResourceLocation(String fileName) {
		try {
			Scanner inputFile = new Scanner(new File(fileName));
			String[] gridPropertiesArray;
			if (inputFile.hasNextLine())
				gridPropertiesArray = inputFile.nextLine().split(" ");
			else
			{
				System.out.println("\terror: "+fileName+" was empty. Returning...");
				inputFile.close();
				return false; 				// return since the number of rows has not been modified
			}
			int resourceCounter = 0;
			while (inputFile.hasNextLine()) {
				resourceCounter++;
				inputFile.nextLine(); // dummy string to grab lines of input file
			}
			inputFile.close();			// close file since finished using
			if (resourceCounter != N) {							// check if the number of rows is valid
				if (vocal)
				System.out
						.println("\terror: numberOfResources did not match. file: '" + resourceCounter + "' N: '" + N + "'. Returning...");
				return false; 				// return since the number of rows has not been modified
			}							// --> errors later down the road
			boolean numAgentsMatches = false;
			for (int i = 0; i < gridPropertiesArray.length; i++)
			{
				String s = gridPropertiesArray[i];
				if (s.equalsIgnoreCase("numagents:"))
				{
					if (numrows*numcols==Integer.parseInt(gridPropertiesArray[i+1]))
						numAgentsMatches = true;
					else {
						System.out.println("\terror: numAgents did not match. Returning...");
						return false; 				// return since the number of rows has not been modified
					}
				}
				if (numAgentsMatches && s.equalsIgnoreCase("numrows"))
				{
					numrows = Integer.parseInt(gridPropertiesArray[i+1]);
				}
				if (numAgentsMatches && s.equalsIgnoreCase("numcols"))
				{
					numcols = Integer.parseInt(gridPropertiesArray[i+1]);
				}
			}
			inputFile = new Scanner(new File(fileName));
			inputFile.nextLine();
			resourceCounter = 0;
			while (inputFile.hasNextLine()) {
				gridPropertiesArray = inputFile.nextLine().split(" ");
				resources[resourceCounter].row = Double.parseDouble(gridPropertiesArray[0]); resources[resourceCounter].column = Double.parseDouble(gridPropertiesArray[1]);
				resourceCounter++;
			}
			inputFile.close();			// close file since finished using
			if (vocal)
				System.out.println("\tassignments adjusted using " + fileName);
			useAssignmentMatrix();		// use assignment matrix
		}								// which will update matrix from data,
		catch (IOException e)			// which will update the XOR matrix
		{								// if cannot open file
			if (vocal)
				System.out.println("\terror using grid file " + fileName);
			return false;
		}
		return true;
	}	// end useGridAndResourceLocation

	// ** Debug *************************************************************************************
	public void debug() {
		// 1st line: numrows, numcols, N, and maxID
		// 2nd line: CONNECTIVITY_RANGE, DENSITY, PARAMETER, SQUARE_RANGE
		// 3rd line: locationAlgorithm, assignmentAlgorithm, RADIUS
		// 4th line: DOT_LAYOUT, magnification, DOT_SCALE, numberOfAgentsWithSameRank
		System.out.println("\tnumrows = " + numrows + ", numcols = " + numcols + ", N = " + N + ", maxID = " + maxID + ", numEdges = "
				+ numEdges);
		System.out.println("\tCONNECTIVITY_RANGE = " + CONNECTIVITY_RANGE + ", DENSITY = " + DENSITY + ", PERIMETER = "
				+ PERIMETER + ", SQUARE_RANGE = " + SQUARE_RANGE);
		System.out.println("\tTOP_PERIMETER = "+TOP_PERIMETER+", BOTTOM_PERIMETER = "+BOTTOM_PERIMETER+", LEFT_PERIMETER = "+LEFT_PERIMETER+", RIGHT_PERIMETER = "+RIGHT_PERIMETER);
		System.out.println("\tdensityVariance = " + densityVariance + ", agentBalanceFactor = " + agentBalanceFactor + ", pGeometric = "
				+ pGeometric);
		System.out.println("\tlocationAlgorithm = " + locationAlgorithm
 + ", assignmentAlgorithm = " + assignmentAlgorithm + ", RADIUS = "
				+ RADIUS);
		System.out.print("\talgorithmBidWhoOrderAndExecution = ");
		toString(algorithmBidWhoOrderAndExecution);
		System.out.print("\talgorithmWhoWinsOrderAndExecution = ");
		toString(algorithmWhoWinsOrderAndExecution);
		System.out.print("\ttestTopographyArray = ");
		toString(testTopographyArray);
		System.out.println("\tvocal = " + vocal + ", printDebug = " + showAlgorithmVisual + ", showDebug = " + showDebug);
		System.out.println("\tprintASGLabels = " + printASGLabels + ", DOT_LAYOUT = " + DOT_LAYOUT + ", DOT_SCALE = " + DOT_SCALE
				+ ", numberOfAgentsWithSameRank = " + numberOfAgentsWithSameRank);
		System.out.println("\tbatchScale = " + batchScale + ", stepScale = " + stepScale + ", algorithmStep = " + algorithmStep);
		System.out.println("\testimatedNumberOfConnections = " + printNumberOfConnections() + ", actualNumberOfConnections = "
				+ actualNumberOfConnections() + ", actualNumberOfAssignments = " + actualNumberOfAssignments());
		System.out.println("\tnumberOfPhases = " + numberOfPhases + ", biddingAggressiveness = " + biddingAggressiveness
				+ ", standardDeviation = " + standardDeviation);
		System.out.println("\tcascadedDecentralizedIsDone = " + cascadedDecentralizedIsDone + ", algorithm = " + algorithm);
		System.out.println("\tceilingLimit = " + ceilingLimit);
		System.out.println();		
		
		// fairnessIndex(0, null, true);
		// small world distance
		// adjacencyMatrix is the first product
		// int[][] connectionMatrixStack = adjacencyMatrix;
		// int stackCounter = 1;
		// while (!checkSmallWorld(connectionMatrixStack)) // while there exists a zero in the matrix
		// {
		// stackCounter++; // increase counter and do another multiply
		// if (stackCounter > numrows * numcols)
		// break;
		// connectionMatrixStack = matrixMultiply(connectionMatrixStack, adjacencyMatrix);
		// } // inform user about the small world status
		// System.out.println("\tsmall world distance = " + stackCounter);
		boolean tempShowDebug = showDebug;
		showDebug = false;
		System.out.println("\tactualNumberOfConnections = " + actualNumberOfConnections());
		System.out.println("\tscaledFairnessIndex = " + scaledAgentFairnessIndexFromScratch());
		if (showDebugLevel2) {
			System.out.println("\tnetworkScaledAgentFairnessIndex = " + networkScaledAgentFairnessIndexFromScratch());
			System.out.println("\tconnectionFairnessIndex[agent] = " + connectionFairnessIndexAgent());
			System.out.println("\tconnectionFairnessIndex[resource] = " + connectionFairnessIndexResource());
			System.out.println("\tdiameter = " + diameter(adjacencyMatrix, 1));
			System.out.println("\tsubgraph = " + numSubgraphs(adjacencyMatrix));
			System.out.println("\tclusterness = " + clusterness());
			System.out.println("\toverlap = " + overlap());
			System.out.println("\taverageNumberOfResourcesPerAdjacency = " + averageNumberOfResourcesPerAdjacency());
			scaledNetworkAgentFairnessIndex();
		}
		showDebug = tempShowDebug;
	} // end debug

	// ** Number of Edges **************************************************************************
	public int actualNumberOfConnections() {
		int returnValue = 0;
		for (int i = 0; i < numrows * numcols; i++)
			for (int j = 0; j < maxID + 1; j++)
				if (connectionMatrix[i][j] > 0)
					returnValue++;
		return returnValue;
	}	// end numEdges
	
	public int numberOfOnes(int[][] matrix) {
		int returnValue = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] > 0) {
					returnValue++;
				}
			}
		}
		return returnValue;
	}
	
	public int actualNumberOfAssignments() {
		int returnValue = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			returnValue += agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
		}
		return returnValue;
	}

	// ** Scaled Agent Fairness Index **************************************************************
	public double scaledAgentFairnessIndex() {

		int numagents = numrows * numcols;
		double maxNumberOfAssignedResources = 0; // initalize each component of each fairness index to 0
		double numerator = 0, denominator = 0;
		double fairnessIndex = 0;
		// in general, the minimum fairness index is 1/numagents
		// unless the number of agents is equal to 0
		// compute fairness index
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {							// sum the numerator and denominator for each agent
				numerator += agents[i][j].numberOfResourcesAssignedToAgent;
				denominator += Math.pow(agents[i][j].numberOfResourcesAssignedToAgent, 2);
				// compute the maximum number of agents to later print out
				// a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
				if (agents[i][j].numberOfResourcesAssignedToAgent > maxNumberOfAssignedResources)
					maxNumberOfAssignedResources = agents[i][j].numberOfResourcesAssignedToAgent;
			}
		}
		numerator = numerator * numerator; // the numerator is squared
		denominator = denominator * numagents; // the denominator is multiplied by the number of agents
		fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
		// System.out.println("\t\tminScaledFI = " + minScaledFI + ", maxScaledFI = " + maxScaledFI + ", FI = " + fairnessIndex
		// + ", scaledFI = "
		// + (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI));
		return minScaledFI >= maxScaledFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI);
	}

	// ** Scaled Agent Fairness Index **************************************************************
	public double scaledNetworkAgentFairnessIndex() {

		int numagents = numrows * numcols;
		double maxNumberOfAssignedResources = 0; // initalize each component of each fairness index to 0
		double numerator = 0, denominator = 0;
		double fairnessIndex = 0;
		// in general, the minimum fairness index is 1/numagents
		// unless the number of agents is equal to 0
		// compute fairness index
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {							// sum the numerator and denominator for each agent
				numerator += agents[i][j].numberOfResourcesAssignedToAgent;
				denominator += Math.pow(agents[i][j].numberOfResourcesAssignedToAgent, 2);
				// compute the maximum number of agents to later print out
				// a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
				if (agents[i][j].numberOfResourcesAssignedToAgent > maxNumberOfAssignedResources)
					maxNumberOfAssignedResources = agents[i][j].numberOfResourcesAssignedToAgent;
			}
		}
		numerator = numerator * numerator; // the numerator is squared
		denominator = denominator * numagents; // the denominator is multiplied by the number of agents
		fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
		// System.out.println("\t\tminScaledFI = " + minScaledFI + ", maxScaledFI = " + maxScaledFI + ", FI = " + fairnessIndex
		// + ", scaledFI = "
		// + (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI));
		double scaledNetworkFI = (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI);
		return minNetworkFI >= maxNetworkFI || fairnessIndex == 1 ? 1 : (scaledNetworkFI - minNetworkFI) / (maxNetworkFI - minNetworkFI);
	}

	// ** Scaled Agent Fairness Index **************************************************************
	public double scaledAgentFairnessIndexFromScratch() {

		int numagents = numrows * numcols;
		minScaledFI = numagents > 0 ? (double) 1 / (numagents) : 0;
		double maxScaledFINumerator = ((numagents - N % numagents) * (N / numagents) + (double) (N % numagents) * (N / numagents + 1));
		maxScaledFINumerator = maxScaledFINumerator * maxScaledFINumerator;
		double maxScaledFIDenominator = (numagents * ((numagents - N % numagents) * (N / numagents) * (N / numagents) + (N % numagents) * (N / numagents + 1) * (N / numagents + 1)));
		maxScaledFI = (N == 0 || numagents == 0) ? 1 : maxScaledFINumerator / maxScaledFIDenominator;

		double maxNumberOfAssignedResources = 0; // initalize each component of each fairness index to 0
		double numerator = 0, denominator = 0;
		double fairnessIndex = 0;
		// in general, the minimum fairness index is 1/numagents
		// unless the number of agents is equal to 0
		// compute fairness index
//		for (int i = 0; i<numrows*numcols; i++) {
//			System.out.print(agents[i/numcols][i%numcols].numberOfResourcesAssignedToAgent + ", ");
//		}
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {							// sum the numerator and denominator for each agent
				numerator += agents[i][j].numberOfResourcesAssignedToAgent;
				denominator += Math.pow(agents[i][j].numberOfResourcesAssignedToAgent, 2);
				// compute the maximum number of agents to later print out
				// a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
				if (agents[i][j].numberOfResourcesAssignedToAgent > maxNumberOfAssignedResources)
					maxNumberOfAssignedResources = agents[i][j].numberOfResourcesAssignedToAgent;
			}
		}
		numerator = numerator * numerator; // the numerator is squared
		denominator = denominator * numagents; // the denominator is multiplied by the number of agents
		fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
//		 System.out.println("\t\tminScaledFI = " + minScaledFI + ", maxScaledFI = " + maxScaledFI + ", FI = " + fairnessIndex
//		 + ", scaledFI = "
//		 + (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI));
		return minScaledFI >= maxScaledFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI);
	}

	// ** Scaled Agent Fairness Index **************************************************************
	public double networkScaledAgentFairnessIndexFromScratch() {
//		if (true) {
//			println("assignment before");
//			for (int i = 0; i < assignmentMatrix.length; i++) {
//				for (int j = 0; j < assignmentMatrix[0].length; j++) {
//					print(assignmentMatrix[i][j]);
//				}
//				println();
//			}
//		}
		printMatrices(15);
		assignmentMatrix = new int[numrows*numcols][N];
		// System.out.println("should be empty");
		// toString(assignmentMatrix);
		// System.out.println();
		boolean[] isResourceConnected = new boolean[N];
		for (int i = 0; i<N; i++) {
			isResourceConnected[i] = false;
		}
		boolean tempVocal = vocal;
		vocal = false;
		worst();
		useAssignmentMatrix();
		// toString(assignmentMatrix);
		double minScaledNetworkFI = scaledAgentFairnessIndexFromScratch();
		// System.out.println("minscalednetworkfi = " + scaledAgentFairnessIndex());
		while (batchCascadedImprovementPath(true) != 0);
		double maxScaledNetworkFI = scaledAgentFairnessIndexFromScratch();
		// toString(assignmentMatrix);
		// System.out.println("maxscalednetworkfi = " + scaledAgentFairnessIndex());
		useAssignmentInput(IOTempAssignmentMatrixFileName3);
		vocal = tempVocal;
		return (scaledAgentFairnessIndexFromScratch() - minScaledNetworkFI) / (maxScaledNetworkFI - minScaledNetworkFI);
		// int numagents = numrows * numcols;
		// minScaledFI = numagents > 0 ? (double) 1 / (numagents) : 0;
		// double maxScaledFINumerator = ((numagents - N % numagents) * (N / numagents) + (double) (N % numagents) * (N / numagents + 1));
		// maxScaledFINumerator = maxScaledFINumerator * maxScaledFINumerator;
		// double maxScaledFIDenominator = (numagents * ((numagents - N % numagents) * (N / numagents) * (N / numagents) + (N % numagents)
		// * (N / numagents + 1) * (N / numagents + 1)));
		// maxScaledFI = (N == 0 || numagents == 0) ? 1 : maxScaledFINumerator / maxScaledFIDenominator;
		//
		// double maxNumberOfAssignedResources = 0; // initalize each component of each fairness index to 0
		// double numerator = 0, denominator = 0;
		// double fairnessIndex = 0;
		// // in general, the minimum fairness index is 1/numagents
		// // unless the number of agents is equal to 0
		//
		// // compute fairness index
		// for (int i = 0; i < numrows; i++) {
		// for (int j = 0; j < numcols; j++) { // sum the numerator and denominator for each agent
		// numerator += agents[i][j].numberOfResourcesAssignedToAgent;
		// denominator += Math.pow(agents[i][j].numberOfResourcesAssignedToAgent, 2);
		// // compute the maximum number of agents to later print out
		// // a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
		// if (agents[i][j].numberOfResourcesAssignedToAgent > maxNumberOfAssignedResources)
		// maxNumberOfAssignedResources = agents[i][j].numberOfResourcesAssignedToAgent;
		// }
		// }
		// numerator = numerator * numerator; // the numerator is squared
		// denominator = denominator * numagents; // the denominator is multiplied by the number of agents
		// fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
		// return minScaledFI >= maxScaledFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI);
	}

	// ** Compute Connection Fairness Index for Agents *********************************************
	public double connectionFairnessIndexAgent() {								// 0 is nothing, 1 is network, 2 is scaled, 3 is regular
		int numagents = numrows * numcols;
		int numberOfEdges = actualNumberOfConnections();
		minScaledFI = numagents > 0 ? 1.0 / numagents : 0;
		double maxScaledFINumerator = ((numagents - numberOfEdges % numagents) * (numberOfEdges / numagents) + (double) (numberOfEdges % numagents)
				* (numberOfEdges / numagents + 1));
		maxScaledFINumerator = maxScaledFINumerator * maxScaledFINumerator;
		double maxScaledFIDenominator = (numagents * ((numagents - numberOfEdges % numagents) * (numberOfEdges / numagents)
				* (numberOfEdges / numagents) + (numberOfEdges % numagents) * (numberOfEdges / numagents + 1)
				* (numberOfEdges / numagents + 1)));
		maxScaledFI = (numberOfEdges == 0 || numagents == 0) ? 1 : maxScaledFINumerator / maxScaledFIDenominator;

		// initalize each component of each fairness index to 0
		double numerator = 0, denominator = 0;
		double fairnessIndex = 0;
		// in general, the minimum fairness index is 1/numagents
		// unless the number of agents is equal to 0
		// compute connection fairness index
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {							// sum the numerator and denominator for each agent
				numerator += agents[i][j].numberOfResourcesConnectedToAgent;
				denominator += Math.pow(agents[i][j].numberOfResourcesConnectedToAgent, 2);
				// compute the maximum number of agents to later print out
				// a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
			}
		}
		numerator = numerator * numerator; // the numerator is squared
		denominator = denominator * numagents; // the denominator is multiplied by the number of agents
		fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
		// System.out.println("minScaledFI = " + minScaledFI + " and maxScaledFI = " + maxScaledFI + " and scaledFI = " + fairnessIndex);
		// compute the scaled fairness index relative to the fairness index
		// unless something weird has occurred,
		// the scaled fairness index is (FI-minFI)/(maxFI-minFI);
		return minScaledFI >= maxScaledFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI);
	} // end Connection Fairness Index for Agents

	// ** Compute Connection Fairness Index for Resources ******************************************
	public double connectionFairnessIndexResource() {								// 0 is nothing, 1 is network, 2 is scaled, 3 is regular
		int numberOfEdges = actualNumberOfConnections();
		minScaledFI = N > 0 ? 1.0 / N : 0;
		double maxScaledFINumerator = (double) (N - numberOfEdges % N) * (numberOfEdges / N) + (double) (numberOfEdges % N)
				* (numberOfEdges / N + 1);
		maxScaledFINumerator = maxScaledFINumerator * maxScaledFINumerator;
		double maxScaledFIDenominator = N
				* ((N - numberOfEdges % N) * (numberOfEdges / N) * (numberOfEdges / N) + (numberOfEdges % N) * (numberOfEdges / N + 1)
						* (numberOfEdges / N + 1));
		maxScaledFI = (numberOfEdges == 0 || N == 0) ? 1 : maxScaledFINumerator / maxScaledFIDenominator;
		double numerator = 0, denominator = 0;
		double fairnessIndex = 0;
		for (int i = 0; i < N; i++) {
			int numberOfAgentsConnectedToResource = 0;
			for (int j = 0; j < numrows * numcols; j++) {
				numberOfAgentsConnectedToResource += connectionMatrix[j][i];
			}
			numerator += numberOfAgentsConnectedToResource;
			denominator += Math.pow(numberOfAgentsConnectedToResource, 2);
		}
		numerator = numerator * numerator; // the numerator is squared
		denominator = denominator * N; // the denominator is multiplied by the number of agents
		fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
		// System.out.println("minScaledFI = " + minScaledFI + " and maxScaledFI = " + maxScaledFI + " and scaledFI = " + fairnessIndex);
		return minScaledFI >= maxScaledFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minScaledFI) / (maxScaledFI - minScaledFI);
	} 	// end Connection Fairness Index for Resources

	// ** Diameter **********************************************************************************
	public int diameter(int[][] adjacencyMatrixStack, int recursionCounter) {									// catch the current stack and initially 0
		if (recursionCounter > numrows * numcols + 100) // if got to this point, then check if recursion should end
			return 0;
		int[][] adjacencyMatrixProduct = new int[numrows * numcols][numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				adjacencyMatrixProduct[i][j] = adjacencyMatrixStack[i][j];
			}
		}
		// Debug Information
		// if (recursionCounter == 1) {
		// System.out.println("matrix at recursion counter = 0");
		// for (int i = 0; i < numrows * numcols; i++) {
		// for (int j = 0; j < numrows * numcols; j++) {
		// System.out.print(adjacencyMatrix[i][j]);
		// }
		// System.out.println();
		// }
		// System.out.println();
		// System.out.println("stack at recursion counter = 0");
		// for (int i = 0; i < numrows * numcols; i++) {
		// for (int j = 0; j < numrows * numcols; j++) {
		// System.out.print(adjacencyMatrixStack[i][j]);
		// }
		// System.out.println();
		// }
		// System.out.println();
		// System.out.println("product at recursion counter = 0");
		// for (int i = 0; i < numrows * numcols; i++) {
		// for (int j = 0; j < numrows * numcols; j++) {
		// System.out.print(adjacencyMatrixProduct[i][j]);
		// }
		// System.out.println();
		// }
		// System.out.println();
		// }
		boolean adjacencyMatrixProductChanged = false;
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				for (int k = 0; k < numrows * numcols; k++) {						// just use the max of 1
					if (adjacencyMatrixStack[i][k] * adjacencyMatrix[k][j] == 1 && adjacencyMatrixProduct[i][j] == 0)  // COULD THIS BE A BUG?
					{
						adjacencyMatrixProductChanged = true;
						adjacencyMatrixProduct[i][j] = 1;
						// System.out.println("at step " + recursionCounter + ", a" + i + " can now reach a" + j + " through a" + k);
					}
					// reachMatrixProduct[i][j] = Math.max(reachMatrixStack[i][j], reachMatrixStack[i][k]*reachMatrix[k][j]);
				}
			}
		}
		// Debug Information
		// System.out.println("at recursion counter = " + recursionCounter);
		// for (int i = 0; i < numrows * numcols; i++) {
		// for (int j = 0; j < numrows * numcols; j++) {
		// System.out.print(adjacencyMatrixProduct[i][j]);
		// }
		// System.out.println();
		// }
		// System.out.println();
		// System.out.println();
		// System.out.println();
		if (adjacencyMatrixProductChanged)
			return diameter(adjacencyMatrixProduct, recursionCounter + 1);
		return recursionCounter;
	} // end diameter

	// ** Number of Subgraphs **********************************************************************
	public int numSubgraphs(int[][] adjacencyMatrixStack) {									// catch the current stack and initially 0
		int[][] adjacencyMatrixProduct = new int[numrows * numcols][numrows * numcols];
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				adjacencyMatrixProduct[i][j] = adjacencyMatrixStack[i][j];
			}
		}
		boolean adjacencyMatrixProductChanged = false;
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				for (int k = 0; k < numrows * numcols; k++) {						// just use the max of 1
					if (adjacencyMatrixStack[i][k] * adjacencyMatrix[k][j] == 1 && adjacencyMatrixProduct[i][j] == 0)  // COULD THIS BE A BUG?
					{
						adjacencyMatrixProductChanged = true;
						adjacencyMatrixProduct[i][j] = 1;
					}
				}
			}
		}
		if (adjacencyMatrixProductChanged)
			return numSubgraphs(adjacencyMatrixProduct);
		HashSet<String> uniqueCluster = new HashSet<String>();
		for (int i = 0; i < numrows * numcols; i++) {
			String cluster = "";
			for (int j = 0; j < numrows * numcols; j++) {
				cluster += adjacencyMatrixProduct[i][j];
			}
			uniqueCluster.add(cluster);
		}
		return uniqueCluster.size();
	} // end diameter

	// ** Print the Number of Connections ***********************************************************
	public double printNumberOfConnections() {
		int actualNumberOfConnections = 0; // initialize number of connections to zero
		double estimatedNumberOfConnections = 0;
		for (int i = 0; i < numrows * numcols; i++) {								// go through the connection matrix and add up the number of connections
			for (int j = 0; j < maxID + 1; j++)
				actualNumberOfConnections += connectionMatrix[i][j];
		}

		for (double i = 0; i < numrows; i++) {
			for (double j = 0; j < numcols; j++) {							// for each agent, compute the boundary to compute the area
				double leftBoundary = j - CONNECTIVITY_RANGE, rightBoundary = j + CONNECTIVITY_RANGE, topBoundary = i - CONNECTIVITY_RANGE, bottomBoundary = i
						+ CONNECTIVITY_RANGE;
				if (leftBoundary < -PERIMETER)
					leftBoundary = -PERIMETER;
				if (rightBoundary > numcols - 1 + PERIMETER)
					rightBoundary = numcols - 1 + PERIMETER;
				if (topBoundary < -PERIMETER)
					topBoundary = -PERIMETER;
				if (bottomBoundary > numrows - 1 + PERIMETER)
					bottomBoundary = numrows - 1 + PERIMETER;
				double area = (rightBoundary - leftBoundary) * (bottomBoundary - topBoundary);
				// adjust area for circular range
				// if area is a square, then each side is sqrt(area)
				// the diamter will also be sqrt(area)
				// the circular area = pi*r^2 = pi*(d/2)^2
				// = pi*area/4 = pi/4*area
				if (!SQUARE_RANGE) {
					double radius = ((rightBoundary - leftBoundary) + (bottomBoundary - topBoundary)) / 4;
					area = Math.PI * radius * radius;
				}
				estimatedNumberOfConnections += ((rightBoundary - leftBoundary) > 0 && (bottomBoundary - topBoundary) > 0) ? area * N
						* DENSITY : 0;
			}							// add up the average number of connections
		}								// scale the estimated number of connections by the number of agents
		estimatedNumberOfConnections /= (numrows - 1 + 2 * PERIMETER) * (numcols - 1 + 2 * PERIMETER);
		if (showDebug) {
			System.out.println("\tif random resource placement, then the estimated number of connections is "
					+ estimatedNumberOfConnections);
			System.out.println("\tthe actual number of connections is " + actualNumberOfConnections);
		}
		return estimatedNumberOfConnections;
	} // end print the number of connections

	// ** Clusterness *******************************************************************************'
	public double clusterness() {
		double clusterness = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			// System.out.println("working on a" + i);
			double agentClusterness = 0;
			int denominator = 0;
			for (int j = 0; j < numrows * numcols; j++) {
				if (adjacencyMatrix[i][j] > 0) {
					denominator++;
					// System.out.println("\tdenomination++ since i is connected to " + j);
					for (int k = 0; k < numrows * numcols; k++) {
						if (i != j && j != k && i != k && adjacencyMatrix[i][k] > 0 && adjacencyMatrix[j][k] > 0) {
							agentClusterness++;
							// System.out.println("\tagentClusterness +1 since both " + j + " and " + k
							// + " is connected to each other and to a" + i);
						}
					}
				}
			}
			// System.out.print("\tdenominator = " + denominator);
			denominator = choose(denominator, 2);
			// System.out.println("\t now denominator = " + denominator);
			agentClusterness = agentClusterness / 2.0 / denominator; // divide by 2 since each is account for twice
			// System.out.println("agentClusterness = " + agentClusterness);
			if (agentClusterness >= 0)
				clusterness += agentClusterness;
		}
		// clusterness = clusterness / (numrows * numcols);
		// System.out.println("\tsmall world clusterness = " + clusterness);
		return clusterness / (numrows * numcols);
	} // end clusterness

	// ** Overlap **********************************************************************************
	public double overlap() {
		int pairCount = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = i + 1; j < numrows * numcols; j++) {
				for (int k = 0; k < N; k++) {
					if (connectionMatrix[i][k] > 0 && connectionMatrix[j][k] > 0) {
						pairCount++;
					}
				}
			}
		}
		// System.out.println("pairCount = " + pairCount);
		// System.out.println("choose = " + choose(numrows * numcols, 2) + ", N = " + N);
		return (double) pairCount / (choose(numrows * numcols, 2) * N);
	}

	// ** Average Number of Resources Per Adjacency ************************************************
	public double averageNumberOfResourcesPerAdjacency() {
		// double overlap = 0;
		int numberOfNeighboringAgents = 0;
		int numberOfOverlappingResources = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			// int numberOfNeighboringAgents = 0;
			// int numberOfOverlappingResources = 0;
			for (int j = i + 1; j < numrows * numcols; j++) {
				if (adjacencyMatrix[i][j] > 0) {
					numberOfNeighboringAgents++;
					for (int k = 0; k < N; k++) {
						if (connectionMatrix[i][k] > 0 && connectionMatrix[j][k] > 0)
							numberOfOverlappingResources++;
					}
				}
			}
			// System.out.println(numberOfOverlappingResources + ", " + numberOfNeighboringAgents);
			// if (numberOfNeighboringAgents > 0)
			// overlap += (double) numberOfOverlappingResources / (double) numberOfNeighboringAgents;
		}
		// return overlap / (double) (numrows * numcols);
		return (double) numberOfOverlappingResources / numberOfNeighboringAgents;
	}

	// ** Test for Wrong Solution of Seeded Input Over an Interval of Agents and Resources **********
	public void testImprovementDeclination(boolean improvement) {
		boolean tempVocal = vocal;
		vocal = false;		// turn on boolean to mute
		PrintWriter outputStream = null; // declare print writer
		try {							// try opening file name
			outputStream = new PrintWriter(new FileOutputStream(testResultsFileName));
		} catch (FileNotFoundException e) {							// open file error checking
			System.out.println("\terror opening the file " + testResultsFileName);
			System.exit(0);
		}
		for (int rowCounter = 5; rowCounter < 6; rowCounter++) // should begin at at least 1
		{							// for row interval
			for (int columnCounter = 5; columnCounter < 6; columnCounter++) // should begin at at least 1
			{						// for column interval
				numrows = rowCounter;
				numcols = columnCounter;
				for (N = 1; N <= 250; N++) // should begin at at least 1
				{					// for number of resources
					maxID = N - 1;	// adjust maxID to follow N
					for (int i = 0; i < 3; i++) {				// for number of trails
						printSeededConnectionMatrix(improvement); // print the ideal matrix
						useConnectionInput(IOSeededConnectionFileName); // use the ideal matrix
						updateMinMaxFairnessIndexes(); // added to attempt to fix scaled FI
						while (batchCascadedImprovementPath(improvement) > 0)
							; // execute all improvement paths
						fairnessIndex(0, outputStream, false); // write results to file output
					}
				}
			}
		}
		outputStream.close();		// close file output
		vocal = tempVocal;		// undo mute operations
		System.out.println("\twrote to file: " + testResultsFileName); // let user know results are written
	} // end test for wrong solution

	public void topographySetupNew(int setUp) {
		if (setUp < 0)
			return;
		switch (setUp) {
		case 0:
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 4000;					// set to zero if do not want to use numEdges
			numAgentsFlops = 0;//(int) (numEdges * 3.2);
			numResourcesFlops = 0;//(int) (numEdges * 3.2);
			CONNECTIVITY_RANGE = 0.89;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 1:	// 5K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 12000;					// set to zero if do not want to use numEdges
			numAgentsFlops = 0;//(int) (numEdges * 3.2);
			numResourcesFlops = 0;//(int) (numEdges * 3.2);
			CONNECTIVITY_RANGE = 1.28;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 2:	// 10K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 36000;					// set to zero if do not want to use numEdges
			numAgentsFlops = 0;//(int) (numEdges * 3.2);
			numResourcesFlops = 0;//(int) (numEdges * 3.2);
			CONNECTIVITY_RANGE = 1.86;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 3:	// 20K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 4000;					// set to zero if do not want to use numEdges
			numAgentsFlops = (int) (numEdges * 0.4);
			numResourcesFlops = (int) (numEdges * 0.2);
			CONNECTIVITY_RANGE = 2.74;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 4:	// 40K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 12000;					// set to zero if do not want to use numEdges
			numAgentsFlops = (int) (numEdges * 0.4);
			numResourcesFlops = (int) (numEdges * 0.2);
			CONNECTIVITY_RANGE = 4.19;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 5:	// 2.5K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 36000;					// set to zero if do not want to use numEdges
			numAgentsFlops = (int) (numEdges * 0.4);
			numResourcesFlops = (int) (numEdges * 0.2);
			CONNECTIVITY_RANGE = 1.21;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = -0.5;		// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = -0.5;			// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = .5;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 1;			// set to 0 if determinate resource quota
			agentBalanceFactor = 1;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 0.075;				// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 6:	// 5K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 1.145;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 7: // 10K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 2.05;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 8: // 20K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 3.91;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		default:
			System.out.println("error: incorrect setUp. setUp = " + setUp);
			break;
		}
	}
	
	
	public void topographySetup(int setUp) {
		if (setUp < 0)
			return;
		switch (setUp) {
		case 0:	// 2.5K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 0.89;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 1:	// 5K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 1.28;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 2:	// 10K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 1.86;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 3:	// 20K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 2.74;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 4:	// 40K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 4.19;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 5:	// 2.5K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 1.21;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = -0.5;		// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = -0.5;			// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = .5;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 1;			// set to 0 if determinate resource quota
			agentBalanceFactor = 1;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 0.075;				// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 6:	// 5K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 1.79;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = -0.5;		// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = -0.5;			// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = .5;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 1;			// set to 0 if determinate resource quota
			agentBalanceFactor = 1;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 0.075;				// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 7: // 10K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 2.67;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = -0.5;		// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = -0.5;			// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = .5;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 1;			// set to 0 if determinate resource quota
			agentBalanceFactor = 1;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 0.075;				// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 8: // 20K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 4.11;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = -0.5;		// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = -0.5;			// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = .5;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 1;			// set to 0 if determinate resource quota
			agentBalanceFactor = 1;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 0.075;				// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 9: // 40K numEdges unbalanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 7.05;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = -0.5;		// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = 0.5;			// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = -0.5;			// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = .5;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 1;			// set to 0 if determinate resource quota
			agentBalanceFactor = 1;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 0.075;				// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		case 10:	// 80K numEdges balanced
			numrows = 10;					// set number of rows of agents
			numcols = 10;					// set number of columns of agents
			N = 1000;						// set number of resources
			numEdges = 0;					// set to zero if do not want to use numEdges
			CONNECTIVITY_RANGE = 7.05;		// set agent-resource connectivity range
			PERIMETER = 0;					// <=0 for agent boarder, >0 for resource boarder
			TOP_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			BOTTOM_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			LEFT_PERIMETER = PERIMETER;		// <=0 for agent boarder, >0 for resource boarder
			RIGHT_PERIMETER = PERIMETER;	// <=0 for agent boarder, >0 for resource boarder
			SQUARE_RANGE = false;			// set to false if want circular range
			DENSITY = 1;					// set the probability of connection, 1 means to connect if within range
			densityVariance = 0;			// set to 0 if determinate resource quota
			agentBalanceFactor = 0;			// set to 0 or infinity for fair connection distribution
			locationAlgorithm = 5;			// 0 = create at PARAMETER, 1 = random, 2 = uniform, 3 = center in-between, 4 = de Moivre's circle, 5 geometric series skew
			assignmentAlgorithm = 1;		// 0 = no assignment, 1 = random, 2 = assign to nearest, 3 = pmf, 4 = bidding
			pGeometric = 1;					// probability of successfully stop creating more resources
			RADIUS = 0.65;					// radius of de Moivre's circle, only applies if locationAlgorithm==4
			break;
		default:
			System.out.println("error: incorrect setUp. setUp = " + setUp);
			break;
		}
	}
	
	public int setupStartingFI() {
		int numberOfPaths = 0;
		if (startingNetworkFI == 0) {
			worst();
		} else if (startingNetworkFI == 1) {
			while (batchCascadedImprovementPath(true) > 0);
		} else if (startingNetworkFI < 0) {
			
		} else if (scaledNetworkAgentFairnessIndex() > startingNetworkFI) {
			while (scaledNetworkAgentFairnessIndex() > startingNetworkFI && (ceilingLimit ? fastIncompleteDeclinationPath(CEILING) : fastIncompleteImprovementPath(false)) > 0) numberOfPaths++; //fastIncompleteImprovementPath
		} else if (scaledNetworkAgentFairnessIndex() < startingNetworkFI) {
			while (scaledNetworkAgentFairnessIndex() < startingNetworkFI && cascadedImprovementPath(reachMatrix, 0, true) > 0);
		}
		if (numberOfPaths > 0) {
			useAssignmentMatrix();
		}
		//toString(assignmentMatrix);
		return numberOfPaths;
	}

	// ** Print Visuals for Graphs and Algorithms **************************************************
	public void printVisuals() {
		boolean tempVocal = vocal;
		vocal = false;
		for (int i = 0; i <= 9; i++) {
			System.out.println("\tworking on i = " + i);
			// topographySetup(i);
			maxID = -1;					// compute maxID for creating new resources
			if (numEdges >= N && numEdges <= numcols * numrows * N)
				init3();	// initialize grid with agents and resources
			else
				init(numrows, numcols, N);
			useConnectionInput("IConnectionInput" + i + ".txt");
			printMatrices(10);
			printMatrices(11);
			String topographySummary = "";
			// if (i == 0)
			// topographySummary = "2500Balnce";
			// if (i == 1)
			// topographySummary = "005KBalnce";
			// if (i == 2)
			// topographySummary = "010KBalnce";
			// if (i == 3)
			// topographySummary = "020KBalnce";
			// if (i == 4)
			// topographySummary = "2500Unbaln";
			// if (i == 5)
			// topographySummary = "005KUnbaln";
			// if (i == 6)
			// topographySummary = "010KUnbaln";
			// if (i == 7)
			// topographySummary = "020KUnbaln";

			// if (i == 0)
			// topographySummary = "LocalNetworkBalnce";
			// if (i == 1)
			// topographySummary = "LocalNtwrkUnbalnce";
			// if (i == 2)
			// topographySummary = "RandomNtwrkBalance";
			// if (i == 3)
			// topographySummary = "OneBand___________";
			// if (i == 4)
			// topographySummary = "TwoBands__________";
			// if (i == 5)
			// topographySummary = "FourBands_________";
			// if (i == 6)
			// topographySummary = "TopMoreDense______";
			// if (i == 7)
			// topographySummary = "LeftMoreDense_____";
			// if (i == 8)
			// topographySummary = "TopLeftMoreDense__";
			// if (i == 9)
			// topographySummary = "TopLeftAndBotRight";
			for (int m = 6; m < 11; m++) {
				System.out.println("\tworking on m = " + m);
				useConnectionInput(IOConnectionMatrixFileName); // use connection matrix first
				useAssignmentInput(IOAssignmentMatrixFileName); // then use assignment matrix
				// if (m == 0) {
				// algorithm = "MnMx_RT";
				// }
				// if (m == 1) {
				// algorithm = "MnMx_WS";
				// }
				// if (m == 2) {
				// algorithm = "OffA_RT";
				// }
				// if (m == 3) {
				// algorithm = "OffA_WS";
				// }
				// if (m == 4) {
				// algorithm = "OA_Casc";
				// }
				// if (m == 5) {
				// algorithm = "StbM_2P";
				// }
				// if (m == 6) {
				// algorithm = "bidding";
				// }
				// if (m == 7) {
				// algorithm = "Cen_NIP";
				// }
				// if (m == 8) {
				// algorithm = "Cen_CIP";
				// }
				if (i == 0)
					topographySummary = "LocalNetworkBalnce";
				if (i == 1)
					topographySummary = "LocalNtwrkUnbalnce";
				if (i == 2)
					topographySummary = "RandomNtwrkBalance";
				if (i == 3)
					topographySummary = "OneBandddddddddddd";
				if (i == 4)
					topographySummary = "TwoBandsssssssssss";
				if (i == 5)
					topographySummary = "FourBandssssssssss";
				if (i == 6)
					topographySummary = "TopMoreDenseeeeeee";
				if (i == 7)
					topographySummary = "LeftMoreDenseeeeee";
				if (i == 8)
					topographySummary = "TopLeftMoreDenseee";
				if (i == 9)
					topographySummary = "TopLeftAndBotRight";
				printAgentsGraph(2, topographySummary + (topographySummary.length() > 0 ? "_" : "") + algorithm + "_" +  algorithmStep);
				results = algorithmCaller(m);
				while (results != 0) {
					algorithmStep += batchScale;
					printAgentsGraph(2, topographySummary);
					results = algorithmCaller(m);
				}
			}
		}
		vocal = tempVocal;
	}
	
	public void printASG() {
		int counter = 1;
		int label = 0;
		for (int i = maxID + 1; i <= (maxID + 1) * numrows * numcols; i++) {
			numEdges = i;
			for (int j = 0; j < counter; j++) {
				boolean tempVocal = vocal;				// used to mute reset
				vocal = true;
				init3();								// initialize grid with agents and resources
				vocal = tempVocal;
				updateMinMaxFairnessIndexes();
				printASG(2, label++);
			}
			// System.out.println("i ="+i+" and compr = "+(((maxID+1)*numrows*numcols+(maxID+1))/2));
			if (i < (((maxID + 1) * numrows * numcols + (maxID + 1)) / 2))
				counter++;
			else
				counter--;
			// System.out.println("counter ="+counter);
		}
	}
	
//	// ** Test Algorithms **************************************************************************
//	public void testAlgorithmsPhases(int testVariable) {
//			int numberOfTrials = 10;
//			int numberOfDisturbances = 0;				// how many volcano eruptions
//			int disturbanceLevel = 0;					// percentage of resources to disturb, the strength of the volcano eruption
//			int maxRecoveryTime = 0;					// frequency of disturbanaces
//			int numberOfPhasesToObserve = (int) 60; // goes by 3*step, 5*step, 7*step, 9*step
//			// int numberOfIterations = 3; // goes by 3*step, 5*step, 7*step, 9*step
//			System.out.println("numberOfTrials = " + numberOfTrials + ", numberOfDisturbances = " + numberOfDisturbances
//					+ ", disturbanceLevel = " + disturbanceLevel + ", maxRecoveryTime = " + maxRecoveryTime);
//			// The following 3 lines of code were replaced with algorithms, the global variable.
//			// String[] algorithmInitials = { "bid001", "bid002", "bid004", "bid008", "bid016", "bid032", "bid064", "bid100" };
//			// String[] algorithmInitials = { "lH_WS", "mM_RT", "mM_WS", "OA_RT", "OA_WS", "OA_Casc", "SM_2P", "BidPeak", "BidHigh", "BidMid", "BidLow", "BidRand", "BNIP", "BCIP", "worst"};
//			
//			int tempResults = 0;
//			int numberOfIterationsExecuted = 0;
//			int numberOfTotalPhases = 0;
//			int numberOfTotalCommunications = 0;
//			int numberOfTotalExecutions = 0;
//			// double[] FIBackwards = new double[numberOfIterations];
//			// int[] resultsBackwards = new int[numberOfIterations];
//			long trialStartMilliseconds = new Date().getTime();
//			long previousTimeMilliseconds = new Date().getTime();
//			int breakPoint = 5; // if index is below, then set up for per iteration as opposed to per algorithm
//			PrintWriter[] outputStreams = new PrintWriter[15];
//			try {							// try opening file name
//				outputStreams[0] = new PrintWriter(new FileOutputStream(testFIResultsFileName));			// declare print writer for scaled FI
//				outputStreams[1] = new PrintWriter(new FileOutputStream(testNetworkFIResultsFileName));		// declare print writer for network scaled FI
//				outputStreams[2] = new PrintWriter(new FileOutputStream(testExecutionsResultsFileName));	// declare print writer for umber of executions
//				outputStreams[3] = new PrintWriter(new FileOutputStream(testPhasesResultsFileName));		// declare print writer for number of phases
//				outputStreams[4] = new PrintWriter(new FileOutputStream(testCommunicationsResultsFileName));// declare print writer for number of communications
//				outputStreams[5] = new PrintWriter(new FileOutputStream(testFinalFIResultsFileName));		// declare print writer for final FI
//				outputStreams[6] = new PrintWriter(new FileOutputStream(testNetworkFinalFIResultsFileName));// declare print writer for newtork final FI
//				outputStreams[7] = new PrintWriter(new FileOutputStream(testTotalExecutionsResultsFileName));// declare print writer for total executions
//				outputStreams[8] = new PrintWriter(new FileOutputStream(testIterationsResults1000FileName));// declare print writer for number of iterations
//				outputStreams[9] = new PrintWriter(new FileOutputStream(testPhasesResults1000FileName));	// declare print writer for total number of phases
//				outputStreams[10] = new PrintWriter(new FileOutputStream(testTotalCommunicationsResultsFileName));	// declare print writer for total number of communications
//				outputStreams[11] = new PrintWriter(new FileOutputStream(testIterationsResults0950FileName));// declare print writer for number of iterations
//				outputStreams[12] = new PrintWriter(new FileOutputStream(testIterationsResults0975FileName));// declare print writer for number of iterations
//				outputStreams[13] = new PrintWriter(new FileOutputStream(testPhasesResults0950FileName));// declare print writer for number of iterations
//				outputStreams[14] = new PrintWriter(new FileOutputStream(testPhasesResults0975FileName));// declare print writer for number of iterations
//			} catch (FileNotFoundException e) {							// open file error checking
//				System.out.println("\terror opening the test result files ");
//				System.exit(0);
//			}
//			double[] testArray = { 432, 896, 1858, 3852, 7987, 16562, 25000 };// 28619, 34343, 41211, 49453, 59344,
//			double[] rangeArray = { 0.74, 1.07, 1.585, 2.36, 3.63, 5.98, 13 };
//			// double[] rangeArray = {0.74, 0.81, 0.89, 0.975, 1.07, 1.18, 1.30, 1.44, 1.585, 1.745, 1.925, 2.125, 2.36, 2.62, 2.915, 3.24, 3.63, 4.07, 4.60, 5.225, 5.98, 7.065, 8.95,
//			// 13};
//			// double[] testArray = { 432, 518, 622, 746, 896, 1075, 1290, 1548, 1858, 2229, 2675, 3210, 3852, 4622, 5547, 6656, 7987, 9584, 11501, 13802, 16562, 19874, 23849, 25000};
//			double[] pGeometricArray = { 0.001, 0.01, 0.025, 0.05, 0.075, 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80, 0.90, 1.00 }; // 1, 0.75, 0.50, 0.375, 0.25, 0.175, 0.10,
//			// 0.075, 0.05, 0.025, 0.01, 0.001};
//			double[] densityVarianceArray = { 0, 0.05, 0.10, 0.20, 0.3, 0.4, 0.50, 0.60, 0.70, 0.80, 0.90, 1, 2, 4, 8 };
//			if (testVariable == 0)
//				testArray = testTopographyArray;
//			if (testVariable == 2)
//				testArray = rangeArray;
//			if (testVariable == 3)
//				testArray = pGeometricArray;
//			if (testVariable == 4) {
//				testArray = densityVarianceArray;
//				if (DENSITY == 1 && CONNECTIVITY_RANGE == 1.3) {
//					System.out.println("changing test densityVariance to DENSITY==0.5&&CONNECTIVITY_RANGE==1.89 from DENSITY==1&&CONNECTIVITY_RANGE==1.3");
//					CONNECTIVITY_RANGE = 1.89;	// set agent-resource connectivity range
//					DENSITY = .5;				// set the probability of connection, 1 means to connect if within range
//				}
//			}
//			if (testVariable == 5) {
//				double[] temp = new double[3];
//				testArray = temp;
//			}
//			if (testVariable == 6) {
//				double[] temp = new double[3];
//				testArray = temp;
//			}
//			boolean tempVocal = vocal;
//			vocal = false;						// mute init
//			for (int testWalker = 0; testWalker < testArray.length; testWalker++) {
//				System.out.println("\tworking on variable = " + testArray[testWalker] + " after "
//						+ ((new Date().getTime() - trialStartMilliseconds) / 1000 + 1) + " seconds ("
//						+ ((new Date().getTime() - previousTimeMilliseconds) / 1000 + 1) + " seconds difference)");
//				previousTimeMilliseconds = new Date().getTime();
//				for (int i = 0; i < outputStreams.length; i++) {
//					if (testVariable == 0) {
//						topographySetup((int) testArray[testWalker]);
//						outputStreams[i].println("customTopographySetup= " + testArray[testWalker]);
//					}
//					if (testVariable == 1) {
//						numEdges = (int) testArray[testWalker];
//						outputStreams[i].println("numEdges= " + numEdges);
//					}
//					if (testVariable == 2) {
//						CONNECTIVITY_RANGE = testArray[testWalker];
//						numEdges = 0;
//						outputStreams[i].println("CONNECTIVITY_RANGE= " + CONNECTIVITY_RANGE);
//					}
//					if (testVariable == 3) {
//						pGeometric = testArray[testWalker];
//						outputStreams[i].println("pGeometric= " + pGeometric);
//					}
//					if (testVariable == 4) {
//						densityVariance = testArray[testWalker];
//						outputStreams[i].println("densityVariance= " + densityVariance);
//					}
//					if (testVariable == 5) {
//						outputStreams[i].println("fileName= IConnectionInput" + testWalker + ".txt");
//					}
//					if (testVariable == 6) {
//						outputStreams[i].println("manualGraph= " + testWalker);
//					}
//					if (i < 4)														// different output format for some files
//						outputStreams[i].printf("%4s", ".");
//					else
//						outputStreams[i].printf("   n");
//					for (int j = 0; j < algorithms.length; j++) {
//						for (int l = 0; l <= numberOfDisturbances; l++) {
//							if (l == 0)
//								outputStreams[i].printf(" %7s", algorithms[j]);
//							else
//								outputStreams[i].printf(" %7s", ("AD" + (l)));			// after disturbance number l
//							if (i < breakPoint) {
//								for (int k = 0; k <= (l == 0 ? numberOfPhasesToObserve - 1 : maxRecoveryTime); k++) // the " - 1" was recently added
//									outputStreams[i].printf(" %7s", ".");
//							}
//						}
//					}
//					if (i < breakPoint) {
//						outputStreams[i].printf("\n   n");
//						for (int j = 0; j < algorithms.length; j++) {
//							for (int l = 0; l <= numberOfDisturbances; l++) {
//								// for (int k = 5*stepScale; k<=7*stepScale; k += batchScale)
////								for (int k = 5 * stepScale; k <= (l == 0 ? 5 * stepScale + numberOfIterations * batchScale : 5 * stepScale + maxRecoveryTime * batchScale); k += batchScale)
//								for (int k = 0; k <= numberOfPhasesToObserve; k++) {
//									outputStreams[i].printf(" %7s", "" + k);
//								}
//							}
//						}
//					}
//				}
//				for (int trialNumber = 1; trialNumber <= numberOfTrials; trialNumber++) // set to 1000 later
//				{
//					// if (showDebug)
//					// if (trialNumber > 1 && (trialNumber - 1) % 5 == 0)
//					System.out.println("\t\tworking on trialNumber " + trialNumber);
//					for (int i = 0; i < outputStreams.length; i++)
//						outputStreams[i].printf("\n %3s", trialNumber);
//					maxID = -1;					// compute maxID for creating new resources
//					if (numEdges >= N && numEdges <= numcols * numrows * N)
//						init2();	// initialize grid with agents and resources
//					else
//						init(numrows, numcols, N);
//					if (assignmentAlgorithm == 4) {
//						randomNumberGenerator = new Random(1);	// Random number generator
//						bidding(true, 1);
//					}
//					if (testVariable == 5)
//						useConnectionInput("IConnectionInput" + testWalker + ".txt");
//					if (testVariable == 6)
//						useManualConnectionMatrixSetUp(testWalker);
//					// ALL START FROM SAME ALLOCATION
//					updateMinMaxFairnessIndexes();
////					double startingFI = 0.85;
////					if (scaledAgentFairnessIndex() > startingFI) {
////						while (scaledAgentFairnessIndex() > startingFI && improvementPath(false) > 0);
////					} else if (scaledAgentFairnessIndex() < startingFI) {
////						while (scaledAgentFairnessIndex() < startingFI && cascadedImprovementPath(reachMatrix, 0, true) > 0);
////					}
//					double startingNetworkFI = 0.50;
//					if (startingNetworkFI == 0) {
//						worst();
//					} else if (scaledNetworkAgentFairnessIndex() > startingNetworkFI) {
//						while (scaledNetworkAgentFairnessIndex() > startingNetworkFI && improvementPath(false) > 0);
//					} else if (scaledNetworkAgentFairnessIndex() < startingNetworkFI) {
//						while (scaledNetworkAgentFairnessIndex() < startingNetworkFI && cascadedImprovementPath(reachMatrix, 0, true) > 0);
//					}
//					printMatrices(10);
//					printMatrices(11);
//					 long variableStartMilliseconds = new Date().getTime();
//					 long previousmMilliseconds = variableStartMilliseconds;
//					for (int m = 0; m < algorithms.length; m++) {
//						 System.out.println("\t\tworking on m = " + m + " after "
//						 + ((new Date().getTime() - variableStartMilliseconds) / 1000 + 1) + " seconds ("
//						 + ((new Date().getTime() - previousmMilliseconds) / 1000 + 1) + " seconds difference)");
//						 previousmMilliseconds = new Date().getTime();
//						 
//						useConnectionInput(IOConnectionMatrixFileName); // use connection matrix
//						useAssignmentInput(IOAssignmentMatrixFileName); // then use assignment matrix
//						disturbanceNumberGenerator = new Random(1);
//						for (int k = 0; k <= numberOfDisturbances; k++) {
//							boolean is0950Set = false;
//							boolean is0975Set = false;
//							int scaledNetworkFI0950Iteration = -1;
//							int scaledNetworkFI0975Iteration = -1;
//							int scaledNetworkFI0950Phase = -1;
//							int scaledNetworkFI0975Phase = -1;
//							numberOfIterationsExecuted = 0;
//							numberOfTotalPhases = 0;
//							numberOfTotalCommunications = 0;
//							numberOfTotalExecutions = 0;
//							randomNumberGenerator = new Random(1);		// Random number generator
//							biddingAggressiveness = 0;
//							outputStreams[0].printf(" %1.5f", scaledAgentFairnessIndex());
//							outputStreams[1].printf(" %1.5f", scaledNetworkAgentFairnessIndex());
//							outputStreams[2].printf(" %7s", 0);
//							outputStreams[3].printf(" %7s", 0);
//							outputStreams[4].printf(" %7s", 0);
//							double[] newValues = {numberOfTotalPhases, scaledAgentFairnessIndex(), scaledNetworkAgentFairnessIndex(), Math.max(tempResults, 0), numberOfPhases, numberOfCommunications };
//							double[] oldValues = new double[newValues.length];
//							for (int i = 1; i < numberOfPhasesToObserve && (k == 0 || maxRecoveryTime <= 0 || i <= maxRecoveryTime); i++) {
//								for (int j = 0; j < newValues.length; j++) {
//									oldValues[j] = newValues[j];
//								}								
//								tempResults = algorithmCaller(m);
//								numberOfTotalPhases += numberOfPhases;
//								numberOfTotalCommunications += numberOfCommunications;
//								newValues[0] = numberOfTotalPhases;
//								newValues[1] = scaledAgentFairnessIndex();
//								newValues[2] = scaledNetworkAgentFairnessIndex();
//								newValues[3] = Math.max(tempResults, 0);
//								newValues[4] = numberOfPhases;
//								newValues[5] = numberOfCommunications;
//								for ( ; i - oldValues[0] < numberOfPhases - 1 && i < numberOfPhasesToObserve; i++) {
//									outputStreams[0].printf(" %1.5f", oldValues[1]);
//									outputStreams[1].printf(" %1.5f", oldValues[2]);
//									outputStreams[2].printf(" %7s", 0);
//									outputStreams[3].printf(" %7s", 1);
//									outputStreams[4].printf(" %7s", (int) oldValues[5]);
//								}
//								if (i < numberOfPhasesToObserve) {
//									outputStreams[0].printf(" %1.5f", newValues[1]);
//									outputStreams[1].printf(" %1.5f", newValues[2]);
//									outputStreams[2].printf(" %7s", (int) newValues[3]);
//									outputStreams[3].printf(" %7s", 1);
//									outputStreams[4].printf(" %7s", (int) newValues[5]);
//								}
//								if (newValues[1] >= 0.95 && !is0950Set) {
//									is0950Set = true;
//									scaledNetworkFI0950Iteration = i + 1;
//									scaledNetworkFI0950Phase = numberOfTotalPhases;
//								}
//								if (newValues[1] >= 0.975 && !is0975Set) {
//									is0975Set = true;
//									scaledNetworkFI0975Iteration = i + 1;
//									scaledNetworkFI0975Phase = numberOfTotalPhases;
//								}
//								if (tempResults > 0)
//									numberOfTotalExecutions += tempResults;
//								if (tempResults == 0) {
//									double scaledAgentFairnessIndex = scaledAgentFairnessIndex();
//									double scaledNetworkAgentFairnessIndex = scaledNetworkAgentFairnessIndex();
//									for (int j = i + 1; j < numberOfPhasesToObserve && (k == 0 || maxRecoveryTime <= 0 || j <= maxRecoveryTime); j++) {
//										outputStreams[0].printf(" %1.5f", scaledAgentFairnessIndex);
//										outputStreams[1].printf(" %1.5f", scaledNetworkAgentFairnessIndex);
//										outputStreams[2].printf(" %7s", 0);
//										outputStreams[3].printf(" %7s", 0);
//										outputStreams[4].printf(" %7s", 0);
//									}
//									break; 			// break out of for loop since already executed
//								}
//								numberOfIterationsExecuted++;
//							}
//							if ((k == 0 || maxRecoveryTime <= 0) && (m < 5 || m > 7)) {
//								tempResults = algorithmCaller(m);
//								while (tempResults != 0) {
//									numberOfIterationsExecuted++;
//									if (!is0950Set && scaledNetworkAgentFairnessIndex() >= 0.95) {
//										is0950Set = true;
//										scaledNetworkFI0950Iteration = numberOfIterationsExecuted;
//									}
//									if (!is0975Set && scaledNetworkAgentFairnessIndex() >= 0.975) {
//										is0975Set = true;
//										scaledNetworkFI0975Iteration = numberOfIterationsExecuted;
//									}
//									numberOfTotalPhases += numberOfPhases;
//									numberOfTotalCommunications += numberOfCommunications;
//									if (tempResults > 0)
//										numberOfTotalExecutions += tempResults;
//									tempResults = algorithmCaller(m);
//								}
//							}
//							if (k == 0) {
//								outputStreams[0].printf(" %1.5f", scaledAgentFairnessIndex());
//								outputStreams[1].printf(" %1.5f", scaledNetworkAgentFairnessIndex());
//								outputStreams[2].printf(" %7s", 0);
//								outputStreams[3].printf(" %7s", 0);
//								outputStreams[4].printf(" %7s", 0);
//							}
//							outputStreams[5].printf(" %1.5f", scaledAgentFairnessIndex());
//							outputStreams[6].printf(" %1.5f", scaledNetworkAgentFairnessIndex());
//							outputStreams[7].printf(" %7s", numberOfTotalExecutions);
//							outputStreams[8].printf(" %7s", numberOfIterationsExecuted);
//							outputStreams[9].printf(" %7s", numberOfTotalPhases);
//							outputStreams[10].printf(" %7s", numberOfTotalCommunications);
//							outputStreams[11].printf(" %7s", scaledNetworkFI0950Iteration);
//							outputStreams[12].printf(" %7s", scaledNetworkFI0975Iteration);
//							outputStreams[13].printf(" %7s", scaledNetworkFI0950Phase);
//							outputStreams[14].printf(" %7s", scaledNetworkFI0975Phase);
//						}
//					}
//				}
//				for (int i = 0; i < outputStreams.length; i++)
//					outputStreams[i].println("\n\n\n");
//				// outputStreams[2].println("\n");
//			}
//			for (int i = 0; i < outputStreams.length; i++)
//				outputStreams[i].close();	// close file output
//			System.out.println("\twrote to files"); 	// let user know results are written
//			System.out.println("\t" + numberOfTrials + " trials were executed for " + testArray.length + " different variables in "
//					+ ((new Date().getTime() - trialStartMilliseconds) / 1000 + 1) + " seconds");
//			vocal = tempVocal;			// undo mute operations
//		} // end test bidding algorithm

	// ** Test Algorithms **************************************************************************
	public void testAlgorithms(int testVariable) {
		int numberOfTrials = 1;
		int numberOfDisturbances = 0;				// how many volcano eruptions
		int disturbanceLevel = 0;					// percentage of resources to disturb, the strength of the volcano eruption
		int maxRecoveryTime = 0;					// frequency of disturbanaces
		int numberOfIterations = (int) Math.ceil(2 * stepScale / batchScale); // goes by 3*step, 5*step, 7*step, 9*step
		// int numberOfIterations = 3; // goes by 3*step, 5*step, 7*step, 9*step
		System.out.println("numberOfTrials = " + numberOfTrials + ", numberOfDisturbances = " + numberOfDisturbances
				+ ", disturbanceLevel = " + disturbanceLevel + ", maxRecoveryTime = " + maxRecoveryTime);
		// The following 3 lines of code were replaced with algorithms, the global variable.
		// String[] algorithmInitials = { "bid001", "bid002", "bid004", "bid008", "bid016", "bid032", "bid064", "bid100" };
		// String[] algorithmInitials = { "lH_WS", "mM_RT", "mM_WS", "OA_RT", "OA_WS", "OA_Casc", "SM_2P", "BidPeak", "BidHigh", "BidMid", "BidLow", "BidRand", "BNIP", "BCIP", "worst"};
		
		int tempResults = 0;
		int numberOfIterationsExecuted = 0;
		int numberOfTotalPhases = 0;
		int numberOfTotalCommunications = 0;
		int numberOfTotalExecutions = 0;
		// double[] FIBackwards = new double[numberOfIterations];
		// int[] resultsBackwards = new int[numberOfIterations];
		long trialStartMilliseconds = new Date().getTime();
		long previousTimeMilliseconds = new Date().getTime();
		int breakPoint = 6; // if index is below, then set up for per iteration as opposed to per algorithm
		PrintWriter[] outputStreams = new PrintWriter[21];
		PrintWriter testDistributionOutputStream = null;
		try {							// try opening file name
			outputStreams[0] = new PrintWriter(new FileOutputStream(testFIResultsFileName));			// declare print writer for scaled FI
			outputStreams[1] = new PrintWriter(new FileOutputStream(testNetworkFIResultsFileName));		// declare print writer for network scaled FI
			outputStreams[2] = new PrintWriter(new FileOutputStream(testExecutionsResultsFileName));	// declare print writer for umber of executions
			outputStreams[3] = new PrintWriter(new FileOutputStream(testPhasesResultsFileName));		// declare print writer for number of phases
			outputStreams[4] = new PrintWriter(new FileOutputStream(testCommunicationsResultsFileName));// declare print writer for number of communications
			outputStreams[5] = new PrintWriter(new FileOutputStream(testOffersResultsFileName));// declare print writer for number of offers
			outputStreams[6] = new PrintWriter(new FileOutputStream(testFinalFIResultsFileName));		// declare print writer for final FI
			outputStreams[7] = new PrintWriter(new FileOutputStream(testNetworkFinalFIResultsFileName));// declare print writer for newtork final FI
			outputStreams[8] = new PrintWriter(new FileOutputStream(testTotalExecutionsResultsFileName));// declare print writer for total executions
			outputStreams[9] = new PrintWriter(new FileOutputStream(testIterationsResults1000FileName));// declare print writer for number of iterations
			outputStreams[10] = new PrintWriter(new FileOutputStream(testPhasesResults1000FileName));	// declare print writer for total number of phases
			outputStreams[11] = new PrintWriter(new FileOutputStream(testCommunicationsResults1000FileName));	// declare print writer for total number of communications
			outputStreams[12] = new PrintWriter(new FileOutputStream(testIterationsResults0950FileName));// declare print writer for number of iterations
			outputStreams[13] = new PrintWriter(new FileOutputStream(testIterationsResults0975FileName));// declare print writer for number of iterations
			outputStreams[14] = new PrintWriter(new FileOutputStream(testPhasesResults0950FileName));// declare print writer for number of iterations
			outputStreams[15] = new PrintWriter(new FileOutputStream(testPhasesResults0975FileName));// declare print writer for number of iterations
			outputStreams[16] = new PrintWriter(new FileOutputStream(testCommunicationsResults0950FileName));// declare print writer for number of iterations
			outputStreams[17] = new PrintWriter(new FileOutputStream(testCommunicationsResults0975FileName));// declare print writer for number of iterations
			outputStreams[18] = new PrintWriter(new FileOutputStream(testFairnessIndexResults02FileName));// declare print writer for number of iterations
			outputStreams[19] = new PrintWriter(new FileOutputStream(testFairnessIndexResults06FileName));// declare print writer for number of iterations
			outputStreams[20] = new PrintWriter(new FileOutputStream(testFairnessIndexResults10FileName));// declare print writer for number of iterations			
			testDistributionOutputStream = new PrintWriter(new FileOutputStream(testDistributionFunctionFileName));// declare print writer for distribution function
		} catch (FileNotFoundException e) {							// open file error checking
			System.out.println("\terror opening the test result files ");
			System.exit(0);
		}
		double[] testArray = { 432, 896, 1858, 3852, 7987, 16562, 25000 };// 28619, 34343, 41211, 49453, 59344,
		double[] rangeArray = { 0.74, 1.07, 1.585, 2.36, 3.63, 5.98, 13 };
		// double[] rangeArray = {0.74, 0.81, 0.89, 0.975, 1.07, 1.18, 1.30, 1.44, 1.585, 1.745, 1.925, 2.125, 2.36, 2.62, 2.915, 3.24, 3.63, 4.07, 4.60, 5.225, 5.98, 7.065, 8.95,
		// 13};
		// double[] testArray = { 432, 518, 622, 746, 896, 1075, 1290, 1548, 1858, 2229, 2675, 3210, 3852, 4622, 5547, 6656, 7987, 9584, 11501, 13802, 16562, 19874, 23849, 25000};
		double[] pGeometricArray = { 0.001, 0.01, 0.025, 0.05, 0.075, 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80, 0.90, 1.00 }; // 1, 0.75, 0.50, 0.375, 0.25, 0.175, 0.10,
		// 0.075, 0.05, 0.025, 0.01, 0.001};
		double[] densityVarianceArray = { 0, 0.05, 0.10, 0.20, 0.3, 0.4, 0.50, 0.60, 0.70, 0.80, 0.90, 1, 2, 4, 8 };
		if (testVariable == 0)
			testArray = testTopographyArray;
		if (testVariable == 2)
			testArray = rangeArray;
		if (testVariable == 3)
			testArray = pGeometricArray;
		if (testVariable == 4) {
			testArray = densityVarianceArray;
			if (DENSITY == 1 && CONNECTIVITY_RANGE == 1.3) {
				System.out.println("changing test densityVariance to DENSITY==0.5&&CONNECTIVITY_RANGE==1.89 from DENSITY==1&&CONNECTIVITY_RANGE==1.3");
				CONNECTIVITY_RANGE = 1.89;	// set agent-resource connectivity range
				DENSITY = .5;				// set the probability of connection, 1 means to connect if within range
			}
		}
		if (testVariable == 5) {
			double[] temp = new double[3];
			testArray = temp;
		}
		if (testVariable == 6) {
			double[] temp = new double[3];
			testArray = temp;
		}
		boolean tempVocal = vocal;
		vocal = false;						// mute init
		for (int testWalker = 0; testWalker < testArray.length; testWalker++) {
			System.out.println("\tworking on variable = " + testArray[testWalker] + " after "
					+ ((new Date().getTime() - trialStartMilliseconds) / 1000 + 1) + " seconds ("
					+ ((new Date().getTime() - previousTimeMilliseconds) / 1000 + 1) + " seconds difference)");
			previousTimeMilliseconds = new Date().getTime();
			for (int i = 0; i < outputStreams.length; i++) {
				if (testVariable == 0) {
					topographySetup((int) testArray[testWalker]);
					outputStreams[i].println("customTopographySetup= " + testArray[testWalker]);
				}
				if (testVariable == 1) {
					numEdges = (int) testArray[testWalker];
					outputStreams[i].println("numEdges= " + numEdges);
				}
				if (testVariable == 2) {
					CONNECTIVITY_RANGE = testArray[testWalker];
					numEdges = 0;
					outputStreams[i].println("CONNECTIVITY_RANGE= " + CONNECTIVITY_RANGE);
				}
				if (testVariable == 3) {
					pGeometric = testArray[testWalker];
					outputStreams[i].println("pGeometric= " + pGeometric);
				}
				if (testVariable == 4) {
					densityVariance = testArray[testWalker];
					outputStreams[i].println("densityVariance= " + densityVariance);
				}
				if (testVariable == 5) {
					outputStreams[i].println("fileName= IConnectionInput" + testWalker + ".txt");
				}
				if (testVariable == 6) {
					outputStreams[i].println("manualGraph= " + testWalker);
				}
				if (i < 4)														// different output format for some files
					outputStreams[i].printf("%4s", ".");
				else
					outputStreams[i].printf("   n");
				for (int j = 0; j < algorithms.length; j++) {
					for (int l = 0; l <= numberOfDisturbances; l++) {
						if (l == 0)
							outputStreams[i].printf(" %7s", algorithms[j]);
						else
							outputStreams[i].printf(" %7s", ("AD" + (l)));			// after disturbance number l
						if (i < breakPoint) {
							for (int k = 0; k <= (l == 0 ? numberOfIterations - 1 : maxRecoveryTime); k++) // the " - 1" was recently added
								outputStreams[i].printf(" %7s", ".");
						}
					}
				}
				if (i < breakPoint) {
					outputStreams[i].printf("\n   n");
					for (int j = 0; j < algorithms.length; j++) {
						for (int l = 0; l <= numberOfDisturbances; l++) {
							// for (int k = 5*stepScale; k<=7*stepScale; k += batchScale)
							for (int k = 5 * stepScale; k <= (l == 0 ? 5 * stepScale + numberOfIterations * batchScale : 5 * stepScale + maxRecoveryTime * batchScale); k += batchScale)
								outputStreams[i].printf(" %7s", k);
						}
					}
				}
			}
			for (int trialNumber = 1; trialNumber <= numberOfTrials; trialNumber++) // set to 1000 later
			{
				// if (showDebug)
				// if (trialNumber > 1 && (trialNumber - 1) % 5 == 0)
				System.out.println("\t\tworking on trialNumber " + trialNumber);
				for (int i = 0; i < outputStreams.length; i++)
					outputStreams[i].printf("\n %3s", trialNumber);
				maxID = -1;					// compute maxID for creating new resources
				if (numEdges >= N && numEdges <= numcols * numrows * N)
					init3();	// initialize grid with agents and resources
				else
					init(numrows, numcols, N);
				if (assignmentAlgorithm == 4) {
					randomNumberGenerator = new Random(1);	// Random number generator
					bidding(true, 1);
				}
				if (testVariable == 5)
					useConnectionInput("IConnectionInput" + testWalker + ".txt");
				if (testVariable == 6)
					useManualConnectionMatrixSetUp(testWalker);
				// ALL START FROM SAME ALLOCATION
				updateMinMaxFairnessIndexes();
				// NEXT BLOCK OF CODE adjusts starting FI
//				double startingFI = 0.85;
//				if (scaledAgentFairnessIndex() > startingFI) {
//					while (scaledAgentFairnessIndex() > startingFI && improvementPath(false) > 0);
//				} else if (scaledAgentFairnessIndex() < startingFI) {
//					while (scaledAgentFairnessIndex() < startingFI && cascadedImprovementPath(reachMatrix, 0, true) > 0);
//				}
				// NEXT BLOCK OF CODE adjusts starting network FI
				setupStartingFI();
				printMatrices(10);
				printMatrices(11);
				testDistributionOutputStream.println(printStatsConnections((int) testArray[testWalker] + "_connections", false));
				long variableStartMilliseconds = new Date().getTime();
				long previousmMilliseconds = variableStartMilliseconds;
				for (int m = 0; m < algorithms.length; m++) {
					if (trialNumber == 1) {
						testDistributionOutputStream.println("\n\n");
					}
					numberOfCalls = 0;
					 System.out.println("\t\tworking on m = " + m + " after "
					 + ((new Date().getTime() - variableStartMilliseconds) / 1000 + 1) + " seconds ("
					 + ((new Date().getTime() - previousmMilliseconds) / 1000 + 1) + " seconds difference)");
					 previousmMilliseconds = new Date().getTime();
					useConnectionInput(IOConnectionMatrixFileName); // use connection matrix
					useAssignmentInput(IOAssignmentMatrixFileName); // then use assignment matrix
					disturbanceNumberGenerator = new Random(1);
					for (int k = 0; k <= numberOfDisturbances; k++) {
						boolean is0950Set = false;
						boolean is0975Set = false;
						int scaledNetworkFI0950Iteration = -1;
						int scaledNetworkFI0975Iteration = -1;
						int scaledNetworkFI0950Phase = -1;
						int scaledNetworkFI0975Phase = -1;
						int scaledNetworkFI0950Communication = -1;
						int scaledNetworkFI0975Communication = -1;
						double FI02 = 0;
						double FI06 = 0;
						double FI10 = 0;
						numberOfIterationsExecuted = 0;
						numberOfTotalPhases = 0;
						numberOfTotalCommunications = 0;
						numberOfTotalExecutions = 0;
						randomNumberGenerator = new Random(1);		// Random number generator
						biddingAggressiveness = 0;
						outputStreams[0].printf(" %1.5f", scaledAgentFairnessIndex());
						outputStreams[1].printf(" %1.5f", scaledNetworkAgentFairnessIndex());
						outputStreams[2].printf(" %7s", 0);
						outputStreams[3].printf(" %7s", 0);
						outputStreams[4].printf(" %7s", 0);
						outputStreams[5].printf(" %7s", 0);
						if (trialNumber == 1) {
							testDistributionOutputStream.println(printStatsAssignments((int) testArray[testWalker] + "_" + algorithm + "_" + 0, false));
							printAgentsGraph(2, "_" + (int) testArray[testWalker] + "_" + algorithm + "_" + 0);
						}
						for (int i = 1; i < numberOfIterations && (k == 0 || maxRecoveryTime <= 0 || i <= maxRecoveryTime); i++) {
							tempResults = algorithmCaller(m);
							if (trialNumber == 1) {
								testDistributionOutputStream.println(printStatsAssignments((int) testArray[testWalker] + "_" + algorithm + "_" + i, false));
								printAgentsGraph(2, "_" + (int) testArray[testWalker] + "_" + algorithm + "_" + i);
							}
							outputStreams[0].printf(" %1.5f", scaledAgentFairnessIndex());
							double scaledNetworkAgentFairnessIndex = scaledNetworkAgentFairnessIndex();
							outputStreams[1].printf(" %1.5f", scaledNetworkAgentFairnessIndex);
							outputStreams[2].printf(" %7s", Math.max(tempResults, 0));
							outputStreams[3].printf(" %7s", numberOfPhases);
							outputStreams[4].printf(" %7s", numberOfCommunications);
							outputStreams[5].printf(" %7s", numberOfOffers);
							numberOfTotalPhases += numberOfPhases;
							numberOfTotalCommunications += numberOfCommunications;
							if (i == 2) {
								FI02 = scaledNetworkAgentFairnessIndex;
							}
							if (i == 6) {
								FI06 = scaledNetworkAgentFairnessIndex;
							}
							if (i == 10) {
								FI10 = scaledNetworkAgentFairnessIndex;
							}
							if (scaledNetworkAgentFairnessIndex >= 0.95 && !is0950Set) {
								is0950Set = true;
								scaledNetworkFI0950Iteration = i + 1;
								scaledNetworkFI0950Phase = numberOfTotalPhases;
								scaledNetworkFI0950Communication = numberOfTotalCommunications;
							}
							if (scaledNetworkAgentFairnessIndex >= 0.975 && !is0975Set) {
								is0975Set = true;
								scaledNetworkFI0975Iteration = i + 1;
								scaledNetworkFI0975Phase = numberOfTotalPhases;
								scaledNetworkFI0975Communication = numberOfTotalCommunications;
							}
							if (tempResults > 0)
								numberOfTotalExecutions += tempResults;
							if (tempResults == 0) {
								double scaledAgentFairnessIndex = scaledAgentFairnessIndex();
								scaledNetworkAgentFairnessIndex = scaledNetworkAgentFairnessIndex();
								for (int j = i + 1; j < numberOfIterations && (k == 0 || maxRecoveryTime <= 0 || j <= maxRecoveryTime); j++) {
									if (j == 2) {
										FI02 = scaledNetworkAgentFairnessIndex;
									}
									if (j == 6) {
										FI06 = scaledNetworkAgentFairnessIndex;
									}
									if (j == 10) {
										FI10 = scaledNetworkAgentFairnessIndex;
									}
									outputStreams[0].printf(" %1.5f", scaledAgentFairnessIndex);
									outputStreams[1].printf(" %1.5f", scaledNetworkAgentFairnessIndex);
									outputStreams[2].printf(" %7s", 0);
									outputStreams[3].printf(" %7s", 0);
									outputStreams[4].printf(" %7s", 0);
									outputStreams[5].printf(" %7s", 0);
									if (trialNumber == 1) {
										testDistributionOutputStream.println(printStatsAssignments((int) testArray[testWalker] + "_" + algorithm + "_" + j, false));
									}
								}
								break; 			// break out of for loop since already executed
							}
							numberOfIterationsExecuted++;
						}
						if ((k == 0 || maxRecoveryTime <= 0) && (m < 5 || m > 7)) {
							tempResults = algorithmCaller(m);
							while (tempResults != 0) {
								numberOfIterationsExecuted++;
								if (!is0950Set && scaledNetworkAgentFairnessIndex() >= 0.95) {
									is0950Set = true;
									scaledNetworkFI0950Iteration = numberOfIterationsExecuted;
								}
								if (!is0975Set && scaledNetworkAgentFairnessIndex() >= 0.975) {
									is0975Set = true;
									scaledNetworkFI0975Iteration = numberOfIterationsExecuted;
								}
								numberOfTotalPhases += numberOfPhases;
								numberOfTotalCommunications += numberOfCommunications;
								if (tempResults > 0)
									numberOfTotalExecutions += tempResults;
								tempResults = algorithmCaller(m);
							}
						}
						if (k == 0) {
							outputStreams[0].printf(" %1.5f", scaledAgentFairnessIndex());
							outputStreams[1].printf(" %1.5f", scaledNetworkAgentFairnessIndex());
							outputStreams[2].printf(" %7s", 0);
							outputStreams[3].printf(" %7s", 0);
							outputStreams[4].printf(" %7s", 0);
							outputStreams[5].printf(" %7s", 0);
						}
						if (trialNumber == 1) {
							testDistributionOutputStream.println(printStatsAssignments((int) testArray[testWalker] + "_" + algorithm + "_" + numberOfIterations, false));
							printAgentsGraph(2, "_" + (int) testArray[testWalker] + "_" + algorithm + "_" + numberOfIterations);
						}
						outputStreams[6].printf(" %1.5f", scaledAgentFairnessIndex());
						outputStreams[7].printf(" %1.5f", scaledNetworkAgentFairnessIndex());
						outputStreams[8].printf(" %7s", numberOfTotalExecutions);
						outputStreams[9].printf(" %7s", numberOfIterationsExecuted);
						outputStreams[10].printf(" %7s", numberOfTotalPhases);
						outputStreams[11].printf(" %7s", numberOfTotalCommunications);
						outputStreams[12].printf(" %7s", scaledNetworkFI0950Iteration);
						outputStreams[13].printf(" %7s", scaledNetworkFI0975Iteration);
						outputStreams[14].printf(" %7s", scaledNetworkFI0950Phase);
						outputStreams[15].printf(" %7s", scaledNetworkFI0975Phase);
						outputStreams[16].printf(" %7s", scaledNetworkFI0950Communication);
						outputStreams[17].printf(" %7s", scaledNetworkFI0975Communication);
						outputStreams[18].printf(" %7s", FI02);
						outputStreams[19].printf(" %7s", FI06);
						outputStreams[20].printf(" %7s", FI10);
						
					}
				}
			}
			for (int i = 0; i < outputStreams.length; i++)
				outputStreams[i].println("\n\n\n");
			// outputStreams[2].println("\n");
		}
		for (int i = 0; i < outputStreams.length; i++)
			outputStreams[i].close();	// close file output
		testDistributionOutputStream.close();
		System.out.println("\twrote to files"); 	// let user know results are written
		System.out.println("\t" + numberOfTrials + " trials were executed for " + testArray.length + " different variables in "
				+ ((new Date().getTime() - trialStartMilliseconds) / 1000 + 1) + " seconds");
		vocal = tempVocal;			// undo mute operations
	} // end test bidding algorithm

	public void useManualConnectionMatrixSetUp(int testVariable) {
		PrintWriter outputStream = null;
		try { // try to open the file of the computed file name
			outputStream = new PrintWriter(new FileOutputStream(IOTempConnectionMatrixFileName2));
		} catch (FileNotFoundException e) {
			System.out.println("\terror opening the file " + IOTempConnectionMatrixFileName2);
			System.exit(0);
		}
		int[][] dummyMatrix = new int[100][1000];
		if (testVariable == 0) {
			for (int i = 0; i < 100; i++) {
				double p = (double) 100 / 1000;
				for (int j = 0; j < 1000; j++) {
					if (Math.random() <= p) {
						dummyMatrix[i][j] = 1;
					} else {
						dummyMatrix[i][j] = 0;
					}
					p = p - (double) 0.1 / 1001;
					if (p < 0) {
						System.out.println();
						System.out.println("exiting... p < 0 at j = " + j);
						System.exit(1);
					}
				}
			}
		}
		if (testVariable == 1) {
			int[][] outputTransverse = new int[1000][100];
			double p = (double) 10 / 100;
			for (int i = 0; i < 1000; i++) {
				p = (double) 10 / 100;
				for (int j = 0; j < 100; j++) {
					if (Math.random() <= p) {
						outputTransverse[i][j] = 1;
					} else {
						outputTransverse[i][j] = 0;
					}
					p = p - (double) 0.1 / 101;
					if (p < 0) {
						System.out.println();
						System.out.println("exiting... p < 0 at j = " + j);
						System.exit(1);
					}
				}

			}
			for (int i = 0; i < 100; i++) {
				for (int j = 0; j < 1000; j++) {
					dummyMatrix[i][j] = outputTransverse[j][i];
				}
			}
		}
		if (testVariable == 2) {
			for (int i = 0; i < 100; i++) {
				double p1 = (double) (100 - i) / 2000;
				for (int j = 0; j < 1000; j++) {
					double p2 = (double) (1000 - j) / 20000;
					if (Math.random() <= p1 + p2) {
						dummyMatrix[i][j] = 1;
					} else {
						dummyMatrix[i][j] = 0;
					}
					if (p1 + p2 < 0) {
						System.out.println();
						System.out.println("exiting... p < 0 at i = " + i + ", j = " + j);
						System.exit(1);
					}
				}
			}
		}
		int[][] currentMatrix = dummyMatrix;
		for (int i = 0; i < numrows * numcols; i++) {
			if (currentMatrix == pathMatrix) {
				if (i == 0) { // the first row of the path matrix is special
					outputStream.print("    |");
					for (int j = 0; j < numrows * numcols; j++)
						outputStream.printf("A%3d|", j);
					outputStream.println();
				} // print out the rest of the path matrix with '|' in-between
				outputStream.printf("A%3d|", i);
				for (int j = 0; j < numrows * numcols; j++)
					outputStream.printf("%4d|", currentMatrix[i][j]);
			} else if (currentMatrix[0].length == maxID + 1)// matrixCounter==0||matrixCounter==2||matrixCounter==3||matrixCounter==10)
			{ // the matrices of 0, 1, and 3 are numrows*numcols by maxID+1
				for (int j = 0; j < maxID + 1; j++)
					outputStream.print(currentMatrix[i][j]);
			} else if (currentMatrix[0].length == numrows * numcols)// matrixCounter==2||matrixCounter==5) // else simply print out the reach matrix
				for (int j = 0; j < numrows * numcols; j++)
					outputStream.print(currentMatrix[i][j]);
			outputStream.print(" " + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent);
			outputStream.println();
		}
		outputStream.close(); // close file and update user
		useConnectionInput(IOTempConnectionMatrixFileName2);
		// System.out.println("\twrote to file: " + IOTempConnectionMatrixFileName2);
	}

	// ** Update Min and Max Fairness Indexes *******************************************************
	public boolean updateMinMaxFairnessIndexes() {
		int numagents = numrows * numcols;
		minScaledFI = numagents > 0 ? (double) 1 / (numagents) : 0;
		double maxScaledFINumerator = ((numagents - N % numagents) * (N / numagents) + (double) (N % numagents) * (N / numagents + 1));
		maxScaledFINumerator = maxScaledFINumerator * maxScaledFINumerator;
		double maxScaledFIDenominator = (numagents * ((numagents - N % numagents) * (N / numagents) * (N / numagents) + (N % numagents)
				* (N / numagents + 1) * (N / numagents + 1)));
		maxScaledFI = (N == 0 || numagents == 0) ? 1 : maxScaledFINumerator / maxScaledFIDenominator;
		boolean tempVocal = vocal;
		vocal = false;
		printMatrices(15);
		worst();
		minNetworkFI = scaledAgentFairnessIndexFromScratch();
		// System.out.println("minscalednetworkfi = " + scaledAgentFairnessIndex());
		boolean tempShowAlgorithmVisual = showAlgorithmVisual;
		showAlgorithmVisual = false;
		while (batchCascadedImprovementPath(true) != 0);
		showAlgorithmVisual = tempShowAlgorithmVisual;
		maxNetworkFI = scaledAgentFairnessIndexFromScratch();
//		vocal = tempVocal;
//		 toString(assignmentMatrix);
		// System.out.println("maxscalednetworkfi = " + scaledAgentFairnessIndex());
		useAssignmentInput(IOTempAssignmentMatrixFileName3);
		vocal = tempVocal;
		return true;
	}	// end Update Min and Max Fairness Indexes

	// ** Compute and Print the Fairness Indices ****************************************************
	public double fairnessIndex(int type, PrintWriter fileStream, boolean printToScreen) {		// 0 is nothing, 1 is network, 2 is scaled, 3 is regular
		int numagents = numrows * numcols;
		minScaledFI = numagents > 0 ? (double) 1 / (numagents) : 0;
		double maxScaledFINumerator = ((numagents - N % numagents) * (N / numagents) + (double) (N % numagents) * (N / numagents + 1));
		maxScaledFINumerator = maxScaledFINumerator * maxScaledFINumerator;
		double maxScaledFIDenominator = (numagents * ((numagents - N % numagents) * (N / numagents) * (N / numagents) + (N % numagents)
				* (N / numagents + 1) * (N / numagents + 1)));
		maxScaledFI = (N == 0 || numagents == 0) ? 1 : maxScaledFINumerator / maxScaledFIDenominator;

		double maxNumberOfAssignedResources = 0; // initalize each component of each fairness index to 0
		double numerator = 0, denominator = 0;
		double fairnessIndex = 0, scaledFairnessIndex = 0, networkFI = 0;
		// in general, the minimum fairness index is 1/numagents
		// unless the number of agents is equal to 0

		// compute fairness index
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {							// sum the numerator and denominator for each agent
				numerator += agents[i][j].numberOfResourcesAssignedToAgent;
				denominator += Math.pow(agents[i][j].numberOfResourcesAssignedToAgent, 2);
				// compute the maximum number of agents to later print out
				// a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
				if (agents[i][j].numberOfResourcesAssignedToAgent > maxNumberOfAssignedResources)
					maxNumberOfAssignedResources = agents[i][j].numberOfResourcesAssignedToAgent;
			}
		}
		numerator = numerator * numerator; // the numerator is squared
		denominator = denominator * numagents; // the denominator is multiplied by the number of agents
		fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
		// compute the scaled fairness index relative to the fairness index
		// unless something weird has occurred,
		// the scaled fairness index is (FI-minFI)/(maxFI-minFI);
		if (type == 3)
			return fairnessIndex;
		scaledFairnessIndex = minScaledFI >= maxScaledFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minScaledFI)
				/ (maxScaledFI - minScaledFI);
		if (type == 2)
			return scaledFairnessIndex;
		networkFI = minNetworkFI >= maxNetworkFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minNetworkFI) / (maxNetworkFI - minNetworkFI);
		if (type == 1)
			return networkFI;
		if (printToScreen) {								// print out each fairness index
			// System.out.printf("\tnetworkFI = %1.3f; scaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", networkFI, scaledFairnessIndex, fairnessIndex, numagents, N);
			System.out.printf("\tscaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", scaledFairnessIndex, fairnessIndex, numagents, N);
		} else {
			// fileStream.printf("\tnetworkFI = %1.3f; scaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", networkFI, scaledFairnessIndex, fairnessIndex, numagents, N);
			fileStream.printf("\tscaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", scaledFairnessIndex, fairnessIndex, numagents, N);
		}
		// if (scaledFairnessIndex<1) // let user know if the solution is not optimal
		// System.out.println("\tscaled fairness index is less than 1 with numagents = "+numagents+", N = "+N);
		// print out how many agents have how many resource
		// e.g. a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
		for (int k = (int) maxNumberOfAssignedResources; k >= 0; k--) {
			int agentCounter = 0;
			for (int i = 0; i < numrows; i++)
				for (int j = 0; j < numcols; j++)
					if (agents[i][j].numberOfResourcesAssignedToAgent == k)
						agentCounter++;
			if (printToScreen)
				System.out.print(agentCounter + "x" + k + (k == 0 ? "" : "+"));
			else
				fileStream.print(agentCounter + "x" + k + (k == 0 ? "" : "+"));
		}
		if (printToScreen)
			System.out.print("\n");
		else
			fileStream.print("\n");
		return 0;
	} // end print fairness index
	
	public double fairnessIndexWithWeightedAgents2(int type, PrintWriter fileStream, boolean printToScreen) {		// 0 is nothing, 1 is network, 2 is scaled, 3 is regular
		double numagents = 0;
		double numerator = 0;
		double denominator = 0;
		for (int i = 0; i < numrows * numcols; i++) {
			numerator += agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
			denominator += agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent * agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent / (double) agentsWeight[i];
			numagents += agentsWeight[i];
		}
//		for (int i = 0; i < numrows * numcols; i++) {
//			print(" " + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent);
//		}
//		println();
//		println("numerator = " + numerator + ", denominator = " + denominator + ", numagents = " + numagents);
		return (numerator * numerator) / ((double) (numagents * denominator));
	}
	
	// ** Compute and Print the Fairness Indices ****************************************************
		public double fairnessIndexWithWeightedAgents(int type, PrintWriter fileStream, boolean printToScreen) {		// 0 is nothing, 1 is network, 2 is scaled, 3 is regular
			double numagents = 0;
			double numerator = 0;
			double denominator = 0;
			for (int i = 0; i < numrows * numcols; i++) {
				numerator += agentsWeight[i] * agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
				denominator += agentsWeight[i] * agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent * agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent;
				numagents += agentsWeight[i];
			}
//			for (int i = 0; i < numrows * numcols; i++) {
//				print(" " + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent);
//			}
//			println();
//			println("numerator = " + numerator + ", denominator = " + denominator + ", numagents = " + numagents);
			return (numerator * numerator) / ((double) (numagents * denominator));
			
//			minScaledFI = numagents > 0 ? (double) 1 / (numagents) : 0;
//			double maxScaledFINumerator = ((numagents - N % numagents) * (N / numagents) + (double) (N % numagents) * (N / numagents + 1));
//			maxScaledFINumerator = maxScaledFINumerator * maxScaledFINumerator;
//			double maxScaledFIDenominator = (numagents * ((numagents - N % numagents) * (N / numagents) * (N / numagents) + (N % numagents)
//					* (N / numagents + 1) * (N / numagents + 1)));
//			maxScaledFI = (N == 0 || numagents == 0) ? 1 : maxScaledFINumerator / maxScaledFIDenominator;
//
//			double maxNumberOfAssignedResources = 0; // initalize each component of each fairness index to 0
//			double numerator = 0, denominator = 0;
//			double fairnessIndex = 0, scaledFairnessIndex = 0, networkFI = 0;
//			// in general, the minimum fairness index is 1/numagents
//			// unless the number of agents is equal to 0
//
//			// compute fairness index
//			for (int i = 0; i < numrows; i++) {
//				for (int j = 0; j < numcols; j++) {							// sum the numerator and denominator for each agent
//					numerator += agents[i][j].numberOfResourcesAssignedToAgent;
//					denominator += Math.pow(agents[i][j].numberOfResourcesAssignedToAgent, 2);
//					// compute the maximum number of agents to later print out
//					// a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
//					if (agents[i][j].numberOfResourcesAssignedToAgent > maxNumberOfAssignedResources)
//						maxNumberOfAssignedResources = agents[i][j].numberOfResourcesAssignedToAgent;
//				}
//			}
//			numerator = numerator * numerator; // the numerator is squared
//			denominator = denominator * numagents; // the denominator is multiplied by the number of agents
//			fairnessIndex = denominator != 0 ? numerator / denominator : 1; // compute the fairness index
//			// compute the scaled fairness index relative to the fairness index
//			// unless something weird has occurred,
//			// the scaled fairness index is (FI-minFI)/(maxFI-minFI);
//			if (type == 3)
//				return fairnessIndex;
//			scaledFairnessIndex = minScaledFI >= maxScaledFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minScaledFI)
//					/ (maxScaledFI - minScaledFI);
//			if (type == 2)
//				return scaledFairnessIndex;
//			networkFI = minNetworkFI >= maxNetworkFI || fairnessIndex == 1 ? 1 : (fairnessIndex - minNetworkFI) / (maxNetworkFI - minNetworkFI);
//			if (type == 1)
//				return networkFI;
//			if (printToScreen) {								// print out each fairness index
//				// System.out.printf("\tnetworkFI = %1.3f; scaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", networkFI, scaledFairnessIndex, fairnessIndex, numagents, N);
//				System.out.printf("\tscaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", scaledFairnessIndex, fairnessIndex, numagents, N);
//			} else {
//				// fileStream.printf("\tnetworkFI = %1.3f; scaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", networkFI, scaledFairnessIndex, fairnessIndex, numagents, N);
//				fileStream.printf("\tscaledFI = %1.3f; FI = %1.3f; %3dx%4d; ", scaledFairnessIndex, fairnessIndex, numagents, N);
//			}
//			// if (scaledFairnessIndex<1) // let user know if the solution is not optimal
//			// System.out.println("\tscaled fairness index is less than 1 with numagents = "+numagents+", N = "+N);
//			// print out how many agents have how many resource
//			// e.g. a0xn + a1x(n-1) + a2x(n-2) + a3x(n-3) + ... + anx0
//			for (int k = (int) maxNumberOfAssignedResources; k >= 0; k--) {
//				int agentCounter = 0;
//				for (int i = 0; i < numrows; i++)
//					for (int j = 0; j < numcols; j++)
//						if (agents[i][j].numberOfResourcesAssignedToAgent == k)
//							agentCounter++;
//				if (printToScreen)
//					System.out.print(agentCounter + "x" + k + (k == 0 ? "" : "+"));
//				else
//					fileStream.print(agentCounter + "x" + k + (k == 0 ? "" : "+"));
//			}
//			if (printToScreen)
//				System.out.print("\n");
//			else
//				fileStream.print("\n");
//			return 0;
		} // end print fairness index

	// ** Return Weather a Small World has Occurred by checking the Connection Matrix Stack *********
	public boolean checkSmallWorld(int[][] connectionMatrixStack) {
		for (int i = 0; i < numrows * numcols; i++) {
			for (int j = 0; j < numrows * numcols; j++) {
				if (connectionMatrixStack[i][j] == 0) {
					return false;
				}
			}
		}
		return true;
	} 	// end check small world

	// public int factorial(int n) {
	// if (n < 2)
	// return 1;
	// else
	// return n * factorial(n - 1);
	// }

	public int choose(int n, int k) {
		if (k == 0)
			return 1;
		if (n == 0)
			return 0;
		return choose(n - 1, k - 1) + choose(n - 1, k);
		// return (int) ((double) factorial(n) / (double) factorial(n - k) / (double) factorial(k));
	}

	public double standardDeviationCalculate(double[] data) {
		final int n = data.length;
		if (n < 2) {
			return Double.NaN;
		}
		double avg = data[0];
		double sum = 0;
		for (int i = 1; i < data.length; i++) {
			double newavg = avg + (data[i] - avg) / (i + 1);
			sum += (data[i] - avg) * (data[i] - newavg);
			avg = newavg;
		}
		// Change to ( n - 1 ) to n if you have complete data instead of a sample.
		return Math.sqrt(sum / (n));
	}

	// Multiply Matrix, but the dimensions has to be the same due to auto transpose ****************
	// In other words, this method multiply integer matrices that is MxN and OxN and result in MxO
	// as OPPOSED to MxN and NxO and result in MxO
	public int[][] matrixMultiply(int[][] a, int[][] b) {
		if (a[0].length != b[0].length)	// check if the dimensions match
		{
			System.out.println("\ta[0].length = " + a[0].length + " while b[0].length = " + b[0].length);
			return null;
		}
		int[][] product = new int[a.length][b.length];
		for (int i = 0; i < a.length; i++) {								// for each element in the product
			for (int j = 0; j < b.length; j++) {
				for (int k = 0; k < a[0].length; k++) {						// multiple the rows
					if (a[i][k] * b[j][k] > 0) {					// the max of the matrix product is 0
						product[i][j] = 1;
						break;			// break to compute the next j
					}
				}
			}
		}
		return product;					// return the product
	} // end matrix multiply

	public void toString(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j]);
			}
			if (matrix == connectionMatrix) {
				System.out.print(" " + agents[i / numcols][i % numcols].numberOfResourcesConnectedToAgent);
			}
			if (matrix == assignmentMatrix) {
				System.out.print(" " + agents[i / numcols][i % numcols].numberOfResourcesAssignedToAgent);
			}
			System.out.println();
		}
	}
	
	public void toString(int[] array) {
//		int max = 0;
//		for (int i = 0; i < array.length; i++) {
//			if (array[i] > max) {
//				max = array[i];
//			}
//		}
//		int[] arrayToPrint = new int[max + 1];
//		for (int i = 0; i < array.length; i++) {
//			arrayToPrint[array[i]]++;
//		}
//		for (int i = 0; i < arrayToPrint.length; i++) {
//			System.out.print(arrayToPrint[i] + (i == arrayToPrint.length - 1 ? "" : ", "));
//		}
//		System.out.println();
				for (int i = 0; i < array.length; i++) {
					System.out.print(array[i] + (i == array.length - 1 ? "" : ", "));
				}
				System.out.println();
	}

	public void toString(double[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + (i == array.length - 1 ? "" : ", "));
		}
		System.out.println();
	}
	
	public int max(int[] array) {
		int max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}
		return max;
	}
	
	public int[] array(int length) {
		int[] returnValue = new int[length];
		for (int i = 0; i < returnValue.length; i++) {
			returnValue[i] = i;
		}
		return returnValue;
	}
	
	public int numberOfNeighboringAgents(int i) {
		int returnValue = 0;
		for (int j = 0; j < numrows * numcols; j++) {
			returnValue += adjacencyMatrix[i][j];
		}
		return returnValue - 1;
	}
	
	double percentile(ArrayList<Integer> a, double percentile) {
		
		Collections.sort(a);
		double rank = (percentile * (a.size() + 1)) - 1;
		return (Math.floor(rank) < 0 ? a.get((int) Math.floor(rank + 1)) : a.get((int) Math.floor(rank))) * (Math.ceil(rank) - rank) + (Math.ceil(rank) > a.size() - 1 ? a.get((int) Math.floor(rank)) : a.get((int) Math.ceil(rank))) * (1 - Math.ceil(rank) + rank);
//
//			System.out.println(rank);
//			System.out.println(Math.floor(rank) < 0 ? a.get((int) Math.floor(rank + 1)) : a.get((int) Math.floor(rank)));
////			System.out.println(a.get((int) Math.floor(rank)));
//			System.out.println((Math.ceil(rank) - rank));
//			
//			System.out.println((Math.ceil(rank) > a.size() - 1 ? a.get((int) Math.floor(rank)) : a.get((int) Math.ceil(rank))));
//			System.out.println((1 - Math.ceil(rank) + rank));
//		Collections.sort(a);
//		double position = (percentile * (a.size() + 1)) - 1;
//		return a.get((int) position) * (Math.ceil(position) - position) + a.get((int) Math.ceil(position)) * (1 - Math.ceil(position) + position);
	}
	
	double min(ArrayList<Integer> a) {
		Collections.sort(a);
		return a.get(0);
	}
	
	double max(ArrayList<Integer> a) {
		Collections.sort(a);
		return a.get(a.size() - 1);
	}
	
	public void print(String s) {
		System.out.print(s);
	}
	
	public <T> void print(T s) {
		System.out.print("" + s);
	}
	
	public void println() {
		System.out.println();
	}
	
	public void println(int i) {
		System.out.println(i);
	}
	
	public void println(String s) {
		System.out.println(s);
	}

	// ** Help Menu *********************************************************************************
	public void help() {
		System.out
				.println("  \tmembers i j: will print a list of IDs of resources connected to the agent(i,j)"
						+ "\n\tpopulation: will print out the population of resources on the entire grid"
						+ "\n\tconnection i j: will print the number of resources connected to the agent(i,j)"
						+ "\n\twhereIs x: will print the agents that is connected to the resource x"
						+ "\n\tmove x i j: will reconnect the resource x from wherever to agent(i,j)"
						+ "\n\tcheckConnection OR checkContains x i j: will print whether resource x is connected to agent(i,j)"
						+ "\n\tkill x: will eliminate resource x from grid"
						+ "\n\tcreate i j: will create a new resource on agent(i,j)"
						+ "\n\tcreate: will create a new resource on a random location"
						+ "\n\tcheckContains x i j: will print whether resource x is connected to agent(i,j)"
						+ "\n\treset: will kill all resources and recreate the same amount on the grid"
						+ "\n\tprint: will print 1) bipartite graph, 2) manual location graph, 3) agent level graph, 4) connection matrix, 5) assignment matrix, 6) XOR matrix, 7) path matrix, and 8) reach matrix"
						+ "\n\tideal OR i: will create an ideal connection matrix using the number of agents, the number of resources, and the density to "
						+ IOSeededConnectionFileName + "\n\treconnect OR useConnectionInput: will modify the connection matrix from "
						+ connectionInputFileName + "\n\tiuse OR ri: will modify the connection matrix from " + IOSeededConnectionFileName
						+ "\n\treassign OR useAssignmentInput: will modify the assignment matrix from " + assignmentInputFileName
						+ "\n\tuseAll: will reconnect and reassign" + "\n\texecuteDifferenceOf2: attempt to execute a difference of 2"
						+ "\n\td2 OR executeDifferencesOf2: execute all differences of 2"
						+ "\n\texecuteImprovementPath: attempt to execute an improvement"
						+ "\n\ti2 OR executeImprovementPaths: execute all improvement paths"
						+ "\n\td OR debug: will print all global variables and their values"
						+ "\n\t'global variable' x: will change the global variable value to x"
						+ "\n\thelp: will print the list of commands" + "\n\tq OR quit: will terminate the program");
	} // end help menu

	/**
	 * <dl>
	 * <dt>Purpose: GraphViz Java API
	 * <dd>
	 *
	 * <dt>Description:
	 * <dd>With this Java class you can simply call dot from your Java programs
	 * <dt>Example usage:
	 * <dd>
	 *
	 * <pre>
	 * GraphViz gv = new GraphViz();
	 * gv.addln(gv.start_graph());
	 * gv.addln(&quot;A -&gt; B;&quot;);
	 * gv.addln(&quot;A -&gt; C;&quot;);
	 * gv.addln(gv.end_graph());
	 * System.out.println(gv.getDotSource());
	 *
	 * String type = &quot;gif&quot;;
	 * File out = new File(&quot;out.&quot; + type);   // out.gif in this example
	 * gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
	 * </pre>
	 *
	 * </dd>
	 *
	 * </dl>
	 *
	 * @version v0.4, 2011/02/05 (February) -- Patch of Keheliya Gallaba is added. Now you
	 *          can specify the type of the output file: gif, dot, fig, pdf, ps, svg, png, etc.
	 * @version v0.3, 2010/11/29 (November) -- Windows support + ability
	 *          to read the graph from a text file
	 * @version v0.2, 2010/07/22 (July) -- bug fix
	 * @version v0.1, 2003/12/04 (December) -- first release
	 * @author Laszlo Szathmary (<a href="jabba.laci@gmail.com">jabba.laci@gmail.com</a>)
	 */
	public class GraphViz {

		/**
		 * The dir. where temporary files will be created.
		 */
		// private static String TEMP_DIR = "/tmp"; // Linux
		private static final String TEMP_DIR = "C:/Temp";	// Windows

		/**
		 * Where is your dot program located? It will be called externally.
		 */
		// private static String DOT = "/usr/bin/dot"; // Linux
		// private static String DOT = "c:/Program Files/Graphviz2.26.3/bin/dot.exe"; // Windows
//		private static final String DOT = "C:/Program Files (x86)/Graphviz 2.28/bin/dot.exe"; // Windows 64 bit
		private final String DOT = System.getProperty("user.dir")+"/Graphviz 2.28/bin/dot.exe";

		/**
		 * The source of the graph written in dot language.
		 */
		private StringBuilder graph = new StringBuilder();

		/**
		 * Constructor: creates a new GraphViz object that will contain
		 * a graph.
		 */
		public GraphViz() {
		}

		/**
		 * Returns the graph's source description in dot language.
		 *
		 * @return Source of the graph in dot language.
		 */
		public String getDotSource() {
			return graph.toString();
		}

		/**
		 * Adds a string to the graph's source (without newline).
		 */
		public void add(String line) {
			graph.append(line);
		}

		/**
		 * Adds a string to the graph's source (with newline).
		 */
		public void addln(String line) {
			graph.append(line + "\n");
		}

		/**
		 * Adds a newline to the graph's source.
		 */
		public void addln() {
			graph.append('\n');
		}

		/**
		 * Returns the graph as an image in binary format.
		 *
		 * @param dot_source
		 *            Source of the graph to be drawn.
		 * @param type
		 *            Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
		 * @return A byte array containing the image of the graph.
		 */
		public byte[] getGraph(String dot_source, String type, String engine) {
			File dot;
			byte[] img_stream = null;

			try {
				dot = writeDotSourceToFile(dot_source);
				if (dot != null) {
					img_stream = get_img_stream(dot, type, engine);
					if (dot.delete() == false)
						System.err.println("Warning: " + dot.getAbsolutePath() + " could not be deleted!");
					return img_stream;
				}
				return null;
			} catch (java.io.IOException ioe) {
				return null;
			}
		}

		/**
		 * Writes the graph's image in a file.
		 *
		 * @param img
		 *            A byte array containing the image of the graph.
		 * @param file
		 *            Name of the file to where we want to write.
		 * @return Success: 1, Failure: -1
		 */
		public int writeGraphToFile(byte[] img, String file) {
			File to = new File(file);
			return writeGraphToFile(img, to);
		}

		/**
		 * Writes the graph's image in a file.
		 *
		 * @param img
		 *            A byte array containing the image of the graph.
		 * @param to
		 *            A File object to where we want to write.
		 * @return Success: 1, Failure: -1
		 */
		public int writeGraphToFile(byte[] img, File to) {
			try {
				FileOutputStream fos = new FileOutputStream(to);
				fos.write(img);
				fos.close();
			} catch (java.io.IOException ioe) {
				return -1;
			}
			return 1;
		}

		/**
		 * It will call the external dot program, and return the image in
		 * binary format.
		 *
		 * @param dot
		 *            Source of the graph (in dot language).
		 * @param type
		 *            Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
		 * @return The image of the graph in .gif format.
		 */
		private byte[] get_img_stream(File dot, String type, String engine) {
			File img;
			byte[] img_stream = null;

			try {
				img = File.createTempFile("graph_", "." + type, new File(GraphViz.TEMP_DIR));
				Runtime rt = Runtime.getRuntime();
				// String[] args = { DOT, "-Kneato", "-n2", "-T" + type, dot.getAbsolutePath(), "-o", img.getAbsolutePath() };
				String[] args = { DOT, "-K" + engine, "-n2", "-T" + type, dot.getAbsolutePath(), "-o", img.getAbsolutePath() };
				Process p = rt.exec(args);
				p.waitFor();
				FileInputStream in = new FileInputStream(img.getAbsolutePath());
				img_stream = new byte[in.available()];
				in.read(img_stream);
				if (in != null)
					in.close();
				if (img.delete() == false)
					System.err.println("Warning: " + img.getAbsolutePath() + " could not be deleted!");
			} catch (java.io.IOException ioe) {
				System.err.println("Error:    in I/O processing of tempfile in dir " + GraphViz.TEMP_DIR + "\n");
				System.err.println("       or in calling external command");
				ioe.printStackTrace();
			} catch (java.lang.InterruptedException ie) {
				System.err.println("Error: the execution of the external program was interrupted");
				ie.printStackTrace();
			}

			return img_stream;
		}

		/**
		 * Writes the source of the graph in a file, and returns the written file
		 * as a File object.
		 *
		 * @param str
		 *            Source of the graph (in dot language).
		 * @return The file (as a File object) that contains the source of the graph.
		 */
		private File writeDotSourceToFile(String str) throws java.io.IOException {
			File temp;
			try {
				new File(TEMP_DIR).mkdirs();
				temp = File.createTempFile("graph_", ".dot.tmp", new File(GraphViz.TEMP_DIR));
				FileWriter fout = new FileWriter(temp);
				fout.write(str);
				fout.close();
			} catch (Exception e) {
				System.err.println("Error: I/O error while writing the dot source to temp file!");
				return null;
			}
			return temp;
		}

		/**
		 * Returns a string that is used to start a graph.
		 *
		 * @return A string to open a graph.
		 */
		public String start_graph() {
			return "digraph G {";
		}

		/**
		 * Returns a string that is used to end a graph.
		 *
		 * @return A string to close a graph.
		 */
		public String end_graph() {
			return "}";
		}

		/**
		 * Read a DOT graph from a text file.
		 *
		 * @param input
		 *            Input text file containing the DOT graph
		 *            source.
		 */
		public void readSource(String input) {
			StringBuilder sb = new StringBuilder();
			try {
				FileInputStream fis = new FileInputStream(input);
				DataInputStream dis = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(dis));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				dis.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
			this.graph = sb;
		}
	} // end of class GraphViz

	// ** Resource Class ****************************************************************************
	public class Resource {

		private int ID;												// keep track of ID, negative ID means dead
		private double row;											// keep track of location
		private double column;
		private long assignedRow;
		private long assignedColumn;
		private Resource previousResource; 							// keep track of prev and next as with a DLL
		private Resource nextResource;

		// The next line of code does not work well because multiple reosurces are created, one for every agent that it is connected to
		// private int numberOfConnectedAgents;

		public Resource() 											// used when a new resource is declared
		{
			ID = 0;
			row = 0;
			column = 0;
			previousResource = null;
			nextResource = null;
		}

		public Resource(int a, double b, double c) {				// used when some values are given
			ID = a;
			row = b;
			column = c;
			assignedRow = -1;
			assignedColumn = -1;
		}

		public Resource(int a, double b, double c, long d, long e) {// use when all values are given
			ID = a;
			row = b;
			column = c;
			assignedRow = d;
			assignedColumn = e;
		}
	} // end resource class

	// ** Agent Class *******************************************************************************
	public class Agent {

		private int numberOfResourcesConnectedToAgent; 				// keep track of resources connected to agent
		private int numberOfResourcesAssignedToAgent;
		private Resource firstResource;				   				// keep track of head to print

		public Agent() {
			numberOfResourcesConnectedToAgent = 0;
			numberOfResourcesAssignedToAgent = 0;
			firstResource = null;
		}
	}	// end agent class
}	// end class
// **************************************************************************************************