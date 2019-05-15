package sample;

import se.chalmers.cse.dat216.project.Product;

import java.util.ArrayList;
import java.util.List;

public class FavouriteManager {
    private List<String> favourites = new ArrayList<>();

    public void addFavourite(Product p){
        favourites.add(p.getName());
    }

    public void removeFavourite(Product p){
        favourites.remove(p.getName());
    }

    public List<String> getFavourites(){
        return favourites;
    }

    public boolean isFavourite(Product p){
        return favourites.contains(p.getName());
    }
}
