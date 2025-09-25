<template>
  <div id="app" class="app sidebar-mini rtl">
    <header class="app-header">
      <a
        class="app-sidebar__toggle"
        href="#"
        data-toggle="sidebar"
        aria-label="Hide Sidebar"
      ></a>
      <ul class="app-nav">
        <li>
          <a class="app-nav__item" href="/index.html">
            <i class="bx bx-log-out bx-rotate-180"></i>
          </a>
        </li>
      </ul>
    </header> 

    <main class="app-content">
      <div class="app-title">
        <ul class="app-breadcrumb breadcrumb side">
          <li class="breadcrumb-item active">
            <a href="#"><b>Tạo mới khách hàng</b></a>
          </li>
        </ul>
      </div>
      <div class="row">
        <div class="col-md-12">
          <div class="tile">
            <h3 class="tile-title">Thông tin khách hàng</h3>
            <div class="tile-body">
              <form @submit.prevent="submitForm">
                <div class="row">
                  <div class="form-group col-md-4">
                    <label for="customerId">ID khách hàng:</label>
                    <input
                      v-model="customer.id"
                      class="form-control"
                      type="text"
                      id="customerId"
                      disabled
                    />
                  </div>
                  <div class="form-group col-md-4">
                    <label for="customerName">Họ và tên:</label>
                    <input
                      v-model="customer.name"
                      class="form-control"
                      type="text"
                      id="customerName"
                      required
                    />
                  </div>
                  <div class="form-group col-md-4">
                    <label for="customerAddress">Địa chỉ:</label>
                    <input
                      v-model="customer.address"
                      class="form-control"
                      type="text"
                      id="customerAddress"
                      required
                    />
                  </div>
                  <div class="form-group col-md-4">
                    <label for="customerPhone">Số điện thoại:</label>
                    <input
                      v-model="customer.phone"
                      class="form-control"
                      type="text"
                      id="customerPhone"
                      required
                    />
                  </div>
                  <div class="form-group col-md-4">
                    <label for="customerNote">Ghi chú:</label>
                    <input
                      v-model="customer.note"
                      class="form-control"
                      type="text"
                      id="customerNote"
                      required
                    />
                  </div>
                  <div class="form-group col-md-4">
                    <label for="customerStatus">Trạng thái:</label>
                    <select
                      v-model="customer.status"
                      class="form-control"
                      id="customerStatus"
                      required
                    >
                      <option value="true">Hoạt động</option>
                      <option value="false">Không hoạt động</option>
                    </select>
                  </div>
                  <div class="form-group col-md-4">
                    <label for="creationDate">Ngày tạo:</label>
                    <input
                      v-model="customer.creation_date"
                      class="form-control"
                      type="date"
                      id="creationDate"
                      disabled
                    />
                  </div>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary" type="submit">Lưu lại</button>
                  <a class="btn btn-secondary" href="/doc/table-data-table.html">Hủy bỏ</a>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import axios from "axios";
import Cookies from "js-cookie";

export default {
  data() {
    return {
      customer: {
        id: null,
        name: "",
        address: "",
        phone: "",
        note: "",
        status: true,
        creation_date: new Date().toISOString().split("T")[0],
      },
    };
  },
  methods: {
    async submitForm() {
      try {
        console.log("Bắt đầu gửi form...");
        console.log("Dữ liệu khách hàng:", this.customer);

        const token = Cookies.get("token");

        if (!token) {
          console.error("Token không tồn tại hoặc người dùng chưa đăng nhập.");
          alert("Chưa đăng nhập hoặc token không tồn tại!");
          return;
        }
        console.log("Token lấy từ cookies:", token);
        const api = "http://localhost:8080/admin/customer/save";
        const payload = {
          name: this.customer.name,
          address: this.customer.address,
          phone: this.customer.phone,
          note: this.customer.note,
          status: this.customer.status,
          edit_Date: null,
        };
        console.log("Payload gửi tới API:", payload);

        const response = await axios.post(api, payload, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          withCredentials: true,
        });

        console.log("Phản hồi từ API:", response.data);
        alert("Tạo khách hàng thành công!");
        this.$router.push("/customer");
      } catch (error) {
        console.error("Lỗi khi gửi form:", error);

        if (error.response) {
          console.error("Chi tiết lỗi từ server:", error.response.data);
          const errorMessage = error.response.data.message || `Lỗi ${error.response.status}: Không thể tạo khách hàng.`;
          alert(errorMessage);
        } else if (error.request) {
          console.error("Không nhận được phản hồi từ server:", error.request);
          alert("Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại đường truyền mạng.");
        } else {
          console.error("Lỗi xảy ra trong quá trình tạo yêu cầu:", error.message);
        }
      }
    },
  },
};
</script>
