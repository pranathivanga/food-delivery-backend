package com.elevance_skills.swiggy_backend_clone.service;

import com.elevance_skills.swiggy_backend_clone.model.Restaurant;
import com.elevance_skills.swiggy_backend_clone.repository.RestaurantRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class RestaurantService {
    private final RestaurantRepository repository;
    private final MongoTemplate mongoTemplate;

    public RestaurantService(RestaurantRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Restaurant> search(String q, String cuisine, Double minRating,
                                   Double minPrice, Double maxPrice, Integer maxDeliveryTime,
                                   Boolean vegetarian, String sortBy) {
        Query query = new Query();
        query.addCriteria(Criteria.where("active").ne(false));
        if (cuisine != null && !cuisine.isBlank())
            query.addCriteria(Criteria.where("cuisine").regex(Pattern.quote(cuisine.trim()), "i"));
        if (minRating != null) query.addCriteria(Criteria.where("rating").gte(minRating));
        if (minPrice != null || maxPrice != null) {
            Criteria price = Criteria.where("priceForTwo");
            if (minPrice != null) price.gte(minPrice);
            if (maxPrice != null) price.lte(maxPrice);
            query.addCriteria(price);
        }
        if (maxDeliveryTime != null) query.addCriteria(Criteria.where("deliveryTime").lte(maxDeliveryTime));
        if (vegetarian != null) query.addCriteria(Criteria.where("vegetarian").is(vegetarian));

        if (sortBy != null && sortBy.equalsIgnoreCase("rating"))
            query.with(Sort.by(Sort.Direction.DESC, "rating"));
        else if (sortBy != null && sortBy.equalsIgnoreCase("price"))
            query.with(Sort.by(Sort.Direction.ASC, "priceForTwo"));
        else
            query.with(Sort.by(Sort.Direction.DESC, "popularity"));

        List<Restaurant> results = mongoTemplate.find(query, Restaurant.class);
        if (q == null || q.isBlank()) return results;

        String term = q.trim().toLowerCase(Locale.ROOT);
        List<Restaurant> matched = results.stream()
                .filter(r -> contains(r.getName(), term) || contains(r.getCuisine(), term)
                        || (r.getTags() != null && r.getTags().stream().anyMatch(t -> contains(t, term))))
                .toList();
        if (!matched.isEmpty()) return matched;

        // Fuzzy fallback: allow small spelling differences for longer search terms.
        return results.stream()
                .filter(r -> distance(term, safe(r.getName()).toLowerCase(Locale.ROOT)) <= Math.max(1, term.length() / 4)
                        || distance(term, safe(r.getCuisine()).toLowerCase(Locale.ROOT)) <= Math.max(1, term.length() / 4))
                .toList();
    }

    private boolean contains(String value, String term) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(term);
    }
    private String safe(String value) { return value == null ? "" : value; }

    private int distance(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return Integer.MAX_VALUE;
        int[] prev = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            int[] curr = new int[b.length() + 1];
            curr[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            prev = curr;
        }
        return prev[b.length()];
    }

    public Restaurant create(Restaurant restaurant) {
        restaurant.setId(null);
        return repository.save(restaurant);
    }

    public Restaurant update(String id, Restaurant changes) {
        Restaurant existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + id));
        if (changes.getName() != null) existing.setName(changes.getName());
        if (changes.getCuisine() != null) existing.setCuisine(changes.getCuisine());
        if (changes.getRating() > 0) existing.setRating(changes.getRating());
        if (changes.getPriceForTwo() > 0) existing.setPriceForTwo(changes.getPriceForTwo());
        if (changes.getDeliveryTime() > 0) existing.setDeliveryTime(changes.getDeliveryTime());
        existing.setVegetarian(changes.isVegetarian());
        if (changes.getPopularity() > 0) existing.setPopularity(changes.getPopularity());
        if (changes.getDescription() != null) existing.setDescription(changes.getDescription());
        if (changes.getTags() != null) existing.setTags(changes.getTags());
        existing.setActive(changes.isActive());
        return repository.save(existing);
    }
}
