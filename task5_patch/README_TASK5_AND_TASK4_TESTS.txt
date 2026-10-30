TASK 5 PATCH + COMBINED TASK 4/5 TEST GUIDE

IMPORTANT INSTALLATION
1. Extract this ZIP into your existing intern_project folder (the folder containing pom.xml).
2. Choose Replace for the included Order.java and DeliveryAssignmentService.java.
   Order.java preserves the Task 1 fraud fields and includes Task 4 delivery fields plus Task 5 status history.
   DeliveryAssignmentService.java fixes reassignment so a declining partner is not immediately selected again.
3. Add this dependency inside <dependencies> in pom.xml (do not replace the whole pom.xml):

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

4. Reload Maven, stop the running app, and restart it. Keep your existing port (examples below use 8083).
5. This patch does NOT change OrderService.java, SecurityConfig.java, application.properties, .env, or existing Task 1-3 services.

TASK 4 + TASK 5 TEST SETUP
Base URL: http://localhost:8083
Use your existing ADMIN JWT for /api/admin/** requests.
Use your existing USER JWT for customer/delivery requests.
Postman: Authorization tab > Bearer Token > paste token. Do not include the word Bearer in the token field.

A. TASK 4 - CREATE TWO DELIVERY PARTNERS (ADMIN TOKEN)
POST {{baseUrl}}/api/admin/delivery-partners
Body (partner 1, closest):
{
  "name": "Ravi Kumar",
  "email": "ravi.partner@example.com",
  "phone": "9999999991",
  "latitude": 17.3850,
  "longitude": 78.4867,
  "maxWorkload": 3
}
Expected: 201 Created. Copy the returned id if needed.

POST {{baseUrl}}/api/admin/delivery-partners
Body (partner 2, alternate):
{
  "name": "Arjun Rao",
  "email": "arjun.partner@example.com",
  "phone": "9999999992",
  "latitude": 17.3900,
  "longitude": 78.4900,
  "maxWorkload": 3
}
Expected: 201 Created.
If you already created these emails, do not create them again; use GET to inspect existing records.

B. TASK 4 - LIST PARTNERS (ADMIN TOKEN)
GET {{baseUrl}}/api/admin/delivery-partners
Expected: 200 OK, both partners visible, available=true, currentWorkload=0.

C. TASK 4 - SET AVAILABILITY (USER TOKEN)
PUT {{baseUrl}}/api/delivery/set-status
Body:
{
  "partnerEmail": "ravi.partner@example.com",
  "available": true
}
Repeat for arjun.partner@example.com. Expected: 200 OK and available=true.

D. CREATE A FRESH ORDER FOR THE COMBINED TEST (USER TOKEN)
POST {{baseUrl}}/api/orders/create
Body:
{
  "restaurantId": "restaurant-101",
  "items": [
    {
      "itemId": "item-1",
      "itemName": "Veg Burger",
      "quantity": 1,
      "unitPrice": 120.00
    }
  ]
}
Expected: 201 Created. Copy the returned order id. If it is PENDING_FRAUD_REVIEW, approve it first using your ADMIN fraud-approval endpoint; status updates are intentionally blocked while fraud review is pending.

E. TASK 4 - ASSIGN PARTNER (USER TOKEN)
POST {{baseUrl}}/api/delivery/orders/ORDER_ID/assign?restaurantLatitude=17.3850&restaurantLongitude=78.4867
Replace ORDER_ID with the id from step D.
Expected: 200 OK, assignedPartnerId/name populated, deliveryAssignmentStatus="ASSIGNED".
Ravi should be selected first if both partners have equal workload because Ravi is closer to the supplied restaurant coordinates.

F. TASK 4 - DECLINE AND REASSIGN (USER TOKEN)
POST {{baseUrl}}/api/delivery/orders/ORDER_ID/decline
Body:
{
  "partnerEmail": "ravi.partner@example.com",
  "restaurantLatitude": 17.3850,
  "restaurantLongitude": 78.4867
}
Expected: 200 OK, assigned partner should now be Arjun (assuming Arjun is active, available and below max workload). The updated service excludes the declining partner from immediate reassignment.

G. TASK 5 - CONNECT TO WEBSOCKET
In Postman create New > WebSocket request.
URL: ws://localhost:8083/ws
Connect. After connected, send this STOMP CONNECT frame (a null terminator is required):
CONNECT
accept-version:1.2
heart-beat:10000,10000

^@

Then subscribe to the order-specific topic, replacing ORDER_ID:
SUBSCRIBE
id:order-sub-1
destination:/topic/orders/ORDER_ID
ack:auto

^@

In Postman, ^@ means the single NUL byte (0x00), not the two printable characters caret and at-sign. If Postman cannot send a NUL byte, use a STOMP-capable WebSocket client. Keep this connection open while running steps H-K.

H. TASK 5 - UPDATE ORDER TO ACCEPTED (ADMIN TOKEN)
PUT {{baseUrl}}/api/orders/update-status/ORDER_ID
Body:
{
  "status": "ACCEPTED",
  "note": "Restaurant accepted the order"
}
Expected: 200 OK. WebSocket subscriber receives a JSON notification with status ACCEPTED.

I. UPDATE TO PREPARING (ADMIN TOKEN)
PUT {{baseUrl}}/api/orders/update-status/ORDER_ID
Body:
{
  "status": "PREPARING",
  "note": "Kitchen started preparing the order"
}
Expected: 200 OK and WebSocket notification.

J. UPDATE TO OUT_FOR_DELIVERY (ADMIN TOKEN)
PUT {{baseUrl}}/api/orders/update-status/ORDER_ID
Body:
{
  "status": "OUT_FOR_DELIVERY",
  "note": "Order handed to delivery partner"
}
Expected: 200 OK and WebSocket notification.

K. UPDATE TO DELIVERED (ADMIN TOKEN)
PUT {{baseUrl}}/api/orders/update-status/ORDER_ID
Body:
{
  "status": "DELIVERED",
  "note": "Order delivered successfully"
}
Expected: 200 OK and WebSocket notification.

L. VERIFY CUSTOMER ORDER (USER TOKEN)
GET {{baseUrl}}/api/orders/ORDER_ID
Expected: 200 OK with status="DELIVERED", assigned partner details, and statusHistory containing ACCEPTED, PREPARING, OUT_FOR_DELIVERY, DELIVERED.

M. NEGATIVE TESTS
1. Try USER token on PUT /api/orders/update-status/ORDER_ID. Expected: 403 Forbidden.
2. Try invalid transition e.g. DELIVERED -> PREPARING. Expected: 400 from existing IllegalStateException handler (or the app's mapped error response).
3. Try an unknown order id. Expected: 400/404 depending on your existing exception handler.

STATUS FLOW SUPPORTED
PLACED -> ACCEPTED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED
PLACED -> CANCELLED and ACCEPTED -> CANCELLED are listed as allowed by the tracking service, but the existing cancellation endpoint should be used for customer cancellation. Fraud-pending orders must be approved before status updates.

WEBSOCKET NOTE
This implementation broadcasts an order-specific topic at /topic/orders/{orderId} and also publishes to /user/queue/orders. The order-specific topic is for local demonstration/testing and should be protected with authenticated subscriptions before production deployment. REST status updates are ADMIN-only; customer REST order reads remain governed by your existing ownership checks.
