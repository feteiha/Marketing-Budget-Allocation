import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


public class MBA {
	public int channelNumber;
	public Double totalBudget;
	public ArrayList<String> channelNames;
	public ArrayList<Double> channelROI;
	public ArrayList<Double> lb;
	public ArrayList<Double> ub;
	private ArrayList<ArrayList<Double>> population = new ArrayList<ArrayList<Double>>();
	private ArrayList<Double> fitness;
	private DecimalFormat dec = new DecimalFormat("#0.00");
	private int N = 15; // Population size
	private int numberOfIterations;
	private double b = 2;
	private Random random = new Random();
	private boolean isUniform;

	
	public MBA (int numberOfIterations, boolean isUniform) {
		this.numberOfIterations = numberOfIterations;
		this.isUniform = isUniform;
	}
	
	public void startGeneticAlgorithm() {
		initalizePopulation();
		for (int counter = 0 ; counter < numberOfIterations ; counter++)
		{
			checkAndCorrect();
			setFitness();
			ArrayList<ArrayList<Double>> children = makeTournament();
			if (isUniform)
				makeUniformMutation(children);		
			else 
				makeNonUniformMutation(children, counter, numberOfIterations, b);
			elitistReplacement(children);
		}
		checkAndCorrect();
		setFitness();
		printOutput();
	}

	private void initalizePopulation() {
		for (int i=0 ; i<N ; i++) {
			ArrayList<Double> chromosome = new ArrayList<Double>();
			for (int j=0 ; j<channelNumber ; j++){
				chromosome.add(generateRandom(j));
			}
			population.add(chromosome);
		}
	}
	
	private double generateRandom(int channelNumber) {
		return lb.get(channelNumber) + (ub.get(channelNumber) - lb.get(channelNumber)) * random.nextDouble();
	}
	private void checkAndCorrect() {
		
		for (int i=0 ; i<N ; i++) {
			while (getSum(population.get(i))> totalBudget) {
				int index = random.nextInt(channelNumber);
				population.get(i).set(index, generateRandom(index));
			}
		}
	}

	private double getSum(ArrayList<Double> ch) {
		double sum = 0.0;
		for (int i=0 ; i<ch.size() ; i++) {
			sum += ch.get(i);
		}
		return sum;
	}
	private void setFitness() {
		fitness = new ArrayList<Double>();
		for (int i=0 ; i<N ; i++) {
			fitness.add(getFitness(population.get(i)));
		}
	}

	private Double getFitness(ArrayList<Double> ch) {
		double fitness = 0.0;
		for (int i=0 ; i<ch.size() ; i++) {
			fitness += (ch.get(i) * channelROI.get(i) * 0.01);
		}
		return fitness;
	}
	
	private void elitistReplacement(ArrayList<ArrayList<Double>> newPopulation) {
		if (N%2 == 0) {
			int maxIndex = 0, secMaxIndex = 1;
			if (fitness.get(maxIndex) < fitness.get(secMaxIndex)) {
				maxIndex = 1;
				secMaxIndex = 0;
			}
			for (int i=2; i< N; i++) {
				if (fitness.get(i) > fitness.get(maxIndex)) {
					secMaxIndex = maxIndex;
					maxIndex = i;
				}
				else if (fitness.get(i)> fitness.get(secMaxIndex)) {
					secMaxIndex = i;
				}
			}
			newPopulation.add(population.get(maxIndex));
			newPopulation.add(population.get(secMaxIndex));
		}
		else {
			int maxIndex = 0;
			for (int i=0; i<N; i++) {
				if (fitness.get(i) > fitness.get(maxIndex))
					maxIndex = i;
			}
			newPopulation.add(population.get(maxIndex));
		}
		population = newPopulation;
	}

	private ArrayList<ArrayList<Double>> makeTournament() {
		ArrayList<ArrayList<Double>> newPopulation = new ArrayList<ArrayList<Double>>();
		int firstChoice = -1; int secondChoice = -1;
		int firstParent = -1; int secondParent = -1;
		double crossOverRate;
		int loopEnd;
		if (N%2 == 0) {
			loopEnd = N-2;
		}
		else {
			loopEnd = N-1;
		}
		while (newPopulation.size() < loopEnd) {
			firstChoice = random.nextInt(N);
			secondChoice = random.nextInt(N);
			while (firstChoice == secondChoice){
				secondChoice = random.nextInt(N);
			}
			firstParent = (fitness.get(firstChoice) > fitness.get(secondChoice))? firstChoice : secondChoice;
			
			do {
				firstChoice = random.nextInt(N);
				secondChoice = random.nextInt(N);
				while (firstChoice == secondChoice){
					secondChoice = random.nextInt(N);
				}
				secondParent = (fitness.get(firstChoice) > fitness.get(secondChoice))? firstChoice : secondChoice;
			} while (secondParent == firstParent);
			crossOverRate = random.nextDouble();
			if (crossOverRate <= 0.7) {
				makeCrossOver(newPopulation, population.get(firstParent), population.get(secondParent));
			} else {
				newPopulation.add(population.get(firstParent));
				newPopulation.add(population.get(secondParent));
			}
		}
		return newPopulation;
	}


