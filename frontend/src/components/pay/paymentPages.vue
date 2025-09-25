<script>
import {ref, onMounted, watch} from 'vue';
import Cookies from 'js-cookie';
import axios from 'axios';
// import { createVNPayPayment } from "@/components/pay/VNPayService";

export default {
  setup() {
    const showQRModal = ref(false);
    const totalPrice = ref(0);
    const cart = ref([]);
    const productDetails = ref([]);

    // Lấy thông tin khách hàng từ Cookies
    const user = ref(null);
    const provinces = ref([
      "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ",
      "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Ninh",
      "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Cà Mau",
      "Đắk Lắk", "Đắk Nông", "Điện Biên", "Hà Giang", "Hà Nam",
      "Hà Tĩnh", "Hải Dương", "Hòa Bình", "Hưng Yên", "Khánh Hòa",
      "Kiên Giang", "Kon Tum", "Lai Châu", "Lạng Sơn", "Lào Cai",
      "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận",
      "Phú Thọ", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh",
      "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên",
      "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "Vĩnh Long",
      "Vĩnh Phúc", "Yên Bái"
    ]);

    const selectedProvince = ref("");
    const detailedAddress = ref("");

    try {
      user.value = JSON.parse(Cookies.get("customers"));
    } catch (error) {
      console.error("Lỗi khi lấy thông tin người dùng:", error);
      user.value = null;
    }

    // Kiểm tra nếu không có user
    if (!user.value || !user.value.userInfo) {
      alert("Bạn cần đăng nhập để tiếp tục!");
      window.location.href = "/login";
      return;
    }

    const getDefaultImage = (images) => {
      if (!images || images.length === 0) return "default-image.jpg"; // Ảnh mặc định nếu không có ảnh nào

      const defaultImage = images;
      return defaultImage ? `http://localhost:8080/upload/images/${defaultImage.cd_Images}` : "default-image.jpg";
    };


    const order = ref({
      customerID: {id: user.value.userInfo.id},
      code_Voucher: '',
      address: '',
      note: '',
      paymentMethod: {
        id: 2
      }, // Đặt mặc định là "Thanh toán khi nhận hàng"
      status: 1,
      type_Oder: true,
      orderLine: []
    });

    const apiUrl = "http://localhost:8080/MiniatureCrafts/";


    // 🛒 Lấy giỏ hàng từ API
    const getCart = async () => {
      try {

        // const response = JSON.parse(Cookies.get("cart"));
        cart.value = JSON.parse(sessionStorage.getItem("cart"));
        console.log("Cart: " + cart.value);
        calculateTotal();
      } catch (error) {
        console.error("Lỗi khi lấy giỏ hàng:", error);
      }
    };

    // 🔢 Tính tổng tiền giỏ hàng
    const calculateTotal = () => {
      console.log("Tien cart: " + cart.value.quantity);
      totalPrice.value = cart.value.reduce((total, item) => {
        return total + item.quantity * item.variation_id.price; // Không format tiền ở đây
      }, 0);
    };


    const formatCurrency = (value) => {
      return value.toLocaleString("vi-VN");
    };

    // ✅ Cập nhật `productDetails` khi `cart` thay đổi
    watch(cart, () => {
      productDetails.value = cart.value.map(item => ({
        name: item.nameVariation,
        quantity: item.quantity,
        totalPrice: item.variation_id.price * item.quantity,
        imgurl: item.imageUrl
      }));
    });

    // ✅ Xác nhận mã QR và xóa giỏ hàng
    const confirmQRCode = () => {
      showQRModal.value = false;
      localStorage.removeItem("cart");
    };


    // ✅ Gửi đơn hàng
    const submitOrder = async () => {
      try {
        if (!order.value.paymentMethod.id) {
          alert("Vui lòng chọn phương thức thanh toán!");
          return;
        }
        if (!selectedProvince.value || !detailedAddress.value) {
          alert("Vui lòng nhập đầy đủ thông tin địa chỉ!");
          return;
        }

        // Gộp tỉnh/thành phố + địa chỉ cụ thể
        order.value.address = `${detailedAddress.value}, ${selectedProvince.value}`;

        const data = {
          customerID: {id: order.value.customerID.id},
          address: order.value.address,
          code_Voucher: '',
          note: order.value.note || "",
          paymentMethod: {
            id: order.value.paymentMethod.id
          },
          type_Oder: "1",
          orderLine: cart.value.map(item => ({
            variationID: {id: item.variation_id.id},
            quantity: item.quantity
          }))
        };

        const token = Cookies.get("authToken");

        if (order.value.paymentMethod.id === 1) {

          const response = await axios.post(`${apiUrl}newOrder`, data, {
            headers: {Authorization: `Bearer ${token}`}
          });

          const orderID = response.data.id; // Lấy ID đơn hàng từ API response
          // 🏦 Xử lý thanh toán PAYOS
          const amount = totalPrice.value; // Tổng tiền
          const dataForPayment = {
            "amount": amount,
            "roles": "customer",
            "description": "Thanh toán đơn hàng: " + orderID,
            "orderId": orderID
          };
          console.log("Data for payment:", dataForPayment);
          const payosResponse = await axios.post(`http://localhost:8080/api/v1/transactions/create-payment-link`, dataForPayment ,
              {
                headers: {
                  Authorization: `Bearer ${token}`
                }
              });
          console.log("PayOS Response:", payosResponse);


          if (response.status === 201) {
            const email = user.value.email;
            console.log(user.value)
            alert("Đơn hàng đã được tạo thành công!");
            alert("Sau khi bạn thanh toán, đơn hàng sẽ được xác nhận và gửi đến đơn vị vận chuyển trong vòng 48h!");


            // Chuyển hướng ngay lập tức
            window.location.href = payosResponse.data.checkoutUrl;


            const orderId = response.data.id; // Lấy ID đơn hàng từ API response
            axios.get(`${apiUrl}send-email/${email}/${orderId}`, {
              headers: {Authorization: `Bearer ${token}`}
            });
            sessionStorage.removeItem("cart")
            // Xóa giỏ hàng (chạy nền)
            axios.get(`http://localhost:8080/api/v1/cart/removeall/${order.value.customerID.id}`)
                .then(() => {
                  cart.value = [];
                  productDetails.value = [];
                  totalPrice.value = 0;
                })
                .catch(error => console.error("Lỗi khi xóa giỏ hàng:", error));
          } else {
            alert("Không thể tạo đơn hàng. Vui lòng thử lại!");
          }


        } else {
          // 🚚 Thanh toán khi nhận hàng (COD)
          const response = await axios.post(`${apiUrl}newOrder`, data, {
            headers: {Authorization: `Bearer ${token}`}
          });
          console.log("Response:", response);

          if (response.status === 201) {
            const email = user.value.email;
            console.log(user.value)
            alert("Đơn hàng đã được tạo thành công!");


            // Chuyển hướng ngay lập tức
            window.location.href = "/history";


            const orderId = response.data.id; // Lấy ID đơn hàng từ API response
            axios.get(`${apiUrl}send-email/${email}/${orderId}`, {
              headers: {Authorization: `Bearer ${token}`}
            });
            sessionStorage.removeItem("cart")
            // Xóa giỏ hàng (chạy nền)
            axios.get(`http://localhost:8080/api/v1/cart/removeall/${order.value.customerID.id}`)
                .then(() => {
                  cart.value = [];
                  productDetails.value = [];
                  totalPrice.value = 0;
                })
                .catch(error => console.error("Lỗi khi xóa giỏ hàng:", error));
          } else {
            alert("Không thể tạo đơn hàng. Vui lòng thử lại!");
          }
        }
      } catch (error) {
        if (error.response && error.response.data) {
          // Lấy thông báo lỗi từ server
          const serverMessage = error.response.data || "";

          if (serverMessage.includes("hết hàng")) {
            alert("❌ Sản phẩm này đã có người nhanh tay đặt trước hoặc đã bán hết!");
          } else {
            alert(serverMessage || "Có lỗi xảy ra, vui lòng thử lại!");
          }
        } else {
          console.error("Lỗi khi lưu đơn hàng:", error);
          alert("Có lỗi xảy ra, vui lòng thử lại!");
        }
      }

    };


    onMounted(() => {
      getCart();
    });

    return {
      provinces,
      selectedProvince,
      detailedAddress,
      getDefaultImage,
      calculateTotal,
      cart,
      totalPriceForCustomer: totalPrice,
      productDetails,
      formatCurrency,
      confirmQRCode,
      order,
      user,
      submitOrder,
      closeQRModal: () => (showQRModal.value = false),
      logout: () => {
        Cookies.remove("authToken");
        window.location.href = "/";
      }
    };
  }
};
</script>

