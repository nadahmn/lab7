package com.example.starsgallery.service;

import com.example.starsgallery.beans.Star;
import com.example.starsgallery.dao.IDao;

import java.util.*;
import java.util.stream.Collectors;

public class StarService implements IDao<Star> {
    private List<Star> stars;
    private static StarService instance;
    private Random random;

    private StarService() {
        stars = new ArrayList<>();
        random = new Random();
        seed();
    }

    public static StarService getInstance() {
        if (instance == null) {
            synchronized (StarService.class) {
                if (instance == null) {
                    instance = new StarService();
                }
            }
        }
        return instance;
    }

    private void seed() {
        Calendar cal = Calendar.getInstance();
        
        cal.set(1990, Calendar.APRIL, 15);
        stars.add(new Star("Emma Watson", "https://i.imgur.com/EmWat.jpg", 4.5f, 
                "Actress & Activist", "British", cal.getTime(), 
                "British actress known for Hermione Granger role and women's rights advocacy"));
        
        cal.set(1962, Calendar.JULY, 3);
        stars.add(new Star("Tom Cruise", "https://i.imgur.com/TomCr.jpg", 4.2f, 
                "Actor & Producer", "American", cal.getTime(), 
                "American actor and producer known for Mission Impossible series and action films"));
        
        cal.set(1984, Calendar.NOVEMBER, 22);
        stars.add(new Star("Scarlett Johansson", "https://i.imgur.com/ScarJo.jpg", 4.7f, 
                "Actress & Singer", "American", cal.getTime(), 
                "American actress and singer known for Black Widow role and indie films"));
        
        cal.set(1974, Calendar.NOVEMBER, 11);
        stars.add(new Star("Leonardo DiCaprio", "https://i.imgur.com/LeoD.jpg", 4.8f, 
                "Actor & Environmentalist", "American", cal.getTime(), 
                "American actor and environmental activist known for Titanic and Inception"));
        
        cal.set(1983, Calendar.JANUARY, 9);
        stars.add(new Star("Kate Bosworth", "https://i.imgur.com/KateB.jpg", 3.8f, 
                "Actress", "American", cal.getTime(), 
                "American actress known for Blue Crush and Superman Returns"));
        
        cal.set(1961, Calendar.MAY, 6);
        stars.add(new Star("George Clooney", "https://i.imgur.com/GeorgeC.jpg", 4.6f, 
                "Actor & Director", "American", cal.getTime(), 
                "American actor, director, and humanitarian known for Ocean's Eleven series"));
        
        cal.set(1978, Calendar.JULY, 12);
        stars.add(new Star("Michelle Rodriguez", "https://i.imgur.com/MichR.jpg", 4.1f, 
                "Actress", "American", cal.getTime(), 
                "American actress known for Fast & Furious series and Avatar"));
        
        cal.set(1975, Calendar.OCTOBER, 5);
        stars.add(new Star("Kate Winslet", "https://i.imgur.com/KateW.jpg", 4.9f, 
                "Actress", "British", cal.getTime(), 
                "British actress known for Titanic and Revolutionary Road"));
        
        cal.set(1982, Calendar.JUNE, 21);
        stars.add(new Star("Chris Pratt", "https://i.imgur.com/ChrisP.jpg", 4.3f, 
                "Actor", "American", cal.getTime(), 
                "American actor known for Guardians of the Galaxy and Jurassic World"));
        
        cal.set(1988, Calendar.MARCH, 16);
        stars.add(new Star("Zendaya", "https://i.imgur.com/Zendaya.jpg", 4.4f, 
                "Actress & Singer", "American", cal.getTime(), 
                "American actress and singer known for Spider-Man and Euphoria"));
        
        // Add more celebrities
        cal.set(1976, Calendar.AUGUST, 20);
        stars.add(new Star("Ryan Gosling", "https://i.imgur.com/RyanG.jpg", 4.5f, 
                "Actor", "Canadian", cal.getTime(), 
                "Canadian actor known for The Notebook and Blade Runner"));
        
        cal.set(1984, Calendar.DECEMBER, 19);
        stars.add(new Star("Benedict Cumberbatch", "https://i.imgur.com/BenC.jpg", 4.7f, 
                "Actor", "British", cal.getTime(), 
                "British actor known for Sherlock and Doctor Strange"));
        
        cal.set(1988, Calendar.JULY, 24);
        stars.add(new Star("Demi Moore", "https://i.imgur.com/DemiM.jpg", 4.3f, 
                "Actress", "American", cal.getTime(), 
                "American actress known for Ghost and Charlie's Angels"));
        
        cal.set(1969, Calendar.MAY, 25);
        stars.add(new Star("Anne Hathaway", "https://i.imgur.com/AnneH.jpg", 4.6f, 
                "Actress", "American", cal.getTime(), 
                "American actress known for Les Misérables and The Dark Knight"));
        
        cal.set(1979, Calendar.OCTOBER, 17);
        stars.add(new Star("Chris Hemsworth", "https://i.imgur.com/ChrisH.jpg", 4.4f, 
                "Actor", "Australian", cal.getTime(), 
                "Australian actor known for Thor and Avengers"));
        
        cal.set(1981, Calendar.JUNE, 9);
        stars.add(new Star("Natalie Portman", "https://i.imgur.com/NatP.jpg", 4.8f, 
                "Actress", "American", cal.getTime(), 
                "American actress known for Black Swan and Thor"));
        
        cal.set(1978, Calendar.NOVEMBER, 5);
        stars.add(new Star("Adam Driver", "https://i.imgur.com/AdamD.jpg", 4.2f, 
                "Actor", "American", cal.getTime(), 
                "American actor known for Star Wars and Marriage Story"));
        
        cal.set(1985, Calendar.DECEMBER, 8);
        stars.add(new Star("Aaron Paul", "https://i.imgur.com/AaronP.jpg", 4.4f, 
                "Actor", "American", cal.getTime(), 
                "American actor known for Breaking Bad and Need for Speed"));

        for (Star star : stars) {
            star.setAwardsCount(random.nextInt(15) + 1);
            if (random.nextBoolean()) {
                star.toggleFavorite();
            }
        }
    }

