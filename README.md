# PetPal - Tìm kiếm & Báo cáo Thú cưng Thất lạc 🐾

**PetPal** là một ứng dụng di động dành cho hệ điều hành Android, được xây dựng với mục tiêu kết nối cộng đồng để giúp tìm kiếm và đoàn tụ những thú cưng bị thất lạc với gia đình của chúng. Người dùng có thể dễ dàng báo cáo một thú cưng bị mất, thông báo về một thú cưng được tìm thấy, và xem bản đồ trực quan về các trường hợp gần đó.

Dự án này là một phần của chương trình học tại **RMIT University Vietnam**.

---
## 📸 Giao diện & Chức năng chính

| Màn hình chính | Màn hình Home | Bản đồ | Báo cáo |
| :---: |:---:|:---:|:---:|
| <img src="[]" width="200"> | <img src="![HomeScreen](https://github.com/user-attachments/assets/a107762b-f33e-4595-9c60-964de037213e)
" width="200"> | <img src="[LINK_TỚI_ẢNH_BẢN_ĐỒ]" width="200"> | <img src="[LINK_TỚI_ẢNH_BÁO_CÁO]" width="200"> |

---
## ✨ Các tính năng nổi bật
![dd704352-a92d-4f03-bd75-88baee46eb14](https://github.com/user-attachments/assets/a8ecf353-d0d9-40f1-82cf-56556cde5279)

* **🏠 Bảng điều khiển trung tâm:** Màn hình chính cung cấp các lối tắt nhanh đến những chức năng cốt lõi:
    * **Home:** Xem danh sách thú cưng thất lạc và được tìm thấy.
    * **Map:** Khám phá bản đồ trực quan về vị trí các thú cưng.
    * **Report Lost/Found:** Nhanh chóng tạo báo cáo mới.

* **🐾 Màn hình Home thông minh:**
    * Hiển thị hai danh sách riêng biệt: **"Lost Pets"** và **"Found Pets"**.
    * Tích hợp thanh **tìm kiếm** mạnh mẽ theo tên, giống loài, hoặc màu sắc.
    * **Bộ lọc** cho phép người dùng xem tất cả, chỉ thú cưng bị mất, hoặc chỉ thú cưng được tìm thấy.
    * Hiển thị thông tin thú cưng dưới dạng thẻ (card) hiện đại, có thể cuộn ngang.

* **🗺️ Bản đồ tương tác:**
    * Hiển thị vị trí của tất cả các thú cưng đã được báo cáo trên Google Maps.
    * Sử dụng các **markers** với màu sắc khác nhau để phân biệt trạng thái (Mất, Tìm thấy).
    * Tự động xác định và hiển thị **vị trí hiện tại của người dùng** để dễ dàng định vị.
    * Yêu cầu quyền truy cập vị trí để tăng cường trải nghiệm.

* **❗ Hệ thống báo cáo chi tiết:**
    * Cung cấp hai form báo cáo riêng biệt cho trường hợp **"Mất thú cưng"** và **"Tìm thấy thú cưng"**.
    * Cho phép người dùng nhập đầy đủ thông tin nhận dạng: tên, giống loài, màu sắc, đặc điểm, phụ kiện...
    * Tích hợp tính năng **chọn ảnh** từ thư viện điện thoại.
    * Cho phép người dùng **chọn vị trí chính xác trên bản đồ** (Map Selector) nơi thú cưng được nhìn thấy lần cuối hoặc được tìm thấy.

---
## 🛠️ Công nghệ & Kiến trúc

Dự án được xây dựng hoàn toàn bằng **Kotlin** và tuân thủ các phương pháp phát triển Android hiện đại.

* **Ngôn ngữ:** **Kotlin**
* **Giao diện (UI):** **Jetpack Compose** - Xây dựng giao diện một cách khai báo, giúp code ngắn gọn và dễ quản lý.
* **Kiến trúc (Architecture):** **MVVM (Model-View-ViewModel)** - Tách biệt logic giao diện khỏi logic nghiệp vụ, giúp mã nguồn dễ bảo trì và kiểm thử.
* **Quản lý State:** **Kotlin Coroutines** & **StateFlow** - Xử lý các tác vụ bất đồng bộ và quản lý trạng thái giao diện một cách hiệu quả.
* **Dependency Injection:** **Hilt** - Giúp đơn giản hóa việc quản lý và cung cấp các dependency trong ứng dụng.
* **Điều hướng (Navigation):** **Navigation Component** - Quản lý luồng di chuyển giữa các màn hình.
* **Bản đồ & Vị trí:**
    * **Google Maps Compose Library:** Tích hợp Google Maps vào giao diện Compose.
    * **Google Play Services Location:** Lấy vị trí hiện tại của người dùng.
* **Tải ảnh:** **Coil** - Một thư viện tải ảnh hiện đại và hiệu quả cho Kotlin.
![HomeScreen](https://github.com/user-attachments/assets/a107762b-f33e-4595-9c60-964de037213e)

---
## 🚀 Cài đặt và Chạy thử

Bạn cần có **Android Studio Giraffe** (hoặc mới hơn) để build dự án này.

1.  **Clone a repo**
    ```bash
    git clone [https://github.com/TrongTruong145/A1PelPal.git](https://github.com/TrongTruong145/A1PelPal.git)
    ```
2.  Mở project bằng **Android Studio**.
3.  Đợi Gradle build và đồng bộ dự án.
4.  Nhấn **Run** ▶️.

---
## 👨‍💻 Tác giả

**Trọng Trương (Adrian Truong)**

* GitHub: [@TrongTruong145](https://github.com/TrongTruong145)
* LinkedIn: [Tên bạn](LINK_TỚI_LINKEDIN_CỦA_BẠN)

---
## 📄 Giấy phép

Dự án này được cấp phép theo Giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.
