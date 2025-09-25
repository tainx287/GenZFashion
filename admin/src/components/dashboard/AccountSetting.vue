<template>
    <main class="account-settings">
        <div class="container">
            <div class="account-container">
                <!-- Sidebar Tabs -->
                <div class="sidebar col-lg-3 col-md-4">
                    <h5 class="mb-4">Cài đặt tài khoản</h5>
                    <nav class="nav flex-column">
                        <a class="nav-link" href="/">
                            <i class="fa fa-tachometer me-2"></i>Dashboard
                        </a>
                        <a class="nav-link" :class="{ active: activeTab === 'profile' }" @click="activeTab = 'profile'">
                            <i class="fa fa-user me-2"></i>Thay đổi thông tin
                        </a>
                        <a class="nav-link" :class="{ active: activeTab === 'orders' }" @click="activeTab = 'orders'">
                            <i class="fa fa-truck me-2"></i>Theo dõi đơn hàng
                        </a>
                        <a class="nav-link" @click="openLogoutModal">
                            <i class="fa fa-sign-out me-2"></i>Đăng xuất
                        </a>
                    </nav>
                </div>

                <!-- Content Area -->
                <div class="content col-lg-9 col-md-8">
                    <!-- Profile Tab Content -->
                    <div id="profile-content" class="tab-content" v-if="activeTab === 'profile'">
                        <h4 class="mb-4">Thay đổi thông tin cá nhân</h4>
                        <div class="profile-info">
                            <img src="https://placehold.co/100x100/FFC7ED/fff?text=Avatar" alt="Ảnh đại diện">
                            <div>
                                <h5>Tên tài khoản</h5>
                                <p class="text-muted">ID: 12345678</p>
                                <button type="button">Thay đổi ảnh đại diện</button>
                            </div>
                        </div>
                        <form>
                            <div class="row">
                                <div class="col-md-6 form-group">
                                    <label for="display-name">Tên hiển thị</label>
                                    <input type="text" class="form-control" id="display-name" value="Fashionista">
                                </div>
                                <div class="col-md-6 form-group">
                                    <label for="username">Tên tài khoản</label>
                                    <input type="text" class="form-control" id="username" value="fashionista_user">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="email">Địa chỉ Email</label>
                                <input type="email" class="form-control" id="email" value="user@example.com">
                            </div>
                            <div class="form-group">
                                <label for="phone">Số điện thoại</label>
                                <input type="tel" class="form-control" id="phone" value="0987654321">
                            </div>
                            <div class="form-group">
                                <label for="address">Địa chỉ</label>
                                <input type="text" class="form-control" id="address"
                                    placeholder="Số nhà, đường, quận, thành phố">
                            </div>
                            <div class="form-group">
                                <label for="social-media">Mạng xã hội</label>
                                <input type="text" class="form-control" id="social-media" placeholder="Link Facebook, Instagram, ...">
                            </div>
                            <div class="form-group">
                                <label for="bank-account">Thông tin ngân hàng</label>
                                <input type="text" class="form-control" id="bank-account" placeholder="Tên ngân hàng, số tài khoản">
                            </div>
                            <button type="submit" class="btn btn-primary-custom">Lưu thay đổi</button>
                        </form>
                    </div>

                    <!-- Order Tracking Tab Content -->
                    <div id="orders-content" class="tab-content" v-if="activeTab === 'orders'">
                        <h4 class="mb-4">Theo dõi đơn hàng #ORD123456</h4>
                        <div class="order-tracking">
                            <div class="timeline">
                                <div class="step completed">
                                    <div class="icon"><i class="fa fa-check"></i></div>
                                    <span>Đã nhận đơn</span>
                                </div>
                                <div class="step completed">
                                    <div class="icon"><i class="fa fa-box-open"></i></div>
                                    <span>Đã xác nhận</span>
                                </div>
                                <div class="step active">
                                    <div class="icon"><i class="fa fa-truck"></i></div>
                                    <span>Đang vận chuyển</span>
                                </div>
                                <div class="step">
                                    <div class="icon"><i class="fa fa-home"></i></div>
                                    <span>Đã giao hàng</span>
                                </div>
                            </div>
                            <div class="order-map text-center my-5">
                                
                            </div>
                        </div>
                    </div>

                    <!-- Logout Confirmation Modal -->
                    <div class="modal-container" :class="{ show: isLogoutModalOpen }">
                        <div class="modal-content">
                            <h5>Xác nhận đăng xuất</h5>
                            <p>Bạn có chắc chắn muốn đăng xuất khỏi tài khoản này?</p>
                            <div class="d-flex justify-content-center gap-3">
                                <button @click="closeLogoutModal" class="btn btn-secondary">Hủy</button>
                                <button @click="confirmLogout" class="btn btn-primary-custom">Đăng xuất</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>

