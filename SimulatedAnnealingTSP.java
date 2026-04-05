// import libraries
import java.util.*;

public class SimulatedAnnealingTSP {

    // define city, and calculate distance
    static class City {
        double x, y;

        City(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double distanceTo(City other) {
            double distanceX = x - other.x;
            double distanceY = y - other.y;
            return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        }
    }

    // generate random cities and add to array list
    static List<City> generateRandomCities(int numOfCities, double maxCoordinate) {
        Random rand = new Random();
        List<City> cities = new ArrayList<>();

        for (int i = 0; i < numOfCities; i++) {
            double x = rand.nextDouble() * maxCoordinate;
            double y = rand.nextDouble() * maxCoordinate;

            City newCity = new City(x, y);
            cities.add(newCity);
        }

        return cities;
    }

    // calculate distance of total trip
    static double calculateTotalDistance(List<City> citiesPath) {
        double totalDistance = 0.0;

        for (int i = 0; i < citiesPath.size() - 1; i++) {
            City current = citiesPath.get(i);
            City next = citiesPath.get(i + 1);
            totalDistance += current.distanceTo(next);
        }

        // add last city distance
        City last = citiesPath.get(citiesPath.size() - 1);
        City first = citiesPath.get(0);
        totalDistance += last.distanceTo(first);

        return totalDistance;
    }

    // 4 operators to generate new neighbors/paths
    static List<City> generateNeighbor(List<City> currentPath, int[] operatorStats) {
        List<City> newPath = new ArrayList<>(currentPath);
        Random rand = new Random();

        int operatorChoice = rand.nextInt(4);
        operatorStats[operatorChoice]++; // track operator usage

        switch (operatorChoice) {
            case 0:
                swapOperator(newPath, rand);
                break;
            case 1:
                inverseOperator(newPath, rand);
                break;
            case 2:
                insertOperator(newPath, rand);
                break;
            case 3:
                insertSubrouteOperator(newPath, rand);
                break;
        }

        return newPath;
    }

    static void swapOperator(List<City> path, Random rand) {
        if (path.size() < 2) return;
        int i = rand.nextInt(path.size());
        int j = rand.nextInt(path.size());
        Collections.swap(path, i, j);
    }

    static void inverseOperator(List<City> path, Random rand) {
        if (path.size() < 2) return;
        int i = rand.nextInt(path.size());
        int j = rand.nextInt(path.size());

        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }

        if (i != j) {
            Collections.reverse(path.subList(i, j + 1));
        }
    }

    static void insertOperator(List<City> path, Random rand) {
        if (path.size() < 2) return;
        int fromIndex = rand.nextInt(path.size());
        int toIndex = rand.nextInt(path.size());

        if (fromIndex != toIndex) {
            City cityToMove = path.remove(fromIndex);
            if (toIndex > fromIndex) {
                toIndex--;
            }
            path.add(toIndex, cityToMove);
        }
    }

    static void insertSubrouteOperator(List<City> path, Random rand) {
        if (path.size() < 4) return;

        int size = path.size();
        int maxSubrouteSize = Math.max(2, size / 4); // limit subroute to 25%

        int start = rand.nextInt(size);
        int subrouteSize = 1 + rand.nextInt(Math.min(maxSubrouteSize, size - start));
        int end = Math.min(start + subrouteSize - 1, size - 1);

        // choose new position to place subroute
        int insertPoint;
        do {
            insertPoint = rand.nextInt(size);
        } while (insertPoint >= start && insertPoint <= end);

        // remove subroute
        List<City> subroute = new ArrayList<>();
        for (int i = end; i >= start; i--) {
            subroute.add(0, path.remove(i));
        }

        // shift insert point if after removed segment
        if (insertPoint > start) {
            insertPoint -= (end - start + 1);
        }

        path.addAll(insertPoint, subroute);
    }