    @Override
    public boolean create(Star o) {
        if (o == null) return false;
        if (findById(o.getId()) != null) return false;
        return stars.add(o);
    }

    @Override
    public boolean update(Star o) {
        if (o == null) return false;
        for (int i = 0; i < stars.size(); i++) {
            if (stars.get(i).getId() == o.getId()) {
                stars.set(i, o);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Star o) {
        if (o == null) return false;
        return stars.removeIf(star -> star.getId() == o.getId());
    }

    @Override
    public Star findById(int id) {
        return stars.stream()
                .filter(star -> star.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Star> findAll() {
        return new ArrayList<>(stars);
    }

    @Override
    public List<Star> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        String lowerQuery = query.toLowerCase().trim();
        return stars.stream()
                .filter(star -> 
                    star.getName().toLowerCase().contains(lowerQuery) ||
                    star.getProfession().toLowerCase().contains(lowerQuery) ||
                    star.getNationality().toLowerCase().contains(lowerQuery) ||
                    star.getBiography().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(int id) {
        return findById(id) != null;
    }

    @Override
    public int count() {
        return stars.size();
    }

    @Override
    public void clear() {
        stars.clear();
    }

    public List<Star> getFavorites() {
        return stars.stream()
                .filter(Star::isFavorite)
                .collect(Collectors.toList());
    }

    public List<Star> getByRating(float minRating) {
        return stars.stream()
                .filter(star -> star.getRating() >= minRating)
                .sorted((s1, s2) -> Float.compare(s2.getRating(), s1.getRating()))
                .collect(Collectors.toList());
    }

    public List<Star> getByProfession(String profession) {
        return stars.stream()
                .filter(star -> star.getProfession().toLowerCase().contains(profession.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Star getTopRated() {
        return stars.stream()
                .max(Comparator.comparing(Star::getRating))
                .orElse(null);
    }

    public float getAverageRating() {
        return (float) stars.stream()
                .mapToDouble(Star::getRating)
                .average()
                .orElse(0.0);
    }

    public Map<String, Long> getProfessionStats() {
        return stars.stream()
                .collect(Collectors.groupingBy(
                    star -> star.getProfession().split(" & ")[0],
                    Collectors.counting()
                ));
    }

    public List<Star> getYoungestStars(int count) {
        return stars.stream()
                .sorted(Comparator.comparing(Star::getBirthDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Star> getMostAwarded(int count) {
        return stars.stream()
                .sorted((s1, s2) -> Integer.compare(s2.getAwardsCount(), s1.getAwardsCount()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void shuffleRatings() {
        stars.forEach(star -> {
            float newRating = 1.0f + random.nextFloat() * 4.0f;
            star.setRating(Math.round(newRating * 10) / 10.0f);
        });
    }

    public Star getRandomStar() {
        if (stars.isEmpty()) return null;
        return stars.get(random.nextInt(stars.size()));
    }

    public void addRandomStar() {
        String[] names = {"Chris Evans", "Jennifer Lawrence", "Robert Downey Jr.", "Meryl Streep", "Brad Pitt"};
        String[] professions = {"Actor", "Actress", "Director", "Producer"};
        String[] nationalities = {"American", "British", "Canadian", "Australian"};
        
        String name = names[random.nextInt(names.length)];
        String profession = professions[random.nextInt(professions.length)];
        String nationality = nationalities[random.nextInt(nationalities.length)];
        
        Calendar cal = Calendar.getInstance();
        cal.set(1960 + random.nextInt(40), random.nextInt(12), random.nextInt(28) + 1);
        
        Star newStar = new Star(name, "https://i.imgur.com/default" + random.nextInt(100) + ".jpg", 
                1.0f + random.nextFloat() * 4.0f, profession, nationality, cal.getTime(), 
                "Talented " + profession.toLowerCase() + " from " + nationality);
        newStar.setAwardsCount(random.nextInt(20));
        
        create(newStar);
    }
}