<script>
export default {
  name: 'AccountSetting',
  data() {
    return {
      activeTab: 'profile',
      isLogoutModalOpen: false,
    };
  },
  methods: {
    openLogoutModal() {
      this.isLogoutModalOpen = true;
    },
    closeLogoutModal() {
      this.isLogoutModalOpen = false;
    },
    confirmLogout() {
      console.log('User has been logged out.');
      this.closeLogoutModal();
      // Thêm logic đăng xuất ở đây, ví dụ: chuyển hướng về trang đăng nhập
    },
  },
};
</script>

<style scoped>
    .account-settings {
        padding: 80px 0;
        background-color: #f7f7f7;
    }

    .account-container {
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
        display: flex;
        flex-direction: column;
        overflow: hidden;
    }

    @media (min-width: 992px) {
        .account-container {
            flex-direction: row;
        }
    }

    .sidebar {
        background-color: #f5f5f5;
        padding: 20px;
        border-right: 1px solid #eee;
    }

    .sidebar .nav-link {
        color: #555;
        padding: 15px 20px;
        border-radius: 8px;
        transition: all 0.3s;
        display: block;
        text-align: left;
        cursor: pointer;
    }

    .sidebar .nav-link:hover,
    .sidebar .nav-link.active {
        background-color: #FFC7ED;
        color: #fff;
    }

    .content {
        padding: 30px;
        flex-grow: 1;
    }

    .profile-info {
        display: flex;
        align-items: center;
        margin-bottom: 30px;
    }

    .profile-info img {
        width: 100px;
        height: 100px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid #FFC7ED;
        margin-right: 20px;
    }

    .profile-info button {
        background-color: transparent;
        border: 1px solid #FFC7ED;
        color: #FFC7ED;
        padding: 8px 15px;
        border-radius: 20px;
        transition: all 0.3s;
    }

    .profile-info button:hover {
        background-color: #FFC7ED;
        color: #fff;
    }

    .form-group {
        margin-bottom: 20px;
    }

    .form-control {
        border: 1px solid #ddd;
        border-radius: 5px;
        padding: 10px;
    }

    .btn-primary-custom {
        background-color: #FFC7ED;
        border-color: #FFC7ED;
        color: #fff;
        padding: 10px 20px;
        border-radius: 20px;
        transition: all 0.3s;
    }

    .btn-primary-custom:hover {
        background-color: #f5a8da;
        border-color: #f5a8da;
    }

    .order-tracking {
        position: relative;
        padding: 50px 0;
    }

    .timeline {
        display: flex;
        justify-content: space-between;
        align-items: center;
        position: relative;
        margin-bottom: 50px;
    }

    .timeline::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 0;
        right: 0;
        height: 4px;
        background: #eee;
        transform: translateY(-50%);
    }

    .timeline .step {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        position: relative;
        z-index: 1;
    }

    .timeline .step .icon {
        width: 40px;
        height: 40px;
        background-color: #fff;
        border: 4px solid #eee;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 10px;
    }

    .timeline .step.active .icon {
        border-color: #FFC7ED;
        background-color: #FFC7ED;
        color: #fff;
    }

    .timeline .step.completed .icon {
        background-color: #FFC7ED;
        color: #fff;
        border-color: #FFC7ED;
    }

    .timeline .step.completed::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 0;
        right: 50%;
        height: 4px;
        background: #FFC7ED;
        transform: translateY(-50%);
        z-index: -1;
    }

    .timeline .step:not(:first-child).completed::after {
        left: -50%;
        right: 50%;
    }

    .modal-container {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 1000;
        visibility: hidden;
        opacity: 0;
        transition: visibility 0s, opacity 0.3s;
    }

    .modal-container.show {
        visibility: visible;
        opacity: 1;
    }

    .modal-content {
        background: #fff;
        padding: 30px;
        border-radius: 10px;
        text-align: center;
        width: 90%;
        max-width: 400px;
        transform: translateY(-20px);
        transition: transform 0.3s;
    }

    .modal-container.show .modal-content {
        transform: translateY(0);
    }
</style>