<template>
  <div class="content">
    <div class="wrap">
      <div class="container">
        <form @submit.prevent="submitOrder">
          <div class="row">
            <div class="col-lg-6 col-12">
              <div class="main">
                <div class="main-header">
                  <a href="">
                    <h1>MiniatureCrafts</h1>
                  </a>
                </div>
                <div class="main-content">
                  <div class="main-title">
                    <h2>Thông tin giao hàng</h2>
                  </div>
                  <div class="main-customer-info">
                    <div class="main-customer-info-img">
                      <img src="../../assets/img/logo/avtusers.png" alt="" width="60px" height="60px">
                    </div>
                    <div class="main-customer-info-logged" v-if="user?.userInfo?.name">
                      <p class="main-customer-info-logged-paragraph">{{ user.userInfo.name }}</p>
                      <a href="#" @click.prevent="logout">Đăng xuất</a>
                    </div>
                  </div>
                  <div class="fieldset">
                    <div class="form-group">
                      <label for="province" class="form-label">Tỉnh/thành phố</label>
                      <select id="province" class="form-control" v-model="selectedProvince">
                        <option value="" disabled>-- Chọn tỉnh/thành phố --</option>
                        <option v-for="province in provinces" :key="province" :value="province">
                          {{ province }}
                        </option>
                      </select>
                    </div>
                    <div class="form-group">
                      <label for="note" class="form-label">Địa chỉ</label>
                      <textarea
                          id="address"
                          type="text"
                          class="form-control"
                          v-model="detailedAddress"
                          placeholder="Nhập địa chỉ cụ thể (Số nhà, đường...)"
                      />
                    </div>

                    <div class="form-group">
                      <label for="note" class="form-label">Ghi chú</label>
                      <textarea id="note" class="form-control" v-model="order.note"></textarea>
                    </div>
                    <div class="form-group">
                      <label for="paymentMethod" class="form-label">Phương thức thanh toán</label>
                      <select id="paymentMethod" class="form-control" v-model.number="order.paymentMethod.id">
                        <option value="1">Thanh toán qua ngân hàng</option>
                        <option value="2">Thanh toán khi nhận hàng</option>
                      </select>
                    </div>
                  </div>
                </div>
                <div class="main-footer">
                  <div class="continue">
                    <a href="/cart">
                      <i class="fi-rs-angle-left"></i>
                      Giỏ hàng
                    </a>
                  </div>
                  <div class="pay">
                    <button type="submit" class="btn-pay form-submit">Đặt hàng</button>
                  </div>
                </div>
              </div>
            </div>
            <!--            form hien thi san pham -->
            <div class="col-lg-6 col-12 hidden-sm hidden-xs" style="background-color:#ffffff; border: 1px solid #ddd; border-radius: 5px; padding: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
              <div class="sliderbar-header">
                <h2>Tổng tiền hiện tại: {{ formatCurrency(totalPriceForCustomer) }} VND</h2>
              </div>
              <div class="sliderbar-content">
                <div class="row row-sliderbar" v-for="(item, index) in cart" :key="index">
                  <div class="col-4">
                    <img :src="getDefaultImage(item.variation_id.images)" alt="Product Image"
                         style="width: 80%;">
                    <span class="notice">{{ item.quantity }}</span>
                  </div>
                  <div class="col-5">
                    <h3>{{ item.variation_id.name }}</h3>
                  </div>
                  <div class="col-110 hidden-xs text-right">
                    <h3>
                      {{
                        formatCurrency(item.variation_id.price * item.quantity)
                      }} VND
                    </h3>
                  </div>
                </div>
              </div>
              <div class="slider-footer">
                <div class="total">
                  <div class="row row-sliderbar-footer">
                    <div class="col-6"><h2>Thành tiền:</h2></div>
                    <div class="col-4 text-right "><h2>{{ formatCurrency(totalPriceForCustomer) }} VND</h2></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>


<style>

.container {
  width: 100%;
}
</style>
