package com.example.GenZFashion.service.order;

import com.example.GenZFashion.dto.CartItemDTO;
import com.example.GenZFashion.entity.CartItem;
import com.example.GenZFashion.entity.Variation;
import com.example.GenZFashion.repository.order.CartRepository;
import com.example.GenZFashion.service.product.VariationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private VariationService variationService;

    @Autowired
    private OrderService orderService;


    public List<CartItemDTO> findAll(Long customerId) {
        List<CartItem> cartItems = cartRepository.findByCustomerId(customerId);
        return cartItems.stream().map(this::mapEntityToDTO).toList();
    }

    public int countTotalItemsByCustomerId(Long customerId) {
        Integer totalItems = cartRepository.countTotalItemsByCustomerId(customerId);
        return totalItems != null ? totalItems : 0;
    }

    public void clearCartByCustomer(Long customerId) {
        cartRepository.deleteByCustomerId(customerId);
    }

    public ResponseEntity<String> addToCart(CartItemDTO cartItemDTO) {
        Long customerId = cartItemDTO.getCustomer_id().getID();
        Long variationId = cartItemDTO.getVariation_id().getID();
        int requestedQuantity = cartItemDTO.getQuantity();

        Variation variation = variationService.findByIDEntity(variationId);
        if (variation == null) {
            throw new RuntimeException("Không tìm thấy Variation với ID: " + variationId);
        }

        int availableQuantity = variation.getQuantity();

        if (availableQuantity == 0) {
            return ResponseEntity.badRequest().body("Sản phẩm đã hết hàng.");
        }

        CartItem cartItem = cartRepository.findByCustomerAndVariation(customerId, variationId);

        if (cartItem == null) {
            // Thêm mới sản phẩm vào giỏ hàng
            if (requestedQuantity > availableQuantity) {
                return ResponseEntity.badRequest().body("Số lượng yêu cầu vượt quá số lượng tồn kho.");
            }
            CartItem newCartItem = new CartItem();
            newCartItem.setCustomer_id(orderService.mapCustomerDTOToEntity(cartItemDTO.getCustomer_id()));
            newCartItem.setVariation_id(variation);
            newCartItem.setQuantity(requestedQuantity);
            newCartItem.setStatus(1);
            cartRepository.save(newCartItem);
        } else {
            // Cập nhật sản phẩm đã có trong giỏ hàng
            int newQuantity = cartItem.getQuantity() + requestedQuantity;
            if (newQuantity > availableQuantity) {
                return ResponseEntity.badRequest().body("Không đủ số lượng sản phẩm trong kho.");
            }
            cartItem.setQuantity(newQuantity);
            cartRepository.save(cartItem);
        }

        return ResponseEntity.ok("Thêm vào giỏ hàng thành công");
    }

    public ResponseEntity<String> updateQuantity(Long cartItemId, int newQuantity) {
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        Variation variation = cartItem.getVariation_id();
        int availableQuantity = variation.getQuantity();

        if (newQuantity > availableQuantity) {
            return ResponseEntity.badRequest().body("Not enough stock");
        }

        if (newQuantity <= 0) {
            cartRepository.delete(cartItem);
            return ResponseEntity.ok("Cart item removed successfully");
        }

        cartItem.setQuantity(newQuantity);
        cartRepository.save(cartItem);
        return ResponseEntity.ok("Quantity updated successfully");
    }

    public ResponseEntity<String> deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartRepository.delete(cartItem);
        return ResponseEntity.ok("Cart item removed successfully");
    }

    public CartItemDTO mapEntityToDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setVariation_id(variationService.mapVariationToDTO(cartItem.getVariation_id()));
        cartItemDTO.setCustomer_id(orderService.mapCustomerEntityToDTO(cartItem.getCustomer_id()));
        return cartItemDTO;
    }

    public CartItem mapDTOToEntity(CartItemDTO cartItemDTO) {
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemDTO.getId());
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setVariation_id(VariationService.mapListDTOtoVariationEntity(cartItemDTO.getVariation_id()));
        cartItem.setCustomer_id(orderService.mapCustomerDTOToEntity(cartItemDTO.getCustomer_id()));
        return cartItem;
    }
}