	private void makeCrossOver(ArrayList<ArrayList<Double>> newPopulation, ArrayList<Double> firstParent, ArrayList<Double> secondParent) {
		int n1 = random.nextInt(channelNumber-1) + 1;
		int n2 = random.nextInt(channelNumber-1) + 1;
		while (n1 == n2) 
			n2 = random.nextInt(channelNumber-1) + 1;
		if (n1 > n2) {
			int temp = n2;
			n2 = n1;
			n1 = temp;
		}
		ArrayList<Double> firstChild = new ArrayList<Double>();
		ArrayList<Double> secondChild = new ArrayList<Double>();
		for (int i=0 ; i<channelNumber ; i++) {
			if (i < n1) {
				firstChild.add(firstParent.get(i));
				secondChild.add(secondParent.get(i));
			} else if (i < n2) {
				firstChild.add(secondParent.get(i));
				secondChild.add(firstParent.get(i));
			} else {
				firstChild.add(firstParent.get(i));
				secondChild.add(secondParent.get(i));
			}
		}
		newPopulation.add(firstChild);
		newPopulation.add(secondChild);		
	}

	private void makeUniformMutation(ArrayList<ArrayList<Double>> children) {
		double mutationRate;
		for (int i=0 ; i<children.size() ; i++) {
			for (int j=0 ; j<channelNumber ; j++) {
				mutationRate = random.nextDouble();
				if (mutationRate < 0.1) {
					double delta;
					double r1 = random.nextDouble();
					double r2 ;
					double x = children.get(i).get(j);
					if (r1 <= 0.5) {
						delta = x -  lb.get(j);						
						r2 = random.nextDouble() * delta;
						children.get(i).set(j, x - r2);
					}
					else {
						delta = ub.get(j) - x;
						r2 = random.nextDouble() * delta;
						children.get(i).set(j, x + r2);

					}
					
				}
					
			}
		}
		
	}

	private void makeNonUniformMutation(ArrayList<ArrayList<Double>> children, int t, int T, double b) {
		double mutationRate;
		for (int i=0 ; i<children.size() ; i++) {
			for (int j=0 ; j<channelNumber ; j++) {
				mutationRate = random.nextDouble();
				if (mutationRate < 0.1) {
					double delta;
					double r = random.nextDouble();
					double x = children.get(i).get(j);
					if (r <= 0.5) {
						delta = (x -  lb.get(j))* (1 - Math.pow(r, Math.pow((1-t/T),b)));
						children.get(i).set(j, x - delta);
					}
					else {
						delta = (ub.get(j) - x)* (1 - Math.pow(r, Math.pow((1-t/T),b)));
						children.get(i).set(j, x + delta);

					}
					
				}
					
			}
		}
		
	}
	
	
	public void printOutput() {
		System.out.println(getAnswer());
	}
	
	public double getBestFitness() {
		int maxFitnessIndex = 0;
		for (int i = 1 ; i<fitness.size() ; i++) {
			if (fitness.get(maxFitnessIndex) < fitness.get(i)) {
				maxFitnessIndex = i;
			}
		}
		return getFitness(population.get(maxFitnessIndex)); 
	}
	public String getAnswer() {
		int maxFitnessIndex = 0;
		for (int i = 1 ; i<fitness.size() ; i++) {
			if (fitness.get(maxFitnessIndex) < fitness.get(i)) {
				maxFitnessIndex = i;
			}
		}
		String output = "The final marketing budget allocation is:\n";
		for (int i=0 ; i<channelNumber ; i++) {
			output += channelNames.get(i) + " -> " + dec.format(population.get(maxFitnessIndex).get(i)) + "k\n";
		}
		output += "The total profit is " + dec.format(getFitness(population.get(maxFitnessIndex)))+"k\n";
		return output;
	}
}
