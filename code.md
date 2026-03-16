# Add to Cart - Implementation Changes

## Overview
Implemented a **dual-mode cart system** that works for both **guest users** (using cookies) and **logged-in users** (cookies + database sync).

### Architecture
- **Cookies** (`pet_cart`) are the primary source for UI (badge count, instant feedback on all pages)
- **Database** is the authoritative store for logged-in users, synced via REST API
- **Guest users** rely entirely on cookies for cart persistence

---

## Files Changed

### 1. `SecurityConfig.java`
- Added `/cart` and `/api/cart/**` to `permitAll()` so guest users can access the cart page and API endpoints

### 2. `CartItemRepository.java`
- Added `findByCartCustomerUsernameAndProductId(String username, Integer productId)` — finds a cart item by the owner's username and product ID (used for product-based update/remove)
- Added `findByCartId(Integer cartId)` — finds all items in a cart (replaces inefficient `findAll()` + filter)

### 3. `CartService.java` (Interface)
Added 3 new methods:
- `updateCartItemByProduct(Integer productId, Integer quantity, String username)` — update cart item using productId
- `removeCartItemByProduct(Integer productId, String username)` — remove cart item using productId
- `getCartDetailsFromCookie(String cookieJson)` — build CartDetailDto from cookie JSON for guest users

### 4. `CartServiceImpl.java`
- Implemented 3 new methods above
- `getCartDetailsFromCookie`: Parses cookie JSON using Jackson `ObjectMapper`, fetches products from DB by ID, builds `CartDetailDto`
- Extracted `buildEmptyCartDetail()` and `buildCartDetail(List<CartItemDto>)` helper methods
- Replaced `findAll().stream().filter(...)` with `findByCartId(cartId)` for better performance

### 5. `CartApiController.java`
- **Guest handling**: All endpoints now return `200 OK` with `status: "success"` for guest users (instead of 401). The actual cart data is managed by cookies on the client side.
- Added helper method `isGuest(Authentication)` to reduce code duplication
- **New endpoints**:
  - `PUT /api/cart/update-product?productId=X&quantity=Y` — updates cart item by product ID
  - `DELETE /api/cart/remove-product/{productId}` — removes cart item by product ID
- These product-based endpoints are used by the cart detail page (where cartItemId may not be available for guests)

### 6. `CartController.java`
- **Dual-mode rendering**:
  - If logged in → `cartService.getCartDetails(username)` (reads from database)
  - If guest → reads `pet_cart` cookie, URL-decodes it, passes to `cartService.getCartDetailsFromCookie(cookieJson)`
- Removed redirect to `/login` for unauthenticated users — guests can now view their cart

### 7. `cart.js` (Complete Rewrite)
**Cookie management:**
- `getCartFromCookie()` — parses the `pet_cart` cookie (URL-decoded JSON)
- `saveCartToCookie(cart)` — saves cart array to cookie (30-day expiry, SameSite=Lax)

**Badge:**
- `updateCartBadge()` — reads cookie, sums quantities, updates `#cart-badge` text, toggles `hidden` class

**Cart operations:**
- `addToCart(productId, quantity)` — updates cookie → updates badge → shows toast → fires API call (best-effort sync)
- `updateCartItem(productId, newQuantity)` — updates cookie → calls API → reloads page
- `removeCartItem(productId)` — confirms → updates cookie → calls API → reloads page

**Init:**
- `DOMContentLoaded` event calls `updateCartBadge()` on every page load

### 8. `cartdetail.html` (Revised)
- Uses shared `head` fragment instead of inline `<head>` (consistent with other pages)
- Uses `header` and `footer` fragments via `th:replace` (consistent with other pages)
- All JS operations use `productId` instead of `cartItemId` (works for both guests and logged-in)
- Currency changed from `$` to VNĐ format using `#numbers.formatDecimal()`
- Image URLs use `th:src="@{...}"` with http/relative check (consistent with other pages)
- Cart summary section hidden when cart is empty
- Improved empty cart state with link to continue shopping

### 9. `header.html`
- Cart badge starts with `hidden` class and text `0` (instead of hardcoded `3`)
- `cart.js` dynamically shows/hides the badge based on cookie data on every page load

---

## Cookie Format

- **Name:** `pet_cart`
- **Value:** URL-encoded JSON array
- **Example:** `[{"productId":1,"quantity":2},{"productId":5,"quantity":1}]`
- **Max-age:** 30 days
- **Path:** `/`
- **SameSite:** Lax

---

## Workflow

### Add to Cart (any page)
1. User clicks "Add to Cart" button
2. `cart.js` → `addToCart(productId, quantity)`
3. Cookie updated immediately
4. Badge updated immediately
5. Toast shown ("Đã thêm vào giỏ hàng")
6. API call fired (syncs to DB for logged-in users, returns success for guests)

### View Cart
1. User clicks cart icon in header → navigates to `/cart`
2. **If logged in:** Server reads cart from database via `CartService.getCartDetails()`
3. **If guest:** Server reads `pet_cart` cookie, parses JSON, fetches product info, builds `CartDetailDto`
4. Template renders cart items with product details, totals, and action buttons

### Update Quantity (cart detail page)
1. User clicks +/- button
2. `cart.js` → `updateCartItem(productId, newQuantity)`
3. Cookie updated immediately
4. API call to `PUT /api/cart/update-product` (syncs DB for logged-in users)
5. Page reloads with updated data

### Remove Item (cart detail page)
1. User clicks delete button
2. Confirmation dialog
3. `cart.js` → `removeCartItem(productId)`
4. Cookie updated immediately
5. API call to `DELETE /api/cart/remove-product/{productId}` (syncs DB for logged-in users)
6. Page reloads with updated data

---

## Pages with Working Add to Cart Buttons
- ✅ Homepage (`homepage.html`) — Best Sellers section
- ✅ Product List (`listproduct.html`) — Product grid
- ✅ Product Detail (`productdetail.html`) — Main add to cart + related products
- ✅ Cart Detail (`cartdetail.html`) — Update quantity & remove
