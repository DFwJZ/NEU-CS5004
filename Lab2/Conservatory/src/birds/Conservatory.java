package birds;

import java.util.*;

import static java.lang.String.valueOf;

public class Conservatory {
    private int numOfAviaries = 0;
    private int numOfBirds = 0;
    private final int maxNumOfAviaries = 20;
    private ArrayList<Aviary> aviaryArrayList;
    //Calculate what food needs to be kept and in what quantities
    private Map<Aviary,Map<Food, Integer>> foodMap;
    private Map<Aviary, Integer> locationMap;
    private Map<AviaryCategory,ArrayList<BirdType>> aviaryCategory;


    private void initializeAviaryCategory() {
        List<BirdType> flightlessNameLists = Arrays.asList(BirdType.Emus, BirdType.Moas, BirdType.Kiwis);
        List<BirdType> preyNameLists = Arrays.asList(BirdType.Hawks, BirdType.Eagles, BirdType.Osprey);
        List<BirdType> waterfowlNameLists = Arrays.asList(BirdType.Duck, BirdType.Swan, BirdType.Osprey);
        List<BirdType> otherNameLists = Arrays.asList(BirdType.Owls, BirdType.RoseRingParakeet, BirdType.GrayParrot,
                                                        BirdType.SulfurCrestedCockatoo, BirdType.GreatAuk,
                                                        BirdType.HornedPuffin, BirdType.AfricanJacana);
        aviaryCategory = new HashMap<>();
        aviaryCategory.put(AviaryCategory.Flightless, new ArrayList<>(flightlessNameLists));
        aviaryCategory.put(AviaryCategory.Prey, new ArrayList<>(preyNameLists));
        aviaryCategory.put(AviaryCategory.Waterfowl, new ArrayList<>(waterfowlNameLists));
        aviaryCategory.put(AviaryCategory.Other, new ArrayList<>(otherNameLists));
    }

    // create a conservatory with given arbitrary number of aviaries
    public Conservatory() {
        initializeAviaryCategory();
        this.locationMap = new HashMap<>();
        this.aviaryArrayList = new ArrayList<>();
        this.foodMap = new HashMap<>();
//        Aviary a1 = new Aviary(5, "Prey");
//        this.locationMap.put(a1, 1);
//        this.aviaryArrayList.add(a1);
//        this.numOfAviaries++;
//        this.locationMap.put(new Aviary(5, "Prey"), 3);
//        this.locationMap.put(new Aviary(5, "Prey"), 4);
//        this.locationMap.put(new Aviary(5, "Prey"), 5);

//        this.locationMap.put(new Aviary(3, "Parrots"), 1);


    }

   private Aviary isCategoryExisted(String category) {
       for (Aviary aviary : aviaryArrayList) {
           if (aviary.getCategory() == category) {
               if (aviary.getSize() < 5 ) {
                   return aviary;
               }
           }
       }
       return null;
   }

    //Add and Assign a bird to an aviary in the conservatory. Assignments must follow some criteria.
    public void assignBirds(Birds bird) throws Exception {
        //check if bird is extinct
        if (bird.isExtinct()) {
            throw new IllegalStateException("Extinct bird cannot be added to conservatory.");
        }

        // finding the existing bird aviary in the aviaryArrayList.
        String existingBirdCate = getCategory(bird);
        Aviary tempAviary = isCategoryExisted(existingBirdCate);
        // if same type of category aviary is found
        if (tempAviary != null) {
            // if the aviary not full
            if (tempAviary.addBird(bird)) {
                numOfBirds++;
                addFood(tempAviary, bird);
            }
        } else {
            if (isFullConservatory()) {
                System.out.println(("Conservatory is Full"));
            } else {
                tempAviary = new Aviary(5, existingBirdCate);
                aviaryArrayList.add(tempAviary);
                numOfBirds++;
                numOfAviaries++;
                locationMap.put(tempAviary, numOfAviaries);
                tempAviary.addBird(bird);
                addFood(tempAviary, bird);
            }
        }
    }

    public void addFood(Aviary aviary, Birds bird) {
        foodMap.computeIfAbsent(aviary, k -> new HashMap<>());
        for (int k = 0; k < bird.getFood().length; ++ k) {

            foodMap.get(aviary).merge(bird.getFood()[k], 1, Integer::sum);
        }
    }

