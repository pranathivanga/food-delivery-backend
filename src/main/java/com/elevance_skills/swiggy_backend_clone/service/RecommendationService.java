package com.elevance_skills.swiggy_backend_clone.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {
    private final MongoTemplate mongoTemplate;

    public RecommendationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Document> recommend(String userEmail) {
        List<Document> orders = mongoTemplate.find(
                Query.query(Criteria.where("userEmail").is(userEmail)),
                Document.class, "orders");
        List<Document> restaurants = mongoTemplate.findAll(Document.class, "restaurants");

        Map<String, Integer> cuisineCounts = new HashMap<>();
        Map<String, Integer> itemCounts = new HashMap<>();
        Set<String> orderedRestaurantIds = new HashSet<>();

        for (Document order : orders) {
            String restaurantId = stringValue(order.get("restaurantId"));
            if (restaurantId != null) orderedRestaurantIds.add(restaurantId);
            for (Object rawItem : listValue(order.get("items"))) {
                if (!(rawItem instanceof Document item)) continue;
                String itemName = stringValue(item.get("itemName"));
                if (itemName != null) itemCounts.merge(itemName.toLowerCase(Locale.ROOT), 1, Integer::sum);
            }
        }

        Map<String, Document> restaurantById = new HashMap<>();
        for (Document restaurant : restaurants) {
            String id = stringValue(restaurant.get("_id"));
            if (id != null) restaurantById.put(id, restaurant);
        }
        for (Document order : orders) {
            String restaurantId = stringValue(order.get("restaurantId"));
            Document restaurant = restaurantById.get(restaurantId);
            if (restaurant != null) {
                String cuisine = stringValue(restaurant.get("cuisine"));
                if (cuisine != null) cuisineCounts.merge(cuisine.toLowerCase(Locale.ROOT), 1, Integer::sum);
            }
        }

        List<Document> results = new ArrayList<>();
        for (Document restaurant : restaurants) {
            if (Boolean.FALSE.equals(restaurant.getBoolean("active"))) continue;
            String id = stringValue(restaurant.get("_id"));
            if (id != null && orderedRestaurantIds.contains(id)) continue;

            String cuisine = stringValue(restaurant.get("cuisine"));
            int cuisineScore = cuisine == null ? 0 : cuisineCounts.getOrDefault(cuisine.toLowerCase(Locale.ROOT), 0) * 20;
            double rating = numberValue(restaurant.get("rating"));
            double popularity = numberValue(restaurant.get("popularity"));
            int itemScore = 0;
            Object tagsRaw = restaurant.get("tags");
            if (tagsRaw instanceof Collection<?> tags) {
                for (Object tag : tags) {
                    if (tag != null) itemScore += itemCounts.getOrDefault(tag.toString().toLowerCase(Locale.ROOT), 0) * 5;
                }
            }
            double score = cuisineScore + (rating * 10.0) + Math.min(popularity, 100.0) * 0.1 + itemScore;
            Document recommendation = new Document(restaurant);
            recommendation.put("recommendationScore", score);
            recommendation.put("recommendationReason", cuisineScore > 0
                    ? "Matches cuisines from your order history"
                    : "Recommended based on rating and popularity");
            results.add(recommendation);
        }
        results.sort((a, b) -> Double.compare(numberValue(b.get("recommendationScore")), numberValue(a.get("recommendationScore"))));
        return results;
    }

    private static String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private static List<?> listValue(Object value) {
        return value instanceof List<?> list ? list : Collections.emptyList();
    }

    private static double numberValue(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0.0;
    }
}
