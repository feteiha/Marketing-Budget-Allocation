import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
/*
100
4
TV advertising 8
Google 12
Twitter 7
Facebook 11
2.7 58
20.5 x
x 18
10 x

 */
public class Main {

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in); 
		String input = "";
		DecimalFormat dec = new DecimalFormat("#0.00");
		System.out.println("Enter the marketing budget (in thousands):");
		Double totalBudget = Double.parseDouble(in.nextLine());
		System.out.println("\nEnter the number of marketing channels:");
		int channelNumber = Integer.parseInt(in.nextLine());
		ArrayList<String> channelNames = new ArrayList<String>();
		ArrayList<Double> channelROI = new ArrayList<Double>();
		System.out.println("\nEnter the name and ROI (in %) of each channel separated by space:");
		
		for (int i=0 ; i<channelNumber ; i++) {
			input = in.nextLine();
			String[] arrOfStr = input.split(" ", 0);
			int len = arrOfStr.length;
			channelROI.add(Double.parseDouble(arrOfStr[len-1]));
			StringBuilder name = new StringBuilder();
			for (int j=0 ; j<len-1 ; j++) {
				name.append(arrOfStr[j] + " ");
			}
			channelNames.add(name.toString());
		}
		
		ArrayList<Double> lb = new ArrayList<Double>();
		ArrayList<Double> ub = new ArrayList<Double>();
		System.out.println("\nEnter the lower (k) and upper bounds (%) of investment in each channel:\n(enter x if there is no bound)");
		
		for (int i=0 ; i<channelNumber ; i++) {
			input = in.nextLine();
			String[] arrOfStr = input.split(" ", 0);
			if (arrOfStr[0].equals("x"))
				lb.add(0.0);
			else 
				lb.add(Double.parseDouble(arrOfStr[0]));
			if (arrOfStr[1].equals("x"))
				ub.add(totalBudget);
			else 
				ub.add(Double.parseDouble(arrOfStr[1]) * totalBudget * 0.01);
		}
		in.close();

		FileWriter fw;
		if (!Files.exists(Paths.get("non uniform.txt"))) {
            new File("non uniform.txt").createNewFile();
        }
        fw = new FileWriter("non uniform.txt", false);
        System.out.println("\nPlease wait while running the GA...\n");
        
        double bestFitness = 0.0;
        String bestFitnessDistribution = "";
		for (int i=0 ; i<20 ; i++) {
			MBA mba = new MBA(100, false);
			mba.channelNames = channelNames;
			mba.channelNumber = channelNumber;
			mba.channelROI = channelROI;
			mba.lb = lb;
			mba.ub = ub;
			mba.totalBudget = totalBudget;
			mba.startGeneticAlgorithm();
			if (bestFitness<mba.getBestFitness()) {
				bestFitness = mba.getBestFitness();
				bestFitnessDistribution = mba.getAnswer();
			}
			fw.write("#" + (i+1) + ":\n");
			fw.write(mba.getAnswer());
			fw.write("____________________________\n");
		}
		System.out.println("____________________________\n");
		System.out.println("Best fitness = " + dec.format(bestFitness) + "\n");
		System.out.println(bestFitnessDistribution);
		fw.write("____________________________\n");
		fw.write("Best fitness = " + dec.format(bestFitness) + "\n");
		fw.write(bestFitnessDistribution);
		fw.close();
	}

}