    // get one of four category that the bird belongs to.
    private String getCategory(Birds bird) {
        String newBirdCate = null;
        for (Map.Entry<AviaryCategory,ArrayList<BirdType>> entry : aviaryCategory.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); ++ i) {
                if (entry.getValue().get(i).equals(bird.getBirdType())) {
                    newBirdCate = valueOf(entry.getKey());
                    return newBirdCate;
                }
            }
        }

        return null;
    }

    // check whether the conservatory is full
    // return true if full, otherwise return false
    private boolean isFullConservatory() {
        if (numOfAviaries == maxNumOfAviaries)
            return true;
        return false;
    }


    // Have a guest look up which aviary a bird is in
    // return aviary location
    public int getAviaryOfBirds(Birds bird) {
        //first find aviary according to birdCategory
        for (Aviary tempAviary : aviaryArrayList) {
            // if a correct type of existed aviary is found
            if (tempAviary.getCategory().equals(valueOf(bird.getBirdType()))) {
                // loop through birdList in this existing aviary to find the bird
                for (int j = 0; j < tempAviary.getBirdList().size(); ++j) {
                    if (bird.getID().equals(valueOf(tempAviary.getBirdList().get(j)))) {
                        System.out.println(bird.getID() + " is at" + locationMap.get(tempAviary));
                        return locationMap.get(tempAviary); // or return i ??
                    }
                }
            }
        }
        // find bird according to birdID in those aviaries
        // if not found, return -1;
        return -1;

    }

    //Print a sign for any given aviary that gives a description of the birds it houses and any interesting information that it may have about that animal.
    public void printSignForAviary(int aviaryIndex) {
        //
        if (aviaryIndex > aviaryArrayList.size()) {
            throw new IllegalStateException("At given location, no aviary found.");
        }

        Aviary tempAviary = aviaryArrayList.get(aviaryIndex);
        for (int i = 0; i < tempAviary.getSize(); ++ i) {
            Birds bird = tempAviary.getBirdList().get(i);
            System.out.println("bird " + i
                    + ", id: " + bird.getID()
                    + ", bird type: " + bird.getBirdType()
                    + ", at Aviary: " + locationMap.get(tempAviary)
                    + ", characteristics: " + Arrays.toString(bird.getCharacteristic())
                    + ", number of wings: " + bird.getNumberOfWings()
                    + ", favorite food: " + Arrays.toString(bird.getFood()));
        }

    }

    //Print a “map” that lists all the aviaries by location and the birds they house
    public void getMapOfAviaryAndBirdsInfo() {
        for (Map.Entry<Aviary, Integer> entry : this.locationMap.entrySet()) {
            Aviary currAviary = entry.getKey();
            System.out.println("location:" + entry.getValue() + "; "
                             + "aviary category: " + currAviary.getCategory());
            for (int i = 0; i < currAviary.getSize(); i++) {
                Birds currBird = currAviary.getBirdList().get(i);
                System.out.println("BirdID:" + currBird.getID() + ", "+ "BirdType: " + currBird.getBirdType() + "; ");
            }
            System.out.println("--------------------------------");
        }
    }

    //Print an index that lists all birds in the conservatory in alphabetical order and their location
    public void printIndex() {
        //loop through each aviary and add each bird in it to the map with their id and location
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<Aviary, Integer> entry : this.locationMap.entrySet()) {
            Integer currLocation = entry.getValue();
            Aviary currAviary = entry.getKey();
            for (int i = 0; i < currAviary.getSize(); i++) {
                Birds currBird = currAviary.getBirdList().get(i);
                String birdTypeAndID = "BirdType:" + currBird.getBirdType() + ", " + "BirdID:" + currBird.getID();
                map.put(birdTypeAndID, currLocation);
            }
        }
        //instantiate a treemap from map and print out all birds information and their location
        Map<String, Integer> treeMap = new TreeMap<>(map);
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + " ,BirdLocation: " + entry.getValue());
        }
    }

    public int getNumOfBirds() {
        int sum = 0;
        for (Map.Entry<Aviary, Integer> entry : locationMap.entrySet()) {
            sum += entry.getKey().getBirdList().size();
        }
        return numOfBirds = sum;
    }



    public int getNumOfAviaries() {
        return this.numOfAviaries = locationMap.size();
    }

    public void setNumOfAviaries(int numOfAviaries) {
        this.numOfAviaries = numOfAviaries;
    }

}