    // simulated annealing core method
    static SimulationResult simulatedAnnealing(List<City> initialPath, double initialTemp, double coolingRate) {
        // initialize current and best paths
        List<City> currentPath = new ArrayList<>(initialPath);
        List<City> bestPath = new ArrayList<>(initialPath);
        double currentDistance = calculateTotalDistance(currentPath);
        double bestDistance = currentDistance;
        double temp = initialTemp;
        Random rand = new Random();

        // termination criteria
        int maxAcceptedSolutions = 1500;
        int maxNoImprovementLimit = 150000;
        int acceptedSolutionCount = 0;
        int noImprovementCounter = 0;
        double previousBestDistance = bestDistance;

        // stats
        int[] operatorStats = new int[4];
        int totalIterations = 0;
        int acceptedMoves = 0;
        int improvedMoves = 0;
        String[] operatorNames = {"Swap", "Inverse", "Insert", "Insert Subroute"};

        // main loop
        while (acceptedSolutionCount < maxAcceptedSolutions && noImprovementCounter < maxNoImprovementLimit) {
            List<City> neighbor = generateNeighbor(currentPath, operatorStats);
            double neighborDistance = calculateTotalDistance(neighbor);
            totalIterations++;

            // print every 100 iterations to track progress
            if (totalIterations % 100 == 0) {
                System.out.printf("[Iter %4d] Temp: %.2f | Best: %.4f | Current: %.4f\n",
                        totalIterations, temp, bestDistance, currentDistance);
            }

            boolean accepted = false;

            if (neighborDistance < currentDistance) {
                currentPath = neighbor;
                currentDistance = neighborDistance;
                accepted = true;
                improvedMoves++;
                acceptedSolutionCount++;

                if (neighborDistance < bestDistance) {
                    bestPath = new ArrayList<>(neighbor);
                    bestDistance = neighborDistance;
                    noImprovementCounter = 0;
                }
            } else {
                double acceptanceProbability = Math.exp((currentDistance - neighborDistance) / temp);
                if (acceptanceProbability > rand.nextDouble()) {
                    currentPath = neighbor;
                    currentDistance = neighborDistance;
                    accepted = true;
                    acceptedSolutionCount++;
                }
            }

            if (accepted) {
                acceptedMoves++;
            }

            if (Math.abs(bestDistance - previousBestDistance) < 1e-10) {
                noImprovementCounter++;
            } else {
                noImprovementCounter = 0;
                previousBestDistance = bestDistance;
            }

            // reduce temperature
            temp *= (1 - coolingRate);
        }

        // determine why loop ended
        String terminationReason = (acceptedSolutionCount >= maxAcceptedSolutions)
                ? "Max accepted solutions reached"
                : "No improvement limit reached";

        // return final result
        return new SimulationResult(
                bestPath,
                bestDistance,
                totalIterations,
                acceptedMoves,
                improvedMoves,
                operatorStats,
                operatorNames,
                terminationReason
        );
    }

    public static void main(String[] args) {
        System.out.println("\uD83D\uDD01 starting simulated annealing for tsp");

        int numberOfCities = 25;
        double maxCoordinate = 100.0;

        // st1 generate random cities
        List<City> cities = generateRandomCities(numberOfCities, maxCoordinate);
        Collections.shuffle(cities);  // start with a random path

        double initialTemp = 5000;
        double coolingRate = 0.003;

        // s2 run the algo
        SimulationResult result = simulatedAnnealing(cities, initialTemp, coolingRate);

        // s3 print results
        result.printStatistics();

        // s4: print the best path
        System.out.println("\nbest route:");
        for (int i = 0; i < result.bestPath.size(); i++) {
            City c = result.bestPath.get(i);
            System.out.printf("  %2d: (%.2f, %.2f)%n", i + 1, c.x, c.y);
        }
    }
}

// storing and printing simulation results
class SimulationResult {
    List<SimulatedAnnealingTSP.City> bestPath;
    double bestDistance;
    int totalIterations;
    int acceptedMoves;
    int improvedMoves;
    int[] operatorStats;
    String[] operatorNames;
    String terminationReason;

    // constructor
    SimulationResult(List<SimulatedAnnealingTSP.City> bestPath,
                     double bestDistance,
                     int totalIterations,
                     int acceptedMoves,
                     int improvedMoves,
                     int[] operatorStats,
                     String[] operatorNames,
                     String terminationReason) {
        this.bestPath = bestPath;
        this.bestDistance = bestDistance;
        this.totalIterations = totalIterations;
        this.acceptedMoves = acceptedMoves;
        this.improvedMoves = improvedMoves;
        this.operatorStats = operatorStats.clone();
        this.operatorNames = operatorNames;
        this.terminationReason = terminationReason;
    }

    // method to print results
    void printStatistics() {
        System.out.println("\n=== SIMULATION RESULTS ===");
        System.out.printf("Best route distance: %.4f%n", bestDistance);
        System.out.printf("Total iterations: %d%n", totalIterations);
        System.out.printf("Accepted moves: %d (%.2f%%)%n", acceptedMoves, (100.0 * acceptedMoves / totalIterations));
        System.out.printf("Improved moves: %d (%.2f%%)%n", improvedMoves, (100.0 * improvedMoves / totalIterations));
        System.out.printf("Termination reason: %s%n", terminationReason);

        System.out.println("\nOperator Usage:");
        int totalOps = Arrays.stream(operatorStats).sum();
        for (int i = 0; i < operatorStats.length; i++) {
            System.out.printf("  %s: %d times (%.2f%%)%n",
                    operatorNames[i],
                    operatorStats[i],
                    (100.0 * operatorStats[i] / totalOps));
        }
    }
}
