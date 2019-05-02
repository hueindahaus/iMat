package sample;

import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class FilterEngine { //filterEngine är till för att sortera/filtrera produkter och returnera listor av produkter

    IMatDataHandler database = IMatDataHandler.getInstance();
    private static FilterEngine singleton = null;
    private ProductCategory searchCategory = null;
    private String searchString;
    private boolean searchIsEcological = false;
    private int searchPriceMin = 0;
    private int searchPriceMax = 0;


    private FilterEngine(){

    }

    public static FilterEngine getInstance(){
        if(singleton == null){
            singleton = new FilterEngine();
        }
        return singleton;
    }
    

    public List<Product> filter(){                          //Detta är huvudmetoden i FilterEngine. Den returnerar en lista med produkter som är filtrerad utifrån vad man söker på/kategori mm
        List<Product> productList = new ArrayList<>();
        for(Product product: database.getProducts()){   //loopar genom
            findMatches(productList,product);
        }
        return productList;
    }


    private void findMatches(List<Product> productList, Product product){
        if(product.getCategory().equals(searchCategory) || searchCategory == null) {    //om kategorin matchar eller om kategorin inte är vald så går produkten vidare till nästa if-sats
            if (searchIsEcological == product.isEcological() || searchIsEcological == false) {
                if (searchPriceMax == 0 || isInSearchPriceRange(product)) {     //om maxpris = 0 eller om produkten är inom sökningen på prisintervallet så ska den gå vidare till nästa if-sats
                    if (searchString == null || product.getName().toLowerCase().contains(searchString.toLowerCase()) || product.getCategory().toString().toLowerCase().contains(searchString.toLowerCase())) {    //om sökrutan inte är skriven i eller om det som står i sökrutan matchar antingen kategori eller produktnamn så blir if-satsen true. Vi gör alla strings till lower case, för att enklare hitta produkter
                        productList.add(product);
                    }
                }
            }
        }
    }


    private boolean isInSearchPriceRange(Product product){
        return (searchPriceMin >= product.getPrice() && product.getPrice() <= searchPriceMax);
    }


    public void setSearchCategory(ProductCategory category){
        searchCategory = category;
    }

    public void setSearchString(String search){ searchString = search;}

    public void setSearchPriceMin(int minPrice){ searchPriceMin = minPrice;}

    public void setSearchPriceMax(int maxPrice){ searchPriceMax = maxPrice;}

    public void setSearchIsEcological(boolean isEcological){searchIsEcological = isEcological;}

    public void resetFilterEngine(){
        searchString = null;
        searchCategory = null;
        searchIsEcological = false;
        searchPriceMax = 0;
        searchPriceMin = 0;
    }



}