<script setup>
import {ref, onMounted} from "vue";
import axios from "axios";
import Cookies from "js-cookie";
import MainNavbar from "../MainNavbar.vue";
import MainFooter from "../MainFooter.vue";
const orderId = ref(null);
const token = Cookies.get("token");
const message = ref("Đơn hàng đã bị hủy");
const tips = ref("Nếu đây là sự nhầm lẫn, vui lòng đặt hàng lại hoặc liên hệ hỗ trợ.");

onMounted(async () => {
  // Lấy orderId từ URL: /payment-cancel/59
  const pathParts = window.location.pathname.split("/");
  orderId.value = pathParts[pathParts.length - 1];

  if (orderId.value) {
    try {
      await axios.get(`http://localhost:8080/MiniatureCrafts/cancelOrder/${orderId.value}`,{
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log("Đã hủy đơn hàng:", orderId.value);
    } catch (error) {
      console.error("Lỗi khi hủy đơn hàng:", error);
    }
  }
});
</script>

<template>
  <MainNavbar />
  <div class="cancel-container">
    <div class="cancel-card">
      <img src="../../assets/img/logo/img.png" alt="cancel" class="icon" />
      <h1>{{ message }}</h1>
      <p>{{ tips }}</p>
      <a href="/cart" class="btn btn-cancel">Quay lại giỏ hàng</a>
      <a href="/" class="btn btn-outline-cancel">Tiếp tục mua sắm</a>
    </div>
  </div>
  <MainFooter />
</template>

<style scoped>
.cancel-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  background: #f8f9fa;
}

.cancel-card {
  background: #fff;
  padding: 40px;
  border-radius: 16px;
  text-align: center;
  max-width: 500px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
}

.icon {
  width: 100px;
  margin-bottom: 20px;
}

h1 {
  color: #dc3545;
  font-size: 28px;
  margin-bottom: 15px;
}

p {
  font-size: 16px;
  color: #555;
  margin-bottom: 25px;
}

.btn-cancel {
  background: #dc3545;
  color: #fff;
  border: none;
}

.btn-cancel:hover {
  background: #c82333;
}

.btn-outline-cancel {
  background: transparent;
  color: #dc3545;
  border: 2px solid #dc3545;
}

.btn-outline-cancel:hover {
  background: #dc3545;
  color: #fff;
}

.btn, .btn-outline-cancel {
  display: inline-block;
  margin: 5px;
  padding: 12px 24px;
  border-radius: 8px;
  text-decoration: none;
  transition: 0.3s;
  font-weight: 500;
}
</style>
