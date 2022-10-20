package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.String.format;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> list = new ArrayList();

        for (int i = 0; i < args.length; i = i + 2){
            if(args[i].equals("--data")){
                list=readFile(args[i+1]);
            }
        }

        outerloop:
        while(true){
            String option = printMenu();
            switch (option){
                case "0": break outerloop;
                case "1":
                    findAPersons(list, getInvertedIndexMap(list));
                    break;
                case "2":
                    printAllData(list);
                    break;
                default:
                    System.out.println("Incorrect option! Try again.");
                    break;
            }
        }
    }

    private static Map<String, List<Integer>> getInvertedIndexMap(List<String> list){
        Map<String, List<Integer>> invertedIndexMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        String[] personData;
        for(int i = 0; i < list.size(); i++){
            personData = list.get(i).split(" ");
            for(int j = 0; j < personData.length; j++){
                if(invertedIndexMap.get(personData[j])!=null) {
                    invertedIndexMap.get(personData[j]).add(i);
                } else {
                    invertedIndexMap.put(personData[j], new ArrayList(Arrays.asList(i)));
                }
            }
        }
        return invertedIndexMap;
    }

    private static List<String> readFile(String fileInPath) {
        List<String> list = new ArrayList();
        try {
            File file = new File(fileInPath);
            Scanner newFile = new Scanner(file);
            while (newFile.hasNextLine()) {
                list.add(newFile.nextLine());
            }
            newFile.close();
        } catch (FileNotFoundException e) {
            System.out.println(format("An error occurred: %s", e.getMessage()));
        }
        return list;
    }

    private static String printMenu(){
        System.out.println("=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");
        return sc.nextLine();
    }

    private static void printAllData(List<String> allPersons) {
        System.out.println("=== List of people ===");
        allPersons.stream().forEach(person -> System.out.println(person));
    }

    private static void findAPersons(List<String> allPersons, Map<String, List<Integer>> invertedIndexMap){
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String searchType = sc.nextLine();
        System.out.println("Enter a name or email to search all suitable people.");
        SearchStrategy searchStrategy = SearchFactory.create(searchType, Arrays.asList(sc.nextLine().split(" ")));
        searchStrategy.search(allPersons, invertedIndexMap);
    }
}

class SearchFactory {

    public static SearchStrategy create(String searchType, List<String> query) {

        switch (searchType) {
            case "ALL": return new AllSearchStrategy(query);
            case "ANY": return new AnySearchStrategy(query);
            case "NONE": return new NoneSearchStrategy(query);
        }
        return null;
    }
}

abstract class SearchStrategy {

    Set<Integer> listOfIndexes = new HashSet<>();
    List<String> query;

    public SearchStrategy(List<String> query) {
        this.query = query;
    }

    abstract void search(List<String> allPersons, Map<String, List<Integer>> invertedIndexMap);

}

class AllSearchStrategy extends SearchStrategy {


    public AllSearchStrategy(List<String> query) {
        super(query);
    }

    @Override
    void search(List<String> allPersons, Map<String, List<Integer>> invertedIndexMap) {
        query.stream().forEach(keyWord -> {
            if(invertedIndexMap.get(keyWord)!=null) {
                listOfIndexes.addAll(invertedIndexMap.get(keyWord));
                listOfIndexes.retainAll(invertedIndexMap.get(keyWord));
            }
        });
        if(listOfIndexes!=null) {
            System.out.println(format("%d persons found:", listOfIndexes.size()));
            listOfIndexes.stream().forEach(index -> System.out.println(allPersons.get(index)));
        } else System.out.println("No matching people found.");
    }
}

class AnySearchStrategy extends SearchStrategy  {

    public AnySearchStrategy(List<String> query) {
        super(query);
    }

    @Override
    void search(List<String> allPersons, Map<String, List<Integer>> invertedIndexMap) {
        query.stream().forEach(keyWord -> {
            if(invertedIndexMap.get(keyWord)!=null)
                listOfIndexes.addAll(invertedIndexMap.get(keyWord));

        });
        if(listOfIndexes!=null) {
            System.out.println(format("%d persons found:", listOfIndexes.size()));
            listOfIndexes.stream().forEach(index -> System.out.println(allPersons.get(index)));
        } else System.out.println("No matching people found.");
    }

}

class NoneSearchStrategy extends SearchStrategy  {

    public NoneSearchStrategy(List<String> query) {
        super(query);
    }

    @Override
    void search(List<String> allPersons, Map<String, List<Integer>> invertedIndexMap) {
        query.stream().forEach(keyWord -> {
            if(invertedIndexMap.get(keyWord)!=null)
                listOfIndexes.addAll(invertedIndexMap.get(keyWord));
              System.out.println("keyWord("+keyWord+") indexes:"+invertedIndexMap.get(keyWord));
              System.out.println("List:"+listOfIndexes.toString());

        });
        if(listOfIndexes!=null) {
            System.out.println(format("%d persons found:", allPersons.size()-listOfIndexes.size()));
            for(int i = 0; i < allPersons.size(); i++){
                if(!listOfIndexes.contains(i)){
                    System.out.println(allPersons.get(i));
                }
            }
        } else System.out.println("No matching people found.");
    }
}


