TASK 4 - SMART DELIVERY PARTNER ASSIGNMENT

Copy the src folder into the existing project root and merge folders. This patch adds new classes only.

IMPORTANT INTEGRATION REQUIRED:
1. Add these fields to model/Order.java, with getters and setters:
   private String assignedPartnerId;
   private String assignedPartnerName;
   private String deliveryAssignmentStatus = "UNASSIGNED";
2. To auto-assign immediately after order creation, after saving the order in OrderService, inject DeliveryAssignmentService and call:
   Order saved = orderRepository.save(order);
   return deliveryAssignmentService.assign(saved.getId(), restaurantLatitude, restaurantLongitude);
   Use the actual restaurant coordinates from your Restaurant model. Do not use arbitrary coordinates.
3. Current assignment endpoints accept restaurant coordinates as request parameters because the uploaded project baseline did not contain the Task 2 Restaurant model. Once integrated with your current Restaurant model, resolve coordinates from restaurantId instead.
4. /api/admin/** should remain ADMIN-only. /api/delivery/** is authenticated by your existing security config. For a production system, partner identity must come from a dedicated authenticated DELIVERY_PARTNER role, not an email supplied in request body.

POST /api/admin/delivery-partners
{
 "name":"Ravi Kumar", "email":"ravi.partner@example.com", "phone":"9999999999",
 "latitude":17.3850, "longitude":78.4867, "maxWorkload":3
}

GET /api/admin/delivery-partners
PUT /api/delivery/set-status
{ "partnerEmail":"ravi.partner@example.com", "available":true }

POST /api/delivery/orders/{orderId}/assign?restaurantLatitude=17.3850&restaurantLongitude=78.4867
POST /api/delivery/orders/{orderId}/decline
{ "partnerEmail":"ravi.partner@example.com", "restaurantLatitude":17.3850, "restaurantLongitude":78.4867 }

Selection prioritizes lower workload, then shorter Haversine distance. A partner is eligible only when active, available, and below maxWorkload.
